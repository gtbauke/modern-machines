package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SideTabElement extends UIElement {
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 28;

    public enum TabStyle {
        VANILLA,
        TEXTURE_NINE_SLICE
    }

    private final Window targetWindow;
    private final Window parentWindow;
    private ItemStack iconItem = ItemStack.EMPTY;
    private int iconU = -1;
    private int iconV = -1;
    private Component tooltip;
    private boolean leftSided = true;
    private TabStyle tabStyle = TabStyle.VANILLA;

    public SideTabElement(Window parentWindow, Window targetWindow, ItemStack iconItem, Component tooltip, boolean leftSided) {
        super(new Bounds(Position.ZERO, new Size(TAB_WIDTH, TAB_HEIGHT)));
        this.parentWindow = parentWindow;
        this.targetWindow = targetWindow;
        this.iconItem = iconItem != null ? iconItem : ItemStack.EMPTY;
        this.tooltip = tooltip;
        this.leftSided = leftSided;
    }

    public SideTabElement(Window parentWindow, Window targetWindow, int iconU, int iconV, Component tooltip, boolean leftSided) {
        super(new Bounds(Position.ZERO, new Size(TAB_WIDTH, TAB_HEIGHT)));
        this.parentWindow = parentWindow;
        this.targetWindow = targetWindow;
        this.iconU = iconU;
        this.iconV = iconV;
        this.tooltip = tooltip;
        this.leftSided = leftSided;
    }

    public SideTabElement(Window parentWindow, Window targetWindow, ItemStack iconItem, Component tooltip) {
        this(parentWindow, targetWindow, iconItem, tooltip, true);
    }

    public SideTabElement(Window parentWindow, Window targetWindow, int iconU, int iconV, Component tooltip) {
        this(parentWindow, targetWindow, iconU, iconV, tooltip, true);
    }

    public Window getTargetWindow() {
        return targetWindow;
    }

    public boolean isLeftSided() {
        return leftSided;
    }

    public TabStyle getTabStyle() {
        return tabStyle;
    }

    public SideTabElement setTabStyle(TabStyle tabStyle) {
        this.tabStyle = tabStyle != null ? tabStyle : TabStyle.VANILLA;
        markDirty();
        return this;
    }

    public void updateDockedPosition(int yOffset) {
        if (parentWindow != null) {
            // Local offset relative to parentWindow's top-left corner
            int x = leftSided ? (-TAB_WIDTH + 1)
                              : (parentWindow.getSize().width() - 1);
            int y = yOffset;
            setPosition(new Position(x, y));
        }
    }

    public void updateDockedPosition() {
        updateDockedPosition(0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && targetWindow != null) {
            Position clickPos = new Position((int) mouseX, (int) mouseY);
            if (getAbsoluteBounds().contains(clickPos)) {
                boolean opening = !targetWindow.isVisible();
                targetWindow.setVisible(opening);

                if (opening && parentWindow != null) {
                    int targetX = leftSided ? (parentWindow.getPosition().x() - targetWindow.getSize().width() - 4)
                                            : (parentWindow.getPosition().x() + parentWindow.getSize().width() + 4);
                    int targetY = parentWindow.getPosition().y();
                    targetWindow.setPosition(new Position(targetX, targetY));
                    targetWindow.calculateSize();
                    targetWindow.calculateLayout();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        // 1. Draw tab background frame
        if (tabStyle == TabStyle.VANILLA) {
            GUIRenderHelper.drawVanillaTab(graphics, absoluteBounds, leftSided);
        } else {
            NineSliceRenderer.drawNineSlice(graphics, leftSided ? NineSliceRenderer.TAB_LEFT : NineSliceRenderer.TAB_RIGHT, absoluteBounds);
        }

        // 2. Centered 16x16 icon position (matching standard inventory slot item dimensions)
        int iconX = absoluteBounds.position().x() + (leftSided ? 6 : 6);
        int iconY = absoluteBounds.position().y() + (absoluteBounds.size().height() - 16) / 2;

        // 3. Draw icon item or atlas sprite
        if (!iconItem.isEmpty()) {
            graphics.fakeItem(iconItem, iconX, iconY);
        } else if (iconU >= 0 && iconV >= 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, iconX, iconY, (float) iconU, (float) iconV, 16, 16, 256, 256);
        }

        // 4. Draw tooltip on hover
        if (tooltip != null && absoluteBounds.contains(new Position(mouseX, mouseY))) {
            GUIRenderHelper.drawTooltip(graphics, Minecraft.getInstance().font, List.of(tooltip), new Position(mouseX, mouseY));
        }
    }
}
