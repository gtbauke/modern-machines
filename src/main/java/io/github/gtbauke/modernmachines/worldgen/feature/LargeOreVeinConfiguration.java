package io.github.gtbauke.modernmachines.worldgen.feature;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record LargeOreVeinConfiguration(
        int minY,
        int maxY,
        BlockState fillerBlock,
        BlockState primaryOre,
        Optional<BlockState> primaryDeepslateOre,
        float primaryOreChance,
        Optional<BlockState> rawBlock,
        float rawBlockChance,
        Optional<BlockState> secondaryOre,
        Optional<BlockState> secondaryDeepslateOre,
        float secondaryOreChance,
        Optional<BlockState> surfaceIndicatorBlock,
        float surfaceIndicatorChance,
        int noiseLength,
        int noiseThickness,
        float noiseDensity
) implements FeatureConfiguration {
    public static final MapCodec<LargeOreVeinConfiguration> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.INT.fieldOf("min_y").forGetter(LargeOreVeinConfiguration::minY),
            Codec.INT.fieldOf("max_y").forGetter(LargeOreVeinConfiguration::maxY),
            BlockState.CODEC.fieldOf("filler_block").forGetter(LargeOreVeinConfiguration::fillerBlock),
            BlockState.CODEC.fieldOf("primary_ore").forGetter(LargeOreVeinConfiguration::primaryOre),
            BlockState.CODEC.optionalFieldOf("primary_deepslate_ore").forGetter(LargeOreVeinConfiguration::primaryDeepslateOre),
            Codec.FLOAT.fieldOf("primary_ore_chance").forGetter(LargeOreVeinConfiguration::primaryOreChance),
            BlockState.CODEC.optionalFieldOf("raw_block").forGetter(LargeOreVeinConfiguration::rawBlock),
            Codec.FLOAT.fieldOf("raw_block_chance").forGetter(LargeOreVeinConfiguration::rawBlockChance),
            BlockState.CODEC.optionalFieldOf("secondary_ore").forGetter(LargeOreVeinConfiguration::secondaryOre),
            BlockState.CODEC.optionalFieldOf("secondary_deepslate_ore").forGetter(LargeOreVeinConfiguration::secondaryDeepslateOre),
            Codec.FLOAT.fieldOf("secondary_ore_chance").forGetter(LargeOreVeinConfiguration::secondaryOreChance),
            BlockState.CODEC.optionalFieldOf("surface_indicator_block").forGetter(LargeOreVeinConfiguration::surfaceIndicatorBlock),
            Codec.FLOAT.fieldOf("surface_indicator_chance").forGetter(LargeOreVeinConfiguration::surfaceIndicatorChance),
            Codec.INT.optionalFieldOf("noise_length", 48).forGetter(LargeOreVeinConfiguration::noiseLength),
            Codec.INT.optionalFieldOf("noise_thickness", 6).forGetter(LargeOreVeinConfiguration::noiseThickness),
            Codec.FLOAT.optionalFieldOf("noise_density", 0.65f).forGetter(LargeOreVeinConfiguration::noiseDensity)
    ).apply(builder, LargeOreVeinConfiguration::new));
}
