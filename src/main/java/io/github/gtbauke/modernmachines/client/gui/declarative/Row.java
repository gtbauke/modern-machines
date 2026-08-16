package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;

public class Row extends FlexContainer {

    public Row() {
        super(FlexDirection.ROW);
    }

    public static Row of(UiWidget... children) {
        Row row = new Row();
        if (children != null) {
            for (UiWidget child : children) {
                if (child != null) {
                    row.addChild(child);
                }
            }
        }
        return row;
    }

    public static Row of(Collection<? extends UiWidget> children) {
        Row row = new Row();
        if (children != null) {
            for (UiWidget child : children) {
                if (child != null) {
                    row.addChild(child);
                }
            }
        }
        return row;
    }

    public Row gap(int gap) {
        this.flexNode.setGap(gap);
        return this;
    }

    public Row align(AlignItems align) {
        this.flexNode.setAlignItems(align);
        return this;
    }

    public Row justify(JustifyContent justify) {
        this.flexNode.setJustifyContent(justify);
        return this;
    }

    public Row center() {
        this.flexNode.setAlignItems(AlignItems.CENTER);
        this.flexNode.setJustifyContent(JustifyContent.CENTER);
        return this;
    }

    public Row add(UiWidget... widgets) {
        if (widgets != null) {
            Arrays.stream(widgets).filter(w -> w != null).forEach(this::addChild);
        }
        return this;
    }

    public Row addIf(boolean condition, UiWidget child) {
        if (condition && child != null) {
            addChild(child);
        }
        return this;
    }

    public Row addIf(BooleanSupplier condition, UiWidget child) {
        if (child != null) {
            addChild(Visibility.of(condition, child));
        }
        return this;
    }

    public <T> Row addAll(Iterable<T> items, Function<T, UiWidget> mapper) {
        if (items != null && mapper != null) {
            for (T item : items) {
                UiWidget widget = mapper.apply(item);
                if (widget != null) {
                    addChild(widget);
                }
            }
        }
        return this;
    }

    public <T> Row addAll(T[] items, Function<T, UiWidget> mapper) {
        if (items != null && mapper != null) {
            for (T item : items) {
                UiWidget widget = mapper.apply(item);
                if (widget != null) {
                    addChild(widget);
                }
            }
        }
        return this;
    }
}
