package io.github.gtbauke.modernmachines.client.gui.core.layout;

public record Padding(int top, int right, int bottom, int left) {
    public Padding(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Padding(int padding) {
        this(padding, padding, padding, padding);
    }
}
