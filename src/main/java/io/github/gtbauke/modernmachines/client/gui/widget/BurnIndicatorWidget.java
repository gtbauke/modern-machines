package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

public class BurnIndicatorWidget extends UiWidget {
    public static final int WIDTH = 13;
    public static final int HEIGHT = 13;
    public static final float EMPTY_U = 48.0F;
    public static final float EMPTY_V = 128.0F;
    public static final float FULL_U = 62.0F;
    public static final float FULL_V = 128.0F;

    private final DoubleSupplier progressSupplier;
    private final BooleanSupplier isLitSupplier;

    public BurnIndicatorWidget(DoubleSupplier progressSupplier) {
        this(progressSupplier, () -> progressSupplier != null && progressSupplier.getAsDouble() > 0.0);
    }

    public BurnIndicatorWidget(DoubleSupplier progressSupplier, BooleanSupplier isLitSupplier) {
        this.progressSupplier = progressSupplier != null ? progressSupplier : () -> 0.0;
        this.isLitSupplier = isLitSupplier != null ? isLitSupplier : () -> this.progressSupplier.getAsDouble() > 0.0;
        this.flexNode.setSize(WIDTH, HEIGHT);
    }

    public static BurnIndicatorWidget of(DoubleSupplier progressSupplier) {
        return new BurnIndicatorWidget(progressSupplier);
    }

    public static BurnIndicatorWidget of(DoubleSupplier progressSupplier, BooleanSupplier isLitSupplier) {
        return new BurnIndicatorWidget(progressSupplier, isLitSupplier);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();

        // 1. Render empty flame background base (13x13)
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), EMPTY_U, EMPTY_V, WIDTH, HEIGHT, 256, 256);

        // 2. Render filled flame from bottom up when active
        if (isLitSupplier.getAsBoolean()) {
            double progress = Math.max(0.0, Math.min(1.0, progressSupplier.getAsDouble()));
            int fillH = (int) Math.round(HEIGHT * progress);
            if (fillH > 0) {
                int drawY = b.y() + HEIGHT - fillH;
                float texV = FULL_V + HEIGHT - fillH;
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), drawY, FULL_U, texV, WIDTH, fillH, 256, 256);
            }
        }
    }
}
