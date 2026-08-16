package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.function.Function;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.client.gui.window.FloatingTabWindow;
import net.minecraft.network.chat.Component;

public class FloatingTab {
    private final Component title;
    private final int iconU;
    private final int iconV;
    private final boolean leftSided;
    private final Function<FloatingTabWindow, UiWidget> contentFactory;

    public FloatingTab(Component title, int iconU, int iconV, boolean leftSided, Function<FloatingTabWindow, UiWidget> contentFactory) {
        this.title = title;
        this.iconU = iconU;
        this.iconV = iconV;
        this.leftSided = leftSided;
        this.contentFactory = contentFactory;
    }

    // Default to Left-Sided tabs
    public static FloatingTab of(Component title, int iconU, int iconV, Function<FloatingTabWindow, UiWidget> contentFactory) {
        return new FloatingTab(title, iconU, iconV, true, contentFactory);
    }

    public static FloatingTab of(String title, int iconU, int iconV, Function<FloatingTabWindow, UiWidget> contentFactory) {
        return new FloatingTab(Component.literal(title), iconU, iconV, true, contentFactory);
    }

    public static FloatingTab of(String title, int iconU, int iconV, Supplier<UiWidget> contentSupplier) {
        return new FloatingTab(Component.literal(title), iconU, iconV, true, win -> contentSupplier.get());
    }

    public static FloatingTab of(Component title, int iconU, int iconV, Supplier<UiWidget> contentSupplier) {
        return new FloatingTab(title, iconU, iconV, true, win -> contentSupplier.get());
    }

    // Explicit Left-Sided
    public static FloatingTab left(Component title, int iconU, int iconV, Function<FloatingTabWindow, UiWidget> contentFactory) {
        return new FloatingTab(title, iconU, iconV, true, contentFactory);
    }

    // Explicit Right-Sided
    public static FloatingTab right(Component title, int iconU, int iconV, Function<FloatingTabWindow, UiWidget> contentFactory) {
        return new FloatingTab(title, iconU, iconV, false, contentFactory);
    }

    // Backward compatibility overloads with width
    public static FloatingTab of(Component title, int iconU, int iconV, int width, Function<FloatingTabWindow, UiWidget> contentFactory) {
        return new FloatingTab(title, iconU, iconV, true, contentFactory);
    }

    public static FloatingTab of(String title, int iconU, int iconV, int width, Function<FloatingTabWindow, UiWidget> contentFactory) {
        return new FloatingTab(Component.literal(title), iconU, iconV, true, contentFactory);
    }

    public static FloatingTab of(String title, int iconU, int iconV, int width, Supplier<UiWidget> contentSupplier) {
        return new FloatingTab(Component.literal(title), iconU, iconV, true, win -> contentSupplier.get());
    }

    public static FloatingTab of(Component title, int iconU, int iconV, int width, Supplier<UiWidget> contentSupplier) {
        return new FloatingTab(title, iconU, iconV, true, win -> contentSupplier.get());
    }

    public Component getTitle() { return title; }
    public int getIconU() { return iconU; }
    public int getIconV() { return iconV; }
    public boolean isLeftSided() { return leftSided; }
    public Function<FloatingTabWindow, UiWidget> getContentFactory() { return contentFactory; }
}
