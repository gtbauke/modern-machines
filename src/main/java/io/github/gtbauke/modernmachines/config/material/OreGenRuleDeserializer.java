package io.github.gtbauke.modernmachines.config.material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class OreGenRuleDeserializer implements JsonDeserializer<OreGenRule> {
    @Override
    public @NonNull OreGenRule deserialize(
            @NonNull JsonElement json,
            @NonNull Type typeOfT,
            @NonNull JsonDeserializationContext context
    ) throws JsonParseException {
        if (!json.isJsonObject()) {
            return OreGenRule.defaultOverworld(3.0f, true, true);
        }

        var obj = json.getAsJsonObject();

        boolean enabled = !obj.has("enabled") || obj.get("enabled").getAsBoolean();

        var dimensions = extractStringList(obj, "dimensions", "dimension");
        var dimensionBlacklist = extractStringList(obj, "dimension_blacklist", "dimensionBlacklist");
        var biomes = extractStringList(obj, "biomes", "biome");
        var biomeTags = extractStringList(obj, "biome_tags", "biomeTags", "biome_tag", "biomeTag");
        var biomeBlacklist = extractStringList(obj, "biome_blacklist", "biomeBlacklist");

        var targets = extractTargets(obj, context);

        int veinSize = getInt(obj, 8, "vein_size", "veinSize");
        int veinsPerChunk = getInt(obj, 8, "veins_per_chunk", "veinsPerChunk", "count");
        int rarity = getInt(obj, 0, "rarity");
        var distribution = getString(obj, "triangle", "distribution");
        int minY = getInt(obj, -64, "min_y", "minY");
        int maxY = getInt(obj, 64, "max_y", "maxY");
        float discardChance = getFloat(obj, 0.0f, "discard_chance_on_air_exposure", "discardChanceOnAirExposure", "air_discard_chance");

        var requiredMod = getString(obj, null, "required_mod", "requiredMod");
        var adjacentToBlock = getString(obj, null, "adjacent_to_block", "adjacentToBlock");

        return new OreGenRule(
                enabled,
                dimensions,
                dimensionBlacklist,
                biomes,
                biomeTags,
                biomeBlacklist,
                targets,
                veinSize,
                veinsPerChunk,
                rarity,
                distribution != null ? distribution : "triangle",
                minY,
                maxY,
                discardChance,
                requiredMod,
                adjacentToBlock
        );
    }

    private static @NonNull List<String> extractStringList(JsonObject obj, String... keys) {
        for (var key : keys) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) {
                continue;
            }

            var elem = obj.get(key);
            if (elem.isJsonArray()) {
                var list = new ArrayList<String>();
                for (var item : elem.getAsJsonArray()) {
                    if (!item.isJsonNull()) {
                        list.add(item.getAsString().trim());
                    }
                }

                return Collections.unmodifiableList(list);
            }

            if (elem.isJsonPrimitive()) {
                return List.of(elem.getAsString().trim());
            }
        }

        return Collections.emptyList();
    }

    private static @NonNull List<OreTargetConfig> extractTargets(JsonObject obj, JsonDeserializationContext context) {
        String[] targetKeys = {"targets", "target"};
        for (var key : targetKeys) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) {
                continue;
            }

            var elem = obj.get(key);
            if (elem.isJsonArray()) {
                var list = new ArrayList<OreTargetConfig>();
                for (var item : elem.getAsJsonArray()) {
                    OreTargetConfig target = context.deserialize(item, OreTargetConfig.class);
                    if (target != null) {
                        list.add(target);
                    }
                }

                return Collections.unmodifiableList(list);
            }

            OreTargetConfig target = context.deserialize(elem, OreTargetConfig.class);
            if (target != null) {
                return List.of(target);
            }
        }

        return Collections.emptyList();
    }

    private static int getInt(JsonObject obj, int fallback, String... keys) {
        for (var key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                try {
                    return obj.get(key).getAsInt();
                } catch (Exception ignored) {
                }
            }
        }

        return fallback;
    }

    private static float getFloat(JsonObject obj, float fallback, String... keys) {
        for (var key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                try {
                    return obj.get(key).getAsFloat();
                } catch (Exception ignored) {
                }
            }
        }

        return fallback;
    }

    private static @Nullable String getString(JsonObject obj, @Nullable String fallback, String... keys) {
        for (var key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsString().trim();
            }
        }

        return fallback;
    }
}
