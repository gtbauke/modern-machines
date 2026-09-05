package io.github.gtbauke.modernmachines.worldgen.placement;

import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.gtbauke.modernmachines.core.registry.ModPlacementModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class DimensionFilterPlacementModifier extends PlacementFilter {
    public static final MapCodec<DimensionFilterPlacementModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.listOf().optionalFieldOf("allowed", Collections.emptyList()).forGetter(DimensionFilterPlacementModifier::allowed),
            Identifier.CODEC.listOf().optionalFieldOf("denied", Collections.emptyList()).forGetter(DimensionFilterPlacementModifier::denied)
    ).apply(builder, DimensionFilterPlacementModifier::new));

    private final List<Identifier> allowed;
    private final List<Identifier> denied;

    public DimensionFilterPlacementModifier(List<Identifier> allowed, List<Identifier> denied) {
        this.allowed = allowed == null ? Collections.emptyList() : List.copyOf(allowed);
        this.denied = denied == null ? Collections.emptyList() : List.copyOf(denied);
    }

    public static DimensionFilterPlacementModifier ofAllowed(List<Identifier> allowed) {
        return new DimensionFilterPlacementModifier(allowed, Collections.emptyList());
    }

    public static DimensionFilterPlacementModifier of(List<Identifier> allowed, List<Identifier> denied) {
        return new DimensionFilterPlacementModifier(allowed, denied);
    }

    public List<Identifier> allowed() {
        return allowed;
    }

    public List<Identifier> denied() {
        return denied;
    }

    @Override
    protected boolean shouldPlace(@NonNull PlacementContext context, @NonNull RandomSource random, @NonNull BlockPos pos) {
        var dimLoc = context.getLevel().getLevel().dimension().identifier();

        if (!denied.isEmpty() && denied.contains(dimLoc)) {
            return false;
        }

        if (!allowed.isEmpty() && !allowed.contains(dimLoc)) {
            return false;
        }

        return true;
    }

    @Override
    public @NonNull PlacementModifierType<?> type() {
        return ModPlacementModifiers.DIMENSION_FILTER.get();
    }
}
