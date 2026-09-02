package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.NineSliceRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.DoubleSupplier;

public class ProgressBarElement extends UIElement {
    public enum ProgressType {
        ARROW,
        LINEAR,
        FLAME
    }

    public static final int ARROW_WIDTH = 22;
    public static final int ARROW_HEIGHT = 15;
    public static final int LINEAR_WIDTH = 13;
    public static final int LINEAR_HEIGHT = 5;
    public static final int FLAME_WIDTH = 13;
    public static final int FLAME_HEIGHT = 13;

    private final ProgressType type;
    private final DoubleSupplier progressSupplier;

    public ProgressBarElement(ProgressType type, DoubleSupplier progressSupplier) {
        super(Position.ZERO, calculateInitialSize(type), Padding.ZERO);
        this.type = type != null ? type : ProgressType.ARROW;
        this.progressSupplier = progressSupplier != null ? progressSupplier : () -> 0.0;
    }

    private static Size calculateInitialSize(ProgressType type) {
        if (type == ProgressType.ARROW) {
            return new Size(ARROW_WIDTH, ARROW_HEIGHT);
        }

        if (type == ProgressType.FLAME) {
            return new Size(FLAME_WIDTH, FLAME_HEIGHT);
        }

        return new Size(LINEAR_WIDTH, LINEAR_HEIGHT);
    }

    public static ProgressBarElement arrow(DoubleSupplier progressSupplier) {
        return new ProgressBarElement(ProgressType.ARROW, progressSupplier);
    }

    public static ProgressBarElement linear(DoubleSupplier progressSupplier) {
        return new ProgressBarElement(ProgressType.LINEAR, progressSupplier);
    }

    public static ProgressBarElement flame(DoubleSupplier progressSupplier) {
        return new ProgressBarElement(ProgressType.FLAME, progressSupplier);
    }

    public ProgressType getType() {
        return type;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (!this.isEffectivelyVisible()) {
            return;
        }

        super.render(graphics, mousePos, partialTick);

        int x = this.left();
        int y = this.top();
        double progress = Math.max(0.0, Math.min(1.0, progressSupplier.getAsDouble()));

        if (type == ProgressType.ARROW) {
            // Empty arrow base
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 0.0F, 128.0F, ARROW_WIDTH, ARROW_HEIGHT, 256, 256);

            // Filled progress arrow (sliced horizontally)
            int fillW = (int) Math.round(ARROW_WIDTH * progress);
            if (fillW > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 24.0F, 128.0F, fillW, ARROW_HEIGHT, 256, 256);
            }
        } else if (type == ProgressType.FLAME) {
            // Empty flame base
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 48.0F, 128.0F, FLAME_WIDTH, FLAME_HEIGHT, 256, 256);

            // Filled flame (sliced vertically bottom-to-top)
            int fillH = (int) Math.round(FLAME_HEIGHT * progress);
            if (fillH > 0) {
                int destY = y + FLAME_HEIGHT - fillH;
                float srcV = 128.0F + FLAME_HEIGHT - fillH;
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, destY, 62.0F, srcV, FLAME_WIDTH, fillH, 256, 256);
            }
        } else {
            // Empty linear bar
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 76.0F, 128.0F, LINEAR_WIDTH, LINEAR_HEIGHT, 256, 256);

            // Filled linear bar (sliced horizontally)
            int fillW = (int) Math.round(LINEAR_WIDTH * progress);
            if (fillW > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 90.0F, 128.0F, fillW, LINEAR_HEIGHT, 256, 256);
            }
        }
    }
}
