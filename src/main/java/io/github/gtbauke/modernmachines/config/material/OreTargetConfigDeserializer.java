package io.github.gtbauke.modernmachines.config.material;

import java.lang.reflect.Type;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class OreTargetConfigDeserializer implements JsonDeserializer<OreTargetConfig> {
    @Override
    public @NonNull OreTargetConfig deserialize(
            @NonNull JsonElement json,
            @NonNull Type typeOfT,
            @NonNull JsonDeserializationContext context
    ) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            var str = json.getAsString();
            if (str.startsWith("#")) {
                return OreTargetConfig.tagMatch(str.substring(1), "ore");
            }

            return str.contains("replaceables")
                    ? OreTargetConfig.tagMatch(str, "ore")
                    : OreTargetConfig.blockMatch(str, "ore");
        }

        if (!json.isJsonObject()) {
            return OreTargetConfig.stone("ore");
        }

        var obj = json.getAsJsonObject();
        var target = getString(obj, "target", "minecraft:stone_ore_replaceables");
        var targetType = getString(obj, "target_type", getString(obj, "targetType", getString(obj, "type", null)));

        if (targetType == null) {
            targetType = target.startsWith("#") || target.contains("replaceables") ? "tag_match" : "block_match";
        }

        if (target.startsWith("#")) {
            target = target.substring(1);
        }

        var oreForm = getString(obj, "ore_form", getString(obj, "oreForm", getString(obj, "form", null)));
        var state = getString(obj, "state", null);

        return new OreTargetConfig(targetType, target, oreForm, state);
    }

    private static @Nullable String getString(JsonObject obj, String key, @Nullable String fallback) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }

        return fallback;
    }
}
