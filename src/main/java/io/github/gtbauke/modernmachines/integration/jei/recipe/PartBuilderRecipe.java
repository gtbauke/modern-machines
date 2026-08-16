package io.github.gtbauke.modernmachines.integration.jei.recipe;

import java.util.List;

import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import net.minecraft.world.item.ItemStack;

public record PartBuilderRecipe(
        ItemStack pattern,
        List<ItemStack> materialInputs,
        ItemStack outputPart,
        Material material,
        ToolPartType partType,
        int materialCost
) {}
