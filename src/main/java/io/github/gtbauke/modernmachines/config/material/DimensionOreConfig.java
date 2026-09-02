package io.github.gtbauke.modernmachines.config.material;

import com.google.gson.annotations.SerializedName;

public record DimensionOreConfig(
        @SerializedName(value = "enabled", alternate = {})
        boolean enabled,
        @SerializedName(value = "vein_size", alternate = {"veinSize"})
        int veinSize,
        @SerializedName(value = "veins_per_chunk", alternate = {"veinsPerChunk", "count"})
        int veinsPerChunk,
        @SerializedName(value = "min_y", alternate = {"minY"})
        int minY,
        @SerializedName(value = "max_y", alternate = {"maxY"})
        int maxY,
        @SerializedName(value = "distribution", alternate = {})
        String distribution
) {
    public DimensionOreConfig {
        if (distribution == null || distribution.isBlank()) {
            distribution = "triangle";
        }
    }

    public static DimensionOreConfig defaultOverworld(float hardness) {
        int veinSize = hardness >= 5.0f ? 4 : hardness >= 4.0f ? 7 : 8;
        int veinsPerChunk = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 6 : 9;
        int minY = hardness >= 5.0f ? -64 : -48;
        int maxY = hardness >= 5.0f ? 0 : 64;

        return new DimensionOreConfig(true, veinSize, veinsPerChunk, minY, maxY, "triangle");
    }

    public static DimensionOreConfig defaultNether(float hardness) {
        int veinSize = hardness >= 5.0f ? 4 : hardness >= 4.0f ? 6 : 8;
        int veinsPerChunk = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 5 : 8;

        return new DimensionOreConfig(true, veinSize, veinsPerChunk, 10, 115, "uniform");
    }

    public static DimensionOreConfig defaultEnd(float hardness) {
        int veinSize = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 4 : 6;
        int veinsPerChunk = hardness >= 5.0f ? 3 : hardness >= 4.0f ? 4 : 5;

        return new DimensionOreConfig(true, veinSize, veinsPerChunk, 10, 70, "uniform");
    }

    public static DimensionOreConfig disabled() {
        return new DimensionOreConfig(false, 0, 0, 0, 0, "uniform");
    }
}
