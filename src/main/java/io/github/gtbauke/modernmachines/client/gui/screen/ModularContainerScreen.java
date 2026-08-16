package io.github.gtbauke.modernmachines.client.gui.screen;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.client.gui.windows.WindowManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.mainWindow = createDefaultMainWindow(this.title);
        this.mainWindow.setBounds(new Bounds(new Position(this.leftPos, this.topPos), new Size(this.imageWidth, this.imageHeight)));

        UIElement content = buildContent();
        if (content != null) {
            this.mainWindow.addChild(content);
        }

        this.windowManager.clearWindows();
        this.windowManager.setMainWindow(this.mainWindow);

        this.windowManager.calculateSize();
        this.windowManager.calculateLayout();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        this.windowManager.render(graphics, mouseX, mouseY, partialTick);
    }

    protected UIElement buildContent() {
        return null;
    }

    protected Window createDefaultMainWindow(Component title) {
        return new Window(title, this.imageWidth, this.imageHeight);
    }
}
