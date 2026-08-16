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

    public static void drawVanillaSlot(GuiGraphicsExtractor graphics, Bounds bounds) {
        int x = bounds.position().x();
        int y = bounds.position().y();
        int right = bounds.right();
        int bottom = bounds.bottom();

        // Inner 16x16 slot well (Classic Vanilla #8B8B8B)
        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, VANILLA_SLOT_BG);

        // Top & Left dark sunken shadow (#373737)
        graphics.fill(x, y, right, y + 1, VANILLA_SLOT_DARK);
        graphics.fill(x, y, x + 1, bottom, VANILLA_SLOT_DARK);

        // Bottom & Right light highlight (#FFFFFF)
        graphics.fill(x + 1, bottom - 1, right, bottom, VANILLA_SLOT_LIGHT);
        graphics.fill(right - 1, y + 1, right, bottom, VANILLA_SLOT_LIGHT);
    }

    public static void drawVanillaTab(GuiGraphicsExtractor graphics, Bounds bounds, boolean leftSided) {
        int x = bounds.position().x();
        int y = bounds.position().y();
        int right = bounds.right();
        int bottom = bounds.bottom();

        if (!leftSided) {
            // Right-sided tab
            // 1. Background fill
            graphics.fill(x, y + 1, right - 1, bottom - 1, VANILLA_BG);

            // 2. Dark outer outline (#373737)
            graphics.fill(x, y, right - 1, y + 1, VANILLA_BORDER_DARK);              // Top outer
            graphics.fill(right - 1, y + 1, right, bottom - 1, VANILLA_BORDER_DARK);  // Right outer
            graphics.fill(x, bottom - 1, right - 1, bottom, VANILLA_BORDER_DARK);     // Bottom outer

            // 3. Inner 3D highlight & shadow
            graphics.fill(x, y + 1, right - 2, y + 2, VANILLA_BORDER_LIGHT);          // Top inner light
            graphics.fill(right - 2, y + 2, right - 1, bottom - 2, VANILLA_BORDER_SHADOW); // Right inner shadow
            graphics.fill(x, bottom - 2, right - 2, bottom - 1, VANILLA_BORDER_SHADOW);    // Bottom inner shadow
        } else {
            // Left-sided tab
            // 1. Background fill
            graphics.fill(x + 1, y + 1, right, bottom - 1, VANILLA_BG);

            // 2. Dark outer outline (#373737)
            graphics.fill(x + 1, y, right, y + 1, VANILLA_BORDER_DARK);              // Top outer
            graphics.fill(x, y + 1, x + 1, bottom - 1, VANILLA_BORDER_DARK);          // Left outer
            graphics.fill(x + 1, bottom - 1, right, bottom, VANILLA_BORDER_DARK);     // Bottom outer

            // 3. Inner 3D highlight & shadow
            graphics.fill(x + 2, y + 1, right, y + 2, VANILLA_BORDER_LIGHT);          // Top inner light
            graphics.fill(x + 1, y + 2, x + 2, bottom - 2, VANILLA_BORDER_LIGHT);     // Left inner light
            graphics.fill(x + 2, bottom - 2, right, bottom - 1, VANILLA_BORDER_SHADOW);    // Bottom inner shadow
        }
    }

    public static void drawTooltip(GuiGraphicsExtractor graphics, Font font, List<Component> text, Position mousePosition) {
        graphics.setTooltipForNextFrame(font, text, Optional.empty(), ItemStack.EMPTY, mousePosition.x(), mousePosition.y());
    }
}
