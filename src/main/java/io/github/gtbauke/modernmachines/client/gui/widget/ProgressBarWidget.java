package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.function.DoubleSupplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

public class ProgressBarWidget extends UiWidget {
    public enum ProgressType {
        ARROW,
        FLAME,
        LINEAR_HORIZONTAL,
        LINEAR_VERTICAL
    }

    public static final int ARROW_WIDTH = 22;
    public static final int ARROW_HEIGHT = 15;
    public static final int FLAME_WIDTH = 13;
    public static final int FLAME_HEIGHT = 13;
    public static final int LINEAR_WIDTH = 13;
    public static final int LINEAR_HEIGHT = 5;

    private final ProgressType type;
    private final DoubleSupplier progressSupplier;

    public ProgressBarWidget(ProgressType type, DoubleSupplier progressSupplier) {
        this.type = type;
        this.progressSupplier = progressSupplier != null ? progressSupplier : () -> 0.0;
        if (type == ProgressType.ARROW) {
            flexNode.setSize(ARROW_WIDTH, ARROW_HEIGHT);
        } else if (type == ProgressType.FLAME) {
            flexNode.setSize(FLAME_WIDTH, FLAME_HEIGHT);
        } else {
            flexNode.setSize(LINEAR_WIDTH, LINEAR_HEIGHT);
        }
    }

    public static ProgressBarWidget arrow(DoubleSupplier progressSupplier) {
        return new ProgressBarWidget(ProgressType.ARROW, progressSupplier);
    }

    public static ProgressBarWidget flame(DoubleSupplier progressSupplier) {
        return new ProgressBarWidget(ProgressType.FLAME, progressSupplier);
    }

    public static ProgressBarWidget linear(DoubleSupplier progressSupplier) {
        return new ProgressBarWidget(ProgressType.LINEAR_HORIZONTAL, progressSupplier);
    }

    public static ProgressBarWidget linear(DoubleSupplier progressSupplier, int width, int height) {
        ProgressBarWidget widget = new ProgressBarWidget(ProgressType.LINEAR_HORIZONTAL, progressSupplier);
        widget.size(width, height);
        return widget;
    }

    public ProgressType getType() {
        return type;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();
        double progress = Math.max(0.0, Math.min(1.0, progressSupplier.getAsDouble()));

        if (type == ProgressType.ARROW) {
            // Draw empty arrow base (22x15)
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 0.0F, 128.0F, ARROW_WIDTH, ARROW_HEIGHT, 256, 256);
            // Draw filled progress arrow
            int fillW = (int) Math.round(ARROW_WIDTH * progress);
            if (fillW > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 24.0F, 128.0F, fillW, ARROW_HEIGHT, 256, 256);
            }
        } else if (type == ProgressType.FLAME) {
            // Draw empty flame base (13x13)
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 48.0F, 128.0F, FLAME_WIDTH, FLAME_HEIGHT, 256, 256);
            int fillH = (int) Math.round(FLAME_HEIGHT * progress);
            if (fillH > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y() + FLAME_HEIGHT - fillH, 62.0F, 128.0F + FLAME_HEIGHT - fillH, FLAME_WIDTH, fillH, 256, 256);
            }
        } else if (type == ProgressType.LINEAR_HORIZONTAL) {
            if (b.width() == LINEAR_WIDTH && b.height() == LINEAR_HEIGHT) {
                // Draw authentic 13x5 progress bar from atlas
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 76.0F, 128.0F, LINEAR_WIDTH, LINEAR_HEIGHT, 256, 256);
                int fillW = (int) Math.round(LINEAR_WIDTH * progress);
                if (fillW > 0) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 90.0F, 128.0F, fillW, LINEAR_HEIGHT, 256, 256);
                }
            } else {
                // Scaled custom-sized progress bar
                graphics.fill(b.x(), b.y(), b.right(), b.bottom(), 0xFF222428);
                int fillW = (int) Math.round(b.width() * progress);
                if (fillW > 0) {
                    graphics.fill(b.x(), b.y(), b.x() + fillW, b.bottom(), theme.accentColor());
                }
            }
        }
    }
}
