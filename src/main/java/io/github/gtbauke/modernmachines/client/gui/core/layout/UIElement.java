package io.github.gtbauke.modernmachines.client.gui.core.layout;

import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIElement {
    protected Bounds bounds;
    protected Padding padding;

    protected UIElement parent;
    protected final List<UIElement> children = new ArrayList<>();

    protected int backgroundColor = 0;
    protected int borderColor = 0;

    protected int flowWeight = 0;
    protected boolean fillParentWidth = false;
    protected boolean fillParentHeight = false;

    protected boolean dirty = true;

    public UIElement(Bounds bounds, Padding padding, UIElement parent, List<UIElement> children) {
        this.bounds = bounds != null ? bounds : Bounds.EMPTY;
        this.padding = padding != null ? padding : new Padding(0);
        this.parent = parent;

        if (children != null) {
            for (UIElement child : children) {
                this.addChild(child);
            }
        }
    }

    public UIElement(Bounds bounds, Padding padding) {
        this(bounds, padding, null, null);
    }

    public UIElement(Bounds bounds) {
        this(bounds, new Padding(0), null, null);
    }

    public UIElement() {
        this(Bounds.EMPTY, new Padding(0), null, null);
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds != null ? bounds : Bounds.EMPTY;
        markDirty();
    }

    public Position getPosition() {
        return bounds.position();
    }

    public void setPosition(Position position) {
        this.bounds = new Bounds(position, this.bounds.size());
        markDirty();
    }

    public Size getSize() {
        return bounds.size();
    }

    public UIElement setSize(Size size) {
        this.bounds = new Bounds(this.bounds.position(), size);
        markDirty();
        return this;
    }

    public UIElement setSize(int width, int height) {
        return setSize(new Size(width, height));
    }

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = padding != null ? padding : new Padding(0);
        markDirty();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public UIElement setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public UIElement setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public UIElement setBackground(int backgroundColor, int borderColor) {
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        return this;
    }

    public UIElement setBackground(int backgroundColor) {
        return setBackground(backgroundColor, 0);
    }

    public int getFlowWeight() {
        return flowWeight;
    }

    public UIElement setFlowWeight(int flowWeight) {
        this.flowWeight = Math.max(0, flowWeight);
        markDirty();
        return this;
    }

    public boolean isFillParentWidth() {
        return fillParentWidth;
    }

    public UIElement setFillParentWidth(boolean fillParentWidth) {
        this.fillParentWidth = fillParentWidth;
        markDirty();
        return this;
    }

    public boolean isFillParentHeight() {
        return fillParentHeight;
    }

    public UIElement setFillParentHeight(boolean fillParentHeight) {
        this.fillParentHeight = fillParentHeight;
        markDirty();
        return this;
    }

    public UIElement getParent() {
        return parent;
    }

    public List<UIElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(UIElement child) {
        if (child != null && !children.contains(child)) {
            if (child.parent != null) {
                child.parent.removeChild(child);
            }

            children.add(child);
            child.parent = this;
            markDirty();
        }
    }

    public void removeChild(UIElement child) {
        if (child != null && children.remove(child)) {
            child.parent = null;
            markDirty();
        }
    }

    public void clearChildren() {
        for (UIElement child : children) {
            child.parent = null;
        }

        children.clear();
        markDirty();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        this.dirty = true;
        if (parent != null) {
            parent.markDirty();
        }
    }

    /**
     * Calculates the position of this element relative to a specified ancestor element.
     * Takes parent positions and parent paddings into account.
     */
    public Position getPositionRelativeTo(UIElement ancestor) {
        int x = this.bounds.position().x();
        int y = this.bounds.position().y();
        UIElement current = this.parent;
        while (current != null && current != ancestor) {
            x += current.getPosition().x() + current.getPadding().left();
            y += current.getPosition().y() + current.getPadding().top();
            current = current.getParent();
        }
        return new Position(x, y);
    }

    /**
     * Calculates the position of this element relative to the root Window/element in the tree.
     */
    public Position getPositionRelativeToRoot() {
        return getPositionRelativeTo(null);
    }

    /**
     * Calculates the absolute position of this element on the screen.
     */
    public Position getAbsolutePosition() {
        int x = this.bounds.position().x();
        int y = this.bounds.position().y();
        UIElement current = this.parent;
        while (current != null) {
            x += current.getPosition().x() + current.getPadding().left();
            y += current.getPosition().y() + current.getPadding().top();
            current = current.getParent();
        }
        return new Position(x, y);
    }

    /**
     * Calculates the absolute bounds of this element on the screen.
     */
    public Bounds getAbsoluteBounds() {
        return new Bounds(getAbsolutePosition(), this.bounds.size());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var child : children) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (var child : children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        for (var child : children) {
            if (child.mouseDragged(mouseX, mouseY, button, dx, dy)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (var child : children) {
            if (child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Phase 1: Size calculation.
     * Recursively computes sizes of this element and its children.
     */
    public void calculateSize() {
        for (UIElement child : children) {
            child.calculateSize();
        }
    }

    /**
     * Phase 2: Layout calculation.
     * Resolves child positions and bounds relative to the parent's content-box.
     */
    public void calculateLayout() {
        for (UIElement child : children) {
            child.calculateLayout();
        }
        this.dirty = false;
    }

    /**
     * Phase 3: Render traversal.
     * Resolves absolute screen bounds and renders self + children.
     *
     * @param graphics Mojang GuiGraphicsExtractor
     * @param parentOrigin Absolute screen position of the parent's content area
     * @param mouseX Current mouse X
     * @param mouseY Current mouse Y
     * @param partialTick Render partial tick
     */
    public void render(GuiGraphicsExtractor graphics, Position parentOrigin, int mouseX, int mouseY, float partialTick) {
        Position absPos = parentOrigin.offset(this.bounds.position());
        Bounds absBounds = new Bounds(absPos, this.bounds.size());

        if ((backgroundColor >>> 24) != 0) {
            GUIRenderHelper.drawRect(graphics, absBounds, backgroundColor);
        }
        if ((borderColor >>> 24) != 0) {
            GUIRenderHelper.drawRectOutline(graphics, absBounds, borderColor);
        }

        renderSelf(graphics, absBounds, mouseX, mouseY, partialTick);

        Position contentOrigin = absPos.offset(new Position(this.padding.left(), this.padding.top()));
        for (UIElement child : children) {
            child.render(graphics, contentOrigin, mouseX, mouseY, partialTick);
        }
    }

    /**
     * Custom rendering logic for this specific element.
     * Override in subclasses to draw visuals (rectangles, nine-slices, text, etc.).
     */
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        // Base implementation does nothing
    }
}
