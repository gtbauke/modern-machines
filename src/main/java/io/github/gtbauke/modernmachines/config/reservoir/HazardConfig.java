package io.github.gtbauke.modernmachines.config.reservoir;

import java.util.List;
import org.jspecify.annotations.NonNull;

import com.google.gson.annotations.SerializedName;

public record HazardConfig(
        @SerializedName(value = "eruption", alternate = {})
        boolean eruption,
        @SerializedName(value = "eruption_velocity", alternate = {"eruptionVelocity"})
        float eruptionVelocity,
        @SerializedName(value = "flammable", alternate = {})
        boolean flammable,
        @SerializedName(value = "ignition_radius", alternate = {"ignitionRadius"})
        int ignitionRadius,
        @SerializedName(value = "explosion_strength", alternate = {"explosionStrength"})
        float explosionStrength,
        @SerializedName(value = "toxic", alternate = {})
        boolean toxic,
        @SerializedName(value = "toxic_duration", alternate = {"toxicDuration"})
        int toxicDuration,
        @SerializedName(value = "toxic_effects", alternate = {"toxicEffects"})
        @NonNull List<String> toxicEffects
) {
    public HazardConfig {
        eruptionVelocity = Math.clamp(eruptionVelocity <= 0.0f ? 1.0f : eruptionVelocity, 0.1f, 5.0f);
        ignitionRadius = Math.clamp(ignitionRadius <= 0 ? 5 : ignitionRadius, 1, 16);
        explosionStrength = Math.clamp(explosionStrength <= 0.0f ? 3.0f : explosionStrength, 0.5f, 10.0f);
        toxicDuration = Math.clamp(toxicDuration <= 0 ? 30 : toxicDuration, 5, 300);
        toxicEffects = List.copyOf(toxicEffects);
    }

    public static @NonNull HazardConfig defaults() {
        return new HazardConfig(true, 1.0f, true, 5, 3.0f, false, 30, List.of());
    }

    public static @NonNull HazardConfig crudeOil() {
        return new HazardConfig(
                true,
                1.2f,
                true,
                6,
                3.5f,
                true,
                40,
                List.of("minecraft:blindness", "minecraft:slowness", "minecraft:nausea")
        );
    }

    public static @NonNull HazardConfig naturalGas() {
        return new HazardConfig(
                true,
                2.0f,
                true,
                8,
                4.5f,
                true,
                45,
                List.of("minecraft:nausea", "minecraft:poison")
        );
    }
}
