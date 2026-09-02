package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class ButtonElement extends UIElement {
    private final Supplier<Component> labelSupplier;
    private final Supplier<Boolean> activeSupplier;
    private final Supplier<UIElement> contentSupplier;
    private final Runnable onClick;
    private boolean primaryGreen = false;
    private Component tooltip;

    public ButtonElement(Size size, Supplier<Component> labelSupplier, Supplier<Boolean> activeSupplier, Runnable onClick, Supplier<UIElement> contentSupplier) {
        super(Position.ZERO, size, Padding.ZERO);
        this.labelSupplier = labelSupplier != null ? labelSupplier : Component::empty;
        this.activeSupplier = activeSupplier != null ? activeSupplier : () -> false;
        this.contentSupplier = contentSupplier != null ? contentSupplier : () -> null;
        this.onClick = onClick;
    }

    public ButtonElement(Size size, Supplier<Component> labelSupplier, Supplier<Boolean> activeSupplier, Runnable onClick) {
        this(size, labelSupplier, activeSupplier, onClick, () -> null);
    }

    public ButtonElement(int width, int height, Component label, Runnable onClick) {
        this(new Size(width, height), () -> label, () -> false, onClick, () -> null);
    }

    public static ButtonElement button(int width, int height, Component label, Runnable onClick) {
        return new ButtonElement(width, height, label, onClick);
    }

    public static ButtonElement primary(int width, int height, Component label, Runnable onClick) {
        return new ButtonElement(width, height, label, onClick).setPrimaryGreen(true);
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
        if (!this.isEffectivelyVisible()) {
            return false;
        }

        if (button == 0 && this.getBounds().contains(new Position((int) mouseX, (int) mouseY))) {
            if (onClick != null) {
                onClick.run();
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (!this.isEffectivelyVisible()) {
            return;
        }

        boolean hovered = this.isHovered(mousePos);
        boolean active = activeSupplier.get();

        GUIRenderHelper.drawOreUIButton(graphics, this.getBounds(), hovered, active, primaryGreen || active);

        var font = Minecraft.getInstance().font;
        var label = labelSupplier.get();
        var content = contentSupplier.get();

        int centerX = this.left() + this.width() / 2;
        int centerY = this.top() + (this.height() - 8) / 2;

        if (label != null && font != null) {
            int textColor = (primaryGreen || active) ? GUIRenderHelper.ORE_TEXT_TITLE : (hovered ? GUIRenderHelper.ORE_TEXT_TITLE : GUIRenderHelper.ORE_TEXT_MUTED);
            GUIRenderHelper.drawCenteredString(graphics, font, label, new Position(centerX, centerY), textColor, true);
        }

        if (content != null) {
            content.setPosition(this.position);
            content.render(graphics, mousePos, partialTick);
        }

        if (tooltip != null && hovered && font != null && mousePos != null) {
            GUIRenderHelper.drawTooltip(graphics, font, List.of(tooltip), mousePos);
        }

        super.render(graphics, mousePos, partialTick);
    }
}
