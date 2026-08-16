package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.mixin.SlotAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.inventory.Slot;

public class SlotElement extends UIElement {
    public static final int SLOT_SIZE = 18;

    public enum SlotStyle {
        VANILLA,
        TEXTURE_NINE_SLICE
    }

    private Slot slot;
    private int slotIndex = -1;
    private boolean drawInsetWell = true;
    private SlotStyle slotStyle = SlotStyle.VANILLA;
    private int ghostIconU = -1;
    private int ghostIconV = -1;

    public SlotElement(Bounds bounds, Slot slot) {
        super(bounds);
        this.slot = slot;
        this.slotIndex = slot != null ? slot.index : -1;
    }

    public SlotElement(Position position, Slot slot) {
        this(new Bounds(position, new Size(SLOT_SIZE, SLOT_SIZE)), slot);
    }

    public SlotElement(Slot slot) {
        this(new Position(slot != null ? slot.x - 1 : 0, slot != null ? slot.y - 1 : 0), slot);
    }

    public SlotElement(Position position) {
        this(position, null);
    }

    public SlotElement(int x, int y) {
        this(new Position(x, y), null);
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
        this.slotIndex = slot != null ? slot.index : -1;
        markDirty();
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
        this.slotStyle = slotStyle != null ? slotStyle : SlotStyle.VANILLA;
        markDirty();
        return this;
    }

    public SlotElement withGhostIcon(int u, int v) {
        this.ghostIconU = u;
        this.ghostIconV = v;
        return this;
    }

    public int getGhostIconU() {
        return ghostIconU;
    }

    public int getGhostIconV() {
        return ghostIconV;
    }

    @Override
    public void calculateLayout() {
        super.calculateLayout();
        syncSlotPosition();
    }

    /**
     * Synchronizes backend Slot coordinates with this element's position relative to the Window.
     */
    public void syncSlotPosition() {
        if (this.slot != null) {
            Position posRelToWindow = getPositionRelativeToWindow();
            int targetX = posRelToWindow.x() + 1;
            int targetY = posRelToWindow.y() + 1;

            if (this.slot instanceof SlotAccessor accessor) {
                try {
                    accessor.setX(targetX);
                    accessor.setY(targetY);
                } catch (Throwable ignored) {}
            }
        }
    }

    private Position getPositionRelativeToWindow() {
        int x = this.bounds.position().x();
        int y = this.bounds.position().y();
        UIElement current = this.parent;
        while (current != null) {
            if (current instanceof Window) {
                break;
            }
            x += current.getPosition().x() + current.getPadding().left();
            y += current.getPosition().y() + current.getPadding().top();
            current = current.getParent();
        }
        return new Position(x, y);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        if (drawInsetWell) {
            if (slotStyle == SlotStyle.VANILLA) {
                GUIRenderHelper.drawVanillaSlot(graphics, absoluteBounds);
            } else {
                NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.SLOT, absoluteBounds);
            }
        }

        if (ghostIconU >= 0 && ghostIconV >= 0 && (slot == null || !slot.hasItem())) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                NineSliceRenderer.GUI_ATLAS,
                absoluteBounds.position().x() + 1,
                absoluteBounds.position().y() + 1,
                (float) ghostIconU,
                (float) ghostIconV,
                16,
                16,
                256,
                256
            );
        }
    }
}
