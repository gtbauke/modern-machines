package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
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
    private boolean shadow = false;
    private TextAlignment alignment = TextAlignment.LEFT;

    public LabelElement(Supplier<Component> textSupplier) {
        super(Position.ZERO, new Size(60, 10), Padding.ZERO);
        this.textSupplier = textSupplier != null ? textSupplier : Component::empty;
        this.autoSize = true;
    }

    public LabelElement(Component text) {
        this(() -> text != null ? text : Component.empty());
    }

    public LabelElement(String text) {
        this(Component.literal(text != null ? text : ""));
    }

    public static LabelElement title(Component text) {
        return new LabelElement(text)
                .setColor(GUIRenderHelper.ORE_TEXT_TITLE)
                .setAlignment(TextAlignment.LEFT);
    }

    public static LabelElement title(String text) {
        return title(Component.literal(text != null ? text : ""));
    }

    public static LabelElement muted(Component text) {
        return new LabelElement(text)
                .setColor(GUIRenderHelper.ORE_TEXT_MUTED)
                .setAlignment(TextAlignment.LEFT);
    }

    public static LabelElement muted(String text) {
        return muted(Component.literal(text != null ? text : ""));
    }

    public static LabelElement of(Component text) {
        return new LabelElement(text);
    }

    public static LabelElement of(String text) {
        return new LabelElement(text);
    }

    public Supplier<Component> getTextSupplier() {
        return textSupplier;
    }

    public LabelElement setTextSupplier(Supplier<Component> textSupplier) {
        this.textSupplier = textSupplier != null ? textSupplier : Component::empty;
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
        return this;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public LabelElement setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public LabelElement setAlignment(TextAlignment alignment) {
        this.alignment = alignment != null ? alignment : TextAlignment.LEFT;
        return this;
    }

    @Override
    public void calculateSize() {
        super.calculateSize();
        var font = Minecraft.getInstance().font;
        if (font != null) {
            var comp = textSupplier.get();
            int strWidth = font.width(comp);
            this.size = new Size(strWidth, 10);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (!this.isEffectivelyVisible()) {
            return;
        }

        super.render(graphics, mousePos, partialTick);

        var font = Minecraft.getInstance().font;
        if (font == null) {
            return;
        }

        var comp = textSupplier.get();
        int strWidth = font.width(comp);

        int drawX = this.left();
        if (alignment == TextAlignment.CENTER) {
            drawX = this.left() + (this.width() - strWidth) / 2;
        } else if (alignment == TextAlignment.RIGHT) {
            drawX = this.right() - strWidth;
        }

        int drawY = this.top() + (this.height() - 8) / 2;
        graphics.text(font, comp, drawX, drawY, color, shadow);
    }
}
