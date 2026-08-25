package io.github.gtbauke.modernmachines.api.client.gui.core;

/**
 * Represents a position in 2D space with an optional z-index for layering.
 * @param x The x-coordinate of the position.
 * @param y The y-coordinate of the position.
 * @param zIndex The z-index for layering, where higher values are rendered above lower values.
 */
public record Position(int x, int y, int zIndex) {
    public static final Position ZERO = new Position(0, 0, 0);

    public Position(int x, int y) {
        this(x, y, 0);
    }

    public Position add(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy, this.zIndex);
    }

    public Position add(Position other) {
        return new Position(this.x + other.x, this.y + other.y, this.zIndex);
    }

    public Position withZIndex(int zIndex) {
        return new Position(this.x, this.y, zIndex);
    }

    public Position withX(int x) {
        return new Position(x, this.y, this.zIndex);
    }

    public Position withY(int y) {
        return new Position(this.x, y, this.zIndex);
    }
}
