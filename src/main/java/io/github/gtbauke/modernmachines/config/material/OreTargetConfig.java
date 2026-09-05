package io.github.gtbauke.modernmachines.config.material;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record OreTargetConfig(
        @SerializedName(value = "target_type", alternate = {"targetType", "type"})
        @NonNull String targetType,
        @SerializedName(value = "target", alternate = {})
        @NonNull String target,
        @SerializedName(value = "ore_form", alternate = {"oreForm", "form"})
        @Nullable String oreForm,
        @SerializedName(value = "state", alternate = {})
        @Nullable String state
) {
    public OreTargetConfig {
        if (targetType == null || targetType.isBlank()) {
            targetType = "tag_match";
        }

        if (target == null || target.isBlank()) {
            target = "minecraft:stone_ore_replaceables";
        }
    }

    public static OreTargetConfig tagMatch(String tag, String oreForm) {
        return new OreTargetConfig("tag_match", tag, oreForm, null);
    }

    public static OreTargetConfig blockMatch(String block, String oreForm) {
        return new OreTargetConfig("block_match", block, oreForm, null);
    }

    public static OreTargetConfig stone(String oreForm) {
        return tagMatch("minecraft:stone_ore_replaceables", oreForm);
    }

    public static OreTargetConfig deepslate(String oreForm) {
        return tagMatch("minecraft:deepslate_ore_replaceables", oreForm);
    }

    public static OreTargetConfig netherrack(String oreForm) {
        return blockMatch("minecraft:netherrack", oreForm);
    }

    public static OreTargetConfig endStone(String oreForm) {
        return blockMatch("minecraft:end_stone", oreForm);
    }
}
