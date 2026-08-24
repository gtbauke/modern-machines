package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.mixin.SlotAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.inventory.Slot;

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
        this.slotStyle = slotStyle != null ? slotStyle : SlotStyle.ORE_UI;
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
     * Synchronizes backend Slot coordinates with this element's position relative to the main Screen.
     */
    public void syncSlotPosition() {
        if (this.slot == null) {
            return;
        }

        var win = findAncestorWindow();
        if (win != null && !win.isVisible()) {
            setSlotCoords(-9999, -9999);
            return;
        }

        int screenLeft = 0;
        int screenTop = 0;
        if (win != null) {
            UIElement root = win;
            while (root.getParent() != null) {
                root = root.getParent();
            }

            screenLeft = root.getPosition().x();
            screenTop = root.getPosition().y();
        }

        var absPos = getAbsolutePosition();
        int targetX = absPos.x() + 1 - screenLeft;
        int targetY = absPos.y() + 1 - screenTop;
        setSlotCoords(targetX, targetY);
    }

    public void syncSlotPosition(int screenLeft, int screenTop) {
        if (this.slot == null) {
            return;
        }

        var win = findAncestorWindow();
        if (win != null && !win.isVisible()) {
            setSlotCoords(-9999, -9999);
            return;
        }

        var absPos = getAbsolutePosition();
        int targetX = absPos.x() + 1 - screenLeft;
        int targetY = absPos.y() + 1 - screenTop;
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

    public Position getAbsolutePosition() {
        int x = this.bounds.position().x();
        int y = this.bounds.position().y();
        var current = this.parent;
        while (current != null) {
            x += current.getPosition().x() + current.getPadding().left();
            y += current.getPosition().y() + current.getPadding().top();
            current = current.getParent();
        }

        return new Position(x, y);
    }

    public Window findAncestorWindow() {
        var current = this.parent;
        while (current != null) {
            if (current instanceof Window win) {
                return win;
            }

            current = current.getParent();
        }

        return null;
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        var win = findAncestorWindow();
        if (win != null && !win.isVisible()) {
            return;
        }

        if (drawInsetWell) {
            if (slotStyle == SlotStyle.ORE_UI) {
                GUIRenderHelper.drawOreUISlot(graphics, absoluteBounds);
            } else if (slotStyle == SlotStyle.VANILLA) {
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
