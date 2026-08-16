package io.github.gtbauke.modernmachines.client.gui.window;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public abstract class ModularScreen extends Screen {
    protected final WindowManager windowManager = new WindowManager();

    public ModularScreen(Component title) {
        super(title);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    @Override
    protected void init() {
        super.init();
        windowManager.clearWindows();
        buildGui();
    }

    protected abstract void buildGui();

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Subtle dark translucent backdrop with clean vignette
        graphics.fill(0, 0, this.width, this.height, 0x990A0C14);
        windowManager.extractBackground(graphics, this.font, mouseX, mouseY, partialTick);
        windowManager.extractForeground(graphics, this.font, mouseX, mouseY, partialTick);
        windowManager.extractTooltip(graphics, this.font, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (windowManager.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (windowManager.mouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (windowManager.mouseDragged(event.x(), event.y(), event.button(), dx, dy)) {
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (windowManager.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
