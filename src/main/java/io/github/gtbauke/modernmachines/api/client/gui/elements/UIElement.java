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

    protected Color topBorderColor = Color.TRANSPARENT;
    protected Color rightBorderColor = Color.TRANSPARENT;
    protected Color bottomBorderColor = Color.TRANSPARENT;
    protected Color leftBorderColor = Color.TRANSPARENT;

    protected int flowWeight = 0;
    protected boolean autoSize = false;

    protected UIElement parent;
    protected List<UIElement> children = new ArrayList<>();

    public UIElement(Position position, Size size, Padding padding) {
        this.position = position;
        this.size = size;
        this.padding = padding;
    }

    public UIElement() {
        this(Position.ZERO, Size.ZERO, Padding.ZERO);
    }

    public UIElement getParent() {
        return parent;
    }

    public void setParent(UIElement parent) {
        this.parent = parent;
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

    public Visibility getVisibility() {
        return visibility;
    }

    public UIElement setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public UIElement setBorderColor(Color color) {
        this.topBorderColor = color;
        this.rightBorderColor = color;
        this.bottomBorderColor = color;
        this.leftBorderColor = color;
        return this;
    }

    public UIElement setTopBorderColor(Color color) {
        this.topBorderColor = color;
        return this;
    }

    public UIElement setRightBorderColor(Color color) {
        this.rightBorderColor = color;
        return this;
    }

    public UIElement setBottomBorderColor(Color color) {
        this.bottomBorderColor = color;
        return this;
    }

    public UIElement setLeftBorderColor(Color color) {
        this.leftBorderColor = color;
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

    public Position getPosition() {
        return position;
    }

    public Size getSize() {
        return size;
    }

    public Padding getPadding() {
        return padding;
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

    public boolean isEffectivelyVisible() {
        if (this.visibility == Visibility.HIDDEN) {
            return false;
        }

        if (this.parent != null) {
            return this.parent.isEffectivelyVisible();
        }

        return true;
    }

    public Position getRootPosition() {
        if (this.parent != null) {
            return this.parent.getRootPosition();
        }

        return this.position;
    }

    public List<UIElement> getChildren() {
        return children;
    }

    public UIElement addChild(UIElement child) {
        if (child != null) {
            child.setParent(this);
            this.children.add(child);
        }

        return this;
    }

    public UIElement removeChild(UIElement child) {
        if (child != null) {
            this.children.remove(child);
            child.setParent(null);
        }

        return this;
    }

    public UIElement clearChildren() {
        for (var child : children) {
            child.setParent(null);
        }

        this.children.clear();
        return this;
    }

    protected Position calculateChildStartPosition() {
        int firstChildX = this.left() + this.padding.left();
        int firstChildY = this.top() + this.padding.top();

        return new Position(firstChildX, firstChildY, this.position.zIndex() + 1);
    }

    public Bounds getBounds() {
        return new Bounds(this.left(), this.top(), this.right(), this.bottom());
    }

    public boolean isHovered(Position mousePos) {
        if (mousePos == null) {
            return false;
        }

        return this.getBounds().contains(mousePos);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isEffectivelyVisible()) {
            return false;
        }

        for (var i = this.children.size() - 1; i >= 0; i--) {
            if (this.children.get(i).mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isEffectivelyVisible()) {
            return false;
        }

        for (var i = this.children.size() - 1; i >= 0; i--) {
            if (this.children.get(i).mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
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

        if (this.topBorderColor.alpha() > 0) {
            GUIRenderHelper.drawRectOutline(graphics, this.getBounds(), this.topBorderColor.toARGB());
        }

        if (this.rightBorderColor.alpha() > 0) {
            GUIRenderHelper.drawRectOutline(graphics, this.getBounds(), this.rightBorderColor.toARGB());
        }

        if (this.bottomBorderColor.alpha() > 0) {
            GUIRenderHelper.drawRectOutline(graphics, this.getBounds(), this.bottomBorderColor.toARGB());
        }

        if (this.leftBorderColor.alpha() > 0) {
            GUIRenderHelper.drawRectOutline(graphics, this.getBounds(), this.leftBorderColor.toARGB());
        }

        this.renderChildren(graphics, mousePos, partialTick);
    }
}
