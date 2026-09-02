package io.github.gtbauke.modernmachines.integration.jei.category;

import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.integration.jei.JeiRecipeTypes;
import io.github.gtbauke.modernmachines.integration.jei.recipe.ToolAssemblyRecipe;
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

public class ToolAssemblyCategory implements IRecipeCategory<ToolAssemblyRecipe> {
    private final IDrawable icon;
    private final IDrawable background;

    public ToolAssemblyCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TINKERING_TABLE.get()));
        this.background = guiHelper.createBlankDrawable(154, 56);
    }

    @Override
    public @NonNull IRecipeType<ToolAssemblyRecipe> getRecipeType() {
        return JeiRecipeTypes.TOOL_ASSEMBLY;
    }

    @Override
    public @NonNull Component getTitle() {
        return Component.literal("Tool Assembly");
    }

    @Override
    public int getWidth() {
        return 154;
    }

    @Override
    public int getHeight() {
        return 56;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolAssemblyRecipe recipe, @NonNull IFocusGroup focuses) {
        // Head Slot
        builder.addInputSlot(14, 8).add(recipe.head());
        // Handle Slot
        builder.addInputSlot(14, 30).add(recipe.handle());
        // Binding Slot
        builder.addInputSlot(36, 19).add(recipe.binding());
        // Attachment Slot
        if (!recipe.attachment().isEmpty()) {
            builder.addInputSlot(58, 19).add(recipe.attachment());
        }
        // Output Modular Tool
        builder.addOutputSlot(120, 19).add(recipe.outputTool());
    }

    @Override
    public void draw(ToolAssemblyRecipe recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 1. Draw Slot Outlines
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 13, 7, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 13, 29, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 35, 18, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 57, 18, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 119, 18, 18, 18);

        // 2. Draw Arrow
        Font font = Minecraft.getInstance().font;
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("➔"), new Position(95, 23), 0xFF55FF55, false);
    }
}
