package io.github.gtbauke.modernmachines.config.material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public record OreGenConfig(
        @SerializedName(value = "enabled", alternate = {})
        boolean enabled,
        @SerializedName(value = "rules", alternate = {"rule"})
        @Nullable List<OreGenRule> rules,
        @SerializedName(value = "large_veins", alternate = {"largeVeins", "large_vein", "largeVein"})
        @Nullable List<LargeOreVeinConfig> largeVeins,
        @SerializedName(value = "overworld", alternate = {})
        @Nullable DimensionOreConfig overworld,
        @SerializedName(value = "nether", alternate = {})
        @Nullable DimensionOreConfig nether,
        @SerializedName(value = "end", alternate = {})
        @Nullable DimensionOreConfig end
) {
    public OreGenConfig(
            boolean enabled,
            @Nullable List<OreGenRule> rules,
            @Nullable DimensionOreConfig overworld,
            @Nullable DimensionOreConfig nether,
            @Nullable DimensionOreConfig end
    ) {
        this(enabled, rules, null, overworld, nether, end);
    }

    public OreGenConfig(
            boolean enabled,
            @Nullable DimensionOreConfig overworld,
            @Nullable DimensionOreConfig nether,
            @Nullable DimensionOreConfig end
    ) {
        this(enabled, null, null, overworld, nether, end);
    }

    public @NonNull List<OreGenRule> getResolvedRules(
            float hardness,
            boolean hasStone,
            boolean hasDeepslate,
            boolean hasNether,
            boolean hasEnd
    ) {
        if (!enabled) {
            return Collections.emptyList();
        }

        if (rules != null && !rules.isEmpty()) {
            var result = new ArrayList<OreGenRule>();
            for (var rule : rules) {
                if (rule != null && rule.enabled()) {
                    result.add(rule);
                }
            }

            return Collections.unmodifiableList(result);
        }

        var result = new ArrayList<OreGenRule>();
        if (overworld != null && overworld.enabled()) {
            var targets = new ArrayList<OreTargetConfig>();
            if (hasStone) {
                targets.add(OreTargetConfig.stone("ore"));
            }

            if (hasDeepslate) {
                targets.add(OreTargetConfig.deepslate("deepslate_ore"));
            }

            if (!targets.isEmpty()) {
                result.add(OreGenRule.fromDimensionConfig(
                        "minecraft:overworld",
                        "#minecraft:is_overworld",
                        overworld,
                        targets
                ));
            }
        }

        if (nether != null && nether.enabled() && hasNether) {
            var targets = List.of(OreTargetConfig.netherrack("netherrack_ore"));
            result.add(OreGenRule.fromDimensionConfig(
                    "minecraft:the_nether",
                    "#minecraft:is_nether",
                    nether,
                    targets
            ));
        }

        if (end != null && end.enabled() && hasEnd) {
            var targets = List.of(OreTargetConfig.endStone("end_stone_ore"));
            result.add(OreGenRule.fromDimensionConfig(
                    "minecraft:the_end",
                    "#minecraft:is_end",
                    end,
                    targets
            ));
        }

        return Collections.unmodifiableList(result);
    }

    public @NonNull List<LargeOreVeinConfig> getResolvedLargeVeins() {
        if (!enabled || largeVeins == null || largeVeins.isEmpty()) {
            return Collections.emptyList();
        }

        var result = new ArrayList<LargeOreVeinConfig>();
        for (var vein : largeVeins) {
            if (vein != null && vein.enabled()) {
                result.add(vein);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public static @NonNull OreGenConfig createDefault(
            float hardness,
            boolean hasStone,
            boolean hasDeepslate,
            boolean hasNether,
            boolean hasEnd
    ) {
        var rules = new ArrayList<OreGenRule>();
        if (hasStone || hasDeepslate) {
            rules.add(OreGenRule.defaultOverworld(hardness, hasStone, hasDeepslate));
        }

        if (hasNether) {
            rules.add(OreGenRule.defaultNether(hardness, true));
        }

        if (hasEnd) {
            rules.add(OreGenRule.defaultEnd(hardness, true));
        }

        return new OreGenConfig(!rules.isEmpty(), rules, null, null, null, null);
    }

    public static @NonNull OreGenConfig createDefault(
            float hardness,
            boolean hasOverworld,
            boolean hasNether,
            boolean hasEnd
    ) {
        return createDefault(hardness, hasOverworld, hasOverworld, hasNether, hasEnd);
    }

    public static @NonNull OreGenConfig mergeWithDefaults(
            @Nullable OreGenConfig config,
            float hardness,
            boolean hasStone,
            boolean hasDeepslate,
            boolean hasNether,
            boolean hasEnd
    ) {
        if (config == null) {
            return createDefault(hardness, hasStone, hasDeepslate, hasNether, hasEnd);
        }

        if (config.rules() != null && !config.rules().isEmpty()) {
            return config;
        }

        var overworld = config.overworld() != null
                ? config.overworld()
                : ((hasStone || hasDeepslate) ? DimensionOreConfig.defaultOverworld(hardness) : DimensionOreConfig.disabled());

        var nether = config.nether() != null
                ? config.nether()
                : (hasNether ? DimensionOreConfig.defaultNether(hardness) : DimensionOreConfig.disabled());

        var end = config.end() != null
                ? config.end()
                : (hasEnd ? DimensionOreConfig.defaultEnd(hardness) : DimensionOreConfig.disabled());

        return new OreGenConfig(config.enabled(), config.rules(), config.largeVeins(), overworld, nether, end);
    }

    public static @NonNull OreGenConfig mergeWithDefaults(
            @Nullable OreGenConfig config,
            float hardness,
            boolean hasOverworld,
            boolean hasNether,
            boolean hasEnd
    ) {
        return mergeWithDefaults(config, hardness, hasOverworld, hasOverworld, hasNether, hasEnd);
    }
}
