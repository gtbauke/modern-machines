package io.github.gtbauke.modernmachines.config.material;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.api.modular.MaterialTrait;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.MaterialBuilder;
import io.github.gtbauke.modernmachines.api.resource.MaterialType;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.fml.loading.FMLPaths;

public class CustomMaterialLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, OreGenConfig> ORE_GEN_CONFIGS = new LinkedHashMap<>();
    private static final Map<Identifier, MaterialToolStats> CUSTOM_TOOL_STATS = new ConcurrentHashMap<>();
    private static final Set<String> CUSTOM_MATERIAL_NAMES = ConcurrentHashMap.newKeySet();

    public static void loadEarly() {
        initBuiltInDefaults();

        Path configDir = FMLPaths.CONFIGDIR.get().resolve("modernmachines/materials");

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                createExampleFile(configDir);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create materials config directory: {}", configDir, e);
            return;
        }

        try (var stream = Files.list(configDir)) {
            stream.filter(path -> path.toString().endsWith(".json")).forEach(CustomMaterialLoader::loadMaterialFile);
        } catch (IOException e) {
            LOGGER.error("Failed to list files in materials config directory: {}", configDir, e);
        }
    }

    private static void initBuiltInDefaults() {
        for (var material : ModMaterials.getAllMaterials()) {
            var name = material.name();
            var resourcePath = "/data/modernmachines/materials/" + name + ".json";

            try (var is = CustomMaterialLoader.class.getResourceAsStream(resourcePath)) {
                if (is != null) {
                    try (var reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        var config = GSON.fromJson(reader, CustomMaterialConfig.class);
                        if (config != null && config.oreGeneration != null) {
                            var oreGen = OreGenConfig.mergeWithDefaults(
                                    config.oreGeneration,
                                    material.hardness(),
                                    material.hasForm(ResourceForm.ORE) || material.hasForm(ResourceForm.DEEPSLATE_ORE),
                                    material.hasForm(ResourceForm.NETHERRACK_ORE),
                                    material.hasForm(ResourceForm.END_STONE_ORE)
                            );
                            ORE_GEN_CONFIGS.put(name, oreGen);
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load resource material JSON for '{}': {}", name, e.getMessage());
            }

            var defaultGen = OreGenConfig.createDefault(
                    material.hardness(),
                    material.hasForm(ResourceForm.ORE) || material.hasForm(ResourceForm.DEEPSLATE_ORE),
                    material.hasForm(ResourceForm.NETHERRACK_ORE),
                    material.hasForm(ResourceForm.END_STONE_ORE)
            );
            ORE_GEN_CONFIGS.put(name, defaultGen);
        }
    }

    private static void loadMaterialFile(Path filePath) {
        var fileName = filePath.getFileName().toString();
        if (fileName.endsWith(".example") || fileName.endsWith(".disabled")) {
            return;
        }

        CustomMaterialConfig config;
        try (var reader = new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8)) {
            config = GSON.fromJson(reader, CustomMaterialConfig.class);
        } catch (Exception e) {
            LOGGER.error("Failed to parse custom material JSON from file: {}", filePath, e);
            return;
        }

        if (config == null) {
            LOGGER.warn("Material config file is empty: {}", filePath);
            return;
        }

        var name = config.name != null && !config.name.isBlank()
                ? config.name.trim().toLowerCase(Locale.ROOT)
                : fileName.replace(".json", "").toLowerCase(Locale.ROOT);

        if (!name.matches("^[a-z0-9_]+$")) {
            LOGGER.error("Invalid material name '{}' in file: {}. Names must only contain lowercase a-z, 0-9, and _", name, filePath);
            return;
        }

        var existingMaterial = ModMaterials.getByName(name);
        if (existingMaterial != null) {
            applyExistingMaterialOverride(existingMaterial, config);
            return;
        }

        registerNewMaterial(name, config);
    }

    private static void applyExistingMaterialOverride(Material material, CustomMaterialConfig config) {
        var oreGen = OreGenConfig.mergeWithDefaults(
                config.oreGeneration,
                material.hardness(),
                material.hasForm(ResourceForm.ORE) || material.hasForm(ResourceForm.DEEPSLATE_ORE),
                material.hasForm(ResourceForm.NETHERRACK_ORE),
                material.hasForm(ResourceForm.END_STONE_ORE)
        );

        ORE_GEN_CONFIGS.put(material.name(), oreGen);

        var toolStats = buildToolStats(material.getId(), material.displayName(), material.colorHex(), material.meltingPoint(), material.hardness(), config, material);
        if (toolStats != null) {
            CUSTOM_TOOL_STATS.put(material.getId(), toolStats);
        }

        LOGGER.info("Applied config overrides for existing material '{}'", material.name());
    }

    private static void registerNewMaterial(String name, CustomMaterialConfig config) {
        var displayName = config.displayName != null && !config.displayName.isBlank()
                ? config.displayName.trim()
                : capitalize(name);

        var materialType = parseMaterialType(config.type);
        var colorHex = config.parseColorHex();
        var miningLevel = parseMiningLevel(config.miningLevel);

        var builder = MaterialBuilder.of(name)
                .displayName(displayName)
                .type(materialType)
                .color(colorHex)
                .mapColor(MapColor.METAL)
                .miningLevel(miningLevel)
                .hardness(config.hardness, config.resistance)
                .meltingPoint(config.meltingPoint)
                .overlayIndex(config.overlayIndex);

        var forms = resolveForms(config, materialType);
        builder.forms(forms.toArray(new ResourceForm[0]));

        var material = ModMaterials.registerCustom(builder);
        CUSTOM_MATERIAL_NAMES.add(name);

        var oreGen = OreGenConfig.mergeWithDefaults(
                config.oreGeneration,
                material.hardness(),
                material.hasForm(ResourceForm.ORE) || material.hasForm(ResourceForm.DEEPSLATE_ORE),
                material.hasForm(ResourceForm.NETHERRACK_ORE),
                material.hasForm(ResourceForm.END_STONE_ORE)
        );

        ORE_GEN_CONFIGS.put(name, oreGen);

        var toolStats = buildToolStats(material.getId(), displayName, colorHex, config.meltingPoint, config.hardness, config, material);
        if (toolStats != null) {
            CUSTOM_TOOL_STATS.put(material.getId(), toolStats);
        }

        LOGGER.info("Registered custom material '{}' with {} forms from config", name, material.supportedForms().size());
    }

    private static List<ResourceForm> resolveForms(CustomMaterialConfig config, MaterialType type) {
        var requested = config.getForms();
        if (!requested.isEmpty()) {
            var result = new ArrayList<ResourceForm>();
            for (var formStr : requested) {
                try {
                    result.add(ResourceForm.valueOf(formStr.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Unknown ResourceForm '{}' specified in material config '{}'", formStr, config.name);
                }
            }

            if (!result.isEmpty()) {
                return result;
            }
        }

        if (type == MaterialType.ALLOY) {
            return List.of(
                    ResourceForm.STORAGE_BLOCK,
                    ResourceForm.INGOT,
                    ResourceForm.NUGGET,
                    ResourceForm.DUST,
                    ResourceForm.PLATE,
                    ResourceForm.ROD,
                    ResourceForm.GEAR,
                    ResourceForm.MOLTEN
            );
        }

        return List.of(
                ResourceForm.ORE,
                ResourceForm.DEEPSLATE_ORE,
                ResourceForm.NETHERRACK_ORE,
                ResourceForm.END_STONE_ORE,
                ResourceForm.RAW_ORE,
                ResourceForm.RAW_STORAGE_BLOCK,
                ResourceForm.STORAGE_BLOCK,
                ResourceForm.INGOT,
                ResourceForm.NUGGET,
                ResourceForm.DUST,
                ResourceForm.PLATE,
                ResourceForm.ROD,
                ResourceForm.GEAR,
                ResourceForm.MOLTEN
        );
    }

    private static @Nullable MaterialToolStats buildToolStats(
            Identifier materialId,
            String displayName,
            int colorHex,
            int meltingPoint,
            float hardness,
            CustomMaterialConfig config,
            Material material
    ) {
        int durability = config.head != null ? config.head.durability : Math.max(100, (int) (hardness * 120));
        float speed = config.head != null ? config.head.miningSpeed : Math.max(2.0f, hardness * 1.5f);
        float damage = config.head != null ? config.head.attackDamage : Math.max(1.0f, hardness * 0.8f);
        var tier = config.head != null ? config.head.harvestTier : (hardness >= 6.0f ? "diamond" : hardness >= 4.0f ? "iron" : "stone");

        var headStats = new MaterialToolStats.HeadStats(durability, speed, damage, tier);

        var handleStats = config.handle != null
                ? new MaterialToolStats.HandleStats(config.handle.durabilityMultiplier, config.handle.miningSpeedMultiplier, config.handle.attackSpeedBonus)
                : new MaterialToolStats.HandleStats(1.0f + (hardness * 0.05f), 1.0f, 0.0f);

        var bindingStats = config.binding != null
                ? new MaterialToolStats.BindingStats(config.binding.bonusDurability)
                : new MaterialToolStats.BindingStats((int) (durability * 0.2f));

        var attachmentStats = config.attachment != null
                ? new MaterialToolStats.AttachmentStats(config.attachment.bonusDurability, config.attachment.attackDamageBonus, config.attachment.speedBonus, Optional.ofNullable(config.attachment.tierOverride))
                : new MaterialToolStats.AttachmentStats((int) (durability * 0.1f), damage * 0.3f, 0.2f, Optional.empty());

        var traits = new ArrayList<MaterialTrait>();
        if (config.traits != null) {
            for (var traitConfig : config.traits) {
                if (traitConfig.id != null && !traitConfig.id.isBlank()) {
                    var traitId = Identifier.parse(traitConfig.id);
                    traits.add(new MaterialTrait(traitId, traitConfig.level, traitConfig.description != null ? traitConfig.description : ""));
                }
            }
        }

        var mainItem = material.getItem(ResourceForm.INGOT);
        if (mainItem == null) {
            mainItem = material.getItem(ResourceForm.GEM);
        }

        if (mainItem == null) {
            mainItem = material.getItem(ResourceForm.RAW_ORE);
        }

        var ingredient = mainItem != null
                ? Optional.of(Ingredient.of(mainItem))
                : Optional.<Ingredient>empty();

        return new MaterialToolStats(
                materialId,
                Optional.of(displayName),
                colorHex,
                Optional.of(meltingPoint),
                ingredient,
                Optional.of(headStats),
                Optional.of(handleStats),
                Optional.of(bindingStats),
                Optional.of(attachmentStats),
                traits
        );
    }

    private static MaterialType parseMaterialType(@Nullable String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            return MaterialType.METALLIC_ORE;
        }

        try {
            return MaterialType.valueOf(typeStr.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return MaterialType.METALLIC_ORE;
        }
    }

    private static TagKey<Block> parseMiningLevel(@Nullable String miningLevel) {
        if (miningLevel == null || miningLevel.isBlank()) {
            return BlockTags.NEEDS_STONE_TOOL;
        }

        return switch (miningLevel.toLowerCase(Locale.ROOT)) {
            case "minecraft:needs_diamond_tool", "diamond" -> BlockTags.NEEDS_DIAMOND_TOOL;
            case "minecraft:needs_iron_tool", "iron" -> BlockTags.NEEDS_IRON_TOOL;
            default -> BlockTags.NEEDS_STONE_TOOL;
        };
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static void createExampleFile(Path configDir) {
        var exampleFile = configDir.resolve("example_material.json.example");
        if (Files.exists(exampleFile)) {
            return;
        }

        var exampleConfig = new CustomMaterialConfig();
        exampleConfig.name = "platinum";
        exampleConfig.displayName = "Platinum";
        exampleConfig.color = "#E5E4E2";
        exampleConfig.type = "metallic_ore";
        exampleConfig.hardness = 4.5f;
        exampleConfig.resistance = 5.0f;
        exampleConfig.miningLevel = "minecraft:needs_iron_tool";
        exampleConfig.meltingPoint = 1768;
        exampleConfig.forms = List.of(
                "ore",
                "deepslate_ore",
                "netherrack_ore",
                "end_stone_ore",
                "raw_ore",
                "raw_storage_block",
                "storage_block",
                "ingot",
                "nugget",
                "dust",
                "plate",
                "rod",
                "gear",
                "molten"
        );
        exampleConfig.oreGeneration = new OreGenConfig(
                true,
                new DimensionOreConfig(true, 6, 5, -48, 32, "triangle"),
                new DimensionOreConfig(false, 6, 4, 10, 115, "uniform"),
                new DimensionOreConfig(false, 4, 3, 10, 70, "uniform")
        );

        exampleConfig.head = new CustomMaterialConfig.HeadConfig();
        exampleConfig.head.durability = 650;
        exampleConfig.head.miningSpeed = 7.0f;
        exampleConfig.head.attackDamage = 2.5f;
        exampleConfig.head.harvestTier = "diamond";

        exampleConfig.handle = new CustomMaterialConfig.HandleConfig();
        exampleConfig.handle.durabilityMultiplier = 1.2f;
        exampleConfig.handle.miningSpeedMultiplier = 1.1f;
        exampleConfig.handle.attackSpeedBonus = 0.1f;

        exampleConfig.binding = new CustomMaterialConfig.BindingConfig();
        exampleConfig.binding.bonusDurability = 90;

        exampleConfig.attachment = new CustomMaterialConfig.AttachmentConfig();
        exampleConfig.attachment.bonusDurability = 80;
        exampleConfig.attachment.attackDamageBonus = 0.6f;
        exampleConfig.attachment.speedBonus = 0.3f;

        var trait = new CustomMaterialConfig.TraitConfig();
        trait.id = "modernmachines:prosperity";
        trait.level = 1;
        trait.description = "Increased fortune and luster";
        exampleConfig.traits = List.of(trait);

        try (var writer = new OutputStreamWriter(Files.newOutputStream(exampleFile), StandardCharsets.UTF_8)) {
            GSON.toJson(exampleConfig, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to write example material file: {}", exampleFile, e);
        }
    }

    public static Map<String, OreGenConfig> getAllOreGenConfigs() {
        return Collections.unmodifiableMap(ORE_GEN_CONFIGS);
    }

    public static @Nullable OreGenConfig getOreGenConfig(String materialName) {
        return ORE_GEN_CONFIGS.get(materialName);
    }

    public static Map<Identifier, MaterialToolStats> getCustomToolStats() {
        return Collections.unmodifiableMap(CUSTOM_TOOL_STATS);
    }

    public static boolean isCustomMaterial(String materialName) {
        return CUSTOM_MATERIAL_NAMES.contains(materialName);
    }

    public static Set<String> getCustomMaterialNames() {
        return Collections.unmodifiableSet(CUSTOM_MATERIAL_NAMES);
    }
}
