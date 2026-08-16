package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.Collections;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Expanded extends UiWidget {
    private final UiWidget child;

    public Expanded(float flexGrow, UiWidget child) {
        this.child = child;
        this.flexNode.setFlexGrow(flexGrow);
        if (child != null) {
            this.child.setParent(this);
            this.flexNode.addChild(child.getFlexNode());
        }
    }

    public static Expanded of(UiWidget child) {
        return new Expanded(1.0f, child);
    }

    public static Expanded of(float flexGrow, UiWidget child) {
        return new Expanded(flexGrow, child);
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
