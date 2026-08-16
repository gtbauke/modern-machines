package io.github.gtbauke.modernmachines.client.gui.windows;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Window extends UIElement {
    protected Component title;

    public Window(Component title, Bounds bounds, Padding padding, UIElement parent, List<UIElement> children) {
        super(bounds, padding, parent, children);
        this.title = title;
    }

    public Window(Component title, Bounds bounds, Padding padding) {
        super(bounds, padding);
        this.title = title;
    }

    public Window(Component title, Bounds bounds) {
        super(bounds, new Padding(0));
        this.title = title;
    }

    public Window(Component title, int width, int height) {
        this(title, new Bounds(Position.ZERO, new Size(width, height)), new Padding(0));
    }

    public Window(Component title, Position position, Size size) {
        this(title, new Bounds(position, size), new Padding(0));
    }

    public Component getTitle() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }
}
