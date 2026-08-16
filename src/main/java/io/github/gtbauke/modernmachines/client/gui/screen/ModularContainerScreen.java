package io.github.gtbauke.modernmachines.client.gui.screen;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.client.gui.windows.WindowManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ModularContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;

    protected final WindowManager windowManager = new WindowManager();
    protected Window mainWindow;

    public ModularContainerScreen(T menu, Inventory playerInventory, Component title) {
        this(menu, playerInventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public ModularContainerScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.windowManager.clearWindows();

        this.mainWindow = createDefaultMainWindow(this.title);
        this.mainWindow.setBounds(new Bounds(new Position(this.leftPos, this.topPos), new Size(this.imageWidth, this.imageHeight)));

        UIElement content = buildContent();
        if (content != null) {
            this.mainWindow.addChild(content);
        }

        this.windowManager.setMainWindow(this.mainWindow);

        initWindows();

        this.windowManager.calculateSize();
        this.windowManager.calculateLayout();
    }

    /**
     * Subclasses can override to register auxiliary windows (e.g. UpgradeWindow, SideTabs)
     */
    protected void initWindows() {
        // Default no-op
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        this.windowManager.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.windowManager.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.windowManager.mouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (this.windowManager.mouseDragged(event.x(), event.y(), event.button(), dx, dy)) {
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    protected UIElement buildContent() {
        return null;
    }

    protected Window createDefaultMainWindow(Component title) {
        return new Window(title, this.imageWidth, this.imageHeight);
    }
}
