package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Visibility extends UiWidget {
    private final UiWidget child;
    private final BooleanSupplier condition;

    public Visibility(BooleanSupplier condition, UiWidget child) {
        this.condition = condition;
        this.child = child;
        if (child != null) {
            this.child.setParent(this);
            this.flexNode.addChild(child.getFlexNode());
        }
    }

    public static Visibility of(BooleanSupplier condition, UiWidget child) {
        return new Visibility(condition, child);
    }

    public static Visibility of(boolean visible, UiWidget child) {
        return new Visibility(() -> visible, child);
    }

    @Override
    public boolean isVisible() {
        return (condition == null || condition.getAsBoolean()) && (visibilitySupplier == null || visibilitySupplier.getAsBoolean()) && visible;
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
        if (isVisible() && child != null) {
            child.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isVisible() && child != null && child.isVisible() && child.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
