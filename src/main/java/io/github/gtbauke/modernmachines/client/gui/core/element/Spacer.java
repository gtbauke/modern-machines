package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;

public class Spacer extends UIElement {
    public Spacer(int width, int height) {
        super(new Bounds(Position.ZERO, new Size(width, height)));
    }

    public Spacer(int size) {
        this(size, size);
    }

    public static Spacer vertical(int height) {
        return new Spacer(0, height);
    }

    public static Spacer horizontal(int width) {
        return new Spacer(width, 0);
    }

    public static Spacer square(int size) {
        return new Spacer(size, size);
    }
}
