package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;

public final class For {

    private For() {}

    public static <T> List<UiWidget> each(Iterable<T> items, Function<T, UiWidget> mapper) {
        List<UiWidget> result = new ArrayList<>();
        if (items != null && mapper != null) {
            for (T item : items) {
                UiWidget widget = mapper.apply(item);
                if (widget != null) {
                    result.add(widget);
                }
            }
        }
        return result;
    }

    public static <T> List<UiWidget> each(T[] items, Function<T, UiWidget> mapper) {
        List<UiWidget> result = new ArrayList<>();
        if (items != null && mapper != null) {
            for (T item : items) {
                UiWidget widget = mapper.apply(item);
                if (widget != null) {
                    result.add(widget);
                }
            }
        }
        return result;
    }
}
