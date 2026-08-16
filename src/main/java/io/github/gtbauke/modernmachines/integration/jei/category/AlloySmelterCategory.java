package io.github.gtbauke.modernmachines.integration.jei.category;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.integration.jei.JeiRecipeTypes;
import io.github.gtbauke.modernmachines.integration.jei.recipe.AlloySmeltingRecipe;
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
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

public class AlloySmelterCategory implements IRecipeCategory<AlloySmeltingRecipe> {
    private final IDrawable icon;
    private final IDrawable background;

    public AlloySmelterCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get()));
        this.background = guiHelper.createBlankDrawable(140, 60);
    }

    @Override
    public @NonNull IRecipeType<AlloySmeltingRecipe> getRecipeType() {
        return JeiRecipeTypes.ALLOY_SMELTING;
    }

    @Override
    public @NonNull Component getTitle() {
        return Component.literal("Basic Alloy Smelting");
    }

    @Override
    public int getWidth() {
        return 140;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloySmeltingRecipe recipe, @NonNull IFocusGroup focuses) {
        builder.addInputSlot(12, 4).add(recipe.inputA());
        builder.addInputSlot(32, 4).add(recipe.inputB());
        builder.addInputSlot(22, 38).add(new ItemStack(Items.COAL));
        builder.addOutputSlot(102, 21).add(recipe.output());
    }

    @Override
    public void draw(AlloySmeltingRecipe recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 11, 3, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 31, 3, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 21, 37, 18, 18);
        NineSliceRenderer.drawNineSlice(guiGraphics, NineSliceRenderer.SLOT, 101, 20, 18, 18);

        Font font = Minecraft.getInstance().font;
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("🔥"), new Position(31, 26), 0xFFFF6600, false);
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal("➔"), new Position(70, 26), 0xFFFF8800, false);

        String timeText = (recipe.processTime() / 20) + "s";
        GUIRenderHelper.drawCenteredString(guiGraphics, font, Component.literal(timeText), new Position(70, 39), 0xFFAAAAAA, false);
    }
}
