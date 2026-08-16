package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Stack extends UiWidget {
    private final List<UiWidget> children = new ArrayList<>();

    public Stack() {
    }

    public static Stack of(UiWidget... children) {
        Stack stack = new Stack();
        if (children != null) {
            for (UiWidget child : children) {
                if (child != null) {
                    stack.addChild(child);
                }
            }
        }
        return stack;
    }

    public static Stack of(Collection<? extends UiWidget> children) {
        Stack stack = new Stack();
        if (children != null) {
            for (UiWidget child : children) {
                if (child != null) {
                    stack.addChild(child);
                }
            }
        }
        return stack;
    }

    public Stack addChild(UiWidget child) {
        if (child != null) {
            this.children.add(child);
            child.setParent(this);
            this.flexNode.addChild(child.getFlexNode());
        }
        return this;
    }

    public Stack add(UiWidget... children) {
        if (children != null) {
            Arrays.stream(children).filter(c -> c != null).forEach(this::addChild);
        }
        return this;
    }

    public Stack addIf(boolean condition, UiWidget child) {
        if (condition && child != null) {
            addChild(child);
        }
        return this;
    }

    public Stack addIf(BooleanSupplier condition, UiWidget child) {
        if (child != null) {
            addChild(Visibility.of(condition, child));
        }
        return this;
    }

    public <T> Stack addAll(Iterable<T> items, Function<T, UiWidget> mapper) {
        if (items != null && mapper != null) {
            for (T item : items) {
                UiWidget widget = mapper.apply(item);
                if (widget != null) {
                    addChild(widget);
                }
            }
        }
        return this;
    }

    public <T> Stack addAll(T[] items, Function<T, UiWidget> mapper) {
        if (items != null && mapper != null) {
            for (T item : items) {
                UiWidget widget = mapper.apply(item);
                if (widget != null) {
                    addChild(widget);
                }
            }
        }
        return this;
    }

    @Override
    public List<UiWidget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        for (UiWidget child : children) {
            if (child.isVisible()) {
                child.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        for (UiWidget child : children) {
            if (child.isVisible()) {
                child.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        for (UiWidget child : children) {
            child.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = children.size() - 1; i >= 0; i--) {
            UiWidget child = children.get(i);
            if (child.isVisible() && child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
