package io.github.gtbauke.modernmachines.client.gui.widget;

import java.lang.reflect.Field;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.mixin.SlotAccessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.inventory.Slot;
import sun.misc.Unsafe;

public class SlotWidget extends UiWidget {
    private static final Unsafe UNSAFE;
    private static final long X_OFFSET;
    private static final long Y_OFFSET;

    static {
        Unsafe unsafe = null;
        long xOff = -1;
        long yOff = -1;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);

            Field xField = Slot.class.getDeclaredField("x");
            Field yField = Slot.class.getDeclaredField("y");
            xOff = unsafe.objectFieldOffset(xField);
            yOff = unsafe.objectFieldOffset(yField);
        } catch (Throwable ignored) {}

        UNSAFE = unsafe;
        X_OFFSET = xOff;
        Y_OFFSET = yOff;
    }

    private final Slot slot;
    private final int slotIndex;
    private boolean drawInsetWell = true;

    public SlotWidget(Slot slot) {
        this.slot = slot;
        this.slotIndex = slot != null ? slot.index : -1;
        flexNode.setSize(18, 18);
    }

    public static SlotWidget of(Slot slot) {
        return new SlotWidget(slot);
    }

    public static SlotWidget of(Slot slot, boolean drawInsetWell) {
        SlotWidget widget = new SlotWidget(slot);
        widget.setDrawInsetWell(drawInsetWell);
        return widget;
    }

    public Slot getSlot() {
        return slot;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public SlotWidget setDrawInsetWell(boolean draw) {
        this.drawInsetWell = draw;
        return this;
    }

    public void syncSlotPosition(int screenLeft, int screenTop) {
        if (this.slot != null) {
            int targetX = -9999;
            int targetY = -9999;
            if (isVisible()) {
                Bounds b = getBounds();
                targetX = b.x() + 1 - screenLeft;
                targetY = b.y() + 1 - screenTop;
            }

            if (UNSAFE != null && X_OFFSET != -1 && Y_OFFSET != -1) {
                UNSAFE.putInt(this.slot, X_OFFSET, targetX);
                UNSAFE.putInt(this.slot, Y_OFFSET, targetY);
            } else if (this.slot instanceof SlotAccessor accessor) {
                try {
                    accessor.setX(targetX);
                    accessor.setY(targetY);
                } catch (Throwable ignored) {}
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();

        if (drawInsetWell) {
            NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.SLOT, b);
        }

        if (hovered) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 84.0F, 0.0F, 18, 18, 256, 256);
        }
    }
}
