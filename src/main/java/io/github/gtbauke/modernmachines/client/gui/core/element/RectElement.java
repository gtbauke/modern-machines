package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;

public class RectElement extends UIElement {
    public RectElement(Bounds bounds, int color, int borderColor) {
        super(bounds);
        setBackground(color, borderColor);
    }

    public RectElement(Bounds bounds, int color) {
        this(bounds, color, 0);
    }

    public RectElement(Position position, Size size, int color) {
        this(new Bounds(position, size), color, 0);
    }

    public RectElement(Position position, Size size, int color, int borderColor) {
        this(new Bounds(position, size), color, borderColor);
    }

    public RectElement(int color) {
        this(Bounds.EMPTY, color, 0);
    }
}
