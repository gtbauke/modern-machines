package io.github.gtbauke.modernmachines.client.gui.editor.picker;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemPickerModal extends UIElement {
    public static final int MODAL_WIDTH = 190;
    public static final int MODAL_HEIGHT = 200;
    public static final int COLS = 9;
    public static final int SLOT_SIZE = 18;

    private final Consumer<String> onSelect;
    private final Runnable onClose;

    private String searchQuery = "";
    private final List<Item> allItems = new ArrayList<>();
    private final List<Item> filteredItems = new ArrayList<>();

    private int scrollRowOffset = 0;
    private final List<SlotHitbox> activeSlotHitboxes = new ArrayList<>();

    public record SlotHitbox(Bounds bounds, String itemId) {}

    public ItemPickerModal(Consumer<String> onSelect, Runnable onClose) {
        super(new Bounds(Position.ZERO, new Size(MODAL_WIDTH, MODAL_HEIGHT)));
        this.onSelect = onSelect;
        this.onClose = onClose;

        // Populate items from registry
        for (var item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR) {
                allItems.add(item);
            }
        }

        filterItems();
    }

    public void filterItems() {
        filteredItems.clear();
        String query = searchQuery.trim().toLowerCase();
        for (var item : allItems) {
            var key = BuiltInRegistries.ITEM.getKey(item);
            if (key != null) {
                String path = key.getPath().toLowerCase();
                String full = key.toString().toLowerCase();
                if (query.isEmpty() || path.contains(query) || full.contains(query)) {
                    filteredItems.add(item);
                }
            }
        }

        scrollRowOffset = 0;
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query : "";
        filterItems();
    }

    public void appendSearchChar(char c) {
        if (Character.isLetterOrDigit(c) || c == '_' || c == ':' || c == '-') {
            this.searchQuery += Character.toLowerCase(c);
            filterItems();
        }
    }

    public void backspaceSearch() {
        if (!searchQuery.isEmpty()) {
            this.searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            filterItems();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        var clickPos = new Position((int) mouseX, (int) mouseY);
        var absBounds = getAbsoluteBounds();

        // Check Close button (top right)
        var closeBtn = new Bounds(new Position(absBounds.right() - 18, absBounds.position().y() + 3), new Size(14, 14));
        if (closeBtn.contains(clickPos)) {
            if (onClose != null) {
                onClose.run();
            }

            return true;
        }

        // Check clicked item slots
        for (var slot : activeSlotHitboxes) {
            if (slot.bounds().contains(clickPos)) {
                if (onSelect != null) {
                    onSelect.accept(slot.itemId());
                }

                if (onClose != null) {
                    onClose.run();
                }

                return true;
            }
        }

        // Clicking inside modal consumes click
        if (absBounds.contains(clickPos)) {
            return true;
        }

        // Clicking outside closes modal
        if (onClose != null) {
            onClose.run();
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalRows = (int) Math.ceil((double) filteredItems.size() / (double) COLS);
        int visibleRows = 8;
        int maxOffset = Math.max(0, totalRows - visibleRows);

        if (scrollY > 0) {
            scrollRowOffset = Math.max(0, scrollRowOffset - 1);
            return true;
        } else if (scrollY < 0) {
            scrollRowOffset = Math.min(maxOffset, scrollRowOffset + 1);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        activeSlotHitboxes.clear();

        // Modal shadow & background
        GUIRenderHelper.drawDropShadow(graphics, absoluteBounds, 0x80000000, 6);
        GUIRenderHelper.drawOreUIPanel(graphics, absoluteBounds);

        var font = Minecraft.getInstance().font;
        int x = absoluteBounds.position().x();
        int y = absoluteBounds.position().y();
        int right = absoluteBounds.right();

        // Header bar
        graphics.fill(x + 1, y + 1, right - 1, y + 18, GUIRenderHelper.ORE_BG_PRIMARY);
        GUIRenderHelper.drawLine(graphics, new Position(x, y + 18), new Position(right, y + 19), GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("Select Item Icon"), x + 8, y + 5, GUIRenderHelper.ORE_TEXT_TITLE, true);

        // Close button
        var closeBtn = new Bounds(new Position(right - 18, y + 3), new Size(14, 14));
        boolean closeHov = closeBtn.contains(new Position(mouseX, mouseY));
        graphics.fill(closeBtn.position().x(), closeBtn.position().y(), closeBtn.right(), closeBtn.bottom(), closeHov ? 0xFFE81123 : GUIRenderHelper.ORE_BUTTON_BG);
        GUIRenderHelper.drawRectOutline(graphics, closeBtn, GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("✕"), closeBtn.position().x() + 4, closeBtn.position().y() + 3, closeHov ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);

        // Search Bar box
        var searchBox = new Bounds(new Position(x + 8, y + 22), new Size(MODAL_WIDTH - 16, 14));
        GUIRenderHelper.drawRect(graphics, searchBox, GUIRenderHelper.ORE_SLOT_BG);
        GUIRenderHelper.drawRectOutline(graphics, searchBox, GUIRenderHelper.ORE_SLOT_BORDER);
        String searchDisplay = searchQuery.isEmpty() ? "§7Search..." : searchQuery + (System.currentTimeMillis() % 1000 < 500 ? "§8_" : "");
        graphics.text(font, Component.literal(searchDisplay), searchBox.position().x() + 4, searchBox.position().y() + 3, 0xFFFFFFFF, false);

        // Item Grid
        int startX = x + 14;
        int startY = y + 40;
        int visibleRows = 8;
        int startIndex = scrollRowOffset * COLS;

        for (int r = 0; r < visibleRows; r++) {
            for (int c = 0; c < COLS; c++) {
                int itemIdx = startIndex + r * COLS + c;
                int slotX = startX + c * SLOT_SIZE;
                int slotY = startY + r * SLOT_SIZE;
                var slotBounds = new Bounds(new Position(slotX, slotY), new Size(SLOT_SIZE, SLOT_SIZE));

                GUIRenderHelper.drawOreUISlot(graphics, slotBounds);

                if (itemIdx < filteredItems.size()) {
                    var item = filteredItems.get(itemIdx);
                    var itemId = BuiltInRegistries.ITEM.getKey(item).toString();
                    activeSlotHitboxes.add(new SlotHitbox(slotBounds, itemId));

                    graphics.fakeItem(new ItemStack(item), slotX + 1, slotY + 1);

                    // Hover highlight
                    if (slotBounds.contains(new Position(mouseX, mouseY))) {
                        graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x50FFFFFF);
                    }
                }
            }
        }

        // Hovered Item Tooltip
        for (var slot : activeSlotHitboxes) {
            if (slot.bounds().contains(new Position(mouseX, mouseY))) {
                var itemHolder = BuiltInRegistries.ITEM.get(net.minecraft.resources.Identifier.parse(slot.itemId()));
                if (itemHolder.isPresent()) {
                    var stack = new ItemStack(itemHolder.get().value());
                    graphics.text(font, stack.getHoverName(), x + 8, absoluteBounds.bottom() - 12, GUIRenderHelper.ORE_TEXT_TITLE, true);
                }

                break;
            }
        }
    }
}
