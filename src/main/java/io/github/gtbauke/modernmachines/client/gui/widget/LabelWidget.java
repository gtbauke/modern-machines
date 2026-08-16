package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.List;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

public class LabelWidget extends UiWidget {
    private Component text;
    private Supplier<Component> textSupplier;
    private int color = 0xFFFFFFFF;
    private int backgroundColor = 0;
    private boolean shadow = true;
    private boolean centered = false;
    private int maxWrapWidth = -1;
    private boolean autoWrap = false;

    public LabelWidget(Component text) {
        this.text = text;
        updateSizeForText();
    }

    public LabelWidget(Supplier<Component> textSupplier) {
        this.textSupplier = textSupplier;
        flexNode.setSize(100, 10);
    }

    private void updateSizeForText() {
        if (this.text != null) {
            String str = this.text.getString();
            if (str.contains("\n")) {
                String[] lines = str.split("\n");
                int maxW = 0;
                for (String l : lines) {
                    maxW = Math.max(maxW, l.length() * 6 + 4);
                }
                flexNode.setSize(Math.max(60, maxW), lines.length * 11);
                return;
            } else if (maxWrapWidth > 0) {
                int approxLines = Math.max(1, (int) Math.ceil((str.length() * 6.0) / maxWrapWidth));
                flexNode.setSize(maxWrapWidth, approxLines * 11);
                return;
            } else {
                int estW = Math.max(40, str.length() * 6 + 4);
                flexNode.setSize(estW, 10);
                return;
            }
        }
        flexNode.setSize(100, 10);
    }

    public static LabelWidget of(Component text) {
        return new LabelWidget(text);
    }

    public static LabelWidget of(String text) {
        return new LabelWidget(Component.literal(text));
    }

    public static LabelWidget of(Supplier<Component> textSupplier) {
        return new LabelWidget(textSupplier);
    }

    public Component getText() {
        return textSupplier != null ? textSupplier.get() : text;
    }

    public LabelWidget setText(Component text) {
        this.text = text;
        this.textSupplier = null;
        updateSizeForText();
        return this;
    }

    public LabelWidget setText(Supplier<Component> textSupplier) {
        this.textSupplier = textSupplier;
        return this;
    }

    public LabelWidget setLabel(Component text) {
        return setText(text);
    }

    public LabelWidget color(int color) {
        this.color = color;
        return this;
    }

    public LabelWidget setColor(int color) {
        return color(color);
    }

    public LabelWidget backgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public LabelWidget setBackgroundColor(int color) {
        return backgroundColor(color);
    }

    public LabelWidget shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public LabelWidget setShadow(boolean shadow) {
        return shadow(shadow);
    }

    public LabelWidget centered(boolean centered) {
        this.centered = centered;
        return this;
    }

    public LabelWidget centered() {
        return centered(true);
    }

    public LabelWidget setCentered(boolean centered) {
        return centered(centered);
    }

    public LabelWidget maxWrapWidth(int width) {
        this.maxWrapWidth = width;
        updateSizeForText();
        return this;
    }

    public LabelWidget setMaxWrapWidth(int width) {
        return maxWrapWidth(width);
    }

    public LabelWidget autoWrap(boolean wrap) {
        this.autoWrap = wrap;
        return this;
    }

    public LabelWidget setAutoWrap(boolean wrap) {
        return autoWrap(wrap);
    }

    public LabelWidget style(ChatFormatting... formats) {
        Component current = getText();
        if (current instanceof MutableComponent mutable) {
            this.text = mutable.withStyle(formats);
        } else if (current != null) {
            this.text = current.copy().withStyle(formats);
        }
        return this;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        if (backgroundColor != 0) {
            Bounds b = getBounds();
            GuiRenderHelper.drawRect(graphics, b.x(), b.y(), b.width(), b.height(), backgroundColor);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Component currentText = getText();
        if (currentText == null) return;
        Bounds b = getBounds();

        int targetWidth = maxWrapWidth > 0 ? maxWrapWidth : Math.max(30, b.width());
        String rawString = currentText.getString();

        if (rawString.contains("\n") || (autoWrap && font.width(currentText) > targetWidth)) {
            List<FormattedCharSequence> lines = font.split(currentText, targetWidth);
            int totalH = lines.size() * font.lineHeight + Math.max(0, lines.size() - 1) * 2;
            int startY = b.y() + Math.max(0, (b.height() - totalH) / 2);

            for (int i = 0; i < lines.size(); i++) {
                FormattedCharSequence line = lines.get(i);
                int lineW = font.width(line);
                int lineX = centered ? (b.x() + (b.width() - lineW) / 2) : b.x();
                int lineY = startY + i * (font.lineHeight + 2);
                graphics.text(font, line, lineX, lineY, color, shadow);
            }
        } else {
            int textW = font.width(currentText);
            int tx = centered ? (b.x() + (b.width() - textW) / 2) : b.x();
            int ty = b.y() + (b.height() - font.lineHeight) / 2;
            graphics.text(font, currentText, tx, ty, color, shadow);
        }
    }
}
