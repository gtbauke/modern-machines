package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Visibility;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.FlexContainer;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.FlexDirection;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class OrePanel extends FlexContainer {
    public enum Style {
        PRIMARY_BACKGROUND,
        SECONDARY_PANEL
    }

    private final Style style;

    public OrePanel(Style style, FlexDirection flexDirection, JustifyContent justifyContent, AlignItems alignItems, int gap) {
        super(flexDirection, justifyContent, alignItems, gap);
        this.style = style != null ? style : Style.PRIMARY_BACKGROUND;
        this.setPadding(new Padding(4));
    }

    public OrePanel(Style style) {
        this(style, FlexDirection.COLUMN, JustifyContent.START, AlignItems.CENTER, 0);
    }

    public static OrePanel background() {
        return new OrePanel(Style.PRIMARY_BACKGROUND);
    }

    public static OrePanel background(UIElement... children) {
        var panel = new OrePanel(Style.PRIMARY_BACKGROUND);

        if (children != null) {
            for (var child : children) {
                panel.addChild(child);
            }
        }

        return panel;
    }

    public static OrePanel panel() {
        return new OrePanel(Style.SECONDARY_PANEL);
    }

    public static OrePanel panel(UIElement... children) {
        var panel = new OrePanel(Style.SECONDARY_PANEL);

        if (children != null) {
            for (var child : children) {
                panel.addChild(child);
            }
        }

        return panel;
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (this.visibility == Visibility.HIDDEN) {
            return;
        }

        if (style == Style.PRIMARY_BACKGROUND) {
            GUIRenderHelper.drawOreUIBackground(graphics, this.getBounds());
        } else {
            GUIRenderHelper.drawOreUIPanel(graphics, this.getBounds());
        }

        this.renderChildren(graphics, mousePos, partialTick);
    }
}
