package io.github.gtbauke.modernmachines.worldgen.placement;

import org.jspecify.annotations.NonNull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.gtbauke.modernmachines.core.registry.ModPlacementModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class AdjacentBlockPlacementModifier extends PlacementFilter {
    public static final MapCodec<AdjacentBlockPlacementModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("block").forGetter(AdjacentBlockPlacementModifier::targetBlock)
    ).apply(builder, AdjacentBlockPlacementModifier::new));

    private final Identifier targetBlock;

    public AdjacentBlockPlacementModifier(Identifier targetBlock) {
        this.targetBlock = targetBlock;
    }

    public Identifier targetBlock() {
        return targetBlock;
    }

    @Override
    protected boolean shouldPlace(@NonNull PlacementContext context, @NonNull RandomSource random, @NonNull BlockPos pos) {
        for (var dir : Direction.values()) {
            var adjacentPos = pos.relative(dir);
            var state = context.getBlockState(adjacentPos);
            var blockLoc = BuiltInRegistries.BLOCK.getKey(state.getBlock());

            if (blockLoc.equals(targetBlock)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NonNull PlacementModifierType<?> type() {
        return ModPlacementModifiers.ADJACENT_BLOCK.get();
    }
}
