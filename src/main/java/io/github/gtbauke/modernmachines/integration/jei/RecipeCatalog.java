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
import io.github.gtbauke.modernmachines.config.material.CustomMaterialConfig;
import io.github.gtbauke.modernmachines.config.material.CustomMaterialLoader;
import io.github.gtbauke.modernmachines.integration.jei.recipe.AlloySmeltingRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.PartBuilderRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolAssemblyRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolUpgradingRecipe;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RecipeCatalog {

    public static List<PartBuilderRecipe> buildPartBuilderRecipes() {
        var recipes = new ArrayList<PartBuilderRecipe>();

        for (var mat : ModMaterials.getAllMaterials()) {
            var inputs = new ArrayList<ItemStack>();
            var ingot = mat.getItem(ResourceForm.INGOT);
            if (ingot != null) {
                inputs.add(new ItemStack(ingot));
            }

            var gem = mat.getItem(ResourceForm.GEM);
            if (gem != null) {
                inputs.add(new ItemStack(gem));
            }

            var rawOre = mat.getItem(ResourceForm.RAW_ORE);
            if (rawOre != null) {
                inputs.add(new ItemStack(rawOre));
            }

            if (inputs.isEmpty()) {
                continue;
            }

            for (var partType : ToolPartType.values()) {
                var pattern = getPatternForPart(partType);
                var partItem = ModItems.getToolPart(partType, mat);
                if (partItem == null) {
                    continue;
                }

                int cost = partType.getMaterialCost();
                var sizedInputs = inputs.stream()
                        .map(i -> {
                            var copy = i.copy();
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
        var recipes = new ArrayList<ToolAssemblyRecipe>();

        var coreMaterials = new Material[] {
                ModMaterials.COPPER,
                ModMaterials.BRONZE,
                ModMaterials.IRON,
                ModMaterials.INVAR,
                ModMaterials.STEEL,
                ModMaterials.TITANIUM,
                ModMaterials.DIAMOND,
                ModMaterials.NETHERITE
        };

        var headTypes = new ToolPartType[] {
                ToolPartType.PICKAXE_HEAD,
                ToolPartType.AXE_HEAD,
                ToolPartType.SHOVEL_HEAD,
                ToolPartType.SWORD_BLADE,
                ToolPartType.HOE_HEAD
        };

        for (var headType : headTypes) {
            var toolItem = switch (headType) {
                case PICKAXE_HEAD -> ModItems.MODULAR_PICKAXE.get();
                case AXE_HEAD -> ModItems.MODULAR_AXE.get();
                case SHOVEL_HEAD -> ModItems.MODULAR_SHOVEL.get();
                case SWORD_BLADE -> ModItems.MODULAR_SWORD.get();
                case HOE_HEAD -> ModItems.MODULAR_HOE.get();
                default -> null;
            };

            if (toolItem == null) {
                continue;
            }

            for (var headMat : coreMaterials) {
                var head = ModItems.getToolPart(headType, headMat);
                var handle = ModItems.getToolPart(ToolPartType.HANDLE, headMat);
                var binding = ModItems.getToolPart(ToolPartType.BINDING, headMat);
                var tip = ModItems.getToolPart(ToolPartType.TIP, headMat);

                if (head == null || handle == null || binding == null) {
                    continue;
                }

                var parts = new EnumMap<PartSlot, Identifier>(PartSlot.class);
                parts.put(PartSlot.HEAD, headMat.getId());
                parts.put(PartSlot.HANDLE, headMat.getId());
                parts.put(PartSlot.BINDING, headMat.getId());
                if (tip != null) {
                    parts.put(PartSlot.TIP, headMat.getId());
                }

                var data = new ModularToolData(parts, List.of(), 0, 0);
                var toolStack = new ItemStack(toolItem);
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
        var recipes = new ArrayList<ToolUpgradingRecipe>();

        var basePick = createSamplePickaxe(ModMaterials.IRON);

        recipes.add(new ToolUpgradingRecipe(
                basePick,
                new ItemStack(Items.DIAMOND),
                createSamplePickaxe(ModMaterials.DIAMOND),
                Component.literal("+500 Durability, +1 Mining Level Tier").withStyle(ChatFormatting.AQUA)
        ));

        recipes.add(new ToolUpgradingRecipe(
                createSamplePickaxe(ModMaterials.DIAMOND),
                new ItemStack(Items.NETHERITE_INGOT),
                createSamplePickaxe(ModMaterials.NETHERITE),
                Component.literal("+800 Durability, Fire Immunity").withStyle(ChatFormatting.GOLD)
        ));

        recipes.add(new ToolUpgradingRecipe(
                basePick,
                new ItemStack(Items.REDSTONE_BLOCK),
                basePick.copy(),
                Component.literal("+2.0 Mining Speed, +10% Attack Speed").withStyle(ChatFormatting.RED)
        ));

        recipes.add(new ToolUpgradingRecipe(
                basePick,
                new ItemStack(Items.LAPIS_BLOCK),
                basePick.copy(),
                Component.literal("+1 Fortune / Looting Level").withStyle(ChatFormatting.BLUE)
        ));

        return recipes;
    }

    public static List<AlloySmeltingRecipe> buildAlloySmeltingRecipes() {
        var recipes = new ArrayList<AlloySmeltingRecipe>();

        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.COPPER_INGOT, 3),
                new ItemStack(ModMaterials.TIN.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.BRONZE.getItem(ResourceForm.INGOT), 4),
                4000,
                200
        ));

        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(ModMaterials.NICKEL.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.INVAR.getItem(ResourceForm.INGOT), 3),
                4000,
                200
        ));

        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.GOLD_INGOT, 1),
                new ItemStack(ModMaterials.SILVER.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.ELECTRUM.getItem(ResourceForm.INGOT), 2),
                4000,
                200
        ));

        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.COPPER_INGOT, 1),
                new ItemStack(ModMaterials.NICKEL.getItem(ResourceForm.INGOT), 1),
                new ItemStack(ModMaterials.CONSTANTAN.getItem(ResourceForm.INGOT), 2),
                4000,
                200
        ));

        recipes.add(new AlloySmeltingRecipe(
                new ItemStack(Items.IRON_INGOT, 1),
                new ItemStack(Items.COAL, 2),
                new ItemStack(ModMaterials.STEEL.getItem(ResourceForm.INGOT), 1),
                6000,
                300
        ));

        for (var entry : CustomMaterialLoader.getAllCustomConfigs().entrySet()) {
            var name = entry.getKey();
            var config = entry.getValue();
            var material = ModMaterials.getByName(name);
            if (material == null || config == null || config.alloyRecipe == null || config.alloyRecipe.inputs.size() < 2) {
                continue;
            }

            var ingot = material.getItem(ResourceForm.INGOT);
            if (ingot == null) {
                continue;
            }

            var stackA = resolveJeiIngredient(config.alloyRecipe.inputs.get(0));
            var stackB = resolveJeiIngredient(config.alloyRecipe.inputs.get(1));
            if (stackA.isEmpty() || stackB.isEmpty()) {
                continue;
            }

            var resultCount = config.alloyRecipe.resultCount > 0 ? config.alloyRecipe.resultCount : 1;
            recipes.add(new AlloySmeltingRecipe(
                    stackA,
                    stackB,
                    new ItemStack(ingot, resultCount),
                    config.alloyRecipe.energy > 0 ? config.alloyRecipe.energy : 3000,
                    config.alloyRecipe.cookingTime > 0 ? config.alloyRecipe.cookingTime : 200
            ));
        }

        return recipes;
    }

    private static ItemStack resolveJeiIngredient(CustomMaterialConfig.AlloyInputConfig input) {
        var str = input.ingredient;
        var count = input.count > 0 ? input.count : 1;
        if (str.startsWith("#")) {
            str = str.substring(1);
        }

        var id = Identifier.tryParse(str);
        if (id != null) {
            var itemHolder = BuiltInRegistries.ITEM.get(id);
            if (itemHolder.isPresent()) {
                return new ItemStack(itemHolder.get().value(), count);
            }
        }

        if ("c:ingots/gold".equals(str) || "c:gold_ingots".equals(str)) {
            return new ItemStack(Items.GOLD_INGOT, count);
        }

        if ("c:ingots/copper".equals(str) || "c:copper_ingots".equals(str)) {
            return new ItemStack(Items.COPPER_INGOT, count);
        }

        if ("c:ingots/iron".equals(str) || "c:iron_ingots".equals(str)) {
            return new ItemStack(Items.IRON_INGOT, count);
        }

        if ("c:ingots/tin".equals(str)) {
            var tin = ModMaterials.TIN.getItem(ResourceForm.INGOT);
            return tin != null ? new ItemStack(tin, count) : ItemStack.EMPTY;
        }

        if ("c:ingots/silver".equals(str)) {
            var silver = ModMaterials.SILVER.getItem(ResourceForm.INGOT);
            return silver != null ? new ItemStack(silver, count) : ItemStack.EMPTY;
        }

        if ("c:ingots/nickel".equals(str)) {
            var nickel = ModMaterials.NICKEL.getItem(ResourceForm.INGOT);
            return nickel != null ? new ItemStack(nickel, count) : ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack createSamplePickaxe(Material mat) {
        var parts = new EnumMap<PartSlot, Identifier>(PartSlot.class);
        parts.put(PartSlot.HEAD, mat.getId());
        parts.put(PartSlot.HANDLE, mat.getId());
        parts.put(PartSlot.BINDING, mat.getId());

        var data = new ModularToolData(parts, List.of(), 0, 0);
        var pick = new ItemStack(ModItems.MODULAR_PICKAXE.get());
        pick.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data);
        return pick;
    }

    private static ItemStack getPatternForPart(ToolPartType partType) {
        var item = switch (partType) {
            case PICKAXE_HEAD -> ModItems.PICKAXE_HEAD_PATTERN.get();
            case AXE_HEAD -> ModItems.AXE_HEAD_PATTERN.get();
            case SHOVEL_HEAD -> ModItems.SHOVEL_HEAD_PATTERN.get();
            case SWORD_BLADE -> ModItems.SWORD_BLADE_PATTERN.get();
            case HOE_HEAD -> ModItems.HOE_HEAD_PATTERN.get();
            case HANDLE -> ModItems.HANDLE_PATTERN.get();
            case BINDING -> ModItems.BINDING_PATTERN.get();
            case TIP -> ModItems.TIP_PATTERN.get();
            case GRIP -> ModItems.GRIP_PATTERN.get();
            case SWORD_GUARD -> ModItems.SWORD_GUARD_PATTERN.get();
            case POMMEL -> ModItems.POMMEL_PATTERN.get();
        };

        return new ItemStack(item);
    }
}
