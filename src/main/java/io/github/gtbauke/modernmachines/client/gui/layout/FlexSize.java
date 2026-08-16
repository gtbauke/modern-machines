package io.github.gtbauke.modernmachines.client.gui.layout;

public record FlexSize(Type type, float value) {
    public enum Type {
        AUTO,
        PIXELS,
        PERCENT,
        MATCH_PARENT
    }

    public static final FlexSize AUTO = new FlexSize(Type.AUTO, 0.0f);
    public static final FlexSize MATCH_PARENT = new FlexSize(Type.MATCH_PARENT, 100.0f);

    public static FlexSize px(float pixels) {
        return new FlexSize(Type.PIXELS, pixels);
    }

    public static FlexSize percent(float percent) {
        return new FlexSize(Type.PERCENT, percent);
    }

    public static FlexSize matchParent() {
        return MATCH_PARENT;
    }

    public static FlexSize fromLength(Length length) {
        if (length == null) return AUTO;
        return switch (length.type()) {
            case AUTO -> AUTO;
            case PIXELS -> px(length.value());
            case PERCENT -> percent(length.value());
            case MATCH_PARENT -> MATCH_PARENT;
        };
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
}
