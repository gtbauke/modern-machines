package io.github.gtbauke.modernmachines.integration.jei.category;

import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.integration.jei.JeiRecipeTypes;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolUpgradingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class ToolUpgradingCategory implements IRecipeCategory<ToolUpgradingRecipe> {
    private final IDrawable icon;
    private final IDrawable background;

    public ToolUpgradingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TINKERING_TABLE.get()));
        this.background = guiHelper.createBlankDrawable(140, 50);
    }

    @Override
    public @NonNull IRecipeType<ToolUpgradingRecipe> getRecipeType() {
        return JeiRecipeTypes.TOOL_UPGRADING;
    }

    @Override
    public @NonNull Component getTitle() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, ToolUpgradingRecipe recipe, @NonNull IFocusGroup focuses) {
        // Base Tool Slot
        builder.addInputSlot(16, 12).add(recipe.inputTool());
        // Modifier Slot
        builder.addInputSlot(42, 12).add(recipe.modifierItem());
        // Upgraded Output Tool
        builder.addOutputSlot(106, 12).add(recipe.outputTool());
    }

    @Override
    public void draw(ToolUpgradingRecipe recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 1. Draw Slot Outlines
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 15, 11, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 41, 11, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 105, 11, 18, 18);

        // 2. Draw plus and arrow
        Font font = Minecraft.getInstance().font;
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("+"), new Position(36, 16), 0xFF888888, false);
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("➔"), new Position(82, 16), 0xFF55FF55, false);

        // 3. Modifier effect description
        if (recipe.description() != null) {
            GUIRenderHelper.drawCenteredString(guiGraphics, font, recipe.description(), new Position(70, 36), 0xFF00E5FF, false);
        }
    }
}
