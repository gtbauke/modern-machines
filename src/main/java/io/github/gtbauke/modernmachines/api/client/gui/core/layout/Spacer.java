package io.github.gtbauke.modernmachines.api.client.gui.core.layout;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;

public class Spacer extends UIElement {

    public Spacer(int width, int height) {
        super(Position.ZERO, new Size(width, height), Padding.ZERO);
    }

    public Spacer(int size) {
        this(size, size);
    }

    public Spacer() {
        this(0, 0);
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

    public static Spacer flex(int weight) {
        var spacer = new Spacer(0, 0);
        spacer.setFlowWeight(weight);
        return spacer;
    }
}
