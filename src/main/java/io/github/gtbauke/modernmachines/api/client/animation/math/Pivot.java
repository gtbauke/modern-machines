package io.github.gtbauke.modernmachines.api.client.animation.math;

public record Pivot(float x, float y, float z) {
    public static final Pivot ZERO = new Pivot(0.0f, 0.0f, 0.0f);
    public static final Pivot CENTER = new Pivot(0.5f, 0.5f, 0.5f);

    public static Pivot of(float x, float y, float z) {
        return new Pivot(x, y, z);
    }

    public static Pivot ofBlock(float x16, float y16, float z16) {
        return new Pivot(x16 / 16.0f, y16 / 16.0f, z16 / 16.0f);
    }
}
