package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class HeaderControlButtonWidget extends UiWidget {
    private final String symbol;
    private final int symbolColor;
    private final Consumer<HeaderControlButtonWidget> onPress;
    private Component tooltip;
    private boolean pressed = false;

    public HeaderControlButtonWidget(String symbol, int symbolColor, Consumer<HeaderControlButtonWidget> onPress) {
        this.symbol = symbol;
        this.symbolColor = symbolColor;
        this.onPress = onPress;
        flexNode.setSize(14, 14);
    }

    public HeaderControlButtonWidget setTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        return tooltip != null ? List.of(tooltip) : List.of();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();

        int bgColor = hovered ? (pressed ? 0xFF1C1C26 : 0xFF353545) : 0xFF242430;
        int borderColor = hovered ? 0xFF58586E : 0xFF3A3A4A;

        // Draw clean button plate
        graphics.fill(b.x(), b.y(), b.x() + b.width(), b.y() + b.height(), borderColor);
        graphics.fill(b.x() + 1, b.y() + 1, b.x() + b.width() - 1, b.y() + b.height() - 1, bgColor);
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();

        int textW = font.width(symbol);
        int tx = b.x() + (b.width() - textW) / 2 + 1;
        int ty = b.y() + (b.height() - font.lineHeight) / 2 + 1 + (pressed ? 1 : 0);

        graphics.text(font, Component.literal(symbol), tx, ty, symbolColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;
        this.pressed = true;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        if (onPress != null) {
            onPress.accept(this);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.pressed && button == 0) {
            this.pressed = false;
            return true;
        }
        return false;
    }
}
