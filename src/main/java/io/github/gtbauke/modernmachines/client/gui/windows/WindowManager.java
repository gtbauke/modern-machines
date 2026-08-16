package io.github.gtbauke.modernmachines.client.gui.windows;

import io.github.gtbauke.modernmachines.client.gui.core.element.SlotElement;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindowManager {
    private final List<Window> windows = new ArrayList<>();
    private Window mainWindow;

    public WindowManager() {}

    public void addWindow(Window window) {
        if (window != null && !windows.contains(window)) {
            windows.add(window);
        }
    }

    public void removeWindow(Window window) {
        if (window != null) {
            windows.remove(window);
            if (this.mainWindow == window) {
                this.mainWindow = null;
            }
        }
    }

    public void setMainWindow(Window mainWindow) {
        if (this.mainWindow != null) {
            windows.remove(this.mainWindow);
        }

        this.mainWindow = mainWindow;
        if (mainWindow != null && !windows.contains(mainWindow)) {
            windows.addFirst(mainWindow);
        }
    }

    public void bringToFront(Window window) {
        if (window != null && windows.contains(window) && window != mainWindow) {
            windows.remove(window);
            windows.add(window);
        }
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    public List<Window> getWindows() {
        return Collections.unmodifiableList(windows);
    }

    public void clearWindows() {
        windows.clear();
        mainWindow = null;
    }

    public void calculateSize() {
        for (Window window : windows) {
            window.calculateSize();
        }
    }

    public void calculateLayout() {
        for (Window window : windows) {
            window.calculateLayout();
        }
        syncSlots();
    }

    public void syncSlots() {
        int screenLeft = mainWindow != null ? mainWindow.getPosition().x() : 0;
        int screenTop = mainWindow != null ? mainWindow.getPosition().y() : 0;
        for (Window window : windows) {
            syncSlotsInTree(window, screenLeft, screenTop);
        }
    }

    private void syncSlotsInTree(UIElement element, int screenLeft, int screenTop) {
        if (element instanceof SlotElement slotEl) {
            slotEl.syncSlotPosition(screenLeft, screenTop);
        }
        for (UIElement child : element.getChildren()) {
            syncSlotsInTree(child, screenLeft, screenTop);
        }
    }

    private boolean windowHasSlotAt(UIElement element, Position clickPos) {
        if (element instanceof SlotElement slotEl) {
            if (slotEl.getAbsoluteBounds().contains(clickPos)) {
                return true;
            }
        }
        for (UIElement child : element.getChildren()) {
            if (windowHasSlotAt(child, clickPos)) {
                return true;
            }
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Position clickPos = new Position((int) mouseX, (int) mouseY);

        // Check windows in reverse Z-order (topmost floating window first)
        for (int i = windows.size() - 1; i >= 0; i--) {
            Window window = windows.get(i);
            if (window.isVisible()) {
                if (window != mainWindow) {
                    if (window.getBounds().contains(clickPos)) {
                        bringToFront(window);

                        // 1. Header click (drag / close button)
                        if (window.handleHeaderClick(mouseX, mouseY, button)) {
                            syncSlots();
                            return true;
                        }

                        // 2. Child controls click
                        if (window.handleChildClick(mouseX, mouseY, button)) {
                            syncSlots();
                            return true;
                        }

                        // 3. If clicking a SlotElement on this window, let click pass to Minecraft super.mouseClicked
                        if (windowHasSlotAt(window, clickPos)) {
                            return false;
                        }

                        // 4. Clicked on floating window background: consume click to prevent clicking behind it
                        return true;
                    }
                } else {
                    // Check UI controls on mainWindow (e.g. SideTabElement)
                    if (window.handleChildClick(mouseX, mouseY, button)) {
                        syncSlots();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            Window window = windows.get(i);
            if (window.mouseDragged(mouseX, mouseY, button, dx, dy)) {
                syncSlots();
                return true;
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = false;
        for (int i = windows.size() - 1; i >= 0; i--) {
            Window window = windows.get(i);
            if (window.mouseReleased(mouseX, mouseY, button)) {
                handled = true;
            }
        }
        syncSlots();
        return handled;
    }

    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        for (Window window : windows) {
            window.render(graphics, Position.ZERO, mouseX, mouseY, partialTick);
        }
    }
}
