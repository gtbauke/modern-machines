package io.github.gtbauke.modernmachines.client.gui.layout;

public record Length(Type type, float value) {
    public enum Type {
        AUTO,
        PIXELS,
        PERCENT,
        MATCH_PARENT
    }

    public static final Length AUTO = new Length(Type.AUTO, 0.0f);
    public static final Length MATCH_PARENT = new Length(Type.MATCH_PARENT, 100.0f);

    public static Length px(float pixels) {
        return new Length(Type.PIXELS, pixels);
    }

    public static Length pct(float percent) {
        return new Length(Type.PERCENT, percent);
    }

    public static Length matchParent() {
        return MATCH_PARENT;
    }

    public static Length auto() {
        return AUTO;
    }

    public float resolve(float parentDimension) {
        return switch (type) {
            case AUTO -> 0.0f;
            case PIXELS -> value;
            case PERCENT, MATCH_PARENT -> (parentDimension * value) / 100.0f;
        };
    }

    public boolean isAuto() {
        return type == Type.AUTO;
    }

    public boolean isRelative() {
        return type == Type.PERCENT || type == Type.MATCH_PARENT;
    }
}
