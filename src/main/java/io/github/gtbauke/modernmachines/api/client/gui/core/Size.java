package io.github.gtbauke.modernmachines.api.client.gui.core;

public record Size(int width, int height) {
    public static final Size ZERO = new Size(0, 0);

    public Size {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must be non-negative");
        }
    }

    public Size(int size) {
        this(size, size);
    }

    public Size add(Size other) {
        return new Size(this.width + other.width, this.height + other.height);
    }

    public Size subtract(Size other) {
        return new Size(this.width - other.width, this.height - other.height);
    }

    public Size multiply(int factor) {
        return new Size(this.width * factor, this.height * factor);
    }

    public Size divide(int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisor must be non-zero");
        }

        return new Size(this.width / divisor, this.height / divisor);
    }
}
