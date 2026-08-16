package io.github.gtbauke.modernmachines.client.gui.core.layout;

public record Size(int width, int height) {
    public static final Size ZERO = new Size(0, 0);

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size(int size) {
        this(size, size);
    }
}
