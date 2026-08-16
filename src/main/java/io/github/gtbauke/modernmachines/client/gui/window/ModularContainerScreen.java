package io.github.gtbauke.modernmachines.client.gui.window;

import io.github.gtbauke.modernmachines.client.gui.debug.GuiDebugInspector;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.PlayerInventoryWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.glfw.GLFW;

public abstract class ModularContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 174;
    public static final FlexInsets DEFAULT_CONTENT_PADDING = FlexInsets.of(2, 6, 6, 6);

    protected final WindowManager windowManager = new WindowManager();
    protected WindowWidget mainWindow;

    public ModularContainerScreen(T menu, Inventory playerInventory, Component title) {
        this(menu, playerInventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public ModularContainerScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public WindowWidget getMainWindow() {
        return mainWindow;
    }

    public void setDebug(boolean debug) {
        GuiDebugInspector.ENABLED = debug;
    }

    /**
     * Declarative build method returning the root widget tree.
     * Screens can implement this method for a Flutter-style declarative UI.
     */
    protected UiWidget buildContent() {
        return null;
    }

    /**
     * Helper to create a standard main window with 176x174 dimensions and 6px bottom padding.
     */
    protected WindowWidget createDefaultMainWindow(Component title) {
        WindowWidget window = new WindowWidget(title, this.imageWidth, this.imageHeight);
        FlexContainer content = window.getContentContainer();
        content.getFlexNode().setPadding(DEFAULT_CONTENT_PADDING);
        content.getFlexNode().setAlignItems(AlignItems.CENTER);
        content.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);
        return window;
    }

    /**
     * Helper to instantiate a 36-slot player inventory widget.
     */
    protected PlayerInventoryWidget playerInventory(int startSlot) {
        return new PlayerInventoryWidget(this.menu, startSlot);
    }

    /**
     * Helper to add a standard 36-slot player inventory widget to the window.
     */
    protected PlayerInventoryWidget addPlayerInventory(int startSlot) {
        PlayerInventoryWidget playerInv = playerInventory(startSlot);
        if (this.mainWindow != null) {
            this.mainWindow.getContentContainer().addChild(playerInv);
        }
        return playerInv;
    }

    /**
     * Helper to add a side tab to the main window.
     */
    protected void addSideTab(SideTabWidget tab) {
        if (this.mainWindow != null) {
            this.mainWindow.addSideTab(tab);
        }
    }

    /**
     * Helper to create and register a floating draggable tab window with a docked tab button on the main window.
     */
    protected FloatingTabWindow addFloatingTab(Component title, int iconU, int iconV, int width, int height, boolean leftSided, java.util.function.Function<FloatingTabWindow, UiWidget> contentFactory) {
        FloatingTabWindow floatingWindow = new FloatingTabWindow(title, iconU, iconV, width, height, this.mainWindow, leftSided);
        floatingWindow.setAutoHeight(true);
        if (contentFactory != null) {
            UiWidget content = contentFactory.apply(floatingWindow);
            if (content != null) {
                floatingWindow.setContent(content);
            }
        }
        floatingWindow.pack();
        floatingWindow.snapToMainWindow();
        this.windowManager.addWindow(floatingWindow);

        SideTabButtonWidget button = new SideTabButtonWidget(title, iconU, iconV, leftSided);
        button.setTargetWindow(floatingWindow);
        if (this.mainWindow != null) {
            this.mainWindow.addTabButton(button);
        }
        return floatingWindow;
    }

    protected FloatingTabWindow addFloatingTab(Component title, int iconU, int iconV, int width, java.util.function.Function<FloatingTabWindow, UiWidget> contentFactory) {
        return addFloatingTab(title, iconU, iconV, width, 100, false, contentFactory);
    }

    protected FloatingTabWindow addFloatingTab(Component title, int iconU, int iconV, int width, int height, java.util.function.Function<FloatingTabWindow, UiWidget> contentFactory) {
        return addFloatingTab(title, iconU, iconV, width, height, false, contentFactory);
    }

    @Override
    protected void init() {
        super.init();
        rebuild();
    }

    /**
     * Reconstructs the GUI widget tree.
     */
    public void rebuild() {
        buildGui();
        if (mainWindow != null) {
            mainWindow.setPosition(this.leftPos, this.topPos);
            windowManager.setMainWindow(mainWindow);
            windowManager.syncSlots(this.leftPos, this.topPos);
        }
    }

    protected void buildGui() {
        this.mainWindow = createDefaultMainWindow(this.title);
        UiWidget content = buildContent();
        if (content != null) {
            this.mainWindow.getContentContainer().addChild(content);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        windowManager.syncSlots(this.leftPos, this.topPos);
        super.extractBackground(graphics, mouseX, mouseY, a);
        windowManager.renderWindows(graphics, this.font, mouseX, mouseY, a, (g, slot, mx, my) -> {
            g.pose().pushMatrix();
            g.pose().translate((float) this.leftPos, (float) this.topPos);
            this.extractSlot(g, slot, mx - this.leftPos, my - this.topPos);
            g.pose().popMatrix();
        });

        // Visual Layout Debugger Overlay
        GuiDebugInspector.render(graphics, this.font, this.mainWindow, mouseX, mouseY);
    }

    @Override
    protected void extractSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Suppress default global slot extraction; slots are rendered per window in Z-order inside windowManager.renderWindows
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Labels handled cleanly by Flex Widgets & WindowWidget
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // If mouse is over any upper floating window, check if it's occluded
        for (int i = windowManager.getWindows().size() - 1; i >= 1; i--) {
            WindowWidget topW = windowManager.getWindows().get(i);
            if (topW.isVisible() && topW.getBounds().contains(mouseX, mouseY)) {
                windowManager.extractTooltip(graphics, this.font, mouseX, mouseY);
                return;
            }
        }
        super.extractTooltip(graphics, mouseX, mouseY);
        windowManager.extractTooltip(graphics, this.font, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (windowManager.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }

        // Check if mouse is over any floating window (index >= 1)
        for (int i = windowManager.getWindows().size() - 1; i >= 1; i--) {
            WindowWidget w = windowManager.getWindows().get(i);
            if (w.isVisible() && w.getBounds().contains(event.x(), event.y())) {
                // If cursor is over a slot belonging to this floating window, pass click through to Minecraft!
                if (windowHasSlotAt(w, event.x(), event.y())) {
                    return super.mouseClicked(event, doubleClick);
                }
                // Cursor is over solid floating window body/card - consume click so it doesn't trigger underlying main screen slots
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    private boolean windowHasSlotAt(WindowWidget window, double mouseX, double mouseY) {
        for (io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget sw : window.getSlotWidgets()) {
            if (sw.isVisible() && sw.getBounds().contains(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
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
    public boolean keyPressed(KeyEvent event) {
        // Toggle Layout Inspector with Ctrl + U or Alt + U or F3 + U
        if ((event.hasControlDown() || event.hasAltDown()) && event.key() == GLFW.GLFW_KEY_U) {
            GuiDebugInspector.toggle();
            return true;
        }
        return super.keyPressed(event);
    }
}
