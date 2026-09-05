package io.github.gtbauke.modernmachines.config.material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record OreGenRule(
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
        @SerializedName(value = "targets", alternate = {"target"})
        @NonNull List<OreTargetConfig> targets,
        @SerializedName(value = "vein_size", alternate = {"veinSize"})
        int veinSize,
        @SerializedName(value = "veins_per_chunk", alternate = {"veinsPerChunk", "count"})
        int veinsPerChunk,
        @SerializedName(value = "rarity", alternate = {})
        int rarity,
        @SerializedName(value = "distribution", alternate = {})
        @NonNull String distribution,
        @SerializedName(value = "min_y", alternate = {"minY"})
        int minY,
        @SerializedName(value = "max_y", alternate = {"maxY"})
        int maxY,
        @SerializedName(value = "discard_chance_on_air_exposure", alternate = {"discardChanceOnAirExposure", "air_discard_chance"})
        float discardChanceOnAirExposure,
        @SerializedName(value = "required_mod", alternate = {"requiredMod"})
        @Nullable String requiredMod,
        @SerializedName(value = "adjacent_to_block", alternate = {"adjacentToBlock"})
        @Nullable String adjacentToBlock
) {
    public OreGenRule {
        dimensions = dimensions == null ? Collections.emptyList() : List.copyOf(dimensions);
        dimensionBlacklist = dimensionBlacklist == null ? Collections.emptyList() : List.copyOf(dimensionBlacklist);
        biomes = biomes == null ? Collections.emptyList() : List.copyOf(biomes);
        biomeTags = biomeTags == null ? Collections.emptyList() : List.copyOf(biomeTags);
        biomeBlacklist = biomeBlacklist == null ? Collections.emptyList() : List.copyOf(biomeBlacklist);
        targets = targets == null ? Collections.emptyList() : List.copyOf(targets);

        veinSize = Math.clamp(veinSize, 1, 64);
        veinsPerChunk = Math.clamp(veinsPerChunk, 0, 256);
        rarity = Math.max(0, rarity);
        discardChanceOnAirExposure = Math.clamp(discardChanceOnAirExposure, 0.0f, 1.0f);

        if (distribution == null || distribution.isBlank()) {
            distribution = "triangle";
        }
    }

    public static OreGenRule defaultOverworld(float hardness, boolean hasStone, boolean hasDeepslate) {
        var targets = new ArrayList<OreTargetConfig>();
        if (hasStone) {
            targets.add(OreTargetConfig.stone("ore"));
        }

        if (hasDeepslate) {
            targets.add(OreTargetConfig.deepslate("deepslate_ore"));
        }

        int veinSize = hardness >= 5.0f ? 4 : hardness >= 4.0f ? 7 : 8;
        int veinsPerChunk = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 6 : 9;
        int minY = hardness >= 5.0f ? -64 : -48;
        int maxY = hardness >= 5.0f ? 0 : 64;

        return new OreGenRule(
                true,
                List.of("minecraft:overworld"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of("#minecraft:is_overworld"),
                Collections.emptyList(),
                targets,
                veinSize,
                veinsPerChunk,
                0,
                "triangle",
                minY,
                maxY,
                0.0f,
                null,
                null
        );
    }

    public static OreGenRule defaultNether(float hardness, boolean hasNetherrack) {
        var targets = new ArrayList<OreTargetConfig>();
        if (hasNetherrack) {
            targets.add(OreTargetConfig.netherrack("netherrack_ore"));
        }

        int veinSize = hardness >= 5.0f ? 4 : hardness >= 4.0f ? 6 : 8;
        int veinsPerChunk = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 5 : 8;

        return new OreGenRule(
                true,
                List.of("minecraft:the_nether"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of("#minecraft:is_nether"),
                Collections.emptyList(),
                targets,
                veinSize,
                veinsPerChunk,
                0,
                "uniform",
                10,
                115,
                0.0f,
                null,
                null
        );
    }

    public static OreGenRule defaultEnd(float hardness, boolean hasEndStone) {
        var targets = new ArrayList<OreTargetConfig>();
        if (hasEndStone) {
            targets.add(OreTargetConfig.endStone("end_stone_ore"));
        }

        int veinSize = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 4 : 6;
        int veinsPerChunk = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 4 : 5;

        return new OreGenRule(
                true,
                List.of("minecraft:the_end"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of("#minecraft:is_end"),
                Collections.emptyList(),
                targets,
                veinSize,
                veinsPerChunk,
                0,
                "uniform",
                10,
                70,
                0.0f,
                null,
                null
        );
    }

    public static OreGenRule fromDimensionConfig(
            String dimensionId,
            String biomeTag,
            DimensionOreConfig config,
            List<OreTargetConfig> targets
    ) {
        return new OreGenRule(
                config.enabled(),
                List.of(dimensionId),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(biomeTag),
                Collections.emptyList(),
                targets,
                config.veinSize(),
                config.veinsPerChunk(),
                0,
                config.distribution(),
                config.minY(),
                config.maxY(),
                0.0f,
                null,
                null
        );
    }
}
