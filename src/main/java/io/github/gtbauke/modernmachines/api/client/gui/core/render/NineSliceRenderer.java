package io.github.gtbauke.modernmachines.api.client.gui.core.render;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.client.gui.core.Bounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class NineSliceRenderer {
    public static final Identifier GUI_ATLAS = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "textures/gui/gui_sprites.png");

    public record Slice(int u, int v, int width, int height, int cornerSize) {}

    public static final Slice WINDOW = new Slice(0, 0, 24, 24, 4);
    public static final Slice WINDOW_DARK = WINDOW;
    public static final Slice PANEL_TRANSLUCENT = WINDOW;
    public static final Slice HEADER = new Slice(32, 0, 24, 20, 4);
    public static final Slice SLOT = new Slice(64, 0, 18, 18, 2);
    public static final Slice BUTTON_NORMAL = new Slice(0, 32, 24, 20, 3);
    public static final Slice BUTTON_HOVER = new Slice(24, 32, 24, 20, 3);
    public static final Slice BUTTON_PRESSED = new Slice(48, 32, 24, 20, 3);
    public static final Slice TAB_LEFT = new Slice(0, 64, 28, 26, 3);
    public static final Slice TAB_RIGHT = new Slice(28, 64, 28, 26, 3);

    public static void drawNineSlice(GuiGraphicsExtractor graphics, Slice slice, Bounds bounds) {
        drawNineSlice(graphics, slice, bounds.left(), bounds.top(), bounds.width(), bounds.height());
    }

    public static void drawNineSlice(GuiGraphicsExtractor graphics, Slice slice, int x, int y, int width, int height) {
        int u = slice.u();
        int v = slice.v();
        int sw = slice.width();
        int sh = slice.height();
        int c = slice.cornerSize();

        int innerW = Math.max(0, width - 2 * c);
        int innerH = Math.max(0, height - 2 * c);
        int srcInnerW = sw - 2 * c;
        int srcInnerH = sh - 2 * c;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x, y, (float) u, (float) v, c, c, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x + width - c, y, (float) (u + sw - c), (float) v, c, c, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x, y + height - c, (float) u, (float) (v + sh - c), c, c, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, x + width - c, y + height - c, (float) (u + sw - c), (float) (v + sh - c), c, c, 256, 256);

        if (innerW > 0) {
            renderTiled(graphics, u + c, v, srcInnerW, c, x + c, y, innerW, c);
            renderTiled(graphics, u + c, v + sh - c, srcInnerW, c, x + c, y + height - c, innerW, c);
        }

        if (innerH > 0) {
            renderTiled(graphics, u, v + c, c, srcInnerH, x, y + c, c, innerH);
            renderTiled(graphics, u + sw - c, v + c, c, srcInnerH, x + width - c, y + c, c, innerH);
        }

        if (innerW > 0 && innerH > 0) {
            renderTiled(graphics, u + c, v + c, srcInnerW, srcInnerH, x + c, y + c, innerW, innerH);
        }
    }

    private static void renderTiled(GuiGraphicsExtractor graphics, int u, int v, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH) {
        if (srcW <= 0 || srcH <= 0 || dstW <= 0 || dstH <= 0) {
            return;
        }

        for (int x = 0; x < dstW; x += srcW) {
            int drawW = Math.min(srcW, dstW - x);
            for (int y = 0; y < dstH; y += srcH) {
                int drawH = Math.min(srcH, dstH - y);
                graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ATLAS, dstX + x, dstY + y, (float) u, (float) v, drawW, drawH, 256, 256);
            }
        }
    }
}
