package io.github.gtbauke.modernmachines.integration.jei.recipe;

import net.minecraft.world.item.ItemStack;

public record ToolAssemblyRecipe(
        ItemStack head,
        ItemStack handle,
        ItemStack binding,
        ItemStack attachment,
        ItemStack outputTool
) {}
