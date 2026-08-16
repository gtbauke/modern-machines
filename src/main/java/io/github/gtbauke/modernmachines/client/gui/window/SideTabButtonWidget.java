package io.github.gtbauke.modernmachines.client.gui.window;

import java.util.List;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class SideTabButtonWidget extends UiWidget {
    private final Component title;
    private final int iconU;
    private final int iconV;
    private final boolean leftSided;
    private WindowWidget targetWindow;
    private WindowWidget parentWindow;
    private Runnable onToggleCallback;

    public SideTabButtonWidget(Component title, int iconU, int iconV, boolean leftSided) {
        this.title = title;
        this.iconU = iconU;
        this.iconV = iconV;
        this.leftSided = leftSided;
        getFlexNode().setSize(28, 26);
    }

    public void setTargetWindow(WindowWidget targetWindow) {
        this.targetWindow = targetWindow;
    }

    public void setParentWindow(WindowWidget parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggleCallback = onToggle;
    }

    public WindowWidget getTargetWindow() {
        return targetWindow;
    }

    public boolean isLeftSided() {
        return leftSided;
    }

    public boolean isActive() {
        return targetWindow != null && targetWindow.isVisible();
    }

    public void updateDockedPosition(int anchorX, int anchorY) {
        int x = leftSided ? (anchorX - 28) : anchorX;
        int y = anchorY;
        getFlexNode().measure(28, 26);
        getFlexNode().layout(x, y, 28, 26);
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (isHovered()) {
            return List.of(title);
        }
        return List.of();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();

        NineSliceRenderer.SliceDef slice = leftSided ? NineSliceRenderer.TAB_LEFT : NineSliceRenderer.TAB_RIGHT;
        NineSliceRenderer.drawNineSlice(graphics, slice, b);

        // Highlight border if active / open
        if (isActive()) {
            int glowColor = 0xFF52A9FF;
            graphics.fill(b.x() + 1, b.y() + 1, b.x() + b.width() - 1, b.y() + 2, glowColor);
            graphics.fill(b.x() + 1, b.y() + b.height() - 2, b.x() + b.width() - 1, b.y() + b.height() - 1, glowColor);
        }

        // Draw Tab Icon (16x16)
        int iconX = b.x() + 6;
        int iconY = b.y() + 5;
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, iconX, iconY, (float) iconU, (float) iconV, 16, 16, 256, 256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        Bounds b = getBounds();

        if (b.contains(mouseX, mouseY) && button == 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (targetWindow != null) {
                boolean nextState = !targetWindow.isVisible();
                targetWindow.setVisible(nextState);
                if (nextState && parentWindow != null) {
                    // Position adjacent on first open if never dragged
                    if (targetWindow.getBounds().x() == 0 && targetWindow.getBounds().y() == 0) {
                        int defaultX = leftSided ? (parentWindow.getBounds().x() - targetWindow.getBounds().width() - 4)
                                                 : (parentWindow.getBounds().x() + parentWindow.getBounds().width() + 4);
                        int defaultY = parentWindow.getBounds().y();
                        targetWindow.setPosition(defaultX, defaultY);
                    }
                }
            }
            if (onToggleCallback != null) {
                onToggleCallback.run();
            }
            return true;
        }
        return false;
    }
}
