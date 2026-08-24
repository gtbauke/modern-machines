package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class ButtonElement extends UIElement {
    private final Supplier<Component> labelSupplier;
    private final Supplier<Boolean> activeSupplier;
    private final Runnable onClick;
    private boolean primaryGreen = false;
    private Component tooltip;

    public ButtonElement(Size size, Supplier<Component> labelSupplier, Supplier<Boolean> activeSupplier, Runnable onClick) {
        super(new Bounds(Position.ZERO, size));
        this.labelSupplier = labelSupplier != null ? labelSupplier : () -> Component.empty();
        this.activeSupplier = activeSupplier != null ? activeSupplier : () -> false;
        this.onClick = onClick;
    }

    public ButtonElement(int width, int height, Component label, Runnable onClick) {
        this(new Size(width, height), () -> label, () -> false, onClick);
    }

    public ButtonElement withTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public ButtonElement setPrimaryGreen(boolean primaryGreen) {
        this.primaryGreen = primaryGreen;
        return this;
    }

    public boolean isPrimaryGreen() {
        return primaryGreen;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && getAbsoluteBounds().contains(new Position((int) mouseX, (int) mouseY))) {
            if (onClick != null) {
                onClick.run();
            }

            markDirty();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        boolean hovered = absoluteBounds.contains(new Position(mouseX, mouseY));
        boolean active = activeSupplier.get();

        GUIRenderHelper.drawOreUIButton(graphics, absoluteBounds, hovered, active, primaryGreen || active);

        var font = Minecraft.getInstance().font;
        var label = labelSupplier.get();
        int centerX = absoluteBounds.position().x() + absoluteBounds.size().width() / 2;
        int centerY = absoluteBounds.position().y() + (absoluteBounds.size().height() - 8) / 2;

        int textColor = (primaryGreen || active) ? GUIRenderHelper.ORE_TEXT_TITLE : (hovered ? GUIRenderHelper.ORE_TEXT_TITLE : GUIRenderHelper.ORE_TEXT_MUTED);
        GUIRenderHelper.drawCenteredString(graphics, font, label, new Position(centerX, centerY), textColor, true);

        if (tooltip != null && hovered) {
            GUIRenderHelper.drawTooltip(graphics, font, List.of(tooltip), new Position(mouseX, mouseY));
        }
    }
}
