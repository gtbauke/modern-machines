package io.github.gtbauke.modernmachines.client.gui.editor.inspector;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ElementDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.TabDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyInspector extends UIElement {
    public static final int INSPECTOR_WIDTH = 136;

    private final ScreenEditorOverlay editor;
    private final List<ClickableAction> actions = new ArrayList<>();
    private final Set<String> collapsedNodes = new HashSet<>();
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private boolean isDraggingScrollbar = false;
    private String focusedField = null;
    private boolean isChildrenSectionCollapsed = false;

    public record ClickableAction(Bounds bounds, Runnable onClick) {}

    public PropertyInspector(ScreenEditorOverlay editor) {
        super(new Bounds(Position.ZERO, new Size(INSPECTOR_WIDTH, 200)));
        this.editor = editor;
    }

    public boolean isFocused() {
        return focusedField != null;
    }

    public void unfocus() {
        this.focusedField = null;
    }

    public void onCharTyped(char c) {
        if (focusedField == null) {
            return;
        }

        var selected = editor.getSelectedElement();
        var tab = editor.getActiveTab();

        if ("label_text".equals(focusedField) && selected != null) {
            selected.setText(selected.getText() + c);
        } else if ("button_text".equals(focusedField) && selected != null) {
            selected.setText(selected.getText() + c);
        } else if ("format_text".equals(focusedField) && selected != null) {
            selected.setLabelFormat(selected.getLabelFormat() + c);
        } else if ("tab_title".equals(focusedField) && tab != null) {
            tab.setTitle(tab.getTitle() + c);
        } else if ("tab_tooltip".equals(focusedField) && tab != null) {
            tab.setTooltip(tab.getTooltip() + c);
        }
    }

    public void onBackspace() {
        if (focusedField == null) {
            return;
        }

        var selected = editor.getSelectedElement();
        var tab = editor.getActiveTab();

        if (("label_text".equals(focusedField) || "button_text".equals(focusedField)) && selected != null) {
            var cur = selected.getText();
            if (!cur.isEmpty()) {
                selected.setText(cur.substring(0, cur.length() - 1));
            }
        } else if ("format_text".equals(focusedField) && selected != null) {
            var cur = selected.getLabelFormat();
            if (!cur.isEmpty()) {
                selected.setLabelFormat(cur.substring(0, cur.length() - 1));
            }
        } else if ("tab_title".equals(focusedField) && tab != null) {
            var cur = tab.getTitle();
            if (!cur.isEmpty()) {
                tab.setTitle(cur.substring(0, cur.length() - 1));
            }
        } else if ("tab_tooltip".equals(focusedField) && tab != null) {
            var cur = tab.getTooltip();
            if (!cur.isEmpty()) {
                tab.setTooltip(cur.substring(0, cur.length() - 1));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        var clickPos = new Position((int) mouseX, (int) mouseY);
        var absBounds = getAbsoluteBounds();
        if (!absBounds.contains(clickPos)) {
            focusedField = null;
            return false;
        }

        int viewTop = absBounds.position().y() + 18;
        int viewBottom = absBounds.bottom() - 2;
        int viewHeight = viewBottom - viewTop;
        int trackX = absBounds.right() - 6;

        // Check scrollbar track click
        if (maxScroll > 0 && clickPos.x() >= trackX && clickPos.x() <= absBounds.right() && clickPos.y() >= viewTop && clickPos.y() <= viewBottom) {
            isDraggingScrollbar = true;
            updateScrollbarThumb(clickPos.y(), viewTop, viewHeight);
            return true;
        }

        boolean actionClicked = false;
        for (var action : actions) {
            if (action.bounds().contains(clickPos)) {
                // Ensure action is within visible viewport
                if (action.bounds().bottom() >= viewTop && action.bounds().position().y() <= viewBottom) {
                    action.onClick().run();
                    actionClicked = true;
                    break;
                }
            }
        }

        if (!actionClicked) {
            focusedField = null;
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (button == 0 && isDraggingScrollbar) {
            var absBounds = getAbsoluteBounds();
            int viewTop = absBounds.position().y() + 18;
            int viewHeight = absBounds.bottom() - 2 - viewTop;
            updateScrollbarThumb((int) mouseY, viewTop, viewHeight);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDraggingScrollbar = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateScrollbarThumb(int mouseY, int viewTop, int viewHeight) {
        if (maxScroll <= 0 || viewHeight <= 0) {
            return;
        }

        float progress = (float) (mouseY - viewTop) / (float) viewHeight;
        progress = Math.max(0.0f, Math.min(1.0f, progress));
        this.scrollOffset = (int) (progress * maxScroll);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var absBounds = getAbsoluteBounds();
        if (absBounds.contains(new Position((int) mouseX, (int) mouseY))) {
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) (scrollY * 18)));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        actions.clear();
        GUIRenderHelper.drawOreUIPanel(graphics, absoluteBounds);

        var font = Minecraft.getInstance().font;
        int x = absoluteBounds.position().x();
        int y = absoluteBounds.position().y();
        int right = absoluteBounds.right();

        // 1. Header (Fixed at top)
        graphics.fill(x + 1, y + 1, right - 1, y + 16, GUIRenderHelper.ORE_BG_PRIMARY);
        GUIRenderHelper.drawLine(graphics, new Position(x, y + 16), new Position(right, y + 17), GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("Inspector & Tree"), x + 6, y + 4, GUIRenderHelper.ORE_TEXT_TITLE, true);

        // 2. Viewport calculation
        int viewTop = y + 18;
        int viewBottom = absoluteBounds.bottom() - 2;
        int viewHeight = viewBottom - viewTop;
        int viewLeft = x + 1;
        int viewRight = right - 6;

        int currentY = viewTop + 2 - scrollOffset;

        // Enable Scissor to prevent any content from overflowing the inspector container
        graphics.enableScissor(viewLeft, viewTop, right - 1, viewBottom);

        var activeTab = editor.getActiveTab();
        var selected = editor.getSelectedElement();

        // If editing a Tab Window, show Tab Window Configuration
        if (activeTab != null && selected == null) {
            currentY = renderTabConfig(graphics, x, currentY, font, mouseX, mouseY, activeTab);
        } else if (selected != null) {
            currentY = renderElementProperties(graphics, x, currentY, font, mouseX, mouseY, selected);
        } else {
            graphics.text(font, Component.literal("No element selected."), x + 6, currentY, GUIRenderHelper.ORE_TEXT_MUTED, false);
            graphics.text(font, Component.literal("Click an element on"), x + 6, currentY + 12, GUIRenderHelper.ORE_TEXT_DARK, false);
            graphics.text(font, Component.literal("canvas or tree."), x + 6, currentY + 22, GUIRenderHelper.ORE_TEXT_DARK, false);
            currentY += 40;
        }

        // Section Divider: Hierarchy Tree
        GUIRenderHelper.drawLine(graphics, new Position(x, currentY), new Position(viewRight, currentY + 1), GUIRenderHelper.ORE_BORDER_DARK);
        currentY += 4;
        graphics.text(font, Component.literal("Hierarchy Tree:"), x + 6, currentY, GUIRenderHelper.ORE_TEXT_TITLE, true);
        currentY += 12;

        var elements = activeTab != null ? activeTab.getElements() : (editor.getActiveLayout() != null ? editor.getActiveLayout().getElements() : List.<ElementDefinition>of());
        for (var el : elements) {
            currentY = renderTreeNode(graphics, x + 6, currentY, el, 0, font, mouseX, mouseY, elements);
        }

        int totalContentHeight = currentY - (viewTop + 2 - scrollOffset);
        this.maxScroll = Math.max(0, totalContentHeight - viewHeight + 10);
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }

        graphics.disableScissor();

        // 3. Scrollbar rendering (Fixed on right border of the viewport)
        if (maxScroll > 0) {
            int trackX = right - 5;
            int trackW = 4;
            graphics.fill(trackX, viewTop, trackX + trackW, viewBottom, 0x30000000);

            float ratio = (float) viewHeight / (float) (totalContentHeight + 10);
            int thumbH = Math.max(14, (int) (viewHeight * ratio));
            float scrollProgress = (float) scrollOffset / (float) maxScroll;
            int thumbY = viewTop + (int) ((viewHeight - thumbH) * scrollProgress);

            var thumbBounds = new Bounds(new Position(trackX, thumbY), new Size(trackW, thumbH));
            boolean thumbHov = thumbBounds.contains(new Position(mouseX, mouseY));
            int thumbColor = isDraggingScrollbar ? GUIRenderHelper.ORE_GREEN_PRIMARY : (thumbHov ? GUIRenderHelper.ORE_GREEN_HOVER : 0x806A6A70);
            graphics.fill(trackX, thumbY, trackX + trackW, thumbY + thumbH, thumbColor);
        }
    }

    private int renderTabConfig(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, TabDefinition tab) {
        int cy = y;

        // Tab Title Text Box
        cy = drawTextBox(graphics, x, cy, font, mouseX, mouseY, "Title:", "tab_title", tab.getTitle());

        // Tab Tooltip Text Box
        cy = drawTextBox(graphics, x, cy, font, mouseX, mouseY, "Tooltip:", "tab_tooltip", tab.getTooltip());

        // Side toggle (Left / Right)
        var sideBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean sideHov = sideBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, sideBtn, sideHov, false, false);
        graphics.text(font, Component.literal("Side: " + tab.getSide()), sideBtn.position().x() + 4, sideBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(sideBtn, () -> tab.setSide("LEFT".equalsIgnoreCase(tab.getSide()) ? "RIGHT" : "LEFT")));
        cy += 18;

        // Icon item selector
        var iconBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean iconHov = iconBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, iconBtn, iconHov, false, false);
        String shortIcon = tab.getIconItem().replace("minecraft:", "").replace("modernmachines:", "");
        graphics.text(font, Component.literal("Icon: " + shortIcon), iconBtn.position().x() + 4, iconBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(iconBtn, () -> editor.openItemPicker(itemId -> tab.setIconItem(itemId))));
        cy += 18;

        // Window Dimensions
        graphics.text(font, Component.literal("Window: " + tab.getWindowWidth() + "x" + tab.getWindowHeight()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
        int step = editor.isSnapToGrid() ? 18 : 4;
        cy = drawIncrementRow(graphics, x + 72, cy, font, mouseX, mouseY,
            () -> tab.setWindowWidth(Math.max(86, tab.getWindowWidth() - step)),
            () -> tab.setWindowWidth(tab.getWindowWidth() + step),
            () -> tab.setWindowHeight(Math.max(76, tab.getWindowHeight() - step)),
            () -> tab.setWindowHeight(tab.getWindowHeight() + step));
        cy += 6;

        // Delete Tab button
        var delTabBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean delHov = delTabBtn.contains(new Position(mouseX, mouseY));
        graphics.fill(delTabBtn.position().x(), delTabBtn.position().y(), delTabBtn.right(), delTabBtn.bottom(), delHov ? 0xFFE81123 : 0xFF7A1B24);
        GUIRenderHelper.drawRectOutline(graphics, delTabBtn, GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("Delete Tab"), delTabBtn.position().x() + 24, delTabBtn.position().y() + 3, 0xFFFFFFFF, true);
        actions.add(new ClickableAction(delTabBtn, () -> {
            editor.getActiveLayout().removeTab(tab);
            editor.setActiveTab(null);
        }));
        cy += 20;

        return cy;
    }

    private int renderElementProperties(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, ElementDefinition selected) {
        int cy = y;

        // Element Type
        graphics.text(font, Component.literal("Type: " + selected.getType().name()), x + 6, cy, GUIRenderHelper.ORE_TEXT_TITLE, true);
        cy += 14;

        // Lock / Unlock toggle button
        var lockBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean lockHov = lockBtn.contains(new Position(mouseX, mouseY));
        boolean isLocked = selected.isLocked();
        GUIRenderHelper.drawOreUIButton(graphics, lockBtn, lockHov, isLocked, isLocked);
        String lockText = isLocked ? "🔒 Locked" : "🔓 Unlocked";
        graphics.text(font, Component.literal(lockText), lockBtn.position().x() + 4, lockBtn.position().y() + 3, isLocked ? 0xFFFFAA00 : GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(lockBtn, () -> {
            editor.saveUndoState();
            selected.setLocked(!selected.isLocked());
        }));
        cy += 18;

        // Parent / Reparenting Selector
        var rootList = editor.getActiveTab() != null ? editor.getActiveTab().getElements() : editor.getActiveLayout().getElements();
        var currentParent = findDirectParent(rootList, selected);
        String parentLabel = currentParent == null ? "Root" : currentParent.getType().name();
        var parentBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean parentHov = parentBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, parentBtn, parentHov, false, false);
        graphics.text(font, Component.literal("Parent: " + parentLabel), parentBtn.position().x() + 4, parentBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(parentBtn, () -> cycleParent(rootList, selected, currentParent)));
        cy += 18;

        // Position (X, Y)
        graphics.text(font, Component.literal("Pos: " + selected.getX() + ", " + selected.getY()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
        cy = drawIncrementRow(graphics, x + 72, cy, font, mouseX, mouseY,
            () -> {
                if (!selected.isLocked()) {
                    selected.setX(Math.max(0, selected.getX() - 2));
                }
            },
            () -> {
                if (!selected.isLocked()) {
                    selected.setX(selected.getX() + 2);
                }
            },
            () -> {
                if (!selected.isLocked()) {
                    selected.setY(Math.max(0, selected.getY() - 2));
                }
            },
            () -> {
                if (!selected.isLocked()) {
                    selected.setY(selected.getY() + 2);
                }
            });
        cy += 4;

        // Size (W, H)
        graphics.text(font, Component.literal("Size: " + selected.getWidth() + "x" + selected.getHeight()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
        int winW = editor.getActiveTab() != null ? editor.getActiveTab().getWindowWidth() : (editor.getActiveLayout() != null ? editor.getActiveLayout().getImageWidth() : 176);
        int winH = editor.getActiveTab() != null ? editor.getActiveTab().getWindowHeight() : (editor.getActiveLayout() != null ? editor.getActiveLayout().getImageHeight() : 166);
        int maxW = Math.max(18, winW - 14);
        int maxH = Math.max(18, winH - 22);
        int elStep = editor.isSnapToGrid() ? 18 : 2;

        cy = drawIncrementRow(graphics, x + 72, cy, font, mouseX, mouseY,
            () -> {
                if (!selected.isLocked()) {
                    selected.setWidth(Math.max(4, selected.getWidth() - elStep));
                }
            },
            () -> {
                if (!selected.isLocked()) {
                    selected.setWidth(Math.min(maxW, selected.getWidth() + elStep));
                }
            },
            () -> {
                if (!selected.isLocked()) {
                    selected.setHeight(Math.max(4, selected.getHeight() - elStep));
                }
            },
            () -> {
                if (!selected.isLocked()) {
                    selected.setHeight(Math.min(maxH, selected.getHeight() + elStep));
                }
            });
        cy += 6;

        // Auto-Fit Parent Width / Height Toggles
        var fitWBtn = new Bounds(new Position(x + 6, cy), new Size((INSPECTOR_WIDTH - 20) / 2, 14));
        var fitHBtn = new Bounds(new Position(x + 8 + (INSPECTOR_WIDTH - 20) / 2, cy), new Size((INSPECTOR_WIDTH - 20) / 2, 14));

        boolean fitWHov = fitWBtn.contains(new Position(mouseX, mouseY));
        boolean fitHHov = fitHBtn.contains(new Position(mouseX, mouseY));
        boolean isFitW = selected.isFitParentWidth();
        boolean isFitH = selected.isFitParentHeight();

        GUIRenderHelper.drawOreUIButton(graphics, fitWBtn, fitWHov, isFitW, isFitW);
        graphics.text(font, Component.literal(isFitW ? "Fit W: ON" : "Fit W: OFF"), fitWBtn.position().x() + 3, fitWBtn.position().y() + 3, isFitW ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);
        actions.add(new ClickableAction(fitWBtn, () -> {
            editor.saveUndoState();
            selected.setFitParentWidth(!selected.isFitParentWidth());
        }));

        GUIRenderHelper.drawOreUIButton(graphics, fitHBtn, fitHHov, isFitH, isFitH);
        graphics.text(font, Component.literal(isFitH ? "Fit H: ON" : "Fit H: OFF"), fitHBtn.position().x() + 3, fitHBtn.position().y() + 3, isFitH ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);
        actions.add(new ClickableAction(fitHBtn, () -> {
            editor.saveUndoState();
            selected.setFitParentHeight(!selected.isFitParentHeight());
        }));
        cy += 18;

        // Flow Amount / Proportional Weight
        graphics.text(font, Component.literal("Flow: " + (selected.getFlowAmount() > 0 ? selected.getFlowAmount() : "0 (Fixed)")), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
        cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
            "-", () -> {
                editor.saveUndoState();
                selected.setFlowAmount(Math.max(0, selected.getFlowAmount() - 1));
            },
            "+", () -> {
                editor.saveUndoState();
                selected.setFlowAmount(selected.getFlowAmount() + 1);
            });
        cy += 4;

        // Slot specific
        if (selected.getType() == ElementDefinition.ElementType.SLOT || selected.getType() == ElementDefinition.ElementType.SLOT_GRID) {
            graphics.text(font, Component.literal("Slot Index: " + selected.getSlotIndex()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
            cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
                "-", () -> selected.setSlotIndex(Math.max(0, selected.getSlotIndex() - 1)),
                "+", () -> selected.setSlotIndex(selected.getSlotIndex() + 1));
            cy += 4;
        }

        // Grid rows & cols
        if (selected.getType() == ElementDefinition.ElementType.SLOT_GRID) {
            graphics.text(font, Component.literal("Grid: " + selected.getGridRows() + "x" + selected.getGridCols()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
            cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
                "-", () -> {
                    selected.setGridRows(Math.max(1, selected.getGridRows() - 1));
                    selected.setGridCols(Math.max(1, selected.getGridCols() - 1));
                },
                "+", () -> {
                    selected.setGridRows(selected.getGridRows() + 1);
                    selected.setGridCols(selected.getGridCols() + 1);
                });
            cy += 4;
        }

        // Button label text box
        if (selected.getType() == ElementDefinition.ElementType.BUTTON) {
            cy = drawTextBox(graphics, x, cy, font, mouseX, mouseY, "Label:", "button_text", selected.getText());
        }

        // Progress / Flame data index
        if (selected.getType() == ElementDefinition.ElementType.PROGRESS_ARROW || selected.getType() == ElementDefinition.ElementType.PROGRESS_LINEAR || selected.getType() == ElementDefinition.ElementType.FLAME) {
            graphics.text(font, Component.literal("Data Index: " + selected.getDataIndex()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
            cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
                "-", () -> selected.setDataIndex(Math.max(0, selected.getDataIndex() - 1)),
                "+", () -> selected.setDataIndex(selected.getDataIndex() + 1));
            cy += 4;
        }

        // ICON Element Properties
        if (selected.getType() == ElementDefinition.ElementType.ICON) {
            var itemBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean itemHov = itemBtn.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, itemBtn, itemHov, false, false);
            String shortIcon = selected.getIconItem().replace("minecraft:", "").replace("modernmachines:", "");
            graphics.text(font, Component.literal("Item: " + shortIcon), itemBtn.position().x() + 4, itemBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(itemBtn, () -> editor.openItemPicker(itemId -> selected.setIconItem(itemId))));
            cy += 18;

            int pct = Math.round(selected.getOpacity() * 100);
            graphics.text(font, Component.literal("Opacity: " + pct + "%"), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
            cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
                "-", () -> selected.setOpacity(Math.max(0.1f, selected.getOpacity() - 0.1f)),
                "+", () -> selected.setOpacity(Math.min(1.0f, selected.getOpacity() + 0.1f)));
            cy += 4;

            var modeBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean modeHov = modeBtn.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, modeBtn, modeHov, false, false);
            graphics.text(font, Component.literal("Mode: " + selected.getColorMode()), modeBtn.position().x() + 4, modeBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(modeBtn, () -> selected.setColorMode("RGB".equalsIgnoreCase(selected.getColorMode()) ? "GRAYSCALE" : "RGB")));
            cy += 18;
        }

        // BLOCK_FACE Element Properties
        if (selected.getType() == ElementDefinition.ElementType.BLOCK_FACE) {
            var faceBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean faceHov = faceBtn.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, faceBtn, faceHov, false, false);
            graphics.text(font, Component.literal("Side: " + selected.getRelativeSide()), faceBtn.position().x() + 4, faceBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(faceBtn, () -> {
                var next = switch (selected.getRelativeSide().toUpperCase()) {
                    case "TOP" -> "BOTTOM";
                    case "BOTTOM" -> "LEFT";
                    case "LEFT" -> "FRONT";
                    case "FRONT" -> "RIGHT";
                    case "RIGHT" -> "BACK";
                    default -> "TOP";
                };
                selected.setRelativeSide(next);
            }));
            cy += 18;
        }

        // TEXT / LABEL Properties
        if (selected.getType() == ElementDefinition.ElementType.TEXT) {
            var srcBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean srcHov = srcBtn.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, srcBtn, srcHov, false, false);
            graphics.text(font, Component.literal("Src: " + selected.getLabelSource().name()), srcBtn.position().x() + 4, srcBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(srcBtn, () -> {
                var next = switch (selected.getLabelSource()) {
                    case STATIC -> ElementDefinition.LabelSourceType.TRANSLATABLE;
                    case TRANSLATABLE -> ElementDefinition.LabelSourceType.MENU_DATA;
                    case MENU_DATA -> ElementDefinition.LabelSourceType.STATIC;
                };
                selected.setLabelSource(next);
            }));
            cy += 18;

            if (selected.getLabelSource() == ElementDefinition.LabelSourceType.STATIC) {
                cy = drawTextBox(graphics, x, cy, font, mouseX, mouseY, "Text:", "label_text", selected.getText());
            } else if (selected.getLabelSource() == ElementDefinition.LabelSourceType.TRANSLATABLE) {
                cy = drawTextBox(graphics, x, cy, font, mouseX, mouseY, "Key:", "label_text", selected.getText());
            } else if (selected.getLabelSource() == ElementDefinition.LabelSourceType.MENU_DATA) {
                graphics.text(font, Component.literal("Data Idx: " + selected.getDataIndex()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
                cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
                    "-", () -> selected.setDataIndex(Math.max(0, selected.getDataIndex() - 1)),
                    "+", () -> selected.setDataIndex(selected.getDataIndex() + 1));
                cy += 4;

                cy = drawTextBox(graphics, x, cy, font, mouseX, mouseY, "Format:", "format_text", selected.getLabelFormat());
            }

            var alignBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean alignHov = alignBtn.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, alignBtn, alignHov, false, false);
            graphics.text(font, Component.literal("Align: " + selected.getLabelAlign()), alignBtn.position().x() + 4, alignBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(alignBtn, () -> {
                if ("LEFT".equalsIgnoreCase(selected.getLabelAlign())) {
                    selected.setLabelAlign("CENTER");
                } else if ("CENTER".equalsIgnoreCase(selected.getLabelAlign())) {
                    selected.setLabelAlign("RIGHT");
                } else {
                    selected.setLabelAlign("LEFT");
                }
            }));
            cy += 18;
        }

        // Container Gap, AlignItems & JustifyContent
        if (selected.getType() == ElementDefinition.ElementType.COLUMN || selected.getType() == ElementDefinition.ElementType.ROW) {
            graphics.text(font, Component.literal("Gap: " + selected.getGap()), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
            cy = drawSimpleButtons(graphics, x + 82, cy, font, mouseX, mouseY,
                "-", () -> selected.setGap(Math.max(0, selected.getGap() - 1)),
                "+", () -> selected.setGap(selected.getGap() + 1));
            cy += 4;

            // AlignItems (Cross Axis)
            var alignBounds = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean alignHovered = alignBounds.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, alignBounds, alignHovered, false, false);
            graphics.text(font, Component.literal("Align: " + selected.getAlign()), alignBounds.position().x() + 4, alignBounds.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(alignBounds, () -> {
                if ("CENTER".equalsIgnoreCase(selected.getAlign())) {
                    selected.setAlign("START");
                } else if ("START".equalsIgnoreCase(selected.getAlign())) {
                    selected.setAlign("END");
                } else {
                    selected.setAlign("CENTER");
                }
            }));
            cy += 18;

            // JustifyContent (Main Axis)
            var justifyBounds = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
            boolean justHovered = justifyBounds.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, justifyBounds, justHovered, false, false);
            String shortJust = selected.getJustifyContent().replace("SPACE_", "S_");
            graphics.text(font, Component.literal("Justify: " + shortJust), justifyBounds.position().x() + 4, justifyBounds.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(justifyBounds, () -> {
                var next = switch (selected.getJustifyContent()) {
                    case "START" -> "CENTER";
                    case "CENTER" -> "END";
                    case "END" -> "SPACE_BETWEEN";
                    case "SPACE_BETWEEN" -> "SPACE_AROUND";
                    case "SPACE_AROUND" -> "SPACE_EVENLY";
                    default -> "START";
                };
                selected.setJustifyContent(next);
            }));
            cy += 18;
        }

        // Collapsible Child Elements Section
        cy = drawChildElementsSection(graphics, x, cy, font, mouseX, mouseY, selected);

        // Hierarchy Grouping Actions
        cy = drawHierarchyActions(graphics, x, cy, font, mouseX, mouseY, selected);

        // Delete Element button
        var deleteBounds = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean delHovered = deleteBounds.contains(new Position(mouseX, mouseY));
        graphics.fill(deleteBounds.position().x(), deleteBounds.position().y(), deleteBounds.right(), deleteBounds.bottom(), delHovered ? 0xFFE81123 : 0xFF7A1B24);
        GUIRenderHelper.drawRectOutline(graphics, deleteBounds, GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("Delete Element"), deleteBounds.position().x() + 16, deleteBounds.position().y() + 3, 0xFFFFFFFF, true);
        actions.add(new ClickableAction(deleteBounds, () -> editor.deleteSelectedElement()));
        cy += 20;

        return cy;
    }

    private int drawChildElementsSection(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, ElementDefinition selected) {
        int cy = y;

        var headerBounds = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean headerHov = headerBounds.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, headerBounds, headerHov, false, false);
        String arrow = isChildrenSectionCollapsed ? "▶ " : "▼ ";
        String title = arrow + "Children (" + selected.getChildren().size() + ")";
        graphics.text(font, Component.literal(title), headerBounds.position().x() + 4, headerBounds.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(headerBounds, () -> {
            isChildrenSectionCollapsed = !isChildrenSectionCollapsed;
        }));
        cy += 16;

        if (isChildrenSectionCollapsed) {
            return cy;
        }

        if (selected.getChildren().isEmpty()) {
            graphics.text(font, Component.literal("No child elements."), x + 8, cy + 2, GUIRenderHelper.ORE_TEXT_DARK, false);
            cy += 14;
        } else {
            var children = selected.getChildren();
            for (int i = 0; i < children.size(); i++) {
                int childIndex = i;
                var child = children.get(i);
                var rowBounds = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 13));
                boolean rowHov = rowBounds.contains(new Position(mouseX, mouseY));

                if (rowHov) {
                    graphics.fill(rowBounds.position().x(), rowBounds.position().y(), rowBounds.right(), rowBounds.bottom(), GUIRenderHelper.ORE_BUTTON_HOVER);
                }

                String label = "• " + child.getType().name();
                if (child.getFlowAmount() > 0) {
                    label += ":" + child.getFlowAmount();
                }

                if (label.length() > 9) {
                    label = label.substring(0, 9);
                }

                graphics.text(font, Component.literal(label), rowBounds.position().x() + 2, rowBounds.position().y() + 3, GUIRenderHelper.ORE_TEXT_MUTED, false);

                // Select button
                var selBtn = new Bounds(new Position(x + 58, cy), new Size(22, 12));
                drawBtn(graphics, selBtn, "Sel", font, mouseX, mouseY, () -> editor.setSelectedElement(child));

                // Move Up button
                var upBtn = new Bounds(new Position(x + 82, cy), new Size(11, 12));
                drawBtn(graphics, upBtn, "↑", font, mouseX, mouseY, () -> {
                    if (childIndex > 0) {
                        editor.saveUndoState();
                        Collections.swap(children, childIndex, childIndex - 1);
                    }
                });

                // Move Down button
                var downBtn = new Bounds(new Position(x + 94, cy), new Size(11, 12));
                drawBtn(graphics, downBtn, "↓", font, mouseX, mouseY, () -> {
                    if (childIndex < children.size() - 1) {
                        editor.saveUndoState();
                        Collections.swap(children, childIndex, childIndex + 1);
                    }
                });

                // Detach from parent button
                var detachBtn = new Bounds(new Position(x + 106, cy), new Size(11, 12));
                drawBtn(graphics, detachBtn, "✕", font, mouseX, mouseY, () -> {
                    editor.saveUndoState();
                    selected.removeChild(child);
                    var rootList = editor.getActiveTab() != null ? editor.getActiveTab().getElements() : editor.getActiveLayout().getElements();
                    rootList.add(child);
                });

                cy += 15;
            }
        }

        // Button: Add Child directly to this container
        var addBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 13));
        boolean addHov = addBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, addBtn, addHov, false, false);
        graphics.text(font, Component.literal("+ Add Child (Slot)"), addBtn.position().x() + 8, addBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(addBtn, () -> {
            editor.saveUndoState();
            var newChild = new ElementDefinition(ElementDefinition.ElementType.SLOT);
            selected.addChild(newChild);
            editor.setSelectedElement(newChild);
        }));
        cy += 18;

        return cy;
    }

    private void cycleParent(List<ElementDefinition> rootList, ElementDefinition target, ElementDefinition currentParent) {
        editor.saveUndoState();

        List<ElementDefinition> candidates = new ArrayList<>();
        collectCandidateContainers(rootList, target, candidates);

        if (currentParent == null) {
            if (!candidates.isEmpty()) {
                rootList.remove(target);
                candidates.get(0).addChild(target);
                editor.showToast("Reparented to " + candidates.get(0).getType().name());
            }
        } else {
            int curIdx = candidates.indexOf(currentParent);
            currentParent.removeChild(target);
            if (curIdx >= 0 && curIdx < candidates.size() - 1) {
                var nextParent = candidates.get(curIdx + 1);
                nextParent.addChild(target);
                editor.showToast("Reparented to " + nextParent.getType().name());
            } else {
                rootList.add(target);
                editor.showToast("Detached to Root");
            }
        }
    }

    private void collectCandidateContainers(List<ElementDefinition> list, ElementDefinition target, List<ElementDefinition> candidates) {
        for (var el : list) {
            if (el == target) {
                continue;
            }

            if (el.getType() == ElementDefinition.ElementType.COLUMN || el.getType() == ElementDefinition.ElementType.ROW) {
                candidates.add(el);
            }

            collectCandidateContainers(el.getChildren(), target, candidates);
        }
    }

    private ElementDefinition findDirectParent(List<ElementDefinition> list, ElementDefinition target) {
        for (var el : list) {
            if (el.getChildren().contains(target)) {
                return el;
            }

            var found = findDirectParent(el.getChildren(), target);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private int drawTextBox(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, String label, String fieldId, String value) {
        int cy = y;
        graphics.text(font, Component.literal(label), x + 6, cy + 2, GUIRenderHelper.ORE_TEXT_MUTED, false);
        cy += 12;

        var boxBounds = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 14));
        boolean isFocused = fieldId.equals(focusedField);
        GUIRenderHelper.drawRect(graphics, boxBounds, GUIRenderHelper.ORE_SLOT_BG);
        GUIRenderHelper.drawRectOutline(graphics, boxBounds, isFocused ? GUIRenderHelper.ORE_GREEN_PRIMARY : GUIRenderHelper.ORE_SLOT_BORDER);

        String displayVal = value != null ? value : "";
        String textToDraw = displayVal;
        if (isFocused && (System.currentTimeMillis() % 1000 < 500)) {
            textToDraw += "§a|";
        }

        if (font.width(textToDraw) > INSPECTOR_WIDTH - 24) {
            textToDraw = "..." + font.plainSubstrByWidth(textToDraw, INSPECTOR_WIDTH - 30, true);
        }

        graphics.text(font, Component.literal(textToDraw), boxBounds.position().x() + 4, boxBounds.position().y() + 3, 0xFFFFFFFF, false);
        actions.add(new ClickableAction(boxBounds, () -> {
            focusedField = fieldId;
        }));

        return cy + 18;
    }

    private int drawHierarchyActions(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, ElementDefinition selected) {
        int cy = y;

        // Button: Wrap in Column
        var wrapColBtn = new Bounds(new Position(x + 6, cy), new Size((INSPECTOR_WIDTH - 20) / 2, 13));
        boolean wrapColHov = wrapColBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, wrapColBtn, wrapColHov, false, false);
        graphics.text(font, Component.literal("Wrap Col"), wrapColBtn.position().x() + 4, wrapColBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(wrapColBtn, () -> wrapElement(selected, ElementDefinition.ElementType.COLUMN)));

        // Button: Wrap in Row
        var wrapRowBtn = new Bounds(new Position(x + 8 + (INSPECTOR_WIDTH - 20) / 2, cy), new Size((INSPECTOR_WIDTH - 20) / 2, 13));
        boolean wrapRowHov = wrapRowBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, wrapRowBtn, wrapRowHov, false, false);
        graphics.text(font, Component.literal("Wrap Row"), wrapRowBtn.position().x() + 4, wrapRowBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
        actions.add(new ClickableAction(wrapRowBtn, () -> wrapElement(selected, ElementDefinition.ElementType.ROW)));
        cy += 16;

        // If container, button to Ungroup
        if (selected.getType() == ElementDefinition.ElementType.COLUMN || selected.getType() == ElementDefinition.ElementType.ROW) {
            var ungroupBtn = new Bounds(new Position(x + 6, cy), new Size(INSPECTOR_WIDTH - 16, 13));
            boolean unHov = ungroupBtn.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIButton(graphics, ungroupBtn, unHov, false, false);
            graphics.text(font, Component.literal("Ungroup Children"), ungroupBtn.position().x() + 10, ungroupBtn.position().y() + 3, GUIRenderHelper.ORE_TEXT_TITLE, false);
            actions.add(new ClickableAction(ungroupBtn, () -> ungroupElement(selected)));
            cy += 16;
        }

        return cy;
    }

    private void wrapElement(ElementDefinition target, ElementDefinition.ElementType containerType) {
        editor.saveUndoState();
        var container = new ElementDefinition(containerType);
        container.setX(target.getX());
        container.setY(target.getY());
        container.setWidth(Math.max(target.getWidth() + 4, 30));
        container.setHeight(Math.max(target.getHeight() + 4, 20));

        var rootList = editor.getActiveTab() != null ? editor.getActiveTab().getElements() : editor.getActiveLayout().getElements();
        replaceElementInTree(rootList, target, container);
        container.addChild(target);
        target.setX(2);
        target.setY(2);
        editor.setSelectedElement(container);
    }

    private void ungroupElement(ElementDefinition container) {
        editor.saveUndoState();
        var rootList = editor.getActiveTab() != null ? editor.getActiveTab().getElements() : editor.getActiveLayout().getElements();
        var parentList = findParentList(rootList, container);
        if (parentList != null) {
            int idx = parentList.indexOf(container);
            parentList.remove(container);
            for (int i = 0; i < container.getChildren().size(); i++) {
                parentList.add(idx + i, container.getChildren().get(i));
            }

            editor.setSelectedElement(null);
        }
    }

    private boolean replaceElementInTree(List<ElementDefinition> list, ElementDefinition target, ElementDefinition replacement) {
        int idx = list.indexOf(target);
        if (idx >= 0) {
            list.set(idx, replacement);
            return true;
        }

        for (var el : list) {
            if (replaceElementInTree(el.getChildren(), target, replacement)) {
                return true;
            }
        }

        return false;
    }

    private List<ElementDefinition> findParentList(List<ElementDefinition> currentList, ElementDefinition target) {
        if (currentList.contains(target)) {
            return currentList;
        }

        for (var el : currentList) {
            var found = findParentList(el.getChildren(), target);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private int renderTreeNode(GuiGraphicsExtractor graphics, int x, int y, ElementDefinition el, int depth, net.minecraft.client.gui.Font font, int mouseX, int mouseY, List<ElementDefinition> siblingList) {
        int nodeY = y;
        var nodeBounds = new Bounds(new Position(x + depth * 8, nodeY), new Size(INSPECTOR_WIDTH - 16 - depth * 8, 12));
        boolean hovered = nodeBounds.contains(new Position(mouseX, mouseY));
        boolean isSelected = editor.getSelectedElement() == el;
        boolean hasChildren = !el.getChildren().isEmpty();
        boolean isCollapsed = collapsedNodes.contains(el.getId());

        if (isSelected) {
            graphics.fill(nodeBounds.position().x(), nodeBounds.position().y(), nodeBounds.right(), nodeBounds.bottom(), GUIRenderHelper.ORE_GREEN_PRIMARY);
        } else if (hovered) {
            graphics.fill(nodeBounds.position().x(), nodeBounds.position().y(), nodeBounds.right(), nodeBounds.bottom(), GUIRenderHelper.ORE_BUTTON_HOVER);
        }

        String icon = hasChildren ? (isCollapsed ? "▶ " : "▼ ") : "• ";
        String lockBadge = el.isLocked() ? " 🔒" : "";
        String label = icon + el.getType().name() + lockBadge;
        int color = isSelected ? 0xFFFFFFFF : (hovered ? GUIRenderHelper.ORE_TEXT_TITLE : GUIRenderHelper.ORE_TEXT_MUTED);
        graphics.text(font, Component.literal(label), nodeBounds.position().x() + 2, nodeBounds.position().y() + 2, color, false);

        // Click node to select or toggle collapse
        actions.add(new ClickableAction(nodeBounds, () -> {
            if (hasChildren && hovered && mouseX <= nodeBounds.position().x() + 10) {
                if (isCollapsed) {
                    collapsedNodes.remove(el.getId());
                } else {
                    collapsedNodes.add(el.getId());
                }
            } else {
                editor.setSelectedElement(el);
            }
        }));

        nodeY += 13;

        if (hasChildren && !isCollapsed) {
            for (var child : el.getChildren()) {
                nodeY = renderTreeNode(graphics, x, nodeY, child, depth + 1, font, mouseX, mouseY, el.getChildren());
            }
        }

        return nodeY;
    }

    private int drawIncrementRow(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, Runnable decW, Runnable incW, Runnable decH, Runnable incH) {
        var decWBounds = new Bounds(new Position(x, y), new Size(12, 12));
        var incWBounds = new Bounds(new Position(x + 13, y), new Size(12, 12));
        var decHBounds = new Bounds(new Position(x + 26, y), new Size(12, 12));
        var incHBounds = new Bounds(new Position(x + 39, y), new Size(12, 12));

        drawBtn(graphics, decWBounds, "<", font, mouseX, mouseY, decW);
        drawBtn(graphics, incWBounds, ">", font, mouseX, mouseY, incW);
        drawBtn(graphics, decHBounds, "-", font, mouseX, mouseY, decH);
        drawBtn(graphics, incHBounds, "+", font, mouseX, mouseY, incH);

        return y + 14;
    }

    private int drawSimpleButtons(GuiGraphicsExtractor graphics, int x, int y, net.minecraft.client.gui.Font font, int mouseX, int mouseY, String label1, Runnable run1, String label2, Runnable run2) {
        var b1 = new Bounds(new Position(x, y), new Size(18, 12));
        var b2 = new Bounds(new Position(x + 20, y), new Size(18, 12));

        drawBtn(graphics, b1, label1, font, mouseX, mouseY, run1);
        drawBtn(graphics, b2, label2, font, mouseX, mouseY, run2);

        return y + 14;
    }

    private void drawBtn(GuiGraphicsExtractor graphics, Bounds bounds, String label, net.minecraft.client.gui.Font font, int mouseX, int mouseY, Runnable onClick) {
        boolean hovered = bounds.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, bounds, hovered, false, false);
        graphics.text(font, Component.literal(label), bounds.position().x() + 4, bounds.position().y() + 2, hovered ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);
        actions.add(new ClickableAction(bounds, onClick));
    }
}
