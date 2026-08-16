package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.Collections;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Padding extends UiWidget {
    private final UiWidget child;

    public Padding(FlexInsets insets, UiWidget child) {
        this.child = child;
        this.flexNode.setDirection(FlexDirection.COLUMN);
        this.flexNode.setPadding(insets);
        if (child != null) {
            this.child.setParent(this);
            this.flexNode.addChild(child.getFlexNode());
        }
    }

    public static Padding of(FlexInsets insets, UiWidget child) {
        return new Padding(insets, child);
    }

    public static Padding all(int padding, UiWidget child) {
        return new Padding(FlexInsets.all(padding), child);
    }

    public static Padding symmetric(int vertical, int horizontal, UiWidget child) {
        return new Padding(FlexInsets.symmetric(vertical, horizontal), child);
    }

    public static Padding of(int top, int right, int bottom, int left, UiWidget child) {
        return new Padding(FlexInsets.of(top, right, bottom, left), child);
    }

    public UiWidget getChild() {
        return child;
    }

    @Override
    public List<UiWidget> getChildren() {
        return child != null ? List.of(child) : Collections.emptyList();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        if (child != null && child.isVisible()) {
            child.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        if (child != null && child.isVisible()) {
            child.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        if (child != null) {
            child.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (child != null && child.isVisible() && child.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
