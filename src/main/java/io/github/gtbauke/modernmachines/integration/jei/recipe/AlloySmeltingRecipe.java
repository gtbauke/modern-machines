package io.github.gtbauke.modernmachines.integration.jei.recipe;

import net.minecraft.world.item.ItemStack;

public record AlloySmeltingRecipe(
        ItemStack inputA,
        ItemStack inputB,
        ItemStack output,
        int energyCost,
        int processTime
) {}
