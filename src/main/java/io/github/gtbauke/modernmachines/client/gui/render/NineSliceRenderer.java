package io.github.gtbauke.modernmachines.client.gui.render;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class NineSliceRenderer {
    public static final Identifier GUI_ATLAS = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "textures/gui/gui_sprites.png");

    public record SliceDef(int u, int v, int width, int height, int cornerSize) {}

    public static final SliceDef WINDOW = new SliceDef(0, 0, 24, 24, 4);
    public static final SliceDef WINDOW_DARK = WINDOW;
    public static final SliceDef PANEL_TRANSLUCENT = WINDOW;
    public static final SliceDef HEADER = new SliceDef(32, 0, 24, 20, 4);
    public static final SliceDef SLOT = new SliceDef(64, 0, 18, 18, 2);
    public static final SliceDef BUTTON_NORMAL = new SliceDef(0, 32, 24, 20, 3);
    public static final SliceDef BUTTON_HOVER = new SliceDef(24, 32, 24, 20, 3);
    public static final SliceDef BUTTON_PRESSED = new SliceDef(48, 32, 24, 20, 3);
    public static final SliceDef TAB_LEFT = new SliceDef(0, 64, 28, 26, 3);
    public static final SliceDef TAB_RIGHT = new SliceDef(28, 64, 28, 26, 3);

    public static void drawNineSlice(GuiGraphicsExtractor graphics, SliceDef slice, Bounds bounds) {
        drawNineSlice(graphics, slice, bounds.x(), bounds.y(), bounds.width(), bounds.height());
    }

    public static void drawNineSlice(GuiGraphicsExtractor graphics, SliceDef slice, int x, int y, int width, int height) {
        int u = slice.u();
        int v = slice.v();
        int sw = slice.width();
        int sh = slice.height();
        int c = slice.cornerSize();

        int innerW = Math.max(0, width - 2 * c);
        int innerH = Math.max(0, height - 2 * c);
        int srcInnerW = sw - 2 * c;
        int srcInnerH = sh - 2 * c;

        // 4 Corners
        // Top-Left
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x, y, (float) u, (float) v, c, c, 256, 256);
        // Top-Right
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x + width - c, y, (float) (u + sw - c), (float) v, c, c, 256, 256);
        // Bottom-Left
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x, y + height - c, (float) u, (float) (v + sh - c), c, c, 256, 256);
        // Bottom-Right
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x + width - c, y + height - c, (float) (u + sw - c), (float) (v + sh - c), c, c, 256, 256);

        // 4 Edges
        // Top Edge
        if (innerW > 0) {
            renderTiled(graphics, u + c, v, srcInnerW, c, x + c, y, innerW, c);
            // Bottom Edge
            renderTiled(graphics, u + c, v + sh - c, srcInnerW, c, x + c, y + height - c, innerW, c);
        }
        // Left Edge
        if (innerH > 0) {
            renderTiled(graphics, u, v + c, c, srcInnerH, x, y + c, c, innerH);
            // Right Edge
            renderTiled(graphics, u + sw - c, v + c, c, srcInnerH, x + width - c, y + c, c, innerH);
        }

        // Center Fill
        if (innerW > 0 && innerH > 0) {
            renderTiled(graphics, u + c, v + c, srcInnerW, srcInnerH, x + c, y + c, innerW, innerH);
        }
    }

    private static void renderTiled(GuiGraphicsExtractor graphics, int u, int v, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH) {
        if (srcW <= 0 || srcH <= 0 || dstW <= 0 || dstH <= 0) return;
        for (int x = 0; x < dstW; x += srcW) {
            int drawW = Math.min(srcW, dstW - x);
            for (int y = 0; y < dstH; y += srcH) {
                int drawH = Math.min(srcH, dstH - y);
                graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, dstX + x, dstY + y, (float) u, (float) v, drawW, drawH, 256, 256);
            }
        }
    }
}
