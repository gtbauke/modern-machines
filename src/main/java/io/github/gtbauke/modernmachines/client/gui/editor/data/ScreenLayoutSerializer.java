package io.github.gtbauke.modernmachines.client.gui.editor.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ScreenLayoutDefinition;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ScreenLayoutSerializer {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Path getScreensDirectory() {
        var dir = FMLPaths.CONFIGDIR.get().resolve(ModernMachines.MOD_ID).resolve("screens");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            ModernMachines.LOGGER.error("Failed to create screens configuration directory", e);
        }

        return dir;
    }

    public static Path getLayoutPath(String screenId) {
        var sanitized = screenId.toLowerCase().replaceAll("[^a-z0-9_\\-]", "_");
        return getScreensDirectory().resolve(sanitized + ".json");
    }

    public static boolean saveLayout(ScreenLayoutDefinition layout) {
        if (layout == null || layout.getScreenId() == null || layout.getScreenId().isEmpty()) {
            return false;
        }

        var path = getLayoutPath(layout.getScreenId());
        try (var writer = new FileWriter(path.toFile())) {
            GSON.toJson(layout, writer);
            return true;
        } catch (IOException e) {
            ModernMachines.LOGGER.error("Failed to save screen layout to: {}", path, e);
            return false;
        }
    }

    public static Optional<ScreenLayoutDefinition> loadLayout(String screenId) {
        var path = getLayoutPath(screenId);
        var file = path.toFile();
        if (!file.exists()) {
            return Optional.empty();
        }

        try (var reader = new FileReader(file)) {
            var layout = GSON.fromJson(reader, ScreenLayoutDefinition.class);
            return Optional.ofNullable(layout);
        } catch (IOException e) {
            ModernMachines.LOGGER.error("Failed to load screen layout from: {}", path, e);
            return Optional.empty();
        }
    }

    public static String toJsonString(ScreenLayoutDefinition layout) {
        return GSON.toJson(layout);
    }

    public static ScreenLayoutDefinition fromJsonString(String json) {
        return GSON.fromJson(json, ScreenLayoutDefinition.class);
    }
}
