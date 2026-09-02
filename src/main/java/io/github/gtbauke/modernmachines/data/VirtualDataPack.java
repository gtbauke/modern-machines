package io.github.gtbauke.modernmachines.data;

import java.util.ArrayList;
import java.util.List;
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
import io.github.gtbauke.modernmachines.config.material.CustomMaterialLoader;
import io.github.gtbauke.modernmachines.config.material.DimensionOreConfig;
import io.github.gtbauke.modernmachines.config.material.OreGenConfig;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
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

        var overworldFeatures = new ArrayList<String>();
        var netherFeatures = new ArrayList<String>();
        var endFeatures = new ArrayList<String>();

        for (var entry : oreConfigs.entrySet()) {
            var name = entry.getKey();
            var oreGen = entry.getValue();
            var material = ModMaterials.getByName(name);

            if (material == null || !oreGen.enabled()) {
                continue;
            }

            addWorldgenResources(pack, material, oreGen, overworldFeatures, netherFeatures, endFeatures);
        }

        addBiomeModifiers(pack, overworldFeatures, netherFeatures, endFeatures);

        LOGGER.info("Registered virtual ore worldgen: {} Overworld, {} Nether, {} End features",
                overworldFeatures.size(), netherFeatures.size(), endFeatures.size());

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
                    allIngots
            );
        }

        addTagResources(
                pack,
                pickaxeMineableBlocks,
                needsStoneBlocks,
                needsIronBlocks,
                needsDiamondBlocks,
                allOreBlocks,
                allRawMaterials,
                allIngots
        );
    }

    private static void addWorldgenResources(
            VirtualPackResources pack,
            Material material,
            OreGenConfig oreGen,
            List<String> overworldFeatures,
            List<String> netherFeatures,
            List<String> endFeatures
    ) {
        if (oreGen.overworld().enabled() && (material.hasForm(ResourceForm.ORE) || material.hasForm(ResourceForm.DEEPSLATE_ORE))) {
            addOverworldOreWorldgen(pack, material, oreGen.overworld(), overworldFeatures);
        }

        if (oreGen.nether().enabled() && material.hasForm(ResourceForm.NETHERRACK_ORE)) {
            addNetherOreWorldgen(pack, material, oreGen.nether(), netherFeatures);
        }

        if (oreGen.end().enabled() && material.hasForm(ResourceForm.END_STONE_ORE)) {
            addEndOreWorldgen(pack, material, oreGen.end(), endFeatures);
        }
    }

    private static void addOverworldOreWorldgen(
            VirtualPackResources pack,
            Material material,
            DimensionOreConfig config,
            List<String> overworldFeatures
    ) {
        var name = material.name();
        var targets = getObjects(material, name);

        if (targets.isEmpty()) {
            return;
        }

        var configuredFeature = Map.of(
                "type", "minecraft:ore",
                "config", Map.of(
                        "size", config.veinSize(),
                        "discard_chance_on_air_exposure", 0.0f,
                        "targets", targets
                )
        );

        var cfgId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/configured_feature/ore_" + name + ".json");
        pack.addResource(cfgId, GSON.toJson(configuredFeature));

        var placedFeature = Map.of(
                "feature", ModernMachines.MOD_ID + ":ore_" + name,
                "placement", List.of(
                        Map.of("type", "minecraft:count", "count", config.veinsPerChunk()),
                        Map.of("type", "minecraft:in_square"),
                        Map.of("type", "minecraft:height_range", "height", Map.of(
                                "type", "uniform".equalsIgnoreCase(config.distribution()) ? "minecraft:uniform" : "minecraft:trapezoid",
                                "min_inclusive", Map.of("absolute", config.minY()),
                                "max_inclusive", Map.of("absolute", config.maxY())
                        )),
                        Map.of("type", "minecraft:biome")
                )
        );

        var placedId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/placed_feature/ore_" + name + "_placed.json");
        pack.addResource(placedId, GSON.toJson(placedFeature));

        overworldFeatures.add(ModernMachines.MOD_ID + ":ore_" + name + "_placed");
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

    private static void addNetherOreWorldgen(
            VirtualPackResources pack,
            Material material,
            DimensionOreConfig config,
            List<String> netherFeatures
    ) {
        var name = material.name();
        var configuredFeature = getOreBlockName(ResourceForm.NETHERRACK_ORE, material, config, "minecraft:netherrack");

        var cfgId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/configured_feature/ore_netherrack_" + name + ".json");
        pack.addResource(cfgId, GSON.toJson(configuredFeature));

        var placedFeature = Map.of(
                "feature", ModernMachines.MOD_ID + ":ore_netherrack_" + name,
                "placement", List.of(
                        Map.of("type", "minecraft:count", "count", config.veinsPerChunk()),
                        Map.of("type", "minecraft:in_square"),
                        Map.of("type", "minecraft:height_range", "height", Map.of(
                                "type", "uniform".equalsIgnoreCase(config.distribution()) ? "minecraft:uniform" : "minecraft:trapezoid",
                                "min_inclusive", Map.of("absolute", config.minY()),
                                "max_inclusive", Map.of("absolute", config.maxY())
                        )),
                        Map.of("type", "minecraft:biome")
                )
        );

        var placedId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/placed_feature/ore_netherrack_" + name + "_placed.json");
        pack.addResource(placedId, GSON.toJson(placedFeature));

        netherFeatures.add(ModernMachines.MOD_ID + ":ore_netherrack_" + name + "_placed");
    }

    private static @NonNull Map<String, Object> getOreBlockName(ResourceForm form, Material material, DimensionOreConfig config, String replaceBlock) {
        var oreBlockId = getBlockId(material, form, material.name());

        return Map.of(
                "type", "minecraft:ore",
                "config", Map.of(
                        "size", config.veinSize(),
                        "discard_chance_on_air_exposure", 0.0f,
                        "targets", List.of(
                                Map.of(
                                        "target", Map.of(
                                                "predicate_type", "minecraft:block_match",
                                                "block", replaceBlock
                                        ),
                                        "state", Map.of(
                                                "Name", oreBlockId
                                        )
                                )
                        )
                )
        );
    }

    private static void addEndOreWorldgen(
            VirtualPackResources pack,
            Material material,
            DimensionOreConfig config,
            List<String> endFeatures
    ) {
        var name = material.name();
        var configuredFeature = getOreBlockName(ResourceForm.END_STONE_ORE, material, config, "minecraft:end_stone");

        var cfgId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/configured_feature/ore_end_stone_" + name + ".json");
        pack.addResource(cfgId, GSON.toJson(configuredFeature));

        var placedFeature = Map.of(
                "feature", ModernMachines.MOD_ID + ":ore_end_stone_" + name,
                "placement", List.of(
                        Map.of("type", "minecraft:count", "count", config.veinsPerChunk()),
                        Map.of("type", "minecraft:in_square"),
                        Map.of("type", "minecraft:height_range", "height", Map.of(
                                "type", "uniform".equalsIgnoreCase(config.distribution()) ? "minecraft:uniform" : "minecraft:trapezoid",
                                "min_inclusive", Map.of("absolute", config.minY()),
                                "max_inclusive", Map.of("absolute", config.maxY())
                        )),
                        Map.of("type", "minecraft:biome")
                )
        );

        var placedId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "worldgen/placed_feature/ore_end_stone_" + name + "_placed.json");
        pack.addResource(placedId, GSON.toJson(placedFeature));

        endFeatures.add(ModernMachines.MOD_ID + ":ore_end_stone_" + name + "_placed");
    }

    private static void addBiomeModifiers(
            VirtualPackResources pack,
            List<String> overworldFeatures,
            List<String> netherFeatures,
            List<String> endFeatures
    ) {
        if (!overworldFeatures.isEmpty()) {
            var overworldModifier = Map.of(
                    "type", "neoforge:add_features",
                    "biomes", "#minecraft:is_overworld",
                    "features", overworldFeatures,
                    "step", "underground_ores"
            );
            var id = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "neoforge/biome_modifier/add_ores.json");
            pack.addResource(id, GSON.toJson(overworldModifier));
        }

        if (!netherFeatures.isEmpty()) {
            var netherModifier = Map.of(
                    "type", "neoforge:add_features",
                    "biomes", "#minecraft:is_nether",
                    "features", netherFeatures,
                    "step", "underground_ores"
            );
            var id = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "neoforge/biome_modifier/add_nether_ores.json");
            pack.addResource(id, GSON.toJson(netherModifier));
        }

        if (!endFeatures.isEmpty()) {
            var endModifier = Map.of(
                    "type", "neoforge:add_features",
                    "biomes", "#minecraft:is_end",
                    "features", endFeatures,
                    "step", "underground_ores"
            );
            var id = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "neoforge/biome_modifier/add_end_ores.json");
            pack.addResource(id, GSON.toJson(endModifier));
        }
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
            List<String> allIngots
    ) {
        var name = material.name();
        var rawItem = material.hasForm(ResourceForm.RAW_ORE) ? ModernMachines.MOD_ID + ":" + ResourceForm.RAW_ORE.getRegistryName(name) : null;
        var ingotItem = material.hasForm(ResourceForm.INGOT) ? ModernMachines.MOD_ID + ":" + ResourceForm.INGOT.getRegistryName(name) : null;
        var nuggetItem = material.hasForm(ResourceForm.NUGGET) ? ModernMachines.MOD_ID + ":" + ResourceForm.NUGGET.getRegistryName(name) : null;
        var storageBlock = material.hasForm(ResourceForm.STORAGE_BLOCK) ? ModernMachines.MOD_ID + ":" + ResourceForm.STORAGE_BLOCK.getRegistryName(name) : null;
        var rawStorageBlock = material.hasForm(ResourceForm.RAW_STORAGE_BLOCK) ? ModernMachines.MOD_ID + ":" + ResourceForm.RAW_STORAGE_BLOCK.getRegistryName(name) : null;

        if (rawItem != null) {
            allRawMaterials.add(rawItem);
        }

        if (ingotItem != null) {
            allIngots.add(ingotItem);
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
            assignMiningLevel(material, storageBlock, needsStoneBlocks, needsIronBlocks, needsDiamondBlocks);
            addSelfDropLootTable(pack, blockName, storageBlock);
        }

        if (rawStorageBlock != null) {
            var blockName = ResourceForm.RAW_STORAGE_BLOCK.getRegistryName(name);
            pickaxeMineableBlocks.add(rawStorageBlock);
            assignMiningLevel(material, rawStorageBlock, needsStoneBlocks, needsIronBlocks, needsDiamondBlocks);
            addSelfDropLootTable(pack, blockName, rawStorageBlock);
        }

        addCraftingAndSmeltingRecipes(pack, name, rawItem, ingotItem, nuggetItem, storageBlock, rawStorageBlock, material.smeltingXp());
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

    private static void addTagResources(
            VirtualPackResources pack,
            List<String> pickaxeMineableBlocks,
            List<String> needsStoneBlocks,
            List<String> needsIronBlocks,
            List<String> needsDiamondBlocks,
            List<String> allOreBlocks,
            List<String> allRawMaterials,
            List<String> allIngots
    ) {
        if (!pickaxeMineableBlocks.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("minecraft", "tags/block/mineable/pickaxe.json"),
                    GSON.toJson(Map.of("replace", false, "values", pickaxeMineableBlocks))
            );
        }

        if (!needsStoneBlocks.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("minecraft", "tags/block/needs_stone_tool.json"),
                    GSON.toJson(Map.of("replace", false, "values", needsStoneBlocks))
            );
        }

        if (!needsIronBlocks.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("minecraft", "tags/block/needs_iron_tool.json"),
                    GSON.toJson(Map.of("replace", false, "values", needsIronBlocks))
            );
        }

        if (!needsDiamondBlocks.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("minecraft", "tags/block/needs_diamond_tool.json"),
                    GSON.toJson(Map.of("replace", false, "values", needsDiamondBlocks))
            );
        }

        if (!allOreBlocks.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("c", "tags/block/ores.json"),
                    GSON.toJson(Map.of("replace", false, "values", allOreBlocks))
            );
        }

        if (!allRawMaterials.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("c", "tags/item/raw_materials.json"),
                    GSON.toJson(Map.of("replace", false, "values", allRawMaterials))
            );
        }

        if (!allIngots.isEmpty()) {
            pack.addResource(
                    Identifier.fromNamespaceAndPath("c", "tags/item/ingots.json"),
                    GSON.toJson(Map.of("replace", false, "values", allIngots))
            );
        }
    }
}
