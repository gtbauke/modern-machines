package io.github.gtbauke.modernmachines.client.gui.core.layout;

public record Position(int x, int y) {
    public static final Position ZERO = new Position(0, 0);

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position offset(Position other) {
        return new Position(this.x + other.x, this.y + other.y);
    }
}
