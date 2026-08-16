package io.github.gtbauke.modernmachines.integration.jei.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record ToolUpgradingRecipe(
        ItemStack inputTool,
        ItemStack modifierItem,
        ItemStack outputTool,
        Component description
) {}
