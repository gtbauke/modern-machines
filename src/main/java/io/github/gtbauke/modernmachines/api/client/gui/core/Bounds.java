package io.github.gtbauke.modernmachines.api.client.gui.core;

public record Bounds(int left, int top, int right, int bottom) {
    public static final Bounds ZERO = new Bounds(0, 0, 0, 0);

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }
}
