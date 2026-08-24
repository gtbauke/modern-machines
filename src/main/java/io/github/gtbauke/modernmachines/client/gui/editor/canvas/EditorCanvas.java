package io.github.gtbauke.modernmachines.client.gui.editor.canvas;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.core.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ElementDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.TabDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorCanvas extends UIElement {
    public enum ResizeHandle {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    private final ScreenEditorOverlay editor;
    private final Map<ElementDefinition, Bounds> computedBounds = new HashMap<>();
    private final Map<TabDefinition, Bounds> previewTabBounds = new HashMap<>();
    private TabDefinition activePreviewTab = null;

    private boolean isDraggingElement = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    private boolean isResizingElement = false;
    private ResizeHandle activeResizeHandle = ResizeHandle.NONE;
    private int resizeInitialW = 0;
    private int resizeInitialH = 0;
    private int resizeInitialX = 0;
    private int resizeInitialY = 0;
    private int resizeMouseStartX = 0;
    private int resizeMouseStartY = 0;

    private boolean isResizingScreen = false;
    private int screenResizeStartW = 0;
    private int screenResizeStartH = 0;
    private int screenResizeStartX = 0;
    private int screenResizeStartY = 0;

    public EditorCanvas(ScreenEditorOverlay editor) {
        super(new Bounds(Position.ZERO, Size.ZERO));
        this.editor = editor;
    }

    public Bounds getCanvasActiveBounds() {
        var activeTab = editor.isPreviewMode() ? null : editor.getActiveTab();
        int targetW = 176;
        int targetH = 166;

        if (activeTab != null) {
            targetW = activeTab.getWindowWidth();
            targetH = activeTab.getWindowHeight();
        } else if (editor.getActiveLayout() != null) {
            targetW = editor.getActiveLayout().getImageWidth();
            targetH = editor.getActiveLayout().getImageHeight();
        }

        int canvasW = this.bounds.size().width();
        int canvasH = this.bounds.size().height();

        int x = this.bounds.position().x() + (canvasW - targetW) / 2;
        int y = this.bounds.position().y() + (canvasH - targetH) / 2;

        return new Bounds(new Position(x, y), new Size(targetW, targetH));
    }

    public List<ElementDefinition> getActiveElementsList() {
        var activeTab = editor.isPreviewMode() ? null : editor.getActiveTab();
        if (activeTab != null) {
            return activeTab.getElements();
        }

        var layout = editor.getActiveLayout();
        return layout != null ? layout.getElements() : List.of();
    }

    public boolean isInsideFlexContainer(ElementDefinition target) {
        if (target == null) {
            return false;
        }

        for (var el : getActiveElementsList()) {
            if (findChildInFlex(el, target)) {
                return true;
            }
        }

        return false;
    }

    private boolean findChildInFlex(ElementDefinition parent, ElementDefinition target) {
        if (parent.getType() == ElementDefinition.ElementType.COLUMN || parent.getType() == ElementDefinition.ElementType.ROW) {
            if (parent.getChildren().contains(target)) {
                return true;
            }
        }

        for (var child : parent.getChildren()) {
            if (findChildInFlex(child, target)) {
                return true;
            }
        }

        return false;
    }

    public ElementDefinition findDirectFlexParent(ElementDefinition target) {
        if (target == null) {
            return null;
        }

        for (var el : getActiveElementsList()) {
            var found = findDirectFlexParentHelper(el, target);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private ElementDefinition findDirectFlexParentHelper(ElementDefinition parent, ElementDefinition target) {
        if (parent.getType() == ElementDefinition.ElementType.COLUMN || parent.getType() == ElementDefinition.ElementType.ROW) {
            if (parent.getChildren().contains(target)) {
                return parent;
            }
        }

        for (var child : parent.getChildren()) {
            var found = findDirectFlexParentHelper(child, target);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        int mx = (int) mouseX;
        int my = (int) mouseY;

        // In Preview Mode: Handle clicking side tabs or close buttons
        if (editor.isPreviewMode()) {
            for (var entry : previewTabBounds.entrySet()) {
                if (entry.getValue().contains(new Position(mx, my))) {
                    if (activePreviewTab == entry.getKey()) {
                        activePreviewTab = null;
                    } else {
                        activePreviewTab = entry.getKey();
                    }

                    return true;
                }
            }

            return true;
        }

        var screenBounds = getCanvasActiveBounds();

        // 1. Check Screen/Window Resize Handle (bottom right corner)
        var screenResizeHandle = new Bounds(new Position(screenBounds.right() - 8, screenBounds.bottom() - 8), new Size(8, 8));
        if (screenResizeHandle.contains(new Position(mx, my))) {
            isResizingScreen = true;
            screenResizeStartX = mx;
            screenResizeStartY = my;
            var activeTab = editor.getActiveTab();
            if (activeTab != null) {
                screenResizeStartW = activeTab.getWindowWidth();
                screenResizeStartH = activeTab.getWindowHeight();
            } else {
                screenResizeStartW = editor.getActiveLayout().getImageWidth();
                screenResizeStartH = editor.getActiveLayout().getImageHeight();
            }

            return true;
        }

        // 2. Check if clicked a resize handle of currently selected element
        var selected = editor.getSelectedElement();
        if (selected != null && computedBounds.containsKey(selected) && !selected.isLocked()) {
            var selBounds = computedBounds.get(selected);
            var handle = getHitResizeHandle(selBounds, mx, my);
            if (handle != ResizeHandle.NONE || editor.getInteractionMode() == ScreenEditorOverlay.InteractionMode.RESIZE) {
                isResizingElement = true;
                activeResizeHandle = handle != ResizeHandle.NONE ? handle : ResizeHandle.BOTTOM_RIGHT;
                resizeInitialW = selected.getWidth();
                resizeInitialH = selected.getHeight();
                resizeInitialX = selected.getX();
                resizeInitialY = selected.getY();
                resizeMouseStartX = mx;
                resizeMouseStartY = my;
                editor.saveUndoState();
                return true;
            }
        }

        // 3. Check if clicked an element on canvas (find leaf element first)
        ElementDefinition clicked = findClickedElement(mx, my);
        if (clicked != null) {
            editor.setSelectedElement(clicked);
            if (!clicked.isLocked()) {
                if (editor.getInteractionMode() == ScreenEditorOverlay.InteractionMode.RESIZE) {
                    isResizingElement = true;
                    activeResizeHandle = ResizeHandle.BOTTOM_RIGHT;
                    resizeInitialW = clicked.getWidth();
                    resizeInitialH = clicked.getHeight();
                    resizeInitialX = clicked.getX();
                    resizeInitialY = clicked.getY();
                    resizeMouseStartX = mx;
                    resizeMouseStartY = my;
                } else {
                    isDraggingElement = true;
                    var b = computedBounds.get(clicked);
                    dragOffsetX = mx - b.position().x();
                    dragOffsetY = my - b.position().y();
                }
            }

            editor.saveUndoState();
            return true;
        }

        // 4. Clicked blank space inside screen container
        if (screenBounds.contains(new Position(mx, my))) {
            editor.setSelectedElement(null);
            return true;
        }

        return false;
    }

    private ElementDefinition findClickedElement(int mx, int my) {
        var activeElements = getActiveElementsList();
        for (int i = activeElements.size() - 1; i >= 0; i--) {
            var hit = findClickedRecursive(activeElements.get(i), mx, my);
            if (hit != null) {
                return hit;
            }
        }

        return null;
    }

    private ElementDefinition findClickedRecursive(ElementDefinition el, int mx, int my) {
        for (int i = el.getChildren().size() - 1; i >= 0; i--) {
            var hit = findClickedRecursive(el.getChildren().get(i), mx, my);
            if (hit != null) {
                return hit;
            }
        }

        var b = computedBounds.get(el);
        if (b != null && b.contains(new Position(mx, my))) {
            return el;
        }

        return null;
    }

    private ResizeHandle getHitResizeHandle(Bounds b, int mx, int my) {
        int x = b.position().x();
        int y = b.position().y();
        int r = b.right();
        int bot = b.bottom();

        if (new Bounds(new Position(r - 5, bot - 5), new Size(6, 6)).contains(new Position(mx, my))) {
            return ResizeHandle.BOTTOM_RIGHT;
        }

        if (new Bounds(new Position(x - 1, y - 1), new Size(6, 6)).contains(new Position(mx, my))) {
            return ResizeHandle.TOP_LEFT;
        }

        if (new Bounds(new Position(r - 5, y - 1), new Size(6, 6)).contains(new Position(mx, my))) {
            return ResizeHandle.TOP_RIGHT;
        }

        if (new Bounds(new Position(x - 1, bot - 5), new Size(6, 6)).contains(new Position(mx, my))) {
            return ResizeHandle.BOTTOM_LEFT;
        }

        if (new Bounds(new Position(r - 4, y + 4), new Size(5, b.size().height() - 8)).contains(new Position(mx, my))) {
            return ResizeHandle.RIGHT;
        }

        if (new Bounds(new Position(x + 4, bot - 4), new Size(b.size().width() - 8, 5)).contains(new Position(mx, my))) {
            return ResizeHandle.BOTTOM;
        }

        return ResizeHandle.NONE;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (button != 0 || editor.isPreviewMode()) {
            return super.mouseDragged(mouseX, mouseY, button, dx, dy);
        }

        int mx = (int) mouseX;
        int my = (int) mouseY;

        // Resizing Screen / Window
        if (isResizingScreen) {
            int newW = screenResizeStartW + (mx - screenResizeStartX) * 2;
            int newH = screenResizeStartH + (my - screenResizeStartY) * 2;

            if (editor.isSnapToGrid()) {
                int cols = Math.max(4, Math.round((float) (newW - 14) / 18.0f));
                int rows = Math.max(3, Math.round((float) (newH - 22) / 18.0f));
                newW = cols * 18 + 14;
                newH = rows * 18 + 22;
            } else {
                newW = Math.max(86, newW);
                newH = Math.max(76, newH);
            }

            var activeTab = editor.getActiveTab();
            if (activeTab != null) {
                activeTab.setWindowWidth(newW);
                activeTab.setWindowHeight(newH);
            } else if (editor.getActiveLayout() != null) {
                editor.getActiveLayout().setImageWidth(newW);
                editor.getActiveLayout().setImageHeight(newH);
            }

            return true;
        }

        // Resizing Selected Element
        if (isResizingElement && editor.getSelectedElement() != null) {
            var sel = editor.getSelectedElement();
            var screenBounds = getCanvasActiveBounds();
            int windowW = screenBounds.size().width();
            int windowH = screenBounds.size().height();

            int maxAllowedW = Math.max(18, windowW - 14);
            int maxAllowedH = Math.max(18, windowH - 22);

            int deltaX = mx - resizeMouseStartX;
            int deltaY = my - resizeMouseStartY;

            int newW = resizeInitialW;
            int newH = resizeInitialH;

            if (activeResizeHandle == ResizeHandle.BOTTOM_RIGHT || activeResizeHandle == ResizeHandle.RIGHT || activeResizeHandle == ResizeHandle.BOTTOM) {
                if (activeResizeHandle != ResizeHandle.BOTTOM) {
                    newW = Math.max(4, resizeInitialW + deltaX);
                }

                if (activeResizeHandle != ResizeHandle.RIGHT) {
                    newH = Math.max(4, resizeInitialH + deltaY);
                }
            } else if (activeResizeHandle == ResizeHandle.TOP_LEFT) {
                newW = Math.max(4, resizeInitialW - deltaX);
                newH = Math.max(4, resizeInitialH - deltaY);
            }

            // Inside a flex container: disable grid snapping!
            boolean insideFlex = isInsideFlexContainer(sel);
            if (!insideFlex && editor.isSnapToGrid()) {
                if (sel.getType() == ElementDefinition.ElementType.SLOT || sel.getType() == ElementDefinition.ElementType.SLOT_GRID) {
                    newW = Math.max(18, Math.round((float) newW / 18.0f) * 18);
                    newH = Math.max(18, Math.round((float) newH / 18.0f) * 18);
                } else if (sel.getType() == ElementDefinition.ElementType.COLUMN || sel.getType() == ElementDefinition.ElementType.ROW) {
                    int cols = Math.max(1, Math.round((float) newW / 18.0f));
                    int rows = Math.max(1, Math.round((float) newH / 18.0f));
                    newW = cols * 18;
                    newH = rows * 18;
                } else {
                    newW = Math.max(4, Math.round((float) newW / 18.0f) * 18);
                    newH = Math.max(4, Math.round((float) newH / 18.0f) * 18);
                }
            }

            // Clamp so element never exceeds window dimensions
            newW = Math.max(4, Math.min(maxAllowedW, newW));
            newH = Math.max(4, Math.min(maxAllowedH, newH));

            if (!insideFlex) {
                if (sel.getX() + newW > windowW - 7) {
                    sel.setX(Math.max(7, windowW - 7 - newW));
                }

                if (sel.getY() + newH > windowH - 7) {
                    sel.setY(Math.max(15, windowH - 7 - newH));
                }
            }

            sel.setWidth(newW);
            sel.setHeight(newH);
            return true;
        }

        // Dragging / Moving Selected Element
        if (isDraggingElement && editor.getSelectedElement() != null) {
            var sel = editor.getSelectedElement();
            var screenBounds = getCanvasActiveBounds();

            boolean insideFlex = isInsideFlexContainer(sel);
            if (insideFlex) {
                // Inside flex container: reorder children dynamically based on mouse drag!
                var flexParent = findDirectFlexParent(sel);
                if (flexParent != null) {
                    var siblings = flexParent.getChildren();
                    int curIdx = siblings.indexOf(sel);
                    if (curIdx >= 0) {
                        if (flexParent.getType() == ElementDefinition.ElementType.COLUMN) {
                            if (curIdx > 0) {
                                var prev = siblings.get(curIdx - 1);
                                var prevBounds = computedBounds.get(prev);
                                if (prevBounds != null && my < prevBounds.position().y() + prevBounds.size().height() / 2) {
                                    Collections.swap(siblings, curIdx, curIdx - 1);
                                }
                            }

                            if (curIdx < siblings.size() - 1) {
                                var next = siblings.get(curIdx + 1);
                                var nextBounds = computedBounds.get(next);
                                if (nextBounds != null && my > nextBounds.position().y() + nextBounds.size().height() / 2) {
                                    Collections.swap(siblings, curIdx, curIdx + 1);
                                }
                            }
                        } else if (flexParent.getType() == ElementDefinition.ElementType.ROW) {
                            if (curIdx > 0) {
                                var prev = siblings.get(curIdx - 1);
                                var prevBounds = computedBounds.get(prev);
                                if (prevBounds != null && mx < prevBounds.position().x() + prevBounds.size().width() / 2) {
                                    Collections.swap(siblings, curIdx, curIdx - 1);
                                }
                            }

                            if (curIdx < siblings.size() - 1) {
                                var next = siblings.get(curIdx + 1);
                                var nextBounds = computedBounds.get(next);
                                if (nextBounds != null && mx > nextBounds.position().x() + nextBounds.size().width() / 2) {
                                    Collections.swap(siblings, curIdx, curIdx + 1);
                                }
                            }
                        }
                    }
                }

                return true;
            }

            int targetX = mx - dragOffsetX - screenBounds.position().x();
            int targetY = my - dragOffsetY - screenBounds.position().y();

            if (editor.isSnapToGrid()) {
                targetX = Math.round((float) targetX / 18.0f) * 18 + 8;
                targetY = Math.round((float) targetY / 18.0f) * 18 + 8;
            }

            sel.setX(Math.max(0, Math.min(screenBounds.size().width() - sel.getWidth(), targetX)));
            sel.setY(Math.max(0, Math.min(screenBounds.size().height() - sel.getHeight(), targetY)));
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isResizingScreen = false;
            isDraggingElement = false;
            isResizingElement = false;
            activeResizeHandle = ResizeHandle.NONE;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        computedBounds.clear();
        previewTabBounds.clear();

        var screenBounds = getCanvasActiveBounds();
        var activeTab = editor.isPreviewMode() ? null : editor.getActiveTab();
        var font = Minecraft.getInstance().font;

        // 1. Draw Surface (Main Window or Tab Window)
        if (activeTab != null) {
            // Render Tab Window Header
            GUIRenderHelper.drawOreUIPanel(graphics, screenBounds);
            graphics.fill(screenBounds.position().x() + 1, screenBounds.position().y() + 1, screenBounds.right() - 1, screenBounds.position().y() + 16, GUIRenderHelper.ORE_BG_SECONDARY);
            GUIRenderHelper.drawLine(graphics, new Position(screenBounds.position().x(), screenBounds.position().y() + 16), new Position(screenBounds.right(), screenBounds.position().y() + 17), GUIRenderHelper.ORE_BORDER_DARK);
            graphics.text(font, Component.literal("Tab: " + activeTab.getTitle()), screenBounds.position().x() + 6, screenBounds.position().y() + 4, GUIRenderHelper.ORE_TEXT_TITLE, true);

            // Tab icon indicator on the left side
            var iconBounds = new Bounds(new Position(screenBounds.position().x() - 28, screenBounds.position().y() + 4), new Size(28, 28));
            GUIRenderHelper.drawOreUITab(graphics, iconBounds, true, true, false);
            try {
                var itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(activeTab.getIconItem()));
                if (itemHolder.isPresent()) {
                    graphics.fakeItem(new ItemStack(itemHolder.get().value()), iconBounds.position().x() + 6, iconBounds.position().y() + 6);
                }
            } catch (Throwable ignored) {
            }
        } else {
            GUIRenderHelper.drawOreUIBackground(graphics, screenBounds);
            if (editor.getActiveLayout() != null) {
                graphics.text(font, Component.literal(editor.getActiveLayout().getTitle()), screenBounds.position().x() + 8, screenBounds.position().y() + 6, GUIRenderHelper.ORE_TEXT_TITLE, false);
            }
        }

        // 2. Draw 18px Slot Grid Overlay (only in editor mode)
        if (!editor.isPreviewMode() && editor.isSnapToGrid()) {
            int startX = screenBounds.position().x() + 8;
            int startY = screenBounds.position().y() + 8;
            int endX = screenBounds.right() - 8;
            int endY = screenBounds.bottom() - 8;

            for (int gx = startX; gx < endX; gx += 18) {
                for (int gy = startY; gy < endY; gy += 18) {
                    graphics.fill(gx, gy, gx + 1, gy + 1, 0x30FFFFFF);
                }
            }
        }

        // 3. Render Active Elements Hierarchy
        int rootInnerW = Math.max(18, screenBounds.size().width() - 16);
        int rootInnerH = Math.max(18, screenBounds.size().height() - 24);

        for (var el : getActiveElementsList()) {
            if (el.isFitParentWidth()) {
                el.setWidth(rootInnerW);
                el.setX(8);
            }

            if (el.isFitParentHeight()) {
                el.setHeight(rootInnerH);
                el.setY(16);
            }

            renderElement(graphics, screenBounds.position().x(), screenBounds.position().y(), el);
        }

        // 4. In Preview Mode: Render Configured Side Tabs on the Main Screen
        if (editor.isPreviewMode() && editor.getActiveLayout() != null) {
            int leftTabIdx = 0;
            int rightTabIdx = 0;

            for (var tab : editor.getActiveLayout().getTabs()) {
                boolean isLeft = "LEFT".equalsIgnoreCase(tab.getSide());
                int tabY = screenBounds.position().y() + 4 + (isLeft ? leftTabIdx * 30 : rightTabIdx * 30);
                int tabX = isLeft ? screenBounds.position().x() - 28 : screenBounds.right();

                var tabBounds = new Bounds(new Position(tabX, tabY), new Size(28, 28));
                previewTabBounds.put(tab, tabBounds);

                boolean isActive = activePreviewTab == tab;
                boolean isHovered = tabBounds.contains(new Position(mouseX, mouseY));
                GUIRenderHelper.drawOreUITab(graphics, tabBounds, isLeft, isActive, isHovered);

                try {
                    var itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(tab.getIconItem()));
                    if (itemHolder.isPresent()) {
                        graphics.fakeItem(new ItemStack(itemHolder.get().value()), tabBounds.position().x() + 6, tabBounds.position().y() + 6);
                    }
                } catch (Throwable ignored) {
                }

                if (isLeft) {
                    leftTabIdx++;
                } else {
                    rightTabIdx++;
                }
            }

            // If a tab is active in preview, render its preview window next to the screen
            if (activePreviewTab != null) {
                boolean isLeft = "LEFT".equalsIgnoreCase(activePreviewTab.getSide());
                int winW = activePreviewTab.getWindowWidth();
                int winH = activePreviewTab.getWindowHeight();
                int winX = isLeft ? screenBounds.position().x() - winW - 32 : screenBounds.right() + 32;
                int winY = screenBounds.position().y();
                var winBounds = new Bounds(new Position(winX, winY), new Size(winW, winH));

                GUIRenderHelper.drawOreUIPanel(graphics, winBounds);
                graphics.fill(winX + 1, winY + 1, winBounds.right() - 1, winY + 16, GUIRenderHelper.ORE_BG_SECONDARY);
                GUIRenderHelper.drawLine(graphics, new Position(winX, winY + 16), new Position(winBounds.right(), winY + 17), GUIRenderHelper.ORE_BORDER_DARK);
                graphics.text(font, Component.literal(activePreviewTab.getTitle()), winX + 6, winY + 4, GUIRenderHelper.ORE_TEXT_TITLE, true);

                for (var el : activePreviewTab.getElements()) {
                    renderElement(graphics, winX, winY, el);
                }
            }
        }

        // 5. Selection Box & Resize Handles (only in editor mode)
        if (!editor.isPreviewMode()) {
            var selected = editor.getSelectedElement();
            if (selected != null && computedBounds.containsKey(selected)) {
                var selBounds = computedBounds.get(selected);

                if (selected.isLocked()) {
                    GUIRenderHelper.drawRectOutline(graphics, selBounds, 0xFFFFAA00);
                    graphics.text(font, Component.literal("🔒"), selBounds.position().x() + 2, selBounds.position().y() + 2, 0xFFFFAA00, true);
                } else {
                    GUIRenderHelper.drawRectOutline(graphics, selBounds, GUIRenderHelper.ORE_GREEN_PRIMARY);

                    // Draw Corner & Edge Anchor Squares
                    drawHandleSquare(graphics, selBounds.position().x() - 2, selBounds.position().y() - 2);
                    drawHandleSquare(graphics, selBounds.right() - 3, selBounds.position().y() - 2);
                    drawHandleSquare(graphics, selBounds.position().x() - 2, selBounds.bottom() - 3);
                    drawHandleSquare(graphics, selBounds.right() - 3, selBounds.bottom() - 3);

                    if (editor.getInteractionMode() == ScreenEditorOverlay.InteractionMode.RESIZE) {
                        drawHandleSquare(graphics, selBounds.right() - 3, selBounds.position().y() + selBounds.size().height() / 2 - 2);
                        drawHandleSquare(graphics, selBounds.position().x() + selBounds.size().width() / 2 - 2, selBounds.bottom() - 3);
                    }
                }
            }

            // Screen / Window Resize Handle (Bottom-Right)
            var handle = new Bounds(new Position(screenBounds.right() - 8, screenBounds.bottom() - 8), new Size(8, 8));
            boolean handleHovered = handle.contains(new Position(mouseX, mouseY));
            graphics.fill(handle.position().x(), handle.position().y(), handle.right(), handle.bottom(), handleHovered ? GUIRenderHelper.ORE_GREEN_HOVER : GUIRenderHelper.ORE_BUTTON_BG);
            GUIRenderHelper.drawRectOutline(graphics, handle, GUIRenderHelper.ORE_BORDER_DARK);
        }
    }

    private void drawHandleSquare(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 5, y + 5, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 1, x + 4, y + 4, GUIRenderHelper.ORE_GREEN_PRIMARY);
    }

    private void renderElement(GuiGraphicsExtractor graphics, int parentX, int parentY, ElementDefinition el) {
        int elX = parentX + el.getX();
        int elY = parentY + el.getY();
        var elBounds = new Bounds(new Position(elX, elY), new Size(el.getWidth(), el.getHeight()));
        computedBounds.put(el, elBounds);

        var font = Minecraft.getInstance().font;
        double liveProgress = (System.currentTimeMillis() % 2400) / 2400.0;
        double liveFlame = 1.0 - ((System.currentTimeMillis() % 3000) / 3000.0);

        switch (el.getType()) {
            case SLOT -> {
                GUIRenderHelper.drawOreUISlot(graphics, elBounds);
                if (!editor.isPreviewMode()) {
                    graphics.text(font, Component.literal("" + el.getSlotIndex()), elX + 5, elY + 5, GUIRenderHelper.ORE_TEXT_DARK, false);
                }
            }
            case SLOT_GRID -> {
                for (int r = 0; r < el.getGridRows(); r++) {
                    for (int c = 0; c < el.getGridCols(); c++) {
                        var slotB = new Bounds(new Position(elX + c * (18 + el.getGap()), elY + r * (18 + el.getGap())), new Size(18, 18));
                        GUIRenderHelper.drawOreUISlot(graphics, slotB);
                        if (!editor.isPreviewMode()) {
                            int idx = el.getSlotIndex() + r * el.getGridCols() + c;
                            graphics.text(font, Component.literal("" + idx), slotB.position().x() + 5, slotB.position().y() + 5, GUIRenderHelper.ORE_TEXT_DARK, false);
                        }
                    }
                }
            }
            case PLAYER_INVENTORY -> {
                graphics.text(font, Component.literal("Inventory"), elX, elY, GUIRenderHelper.ORE_TEXT_TITLE, false);

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 9; col++) {
                        var b = new Bounds(new Position(elX + col * 18, elY + 10 + row * 18), new Size(18, 18));
                        GUIRenderHelper.drawOreUISlot(graphics, b);
                    }
                }

                for (int col = 0; col < 9; col++) {
                    var b = new Bounds(new Position(elX + col * 18, elY + 10 + 3 * 18 + 4), new Size(18, 18));
                    GUIRenderHelper.drawOreUISlot(graphics, b);
                }
            }
            case PROGRESS_ARROW -> {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, elX, elY, 0.0F, 128.0F, 22, 15, 256, 256);
                if (editor.isPreviewMode()) {
                    int arrowW = (int) Math.round(22 * liveProgress);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, elX, elY, 22.0F, 128.0F, arrowW, 15, 256, 256);
                }
            }
            case PROGRESS_LINEAR -> {
                GUIRenderHelper.drawOreUIProgressBar(graphics, elBounds, editor.isPreviewMode() ? liveProgress : 0.6, GUIRenderHelper.ORE_GREEN_PRIMARY);
            }
            case FLAME -> {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, elX, elY, 48.0F, 128.0F, 13, 13, 256, 256);
                if (editor.isPreviewMode()) {
                    int flameH = (int) Math.round(13 * liveFlame);
                    int offset = 13 - flameH;
                    graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, elX, elY + offset, 61.0F, 128.0F + offset, 13, flameH, 256, 256);
                }
            }
            case COLUMN -> {
                if (!editor.isPreviewMode()) {
                    drawContainerOutline(graphics, elBounds, "Column (" + el.getChildren().size() + ")");
                }

                int contentW = Math.max(0, el.getWidth() - 4);
                int contentH = Math.max(0, el.getHeight() - 4);
                int count = el.getChildren().size();

                int totalFlow = 0;
                int fixedH = 0;
                for (var child : el.getChildren()) {
                    if (child.isFitParentWidth()) {
                        child.setWidth(contentW);
                    }

                    if (child.getFlowAmount() > 0) {
                        totalFlow += child.getFlowAmount();
                    } else if (child.isFitParentHeight()) {
                        totalFlow += 1;
                    } else {
                        fixedH += child.getHeight();
                    }
                }

                int gaps = count > 1 ? (count - 1) * el.getGap() : 0;
                int availableH = Math.max(0, contentH - fixedH - gaps);

                int usedH = 0;
                for (var child : el.getChildren()) {
                    int flow = child.getFlowAmount() > 0 ? child.getFlowAmount() : (child.isFitParentHeight() ? 1 : 0);
                    if (flow > 0 && totalFlow > 0) {
                        int proportionalH = (int) Math.round(((double) flow / totalFlow) * availableH);
                        child.setHeight(Math.max(2, proportionalH));
                    }

                    usedH += child.getHeight();
                }

                if (count > 1) {
                    usedH += (count - 1) * el.getGap();
                }

                int remH = Math.max(0, contentH - usedH);
                int cursorY = elY + 2;
                int extraGap = 0;

                String jc = el.getJustifyContent() != null ? el.getJustifyContent() : "START";
                if ("CENTER".equalsIgnoreCase(jc)) {
                    cursorY += remH / 2;
                } else if ("END".equalsIgnoreCase(jc)) {
                    cursorY += remH;
                } else if ("SPACE_BETWEEN".equalsIgnoreCase(jc)) {
                    extraGap = count > 1 ? remH / (count - 1) : 0;
                } else if ("SPACE_AROUND".equalsIgnoreCase(jc)) {
                    int unit = count > 0 ? remH / (count * 2) : 0;
                    cursorY += unit;
                    extraGap = unit * 2;
                } else if ("SPACE_EVENLY".equalsIgnoreCase(jc)) {
                    int unit = count > 0 ? remH / (count + 1) : 0;
                    cursorY += unit;
                    extraGap = unit;
                }

                String align = el.getAlign() != null ? el.getAlign() : "CENTER";

                for (var child : el.getChildren()) {
                    int childW = child.getWidth();
                    int childX = elX + 2;

                    if ("CENTER".equalsIgnoreCase(align)) {
                        childX = elX + 2 + (contentW - childW) / 2;
                    } else if ("END".equalsIgnoreCase(align)) {
                        childX = elX + 2 + (contentW - childW);
                    }

                    child.setX(childX - elX);
                    child.setY(cursorY - elY);
                    renderElement(graphics, elX, elY, child);
                    cursorY += child.getHeight() + el.getGap() + extraGap;
                }
            }
            case ROW -> {
                if (!editor.isPreviewMode()) {
                    drawContainerOutline(graphics, elBounds, "Row (" + el.getChildren().size() + ")");
                }

                int contentW = Math.max(0, el.getWidth() - 4);
                int contentH = Math.max(0, el.getHeight() - 4);
                int count = el.getChildren().size();

                int totalFlow = 0;
                int fixedW = 0;
                for (var child : el.getChildren()) {
                    if (child.isFitParentHeight()) {
                        child.setHeight(contentH);
                    }

                    if (child.getFlowAmount() > 0) {
                        totalFlow += child.getFlowAmount();
                    } else if (child.isFitParentWidth()) {
                        totalFlow += 1;
                    } else {
                        fixedW += child.getWidth();
                    }
                }

                int gaps = count > 1 ? (count - 1) * el.getGap() : 0;
                int availableW = Math.max(0, contentW - fixedW - gaps);

                int usedW = 0;
                for (var child : el.getChildren()) {
                    int flow = child.getFlowAmount() > 0 ? child.getFlowAmount() : (child.isFitParentWidth() ? 1 : 0);
                    if (flow > 0 && totalFlow > 0) {
                        int proportionalW = (int) Math.round(((double) flow / totalFlow) * availableW);
                        child.setWidth(Math.max(2, proportionalW));
                    }

                    usedW += child.getWidth();
                }

                if (count > 1) {
                    usedW += (count - 1) * el.getGap();
                }

                int remW = Math.max(0, contentW - usedW);
                int cursorX = elX + 2;
                int extraGap = 0;

                String jc = el.getJustifyContent() != null ? el.getJustifyContent() : "START";
                if ("CENTER".equalsIgnoreCase(jc)) {
                    cursorX += remW / 2;
                } else if ("END".equalsIgnoreCase(jc)) {
                    cursorX += remW;
                } else if ("SPACE_BETWEEN".equalsIgnoreCase(jc)) {
                    extraGap = count > 1 ? remW / (count - 1) : 0;
                } else if ("SPACE_AROUND".equalsIgnoreCase(jc)) {
                    int unit = count > 0 ? remW / (count * 2) : 0;
                    cursorX += unit;
                    extraGap = unit * 2;
                } else if ("SPACE_EVENLY".equalsIgnoreCase(jc)) {
                    int unit = count > 0 ? remW / (count + 1) : 0;
                    cursorX += unit;
                    extraGap = unit;
                }

                String align = el.getAlign() != null ? el.getAlign() : "CENTER";

                for (var child : el.getChildren()) {
                    int childH = child.getHeight();
                    int childY = elY + 2;

                    if ("CENTER".equalsIgnoreCase(align)) {
                        childY = elY + 2 + (contentH - childH) / 2;
                    } else if ("END".equalsIgnoreCase(align)) {
                        childY = elY + 2 + (contentH - childH);
                    }

                    child.setX(cursorX - elX);
                    child.setY(childY - elY);
                    renderElement(graphics, elX, elY, child);
                    cursorX += child.getWidth() + el.getGap() + extraGap;
                }
            }
            case SPACER -> {
                if (!editor.isPreviewMode()) {
                    drawContainerOutline(graphics, elBounds, "Spacer");
                }
            }
            case BUTTON -> {
                GUIRenderHelper.drawOreUIButton(graphics, elBounds, false, false, false);
                graphics.text(font, Component.literal(el.getText()), elX + 4, elY + 4, GUIRenderHelper.ORE_TEXT_TITLE, false);
            }
            case TEXT -> {
                String displayText = el.getText();
                if (el.getLabelSource() == ElementDefinition.LabelSourceType.TRANSLATABLE) {
                    displayText = editor.isPreviewMode() ? Component.translatable(el.getText()).getString() : "§7[" + el.getText() + "]";
                } else if (el.getLabelSource() == ElementDefinition.LabelSourceType.MENU_DATA) {
                    var fmt = el.getLabelFormat() != null ? el.getLabelFormat() : "%d";
                    int sampleVal = (int) (liveProgress * 1000);
                    displayText = editor.isPreviewMode() ? String.format(fmt.replace("%d", "" + sampleVal)) : "§a" + String.format(fmt.replace("%d", "0"), 0);
                }

                int strW = font.width(displayText);
                int drawX = elX;
                if ("CENTER".equalsIgnoreCase(el.getLabelAlign())) {
                    drawX = elX + (el.getWidth() - strW) / 2;
                } else if ("RIGHT".equalsIgnoreCase(el.getLabelAlign())) {
                    drawX = elBounds.right() - strW;
                }

                graphics.text(font, Component.literal(displayText), drawX, elY, el.getColor(), el.isShadow());
            }
            case ICON -> {
                int iconX = elX + (el.getWidth() - 16) / 2;
                int iconY = elY + (el.getHeight() - 16) / 2;
                try {
                    var itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(el.getIconItem()));
                    if (itemHolder.isPresent()) {
                        graphics.fakeItem(new ItemStack(itemHolder.get().value()), iconX, iconY);
                    }
                } catch (Throwable ignored) {
                }

                if ("GRAYSCALE".equalsIgnoreCase(el.getColorMode())) {
                    graphics.fill(iconX, iconY, iconX + 16, iconY + 16, 0x88404040);
                }

                if (el.getOpacity() < 0.99f) {
                    int alphaInt = Math.max(0, Math.min(255, (int) ((1.0f - el.getOpacity()) * 200)));
                    int maskColor = (alphaInt << 24) | 0x1E1E20;
                    graphics.fill(iconX, iconY, iconX + 16, iconY + 16, maskColor);
                }
            }
            case SIDE_TAB -> {
                if (!editor.isPreviewMode()) {
                    GUIRenderHelper.drawOreUITab(graphics, elBounds, true, true, false);
                }
            }
            case BLOCK_FACE -> {
                String sideName = el.getRelativeSide() != null ? el.getRelativeSide() : "FRONT";
                String shortLabel = switch (sideName.toUpperCase()) {
                    case "TOP" -> "T";
                    case "BOTTOM" -> "B";
                    case "BACK" -> "Bk";
                    case "LEFT" -> "L";
                    case "RIGHT" -> "R";
                    default -> "F";
                };

                drawFaceButton(graphics, font, elBounds, shortLabel, 0xFF3C3F46);
            }
            case SIDE_CONFIG_GRID -> {
                int faceSize = 22;
                int gap = 2;
                int totalH = 114;
                int contentY = elY + Math.max(0, (el.getHeight() - totalH) / 2);

                // Capability tabs preview (centered)
                int capStartX = elX + (el.getWidth() - 96) / 2;
                var capBtn1 = new Bounds(new Position(capStartX, contentY), new Size(46, 14));
                var capBtn2 = new Bounds(new Position(capStartX + 50, contentY), new Size(46, 14));
                GUIRenderHelper.drawOreUIButton(graphics, capBtn1, false, true, false);
                GUIRenderHelper.drawOreUIButton(graphics, capBtn2, false, false, false);
                graphics.text(font, Component.literal("ITEM"), capStartX + 12, contentY + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
                graphics.text(font, Component.literal("FLUID"), capStartX + 60, contentY + 3, GUIRenderHelper.ORE_TEXT_MUTED, false);

                // Cross Grid (centered)
                int gridStartX = elX + (el.getWidth() - 94) / 2;
                int gridStartY = contentY + 17;
                int col0 = gridStartX;
                int col1 = gridStartX + faceSize + gap;
                int col2 = gridStartX + (faceSize + gap) * 2;
                int col3 = gridStartX + (faceSize + gap) * 3;

                int row0 = gridStartY;
                int row1 = gridStartY + faceSize + gap;
                int row2 = gridStartY + (faceSize + gap) * 2;

                drawFaceButton(graphics, font, new Bounds(new Position(col1, row0), new Size(faceSize, faceSize)), "T", 0xFF3C3F46);

                // Middle Row (R, F, L, Bk) — player-relative
                drawFaceButton(graphics, font, new Bounds(new Position(col0, row1), new Size(faceSize, faceSize)), "R", 0xFF3C3F46);
                drawFaceButton(graphics, font, new Bounds(new Position(col1, row1), new Size(faceSize, faceSize)), "F", 0xFF3C3F46);
                drawFaceButton(graphics, font, new Bounds(new Position(col2, row1), new Size(faceSize, faceSize)), "L", 0xFF3C3F46);
                drawFaceButton(graphics, font, new Bounds(new Position(col3, row1), new Size(faceSize, faceSize)), "Bk", 0xFF3C3F46);

                // Bottom Face (B)
                drawFaceButton(graphics, font, new Bounds(new Position(col1, row2), new Size(faceSize, faceSize)), "B", 0xFF3C3F46);

                // Auto IO Labels & Buttons (Pull / Eject) (centered)
                int labelY = row2 + faceSize + 3;
                int autoStartX = elX + (el.getWidth() - 96) / 2;
                graphics.text(font, Component.literal("Auto Pull"), autoStartX + 2, labelY, GUIRenderHelper.ORE_TEXT_MUTED, false);
                graphics.text(font, Component.literal("Auto Eject"), autoStartX + 50, labelY, GUIRenderHelper.ORE_TEXT_MUTED, false);

                int toggleY = labelY + 10;
                var pullBtn = new Bounds(new Position(autoStartX, toggleY), new Size(46, 14));
                var ejectBtn = new Bounds(new Position(autoStartX + 50, toggleY), new Size(46, 14));
                GUIRenderHelper.drawOreUIButton(graphics, pullBtn, false, false, false);
                GUIRenderHelper.drawOreUIButton(graphics, ejectBtn, false, false, false);
                graphics.text(font, Component.literal("OFF"), autoStartX + 14, toggleY + 3, 0xFFE0E0E0, false);
                graphics.text(font, Component.literal("OFF"), autoStartX + 64, toggleY + 3, 0xFFE0E0E0, false);
            }
        }
    }

    private void drawFaceButton(GuiGraphicsExtractor graphics, net.minecraft.client.gui.Font font, Bounds bounds, String label, int fillColor) {
        GUIRenderHelper.drawRect(graphics, bounds, fillColor);
        GUIRenderHelper.drawRectOutline(graphics, bounds, 0xFF222428);
        GUIRenderHelper.drawBevel(graphics, bounds, 0x40FFFFFF, 0x40000000);
        int centerX = bounds.position().x() + bounds.size().width() / 2;
        int centerY = bounds.position().y() + (bounds.size().height() - 8) / 2;
        GUIRenderHelper.drawCenteredString(graphics, font, Component.literal(label), new Position(centerX, centerY), 0xFFFFFFFF, true);
    }

    private void drawContainerOutline(GuiGraphicsExtractor graphics, Bounds bounds, String label) {
        graphics.fill(bounds.position().x(), bounds.position().y(), bounds.right(), bounds.bottom(), 0x1AFFFFFF);
        GUIRenderHelper.drawRectOutline(graphics, bounds, 0x40FFFFFF);
        var font = Minecraft.getInstance().font;
        graphics.text(font, Component.literal(label), bounds.position().x() + 2, bounds.position().y() + 2, 0x80FFFFFF, false);
    }
}
