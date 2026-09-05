package io.github.gtbauke.modernmachines.worldgen.feature;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SubsurfaceReservoirConfiguration(
        String reservoirType,
        String fluidId,
        BlockState fluidBlock,
        BlockState barrierBlock,
        Optional<BlockState> capstoneBlock,
        int minY,
        int maxY,
        int radiusXz,
        int radiusY,
        float initialPressure,
        long totalVolume
) implements FeatureConfiguration {
    public static final MapCodec<SubsurfaceReservoirConfiguration> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.STRING.fieldOf("reservoir_type").forGetter(SubsurfaceReservoirConfiguration::reservoirType),
            Codec.STRING.fieldOf("fluid_id").forGetter(SubsurfaceReservoirConfiguration::fluidId),
            BlockState.CODEC.fieldOf("fluid_block").forGetter(SubsurfaceReservoirConfiguration::fluidBlock),
            BlockState.CODEC.fieldOf("barrier_block").forGetter(SubsurfaceReservoirConfiguration::barrierBlock),
            BlockState.CODEC.optionalFieldOf("capstone_block").forGetter(SubsurfaceReservoirConfiguration::capstoneBlock),
            Codec.INT.fieldOf("min_y").forGetter(SubsurfaceReservoirConfiguration::minY),
            Codec.INT.fieldOf("max_y").forGetter(SubsurfaceReservoirConfiguration::maxY),
            Codec.INT.optionalFieldOf("radius_xz", 10).forGetter(SubsurfaceReservoirConfiguration::radiusXz),
            Codec.INT.optionalFieldOf("radius_y", 5).forGetter(SubsurfaceReservoirConfiguration::radiusY),
            Codec.FLOAT.optionalFieldOf("initial_pressure", 5.0f).forGetter(SubsurfaceReservoirConfiguration::initialPressure),
            Codec.LONG.optionalFieldOf("total_volume", 250_000L).forGetter(SubsurfaceReservoirConfiguration::totalVolume)
    ).apply(builder, SubsurfaceReservoirConfiguration::new));
}
