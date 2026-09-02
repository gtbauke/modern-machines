package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IconElement extends UIElement {

    public enum ColorMode {
        RGB,
        GRAYSCALE
    }

    private ItemStack itemStack = ItemStack.EMPTY;
    private float opacity = 1.0f;
    private ColorMode colorMode = ColorMode.RGB;

    public IconElement(ItemStack itemStack) {
        super(Position.ZERO, new Size(16, 16), Padding.ZERO);
        this.itemStack = itemStack != null ? itemStack : ItemStack.EMPTY;
    }

    public IconElement(Item item) {
        this(item != null ? new ItemStack(item) : ItemStack.EMPTY);
    }

    public IconElement(String itemId) {
        this(resolveItem(itemId));
    }

    public IconElement() {
        this(ItemStack.EMPTY);
    }

    private static ItemStack resolveItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            var holder = BuiltInRegistries.ITEM.get(Identifier.parse(itemId));
            if (holder.isPresent()) {
                return new ItemStack(holder.get().value());
            }
        } catch (Throwable ignored) {
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public IconElement setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack != null ? itemStack : ItemStack.EMPTY;
        return this;
    }

    public IconElement setItem(Item item) {
        return setItemStack(item != null ? new ItemStack(item) : ItemStack.EMPTY);
    }

    public IconElement setItem(String itemId) {
        return setItemStack(resolveItem(itemId));
    }

    public float getOpacity() {
        return opacity;
    }

    public IconElement setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
        return this;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    public IconElement setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode != null ? colorMode : ColorMode.RGB;
        return this;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (!this.isEffectivelyVisible() || itemStack.isEmpty()) {
            return;
        }

        super.render(graphics, mousePos, partialTick);

        int drawX = this.left() + (this.width() - 16) / 2;
        int drawY = this.top() + (this.height() - 16) / 2;

        graphics.fakeItem(itemStack, drawX, drawY);

        if (colorMode == ColorMode.GRAYSCALE) {
            graphics.fill(drawX, drawY, drawX + 16, drawY + 16, 0x88404040);
        }

        if (opacity < 0.99f) {
            int alphaInt = Math.max(0, Math.min(255, (int) ((1.0f - opacity) * 200)));
            int maskColor = (alphaInt << 24) | 0x1E1E20;
            graphics.fill(drawX, drawY, drawX + 16, drawY + 16, maskColor);
        }
    }
}
