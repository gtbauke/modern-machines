package io.github.gtbauke.modernmachines.config.reservoir;

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

public class ReservoirConfigDeserializer implements JsonDeserializer<ReservoirConfig> {
    @Override
    public @NonNull ReservoirConfig deserialize(
            @NonNull JsonElement json,
            @NonNull Type typeOfT,
            @NonNull JsonDeserializationContext context
    ) throws JsonParseException {
        if (!json.isJsonObject()) {
            return new ReservoirConfig(
                    null,
                    false,
                    "minecraft:water",
                    "minecraft:tuff",
                    null,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    -60,
                    20,
                    24,
                    8,
                    4,
                    5.0f,
                    100_000L,
                    HazardConfig.defaults()
            );
        }

        var obj = json.getAsJsonObject();

        var name = getString(obj, null, "name");
        boolean enabled = !obj.has("enabled") || obj.get("enabled").getAsBoolean();

        var fluid = getString(obj, "minecraft:water", "fluid", "fluid_id", "fluidId");
        var barrierBlock = getString(obj, "minecraft:tuff", "barrier_block", "barrierBlock", "barrier");
        var capstoneBlock = getString(obj, null, "capstone_block", "capstoneBlock", "capstone");

        var dimensions = extractStringList(obj, "dimensions", "dimension");
        var dimensionBlacklist = extractStringList(obj, "dimension_blacklist", "dimensionBlacklist");
        var biomes = extractStringList(obj, "biomes", "biome");
        var biomeTags = extractStringList(obj, "biome_tags", "biomeTags", "biome_tag", "biomeTag");
        var biomeBlacklist = extractStringList(obj, "biome_blacklist", "biomeBlacklist");

        int minY = getInt(obj, -60, "min_y", "minY");
        int maxY = getInt(obj, 20, "max_y", "maxY");
        int rarity = getInt(obj, 24, "rarity");
        int radiusXz = getInt(obj, 8, "radius_xz", "radiusXz", "radius");
        int radiusY = getInt(obj, 4, "radius_y", "radiusY", "height");
        float initialPressure = getFloat(obj, 5.0f, "initial_pressure", "initialPressure", "pressure");
        long capacity = getLong(obj, 100_000L, "capacity", "volume", "max_volume");

        var hazards = extractHazards(obj);

        return new ReservoirConfig(
                name,
                enabled,
                fluid != null ? fluid : "minecraft:water",
                barrierBlock != null ? barrierBlock : "minecraft:tuff",
                capstoneBlock,
                dimensions,
                dimensionBlacklist,
                biomes,
                biomeTags,
                biomeBlacklist,
                minY,
                maxY,
                rarity,
                radiusXz,
                radiusY,
                initialPressure,
                capacity,
                hazards
        );
    }

    private static @NonNull HazardConfig extractHazards(JsonObject obj) {
        String[] keys = {"hazards", "hazard"};
        for (var key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                var elem = obj.get(key);
                if (elem.isJsonObject()) {
                    var hazObj = elem.getAsJsonObject();
                    boolean eruption = !hazObj.has("eruption") || hazObj.get("eruption").getAsBoolean();
                    float eruptionVelocity = getFloat(hazObj, 1.0f, "eruption_velocity", "eruptionVelocity");
                    boolean flammable = !hazObj.has("flammable") || hazObj.get("flammable").getAsBoolean();
                    int ignitionRadius = getInt(hazObj, 5, "ignition_radius", "ignitionRadius");
                    float explosionStrength = getFloat(hazObj, 3.0f, "explosion_strength", "explosionStrength");
                    boolean toxic = hazObj.has("toxic") && hazObj.get("toxic").getAsBoolean();
                    int toxicDuration = getInt(hazObj, 30, "toxic_duration", "toxicDuration");
                    var toxicEffects = extractStringList(hazObj, "toxic_effects", "toxicEffects");

                    return new HazardConfig(
                            eruption,
                            eruptionVelocity,
                            flammable,
                            ignitionRadius,
                            explosionStrength,
                            toxic,
                            toxicDuration,
                            toxicEffects
                    );
                }
            }
        }

        return HazardConfig.defaults();
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

    private static long getLong(JsonObject obj, long fallback, String... keys) {
        for (var key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                try {
                    return obj.get(key).getAsLong();
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
