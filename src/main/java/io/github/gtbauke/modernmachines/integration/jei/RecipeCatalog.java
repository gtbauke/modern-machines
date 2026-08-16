package io.github.gtbauke.modernmachines.integration.jei;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import io.github.gtbauke.modernmachines.integration.jei.recipe.AlloySmeltingRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.PartBuilderRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolAssemblyRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolUpgradingRecipe;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RecipeCatalog {

    public static List<PartBuilderRecipe> buildPartBuilderRecipes() {
        List<PartBuilderRecipe> recipes = new ArrayList<>();

        for (Material mat : ModMaterials.getAllMaterials()) {
            // Find input raw items (Ingot / Gem / Raw Ore / Storage Block)
            List<ItemStack> inputs = new ArrayList<>();
            Item ingot = mat.getItem(ResourceForm.INGOT);
            if (ingot != null) inputs.add(new ItemStack(ingot));
            Item gem = mat.getItem(ResourceForm.GEM);
            if (gem != null) inputs.add(new ItemStack(gem));
            Item rawOre = mat.getItem(ResourceForm.RAW_ORE);
            if (rawOre != null) inputs.add(new ItemStack(rawOre));

            if (inputs.isEmpty()) continue;

            for (ToolPartType partType : ToolPartType.values()) {
                ItemStack pattern = getPatternForPart(partType);
                ToolPartItem partItem = ModItems.getToolPart(partType, mat);
                if (partItem == null) continue;

                int cost = partType.getMaterialCost();
                List<ItemStack> sizedInputs = inputs.stream()
                        .map(i -> {
                            ItemStack copy = i.copy();
                            copy.setCount(cost);
                            return copy;
                        })
                        .toList();

                recipes.add(new PartBuilderRecipe(
                        pattern,
                        sizedInputs,
                        new ItemStack(partItem),
                        mat,
                        partType,
                        cost
                ));
            }
        }
        return recipes;
    }

    public static List<ToolAssemblyRecipe> buildToolAssemblyRecipes() {
        List<ToolAssemblyRecipe> recipes = new ArrayList<>();

        // Generate combinations across material tiers
        Material[] coreMaterials = new Material[] {
                ModMaterials.COPPER,
                ModMaterials.BRONZE,
                ModMaterials.IRON,
                ModMaterials.INVAR,
                ModMaterials.STEEL,
                ModMaterials.TITANIUM,
                ModMaterials.DIAMOND,
                ModMaterials.NETHERITE
        };

        ToolPartType[] headTypes = new ToolPartType[] {
                ToolPartType.PICKAXE_HEAD,
                ToolPartType.AXE_HEAD,
                ToolPartType.SHOVEL_HEAD,
                ToolPartType.SWORD_BLADE,
                ToolPartType.HOE_HEAD
        };

        for (ToolPartType headType : headTypes) {
            Item toolItem = switch (headType) {
                case PICKAXE_HEAD -> ModItems.MODULAR_PICKAXE.get();
                case AXE_HEAD -> ModItems.MODULAR_AXE.get();
                case SHOVEL_HEAD -> ModItems.MODULAR_SHOVEL.get();
                case SWORD_BLADE -> ModItems.MODULAR_SWORD.get();
                case HOE_HEAD -> ModItems.MODULAR_HOE.get();
                default -> null;
            };
            if (toolItem == null) continue;

            for (Material headMat : coreMaterials) {
                ToolPartItem head = ModItems.getToolPart(headType, headMat);
                ToolPartItem handle = ModItems.getToolPart(ToolPartType.HANDLE, headMat);
                ToolPartItem binding = ModItems.getToolPart(ToolPartType.BINDING, headMat);
                ToolPartItem tip = ModItems.getToolPart(ToolPartType.TIP, headMat);

                if (head == null || handle == null || binding == null) continue;

                Map<PartSlot, Identifier> parts = new EnumMap<>(PartSlot.class);
                parts.put(PartSlot.HEAD, headMat.getId());
                parts.put(PartSlot.HANDLE, headMat.getId());
                parts.put(PartSlot.BINDING, headMat.getId());
                if (tip != null) {
                    parts.put(PartSlot.TIP, headMat.getId());
                }

                ModularToolData data = new ModularToolData(parts, List.of(), 0, 0);
                ItemStack toolStack = new ItemStack(toolItem);
                toolStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data);

                recipes.add(new ToolAssemblyRecipe(
                        new ItemStack(head),
                        new ItemStack(handle),
                        new ItemStack(binding),
                        tip != null ? new ItemStack(tip) : ItemStack.EMPTY,
                        toolStack
                ));
            }
        }
        return recipes;
    }

    public static List<ToolUpgradingRecipe> buildToolUpgradingRecipes() {
        List<ToolUpgradingRecipe> recipes = new ArrayList<>();

        ItemStack basePick = createSamplePickaxe(ModMaterials.IRON);

        // 1. Diamond Upgrade (+500 Durability, +1 Mining Level)
        recipes.add(new ToolUpgradingRecipe(
                basePick,
                new ItemStack(Items.DIAMOND),
                createSamplePickaxe(ModMaterials.DIAMOND),
                Component.literal("+500 Durability, +1 Mining Level Tier").withStyle(ChatFormatting.AQUA)
        ));

        // 2. Netherite Upgrade (+Max Durability & Toughness)
        recipes.add(new ToolUpgradingRecipe(
                createSamplePickaxe(ModMaterials.DIAMOND),
                new ItemStack(Items.NETHERITE_INGOT),
                createSamplePickaxe(ModMaterials.NETHERITE),
                Component.literal("+800 Durability, Fire Immunity").withStyle(ChatFormatting.GOLD)
        ));

        // 3. Redstone Speed Modifier (+1.5 Mining Speed)
        recipes.add(new ToolUpgradingRecipe(
                basePick,
                new ItemStack(Items.REDSTONE_BLOCK),
                basePick.copy(),
                Component.literal("+2.0 Mining Speed, +10% Attack Speed").withStyle(ChatFormatting.RED)
        ));

        // 4. Lapis Luck Modifier (+Fortune / Looting)
        recipes.add(new ToolUpgradingRecipe(
                basePick,
                new ItemStack(Items.LAPIS_BLOCK),
                basePick.copy(),
                Component.literal("+1 Fortune / Looting Level").withStyle(ChatFormatting.BLUE)
        ));

        return recipes;
    }

    public static List<AlloySmeltingRecipe> buildAlloySmeltingRecipes() {
        List<AlloySmeltingRecipe> recipes = new ArrayList<>();

        // Bronze: 3 Copper + 1 Tin -> 4 Bronze (200t, 4000 RF)
        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.COPPER_INGOT, 3),
                new ItemStack(ModMaterials.TIN.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.BRONZE.getItem(ResourceForm.INGOT), 4),
                4000,
                200
        ));

        // Invar: 2 Iron + 1 Nickel -> 3 Invar (200t, 4000 RF)
        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(ModMaterials.NICKEL.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.INVAR.getItem(ResourceForm.INGOT), 3),
                4000,
                200
        ));

        // Electrum: 1 Gold + 1 Silver -> 2 Electrum (200t, 4000 RF)
        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.GOLD_INGOT, 1),
                new ItemStack(ModMaterials.SILVER.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.ELECTRUM.getItem(ResourceForm.INGOT), 2),
                4000,
                200
        ));

        // Constantan: 1 Copper + 1 Nickel -> 2 Constantan (200t, 4000 RF)
        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.COPPER_INGOT, 1),
                new ItemStack(ModMaterials.NICKEL.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.CONSTANTAN.getItem(ResourceForm.INGOT), 2),
                4000,
                200
        ));

        // Steel: 1 Iron + 2 Coal -> 1 Steel (300t, 6000 RF)
        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.IRON_INGOT, 1),
                new ItemStack(Items.COAL, 2),
                new ItemStack(ModMaterials.STEEL.getItem(ResourceForm.INGOT), 1),
                6000,
                300
        ));

        return recipes;
    }

    private static ItemStack createSamplePickaxe(Material mat) {
        Map<PartSlot, Identifier> parts = new EnumMap<>(PartSlot.class);
        parts.put(PartSlot.HEAD, mat.getId());
        parts.put(PartSlot.HANDLE, mat.getId());
        parts.put(PartSlot.BINDING, mat.getId());

        ModularToolData data = new ModularToolData(parts, List.of(), 0, 0);
        ItemStack pick = new ItemStack(ModItems.MODULAR_PICKAXE.get());
        pick.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data);
        return pick;
    }

    private static ItemStack getPatternForPart(ToolPartType partType) {
        Item item = switch (partType) {
            case PICKAXE_HEAD -> ModItems.PICKAXE_HEAD_PATTERN.get();
            case AXE_HEAD -> ModItems.AXE_HEAD_PATTERN.get();
            case SHOVEL_HEAD -> ModItems.SHOVEL_HEAD_PATTERN.get();
            case SWORD_BLADE -> ModItems.SWORD_BLADE_PATTERN.get();
            case HOE_HEAD -> ModItems.HOE_HEAD_PATTERN.get();
            case HANDLE -> ModItems.HANDLE_PATTERN.get();
            case BINDING -> ModItems.BINDING_PATTERN.get();
            case TIP -> ModItems.TIP_PATTERN.get();
            case GRIP -> ModItems.GRIP_PATTERN.get();
        };
        return new ItemStack(item);
    }
}
