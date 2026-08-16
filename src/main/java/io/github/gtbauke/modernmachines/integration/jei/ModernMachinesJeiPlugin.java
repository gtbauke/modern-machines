package io.github.gtbauke.modernmachines.integration.jei;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.integration.jei.category.AlloySmelterCategory;
import io.github.gtbauke.modernmachines.integration.jei.category.PartBuilderCategory;
import io.github.gtbauke.modernmachines.integration.jei.category.ToolAssemblyCategory;
import io.github.gtbauke.modernmachines.integration.jei.category.ToolUpgradingCategory;
import io.github.gtbauke.modernmachines.machine.client.AlloySmelterScreen;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import io.github.gtbauke.modernmachines.modular.client.PartBuilderScreen;
import io.github.gtbauke.modernmachines.modular.client.TinkeringTableScreen;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class ModernMachinesJeiPlugin implements IModPlugin {
    public static final Identifier PLUGIN_UID = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new PartBuilderCategory(registration.getJeiHelpers().getGuiHelper()),
                new ToolAssemblyCategory(registration.getJeiHelpers().getGuiHelper()),
                new ToolUpgradingCategory(registration.getJeiHelpers().getGuiHelper()),
                new AlloySmelterCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(JeiRecipeTypes.PART_BUILDING, RecipeCatalog.buildPartBuilderRecipes());
        registration.addRecipes(JeiRecipeTypes.TOOL_ASSEMBLY, RecipeCatalog.buildToolAssemblyRecipes());
        registration.addRecipes(JeiRecipeTypes.TOOL_UPGRADING, RecipeCatalog.buildToolUpgradingRecipes());
        registration.addRecipes(JeiRecipeTypes.ALLOY_SMELTING, RecipeCatalog.buildAlloySmeltingRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(JeiRecipeTypes.PART_BUILDING, ModBlocks.PART_BUILDER.get());
        registration.addCraftingStation(JeiRecipeTypes.TOOL_ASSEMBLY, ModBlocks.TINKERING_TABLE.get());
        registration.addCraftingStation(JeiRecipeTypes.TOOL_UPGRADING, ModBlocks.TINKERING_TABLE.get());
        registration.addCraftingStation(JeiRecipeTypes.ALLOY_SMELTING, ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get());
        registration.addCraftingStation(JeiRecipeTypes.ALLOY_SMELTING, ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Part Builder click area (center arrow)
        registration.addRecipeClickArea(PartBuilderScreen.class, 88, 30, 24, 20, JeiRecipeTypes.PART_BUILDING);

        // Tinkering Table click area (center area)
        registration.addRecipeClickArea(TinkeringTableScreen.class, 90, 28, 28, 20,
                JeiRecipeTypes.TOOL_ASSEMBLY, JeiRecipeTypes.TOOL_UPGRADING);

        // Alloy Smelter click area (center flame and progress arrow)
        registration.addRecipeClickArea(AlloySmelterScreen.class, 76, 30, 28, 20, JeiRecipeTypes.ALLOY_SMELTING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Part Builder: 2 input slots (0..1), player inv (3..38)
        registration.addRecipeTransferHandler(PartBuilderMenu.class, ModMenuTypes.PART_BUILDER.get(),
                JeiRecipeTypes.PART_BUILDING, 0, 2, 3, 36);

        // Tinkering Table Assembly: 4 input slots (0..3), player inv (5..40)
        registration.addRecipeTransferHandler(TinkeringTableMenu.class, ModMenuTypes.TINKERING_TABLE.get(),
                JeiRecipeTypes.TOOL_ASSEMBLY, 0, 4, 5, 36);

        // Tinkering Table Upgrades: 2 input slots (0..1), player inv (5..40)
        registration.addRecipeTransferHandler(TinkeringTableMenu.class, ModMenuTypes.TINKERING_TABLE.get(),
                JeiRecipeTypes.TOOL_UPGRADING, 0, 2, 5, 36);

        // Alloy Smelter: 2 input slots (0..1), player inv (3..38)
        registration.addRecipeTransferHandler(AlloySmelterMenu.class, ModMenuTypes.ALLOY_SMELTER.get(),
                JeiRecipeTypes.ALLOY_SMELTING, 0, 2, 3, 36);
    }
}
