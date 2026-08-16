package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.client.gui.widget.HeaderControlButtonWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class WindowControl {
    private final String symbol;
    private final int symbolColor;
    private final Component tooltip;
    private final Consumer<HeaderControlButtonWidget> onPress;

    public WindowControl(String symbol, int symbolColor, Component tooltip, Consumer<HeaderControlButtonWidget> onPress) {
        this.symbol = symbol;
        this.symbolColor = symbolColor;
        this.tooltip = tooltip;
        this.onPress = onPress;
    }

    public static WindowControl dock(Runnable onDock) {
        return new WindowControl("⤢", 0xFF52A9FF, Component.literal("Dock adjacent to main window").withStyle(ChatFormatting.GRAY), btn -> {
            if (onDock != null) onDock.run();
        });
    }

    public static WindowControl close(Runnable onClose) {
        return new WindowControl("✕", 0xFFFF5555, Component.literal("Close Window").withStyle(ChatFormatting.GRAY), btn -> {
            if (onClose != null) onClose.run();
        });
    }

    public static WindowControl minimize(Runnable onMinimize) {
        return new WindowControl("-", 0xFFFFAA00, Component.literal("Minimize Window").withStyle(ChatFormatting.GRAY), btn -> {
            if (onMinimize != null) onMinimize.run();
        });
    }

    public static WindowControl custom(String symbol, int color, Component tooltip, Consumer<HeaderControlButtonWidget> onPress) {
        return new WindowControl(symbol, color, tooltip, onPress);
    }

    public HeaderControlButtonWidget createWidget() {
        HeaderControlButtonWidget widget = new HeaderControlButtonWidget(symbol, symbolColor, onPress);
        if (tooltip != null) {
            widget.setTooltip(tooltip);
        }
        return widget;
    }
}
