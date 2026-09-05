package io.github.gtbauke.modernmachines.worldgen.feature;

import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;

import io.github.gtbauke.modernmachines.world.reservoir.ReservoirInstance;
import io.github.gtbauke.modernmachines.world.reservoir.ReservoirSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SubsurfaceReservoirFeature extends Feature<SubsurfaceReservoirConfiguration> {
    public SubsurfaceReservoirFeature(Codec<SubsurfaceReservoirConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NonNull FeaturePlaceContext<SubsurfaceReservoirConfiguration> context) {
        var level = context.level();
        var origin = context.origin();
        var config = context.config();

        int cx = origin.getX();
        int cy = origin.getY();
        int cz = origin.getZ();

        int minY = Math.max(config.minY(), level.getMinY());
        int maxY = Math.min(config.maxY(), level.getMaxY());
        if (minY >= maxY) {
            return false;
        }

        cy = Math.clamp(cy, minY + config.radiusY() + 2, maxY - config.radiusY() - 2);

        int rx = config.radiusXz();
        int ry = config.radiusY();
        int rz = config.radiusXz();

        // Register logical reservoir instance if not yet registered for this origin
        var existing = ReservoirSavedData.get().getReservoirAt(cx, cy, cz);
        if (existing == null) {
            var instance = ReservoirInstance.createNew(
                    config.reservoirType(),
                    config.fluidId(),
                    cx - rx - 1,
                    cy - ry - 1,
                    cz - rz - 1,
                    cx + rx + 1,
                    cy + ry + 1,
                    cz + rz + 1,
                    config.totalVolume(),
                    config.initialPressure()
            );
            ReservoirSavedData.get().addReservoir(instance);
        }

        int chunkStartX = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(origin.getX()));
        int chunkStartZ = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(origin.getZ()));

        int minX = Math.max(cx - rx - 2, chunkStartX);
        int maxX = Math.min(cx + rx + 2, chunkStartX + 15);
        int minZ = Math.max(cz - rz - 2, chunkStartZ);
        int maxZ = Math.min(cz + rz + 2, chunkStartZ + 15);

        var mutablePos = new BlockPos.MutableBlockPos();
        boolean placedAny = false;

        for (int x = minX; x <= maxX; x++) {
            double dx = (double) (x - cx) / (double) rx;

            for (int z = minZ; z <= maxZ; z++) {
                double dz = (double) (z - cz) / (double) rz;

                for (int y = cy - ry - 2; y <= cy + ry + 2; y++) {
                    if (y < minY || y > maxY) {
                        continue;
                    }

                    double dy = (double) (y - cy) / (double) ry;
                    double distSq = (dx * dx) + (dy * dy) + (dz * dz);

                    if (distSq > 1.15) {
                        continue;
                    }

                    mutablePos.set(x, y, z);
                    var currentState = level.getBlockState(mutablePos);
                    if (!isReplaceable(currentState)) {
                        continue;
                    }

                    if (distSq <= 0.75) {
                        // Fluid core
                        level.setBlock(mutablePos, config.fluidBlock(), Block.UPDATE_CLIENTS);
                        placedAny = true;
                    } else if (distSq <= 1.05) {
                        // Barrier shell
                        if (y >= cy + (int) (ry * 0.4) && config.capstoneBlock().isPresent()) {
                            level.setBlock(mutablePos, config.capstoneBlock().get(), Block.UPDATE_CLIENTS);
                        } else {
                            level.setBlock(mutablePos, config.barrierBlock(), Block.UPDATE_CLIENTS);
                        }

                        placedAny = true;
                    }
                }
            }
        }

        return placedAny;
    }

    private static boolean isReplaceable(BlockState state) {
        if (state.isAir() || state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER)) {
            return false;
        }

        return state.is(LargeOreVeinFeature.LARGE_VEIN_REPLACEABLE) ||
                state.is(Blocks.STONE) ||
                state.is(Blocks.DEEPSLATE) ||
                state.is(Blocks.TUFF) ||
                state.is(Blocks.ANDESITE) ||
                state.is(Blocks.DIORITE) ||
                state.is(Blocks.GRANITE) ||
                state.is(Blocks.NETHERRACK) ||
                state.is(Blocks.END_STONE);
    }
}
