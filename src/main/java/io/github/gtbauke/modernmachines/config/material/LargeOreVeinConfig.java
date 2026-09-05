package io.github.gtbauke.modernmachines.config.material;

import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record LargeOreVeinConfig(
        @SerializedName(value = "enabled", alternate = {})
        boolean enabled,
        @SerializedName(value = "dimensions", alternate = {"dimension"})
        @NonNull List<String> dimensions,
        @SerializedName(value = "dimension_blacklist", alternate = {"dimensionBlacklist"})
        @NonNull List<String> dimensionBlacklist,
        @SerializedName(value = "biomes", alternate = {"biome"})
        @NonNull List<String> biomes,
        @SerializedName(value = "biome_tags", alternate = {"biomeTags", "biome_tag", "biomeTag"})
        @NonNull List<String> biomeTags,
        @SerializedName(value = "biome_blacklist", alternate = {"biomeBlacklist"})
        @NonNull List<String> biomeBlacklist,
        @SerializedName(value = "min_y", alternate = {"minY"})
        int minY,
        @SerializedName(value = "max_y", alternate = {"maxY"})
        int maxY,
        @SerializedName(value = "rarity", alternate = {})
        int rarity,
        @SerializedName(value = "filler_block", alternate = {"fillerBlock", "filler"})
        @NonNull String fillerBlock,
        @SerializedName(value = "primary_ore_chance", alternate = {"primaryOreChance", "ore_chance"})
        float primaryOreChance,
        @SerializedName(value = "raw_block_chance", alternate = {"rawBlockChance"})
        float rawBlockChance,
        @SerializedName(value = "secondary_material", alternate = {"secondaryMaterial", "secondary"})
        @Nullable String secondaryMaterial,
        @SerializedName(value = "secondary_ore_chance", alternate = {"secondaryOreChance"})
        float secondaryOreChance,
        @SerializedName(value = "surface_indicators", alternate = {"surfaceIndicators", "indicator"})
        @Nullable SurfaceIndicatorConfig surfaceIndicators,
        @SerializedName(value = "noise", alternate = {})
        @Nullable VeinNoiseConfig noise
) {
    public LargeOreVeinConfig {
        dimensions = List.copyOf(dimensions);
        dimensionBlacklist = List.copyOf(dimensionBlacklist);
        biomes = List.copyOf(biomes);
        biomeTags = List.copyOf(biomeTags);
        biomeBlacklist = List.copyOf(biomeBlacklist);

        if (fillerBlock.isBlank()) {
            fillerBlock = "minecraft:granite";
        }

        rarity = Math.clamp(rarity <= 0 ? 16 : rarity, 1, 128);
        primaryOreChance = Math.clamp(primaryOreChance <= 0.0f ? 0.35f : primaryOreChance, 0.0f, 1.0f);
        rawBlockChance = Math.clamp(rawBlockChance, 0.0f, 1.0f);
        secondaryOreChance = Math.clamp(secondaryOreChance, 0.0f, 1.0f);

        if (surfaceIndicators == null) {
            surfaceIndicators = SurfaceIndicatorConfig.disabled();
        }

        if (noise == null) {
            noise = VeinNoiseConfig.defaultNoise();
        }
    }
}
