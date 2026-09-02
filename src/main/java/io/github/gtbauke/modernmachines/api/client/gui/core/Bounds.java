package io.github.gtbauke.modernmachines.api.client.gui.core;

public record Bounds(int left, int top, int right, int bottom) {
    public static final Bounds ZERO = new Bounds(0, 0, 0, 0);

    public Bounds(Position position, Size size) {
        this(position.x(), position.y(), position.x() + size.width(), position.y() + size.height());
    }

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }

    public boolean contains(int x, int y) {
        return x >= left && x < right && y >= top && y < bottom;
    }

    public boolean contains(Position position) {
        if (position == null) {
            return false;
        }

        return contains(position.x(), position.y());
    }
}
