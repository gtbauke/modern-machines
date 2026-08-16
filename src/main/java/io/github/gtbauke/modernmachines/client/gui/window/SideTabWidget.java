package io.github.gtbauke.modernmachines.client.gui.window;

import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class SideTabWidget extends UiWidget {
    private final Component tabTitle;
    private final int iconU;
    private final int iconV;
    private final boolean leftSided;
    private final FlexContainer contentContainer;

    private WindowWidget parentWindow;
    private boolean expanded = false;
    private boolean docked = true;

    private int currentX = 0;
    private int currentY = 0;
    private int collapsedWidth = 28;
    private int expandedWidth = 110;
    private int tabHeight = 120;

    public SideTabWidget(Component tabTitle, int iconU, int iconV, boolean leftSided) {
        this.tabTitle = tabTitle;
        this.iconU = iconU;
        this.iconV = iconV;
        this.leftSided = leftSided;

        contentContainer = new FlexContainer(FlexDirection.COLUMN);
        contentContainer.getFlexNode().setPadding(FlexInsets.all(4));
        contentContainer.getFlexNode().setAlignItems(AlignItems.CENTER);
        contentContainer.getFlexNode().setGap(4);

        LabelWidget header = new LabelWidget(tabTitle);
        header.setColor(0xFFEEEEEE);
        header.setCentered(true);
        contentContainer.addChild(header);
    }

    public void setParentWindow(WindowWidget window) {
        this.parentWindow = window;
    }

    public boolean isLeftSided() {
        return leftSided;
    }

    public boolean isDocked() {
        return docked;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        relayout();
    }

    public SideTabWidget setExpandedSize(int width, int height) {
        this.expandedWidth = width;
        this.tabHeight = height;
        relayout();
        return this;
    }

    public FlexContainer getContentContainer() {
        return contentContainer;
    }

    public void updateDockedPosition(int anchorX, int anchorY) {
        int w = expanded ? expandedWidth : collapsedWidth;
        int h = expanded ? tabHeight : 26;
        int x = leftSided ? (anchorX - w) : anchorX;
        int y = anchorY;

        this.currentX = x;
        this.currentY = y;
        relayout();
    }

    public void relayout() {
        int w = expanded ? expandedWidth : collapsedWidth;
        int h = expanded ? tabHeight : 26;
        flexNode.setSize(w, h);
        flexNode.measure(w, h);
        flexNode.layout(currentX, currentY, w, h);

        if (expanded) {
            contentContainer.getFlexNode().setSize(w - 6, h - 24);
            contentContainer.getFlexNode().measure(w - 6, h - 24);
            contentContainer.getFlexNode().layout(currentX + 3, currentY + 22, w - 6, h - 24);
        }
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        if (expanded) {
            contentContainer.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (!expanded && isHovered()) {
            return List.of(tabTitle);
        }
        if (expanded) {
            return contentContainer.getTooltip(mouseX, mouseY);
        }
        return List.of();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();

        // Draw Tab Frame
        if (expanded) {
            GuiRenderHelper.drawDropShadow(graphics, b, theme.dropShadowColor(), 3);
            NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.WINDOW, b);
        } else {
            NineSliceRenderer.SliceDef slice = leftSided ? NineSliceRenderer.TAB_LEFT : NineSliceRenderer.TAB_RIGHT;
            NineSliceRenderer.drawNineSlice(graphics, slice, b);
        }

        // Draw Tab Icon (16x16)
        int iconX = leftSided ? (b.x() + 6) : (b.x() + 6);
        int iconY = b.y() + 5;
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, iconX, iconY, (float) iconU, (float) iconV, 16, 16, 256, 256);

        if (expanded) {
            contentContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        if (expanded) {
            contentContainer.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        Bounds b = getBounds();

        if (b.contains(mouseX, mouseY)) {
            // Check icon/header area to toggle expand/collapse
            Bounds headerBounds = new Bounds(b.x(), b.y(), b.width(), 26);
            if (headerBounds.contains(mouseX, mouseY)) {
                this.expanded = !this.expanded;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2F));
                if (this.expanded && parentWindow != null) {
                    parentWindow.onTabExpanded(this);
                } else if (parentWindow != null) {
                    parentWindow.updateSideTabsPosition();
                }
                return true;
            }

            if (expanded && contentContainer.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded) {
            return contentContainer.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (expanded) {
            return contentContainer.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (expanded) {
            return contentContainer.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return false;
    }

    @Override
    public List<UiWidget> getChildren() {
        return List.of(contentContainer);
    }
}
