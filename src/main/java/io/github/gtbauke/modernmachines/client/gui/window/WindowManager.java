package io.github.gtbauke.modernmachines.client.gui.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

public class WindowManager {
    private final List<WindowWidget> windows = new ArrayList<>();

    private WindowWidget mainWindow;
    private GuiTheme theme = GuiTheme.INDUSTRIAL_DARK;

    public WindowManager() {}

    public GuiTheme getTheme() {
        return theme;
    }

    public void setTheme(GuiTheme theme) {
        this.theme = theme;
    }

    public WindowWidget getMainWindow() {
        return mainWindow;
    }

    public List<WindowWidget> getWindows() {
        return Collections.unmodifiableList(windows);
    }

    public void setMainWindow(WindowWidget mainWindow) {
        if (this.mainWindow != null) {
            windows.remove(this.mainWindow);
        }

        this.mainWindow = mainWindow;
        if (mainWindow != null && !windows.contains(mainWindow)) {
            windows.addFirst(mainWindow);
        }
    }

    public void addWindow(WindowWidget window) {
        if (!windows.contains(window)) {
            windows.add(window);
        }
    }

    public void removeWindow(WindowWidget window) {
        windows.remove(window);
    }

    public void clearWindows() {
        windows.clear();
        mainWindow = null;
    }

    public void bringToFront(WindowWidget window) {
        if (windows.remove(window)) {
            windows.add(window);
        }
    }

    public void centerMainWindow(int screenWidth, int screenHeight) {
        if (mainWindow != null) {
            int x = (screenWidth - mainWindow.getBounds().width()) / 2;
            int y = (screenHeight - mainWindow.getBounds().height()) / 2;
            mainWindow.setPosition(x, y);
        }
    }

    public void syncSlots(int screenLeft, int screenTop) {
        for (WindowWidget window : windows) {
            if (window.isVisible()) {
                syncSlotsRecursive(window, screenLeft, screenTop);
            } else {
                hideSlotsRecursive(window);
            }
        }
    }

    private void syncSlotsRecursive(UiWidget widget, int screenLeft, int screenTop) {
        if (widget instanceof io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget slotWidget) {
            slotWidget.syncSlotPosition(screenLeft, screenTop);
        }

        for (UiWidget child : widget.getChildren()) {
            syncSlotsRecursive(child, screenLeft, screenTop);
        }
    }

    private void hideSlotsRecursive(UiWidget widget) {
        if (widget instanceof SlotWidget slotWidget) {
            slotWidget.getSlot().x = -9999;
            slotWidget.getSlot().y = -9999;
        }

        for (UiWidget child : widget.getChildren()) {
            hideSlotsRecursive(child);
        }
    }

    @FunctionalInterface
    public interface SlotRenderer {
        void renderSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY);
    }

    public void renderWindows(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, float partialTick) {
        renderWindows(graphics, font, mouseX, mouseY, partialTick, null);
    }

    public void renderWindows(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, float partialTick, SlotRenderer slotRenderer) {
        for (WindowWidget window : windows) {
            window.updateHoverState(mouseX, mouseY);

            if (window.isVisible()) {
                window.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
                window.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);

                if (slotRenderer != null) {
                    for (io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget sw : window.getSlotWidgets()) {
                        Slot s = sw.getSlot();

                        if (s != null && s.x > -9000) {
                            slotRenderer.renderSlot(graphics, s, mouseX, mouseY);
                        }
                    }
                }
            }
        }
    }

    public void extractBackground(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, float partialTick) {
        renderWindows(graphics, font, mouseX, mouseY, partialTick, null);
    }

    public void extractForeground(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, float partialTick) {
        // Unified inside renderWindows to prevent lower window foreground from bleeding over upper window background
    }

    public void extractTooltip(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowWidget window = windows.get(i);

            if (window.isVisible()) {
                List<Component> tooltip = window.getTooltip(mouseX, mouseY);

                if (!tooltip.isEmpty()) {
                    GuiRenderHelper.drawTooltip(graphics, font, tooltip, mouseX, mouseY);
                    return;
                }
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowWidget window = windows.get(i);

            if (window.isVisible() && window.isEnabled()) {
                if (window.mouseClicked(mouseX, mouseY, button)) {
                    bringToFront(window);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowWidget window = windows.get(i);

            if (window.isVisible() && window.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowWidget window = windows.get(i);

            if (window.isVisible() && window.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }

        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowWidget window = windows.get(i);

            if (window.isVisible() && window.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }

        return false;
    }
}
