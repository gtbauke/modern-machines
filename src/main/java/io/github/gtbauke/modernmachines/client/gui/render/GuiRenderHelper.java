package io.github.gtbauke.modernmachines.client.gui.render;

import java.util.List;
import java.util.Optional;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GuiRenderHelper {

    public static void drawRect(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + h, color);
    }

    public static void drawRectOutline(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void drawHorizontalLine(GuiGraphicsExtractor graphics, int x1, int x2, int y, int color) {
        graphics.fill(Math.min(x1, x2), y, Math.max(x1, x2), y + 1, color);
    }

    public static void drawLine(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int currX = x1;
        int currY = y1;

        while (true) {
            graphics.fill(currX, currY, currX + 1, currY + 1, color);
            if (currX == x2 && currY == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currY += sy;
            }
        }
    }

    public static void drawCenteredString(GuiGraphicsExtractor graphics, Font font, Component text, int cx, int y, int color, boolean shadow) {
        graphics.text(font, text, cx - font.width(text) / 2, y, color, shadow);
    }

    public static void drawDropShadow(GuiGraphicsExtractor graphics, Bounds bounds, int shadowColor, int radius) {
        int x = bounds.x();
        int y = bounds.y();
        int w = bounds.width();
        int h = bounds.height();

        for (int i = 1; i <= radius; i++) {
            int alpha = (shadowColor >>> 24) * (radius - i + 1) / (radius * 2);
            int layerColor = (alpha << 24) | (shadowColor & 0x00FFFFFF);
            // Right and bottom shadow expansion
            graphics.fill(x + i, y + h, x + w + i, y + h + 1, layerColor);
            graphics.fill(x + w, y + i, x + w + 1, y + h + i, layerColor);
        }
    }

    public static void drawBevel(GuiGraphicsExtractor graphics, Bounds bounds, int topLight, int botDark) {
        int x = bounds.x();
        int y = bounds.y();
        int w = bounds.width();
        int h = bounds.height();

        // Top & Left
        graphics.fill(x, y, x + w, y + 1, topLight);
        graphics.fill(x, y, x + 1, y + h, topLight);

        // Bottom & Right
        graphics.fill(x, y + h - 1, x + w, y + h, botDark);
        graphics.fill(x + w - 1, y, x + w, y + h, botDark);
    }

    public static void drawTooltip(GuiGraphicsExtractor graphics, Font font, List<Component> text, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(font, text, Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
    }
}
