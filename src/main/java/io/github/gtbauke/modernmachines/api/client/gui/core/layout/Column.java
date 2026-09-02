package io.github.gtbauke.modernmachines.api.client.gui.core.layout;

import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;

import java.util.stream.Stream;

public class Column extends FlexContainer {

    public Column(int gap, AlignItems alignItems, JustifyContent justifyContent, UIElement... children) {
        super(FlexDirection.COLUMN, justifyContent, alignItems, gap);
        if (children != null) {
            for (var child : children) {
                this.addChild(child);
            }
        }
    }

    public Column(int gap, AlignItems alignItems, UIElement... children) {
        this(gap, alignItems, JustifyContent.START, children);
    }

    public Column(int gap, UIElement... children) {
        this(gap, AlignItems.CENTER, JustifyContent.START, children);
    }

    public Column() {
        this(0, AlignItems.CENTER, JustifyContent.START);
    }

    public static Column of(int gap, UIElement... children) {
        return new Column(gap, children);
    }

    public static Column of(UIElement... children) {
        return new Column(0, children);
    }

    public static Column of(int gap, AlignItems alignItems, UIElement... children) {
        return new Column(gap, alignItems, children);
    }

    public static Column of(int gap, AlignItems alignItems, JustifyContent justifyContent, UIElement... children) {
        return new Column(gap, alignItems, justifyContent, children);
    }

    public static Column of(int gap, JustifyContent justifyContent, UIElement... children) {
        return new Column(gap, AlignItems.CENTER, justifyContent, children);
    }

    public static Column of(int gap, Stream<UIElement> childrenStream) {
        var column = new Column(gap);
        if (childrenStream != null) {
            childrenStream.forEach(column::addChild);
        }

        return column;
    }
}
