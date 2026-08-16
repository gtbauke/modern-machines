package io.github.gtbauke.modernmachines.client.gui.debug;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexNode;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class GuiDebugInspector {
    public static boolean ENABLED = false;

    private static final int COLOR_BOUNDS = 0xAA00FF00;      // Bright Green wireframe
    private static final int COLOR_PADDING = 0x330088FF;     // Semi-transparent Blue
    private static final int COLOR_MARGIN = 0x33FFAA00;      // Semi-transparent Orange
    private static final int COLOR_HOVER_BG = 0xEE1A1C23;    // Dark Tooltip Box
    private static final int COLOR_HOVER_TEXT = 0xFF00FFFF;  // Cyan Text

    private GuiDebugInspector() {}

    public static void toggle() {
        ENABLED = !ENABLED;
    }

    public static void render(GuiGraphicsExtractor graphics, Font font, UiWidget root, int mouseX, int mouseY) {
        if (!ENABLED || root == null || !root.isVisible()) return;

        UiWidget hoveredWidget = renderWidgetNode(graphics, font, root, mouseX, mouseY);

        if (hoveredWidget != null) {
            drawHoverTag(graphics, font, hoveredWidget, mouseX, mouseY);
        }
    }

    private static UiWidget renderWidgetNode(GuiGraphicsExtractor graphics, Font font, UiWidget widget, int mouseX, int mouseY) {
        if (!widget.isVisible()) return null;

        FlexNode node = widget.getFlexNode();
        Bounds b = widget.getBounds();
        FlexInsets pad = node.getPadding();
        FlexInsets mar = node.getMargin();

        // 1. Draw Margins (Orange overlay around outer box)
        if (mar.top() > 0 || mar.bottom() > 0 || mar.left() > 0 || mar.right() > 0) {
            // Top Margin
            GuiRenderHelper.drawRect(graphics, b.x() - mar.left(), b.y() - mar.top(), b.width() + mar.horizontal(), mar.top(), COLOR_MARGIN);
            // Bottom Margin
            GuiRenderHelper.drawRect(graphics, b.x() - mar.left(), b.bottom(), b.width() + mar.horizontal(), mar.bottom(), COLOR_MARGIN);
            // Left Margin
            GuiRenderHelper.drawRect(graphics, b.x() - mar.left(), b.y(), mar.left(), b.height(), COLOR_MARGIN);
            // Right Margin
            GuiRenderHelper.drawRect(graphics, b.right(), b.y(), mar.right(), b.height(), COLOR_MARGIN);
        }

        // 2. Draw Padding (Blue overlay inside bounds)
        if (pad.top() > 0 || pad.bottom() > 0 || pad.left() > 0 || pad.right() > 0) {
            // Top Padding
            GuiRenderHelper.drawRect(graphics, b.x(), b.y(), b.width(), pad.top(), COLOR_PADDING);
            // Bottom Padding
            GuiRenderHelper.drawRect(graphics, b.x(), b.bottom() - pad.bottom(), b.width(), pad.bottom(), COLOR_PADDING);
            // Left Padding
            GuiRenderHelper.drawRect(graphics, b.x(), b.y() + pad.top(), pad.left(), b.height() - pad.vertical(), COLOR_PADDING);
            // Right Padding
            GuiRenderHelper.drawRect(graphics, b.right() - pad.right(), b.y() + pad.top(), pad.right(), b.height() - pad.vertical(), COLOR_PADDING);
        }

        // 3. Draw Content Bounds (Green 1px wireframe border)
        drawWireframe(graphics, b.x(), b.y(), b.width(), b.height(), COLOR_BOUNDS);

        UiWidget deepestHovered = b.contains(mouseX, mouseY) ? widget : null;

        for (UiWidget child : widget.getChildren()) {
            UiWidget childHovered = renderWidgetNode(graphics, font, child, mouseX, mouseY);
            if (childHovered != null) {
                deepestHovered = childHovered;
            }
        }

        return deepestHovered;
    }

    private static void drawWireframe(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
        GuiRenderHelper.drawRect(graphics, x, y, w, 1, color);             // Top
        GuiRenderHelper.drawRect(graphics, x, y + h - 1, w, 1, color);     // Bottom
        GuiRenderHelper.drawRect(graphics, x, y, 1, h, color);             // Left
        GuiRenderHelper.drawRect(graphics, x + w - 1, y, 1, h, color);     // Right
    }

    private static void drawHoverTag(GuiGraphicsExtractor graphics, Font font, UiWidget widget, int mouseX, int mouseY) {
        Bounds b = widget.getBounds();
        FlexNode node = widget.getFlexNode();
        String name = widget.getClass().getSimpleName();
        if (name.isEmpty()) name = "Widget";

        String line1 = name + " [" + b.width() + "x" + b.height() + "] @ (" + b.x() + ", " + b.y() + ")";
        String line2 = "Pad: " + node.getPadding().top() + "," + node.getPadding().right() + "," + node.getPadding().bottom() + "," + node.getPadding().left()
                + " | Mar: " + node.getMargin().top() + "," + node.getMargin().right() + "," + node.getMargin().bottom() + "," + node.getMargin().left();

        int w1 = font.width(line1);
        int w2 = font.width(line2);
        int tagW = Math.max(w1, w2) + 8;
        int tagH = 22;

        int tagX = mouseX + 12;
        int tagY = mouseY - 18;

        GuiRenderHelper.drawRect(graphics, tagX, tagY, tagW, tagH, COLOR_HOVER_BG);
        drawWireframe(graphics, tagX, tagY, tagW, tagH, 0xFF00E5FF);

        graphics.text(font, Component.literal(line1), tagX + 4, tagY + 3, COLOR_HOVER_TEXT, false);
        graphics.text(font, Component.literal(line2), tagX + 4, tagY + 12, 0xFFAAAAAA, false);
    }
}
