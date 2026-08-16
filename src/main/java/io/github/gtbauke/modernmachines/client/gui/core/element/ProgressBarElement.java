package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.DoubleSupplier;

public class ProgressBarElement extends UIElement {
    public enum ProgressType {
        ARROW,
        LINEAR
    }

    public static final int ARROW_WIDTH = 22;
    public static final int ARROW_HEIGHT = 15;
    public static final int LINEAR_WIDTH = 13;
    public static final int LINEAR_HEIGHT = 5;

    private final ProgressType type;
    private final DoubleSupplier progressSupplier;

    public ProgressBarElement(ProgressType type, DoubleSupplier progressSupplier) {
        super(new Bounds(Position.ZERO, type == ProgressType.ARROW ? new Size(ARROW_WIDTH, ARROW_HEIGHT) : new Size(LINEAR_WIDTH, LINEAR_HEIGHT)));
        this.type = type != null ? type : ProgressType.ARROW;
        this.progressSupplier = progressSupplier != null ? progressSupplier : () -> 0.0;
    }

    public static ProgressBarElement arrow(DoubleSupplier progressSupplier) {
        return new ProgressBarElement(ProgressType.ARROW, progressSupplier);
    }

    public static ProgressBarElement linear(DoubleSupplier progressSupplier) {
        return new ProgressBarElement(ProgressType.LINEAR, progressSupplier);
    }

    public ProgressType getType() {
        return type;
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        int x = absoluteBounds.position().x();
        int y = absoluteBounds.position().y();
        double progress = Math.max(0.0, Math.min(1.0, progressSupplier.getAsDouble()));

        if (type == ProgressType.ARROW) {
            // Draw empty arrow base (22x15)
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 0.0F, 128.0F, ARROW_WIDTH, ARROW_HEIGHT, 256, 256);

            // Draw filled progress arrow (sliced horizontally)
            int fillW = (int) Math.round(ARROW_WIDTH * progress);
            if (fillW > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 24.0F, 128.0F, fillW, ARROW_HEIGHT, 256, 256);
            }
        } else {
            // Draw empty linear bar (13x5)
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 76.0F, 128.0F, LINEAR_WIDTH, LINEAR_HEIGHT, 256, 256);

            // Draw filled linear bar (sliced horizontally)
            int fillW = (int) Math.round(LINEAR_WIDTH * progress);
            if (fillW > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 90.0F, 128.0F, fillW, LINEAR_HEIGHT, 256, 256);
            }
        }
    }
}
