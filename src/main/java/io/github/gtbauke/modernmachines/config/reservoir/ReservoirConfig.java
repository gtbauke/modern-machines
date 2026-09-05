package io.github.gtbauke.modernmachines.config.reservoir;

import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record ReservoirConfig(
        @SerializedName(value = "name", alternate = {})
        @Nullable String name,
        @SerializedName(value = "enabled", alternate = {})
        boolean enabled,
        @SerializedName(value = "fluid", alternate = {"fluid_id", "fluidId"})
        @NonNull String fluid,
        @SerializedName(value = "barrier_block", alternate = {"barrierBlock", "barrier"})
        @NonNull String barrierBlock,
        @SerializedName(value = "capstone_block", alternate = {"capstoneBlock", "capstone"})
        @Nullable String capstoneBlock,
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
        @SerializedName(value = "radius_xz", alternate = {"radiusXz", "radius"})
        int radiusXz,
        @SerializedName(value = "radius_y", alternate = {"radiusY", "height"})
        int radiusY,
        @SerializedName(value = "initial_pressure", alternate = {"initialPressure", "pressure"})
        float initialPressure,
        @SerializedName(value = "capacity", alternate = {"volume", "max_volume"})
        long capacity,
        @SerializedName(value = "hazards", alternate = {"hazard"})
        @NonNull HazardConfig hazards
) {
    public ReservoirConfig {
        dimensions = List.copyOf(dimensions);
        dimensionBlacklist = List.copyOf(dimensionBlacklist);
        biomes = List.copyOf(biomes);
        biomeTags = List.copyOf(biomeTags);
        biomeBlacklist = List.copyOf(biomeBlacklist);

        if (fluid.isBlank()) {
            fluid = "minecraft:water";
        }

        if (barrierBlock.isBlank()) {
            barrierBlock = "minecraft:tuff";
        }

        rarity = Math.clamp(rarity <= 0 ? 24 : rarity, 1, 1000);
        radiusXz = Math.clamp(radiusXz <= 0 ? 8 : radiusXz, 3, 24);
        radiusY = Math.clamp(radiusY <= 0 ? 4 : radiusY, 2, 16);
        initialPressure = Math.clamp(initialPressure <= 0.0f ? 5.0f : initialPressure, 0.5f, 20.0f);
        capacity = Math.clamp(capacity <= 0 ? 100_000L : capacity, 1_000L, 10_000_000L);
    }
}
