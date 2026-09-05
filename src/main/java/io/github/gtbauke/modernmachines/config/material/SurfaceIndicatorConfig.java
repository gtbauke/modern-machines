package io.github.gtbauke.modernmachines.config.material;

import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record SurfaceIndicatorConfig(
        @SerializedName(value = "enabled", alternate = {})
        boolean enabled,
        @SerializedName(value = "block", alternate = {})
        @Nullable String block,
        @SerializedName(value = "chance", alternate = {})
        float chance
) {
    public SurfaceIndicatorConfig {
        chance = Math.clamp(chance <= 0.0f ? 0.6f : chance, 0.0f, 1.0f);
    }

    public static SurfaceIndicatorConfig disabled() {
        return new SurfaceIndicatorConfig(false, null, 0.0f);
    }

    public static SurfaceIndicatorConfig of(String block, float chance) {
        return new SurfaceIndicatorConfig(true, block, chance);
    }
}
