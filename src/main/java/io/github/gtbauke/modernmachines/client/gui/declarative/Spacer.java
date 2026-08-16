package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;

public class Spacer extends UiWidget {

    public Spacer() {
        this(1.0f);
    }

    public Spacer(float flexGrow) {
        this.flexNode.setFlexGrow(flexGrow);
    }

    public static Spacer of() {
        return new Spacer(1.0f);
    }

    public static Spacer of(float flexGrow) {
        return new Spacer(flexGrow);
    }

    public static Spacer grow(float flexGrow) {
        return new Spacer(flexGrow);
    }
}
