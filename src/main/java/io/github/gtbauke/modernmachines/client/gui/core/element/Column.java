package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;

public class Column extends FlexContainer {
    public Column(int gap, AlignItems alignItems, JustifyContent justifyContent, Bounds bounds, Padding padding) {
        super(FlexDirection.COLUMN, gap, alignItems, justifyContent, bounds, padding);
    }

    public Column(int gap, AlignItems alignItems, JustifyContent justifyContent) {
        super(FlexDirection.COLUMN, gap, alignItems, justifyContent);
    }

    public Column(int gap, AlignItems alignItems) {
        super(FlexDirection.COLUMN, gap, alignItems, JustifyContent.START);
    }

    public Column(int gap) {
        super(FlexDirection.COLUMN, gap, AlignItems.START, JustifyContent.START);
    }

    public Column() {
        super(FlexDirection.COLUMN, 0, AlignItems.START, JustifyContent.START);
    }

    public Column(int gap, UIElement... children) {
        super(FlexDirection.COLUMN, gap, AlignItems.START, JustifyContent.START);
        if (children != null) {
            for (UIElement child : children) {
                this.addChild(child);
            }
        }
    }

    public static Column of(int gap, UIElement... children) {
        return new Column(gap, children);
    }

    public static Column of(UIElement... children) {
        return new Column(0, children);
    }

    public static Column of(int gap, AlignItems alignItems, UIElement... children) {
        Column column = new Column(gap, alignItems);
        if (children != null) {
            for (UIElement child : children) {
                column.addChild(child);
            }
        }
        return column;
    }

    public static Column of(int gap, AlignItems alignItems, JustifyContent justifyContent, UIElement... children) {
        Column column = new Column(gap, alignItems, justifyContent);
        if (children != null) {
            for (UIElement child : children) {
                column.addChild(child);
            }
        }
        return column;
    }

    public static Column of(int gap, JustifyContent justifyContent, UIElement... children) {
        return of(gap, AlignItems.START, justifyContent, children);
    }
}
