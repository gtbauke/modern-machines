package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.*;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a UI element with a position, size, and padding.
 * The position always refers to the top-left corner of the element.
 */
public abstract class UIElement {
    protected Position position;
    protected Size size;
    protected Padding padding;
    protected Visibility visibility = Visibility.VISIBLE;
    protected Color backgroundColor = Color.TRANSPARENT;
    protected Color borderColor = Color.TRANSPARENT;
    protected int flowWeight = 0;
    protected boolean autoSize = false;

    protected List<UIElement> children = new ArrayList<>();

    public UIElement(Position position, Size size, Padding padding) {
        this.position = position;
        this.size = size;
        this.padding = padding;
    }

    public UIElement() {
        this(Position.ZERO, Size.ZERO, Padding.ZERO);
    }

    public UIElement setPosition(Position position) {
        this.position = position;
        return this;
    }

    public UIElement setSize(Size size) {
        this.size = size;
        this.autoSize = false;

        return this;
    }

    public UIElement setPadding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public UIElement setVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public UIElement setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public UIElement setBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    public UIElement setFlowWeight(int weight) {
        this.flowWeight = weight;
        return this;
    }

    public UIElement setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
        return this;
    }

    public int width() {
        return size.width();
    }

    public int height() {
        return size.height();
    }

    public int left() {
        return position.x();
    }

    public int right() {
        return position.x() + size.width();
    }

    public int top() {
        return position.y();
    }

    public int bottom() {
        return position.y() + size.height();
    }

    public int zIndex() {
        return position.zIndex();
    }

    public int flowWeight() {
        return flowWeight;
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public boolean isHidden() {
        return this.visibility == Visibility.HIDDEN;
    }

    public UIElement addChild(UIElement child) {
        this.children.add(child);
        return this;
    }

    protected Position calculateChildStartPosition() {
        int firstChildX = this.left() + this.padding.left();
        int firstChildY = this.top() + this.padding.top();

        return new Position(firstChildX, firstChildY, this.position.zIndex() + 1);
    }

    protected Bounds getBounds() {
        return new Bounds(this.left(), this.top(), this.right(), this.bottom());
    }

    public void calculateSize() {
        for (var child : children) {
            child.calculateSize();
        }
    }

    public void calculateLayout() {
        for (var child : children) {
            child.calculateLayout();
        }
    }

    protected void renderChildren(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {}

    public void render(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        if (this.visibility == Visibility.HIDDEN) {
            return;
        }

        if (this.backgroundColor.alpha() > 0) {
            GUIRenderHelper.drawRect(graphics, this.getBounds(), this.backgroundColor.toARGB());
        }

        if (this.borderColor.alpha() > 0) {
            GUIRenderHelper.drawRectOutline(graphics, this.getBounds(), this.borderColor.toARGB());
        }

        this.renderChildren(graphics, mousePos, partialTick);
    }
}
