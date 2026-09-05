package io.github.gtbauke.modernmachines.config.material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class LargeOreVeinConfigDeserializer implements JsonDeserializer<LargeOreVeinConfig> {
    @Override
    public @NonNull LargeOreVeinConfig deserialize(
            @NonNull JsonElement json,
            @NonNull Type typeOfT,
            @NonNull JsonDeserializationContext context
    ) throws JsonParseException {
        if (!json.isJsonObject()) {
            return new LargeOreVeinConfig(
                    false,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    -64,
                    64,
                    16,
                    "minecraft:granite",
                    0.35f,
                    0.0f,
                    null,
                    0.0f,
                    SurfaceIndicatorConfig.disabled(),
                    VeinNoiseConfig.defaultNoise()
            );
        }

        var obj = json.getAsJsonObject();

        boolean enabled = !obj.has("enabled") || obj.get("enabled").getAsBoolean();

        var dimensions = extractStringList(obj, "dimensions", "dimension");
        var dimensionBlacklist = extractStringList(obj, "dimension_blacklist", "dimensionBlacklist");
        var biomes = extractStringList(obj, "biomes", "biome");
        var biomeTags = extractStringList(obj, "biome_tags", "biomeTags", "biome_tag", "biomeTag");
        var biomeBlacklist = extractStringList(obj, "biome_blacklist", "biomeBlacklist");

        int minY = getInt(obj, -64, "min_y", "minY");
        int maxY = getInt(obj, 64, "max_y", "maxY");
        int rarity = getInt(obj, 16, "rarity");
        var fillerBlock = getString(obj, "minecraft:granite", "filler_block", "fillerBlock", "filler");
        float primaryOreChance = getFloat(obj, 0.35f, "primary_ore_chance", "primaryOreChance", "ore_chance");
        float rawBlockChance = getFloat(obj, 0.0f, "raw_block_chance", "rawBlockChance");
        var secondaryMaterial = getString(obj, null, "secondary_material", "secondaryMaterial", "secondary");
        float secondaryOreChance = getFloat(obj, 0.0f, "secondary_ore_chance", "secondaryOreChance");

        var surfaceIndicators = extractSurfaceIndicators(obj);
        var noise = extractNoise(obj);

        return new LargeOreVeinConfig(
                enabled,
                dimensions,
                dimensionBlacklist,
                biomes,
                biomeTags,
                biomeBlacklist,
                minY,
                maxY,
                rarity,
                fillerBlock != null ? fillerBlock : "minecraft:granite",
                primaryOreChance,
                rawBlockChance,
                secondaryMaterial,
                secondaryOreChance,
                surfaceIndicators,
                noise
        );
    }

    private static @NonNull SurfaceIndicatorConfig extractSurfaceIndicators(JsonObject obj) {
        String[] keys = {"surface_indicators", "surfaceIndicators", "indicator"};
        for (var key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                var elem = obj.get(key);
                if (elem.isJsonObject()) {
                    var indObj = elem.getAsJsonObject();
                    boolean indEnabled = !indObj.has("enabled") || indObj.get("enabled").getAsBoolean();
                    var block = getString(indObj, null, "block");
                    float chance = getFloat(indObj, 0.6f, "chance");
                    return new SurfaceIndicatorConfig(indEnabled, block, chance);
                }
            }
        }

        return SurfaceIndicatorConfig.disabled();
    }

    private static @NonNull VeinNoiseConfig extractNoise(JsonObject obj) {
        if (obj.has("noise") && !obj.get("noise").isJsonNull()) {
            var elem = obj.get("noise");
            if (elem.isJsonObject()) {
                var noiseObj = elem.getAsJsonObject();
                int length = getInt(noiseObj, 48, "length");
                int thickness = getInt(noiseObj, 6, "thickness");
                float density = getFloat(noiseObj, 0.65f, "density");
                return new VeinNoiseConfig(length, thickness, density);
            }
        }

        return VeinNoiseConfig.defaultNoise();
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
