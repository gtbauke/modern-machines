package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;

public class Row extends FlexContainer {
    public Row(int gap, AlignItems alignItems, JustifyContent justifyContent, Bounds bounds, Padding padding) {
        super(FlexDirection.ROW, gap, alignItems, justifyContent, bounds, padding);
    }

    public Row(int gap, AlignItems alignItems, JustifyContent justifyContent) {
        super(FlexDirection.ROW, gap, alignItems, justifyContent);
    }

    public Row(int gap, AlignItems alignItems) {
        super(FlexDirection.ROW, gap, alignItems, JustifyContent.START);
    }

    public Row(int gap) {
        super(FlexDirection.ROW, gap, AlignItems.START, JustifyContent.START);
    }

    public Row() {
        super(FlexDirection.ROW, 0, AlignItems.START, JustifyContent.START);
    }

    public Row(int gap, UIElement... children) {
        super(FlexDirection.ROW, gap, AlignItems.START, JustifyContent.START);
        if (children != null) {
            for (UIElement child : children) {
                this.addChild(child);
            }
        }
    }

    public static Row of(int gap, UIElement... children) {
        return new Row(gap, children);
    }

    public static Row of(UIElement... children) {
        return new Row(0, children);
    }

    public static Row of(int gap, AlignItems alignItems, UIElement... children) {
        Row row = new Row(gap, alignItems);
        if (children != null) {
            for (UIElement child : children) {
                row.addChild(child);
            }
        }
        return row;
    }

    public static Row of(int gap, AlignItems alignItems, JustifyContent justifyContent, UIElement... children) {
        Row row = new Row(gap, alignItems, justifyContent);
        if (children != null) {
            for (UIElement child : children) {
                row.addChild(child);
            }
        }
        return row;
    }

    public static Row of(int gap, JustifyContent justifyContent, UIElement... children) {
        return of(gap, AlignItems.START, justifyContent, children);
    }
}
