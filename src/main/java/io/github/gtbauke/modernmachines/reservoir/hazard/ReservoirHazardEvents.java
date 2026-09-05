package io.github.gtbauke.modernmachines.reservoir.hazard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.config.reservoir.HazardConfig;
import io.github.gtbauke.modernmachines.config.reservoir.ReservoirLoader;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.world.reservoir.ReservoirSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = ModernMachines.MOD_ID)
public class ReservoirHazardEvents {
    private static final Map<ChunkPos, Long> LAST_HAZARD_TICKS = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TICKS = 40L;

    @SubscribeEvent
    public static void onBlockBreak(@NonNull BlockDropsEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        ServerLevel serverLevel = event.getLevel();

        var pos = event.getPos();
        var brokenState = event.getState();

        // If broken block is a drill casing or wellhead, extraction is properly contained
        if (brokenState.is(ModBlocks.DRILL_CASING.get()) || brokenState.is(ModBlocks.WELLHEAD.get())) {
            return;
        }

        var reservoir = ReservoirSavedData.get().getReservoirAt(pos);
        if (reservoir == null) {
            // Check adjacent block downwards or around
            reservoir = ReservoirSavedData.get().getReservoirAt(pos.below());
            if (reservoir == null) {
                return;
            }
        }

        // Enforce chunk cooldown to prevent recursive explosion / blowout spam
        var chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        long currentTick = serverLevel.getGameTime();
        var lastTick = LAST_HAZARD_TICKS.get(chunkPos);
        if (lastTick != null && (currentTick - lastTick) < COOLDOWN_TICKS) {
            return;
        }

        LAST_HAZARD_TICKS.put(chunkPos, currentTick);

        var config = ReservoirLoader.getReservoir(reservoir.getReservoirType());
        var hazards = config != null ? config.hazards() : HazardConfig.defaults();

        // 1. Blowout Geyser Eruption
        if (hazards.eruption() && reservoir.getCurrentPressure() > 0.5f) {
            triggerBlowoutGeyser(serverLevel, pos, hazards);
            reservoir.drain(2500L);
        }

        // 2. Flammable Flashover
        if (hazards.flammable() && isNearIgnitionSource(serverLevel, pos, hazards.ignitionRadius())) {
            serverLevel.explode(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    hazards.explosionStrength(),
                    Level.ExplosionInteraction.BLOCK
            );
        }

        // 3. Toxic Vapor Cloud
        if (hazards.toxic()) {
            spawnToxicCloud(serverLevel, pos, hazards);
        }
    }

    private static void triggerBlowoutGeyser(ServerLevel level, BlockPos pos, HazardConfig hazards) {
        level.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                40,
                0.3,
                1.5,
                0.3,
                hazards.eruptionVelocity() * 0.05
        );

        level.playSound(
                null,
                pos,
                SoundEvents.LAVA_EXTINGUISH,
                SoundSource.BLOCKS,
                1.0f,
                0.7f
        );
    }

    private static boolean isNearIgnitionSource(ServerLevel level, BlockPos pos, int radius) {
        int rad = Math.clamp(radius, 1, 10);
        var checkPos = new BlockPos.MutableBlockPos();

        for (int x = -rad; x <= rad; x++) {
            for (int y = -rad; y <= rad; y++) {
                for (int z = -rad; z <= rad; z++) {
                    checkPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    var state = level.getBlockState(checkPos);

                    if (state.is(Blocks.TORCH) ||
                            state.is(Blocks.WALL_TORCH) ||
                            state.is(Blocks.SOUL_TORCH) ||
                            state.is(Blocks.SOUL_WALL_TORCH) ||
                            state.is(Blocks.LAVA) ||
                            state.is(Blocks.FIRE) ||
                            state.is(Blocks.SOUL_FIRE) ||
                            state.is(Blocks.CAMPFIRE) ||
                            state.is(Blocks.SOUL_CAMPFIRE)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void spawnToxicCloud(ServerLevel level, BlockPos pos, HazardConfig hazards) {
        var cloud = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        cloud.setRadius(3.5f);
        cloud.setRadiusOnUse(-0.5f);
        cloud.setWaitTime(10);
        cloud.setDuration(hazards.toxicDuration() * 20);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());

        for (var effectStr : hazards.toxicEffects()) {
            var loc = Identifier.tryParse(effectStr);
            if (loc != null) {
                var effectOpt = BuiltInRegistries.MOB_EFFECT.getOptional(loc);
                effectOpt.ifPresent(effect -> cloud.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), 120, 1)));
            }
        }

        level.addFreshEntity(cloud);
    }
}
