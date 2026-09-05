package io.github.gtbauke.modernmachines.config.reservoir;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

public class ReservoirLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ReservoirConfig.class, new ReservoirConfigDeserializer())
            .setPrettyPrinting()
            .create();

    private static final Map<String, ReservoirConfig> RESERVOIRS = new ConcurrentHashMap<>();

    public static void loadEarly() {
        RESERVOIRS.clear();
        initDefaults();
        loadClasspathReservoirs();
        loadConfigReservoirs();
    }

    public static @NonNull Map<String, ReservoirConfig> getAllReservoirs() {
        return Collections.unmodifiableMap(RESERVOIRS);
    }

    public static @Nullable ReservoirConfig getReservoir(String name) {
        return RESERVOIRS.get(name.toLowerCase(Locale.ROOT));
    }

    private static void initDefaults() {
        registerDefault(new ReservoirConfig(
                "crude_oil",
                true,
                "modernmachines:crude_oil",
                "modernmachines:oil_shale",
                "modernmachines:capstone",
                List.of("minecraft:overworld"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of("#minecraft:is_overworld"),
                Collections.emptyList(),
                -50,
                20,
                32,
                10,
                5,
                7.5f,
                250_000L,
                HazardConfig.crudeOil()
        ));

        registerDefault(new ReservoirConfig(
                "natural_gas",
                true,
                "modernmachines:natural_gas",
                "modernmachines:capstone",
                null,
                List.of("minecraft:overworld"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of("#minecraft:is_overworld"),
                Collections.emptyList(),
                -40,
                10,
                40,
                8,
                4,
                10.0f,
                150_000L,
                HazardConfig.naturalGas()
        ));

        registerDefault(new ReservoirConfig(
                "geothermal_brine",
                true,
                "minecraft:water",
                "minecraft:tuff",
                null,
                List.of("minecraft:overworld", "minecraft:the_nether"),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                -60,
                -10,
                28,
                9,
                5,
                6.0f,
                500_000L,
                new HazardConfig(true, 1.5f, false, 0, 1.0f, false, 0, List.of())
        ));
    }

    private static void registerDefault(ReservoirConfig config) {
        if (config.name() != null) {
            RESERVOIRS.put(config.name().toLowerCase(Locale.ROOT), config);
        }
    }

    private static void loadClasspathReservoirs() {
        var devPath = Path.of("src/main/resources/data/modernmachines/reservoirs");
        if (Files.isDirectory(devPath)) {
            try (var stream = Files.list(devPath)) {
                stream.filter(path -> path.toString().endsWith(".json")).forEach(ReservoirLoader::loadReservoirFile);
            } catch (IOException e) {
                LOGGER.warn("Failed to read dev resources directory for reservoirs: {}", devPath, e);
            }
        }

        var loader = FMLLoader.getCurrentOrNull();
        if (loader == null) {
            return;
        }

        var modFile = loader.getModFileByClass(ModernMachines.class);
        if (modFile == null) {
            return;
        }

        var contents = modFile.getContents();
        for (var root : contents.getContentRoots()) {
            var resDir = root.resolve("data/modernmachines/reservoirs");
            if (Files.isDirectory(resDir)) {
                try (var stream = Files.list(resDir)) {
                    stream.filter(path -> path.toString().endsWith(".json")).forEach(ReservoirLoader::loadReservoirFile);
                } catch (IOException e) {
                    LOGGER.warn("Failed to list reservoirs in mod content root: {}", resDir, e);
                }
            }
        }

        contents.visitContent("data/modernmachines/reservoirs", (relPath, resource) -> {
            if (!relPath.endsWith(".json")) {
                return;
            }

            try (var is = resource.open()) {
                loadReservoirStream(is, relPath);
            } catch (Exception e) {
                LOGGER.warn("Failed to load reservoir from jar resource '{}': {}", relPath, e.getMessage());
            }
        });
    }

    private static void loadConfigReservoirs() {
        var configDir = FMLPaths.CONFIGDIR.get().resolve("modernmachines/reservoirs");

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create reservoirs config directory: {}", configDir, e);
            return;
        }

        try (var stream = Files.list(configDir)) {
            stream.filter(path -> path.toString().endsWith(".json")).forEach(ReservoirLoader::loadReservoirFile);
        } catch (IOException e) {
            LOGGER.error("Failed to list files in reservoirs config directory: {}", configDir, e);
        }
    }

    private static void loadReservoirFile(Path filePath) {
        var fileName = filePath.getFileName().toString();
        if (fileName.endsWith(".example") || fileName.endsWith(".disabled")) {
            return;
        }

        try (var is = Files.newInputStream(filePath)) {
            loadReservoirStream(is, filePath.toString());
        } catch (Exception e) {
            LOGGER.error("Failed to read reservoir file: {}", filePath, e);
        }
    }

    private static void loadReservoirStream(java.io.InputStream is, String sourceIdentifier) {
        ReservoirConfig config;
        try (var reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            config = GSON.fromJson(reader, ReservoirConfig.class);
        } catch (Exception e) {
            LOGGER.error("Failed to parse custom reservoir JSON from {}: {}", sourceIdentifier, e);
            return;
        }

        if (config == null) {
            LOGGER.warn("Reservoir config is empty from {}", sourceIdentifier);
            return;
        }

        var fileName = sourceIdentifier.contains("/") ? sourceIdentifier.substring(sourceIdentifier.lastIndexOf('/') + 1) : sourceIdentifier;
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf('\\') + 1);
        }

        var name = config.name() != null && !config.name().isBlank()
                ? config.name().trim().toLowerCase(Locale.ROOT)
                : fileName.replace(".json", "").toLowerCase(Locale.ROOT);

        var finalConfig = new ReservoirConfig(
                name,
                config.enabled(),
                config.fluid(),
                config.barrierBlock(),
                config.capstoneBlock(),
                config.dimensions(),
                config.dimensionBlacklist(),
                config.biomes(),
                config.biomeTags(),
                config.biomeBlacklist(),
                config.minY(),
                config.maxY(),
                config.rarity(),
                config.radiusXz(),
                config.radiusY(),
                config.initialPressure(),
                config.capacity(),
                config.hazards()
        );

        RESERVOIRS.put(name, finalConfig);
        LOGGER.info("Loaded reservoir config '{}' for fluid '{}' from {}", name, finalConfig.fluid(), sourceIdentifier);
    }
}
