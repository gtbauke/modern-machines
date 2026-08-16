package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.network.chat.Component;

public record MainWindow(Component title, UiWidget body) {

    public static MainWindow of(UiWidget... children) {
        return new MainWindow(null, Column.of(children));
    }

    public static MainWindow of(Component title, UiWidget... children) {
        return new MainWindow(title, Column.of(children));
    }

    public static MainWindow of(UiWidget body) {
        return new MainWindow(null, body);
    }

    public static MainWindow of(Component title, UiWidget body) {
        return new MainWindow(title, body);
    }
}
