package io.github.gtbauke.modernmachines.client.gui.core.render;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class GUIRenderHelper {
    public static final int ORE_BG_PRIMARY = 0xFF1E1E20;
    public static final int ORE_BG_SECONDARY = 0xFF262629;
    public static final int ORE_BORDER_DARK = 0xFF141416;
    public static final int ORE_BORDER_LIGHT = 0xFF3E3E44;
    public static final int ORE_SLOT_BG = 0xFF121214;
    public static final int ORE_SLOT_BORDER = 0xFF2D2D32;
    public static final int ORE_SLOT_HIGHLIGHT = 0xFF44444A;
    public static final int ORE_GREEN_PRIMARY = 0xFF3C8527;
    public static final int ORE_GREEN_HOVER = 0xFF4EA336;
    public static final int ORE_GREEN_PRESSED = 0xFF2D661D;
    public static final int ORE_GREEN_BORDER = 0xFF5FBF43;
    public static final int ORE_BUTTON_BG = 0xFF2F3033;
    public static final int ORE_BUTTON_HOVER = 0xFF424347;
    public static final int ORE_BUTTON_PRESSED = 0xFF202123;
    public static final int ORE_BUTTON_BORDER = 0xFF505155;
    public static final int ORE_TEXT_TITLE = 0xFFFFFFFF;
    public static final int ORE_TEXT_MUTED = 0xFFA0A0A5;
    public static final int ORE_TEXT_DARK = 0xFF606065;

    public static final int VANILLA_BG = 0xFFC6C6C6;
    public static final int VANILLA_SLOT_BG = 0xFF8B8B8B;
    public static final int VANILLA_SLOT_DARK = 0xFF373737;
    public static final int VANILLA_SLOT_LIGHT = 0xFFFFFFFF;
    public static final int VANILLA_BORDER_DARK = 0xFF373737;
    public static final int VANILLA_BORDER_SHADOW = 0xFF555555;
    public static final int VANILLA_BORDER_LIGHT = 0xFFFFFFFF;

    public static void drawRect(GuiGraphicsExtractor graphics, Bounds bounds, int color) {
        graphics.fill(bounds.position().x(), bounds.position().y(), bounds.right(), bounds.bottom(), color);
    }

    public static void drawRectOutline(GuiGraphicsExtractor graphics, Bounds bounds, int color) {
        graphics.fill(bounds.position().x(), bounds.position().y(), bounds.right(), bounds.position().y() + 1, color);
        graphics.fill(bounds.position().x(), bounds.bottom() - 1, bounds.right(), bounds.bottom(), color);
        graphics.fill(bounds.position().x(), bounds.position().y(), bounds.position().x() + 1, bounds.bottom(), color);
        graphics.fill(bounds.right() - 1, bounds.position().y(), bounds.right(), bounds.bottom(), color);
    }

    public static void drawLine(GuiGraphicsExtractor graphics, Position start, Position end, int color) {
        graphics.fill(start.x(), start.y(), end.x(), end.y(), color);
    }

    public static void drawCenteredString(GuiGraphicsExtractor graphics, Font font, Component text, Position position, int color, boolean shadow) {
        graphics.text(font, text, position.x() - font.width(text) / 2, position.y(), color, shadow);
    }

    public static void drawDropShadow(GuiGraphicsExtractor graphics, Bounds bounds, int shadowColor, int radius) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var width = bounds.size().width();
        var height = bounds.size().height();

        for (var i = 1; i <= radius; ++i) {
            var alpha = (shadowColor >>> 24) * (radius - i + 1) / (radius * 2);
            var layerColor = (alpha << 24) | (shadowColor & 0x00FFFFFF);

            graphics.fill(x + i, y + height, x + width + i, y + height + 1, layerColor);
            graphics.fill(x + width, y + i, x + width + 1, y + height + i, layerColor);
        }
    }

    public static void drawBevel(GuiGraphicsExtractor graphics, Bounds bounds, int topLight, int botDark) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var width = bounds.size().width();
        var height = bounds.size().height();

        graphics.fill(x, y, x + width, y + 1, topLight);
        graphics.fill(x, y, x + 1, y + height, topLight);
        graphics.fill(x, y + height - 1, x + width, y + height, botDark);
        graphics.fill(x + width - 1, y, x + width, y + height, botDark);
    }

    public static void drawOreUIBackground(GuiGraphicsExtractor graphics, Bounds bounds) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var right = bounds.right();
        var bottom = bounds.bottom();

        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, ORE_BG_PRIMARY);
        drawRectOutline(graphics, bounds, ORE_BORDER_DARK);
        graphics.fill(x + 1, y + 1, right - 1, y + 2, ORE_BORDER_LIGHT);
        graphics.fill(x + 1, y + 1, x + 2, bottom - 1, ORE_BORDER_LIGHT);
    }

    public static void drawOreUIPanel(GuiGraphicsExtractor graphics, Bounds bounds) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var right = bounds.right();
        var bottom = bounds.bottom();

        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, ORE_BG_SECONDARY);
        drawRectOutline(graphics, bounds, ORE_BORDER_DARK);
        graphics.fill(x + 1, y + 1, right - 1, y + 2, ORE_BORDER_LIGHT);
    }

    public static void drawOreUISlot(GuiGraphicsExtractor graphics, Bounds bounds) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var right = bounds.right();
        var bottom = bounds.bottom();

        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, ORE_SLOT_BG);
        graphics.fill(x, y, right, y + 1, ORE_BORDER_DARK);
        graphics.fill(x, y, x + 1, bottom, ORE_BORDER_DARK);
        graphics.fill(x + 1, bottom - 1, right, bottom, ORE_SLOT_BORDER);
        graphics.fill(right - 1, y + 1, right, bottom, ORE_SLOT_BORDER);
    }

    public static void drawOreUIButton(GuiGraphicsExtractor graphics, Bounds bounds, boolean hovered, boolean pressed, boolean primaryGreen) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var right = bounds.right();
        var bottom = bounds.bottom();

        int bg;
        int border;
        int topHighlight;

        if (primaryGreen) {
            bg = pressed ? ORE_GREEN_PRESSED : (hovered ? ORE_GREEN_HOVER : ORE_GREEN_PRIMARY);
            border = ORE_BORDER_DARK;
            topHighlight = ORE_GREEN_BORDER;
        } else {
            bg = pressed ? ORE_BUTTON_PRESSED : (hovered ? ORE_BUTTON_HOVER : ORE_BUTTON_BG);
            border = ORE_BORDER_DARK;
            topHighlight = hovered ? ORE_BORDER_LIGHT : ORE_BUTTON_BORDER;
        }

        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, bg);
        drawRectOutline(graphics, bounds, border);
        if (!pressed) {
            graphics.fill(x + 1, y + 1, right - 1, y + 2, topHighlight);
        }
    }

    public static void drawOreUITab(GuiGraphicsExtractor graphics, Bounds bounds, boolean leftSided, boolean active, boolean hovered) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var right = bounds.right();
        var bottom = bounds.bottom();

        int bg = active ? ORE_BG_PRIMARY : (hovered ? ORE_BUTTON_HOVER : ORE_BG_SECONDARY);

        if (!leftSided) {
            graphics.fill(x, y + 1, right - 1, bottom - 1, bg);
            graphics.fill(x, y, right - 1, y + 1, ORE_BORDER_DARK);
            graphics.fill(right - 1, y + 1, right, bottom - 1, ORE_BORDER_DARK);
            graphics.fill(x, bottom - 1, right - 1, bottom, ORE_BORDER_DARK);

            if (active) {
                graphics.fill(right - 3, y + 2, right - 1, bottom - 2, ORE_GREEN_PRIMARY);
            }
        } else {
            graphics.fill(x + 1, y + 1, right, bottom - 1, bg);
            graphics.fill(x + 1, y, right, y + 1, ORE_BORDER_DARK);
            graphics.fill(x, y + 1, x + 1, bottom - 1, ORE_BORDER_DARK);
            graphics.fill(x + 1, bottom - 1, right, bottom, ORE_BORDER_DARK);

            if (active) {
                graphics.fill(x + 1, y + 2, x + 3, bottom - 2, ORE_GREEN_PRIMARY);
            }
        }
    }

    public static void drawOreUIProgressBar(GuiGraphicsExtractor graphics, Bounds bounds, double progress, int fillColor) {
        var x = bounds.position().x();
        var y = bounds.position().y();
        var right = bounds.right();
        var bottom = bounds.bottom();

        graphics.fill(x, y, right, bottom, ORE_SLOT_BG);
        drawRectOutline(graphics, bounds, ORE_BORDER_DARK);

        int innerWidth = bounds.size().width() - 2;
        int fillWidth = (int) Math.round(innerWidth * Math.min(1.0, Math.max(0.0, progress)));
        if (fillWidth > 0) {
            graphics.fill(x + 1, y + 1, x + 1 + fillWidth, bottom - 1, fillColor);
        }
    }

    public static void drawVanillaSlot(GuiGraphicsExtractor graphics, Bounds bounds) {
        int x = bounds.position().x();
        int y = bounds.position().y();
        int right = bounds.right();
        int bottom = bounds.bottom();

        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, VANILLA_SLOT_BG);
        graphics.fill(x, y, right, y + 1, VANILLA_SLOT_DARK);
        graphics.fill(x, y, x + 1, bottom, VANILLA_SLOT_DARK);
        graphics.fill(x + 1, bottom - 1, right, bottom, VANILLA_SLOT_LIGHT);
        graphics.fill(right - 1, y + 1, right, bottom, VANILLA_SLOT_LIGHT);
    }

    public static void drawVanillaTab(GuiGraphicsExtractor graphics, Bounds bounds, boolean leftSided) {
        int x = bounds.position().x();
        int y = bounds.position().y();
        int right = bounds.right();
        int bottom = bounds.bottom();

        if (!leftSided) {
            graphics.fill(x, y + 1, right - 1, bottom - 1, VANILLA_BG);
            graphics.fill(x, y, right - 1, y + 1, VANILLA_BORDER_DARK);
            graphics.fill(right - 1, y + 1, right, bottom - 1, VANILLA_BORDER_DARK);
            graphics.fill(x, bottom - 1, right - 1, bottom, VANILLA_BORDER_DARK);
            graphics.fill(x, y + 1, right - 2, y + 2, VANILLA_BORDER_LIGHT);
            graphics.fill(right - 2, y + 2, right - 1, bottom - 2, VANILLA_BORDER_SHADOW);
            graphics.fill(x, bottom - 2, right - 2, bottom - 1, VANILLA_BORDER_SHADOW);
        } else {
            graphics.fill(x + 1, y + 1, right, bottom - 1, VANILLA_BG);
            graphics.fill(x + 1, y, right, y + 1, VANILLA_BORDER_DARK);
            graphics.fill(x, y + 1, x + 1, bottom - 1, VANILLA_BORDER_DARK);
            graphics.fill(x + 1, bottom - 1, right, bottom, VANILLA_BORDER_DARK);
            graphics.fill(x + 2, y + 1, right, y + 2, VANILLA_BORDER_LIGHT);
            graphics.fill(x + 1, y + 2, x + 2, bottom - 2, VANILLA_BORDER_LIGHT);
            graphics.fill(x + 2, bottom - 2, right, bottom - 1, VANILLA_BORDER_SHADOW);
        }
    }

    public static void drawTooltip(GuiGraphicsExtractor graphics, Font font, List<Component> text, Position mousePosition) {
        graphics.setTooltipForNextFrame(font, text, Optional.empty(), ItemStack.EMPTY, mousePosition.x(), mousePosition.y());
    }
}
