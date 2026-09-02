package io.github.gtbauke.modernmachines.integration.jei.category;

import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.integration.jei.JeiRecipeTypes;
import io.github.gtbauke.modernmachines.integration.jei.recipe.PartBuilderRecipe;
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

public class PartBuilderCategory implements IRecipeCategory<PartBuilderRecipe> {
    private final IDrawable icon;
    private final IDrawable background;

    public PartBuilderCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PART_BUILDER.get()));
        this.background = guiHelper.createBlankDrawable(140, 48);
    }

    @Override
    public @NonNull IRecipeType<PartBuilderRecipe> getRecipeType() {
        return JeiRecipeTypes.PART_BUILDING;
    }

    @Override
    public @NonNull Component getTitle() {
        return Component.literal("Part Building");
    }

    @Override
    public int getWidth() {
        return 140;
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PartBuilderRecipe recipe, IFocusGroup focuses) {
        // Pattern Slot
        builder.addInputSlot(16, 15).add(recipe.pattern());
        // Material Slot
        builder.addInputSlot(44, 15).addItemStacks(recipe.materialInputs());
        // Output Slot
        builder.addOutputSlot(106, 15).add(recipe.outputPart());
    }

    @Override
    public void draw(PartBuilderRecipe recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 1. Draw Slot Outlines
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 15, 14, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 43, 14, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 105, 14, 18, 18);

        // 2. Draw plus and arrow
        Font font = Minecraft.getInstance().font;
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("+"), new Position(38, 19), 0xFF888888, false);
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("➔"), new Position(84, 19), 0xFF55FF55, false);

        // 3. Draw Material Cost Tag
        String costText = "Cost: " + recipe.materialCost() + "x";
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal(costText), new Position(52, 35), 0xFFFFAA00, false);
    }
}
