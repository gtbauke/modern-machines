package io.github.gtbauke.modernmachines.client.gui.editor.palette;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.editor.ScreenEditorOverlay;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ElementDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ElementPalette extends UIElement {
    public static final int PALETTE_WIDTH = 114;

    private final ScreenEditorOverlay editor;
    private final Consumer<ElementDefinition> onElementSpawn;
    private final List<PaletteItem> items = new ArrayList<>();
    private final List<ClickableAction> actions = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private boolean isDraggingScrollbar = false;

    public record PaletteItem(String label, ElementDefinition.ElementType type, String category) {}
    public record ClickableAction(Bounds bounds, Runnable onClick) {}

    public ElementPalette(ScreenEditorOverlay editor, Consumer<ElementDefinition> onElementSpawn) {
        super(new Bounds(Position.ZERO, new Size(PALETTE_WIDTH, 200)));
        this.editor = editor;
        this.onElementSpawn = onElementSpawn;

        registerPaletteItems();
    }

    private void registerPaletteItems() {
        // Slots
        items.add(new PaletteItem("Single Slot", ElementDefinition.ElementType.SLOT, "Slots"));
        items.add(new PaletteItem("2x2 Slot Grid", ElementDefinition.ElementType.SLOT_GRID, "Slots"));
        items.add(new PaletteItem("Player Inv (36)", ElementDefinition.ElementType.PLAYER_INVENTORY, "Slots"));

        // Progress & Meters
        items.add(new PaletteItem("Progress Arrow", ElementDefinition.ElementType.PROGRESS_ARROW, "Meters"));
        items.add(new PaletteItem("Linear Bar", ElementDefinition.ElementType.PROGRESS_LINEAR, "Meters"));
        items.add(new PaletteItem("Fuel Flame", ElementDefinition.ElementType.FLAME, "Meters"));

        // Layout Containers
        items.add(new PaletteItem("Column (VBox)", ElementDefinition.ElementType.COLUMN, "Layout"));
        items.add(new PaletteItem("Row (HBox)", ElementDefinition.ElementType.ROW, "Layout"));
        items.add(new PaletteItem("Spacer", ElementDefinition.ElementType.SPACER, "Layout"));

        // Widgets
        items.add(new PaletteItem("Button", ElementDefinition.ElementType.BUTTON, "Widgets"));
        items.add(new PaletteItem("Text Label", ElementDefinition.ElementType.TEXT, "Widgets"));
        items.add(new PaletteItem("Item Icon", ElementDefinition.ElementType.ICON, "Widgets"));
        items.add(new PaletteItem("Side Tab", ElementDefinition.ElementType.SIDE_TAB, "Widgets"));

        // Machine Config
        items.add(new PaletteItem("Block Face", ElementDefinition.ElementType.BLOCK_FACE, "Machine Config"));
        items.add(new PaletteItem("Side Config Grid", ElementDefinition.ElementType.SIDE_CONFIG_GRID, "Machine Config"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        var clickPos = new Position((int) mouseX, (int) mouseY);
        var absBounds = getAbsoluteBounds();
        if (!absBounds.contains(clickPos)) {
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

        for (var action : actions) {
            if (action.bounds().contains(clickPos)) {
                if (action.bounds().bottom() >= viewTop && action.bounds().position().y() <= viewBottom) {
                    action.onClick().run();
                    return true;
                }
            }
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
        graphics.text(font, Component.literal("UI Elements"), x + 6, y + 4, GUIRenderHelper.ORE_TEXT_TITLE, true);

        // 2. Viewport & Scissor Setup
        int viewTop = y + 18;
        int viewBottom = absoluteBounds.bottom() - 2;
        int viewHeight = viewBottom - viewTop;
        int btnWidth = PALETTE_WIDTH - 14;

        // Calculate total content height with category headers
        int totalContentH = 6;
        String lastCategory = null;
        for (var item : items) {
            if (!item.category().equals(lastCategory)) {
                totalContentH += 14;
                lastCategory = item.category();
            }

            totalContentH += 18;
        }

        maxScroll = Math.max(0, totalContentH - viewHeight);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        graphics.enableScissor(x + 1, viewTop, right - 1, viewBottom);

        int curY = viewTop + 4 - scrollOffset;
        lastCategory = null;

        for (var item : items) {
            // Category header
            if (!item.category().equals(lastCategory)) {
                lastCategory = item.category();
                if (curY >= viewTop - 12 && curY <= viewBottom) {
                    graphics.text(font, Component.literal("§8" + lastCategory.toUpperCase()), x + 6, curY + 2, 0xFF888888, false);
                }

                curY += 14;
            }

            var itemBounds = new Bounds(new Position(x + 4, curY), new Size(btnWidth, 16));
            boolean hovered = itemBounds.contains(new Position(mouseX, mouseY)) && mouseX < right - 6;

            if (curY + 16 >= viewTop && curY <= viewBottom) {
                GUIRenderHelper.drawOreUIButton(graphics, itemBounds, hovered, false, false);
                graphics.text(font, Component.literal("+ " + item.label()), itemBounds.position().x() + 4, itemBounds.position().y() + 4, hovered ? GUIRenderHelper.ORE_TEXT_TITLE : GUIRenderHelper.ORE_TEXT_MUTED, false);

                actions.add(new ClickableAction(itemBounds, () -> {
                    var newDef = new ElementDefinition(item.type());
                    if (onElementSpawn != null) {
                        onElementSpawn.accept(newDef);
                    }
                }));
            }

            curY += 18;
        }

        graphics.disableScissor();

        // 3. Scrollbar
        if (maxScroll > 0) {
            int trackX = right - 6;
            graphics.fill(trackX, viewTop, right - 2, viewBottom, 0xFF141416);

            float scrollRatio = (float) scrollOffset / (float) maxScroll;
            int thumbHeight = Math.max(12, (int) ((float) viewHeight / (float) totalContentH * viewHeight));
            int thumbY = viewTop + (int) (scrollRatio * (viewHeight - thumbHeight));

            var thumbBounds = new Bounds(new Position(trackX, thumbY), new Size(4, thumbHeight));
            boolean thumbHovered = thumbBounds.contains(new Position(mouseX, mouseY)) || isDraggingScrollbar;
            int thumbColor = thumbHovered ? GUIRenderHelper.ORE_GREEN_PRIMARY : 0xFF4A4E58;

            graphics.fill(trackX, thumbY, right - 2, thumbY + thumbHeight, thumbColor);
        }
    }
}
