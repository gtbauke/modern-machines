package io.github.gtbauke.modernmachines.config.material;

import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record OreGenConfig(
        @SerializedName(value = "enabled", alternate = {})
        boolean enabled,
        @SerializedName(value = "overworld", alternate = {})
        DimensionOreConfig overworld,
        @SerializedName(value = "nether", alternate = {})
        DimensionOreConfig nether,
        @SerializedName(value = "end", alternate = {})
        DimensionOreConfig end
) {
    public static OreGenConfig createDefault(float hardness, boolean hasOverworld, boolean hasNether, boolean hasEnd) {
        var overworld = hasOverworld ? DimensionOreConfig.defaultOverworld(hardness) : DimensionOreConfig.disabled();
        var nether = hasNether ? DimensionOreConfig.defaultNether(hardness) : DimensionOreConfig.disabled();
        var end = hasEnd ? DimensionOreConfig.defaultEnd(hardness) : DimensionOreConfig.disabled();

        return new OreGenConfig(hasOverworld || hasNether || hasEnd, overworld, nether, end);
    }

    public static OreGenConfig mergeWithDefaults(
            @Nullable OreGenConfig config,
            float hardness,
            boolean hasOverworld,
            boolean hasNether,
            boolean hasEnd
    ) {
        if (config == null) {
            return createDefault(hardness, hasOverworld, hasNether, hasEnd);
        }

        var overworld = config.overworld() != null
                ? config.overworld()
                : (hasOverworld ? DimensionOreConfig.defaultOverworld(hardness) : DimensionOreConfig.disabled());

        var nether = config.nether() != null
                ? config.nether()
                : (hasNether ? DimensionOreConfig.defaultNether(hardness) : DimensionOreConfig.disabled());

        var end = config.end() != null
                ? config.end()
                : (hasEnd ? DimensionOreConfig.defaultEnd(hardness) : DimensionOreConfig.disabled());

        return new OreGenConfig(config.enabled(), overworld, nether, end);
    }
}
