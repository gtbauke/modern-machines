package io.github.gtbauke.modernmachines.integration.jei.category;

import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.integration.jei.JeiRecipeTypes;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolUpgradingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ToolUpgradingCategory implements IRecipeCategory<ToolUpgradingRecipe> {
    private final IDrawable icon;
    private final IDrawable background;

    public ToolUpgradingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TINKERING_TABLE.get()));
        this.background = guiHelper.createBlankDrawable(140, 50);
    }

    @Override
    public RecipeType<ToolUpgradingRecipe> getRecipeType() {
        return JeiRecipeTypes.TOOL_UPGRADING;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Tool Modifying & Upgrades");
    }

    @Override
    public int getWidth() {
        return 140;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolUpgradingRecipe recipe, IFocusGroup focuses) {
        // Base Tool Slot
        builder.addInputSlot(16, 12).add(recipe.inputTool());
        // Modifier Slot
        builder.addInputSlot(42, 12).add(recipe.modifierItem());
        // Upgraded Output Tool
        builder.addOutputSlot(106, 12).add(recipe.outputTool());
    }

    @Override
    public void draw(ToolUpgradingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 1. Draw Slot Outlines
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 15, 11, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 41, 11, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 105, 11, 18, 18);

        // 2. Draw plus and arrow
        Font font = Minecraft.getInstance().font;
        GuiRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("+"), 36, 16, 0xFF888888, false);
        GuiRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("➔"), 82, 16, 0xFF55FF55, false);

        // 3. Modifier effect description
        if (recipe.description() != null) {
            GuiRenderHelper.drawCenteredString(guiGraphics, font, recipe.description(), 70, 36, 0xFF00E5FF, false);
        }
    }
}
