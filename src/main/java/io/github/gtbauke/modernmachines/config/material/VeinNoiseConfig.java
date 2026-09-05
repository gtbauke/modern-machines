package io.github.gtbauke.modernmachines.config.material;

import com.google.gson.annotations.SerializedName;

public record VeinNoiseConfig(
        @SerializedName(value = "length", alternate = {})
        int length,
        @SerializedName(value = "thickness", alternate = {})
        int thickness,
        @SerializedName(value = "density", alternate = {})
        float density
) {
    public VeinNoiseConfig {
        length = Math.clamp(length <= 0 ? 48 : length, 16, 128);
        thickness = Math.clamp(thickness <= 0 ? 6 : thickness, 2, 16);
        density = Math.clamp(density <= 0.0f ? 0.65f : density, 0.1f, 1.0f);
    }

    public static VeinNoiseConfig defaultNoise() {
        return new VeinNoiseConfig(48, 6, 0.65f);
    }
}
