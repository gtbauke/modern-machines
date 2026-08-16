package io.github.gtbauke.modernmachines.client.gui.windows;

import io.github.gtbauke.modernmachines.client.gui.core.element.SideTabElement;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Window extends UIElement {
    public static final int DEFAULT_HEADER_HEIGHT = 18;

    protected Component title;
    protected boolean hasHeader = false;
    protected int headerHeight = DEFAULT_HEADER_HEIGHT;
    protected boolean draggable = false;
    protected boolean visible = true;
    protected boolean isDragging = false;
    protected double dragOffsetX;
    protected double dragOffsetY;
    protected boolean hasCloseButton = false;
    protected UIElement content;

    public Window(Component title, Bounds bounds, Padding padding, UIElement parent, List<UIElement> children) {
        super(bounds, padding, parent, children);
        this.title = title;
    }

    public Window(Component title, Bounds bounds, Padding padding) {
        super(bounds, padding);
        this.title = title;
    }

    public Window(Component title, Bounds bounds) {
        super(bounds, new Padding(0));
        this.title = title;
    }

    public Window(Component title, int width, int height) {
        this(title, new Bounds(Position.ZERO, new Size(width, height)), new Padding(0));
    }

    public Window(Component title, Position position, Size size) {
        this(title, new Bounds(position, size), new Padding(0));
    }

    public Component getTitle() {
        return title;
    }

    public Window setTitle(Component title) {
        this.title = title;
        markDirty();
        return this;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public Window setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        updateHeaderPadding();
        return this;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public Window setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
        updateHeaderPadding();
        return this;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public Window setDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public Window setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            markDirty();
            calculateSize();
            calculateLayout();
        }
        return this;
    }

    public boolean isHasCloseButton() {
        return hasCloseButton;
    }

    public Window setHasCloseButton(boolean hasCloseButton) {
        this.hasCloseButton = hasCloseButton;
        return this;
    }

    public UIElement getContent() {
        return content;
    }

    public Window setContent(UIElement content) {
        if (this.content != null) {
            removeChild(this.content);
        }
        this.content = content;
        if (content != null) {
            addChild(content);
        }
        return this;
    }

    private void updateHeaderPadding() {
        int topPad = hasHeader ? Math.max(headerHeight + 2, padding.top()) : padding.top();
        this.padding = new Padding(topPad, padding.right(), padding.bottom(), padding.left());
        markDirty();
    }

    public Bounds getHeaderBounds() {
        if (!hasHeader) return Bounds.EMPTY;
        return new Bounds(this.bounds.position(), new Size(this.bounds.size().width(), headerHeight));
    }

    public Bounds getCloseButtonBounds() {
        if (!hasCloseButton || !hasHeader) return Bounds.EMPTY;
        int btnSize = 12;
        int x = this.bounds.position().x() + this.bounds.size().width() - btnSize - 3;
        int y = this.bounds.position().y() + (headerHeight - btnSize) / 2;
        return new Bounds(new Position(x, y), new Size(btnSize, btnSize));
    }

    public boolean handleChildClick(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        for (UIElement child : children) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleHeaderClick(double mouseX, double mouseY, int button) {
        if (!visible || !hasHeader || button != 0) return false;

        Position clickPos = new Position((int) mouseX, (int) mouseY);

        // 1. Check close button
        if (hasCloseButton) {
            if (getCloseButtonBounds().contains(clickPos)) {
                setVisible(false);
                return true;
            }
        }

        // 2. Check header drag
        if (draggable) {
            if (getHeaderBounds().contains(clickPos)) {
                this.isDragging = true;
                this.dragOffsetX = mouseX - bounds.position().x();
                this.dragOffsetY = mouseY - bounds.position().y();
                return true;
            }
        }

        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        if (handleChildClick(mouseX, mouseY, button)) return true;
        if (handleHeaderClick(mouseX, mouseY, button)) return true;
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!visible) return false;

        if (isDragging && button == 0) {
            int newX = (int) (mouseX - dragOffsetX);
            int newY = (int) (mouseY - dragOffsetY);
            setPosition(new Position(newX, newY));
            calculateLayout();
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging && button == 0) {
            this.isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position parentOrigin, int mouseX, int mouseY, float partialTick) {
        if (!visible) {
            return;
        }
        super.render(graphics, parentOrigin, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int x = absoluteBounds.position().x();
        int y = absoluteBounds.position().y();
        int w = absoluteBounds.size().width();
        int h = absoluteBounds.size().height();

        // 1. Drop shadow for floating windows
        if (hasHeader || draggable) {
            GUIRenderHelper.drawDropShadow(graphics, absoluteBounds, 0x50000000, 3);
        }

        // 2. Base window background fill and border
        int bg = backgroundColor != 0 ? backgroundColor : 0xFFC6C6C6;
        int border = borderColor != 0 ? borderColor : 0xFF373737;
        GUIRenderHelper.drawRect(graphics, absoluteBounds, bg);
        GUIRenderHelper.drawRectOutline(graphics, absoluteBounds, border);

        // 3. Header bar (if present)
        if (hasHeader) {
            // Header background
            graphics.fill(x + 1, y + 1, x + w - 1, y + headerHeight, 0xFFB8B8B8);
            // Header bottom border
            graphics.fill(x, y + headerHeight, x + w, y + headerHeight + 1, border);

            // Header Title text
            if (title != null) {
                Font font = Minecraft.getInstance().font;
                graphics.text(font, title, x + 5, y + (headerHeight - 8) / 2, 0xFF404040, false);
            }

            // Close button ('✕')
            if (hasCloseButton) {
                Bounds btnBounds = new Bounds(new Position(x + w - 14, y + (headerHeight - 12) / 2), new Size(12, 12));
                boolean hovered = mouseX >= btnBounds.position().x() && mouseX < btnBounds.right()
                               && mouseY >= btnBounds.position().y() && mouseY < btnBounds.bottom();

                int btnBg = hovered ? 0xFFE81123 : 0xFFCCCCCC;
                int btnTextColor = hovered ? 0xFFFFFFFF : 0xFF333333;

                graphics.fill(btnBounds.position().x(), btnBounds.position().y(), btnBounds.right(), btnBounds.bottom(), btnBg);
                GUIRenderHelper.drawRectOutline(graphics, btnBounds, 0xFF555555);

                Font font = Minecraft.getInstance().font;
                graphics.text(font, Component.literal("✕"), btnBounds.position().x() + 3, btnBounds.position().y() + 2, btnTextColor, false);
            }
        }
    }
}
