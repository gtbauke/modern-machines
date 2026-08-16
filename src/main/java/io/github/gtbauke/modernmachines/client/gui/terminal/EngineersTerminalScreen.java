package io.github.gtbauke.modernmachines.client.gui.terminal;

import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.widget.TaskbarDockWidget;
import io.github.gtbauke.modernmachines.client.gui.window.ModularScreen;
import io.github.gtbauke.modernmachines.client.gui.window.WindowWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class EngineersTerminalScreen extends ModularScreen {
    private HubSubwindow hubWindow;
    private EnergyAnalyticsSubwindow energyWindow;
    private SideIoMatrixSubwindow sideIoWindow;
    private MaterialInspectorSubwindow codexWindow;
    private TaskbarDockWidget taskbarDock;

    public EngineersTerminalScreen(Component title) {
        super(title);
    }

    @Override
    protected void buildGui() {
        // 1. Create Subwindows
        energyWindow = new EnergyAnalyticsSubwindow();
        sideIoWindow = new SideIoMatrixSubwindow();
        codexWindow = new MaterialInspectorSubwindow();

        hubWindow = new HubSubwindow(
                () -> toggleWindow(energyWindow),
                () -> toggleWindow(sideIoWindow),
                () -> toggleWindow(codexWindow)
        );

        // 2. Position Subwindows in an elegant workspace arrangement
        int cx = this.width / 2;
        int cy = this.height / 2;

        hubWindow.setPosition(Math.max(10, cx - 220), Math.max(10, cy - 100));
        energyWindow.setPosition(Math.min(this.width - 230, cx + 10), Math.max(10, cy - 100));
        sideIoWindow.setPosition(Math.max(10, cx - 220), Math.min(this.height - 210, cy + 10));
        codexWindow.setPosition(Math.min(this.width - 240, cx + 10), Math.min(this.height - 215, cy + 10));

        // Subwindows start visible or minimizable
        energyWindow.setVisible(true);
        sideIoWindow.setVisible(false);
        codexWindow.setVisible(false);

        windowManager.addWindow(hubWindow);
        windowManager.addWindow(energyWindow);
        windowManager.addWindow(sideIoWindow);
        windowManager.addWindow(codexWindow);
        windowManager.setMainWindow(hubWindow);

        // 3. Create Bottom Taskbar Dock
        taskbarDock = new TaskbarDockWidget(windowManager);
        taskbarDock.addDockEntry(Component.literal("🖥️ Hub"), hubWindow, Component.literal("Toggle Terminal Hub"));
        taskbarDock.addDockEntry(Component.literal("⚡ Energy"), energyWindow, Component.literal("Toggle Energy Analytics"));
        taskbarDock.addDockEntry(Component.literal("🔄 Side I/O"), sideIoWindow, Component.literal("Toggle Side I/O Matrix"));
        taskbarDock.addDockEntry(Component.literal("📖 Codex"), codexWindow, Component.literal("Toggle Material Codex"));
        taskbarDock.updatePosition(this.width, this.height);
    }

    private void toggleWindow(WindowWidget window) {
        if (!window.isVisible()) {
            window.setVisible(true);
            windowManager.bringToFront(window);
        } else {
            windowManager.bringToFront(window);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        if (taskbarDock != null) {
            taskbarDock.updateHoverState(mouseX, mouseY);
            taskbarDock.extractBackground(graphics, this.font, windowManager.getTheme(), mouseX, mouseY, partialTick);
            taskbarDock.extractForeground(graphics, this.font, windowManager.getTheme(), mouseX, mouseY, partialTick);

            List<Component> tip = taskbarDock.getTooltip(mouseX, mouseY);
            if (!tip.isEmpty()) {
                GuiRenderHelper.drawTooltip(graphics, this.font, tip, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (taskbarDock != null && taskbarDock.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
