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

public class Column extends FlexContainer {

    public Column() {
        super(FlexDirection.COLUMN);
    }

    public static Column of(UiWidget... children) {
        Column col = new Column();
        if (children != null) {
            for (UiWidget child : children) {
                if (child != null) {
                    col.addChild(child);
                }
            }
        }
        return col;
    }

    public static Column of(Collection<? extends UiWidget> children) {
        Column col = new Column();
        if (children != null) {
            for (UiWidget child : children) {
                if (child != null) {
                    col.addChild(child);
                }
            }
        }
        return col;
    }

    public Column gap(int gap) {
        this.flexNode.setGap(gap);
        return this;
    }

    public Column align(AlignItems align) {
        this.flexNode.setAlignItems(align);
        return this;
    }

    public Column justify(JustifyContent justify) {
        this.flexNode.setJustifyContent(justify);
        return this;
    }

    public Column center() {
        this.flexNode.setAlignItems(AlignItems.CENTER);
        this.flexNode.setJustifyContent(JustifyContent.CENTER);
        return this;
    }

    public Column add(UiWidget... widgets) {
        if (widgets != null) {
            Arrays.stream(widgets).filter(w -> w != null).forEach(this::addChild);
        }
        return this;
    }

    public Column addIf(boolean condition, UiWidget child) {
        if (condition && child != null) {
            addChild(child);
        }
        return this;
    }

    public Column addIf(BooleanSupplier condition, UiWidget child) {
        if (child != null) {
            addChild(Visibility.of(condition, child));
        }
        return this;
    }

    public <T> Column addAll(Iterable<T> items, Function<T, UiWidget> mapper) {
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

    public <T> Column addAll(T[] items, Function<T, UiWidget> mapper) {
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
