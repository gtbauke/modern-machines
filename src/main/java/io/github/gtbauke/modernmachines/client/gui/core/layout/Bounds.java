package io.github.gtbauke.modernmachines.client.gui.core.layout;

public record Bounds(Position position, Size size) {
    public static final Bounds EMPTY = new Bounds(Position.ZERO, Size.ZERO);

    public Bounds(Position position, Size size) {
        this.position = position;
        this.size = size;
    }

    public int right() {
        return position.x() + size.width();
    }

    public int bottom() {
        return position.y() + size.height();
    }

    public boolean contains(Position point) {
        return point.x() >= position.x() && point.x() <= right() &&
               point.y() >= position.y() && point.y() <= bottom();
    }

    public boolean intersects(Bounds other) {
        return !(other.position.x() > right() ||
                 other.right() < position.x() ||
                 other.position.y() > bottom() ||
                 other.bottom() < position.y());
    }

    public Bounds offset(Position offset) {
        return new Bounds(position.offset(offset), size);
    }

    public Bounds resize(Size newSize) {
        return new Bounds(position, newSize);
    }

    public Bounds expand(Size expansion) {
        return new Bounds(position, new Size(size.width() + expansion.width(), size.height() + expansion.height()));
    }
}
