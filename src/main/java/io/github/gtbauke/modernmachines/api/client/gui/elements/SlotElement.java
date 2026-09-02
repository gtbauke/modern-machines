package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.mixin.SlotAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SlotElement extends UIElement {
    public static final int SLOT_SIZE = 18;

    public enum SlotStyle {
        ORE_UI,
        VANILLA,
        TEXTURE_NINE_SLICE
    }

    private Slot slot;
    private int slotIndex = -1;
    private boolean drawInsetWell = true;
    private SlotStyle slotStyle = SlotStyle.ORE_UI;
    private IconElement icon = null;
    private Supplier<Item> ghostIconSupplier = null;

    public SlotElement(Slot slot) {
        super(Position.ZERO, new Size(SLOT_SIZE, SLOT_SIZE), Padding.ZERO);
        this.slot = slot;
        this.slotIndex = slot != null ? slot.index : -1;
    }

    public SlotElement() {
        this(null);
    }

    public Slot getSlot() {
        return slot;
    }

    public SlotElement setSlot(Slot slot) {
        this.slot = slot;
        this.slotIndex = slot != null ? slot.index : -1;
        return this;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public boolean isDrawInsetWell() {
        return drawInsetWell;
    }

    public SlotElement setDrawInsetWell(boolean drawInsetWell) {
        this.drawInsetWell = drawInsetWell;
        return this;
    }

    public SlotStyle getSlotStyle() {
        return slotStyle;
    }

    public SlotElement setSlotStyle(SlotStyle slotStyle) {
        this.slotStyle = slotStyle != null ? slotStyle : SlotStyle.ORE_UI;
        return this;
    }

    public SlotElement withGhostIcon(Supplier<Item> iconSupplier) {
        this.ghostIconSupplier = iconSupplier;
        this.icon = new IconElement(ItemStack.EMPTY).setColorMode(IconElement.ColorMode.GRAYSCALE).setOpacity(0.4f);
        return this;
    }

    public SlotElement withGhostIcon(Item icon) {
        return withGhostIcon(() -> icon);
    }

    @Override
    public void calculateLayout() {
        super.calculateLayout();
        syncSlotPosition();
    }

    public void syncSlotPosition() {
        if (this.slot == null) {
            return;
        }

        if (!this.isEffectivelyVisible()) {
            setSlotCoords(-9999, -9999);
            return;
        }

        var rootPos = getRootPosition();
        int targetX = this.left() + 1 - rootPos.x();
        int targetY = this.top() + 1 - rootPos.y();
        setSlotCoords(targetX, targetY);
    }

    private void setSlotCoords(int x, int y) {
        if (this.slot instanceof SlotAccessor accessor) {
            try {
                accessor.setX(x);
                accessor.setY(y);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (!this.isEffectivelyVisible()) {
            setSlotCoords(-9999, -9999);
            return;
        }

        syncSlotPosition();

        if (drawInsetWell) {
            if (slotStyle == SlotStyle.ORE_UI) {
                GUIRenderHelper.drawOreUISlot(graphics, this.getBounds());
            } else if (slotStyle == SlotStyle.VANILLA) {
                GUIRenderHelper.drawVanillaSlot(graphics, this.getBounds());
            } else {
                NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.SLOT, this.getBounds());
            }
        }

        if (slot == null || !slot.hasItem()) {
            if (ghostIconSupplier != null) {
                var item = ghostIconSupplier.get();
                if (icon == null) {
                    icon = new IconElement(item).setColorMode(IconElement.ColorMode.GRAYSCALE).setOpacity(0.4f);
                } else {
                    icon.setItem(item);
                }
            }

            if (icon != null && !icon.getItemStack().isEmpty()) {
                icon.setPosition(new Position(this.left() + 1, this.top() + 1));
                icon.render(graphics, mousePos, partialTick);
            }
        }

        super.render(graphics, mousePos, partialTick);
    }
}
