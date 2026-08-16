package io.github.gtbauke.modernmachines.client.gui.windows;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
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
    }

    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        for (Window window : windows) {
            window.render(graphics, Position.ZERO, mouseX, mouseY, partialTick);
        }
    }
}
