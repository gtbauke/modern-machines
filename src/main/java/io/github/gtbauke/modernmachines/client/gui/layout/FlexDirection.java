package io.github.gtbauke.modernmachines.client.gui.layout;

public enum FlexDirection {
    ROW,
    ROW_REVERSE,
    COLUMN,
    COLUMN_REVERSE;

    public boolean isHorizontal() {
        return this == ROW || this == ROW_REVERSE;
    }

    public boolean isReversed() {
        return this == ROW_REVERSE || this == COLUMN_REVERSE;
    }
}
