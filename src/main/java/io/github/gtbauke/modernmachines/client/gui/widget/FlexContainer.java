package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class FlexContainer extends UiWidget {
    protected final List<UiWidget> children = new ArrayList<>();

    public FlexContainer() {
        this(FlexDirection.COLUMN);
    }

    public FlexContainer(FlexDirection direction) {
        flexNode.setDirection(direction);
    }

    public static FlexContainer row() {
        return new FlexContainer(FlexDirection.ROW);
    }

    public static FlexContainer column() {
        return new FlexContainer(FlexDirection.COLUMN);
    }

    public List<UiWidget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public FlexContainer addChild(UiWidget child) {
        if (child.getParent() instanceof FlexContainer currentParent) {
            currentParent.removeChild(child);
        }
        child.setParent(this);
        children.add(child);
        flexNode.addChild(child.getFlexNode());
        return this;
    }

    public FlexContainer removeChild(UiWidget child) {
        if (children.remove(child)) {
            child.setParent(null);
            flexNode.removeChild(child.getFlexNode());
        }
        return this;
    }

    public void clearChildren() {
        for (UiWidget child : children) {
            child.setParent(null);
        }
        children.clear();
        flexNode.clearChildren();
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        if (visible) {
            for (UiWidget child : children) {
                child.updateHoverState(mouseX, mouseY);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        for (UiWidget child : children) {
            if (child.isVisible()) {
                child.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        for (UiWidget child : children) {
            if (child.isVisible()) {
                child.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (!visible) return Collections.emptyList();
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.isHovered()) {
                List<Component> tooltip = child.getTooltip(mouseX, mouseY);
                if (!tooltip.isEmpty()) {
                    return tooltip;
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.isEnabled() && child.getBounds().contains(mouseX, mouseY)) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
}
