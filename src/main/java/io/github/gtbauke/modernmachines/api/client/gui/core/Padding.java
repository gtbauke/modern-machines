package io.github.gtbauke.modernmachines.api.client.gui.core;

public record Padding(int top, int right, int bottom, int left) {
    public static final Padding ZERO = new Padding(0, 0, 0, 0);

    public Padding(int topBottom, int leftRight) {
        this(topBottom, leftRight, topBottom, leftRight);
    }

    public Padding(int all) {
        this(all, all, all, all);
    }
}
