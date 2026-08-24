package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class IconElement extends UIElement {
    public enum ColorMode {
        RGB,
        GRAYSCALE
    }

    private ItemStack itemStack = ItemStack.EMPTY;
    private float opacity = 1.0f;
    private ColorMode colorMode = ColorMode.RGB;

    public IconElement(ItemStack itemStack) {
        super(new Bounds(Position.ZERO, new Size(16, 16)));
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
        markDirty();
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
        markDirty();
        return this;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    public IconElement setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode != null ? colorMode : ColorMode.RGB;
        markDirty();
        return this;
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        if (itemStack.isEmpty()) {
            return;
        }

        int x = absoluteBounds.position().x() + (absoluteBounds.size().width() - 16) / 2;
        int y = absoluteBounds.position().y() + (absoluteBounds.size().height() - 16) / 2;

        graphics.fakeItem(itemStack, x, y);

        // Grayscale post-filter effect overlay
        if (colorMode == ColorMode.GRAYSCALE) {
            graphics.fill(x, y, x + 16, y + 16, 0x88404040);
        }

        // Opacity dimming / fade overlay
        if (opacity < 0.99f) {
            int alphaInt = Math.max(0, Math.min(255, (int) ((1.0f - opacity) * 200)));
            int maskColor = (alphaInt << 24) | 0x1E1E20;
            graphics.fill(x, y, x + 16, y + 16, maskColor);
        }
    }
}
