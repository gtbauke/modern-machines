package io.github.gtbauke.modernmachines.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class ModRecipeProvider extends RecipeProvider {

    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // Engineer's Hammer: 5 Iron Ingots + 2 Sticks
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, ModItems.ENGINEER_HAMMER.get())
                .pattern("III")
                .pattern("ISI")
                .pattern(" S ")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "engineers_hammer")));

        // Wire Cutters: 2 Iron Ingots + 1 Stick
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, ModItems.WIRE_CUTTER.get())
                .pattern("I ")
                .pattern(" I")
                .pattern("S ")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "wire_cutter")));

        // Workstation: Part Builder (Crafting table + 2 planks)
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModBlocks.PART_BUILDER.get())
                .pattern("P ")
                .pattern("CW")
                .define('P', Items.OAK_PLANKS)
                .define('C', Items.CRAFTING_TABLE)
                .define('W', Items.OAK_LOG)
                .unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "part_builder")));

        // Workstation: Tinkering Table (Part builder + 2 stone)
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModBlocks.TINKERING_TABLE.get())
                .pattern("SS")
                .pattern("PB")
                .define('S', Items.SMOOTH_STONE)
                .define('P', ModBlocks.PART_BUILDER.get())
                .define('B', Items.IRON_BLOCK)
                .unlockedBy("has_part_builder", has(ModBlocks.PART_BUILDER.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "tinkering_table")));

        // Workstation: Basic Alloy Smelter Controller (Copper + Iron + Redstone)
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get())
                .pattern("ICI")
                .pattern("CRC")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "basic_alloy_smelter_controller")));

        // Workstation: Basic Alloy Smelter Heater (Furnace + 4 Iron + 4 Copper)
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get())
                .pattern("ICI")
                .pattern("CFC")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('F', Items.FURNACE)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "basic_alloy_smelter_heater")));

        // Copper Pipe (6 Copper Ingots -> 6 Pipes)
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COPPER_PIPE.get(), 6)
                .pattern("CCC")
                .pattern("   ")
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "copper_pipe")));

        // Steel Pipe (6 Steel Ingots -> 6 Pipes)
        Item steelIngot = ModMaterials.STEEL.getItem(ResourceForm.INGOT);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_PIPE.get(), 6)
                .pattern("SSS")
                .pattern("   ")
                .pattern("SSS")
                .define('S', steelIngot)
                .unlockedBy("has_steel_ingot", has(steelIngot))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "steel_pipe")));

        // Upgrades: Speed Upgrade
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ModItems.SPEED_UPGRADE.get())
                .pattern("PRP")
                .pattern("WGW")
                .pattern("PRP")
                .define('P', ModMaterials.COPPER.getItem(ResourceForm.PLATE))
                .define('R', Items.REDSTONE)
                .define('W', ModMaterials.GOLD.getItem(ResourceForm.WIRE))
                .define('G', ModMaterials.BRONZE.getItem(ResourceForm.GEAR))
                .unlockedBy("has_bronze_gear", has(ModMaterials.BRONZE.getItem(ResourceForm.GEAR)))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "speed_upgrade")));

        // Upgrades: Energy Efficiency Upgrade
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ModItems.ENERGY_EFFICIENCY_UPGRADE.get())
                .pattern("PRP")
                .pattern("WGW")
                .pattern("PRP")
                .define('P', ModMaterials.TIN.getItem(ResourceForm.PLATE))
                .define('R', Items.REDSTONE)
                .define('W', ModMaterials.COPPER.getItem(ResourceForm.WIRE))
                .define('G', ModMaterials.INVAR.getItem(ResourceForm.GEAR))
                .unlockedBy("has_invar_gear", has(ModMaterials.INVAR.getItem(ResourceForm.GEAR)))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "energy_efficiency_upgrade")));

        // Blank Pattern (2 sticks + 2 planks -> 4 patterns)
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ModItems.BLANK_PATTERN.get(), 4)
                .pattern("SP")
                .pattern("PS")
                .define('S', Items.STICK)
                .define('P', Items.OAK_PLANKS)
                .unlockedBy("has_planks", has(Items.OAK_PLANKS))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "blank_pattern")));

        // Specific Patterns (Shapeless with Blank Pattern)
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.PICKAXE_HEAD_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.COBBLESTONE)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "pickaxe_head_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.AXE_HEAD_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.STONE)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "axe_head_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.SHOVEL_HEAD_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.DIRT)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "shovel_head_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.SWORD_BLADE_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.FLINT)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "sword_blade_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.HOE_HEAD_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.OAK_PLANKS)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "hoe_head_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.HANDLE_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.STICK)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "handle_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.BINDING_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.STRING)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "binding_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.TIP_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.FEATHER)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "tip_pattern")));

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ModItems.GRIP_PATTERN.get())
                .requires(ModItems.BLANK_PATTERN.get())
                .requires(Items.LEATHER)
                .unlockedBy("has_blank_pattern", has(ModItems.BLANK_PATTERN.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "grip_pattern")));

        // Engineer's Tablet Recipe
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, ModItems.ENGINEERS_TABLET.get())
                .pattern("IRI")
                .pattern("IGI")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('G', Items.GLASS_PANE)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "engineers_tablet")));

        // Engineer's Terminal Recipe
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModBlocks.ENGINEERS_TERMINAL.get())
                .pattern("IGI")
                .pattern("ITI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GLASS_PANE)
                .define('T', ModBlocks.PART_BUILDER.get())
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_part_builder", has(ModBlocks.PART_BUILDER.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "engineers_terminal")));

        // Iterate over materials
        for (Material material : ModMaterials.getAllMaterials()) {
            Item ingot = material.getItem(ResourceForm.INGOT);
            if (ingot == null) {
                ingot = material.getItem(ResourceForm.GEM);
            }
            Item nugget = material.getItem(ResourceForm.NUGGET);
            Block storageBlock = material.getBlock(ResourceForm.STORAGE_BLOCK);
            Block rawStorageBlock = material.getBlock(ResourceForm.RAW_STORAGE_BLOCK);
            Item rawOre = material.getItem(ResourceForm.RAW_ORE);
            Block ore = material.getBlock(ResourceForm.ORE);
            Block deepslateOre = material.getBlock(ResourceForm.DEEPSLATE_ORE);
            Block netherrackOre = material.getBlock(ResourceForm.NETHERRACK_ORE);
            Block endStoneOre = material.getBlock(ResourceForm.END_STONE_ORE);
            Item dust = material.getItem(ResourceForm.DUST);
            Item plate = material.getItem(ResourceForm.PLATE);
            Item rod = material.getItem(ResourceForm.ROD);
            Item screw = material.getItem(ResourceForm.SCREW);
            Item wire = material.getItem(ResourceForm.WIRE);
            Item gear = material.getItem(ResourceForm.GEAR);

            // 1. Storage Block <-> Ingot
            if (storageBlock != null && ingot != null && material.isRegisteredLocally(ResourceForm.STORAGE_BLOCK)) {
                threeByThreePacker(RecipeCategory.BUILDING_BLOCKS, storageBlock, ingot);
                oneToOneConversionRecipe(ingot, storageBlock, null, 9);
            }

            // 2. Raw Storage Block <-> Raw Ore
            if (rawStorageBlock != null && rawOre != null && material.isRegisteredLocally(ResourceForm.RAW_STORAGE_BLOCK)) {
                threeByThreePacker(RecipeCategory.BUILDING_BLOCKS, rawStorageBlock, rawOre);
                oneToOneConversionRecipe(rawOre, rawStorageBlock, null, 9);
            }

            // 3. Ingot <-> Nugget
            if (ingot != null && nugget != null && material.isRegisteredLocally(ResourceForm.NUGGET)) {
                threeByThreePacker(RecipeCategory.MISC, ingot, nugget);
                oneToOneConversionRecipe(nugget, ingot, null, 9);
            }

            // 4. Smelting & Blasting
            if (ingot != null) {
                List<ItemLike> smeltables = new ArrayList<>();
                if (material.isRegisteredLocally(ResourceForm.ORE) && ore != null) smeltables.add(ore);
                if (material.isRegisteredLocally(ResourceForm.DEEPSLATE_ORE) && deepslateOre != null) smeltables.add(deepslateOre);
                if (material.isRegisteredLocally(ResourceForm.NETHERRACK_ORE) && netherrackOre != null) smeltables.add(netherrackOre);
                if (material.isRegisteredLocally(ResourceForm.END_STONE_ORE) && endStoneOre != null) smeltables.add(endStoneOre);
                if (material.isRegisteredLocally(ResourceForm.RAW_ORE) && rawOre != null) smeltables.add(rawOre);
                if (material.isRegisteredLocally(ResourceForm.DUST) && dust != null) smeltables.add(dust);

                int counter = 0;
                for (ItemLike smeltable : smeltables) {
                    counter++;
                    SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(smeltable),
                            RecipeCategory.MISC,
                            CookingBookCategory.MISC,
                            ingot,
                            material.smeltingXp(),
                            200
                    )
                    .unlockedBy("has_" + material.name(), has(smeltable))
                    .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_ingot_smelting_" + counter)));

                    SimpleCookingRecipeBuilder.blasting(
                            Ingredient.of(smeltable),
                            RecipeCategory.MISC,
                            CookingBookCategory.MISC,
                            ingot,
                            material.smeltingXp(),
                            100
                    )
                    .unlockedBy("has_" + material.name(), has(smeltable))
                    .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_ingot_blasting_" + counter)));
                }
            }

            // 5. Early-game Plate Crafting: Hammer + Ingot -> Plate
            if (plate != null && ingot != null && material.isRegisteredLocally(ResourceForm.PLATE)) {
                ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, plate)
                        .requires(ModItems.ENGINEER_HAMMER.get())
                        .requires(ingot)
                        .unlockedBy("has_" + material.name(), has(ingot))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_plate_from_hammer")));
            }

            // 6. Early-game Dust Crafting: Hammer + Ingot -> Dust
            if (dust != null && ingot != null && material.isRegisteredLocally(ResourceForm.DUST)) {
                ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, dust)
                        .requires(ModItems.ENGINEER_HAMMER.get())
                        .requires(ingot)
                        .unlockedBy("has_" + material.name(), has(ingot))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_dust_from_hammer")));
            }

            // 7. Rod: 2 Ingots vertically -> 4 Rods
            if (rod != null && ingot != null && material.isRegisteredLocally(ResourceForm.ROD)) {
                ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, rod, 4)
                        .pattern("I")
                        .pattern("I")
                        .define('I', ingot)
                        .unlockedBy("has_" + material.name(), has(ingot))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_rod")));
            }

            // 8. Screws: Hammer + Rod -> 4 Screws
            if (screw != null && rod != null && material.isRegisteredLocally(ResourceForm.SCREW)) {
                ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, screw, 4)
                        .requires(ModItems.ENGINEER_HAMMER.get())
                        .requires(rod)
                        .unlockedBy("has_" + material.name() + "_rod", has(rod))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_screw_from_hammer")));
            }

            // 9. Wire: Wire Cutters + Plate -> 2 Wires
            if (wire != null && plate != null && material.isRegisteredLocally(ResourceForm.WIRE)) {
                ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, wire, 2)
                        .requires(ModItems.WIRE_CUTTER.get())
                        .requires(plate)
                        .unlockedBy("has_" + material.name() + "_plate", has(plate))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_wire_from_cutters")));
            }

            // 10. Gear: 4 Plates + 1 Ingot -> 1 Gear
            if (gear != null && plate != null && material.isRegisteredLocally(ResourceForm.GEAR)) {
                ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, gear)
                        .pattern(" P ")
                        .pattern("PIP")
                        .pattern(" P ")
                        .define('P', plate)
                        .define('I', ingot != null ? ingot : Items.IRON_INGOT)
                        .unlockedBy("has_" + material.name() + "_plate", has(plate))
                        .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, material.name() + "_gear")));
            }
        }

        // Alloy Smelting Recipes
        HolderLookup.RegistryLookup<Item> itemLookup = this.registries.lookupOrThrow(Registries.ITEM);

        // 1. Bronze: 3x Copper Ingot + 1x Tin Ingot -> 4x Bronze Ingot
        AlloySmeltingRecipeBuilder.smelting(ModMaterials.BRONZE.getItem(ResourceForm.INGOT), 4)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.COPPER.getItemTag(ResourceForm.INGOT))), 3)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.TIN.getItemTag(ResourceForm.INGOT))), 1)
                .cookingTime(200)
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "alloy_bronze")));

        // 2. Constantan: 1x Copper Ingot + 1x Nickel Ingot -> 2x Constantan Ingot
        AlloySmeltingRecipeBuilder.smelting(ModMaterials.CONSTANTAN.getItem(ResourceForm.INGOT), 2)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.COPPER.getItemTag(ResourceForm.INGOT))), 1)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.NICKEL.getItemTag(ResourceForm.INGOT))), 1)
                .cookingTime(180)
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "alloy_constantan")));

        // 3. Electrum: 1x Gold Ingot + 1x Silver Ingot -> 2x Electrum Ingot
        AlloySmeltingRecipeBuilder.smelting(ModMaterials.ELECTRUM.getItem(ResourceForm.INGOT), 2)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.GOLD.getItemTag(ResourceForm.INGOT))), 1)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.SILVER.getItemTag(ResourceForm.INGOT))), 1)
                .cookingTime(180)
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "alloy_electrum")));

        // 4. Invar: 2x Iron Ingot + 1x Nickel Ingot -> 3x Invar Ingot
        AlloySmeltingRecipeBuilder.smelting(ModMaterials.INVAR.getItem(ResourceForm.INGOT), 3)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.IRON.getItemTag(ResourceForm.INGOT))), 2)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.NICKEL.getItemTag(ResourceForm.INGOT))), 1)
                .cookingTime(200)
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "alloy_invar")));

        // 5. Steel: 1x Iron Ingot + 1x Coal/Charcoal -> 1x Steel Ingot
        AlloySmeltingRecipeBuilder.smelting(ModMaterials.STEEL.getItem(ResourceForm.INGOT), 1)
                .addInput(Ingredient.of(itemLookup.getOrThrow(ModMaterials.IRON.getItemTag(ResourceForm.INGOT))), 1)
                .addInput(Ingredient.of(itemLookup.getOrThrow(net.minecraft.tags.ItemTags.COALS)), 1)
                .cookingTime(240)
                .save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "alloy_steel")));
    }

    public static class AlloySmeltingRecipeBuilder {
        private final List<net.neoforged.neoforge.common.crafting.SizedIngredient> inputs = new ArrayList<>();
        private final net.minecraft.world.item.ItemStackTemplate result;
        private int cookingTime = 200;
        private int energyCost = 3000;
        private float experience = 0.7f;

        public AlloySmeltingRecipeBuilder(net.minecraft.world.item.ItemStackTemplate result) {
            this.result = result;
        }

        public static AlloySmeltingRecipeBuilder smelting(ItemLike result, int count) {
            return new AlloySmeltingRecipeBuilder(new net.minecraft.world.item.ItemStackTemplate(result.asItem(), count));
        }

        public AlloySmeltingRecipeBuilder addInput(Ingredient ingredient, int count) {
            this.inputs.add(new net.neoforged.neoforge.common.crafting.SizedIngredient(ingredient, count));
            return this;
        }

        public AlloySmeltingRecipeBuilder cookingTime(int time) {
            this.cookingTime = time;
            return this;
        }

        public AlloySmeltingRecipeBuilder energy(int energy) {
            this.energyCost = energy;
            return this;
        }

        public AlloySmeltingRecipeBuilder experience(float xp) {
            this.experience = xp;
            return this;
        }

        public void save(RecipeOutput output, ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> key) {
            io.github.gtbauke.modernmachines.machine.recipe.AlloySmeltingRecipe recipe =
                    new io.github.gtbauke.modernmachines.machine.recipe.AlloySmeltingRecipe(inputs, result, cookingTime, energyCost, experience);
            output.accept(key, recipe, null);
        }
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Modern Machines Recipes";
        }
    }
}
