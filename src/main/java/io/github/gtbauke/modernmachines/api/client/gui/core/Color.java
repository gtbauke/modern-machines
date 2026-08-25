package io.github.gtbauke.modernmachines.api.client.gui.core;

public record Color(int red, int green, int blue, int alpha) {
    public static final Color WHITE = new Color(255, 255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0, 255);
    public static final Color RED = new Color(255, 0, 0, 255);
    public static final Color GREEN = new Color(0, 255, 0, 255);
    public static final Color BLUE = new Color(0, 0, 255, 255);
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public int toARGB() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public int alpha() {
        return alpha;
    }

    public int red() {
        return red;
    }

    public int green() {
        return green;
    }

    public int blue() {
        return blue;
    }
}
