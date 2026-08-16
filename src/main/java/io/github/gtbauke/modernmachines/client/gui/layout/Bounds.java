package io.github.gtbauke.modernmachines.client.gui.layout;

public record Bounds(int x, int y, int width, int height) {
    public static final Bounds EMPTY = new Bounds(0, 0, 0, 0);

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean contains(double px, double py) {
        return px >= x && px < x + width && py >= y && py < y + height;
    }

    public Bounds offset(int dx, int dy) {
        return new Bounds(x + dx, y + dy, width, height);
    }

    public Bounds withSize(int w, int h) {
        return new Bounds(x, y, w, h);
    }

    public Bounds withPosition(int nx, int ny) {
        return new Bounds(nx, ny, width, height);
    }

    public Bounds inset(FlexInsets insets) {
        return new Bounds(
                x + insets.left(),
                y + insets.top(),
                Math.max(0, width - insets.horizontal()),
                Math.max(0, height - insets.vertical())
        );
    }
}
