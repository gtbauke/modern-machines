package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class LabelElement extends UIElement {
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private Supplier<Component> textSupplier;
    private int color = GUIRenderHelper.ORE_TEXT_TITLE;
    private boolean shadow = true;
    private TextAlignment alignment = TextAlignment.LEFT;

    public LabelElement(Supplier<Component> textSupplier) {
        super(new Bounds(Position.ZERO, new Size(60, 10)));
        this.textSupplier = textSupplier != null ? textSupplier : () -> Component.empty();
    }

    public LabelElement(Component text) {
        this(() -> text != null ? text : Component.empty());
    }

    public LabelElement(String text) {
        this(Component.literal(text != null ? text : ""));
    }

    public Supplier<Component> getTextSupplier() {
        return textSupplier;
    }

    public LabelElement setTextSupplier(Supplier<Component> textSupplier) {
        this.textSupplier = textSupplier != null ? textSupplier : () -> Component.empty();
        markDirty();
        return this;
    }

    public LabelElement setText(Component text) {
        return setTextSupplier(() -> text != null ? text : Component.empty());
    }

    public LabelElement setText(String text) {
        return setText(Component.literal(text != null ? text : ""));
    }

    public int getColor() {
        return color;
    }

    public LabelElement setColor(int color) {
        this.color = color;
        markDirty();
        return this;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public LabelElement setShadow(boolean shadow) {
        this.shadow = shadow;
        markDirty();
        return this;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public LabelElement setAlignment(TextAlignment alignment) {
        this.alignment = alignment != null ? alignment : TextAlignment.LEFT;
        markDirty();
        return this;
    }

    @Override
    public void calculateSize() {
        super.calculateSize();
        var font = Minecraft.getInstance().font;
        var comp = textSupplier.get();
        int strWidth = font.width(comp);
        if (this.bounds.size().width() < strWidth) {
            setSize(new Size(strWidth, 10));
        }
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        var font = Minecraft.getInstance().font;
        var comp = textSupplier.get();
        int strWidth = font.width(comp);

        int drawX = absoluteBounds.position().x();
        if (alignment == TextAlignment.CENTER) {
            drawX = absoluteBounds.position().x() + (absoluteBounds.size().width() - strWidth) / 2;
        } else if (alignment == TextAlignment.RIGHT) {
            drawX = absoluteBounds.right() - strWidth;
        }

        int drawY = absoluteBounds.position().y() + (absoluteBounds.size().height() - 8) / 2;
        graphics.text(font, comp, drawX, drawY, color, shadow);
    }
}
