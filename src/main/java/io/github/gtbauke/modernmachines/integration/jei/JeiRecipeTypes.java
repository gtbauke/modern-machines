package io.github.gtbauke.modernmachines.integration.jei;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.integration.jei.recipe.AlloySmeltingRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.PartBuilderRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolAssemblyRecipe;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolUpgradingRecipe;
import mezz.jei.api.recipe.RecipeType;

public class JeiRecipeTypes {
    public static final RecipeType<PartBuilderRecipe> PART_BUILDING =
            RecipeType.create(ModernMachines.MOD_ID, "part_building", PartBuilderRecipe.class);

    public static final RecipeType<ToolAssemblyRecipe> TOOL_ASSEMBLY =
            RecipeType.create(ModernMachines.MOD_ID, "tool_assembly", ToolAssemblyRecipe.class);

    public static final RecipeType<ToolUpgradingRecipe> TOOL_UPGRADING =
            RecipeType.create(ModernMachines.MOD_ID, "tool_upgrading", ToolUpgradingRecipe.class);

    public static final RecipeType<AlloySmeltingRecipe> ALLOY_SMELTING =
            RecipeType.create(ModernMachines.MOD_ID, "alloy_smelting", AlloySmeltingRecipe.class);
}
