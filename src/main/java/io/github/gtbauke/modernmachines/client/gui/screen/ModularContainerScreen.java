package io.github.gtbauke.modernmachines.client.gui.screen;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.client.gui.windows.WindowManager;
import net.minecraft.client.Minecraft;
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
    protected io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay screenEditor;
    protected boolean isEditorOpen = false;

    public ModularContainerScreen(T menu, Inventory playerInventory, Component title) {
        this(menu, playerInventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public ModularContainerScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
        this.titleLabelX = -9999;
        this.titleLabelY = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    public boolean isEditorOpen() {
        return isEditorOpen;
    }

    public void toggleScreenEditor() {
        this.isEditorOpen = !this.isEditorOpen;
        if (this.isEditorOpen) {
            if (this.screenEditor == null) {
                this.screenEditor = new io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay(this);
            }

            this.screenEditor.recalculateLayoutBounds(this.width, this.height);

            for (var slot : this.menu.slots) {
                if (slot instanceof io.github.gtbauke.modernmachines.mixin.SlotAccessor accessor) {
                    try {
                        accessor.setX(-9999);
                        accessor.setY(-9999);
                    } catch (Throwable ignored) {
                    }
                }
            }
        } else {
            this.windowManager.calculateSize();
            this.windowManager.calculateLayout();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.windowManager.clearWindows();

        this.mainWindow = createDefaultMainWindow(this.title);
        this.mainWindow.setBounds(new Bounds(new Position(this.leftPos, this.topPos), new Size(this.imageWidth, this.imageHeight)));

        var content = buildContent();
        if (content != null) {
            this.mainWindow.addChild(content);
        }

        this.windowManager.setMainWindow(this.mainWindow);

        initWindows();

        this.windowManager.calculateSize();
        this.windowManager.calculateLayout();

        if (this.screenEditor != null) {
            this.screenEditor.recalculateLayoutBounds(this.width, this.height);
        }

        if (this.isEditorOpen) {
            for (var slot : this.menu.slots) {
                if (slot instanceof io.github.gtbauke.modernmachines.mixin.SlotAccessor accessor) {
                    try {
                        accessor.setX(-9999);
                        accessor.setY(-9999);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
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

        if (this.isEditorOpen && this.screenEditor != null) {
            this.screenEditor.render(graphics, Position.ZERO, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isEditorOpen && this.screenEditor != null) {
            this.screenEditor.mouseClicked(event.x(), event.y(), event.button());
            return true;
        }

        if (this.windowManager.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.isEditorOpen && this.screenEditor != null) {
            this.screenEditor.mouseReleased(event.x(), event.y(), event.button());
            return true;
        }

        if (this.windowManager.mouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (this.isEditorOpen && this.screenEditor != null) {
            this.screenEditor.mouseDragged(event.x(), event.y(), event.button(), dx, dy);
            return true;
        }

        if (this.windowManager.mouseDragged(event.x(), event.y(), event.button(), dx, dy)) {
            return true;
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.isEditorOpen && this.screenEditor != null) {
            this.screenEditor.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        if (this.isEditorOpen) {
            return false;
        }

        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop) {
        if (this.isEditorOpen) {
            return false;
        }

        return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop);
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public static boolean isCtrlDown() {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) {
            return false;
        }

        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(mc.getWindow(), org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL)
            || com.mojang.blaze3d.platform.InputConstants.isKeyDown(mc.getWindow(), org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (isCtrlDown() && event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_U) {
            toggleScreenEditor();
            return true;
        }

        if (this.isEditorOpen) {
            if (this.screenEditor != null && this.screenEditor.isItemPickerOpen()) {
                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                    this.screenEditor.closeItemPicker();
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
                    this.screenEditor.getActiveItemPicker().backspaceSearch();
                    return true;
                }

                if (event.key() >= org.lwjgl.glfw.GLFW.GLFW_KEY_A && event.key() <= org.lwjgl.glfw.GLFW.GLFW_KEY_Z) {
                    char c = (char) ('a' + (event.key() - org.lwjgl.glfw.GLFW.GLFW_KEY_A));
                    this.screenEditor.getActiveItemPicker().appendSearchChar(c);
                    return true;
                }

                if (event.key() >= org.lwjgl.glfw.GLFW.GLFW_KEY_0 && event.key() <= org.lwjgl.glfw.GLFW.GLFW_KEY_9) {
                    char c = (char) ('0' + (event.key() - org.lwjgl.glfw.GLFW.GLFW_KEY_0));
                    this.screenEditor.getActiveItemPicker().appendSearchChar(c);
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS) {
                    this.screenEditor.getActiveItemPicker().appendSearchChar('_');
                    return true;
                }

                return true;
            }

            if (this.screenEditor != null && this.screenEditor.getInspector() != null && this.screenEditor.getInspector().isFocused()) {
                var inspector = this.screenEditor.getInspector();
                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE || event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) {
                    inspector.unfocus();
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
                    inspector.onBackspace();
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE) {
                    inspector.onCharTyped(' ');
                    return true;
                }

                boolean isShift = (event.modifiers() & org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT) != 0;

                if (event.key() >= org.lwjgl.glfw.GLFW.GLFW_KEY_A && event.key() <= org.lwjgl.glfw.GLFW.GLFW_KEY_Z) {
                    char base = (char) ('a' + (event.key() - org.lwjgl.glfw.GLFW.GLFW_KEY_A));
                    inspector.onCharTyped(isShift ? Character.toUpperCase(base) : base);
                    return true;
                }

                if (event.key() >= org.lwjgl.glfw.GLFW.GLFW_KEY_0 && event.key() <= org.lwjgl.glfw.GLFW.GLFW_KEY_9) {
                    if (isShift) {
                        char[] shiftNums = {')', '!', '@', '#', '$', '%', '^', '&', '*', '('};
                        inspector.onCharTyped(shiftNums[event.key() - org.lwjgl.glfw.GLFW.GLFW_KEY_0]);
                    } else {
                        char c = (char) ('0' + (event.key() - org.lwjgl.glfw.GLFW.GLFW_KEY_0));
                        inspector.onCharTyped(c);
                    }

                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS) {
                    inspector.onCharTyped(isShift ? '_' : '-');
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD) {
                    inspector.onCharTyped(isShift ? '>' : '.');
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA) {
                    inspector.onCharTyped(isShift ? '<' : ',');
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH) {
                    inspector.onCharTyped(isShift ? '?' : '/');
                    return true;
                }

                if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON) {
                    inspector.onCharTyped(isShift ? ':' : ';');
                    return true;
                }

                return true;
            }

            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                if (this.screenEditor != null && this.screenEditor.isPreviewMode()) {
                    this.screenEditor.setPreviewMode(false);
                    return true;
                }

                toggleScreenEditor();
                return true;
            }

            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_P) {
                if (this.screenEditor != null) {
                    this.screenEditor.setPreviewMode(!this.screenEditor.isPreviewMode());
                }

                return true;
            }

            if (isCtrlDown() && event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_Z) {
                if (this.screenEditor != null) {
                    this.screenEditor.undo();
                }

                return true;
            }

            if (isCtrlDown() && event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_S) {
                if (this.screenEditor != null) {
                    this.screenEditor.saveLayoutToFile();
                }

                return true;
            }

            if (isCtrlDown() && event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_E) {
                if (this.screenEditor != null) {
                    this.screenEditor.exportJavaCode();
                }

                return true;
            }

            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_M) {
                if (this.screenEditor != null) {
                    this.screenEditor.setInteractionMode(io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay.InteractionMode.MOVE);
                }

                return true;
            }

            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_R) {
                if (this.screenEditor != null) {
                    this.screenEditor.setInteractionMode(io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay.InteractionMode.RESIZE);
                }

                return true;
            }

            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_L) {
                if (this.screenEditor != null && this.screenEditor.getSelectedElement() != null) {
                    this.screenEditor.saveUndoState();
                    var sel = this.screenEditor.getSelectedElement();
                    sel.setLocked(!sel.isLocked());
                    this.screenEditor.showToast(sel.isLocked() ? "Element Locked 🔒" : "Element Unlocked 🔓");
                }

                return true;
            }

            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE || event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
                if (this.screenEditor != null && this.screenEditor.getSelectedElement() != null) {
                    this.screenEditor.deleteSelectedElement();
                }

                return true;
            }

            return true;
        }

        return super.keyPressed(event);
    }

    protected UIElement buildContent() {
        return null;
    }

    protected Window createDefaultMainWindow(Component title) {
        return new Window(title, this.imageWidth, this.imageHeight);
    }
}
