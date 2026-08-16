package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.window.WindowManager;
import io.github.gtbauke.modernmachines.client.gui.window.WindowWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class TaskbarDockWidget extends UiWidget {
    public static class DockEntry {
        public final Component label;
        public final WindowWidget window;
        public final Component tooltip;

        public DockEntry(Component label, WindowWidget window, Component tooltip) {
            this.label = label;
            this.window = window;
            this.tooltip = tooltip;
        }
    }

    private final WindowManager windowManager;
    private final List<DockEntry> entries = new ArrayList<>();
    private int hoveredIndex = -1;

    public TaskbarDockWidget(WindowManager windowManager) {
        this.windowManager = windowManager;
        flexNode.setSize(300, 24);
    }

    public TaskbarDockWidget addDockEntry(Component label, WindowWidget window, Component tooltip) {
        entries.add(new DockEntry(label, window, tooltip));
        recalculateWidth();
        return this;
    }

    private void recalculateWidth() {
        int width = Math.max(120, entries.size() * 70 + 16);
        flexNode.setSize(width, 24);
    }

    public void updatePosition(int screenWidth, int screenHeight) {
        int w = Math.max(120, entries.size() * 70 + 16);
        int h = 24;
        int x = (screenWidth - w) / 2;
        int y = screenHeight - h - 4;
        flexNode.measure(w, h);
        flexNode.layout(x, y, w, h);
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        hoveredIndex = -1;
        if (!hovered) return;

        Bounds b = getBounds();
        int startX = b.x() + 8;
        int buttonWidth = (b.width() - 16) / Math.max(1, entries.size());

        for (int i = 0; i < entries.size(); i++) {
            int bx = startX + i * buttonWidth;
            if (mouseX >= bx && mouseX < bx + buttonWidth && mouseY >= b.y() + 2 && mouseY < b.y() + b.height() - 2) {
                hoveredIndex = i;
                break;
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();

        // 1. Sleek glass dock container
        NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.PANEL_TRANSLUCENT, b);
        GuiRenderHelper.drawRectOutline(graphics, b.x(), b.y(), b.width(), b.height(), 0xFF2A2E3D);

        // 2. Draw dock buttons
        int startX = b.x() + 8;
        int buttonWidth = (b.width() - 16) / Math.max(1, entries.size());

        for (int i = 0; i < entries.size(); i++) {
            DockEntry entry = entries.get(i);
            int bx = startX + i * buttonWidth;
            int by = b.y() + 3;
            int bw = buttonWidth - 4;
            int bh = b.height() - 6;

            boolean isOpen = entry.window.isVisible();
            List<WindowWidget> windows = windowManager.getWindows();
            boolean isFocused = isOpen && !windows.isEmpty() && windows.get(windows.size() - 1) == entry.window;
            boolean isHovered = (i == hoveredIndex);

            // Button background
            int bgCol = isFocused ? 0x6600E5FF : (isHovered ? 0x44FFFFFF : (isOpen ? 0x22FFFFFF : 0x11000000));
            GuiRenderHelper.drawRect(graphics, bx, by, bw, bh, bgCol);

            // Button border
            int borderCol = isFocused ? 0xFF00E5FF : (isOpen ? 0xFF5588AA : 0xFF252A36);
            GuiRenderHelper.drawRectOutline(graphics, bx, by, bw, bh, borderCol);

            // Active indicator bar underneath
            int indicatorCol = isFocused ? 0xFF00FFFF : (isOpen ? 0xFF0099BB : 0xFF333333);
            GuiRenderHelper.drawRect(graphics, bx + 4, by + bh - 2, bw - 8, 2, indicatorCol);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();
        int startX = b.x() + 8;
        int buttonWidth = (b.width() - 16) / Math.max(1, entries.size());

        for (int i = 0; i < entries.size(); i++) {
            DockEntry entry = entries.get(i);
            int bx = startX + i * buttonWidth;
            int by = b.y() + 3;
            int bw = buttonWidth - 4;
            int bh = b.height() - 6;

            boolean isOpen = entry.window.isVisible();
            List<WindowWidget> windows = windowManager.getWindows();
            boolean isFocused = isOpen && !windows.isEmpty() && windows.get(windows.size() - 1) == entry.window;

            int textCol = isFocused ? 0xFF00FFFF : (isOpen ? 0xFFE0E0E0 : 0xFF888888);
            GuiRenderHelper.drawCenteredString(graphics, font, entry.label, bx + bw / 2, by + 5, textCol, false);
        }
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (hoveredIndex >= 0 && hoveredIndex < entries.size()) {
            return List.of(entries.get(hoveredIndex).tooltip);
        }
        return List.of();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;

        if (hoveredIndex >= 0 && hoveredIndex < entries.size()) {
            DockEntry entry = entries.get(hoveredIndex);
            WindowWidget win = entry.window;
            List<WindowWidget> windows = windowManager.getWindows();
            boolean isFocused = win.isVisible() && !windows.isEmpty() && windows.get(windows.size() - 1) == win;

            if (!win.isVisible()) {
                // Open window and bring to front
                win.setVisible(true);
                windowManager.bringToFront(win);
            } else if (isFocused) {
                // Minimize window
                win.setVisible(false);
            } else {
                // Bring open window to front
                windowManager.bringToFront(win);
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        return getBounds().contains(mouseX, mouseY);
    }
}
