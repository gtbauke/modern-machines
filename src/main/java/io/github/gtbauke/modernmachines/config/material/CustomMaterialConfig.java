package io.github.gtbauke.modernmachines.config.material;

import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class CustomMaterialConfig {
    public @Nullable String name;

    @SerializedName(value = "display_name", alternate = {"displayName"})
    public @Nullable String displayName;

    public @Nullable String color;
    public @Nullable String type;
    public float hardness = 3.0f;
    public float resistance = 3.0f;

    @SerializedName(value = "mining_level", alternate = {"miningLevel"})
    public @Nullable String miningLevel;

    @SerializedName(value = "melting_point", alternate = {"meltingPoint"})
    public int meltingPoint = 1000;

    @SerializedName(value = "overlay_index", alternate = {"overlayIndex"})
    public int overlayIndex = 1;

    public @Nullable List<String> forms;

    @SerializedName(value = "ore_generation", alternate = {"oreGeneration"})
    public @Nullable OreGenConfig oreGeneration;

    // Optional tool stats configuration
    public @Nullable HeadConfig head;
    public @Nullable HandleConfig handle;
    public @Nullable BindingConfig binding;
    public @Nullable AttachmentConfig attachment;
    public @Nullable List<TraitConfig> traits;

    public int parseColorHex() {
        if (color == null || color.isBlank()) {
            return 0xFFFFFF;
        }

        try {
            var cleaned = color.replace("#", "").replace("0x", "").trim();
            return (int) Long.parseLong(cleaned, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public List<String> getForms() {
        if (forms == null) {
            return Collections.emptyList();
        }

        return forms;
    }

    public static class HeadConfig {
        public int durability = 200;

        @SerializedName(value = "mining_speed", alternate = {"miningSpeed"})
        public float miningSpeed = 4.0f;

        @SerializedName(value = "attack_damage", alternate = {"attackDamage"})
        public float attackDamage = 2.0f;

        @SerializedName(value = "harvest_tier", alternate = {"harvestTier"})
        public String harvestTier = "stone";
    }

    public static class HandleConfig {
        @SerializedName(value = "durability_multiplier", alternate = {"durabilityMultiplier"})
        public float durabilityMultiplier = 1.0f;

        @SerializedName(value = "mining_speed_multiplier", alternate = {"miningSpeedMultiplier"})
        public float miningSpeedMultiplier = 1.0f;

        @SerializedName(value = "attack_speed_bonus", alternate = {"attackSpeedBonus"})
        public float attackSpeedBonus = 0.0f;
    }

    public static class BindingConfig {
        @SerializedName(value = "bonus_durability", alternate = {"bonusDurability"})
        public int bonusDurability = 50;
    }

    public static class AttachmentConfig {
        @SerializedName(value = "bonus_durability", alternate = {"bonusDurability"})
        public int bonusDurability = 25;

        @SerializedName(value = "attack_damage_bonus", alternate = {"attackDamageBonus"})
        public float attackDamageBonus = 0.5f;

        @SerializedName(value = "speed_bonus", alternate = {"speedBonus"})
        public float speedBonus = 0.2f;

        @SerializedName(value = "tier_override", alternate = {"tierOverride"})
        public @Nullable String tierOverride;
    }

    public static class TraitConfig {
        public String id = "";
        public int level = 1;
        public @Nullable String description;
    }
}
