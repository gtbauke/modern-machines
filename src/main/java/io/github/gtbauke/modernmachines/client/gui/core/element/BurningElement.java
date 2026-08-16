package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.DoubleSupplier;

public class BurningElement extends UIElement {
    public static final int FLAME_WIDTH = 13;
    public static final int FLAME_HEIGHT = 13;

    private final DoubleSupplier burnSupplier;

    public BurningElement(DoubleSupplier burnSupplier) {
        super(new Bounds(Position.ZERO, new Size(FLAME_WIDTH, FLAME_HEIGHT)));
        this.burnSupplier = burnSupplier != null ? burnSupplier : () -> 0.0;
    }

    public static BurningElement flame(DoubleSupplier burnSupplier) {
        return new BurningElement(burnSupplier);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        int x = absoluteBounds.position().x();
        int y = absoluteBounds.position().y();
        double burnProgress = Math.max(0.0, Math.min(1.0, burnSupplier.getAsDouble()));

        // Draw empty flame base (13x13)
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, y, 48.0F, 128.0F, FLAME_WIDTH, FLAME_HEIGHT, 256, 256);

        // Draw filled flame (sliced from bottom to top)
        int fillH = (int) Math.round(FLAME_HEIGHT * burnProgress);
        if (fillH > 0) {
            int destY = y + FLAME_HEIGHT - fillH;
            float srcV = 128.0F + FLAME_HEIGHT - fillH;
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, x, destY, 62.0F, srcV, FLAME_WIDTH, fillH, 256, 256);
        }
    }
}
