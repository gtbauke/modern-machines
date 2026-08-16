package io.github.gtbauke.modernmachines.client.gui.layout;

public record FlexInsets(int top, int right, int bottom, int left) {
    public static final FlexInsets ZERO = new FlexInsets(0, 0, 0, 0);

    public static FlexInsets all(int all) {
        return new FlexInsets(all, all, all, all);
    }

    public static FlexInsets symmetric(int vertical, int horizontal) {
        return new FlexInsets(vertical, horizontal, vertical, horizontal);
    }

    public static FlexInsets of(int top, int right, int bottom, int left) {
        return new FlexInsets(top, right, bottom, left);
    }

    public int horizontal() {
        return left + right;
    }

    public int vertical() {
        return top + bottom;
    }
}
