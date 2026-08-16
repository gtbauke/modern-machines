package io.github.gtbauke.modernmachines.client.gui.window;

import java.util.ArrayList;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class WindowWidget extends UiWidget {
    protected Component title;

    protected final FlexContainer headerContainer;
    protected final FlexContainer headerButtonsContainer;
    protected final FlexContainer contentContainer;
    protected final List<SideTabWidget> sideTabs = new ArrayList<>();
    protected final List<SideTabButtonWidget> tabButtons = new ArrayList<>();

    protected ButtonWidget minimizeButton;
    protected ButtonWidget closeButton;
    protected Runnable onCloseCallback;
    protected Runnable onMinimizeCallback;

    protected int posX = 0;
    protected int posY = 0;
    protected int windowWidth = 176;
    protected int windowHeight = 166;

    protected boolean draggable = false;
    protected boolean isDragging = false;
    protected double dragOffsetX = 0;
    protected double dragOffsetY = 0;

    protected boolean autoWidth = true;
    protected boolean autoHeight = true;
    protected int minWindowWidth = 80;
    protected int maxWindowWidth = 600;
    protected int minWindowHeight = 40;
    protected int maxWindowHeight = 500;

    public WindowWidget(Component title) {
        this(title, 176, 166);
        this.autoWidth = true;
        this.autoHeight = true;
    }

    public WindowWidget(Component title, int width, int height) {
        this.title = title;
        this.windowWidth = width;
        this.windowHeight = height;

        flexNode.setSize(width, height);
        flexNode.setDirection(FlexDirection.COLUMN);

        // 1. Header Bar (20px height)
        headerContainer = new FlexContainer(FlexDirection.ROW);
        headerContainer.getFlexNode().setSize(width, 20);
        headerContainer.getFlexNode().setPadding(FlexInsets.of(2, 6, 2, 6));
        headerContainer.getFlexNode().setAlignItems(AlignItems.CENTER);
        headerContainer.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        LabelWidget titleLabel = new LabelWidget(title);
        titleLabel.setColor(0xFFFFFFFF);
        titleLabel.setShadow(true);
        headerContainer.addChild(titleLabel);

        headerButtonsContainer = new FlexContainer(FlexDirection.ROW);
        headerButtonsContainer.getFlexNode().setGap(2);
        headerButtonsContainer.getFlexNode().setAlignItems(AlignItems.CENTER);
        headerContainer.addChild(headerButtonsContainer);

        // 2. Content Area
        contentContainer = new FlexContainer(FlexDirection.COLUMN);
        contentContainer.getFlexNode().setSize(width, height - 20);
        contentContainer.getFlexNode().setPadding(FlexInsets.all(4));
        contentContainer.getFlexNode().setFlexGrow(1.0f);

        flexNode.addChild(headerContainer.getFlexNode());
        flexNode.addChild(contentContainer.getFlexNode());
    }

    public Component getTitle() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }

    public WindowWidget setAutoWidth(boolean autoWidth) {
        this.autoWidth = autoWidth;
        return this;
    }

    public boolean isAutoWidth() {
        return autoWidth;
    }

    public WindowWidget setAutoHeight(boolean autoHeight) {
        this.autoHeight = autoHeight;
        return this;
    }

    public boolean isAutoHeight() {
        return autoHeight;
    }

    public WindowWidget setAutoSize(boolean autoSize) {
        this.autoWidth = autoSize;
        this.autoHeight = autoSize;
        return this;
    }

    public WindowWidget setMinWindowWidth(int minWidth) {
        this.minWindowWidth = minWidth;
        return this;
    }

    public WindowWidget setMaxWindowWidth(int maxWidth) {
        this.maxWindowWidth = maxWidth;
        return this;
    }

    public WindowWidget setMinWindowHeight(int minHeight) {
        this.minWindowHeight = minHeight;
        return this;
    }

    public WindowWidget setMaxWindowHeight(int maxHeight) {
        this.maxWindowHeight = maxHeight;
        return this;
    }

    public WindowWidget setWindowControls(io.github.gtbauke.modernmachines.client.gui.declarative.WindowControls controls) {
        this.headerButtonsContainer.clearChildren();
        if (controls != null) {
            for (io.github.gtbauke.modernmachines.client.gui.declarative.WindowControl c : controls.getControls()) {
                this.headerButtonsContainer.addChild(c.createWidget());
            }
        }
        pack();
        return this;
    }

    public void pack() {
        syncFlexDisplay();

        // 1. Measure header with natural width
        headerContainer.getFlexNode().setWidth(io.github.gtbauke.modernmachines.client.gui.layout.FlexSize.AUTO);
        headerContainer.getFlexNode().setHeight(20);
        headerContainer.getFlexNode().measure(2000, 20);
        int naturalHeaderW = headerContainer.getFlexNode().getMeasuredWidth();

        // 2. Measure content container unconstrained intrinsically
        contentContainer.getFlexNode().setWidth(io.github.gtbauke.modernmachines.client.gui.layout.FlexSize.AUTO);
        contentContainer.getFlexNode().setHeight(io.github.gtbauke.modernmachines.client.gui.layout.FlexSize.AUTO);
        contentContainer.getFlexNode().measure(2000, 2000);

        int naturalContentW = contentContainer.getFlexNode().getMeasuredWidth();
        int naturalContentH = contentContainer.getFlexNode().getMeasuredHeight();

        // 3. Compute 2D auto dimensions
        if (autoWidth) {
            int naturalW = Math.max(naturalContentW + 8, naturalHeaderW + 16);
            this.windowWidth = Math.max(minWindowWidth, Math.min(maxWindowWidth, naturalW));
        }
        if (autoHeight) {
            int naturalH = 20 + naturalContentH + 8;
            this.windowHeight = Math.max(minWindowHeight, Math.min(maxWindowHeight, naturalH));
        }

        // 4. Layout header and content with final resolved window bounds
        headerContainer.getFlexNode().setWidth(this.windowWidth);
        int finalContentW = this.windowWidth - 8;
        contentContainer.getFlexNode().setWidth(finalContentW);
        contentContainer.getFlexNode().setHeight(this.windowHeight - 28);

        flexNode.setSize(windowWidth, windowHeight);
        flexNode.measure(windowWidth, windowHeight);
        flexNode.layout(posX, posY, windowWidth, windowHeight);
        updateSideTabsPosition();
    }

    public FlexContainer getContentContainer() {
        return contentContainer;
    }

    public FlexContainer getHeaderContainer() {
        return headerContainer;
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }

    public WindowWidget setClosable(boolean closable) {
        if (closable && closeButton == null) {
            closeButton = new ButtonWidget(Component.literal("x"), b -> {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                } else {
                    setVisible(false);
                }
            });
            closeButton.getFlexNode().setSize(14, 14);
            headerButtonsContainer.addChild(closeButton);
        }
        return this;
    }

    public WindowWidget setMinimizable(boolean minimizable) {
        if (minimizable && minimizeButton == null) {
            minimizeButton = new ButtonWidget(Component.literal("-"), b -> {
                if (onMinimizeCallback != null) {
                    onMinimizeCallback.run();
                } else {
                    setVisible(false);
                }
            });
            minimizeButton.getFlexNode().setSize(14, 14);
            headerButtonsContainer.addChild(minimizeButton);
        }
        return this;
    }

    public WindowWidget setOnClose(Runnable onClose) {
        this.onCloseCallback = onClose;
        return this;
    }

    public WindowWidget setOnMinimize(Runnable onMinimize) {
        this.onMinimizeCallback = onMinimize;
        return this;
    }

    public WindowWidget setContent(UiWidget content) {
        this.contentContainer.clearChildren();
        if (content != null) {
            this.contentContainer.addChild(content);
        }
        pack();
        return this;
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
        pack();
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public void addSideTab(SideTabWidget tab) {
        sideTabs.add(tab);
        tab.setParentWindow(this);
        updateSideTabsPosition();
    }

    public void addTabButton(SideTabButtonWidget button) {
        tabButtons.add(button);
        button.setParentWindow(this);
        updateSideTabsPosition();
    }

    public void onTabExpanded(SideTabWidget expandedTab) {
        for (SideTabWidget tab : sideTabs) {
            if (tab != expandedTab && tab.isExpanded()) {
                tab.setExpanded(false);
            }
        }
        updateSideTabsPosition();
    }

    public void updateSideTabsPosition() {
        int leftYOffset = 4;
        int rightYOffset = 4;
        for (SideTabWidget tab : sideTabs) {
            if (tab.isLeftSided()) {
                tab.updateDockedPosition(posX, posY + leftYOffset);
                leftYOffset += tab.getBounds().height() + 2;
            } else {
                tab.updateDockedPosition(posX + windowWidth, posY + rightYOffset);
                rightYOffset += tab.getBounds().height() + 2;
            }
        }
        for (SideTabButtonWidget btn : tabButtons) {
            if (btn.isLeftSided()) {
                btn.updateDockedPosition(posX, posY + leftYOffset);
                leftYOffset += btn.getBounds().height() + 2;
            } else {
                btn.updateDockedPosition(posX + windowWidth, posY + rightYOffset);
                rightYOffset += btn.getBounds().height() + 2;
            }
        }
    }

    public List<SideTabWidget> getSideTabs() {
        return sideTabs;
    }

    public List<SideTabButtonWidget> getTabButtons() {
        return tabButtons;
    }

    public List<SlotWidget> getSlotWidgets() {
        List<SlotWidget> list = new ArrayList<>();
        collectSlots(contentContainer, list);
        return list;
    }

    private void collectSlots(UiWidget widget, List<SlotWidget> out) {
        if (widget instanceof SlotWidget slot) {
            out.add(slot);
        }
        for (UiWidget child : widget.getChildren()) {
            collectSlots(child, out);
        }
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        headerContainer.updateHoverState(mouseX, mouseY);
        contentContainer.updateHoverState(mouseX, mouseY);
        for (SideTabWidget tab : sideTabs) {
            tab.updateHoverState(mouseX, mouseY);
        }
        for (SideTabButtonWidget btn : tabButtons) {
            btn.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        // 1. Draw side tabs behind window
        for (SideTabWidget tab : sideTabs) {
            if (tab.isVisible()) {
                tab.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }
        for (SideTabButtonWidget btn : tabButtons) {
            if (btn.isVisible()) {
                btn.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }

        // 2. Draw window body & header
        Bounds b = getBounds();
        graphics.fill(b.x() + 2, b.y() + 2, b.x() + b.width() - 2, b.y() + b.height() - 2, 0xFF181820);
        NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.WINDOW_DARK, b);

        // Header separator line
        GuiRenderHelper.drawHorizontalLine(graphics, b.x() + 4, b.x() + b.width() - 4, b.y() + 20, 0xFF353545);

        // 3. Extract content container background
        contentContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        headerContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        // 1. Draw side tabs foreground
        for (SideTabWidget tab : sideTabs) {
            if (tab.isVisible()) {
                tab.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }
        for (SideTabButtonWidget btn : tabButtons) {
            if (btn.isVisible()) {
                btn.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
            }
        }

        // 2. Extract header & content foreground
        headerContainer.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
        contentContainer.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (!visible) return List.of();
        for (SideTabWidget tab : sideTabs) {
            if (tab.isVisible()) {
                List<Component> tabTip = tab.getTooltip(mouseX, mouseY);
                if (!tabTip.isEmpty()) return tabTip;
            }
        }
        for (SideTabButtonWidget btn : tabButtons) {
            if (btn.isVisible()) {
                List<Component> btnTip = btn.getTooltip(mouseX, mouseY);
                if (!btnTip.isEmpty()) return btnTip;
            }
        }
        List<Component> tip = contentContainer.getTooltip(mouseX, mouseY);
        if (!tip.isEmpty()) return tip;
        return headerContainer.getTooltip(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;

        // Check side tabs and buttons first
        for (SideTabWidget tab : sideTabs) {
            if (tab.isVisible() && tab.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        for (SideTabButtonWidget btn : tabButtons) {
            if (btn.isVisible() && btn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        // Check header buttons & container
        if (headerContainer.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Check content widgets (e.g. custom buttons)
        if (contentContainer.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Check window dragging on titlebar
        Bounds headerBounds = new Bounds(posX, posY, windowWidth, 20);
        if (draggable && button == 0 && headerBounds.contains(mouseX, mouseY)) {
            this.isDragging = true;
            this.dragOffsetX = mouseX - posX;
            this.dragOffsetY = mouseY - posY;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging && button == 0) {
            this.isDragging = false;
            return true;
        }
        for (SideTabWidget tab : sideTabs) {
            tab.mouseReleased(mouseX, mouseY, button);
        }
        contentContainer.mouseReleased(mouseX, mouseY, button);
        headerContainer.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && button == 0) {
            setPosition((int) (mouseX - dragOffsetX), (int) (mouseY - dragOffsetY));
            return true;
        }
        for (SideTabWidget tab : sideTabs) {
            if (tab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return contentContainer.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (SideTabWidget tab : sideTabs) {
            if (tab.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }
        return contentContainer.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public List<UiWidget> getChildren() {
        List<UiWidget> all = new ArrayList<>();
        all.add(headerContainer);
        all.add(contentContainer);
        all.addAll(sideTabs);
        return all;
    }
}
