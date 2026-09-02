package io.github.gtbauke.modernmachines.api.client.gui.core.layout;

import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;

import java.util.stream.Stream;

public class Row extends FlexContainer {

    public Row(int gap, AlignItems alignItems, JustifyContent justifyContent, UIElement... children) {
        super(FlexDirection.ROW, justifyContent, alignItems, gap);
        if (children != null) {
            for (var child : children) {
                this.addChild(child);
            }
        }
    }

    public Row(int gap, AlignItems alignItems, UIElement... children) {
        this(gap, alignItems, JustifyContent.START, children);
    }

    public Row(int gap, UIElement... children) {
        this(gap, AlignItems.CENTER, JustifyContent.START, children);
    }

    public Row() {
        this(0, AlignItems.CENTER, JustifyContent.START);
    }

    public static Row of(int gap, UIElement... children) {
        return new Row(gap, children);
    }

    public static Row of(UIElement... children) {
        return new Row(0, children);
    }

    public static Row of(int gap, AlignItems alignItems, UIElement... children) {
        return new Row(gap, alignItems, children);
    }

    public static Row of(int gap, AlignItems alignItems, JustifyContent justifyContent, UIElement... children) {
        return new Row(gap, alignItems, justifyContent, children);
    }

    public static Row of(int gap, JustifyContent justifyContent, UIElement... children) {
        return new Row(gap, AlignItems.CENTER, justifyContent, children);
    }

    public static Row of(int gap, Stream<UIElement> childrenStream) {
        var row = new Row(gap);
        if (childrenStream != null) {
            childrenStream.forEach(row::addChild);
        }

        return row;
    }
}
