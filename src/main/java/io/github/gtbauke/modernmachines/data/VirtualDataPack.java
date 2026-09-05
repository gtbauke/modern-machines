package io.github.gtbauke.modernmachines.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.config.material.CustomMaterialConfig;
import io.github.gtbauke.modernmachines.config.material.CustomMaterialLoader;
import io.github.gtbauke.modernmachines.config.material.DimensionOreConfig;
import io.github.gtbauke.modernmachines.config.material.LargeOreVeinConfig;
import io.github.gtbauke.modernmachines.config.material.OreGenConfig;
import io.github.gtbauke.modernmachines.config.material.OreGenRule;
import io.github.gtbauke.modernmachines.config.material.OreTargetConfig;
import io.github.gtbauke.modernmachines.config.reservoir.ReservoirConfig;
import io.github.gtbauke.modernmachines.config.reservoir.ReservoirLoader;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

public class VirtualDataPack {
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualDataPack.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static @Nullable Pack createDataPack() {
        var packResources = new VirtualPackResources(
                "modernmachines_virtual_data",
                "Modern Machines Virtual Data Pack",
                PackType.SERVER_DATA,
                PackSource.BUILT_IN
        );

        populateDataResources(packResources);

        var selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);
        return Pack.readMetaAndCreate(
                packResources.location(),
                new Pack.ResourcesSupplier() {
                    @Override
                    public @NonNull PackResources openPrimary(@NonNull PackLocationInfo loc) {
                        return packResources;
                    }

                    @Override
                    public @NonNull PackResources openFull(@NonNull PackLocationInfo loc, Pack.@NonNull Metadata metadata) {
                        return packResources;
                    }
                },
                PackType.SERVER_DATA,
                selectionConfig
        );
    }

    private static void populateDataResources(VirtualPackResources pack) {
        var oreConfigs = CustomMaterialLoader.getAllOreGenConfigs();
        var pickaxeMineableBlocks = new ArrayList<String>();
        var needsStoneBlocks = new ArrayList<String>();
        var needsIronBlocks = new ArrayList<String>();
        var needsDiamondBlocks = new ArrayList<String>();
        var allOreBlocks = new ArrayList<String>();
        var allRawMaterials = new ArrayList<String>();
        var allIngots = new ArrayList<String>();
        var allPlates = new ArrayList<String>();
        var allRods = new ArrayList<String>();
        var allGears = new ArrayList<String>();
        var allScrews = new ArrayList<String>();
        var allWires = new ArrayList<String>();
        var allDusts = new ArrayList<String>();
        var allNuggets = new ArrayList<String>();
        var allStorageBlocks = new ArrayList<String>();

        int totalRulesGenerated = 0;
        for (var entry : oreConfigs.entrySet()) {
            var name = entry.getKey();
            var oreGen = entry.getValue();
            var material = ModMaterials.getByName(name);

            if (material == null || !oreGen.enabled()) {
                continue;
            }

            var rules = oreGen.getResolvedRules(
                    material.hardness(),
                    material.hasForm(ResourceForm.ORE),
                    material.hasForm(ResourceForm.DEEPSLATE_ORE),
                    material.hasForm(ResourceForm.NETHERRACK_ORE),
                    material.hasForm(ResourceForm.END_STONE_ORE)
            );

            for (int i = 0; i < rules.size(); i++) {
                var rule = rules.get(i);
                if (addOreRuleWorldgen(pack, material, rule, i)) {
                    totalRulesGenerated++;
                }
            }

            var largeVeins = oreGen.getResolvedLargeVeins();
            for (int i = 0; i < largeVeins.size(); i++) {
                var vein = largeVeins.get(i);
                if (addLargeOreVeinWorldgen(pack, material, vein, i)) {
                    totalRulesGenerated++;
                }
            }
        }

        LOGGER.info("Registered virtual ore worldgen: {} rules/veins generated across materials", totalRulesGenerated);

        int totalReservoirsGenerated = 0;
        for (var reservoir : ReservoirLoader.getAllReservoirs().values()) {
            if (addReservoirWorldgen(pack, reservoir)) {
                totalReservoirsGenerated++;
            }
        }

        LOGGER.info("Registered virtual reservoir worldgen: {} reservoirs generated", totalReservoirsGenerated);

        for (var materialName : CustomMaterialLoader.getCustomMaterialNames()) {
            var material = ModMaterials.getByName(materialName);
            if (material == null) {
                continue;
            }

            addGameplayResources(
                    pack,
                    material,
                    pickaxeMineableBlocks,
                    needsStoneBlocks,
                    needsIronBlocks,
                    needsDiamondBlocks,
                    allOreBlocks,
                    allRawMaterials,
                    allIngots,
                    allPlates,
                    allRods,
                    allGears,
                    allScrews,
                    allWires,
                    allDusts,
                    allNuggets,
                    allStorageBlocks
            );
        }

        for (var entry : CustomMaterialLoader.getAllCustomConfigs().entrySet()) {
            var name = entry.getKey();
            var config = entry.getValue();

            if (CustomMaterialLoader.getCustomMaterialNames().contains(name)) {
                continue;
            }

            var material = ModMaterials.getByName(name);
            if (material != null && config.alloyRecipe != null && !config.alloyRecipe.inputs.isEmpty()) {
                addAlloySmeltingRecipe(pack, material, config.alloyRecipe);
            }
        }

        addTagResources(
                pack,
                pickaxeMineableBlocks,
                needsStoneBlocks,
                needsIronBlocks,
                needsDiamondBlocks,
                allOreBlocks,
                allRawMaterials,
                allIngots,
                allPlates,
                allRods,
                allGears,
                allScrews,
                allWires,
                allDusts,
                allNuggets,
                allStorageBlocks
        );
    }

    private static boolean addOreRuleWorldgen(
            VirtualPackResources pack,
            Material material,
            OreGenRule rule,
            int ruleIndex
    ) {
        if (!rule.enabled()) {
            return false;
        }

        if (rule.requiredMod() != null && !rule.requiredMod().isBlank()) {
            var loader = FMLLoader.getCurrentOrNull();
            if (loader != null && loader.getLoadingModList().getModFileById(rule.requiredMod().trim()) == null) {
                LOGGER.debug("Skipping ore rule {} for material {} because required mod '{}' is not loaded",
                        ruleIndex, material.name(), rule.requiredMod());
                return false;
            }
        }

        var targets = resolveRuleTargets(material, rule);
        if (targets.isEmpty()) {
            return false;
        }

        var name = material.name();
        var featureSuffix = name + "_rule_" + ruleIndex;

        var configuredFeature = Map.of(
                "type", "minecraft:ore",
                "config", Map.of(
                        "size", rule.veinSize(),
                        "discard_chance_on_air_exposure", rule.discardChanceOnAirExposure(),
                        "targets", targets
                )
        );

        var cfgId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/configured_feature/ore_" + featureSuffix + ".json");
        pack.addResource(cfgId, GSON.toJson(configuredFeature));

        var placement = new ArrayList<Object>();

        if (rule.rarity() > 0) {
            placement.add(Map.of("type", "minecraft:rarity_filter", "chance", rule.rarity()));
        } else {
            placement.add(Map.of("type", "minecraft:count", "count", rule.veinsPerChunk()));
        }

        placement.add(Map.of("type", "minecraft:in_square"));

        placement.add(Map.of(
                "type", "minecraft:height_range",
                "height", Map.of(
                        "type", "uniform".equalsIgnoreCase(rule.distribution()) ? "minecraft:uniform" : "minecraft:trapezoid",
                        "min_inclusive", Map.of("absolute", rule.minY()),
                        "max_inclusive", Map.of("absolute", rule.maxY())
                )
        ));

        if (!rule.dimensions().isEmpty() || !rule.dimensionBlacklist().isEmpty()) {
            placement.add(Map.of(
                    "type", ModernMachines.MOD_ID + ":dimension_filter",
                    "allowed", rule.dimensions(),
                    "denied", rule.dimensionBlacklist()
            ));
        }

        if (rule.adjacentToBlock() != null && !rule.adjacentToBlock().isBlank()) {
            placement.add(Map.of(
                    "type", ModernMachines.MOD_ID + ":adjacent_block",
                    "block", rule.adjacentToBlock().trim()
            ));
        }

        placement.add(Map.of("type", "minecraft:biome"));

        var placedFeature = Map.of(
                "feature", ModernMachines.MOD_ID + ":ore_" + featureSuffix,
                "placement", placement
        );

        var placedId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/placed_feature/ore_" + featureSuffix + "_placed.json");
        pack.addResource(placedId, GSON.toJson(placedFeature));

        var biomeSelector = resolveBiomeSelector(rule);
        var biomeModifier = Map.of(
                "type", "neoforge:add_features",
                "biomes", biomeSelector,
                "features", List.of(ModernMachines.MOD_ID + ":ore_" + featureSuffix + "_placed"),
                "step", "underground_ores"
        );

        var modifierId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "neoforge/biome_modifier/add_ore_" + featureSuffix + ".json");
        pack.addResource(modifierId, GSON.toJson(biomeModifier));

        return true;
    }

    private static boolean addLargeOreVeinWorldgen(
            VirtualPackResources pack,
            Material material,
            LargeOreVeinConfig vein,
            int veinIndex
    ) {
        if (!vein.enabled()) {
            return false;
        }

        var name = material.name();
        var featureSuffix = name + "_" + veinIndex;

        var primaryOreBlock = material.hasForm(ResourceForm.ORE)
                ? BuiltInRegistries.BLOCK.getKey(material.getBlock(ResourceForm.ORE)).toString()
                : (material.hasForm(ResourceForm.DEEPSLATE_ORE)
                        ? BuiltInRegistries.BLOCK.getKey(material.getBlock(ResourceForm.DEEPSLATE_ORE)).toString()
                        : "minecraft:" + name + "_ore");

        var configMap = new LinkedHashMap<String, Object>();
        configMap.put("min_y", vein.minY());
        configMap.put("max_y", vein.maxY());
        configMap.put("filler_block", blockStateJson(vein.fillerBlock()));
        configMap.put("primary_ore", blockStateJson(primaryOreBlock));

        if (material.hasForm(ResourceForm.DEEPSLATE_ORE)) {
            var deepslateId = BuiltInRegistries.BLOCK.getKey(material.getBlock(ResourceForm.DEEPSLATE_ORE)).toString();
            configMap.put("primary_deepslate_ore", blockStateJson(deepslateId));
        }

        configMap.put("primary_ore_chance", vein.primaryOreChance());

        if (vein.rawBlockChance() > 0.0f) {
            if (material.hasForm(ResourceForm.RAW_STORAGE_BLOCK)) {
                var rawBlockId = BuiltInRegistries.BLOCK.getKey(material.getBlock(ResourceForm.RAW_STORAGE_BLOCK)).toString();
                configMap.put("raw_block", blockStateJson(rawBlockId));
            } else {
                configMap.put("raw_block", blockStateJson("minecraft:raw_" + name + "_block"));
            }
        }

        configMap.put("raw_block_chance", vein.rawBlockChance());

        if (vein.secondaryMaterial() != null && !vein.secondaryMaterial().isBlank() && vein.secondaryOreChance() > 0.0f) {
            var secName = vein.secondaryMaterial().trim();
            var secMat = ModMaterials.getByName(secName);
            if (secMat != null) {
                if (secMat.hasForm(ResourceForm.ORE)) {
                    var secOreId = BuiltInRegistries.BLOCK.getKey(secMat.getBlock(ResourceForm.ORE)).toString();
                    configMap.put("secondary_ore", blockStateJson(secOreId));
                } else if (secMat.hasForm(ResourceForm.DEEPSLATE_ORE)) {
                    var secOreId = BuiltInRegistries.BLOCK.getKey(secMat.getBlock(ResourceForm.DEEPSLATE_ORE)).toString();
                    configMap.put("secondary_ore", blockStateJson(secOreId));
                }

                if (secMat.hasForm(ResourceForm.DEEPSLATE_ORE)) {
                    var secDeepId = BuiltInRegistries.BLOCK.getKey(secMat.getBlock(ResourceForm.DEEPSLATE_ORE)).toString();
                    configMap.put("secondary_deepslate_ore", blockStateJson(secDeepId));
                }
            } else {
                configMap.put("secondary_ore", blockStateJson("minecraft:" + secName + "_ore"));
                configMap.put("secondary_deepslate_ore", blockStateJson("minecraft:deepslate_" + secName + "_ore"));
            }
        }

        configMap.put("secondary_ore_chance", vein.secondaryOreChance());

        if (vein.surfaceIndicators().enabled() && vein.surfaceIndicators().chance() > 0.0f) {
            var indBlock = vein.surfaceIndicators().block();
            if (indBlock != null && !indBlock.isBlank()) {
                configMap.put("surface_indicator_block", blockStateJson(indBlock.trim()));
            } else {
                configMap.put("surface_indicator_block", blockStateJson(primaryOreBlock));
            }

            configMap.put("surface_indicator_chance", vein.surfaceIndicators().chance());
        } else {
            configMap.put("surface_indicator_chance", 0.0f);
        }

        var noise = vein.noise();
        if (noise != null) {
            configMap.put("noise_length", noise.length());
            configMap.put("noise_thickness", noise.thickness());
            configMap.put("noise_density", noise.density());
        }

        var configuredFeature = Map.of(
                "type", ModernMachines.MOD_ID + ":large_ore_vein",
                "config", configMap
        );

        var cfgId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/configured_feature/large_ore_vein_" + featureSuffix + ".json");
        pack.addResource(cfgId, GSON.toJson(configuredFeature));

        var placement = new ArrayList<Object>();

        if (vein.rarity() > 0) {
            placement.add(Map.of("type", "minecraft:rarity_filter", "chance", vein.rarity()));
        }

        placement.add(Map.of("type", "minecraft:in_square"));

        placement.add(Map.of(
                "type", "minecraft:height_range",
                "height", Map.of(
                        "type", "minecraft:uniform",
                        "min_inclusive", Map.of("absolute", vein.minY()),
                        "max_inclusive", Map.of("absolute", vein.maxY())
                )
        ));

        if (!vein.dimensions().isEmpty() || !vein.dimensionBlacklist().isEmpty()) {
            placement.add(Map.of(
                    "type", ModernMachines.MOD_ID + ":dimension_filter",
                    "allowed", vein.dimensions(),
                    "denied", vein.dimensionBlacklist()
            ));
        }

        placement.add(Map.of("type", "minecraft:biome"));

        var placedFeature = Map.of(
                "feature", ModernMachines.MOD_ID + ":large_ore_vein_" + featureSuffix,
                "placement", placement
        );

        var placedId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/placed_feature/large_ore_vein_" + featureSuffix + "_placed.json");
        pack.addResource(placedId, GSON.toJson(placedFeature));

        var biomeSelector = resolveVeinBiomeSelector(vein);
        var biomeModifier = Map.of(
                "type", "neoforge:add_features",
                "biomes", biomeSelector,
                "features", List.of(ModernMachines.MOD_ID + ":large_ore_vein_" + featureSuffix + "_placed"),
                "step", "underground_ores"
        );

        var modifierId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "neoforge/biome_modifier/add_large_ore_vein_" + featureSuffix + ".json");
        pack.addResource(modifierId, GSON.toJson(biomeModifier));

        return true;
    }

    private static @NonNull Map<String, String> blockStateJson(String blockId) {
        var id = blockId.contains(":") ? blockId : "minecraft:" + blockId;
        return Map.of("Name", id);
    }

    private static @NonNull Object resolveVeinBiomeSelector(LargeOreVeinConfig vein) {
        if (!vein.biomeBlacklist().isEmpty()) {
            var positiveBiomes = extractPositiveVeinBiomes(vein);

            return Map.of(
                    "type", "neoforge:and",
                    "values", List.of(
                            positiveBiomes,
                            Map.of(
                                    "type", "neoforge:none",
                                    "values", vein.biomeBlacklist()
                            )
                    )
            );
        }

        return extractPositiveVeinBiomes(vein);
    }

    private static @NonNull Object extractPositiveVeinBiomes(LargeOreVeinConfig vein) {
        var values = new ArrayList<String>();
        values.addAll(vein.biomeTags());
        values.addAll(vein.biomes());

        if (values.isEmpty()) {
            if (vein.dimensions().contains("minecraft:the_nether")) {
                return "#minecraft:is_nether";
            }

            if (vein.dimensions().contains("minecraft:the_end")) {
                return "#minecraft:is_end";
            }

            return "#minecraft:is_overworld";
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        return values;
    }

    private static boolean addReservoirWorldgen(
            VirtualPackResources pack,
            ReservoirConfig reservoir
    ) {
        if (!reservoir.enabled()) {
            return false;
        }

        var name = reservoir.name() != null ? reservoir.name() : "reservoir";
        var featureSuffix = name.toLowerCase(Locale.ROOT);

        var configMap = new LinkedHashMap<String, Object>();
        configMap.put("reservoir_type", name);
        configMap.put("fluid_id", reservoir.fluid());

        var fluidBlock = "minecraft:water";
        if (reservoir.fluid().contains("lava")) {
            fluidBlock = "minecraft:lava";
        }

        configMap.put("fluid_block", blockStateJson(fluidBlock));
        configMap.put("barrier_block", blockStateJson(reservoir.barrierBlock()));

        if (reservoir.capstoneBlock() != null && !reservoir.capstoneBlock().isBlank()) {
            configMap.put("capstone_block", blockStateJson(reservoir.capstoneBlock().trim()));
        }

        configMap.put("min_y", reservoir.minY());
        configMap.put("max_y", reservoir.maxY());
        configMap.put("radius_xz", reservoir.radiusXz());
        configMap.put("radius_y", reservoir.radiusY());
        configMap.put("initial_pressure", reservoir.initialPressure());
        configMap.put("total_volume", reservoir.capacity());

        var configuredFeature = Map.of(
                "type", ModernMachines.MOD_ID + ":subsurface_reservoir",
                "config", configMap
        );

        var cfgId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/configured_feature/reservoir_" + featureSuffix + ".json");
        pack.addResource(cfgId, GSON.toJson(configuredFeature));

        var placement = new ArrayList<Object>();

        if (reservoir.rarity() > 0) {
            placement.add(Map.of("type", "minecraft:rarity_filter", "chance", reservoir.rarity()));
        }

        placement.add(Map.of("type", "minecraft:in_square"));

        placement.add(Map.of(
                "type", "minecraft:height_range",
                "height", Map.of(
                        "type", "minecraft:uniform",
                        "min_inclusive", Map.of("absolute", reservoir.minY()),
                        "max_inclusive", Map.of("absolute", reservoir.maxY())
                )
        ));

        if (!reservoir.dimensions().isEmpty() || !reservoir.dimensionBlacklist().isEmpty()) {
            placement.add(Map.of(
                    "type", ModernMachines.MOD_ID + ":dimension_filter",
                    "allowed", reservoir.dimensions(),
                    "denied", reservoir.dimensionBlacklist()
            ));
        }

        placement.add(Map.of("type", "minecraft:biome"));

        var placedFeature = Map.of(
                "feature", ModernMachines.MOD_ID + ":reservoir_" + featureSuffix,
                "placement", placement
        );

        var placedId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/placed_feature/reservoir_" + featureSuffix + "_placed.json");
        pack.addResource(placedId, GSON.toJson(placedFeature));

        var biomeSelector = resolveReservoirBiomeSelector(reservoir);
        var biomeModifier = Map.of(
                "type", "neoforge:add_features",
                "biomes", biomeSelector,
                "features", List.of(ModernMachines.MOD_ID + ":reservoir_" + featureSuffix + "_placed"),
                "step", "underground_ores"
        );

        var modifierId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "neoforge/biome_modifier/add_reservoir_" + featureSuffix + ".json");
        pack.addResource(modifierId, GSON.toJson(biomeModifier));

        return true;
    }

    private static @NonNull Object resolveReservoirBiomeSelector(ReservoirConfig reservoir) {
        if (!reservoir.biomeBlacklist().isEmpty()) {
            var positiveBiomes = extractPositiveReservoirBiomes(reservoir);

            return Map.of(
                    "type", "neoforge:and",
                    "values", List.of(
                            positiveBiomes,
                            Map.of(
                                    "type", "neoforge:none",
                                    "values", reservoir.biomeBlacklist()
                            )
                    )
            );
        }

        return extractPositiveReservoirBiomes(reservoir);
    }

    private static @NonNull Object extractPositiveReservoirBiomes(ReservoirConfig reservoir) {
        var values = new ArrayList<String>();
        values.addAll(reservoir.biomeTags());
        values.addAll(reservoir.biomes());

        if (values.isEmpty()) {
            if (reservoir.dimensions().contains("minecraft:the_nether")) {
                return "#minecraft:is_nether";
            }

            if (reservoir.dimensions().contains("minecraft:the_end")) {
                return "#minecraft:is_end";
            }

            return "#minecraft:is_overworld";
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        return values;
    }

    private static @NonNull List<Object> resolveRuleTargets(Material material, OreGenRule rule) {
        var targets = new ArrayList<>();

        if (rule.targets() != null && !rule.targets().isEmpty()) {
            for (var targetConfig : rule.targets()) {
                var predicateType = targetConfig.targetType();
                if (!predicateType.contains(":")) {
                    predicateType = "minecraft:" + predicateType;
                }

                Map<String, Object> predicate;
                if (predicateType.equals("minecraft:block_match") || predicateType.equals("minecraft:blockstate_match")) {
                    predicate = Map.of(
                            "predicate_type", predicateType,
                            "block", targetConfig.target()
                    );
                } else {
                    predicate = Map.of(
                            "predicate_type", predicateType,
                            "tag", targetConfig.target()
                    );
                }

                String stateBlockId;
                if (targetConfig.state() != null && !targetConfig.state().isBlank()) {
                    stateBlockId = targetConfig.state().trim();
                } else {
                    var form = parseOreForm(targetConfig.oreForm());
                    if (form != null && material.hasForm(form)) {
                        stateBlockId = getBlockId(material, form, material.name());
                    } else {
                        stateBlockId = getFirstAvailableOreBlockId(material);
                    }
                }

                if (stateBlockId != null) {
                    targets.add(Map.of(
                            "target", predicate,
                            "state", Map.of("Name", stateBlockId)
                    ));
                }
            }
        }

        if (targets.isEmpty()) {
            var defaultTargets = getObjects(material, material.name());
            targets.addAll(defaultTargets);
        }

        return targets;
    }

    private static @NonNull Object resolveBiomeSelector(OreGenRule rule) {
        if (!rule.biomeBlacklist().isEmpty()) {
            var positiveBiomes = extractPositiveBiomes(rule);

            return Map.of(
                    "type", "neoforge:and",
                    "values", List.of(
                            positiveBiomes,
                            Map.of(
                                    "type", "neoforge:none",
                                    "values", rule.biomeBlacklist()
                            )
                    )
            );
        }

        return extractPositiveBiomes(rule);
    }

    private static @NonNull Object extractPositiveBiomes(OreGenRule rule) {
        var values = new ArrayList<String>();
        values.addAll(rule.biomeTags());
        values.addAll(rule.biomes());

        if (values.isEmpty()) {
            if (rule.dimensions().contains("minecraft:the_nether")) {
                return "#minecraft:is_nether";
            }

            if (rule.dimensions().contains("minecraft:the_end")) {
                return "#minecraft:is_end";
            }

            return "#minecraft:is_overworld";
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        return values;
    }

    private static @Nullable ResourceForm parseOreForm(@Nullable String formStr) {
        if (formStr == null || formStr.isBlank()) {
            return null;
        }

        var normalized = formStr.trim().toUpperCase(Locale.ROOT).replace(" ", "_");
        try {
            return ResourceForm.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            if (normalized.equals("STONE") || normalized.equals("STONE_ORE")) {
                return ResourceForm.ORE;
            }

            if (normalized.equals("DEEPSLATE")) {
                return ResourceForm.DEEPSLATE_ORE;
            }

            if (normalized.equals("NETHERRACK") || normalized.equals("NETHER")) {
                return ResourceForm.NETHERRACK_ORE;
            }

            if (normalized.equals("END") || normalized.equals("END_STONE")) {
                return ResourceForm.END_STONE_ORE;
            }

            return null;
        }
    }

    private static @Nullable String getFirstAvailableOreBlockId(Material material) {
        if (material.hasForm(ResourceForm.ORE)) {
            return getBlockId(material, ResourceForm.ORE, material.name());
        }

        if (material.hasForm(ResourceForm.DEEPSLATE_ORE)) {
            return getBlockId(material, ResourceForm.DEEPSLATE_ORE, material.name());
        }

        if (material.hasForm(ResourceForm.NETHERRACK_ORE)) {
            return getBlockId(material, ResourceForm.NETHERRACK_ORE, material.name());
        }

        if (material.hasForm(ResourceForm.END_STONE_ORE)) {
            return getBlockId(material, ResourceForm.END_STONE_ORE, material.name());
        }

        return null;
    }

    private static @NonNull List<Object> getObjects(Material material, String name) {
        var targets = new ArrayList<>();

        if (material.hasForm(ResourceForm.ORE)) {
            var oreBlockId = getBlockId(material, ResourceForm.ORE, name);
            var stoneTarget = Map.of(
                    "target", Map.of(
                            "predicate_type", "minecraft:tag_match",
                            "tag", "minecraft:stone_ore_replaceables"
                    ),
                    "state", Map.of(
                            "Name", oreBlockId
                    )
            );
            targets.add(stoneTarget);
        }

        if (material.hasForm(ResourceForm.DEEPSLATE_ORE)) {
            var deepslateBlockId = getBlockId(material, ResourceForm.DEEPSLATE_ORE, name);
            var deepslateTarget = Map.of(
                    "target", Map.of(
                            "predicate_type", "minecraft:tag_match",
                            "tag", "minecraft:deepslate_ore_replaceables"
                    ),
                    "state", Map.of(
                            "Name", deepslateBlockId
                    )
            );
            targets.add(deepslateTarget);
        }

        return targets;
    }

    private static String getBlockId(Material material, ResourceForm form, String name) {
        var deferred = material.getDeferredBlock(form);
        if (deferred != null) {
            return deferred.getId().toString();
        }

        var block = material.getBlock(form);
        if (block != null) {
            return BuiltInRegistries.BLOCK.getKey(block).toString();
        }

        return ModernMachines.MOD_ID + ":" + form.getRegistryName(name);
    }

    private static void addGameplayResources(
            VirtualPackResources pack,
            Material material,
            List<String> pickaxeMineableBlocks,
            List<String> needsStoneBlocks,
            List<String> needsIronBlocks,
            List<String> needsDiamondBlocks,
            List<String> allOreBlocks,
            List<String> allRawMaterials,
            List<String> allIngots,
            List<String> allPlates,
            List<String> allRods,
            List<String> allGears,
            List<String> allScrews,
            List<String> allWires,
            List<String> allDusts,
            List<String> allNuggets,
            List<String> allStorageBlocks
    ) {
        var name = material.name();
        var rawItem = material.hasForm(ResourceForm.RAW_ORE) ? ModernMachines.MOD_ID + ":" + ResourceForm.RAW_ORE.getRegistryName(name) : null;
        var ingotItem = material.hasForm(ResourceForm.INGOT) ? ModernMachines.MOD_ID + ":" + ResourceForm.INGOT.getRegistryName(name) : null;
        var nuggetItem = material.hasForm(ResourceForm.NUGGET) ? ModernMachines.MOD_ID + ":" + ResourceForm.NUGGET.getRegistryName(name) : null;
        var storageBlock = material.hasForm(ResourceForm.STORAGE_BLOCK) ? ModernMachines.MOD_ID + ":" + ResourceForm.STORAGE_BLOCK.getRegistryName(name) : null;
        var rawStorageBlock = material.hasForm(ResourceForm.RAW_STORAGE_BLOCK) ? ModernMachines.MOD_ID + ":" + ResourceForm.RAW_STORAGE_BLOCK.getRegistryName(name) : null;
        var plateItem = material.hasForm(ResourceForm.PLATE) ? ModernMachines.MOD_ID + ":" + ResourceForm.PLATE.getRegistryName(name) : null;
        var rodItem = material.hasForm(ResourceForm.ROD) ? ModernMachines.MOD_ID + ":" + ResourceForm.ROD.getRegistryName(name) : null;
        var gearItem = material.hasForm(ResourceForm.GEAR) ? ModernMachines.MOD_ID + ":" + ResourceForm.GEAR.getRegistryName(name) : null;
        var screwItem = material.hasForm(ResourceForm.SCREW) ? ModernMachines.MOD_ID + ":" + ResourceForm.SCREW.getRegistryName(name) : null;
        var wireItem = material.hasForm(ResourceForm.WIRE) ? ModernMachines.MOD_ID + ":" + ResourceForm.WIRE.getRegistryName(name) : null;
        var dustItem = material.hasForm(ResourceForm.DUST) ? ModernMachines.MOD_ID + ":" + ResourceForm.DUST.getRegistryName(name) : null;

        if (rawItem != null) {
            allRawMaterials.add(rawItem);
        }

        if (ingotItem != null) {
            allIngots.add(ingotItem);
            addTag(pack, "c", "tags/item/ingots/" + name + ".json", ingotItem);
        }

        if (nuggetItem != null) {
            allNuggets.add(nuggetItem);
            addTag(pack, "c", "tags/item/nuggets/" + name + ".json", nuggetItem);
        }

        if (plateItem != null) {
            allPlates.add(plateItem);
            addTag(pack, "c", "tags/item/plates/" + name + ".json", plateItem);
        }

        if (rodItem != null) {
            allRods.add(rodItem);
            addTag(pack, "c", "tags/item/rods/" + name + ".json", rodItem);
        }

        if (gearItem != null) {
            allGears.add(gearItem);
            addTag(pack, "c", "tags/item/gears/" + name + ".json", gearItem);
        }

        if (screwItem != null) {
            allScrews.add(screwItem);
            addTag(pack, "c", "tags/item/screws/" + name + ".json", screwItem);
        }

        if (wireItem != null) {
            allWires.add(wireItem);
            addTag(pack, "c", "tags/item/wires/" + name + ".json", wireItem);
        }

        if (dustItem != null) {
            allDusts.add(dustItem);
            addTag(pack, "c", "tags/item/dusts/" + name + ".json", dustItem);
        }

        var oreForms = List.of(
                ResourceForm.ORE,
                ResourceForm.DEEPSLATE_ORE,
                ResourceForm.NETHERRACK_ORE,
                ResourceForm.END_STONE_ORE
        );

        var dropItemId = rawItem != null ? rawItem : ingotItem;

        for (var form : oreForms) {
            if (material.hasForm(form)) {
                var blockName = form.getRegistryName(name);
                var fullBlockId = ModernMachines.MOD_ID + ":" + blockName;
                pickaxeMineableBlocks.add(fullBlockId);
                allOreBlocks.add(fullBlockId);
                assignMiningLevel(material, fullBlockId, needsStoneBlocks, needsIronBlocks, needsDiamondBlocks);

                if (dropItemId != null) {
                    addOreLootTable(pack, blockName, fullBlockId, dropItemId);
                } else {
                    addSelfDropLootTable(pack, blockName, fullBlockId);
                }
            }
        }

        if (storageBlock != null) {
            var blockName = ResourceForm.STORAGE_BLOCK.getRegistryName(name);
            pickaxeMineableBlocks.add(storageBlock);
            allStorageBlocks.add(storageBlock);
            assignMiningLevel(material, storageBlock, needsStoneBlocks, needsIronBlocks, needsDiamondBlocks);
            addSelfDropLootTable(pack, blockName, storageBlock);
            addTag(pack, "c", "tags/block/storage_blocks/" + name + ".json", storageBlock);
            addTag(pack, "c", "tags/item/storage_blocks/" + name + ".json", storageBlock);
        }

        if (rawStorageBlock != null) {
            var blockName = ResourceForm.RAW_STORAGE_BLOCK.getRegistryName(name);
            pickaxeMineableBlocks.add(rawStorageBlock);
            assignMiningLevel(material, rawStorageBlock, needsStoneBlocks, needsIronBlocks, needsDiamondBlocks);
            addSelfDropLootTable(pack, blockName, rawStorageBlock);
        }

        addCraftingAndSmeltingRecipes(pack, name, rawItem, ingotItem, nuggetItem, storageBlock, rawStorageBlock, material.smeltingXp());
        addComponentRecipes(pack, name, ingotItem, plateItem, rodItem, gearItem, screwItem, wireItem, dustItem);

        var customConfig = CustomMaterialLoader.getCustomConfig(name);
        if (customConfig != null && customConfig.alloyRecipe != null && !customConfig.alloyRecipe.inputs.isEmpty()) {
            addAlloySmeltingRecipe(pack, material, customConfig.alloyRecipe);
        }
    }

    private static void addAlloySmeltingRecipe(
            VirtualPackResources pack,
            Material material,
            CustomMaterialConfig.AlloyRecipeConfig recipeConfig
    ) {
        var name = material.name();
        var inputs = new ArrayList<Map<String, Object>>();

        for (var input : recipeConfig.inputs) {
            inputs.add(Map.of(
                    "count", input.count,
                    "ingredient", input.ingredient
            ));
        }

        var resultCount = recipeConfig.resultCount > 0 ? recipeConfig.resultCount : 1;
        var recipeJson = Map.of(
                "type", ModernMachines.MOD_ID + ":alloy_smelting",
                "energy", recipeConfig.energy > 0 ? recipeConfig.energy : 3000,
                "cooking_time", recipeConfig.cookingTime > 0 ? recipeConfig.cookingTime : 200,
                "experience", recipeConfig.experience,
                "inputs", inputs,
                "result", Map.of(
                        "count", resultCount,
                        "id", ModernMachines.MOD_ID + ":" + ResourceForm.INGOT.getRegistryName(name)
                )
        );

        var recipeId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/alloy_" + name + ".json");
        pack.addResource(recipeId, GSON.toJson(recipeJson));
    }

    private static void assignMiningLevel(
            Material material,
            String blockId,
            List<String> needsStone,
            List<String> needsIron,
            List<String> needsDiamond
    ) {
        if (material.hardness() >= 6.0f) {
            needsDiamond.add(blockId);
            return;
        }

        if (material.hardness() >= 4.0f) {
            needsIron.add(blockId);
            return;
        }

        needsStone.add(blockId);
    }

    private static void addSelfDropLootTable(VirtualPackResources pack, String blockName, String blockId) {
        var lootTable = Map.of(
                "type", "minecraft:block",
                "pools", List.of(
                        Map.of(
                                "rolls", 1.0f,
                                "bonus_rolls", 0.0f,
                                "entries", List.of(
                                        Map.of("type", "minecraft:item", "name", blockId)
                                )
                        )
                )
        );

        var id = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "loot_table/blocks/" + blockName + ".json");
        pack.addResource(id, GSON.toJson(lootTable));
    }

    private static void addOreLootTable(VirtualPackResources pack, String blockName, String blockId, String dropItemId) {
        var lootTable = Map.of(
                "type", "minecraft:block",
                "pools", List.of(
                        Map.of(
                                "rolls", 1.0f,
                                "bonus_rolls", 0.0f,
                                "entries", List.of(
                                        Map.of(
                                                "type", "minecraft:alternatives",
                                                "children", List.of(
                                                        Map.of(
                                                                "type", "minecraft:item",
                                                                "name", blockId,
                                                                "conditions", List.of(
                                                                        Map.of(
                                                                                "condition", "minecraft:match_tool",
                                                                                "predicate", Map.of(
                                                                                        "predicates", Map.of(
                                                                                                "minecraft:enchantments", List.of(
                                                                                                        Map.of(
                                                                                                                "enchantments", "minecraft:silk_touch",
                                                                                                                "levels", Map.of("min", 1)
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        ),
                                                        Map.of(
                                                                "type", "minecraft:item",
                                                                "name", dropItemId,
                                                                "functions", List.of(
                                                                        Map.of(
                                                                                "function", "minecraft:apply_bonus",
                                                                                "enchantment", "minecraft:fortune",
                                                                                "formula", "minecraft:ore_drops"
                                                                        ),
                                                                        Map.of("function", "minecraft:explosion_decay")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        var id = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "loot_table/blocks/" + blockName + ".json");
        pack.addResource(id, GSON.toJson(lootTable));
    }

    private static void addCraftingAndSmeltingRecipes(
            VirtualPackResources pack,
            String name,
            @Nullable String rawItem,
            @Nullable String ingotItem,
            @Nullable String nuggetItem,
            @Nullable String storageBlock,
            @Nullable String rawStorageBlock,
            float xp
    ) {
        if (rawItem != null && ingotItem != null) {
            var smeltingRecipe = Map.of(
                    "type", "minecraft:smelting",
                    "category", "misc",
                    "ingredient", rawItem,
                    "result", Map.of("id", ingotItem),
                    "experience", xp,
                    "cookingtime", 200
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/smelting/" + name + "_ingot_from_raw.json"),
                    GSON.toJson(smeltingRecipe)
            );

            var blastingRecipe = Map.of(
                    "type", "minecraft:blasting",
                    "category", "misc",
                    "ingredient", rawItem,
                    "result", Map.of("id", ingotItem),
                    "experience", xp,
                    "cookingtime", 100
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/blasting/" + name + "_ingot_from_raw.json"),
                    GSON.toJson(blastingRecipe)
            );
        }

        if (ingotItem != null && storageBlock != null) {
            var blockRecipe = Map.of(
                    "type", "minecraft:crafting_shaped",
                    "category", "building",
                    "pattern", List.of("###", "###", "###"),
                    "key", Map.of("#", ingotItem),
                    "result", Map.of("id", storageBlock)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_block.json"),
                    GSON.toJson(blockRecipe)
            );

            var uncraftRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of(storageBlock),
                    "result", Map.of("id", ingotItem, "count", 9)
            );

            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_ingots_from_block.json"),
                    GSON.toJson(uncraftRecipe)
            );
        }

        if (rawItem != null && rawStorageBlock != null) {
            var rawBlockRecipe = Map.of(
                    "type", "minecraft:crafting_shaped",
                    "category", "building",
                    "pattern", List.of("###", "###", "###"),
                    "key", Map.of("#", rawItem),
                    "result", Map.of("id", rawStorageBlock)
            );

            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/raw_" + name + "_block.json"),
                    GSON.toJson(rawBlockRecipe)
            );

            var uncraftRawRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of(rawStorageBlock),
                    "result", Map.of("id", rawItem, "count", 9)
            );

            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/raw_" + name + "_from_block.json"),
                    GSON.toJson(uncraftRawRecipe)
            );
        }

        if (ingotItem != null && nuggetItem != null) {
            var nuggetsRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of(ingotItem),
                    "result", Map.of("id", nuggetItem, "count", 9)
            );

            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_nuggets.json"),
                    GSON.toJson(nuggetsRecipe)
            );

            var ingotFromNuggets = Map.of(
                    "type", "minecraft:crafting_shaped",
                    "category", "misc",
                    "pattern", List.of("###", "###", "###"),
                    "key", Map.of("#", nuggetItem),
                    "result", Map.of("id", ingotItem)
            );

            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_ingot_from_nuggets.json"),
                    GSON.toJson(ingotFromNuggets)
            );
        }
    }

    private static void addTag(VirtualPackResources pack, String namespace, String path, String itemOrBlockId) {
        var id = Identifier.fromNamespaceAndPath(namespace, path);
        pack.addResource(id, GSON.toJson(Map.of("replace", false, "values", List.of(itemOrBlockId))));
    }

    private static void addComponentRecipes(
            VirtualPackResources pack,
            String name,
            @Nullable String ingotItem,
            @Nullable String plateItem,
            @Nullable String rodItem,
            @Nullable String gearItem,
            @Nullable String screwItem,
            @Nullable String wireItem,
            @Nullable String dustItem
    ) {
        if (plateItem != null && ingotItem != null) {
            var plateRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of("modernmachines:engineers_hammer", ingotItem),
                    "result", Map.of("id", plateItem)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_plate_from_hammer.json"),
                    GSON.toJson(plateRecipe)
            );
        }

        if (dustItem != null && ingotItem != null) {
            var dustRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of("modernmachines:engineers_hammer", ingotItem),
                    "result", Map.of("id", dustItem)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_dust_from_hammer.json"),
                    GSON.toJson(dustRecipe)
            );
        }

        if (rodItem != null && ingotItem != null) {
            var rodRecipe = Map.of(
                    "type", "minecraft:crafting_shaped",
                    "category", "misc",
                    "pattern", List.of("I", "I"),
                    "key", Map.of("I", ingotItem),
                    "result", Map.of("id", rodItem, "count", 4)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_rod.json"),
                    GSON.toJson(rodRecipe)
            );
        }

        if (screwItem != null && rodItem != null) {
            var screwRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of("modernmachines:engineers_hammer", rodItem),
                    "result", Map.of("id", screwItem, "count", 4)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_screw_from_hammer.json"),
                    GSON.toJson(screwRecipe)
            );
        }

        if (wireItem != null && plateItem != null) {
            var wireRecipe = Map.of(
                    "type", "minecraft:crafting_shapeless",
                    "category", "misc",
                    "ingredients", List.of("modernmachines:wire_cutter", plateItem),
                    "result", Map.of("id", wireItem, "count", 2)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_wire_from_cutters.json"),
                    GSON.toJson(wireRecipe)
            );
        }

        if (gearItem != null && plateItem != null) {
            var centerIngot = ingotItem != null ? ingotItem : "minecraft:iron_ingot";
            var gearRecipe = Map.of(
                    "type", "minecraft:crafting_shaped",
                    "category", "misc",
                    "pattern", List.of(" P ", "PIP", " P "),
                    "key", Map.of("P", plateItem, "I", centerIngot),
                    "result", Map.of("id", gearItem)
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "recipe/crafting/" + name + "_gear.json"),
                    GSON.toJson(gearRecipe)
            );
        }
    }

    private static void addTagResources(
            VirtualPackResources pack,
            List<String> pickaxeMineableBlocks,
            List<String> needsStoneBlocks,
            List<String> needsIronBlocks,
            List<String> needsDiamondBlocks,
            List<String> allOreBlocks,
            List<String> allRawMaterials,
            List<String> allIngots,
            List<String> allPlates,
            List<String> allRods,
            List<String> allGears,
            List<String> allScrews,
            List<String> allWires,
            List<String> allDusts,
            List<String> allNuggets,
            List<String> allStorageBlocks
    ) {
        addTagIfNotEmpty(pack, "minecraft", "tags/block/mineable/pickaxe.json", pickaxeMineableBlocks);
        addTagIfNotEmpty(pack, "minecraft", "tags/block/needs_stone_tool.json", needsStoneBlocks);
        addTagIfNotEmpty(pack, "minecraft", "tags/block/needs_iron_tool.json", needsIronBlocks);
        addTagIfNotEmpty(pack, "minecraft", "tags/block/needs_diamond_tool.json", needsDiamondBlocks);
        addTagIfNotEmpty(pack, "c", "tags/block/ores.json", allOreBlocks);
        addTagIfNotEmpty(pack, "c", "tags/item/raw_materials.json", allRawMaterials);
        addTagIfNotEmpty(pack, "c", "tags/item/ingots.json", allIngots);
        addTagIfNotEmpty(pack, "c", "tags/item/plates.json", allPlates);
        addTagIfNotEmpty(pack, "c", "tags/item/rods.json", allRods);
        addTagIfNotEmpty(pack, "c", "tags/item/gears.json", allGears);
        addTagIfNotEmpty(pack, "c", "tags/item/screws.json", allScrews);
        addTagIfNotEmpty(pack, "c", "tags/item/wires.json", allWires);
        addTagIfNotEmpty(pack, "c", "tags/item/dusts.json", allDusts);
        addTagIfNotEmpty(pack, "c", "tags/item/nuggets.json", allNuggets);
        addTagIfNotEmpty(pack, "c", "tags/item/storage_blocks.json", allStorageBlocks);
        addTagIfNotEmpty(pack, "c", "tags/block/storage_blocks.json", allStorageBlocks);
        addTagIfNotEmpty(pack, ModernMachines.MOD_ID, "tags/block/large_vein_replaceable.json", List.of(
                "#minecraft:base_stone_overworld",
                "#minecraft:base_stone_nether",
                "#minecraft:stone_ore_replaceables",
                "#minecraft:deepslate_ore_replaceables",
                "minecraft:end_stone",
                "minecraft:netherrack"
        ));
    }

    private static void addTagIfNotEmpty(VirtualPackResources pack, String namespace, String path, List<String> values) {
        if (!values.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath(namespace, path),
                    GSON.toJson(Map.of("replace", false, "values", values))
            );
        }
    }
}
