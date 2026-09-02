package io.github.gtbauke.modernmachines.api.client.gui.elements;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.Column;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.FlexContainer;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.FlexDirection;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.Row;
import io.github.gtbauke.modernmachines.api.client.gui.core.render.GUIRenderHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerInventoryElement extends FlexContainer {
    public static final int WIDTH = 162;
    public static final int MAIN_INV_HEIGHT = 54;
    public static final int HOTBAR_GAP = 4;
    public static final int HOTBAR_HEIGHT = 18;
    public static final int TOTAL_SLOTS_HEIGHT = 76;
    public static final int LABEL_OFFSET = 12;

    private final AbstractContainerMenu menu;
    private final int playerInvStartIndex;
    private boolean showLabel = true;
    private Component label = Component.translatable("container.inventory");
    private int labelColor = GUIRenderHelper.ORE_TEXT_TITLE;

    public PlayerInventoryElement(AbstractContainerMenu menu, int playerInvStartIndex, boolean showLabel) {
        super(FlexDirection.COLUMN, JustifyContent.START, AlignItems.START, 2);
        this.menu = menu;
        this.playerInvStartIndex = playerInvStartIndex;
        this.showLabel = showLabel;
        this.setPadding(Padding.ZERO);
        rebuildSlots();
    }

    public PlayerInventoryElement(AbstractContainerMenu menu, int playerInvStartIndex) {
        this(menu, playerInvStartIndex, true);
    }

    public void rebuildSlots() {
        this.clearChildren();

        if (showLabel && label != null) {
            var labelElement = new LabelElement(label).setColor(labelColor);
            this.addChild(labelElement);
        }

        var mainInv = new Column(0, AlignItems.START);
        for (var row = 0; row < 3; row++) {
            var rowElement = new Row(0, AlignItems.START);
            for (var col = 0; col < 9; col++) {
                int slotIndex = playerInvStartIndex + col + row * 9;
                var slot = (menu != null && slotIndex >= 0 && slotIndex < menu.slots.size()) ? menu.slots.get(slotIndex) : null;
                rowElement.addChild(new SlotElement(slot));
            }

            mainInv.addChild(rowElement);
        }

        this.addChild(mainInv);

        var hotbarRow = new Row(0, AlignItems.START);
        for (var col = 0; col < 9; col++) {
            int slotIndex = playerInvStartIndex + 27 + col;
            var slot = (menu != null && slotIndex >= 0 && slotIndex < menu.slots.size()) ? menu.slots.get(slotIndex) : null;
            hotbarRow.addChild(new SlotElement(slot));
        }

        this.addChild(hotbarRow);
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public PlayerInventoryElement setShowLabel(boolean showLabel) {
        if (this.showLabel != showLabel) {
            this.showLabel = showLabel;
            rebuildSlots();
        }

        return this;
    }

    public Component getLabel() {
        return label;
    }

    public PlayerInventoryElement setLabel(Component label) {
        this.label = label;
        rebuildSlots();
        return this;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public PlayerInventoryElement setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        rebuildSlots();
        return this;
    }
}
