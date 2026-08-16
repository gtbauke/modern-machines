package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerInventory extends FlexContainer {

    public PlayerInventory(AbstractContainerMenu menu, int startIndex, boolean showLabel) {
        super(FlexDirection.COLUMN);
        getFlexNode().setGap(2);

        if (showLabel) {
            LabelWidget label = new LabelWidget(Component.translatable("container.inventory"));
            label.setColor(0xFF404040);
            label.setShadow(false);
            addChild(label);
        }

        // 3x9 Main Inventory Grid
        FlexContainer mainInv = new FlexContainer(FlexDirection.COLUMN);
        mainInv.getFlexNode().setGap(2);
        for (int row = 0; row < 3; row++) {
            FlexContainer rowContainer = new FlexContainer(FlexDirection.ROW);
            rowContainer.getFlexNode().setGap(2);
            for (int col = 0; col < 9; col++) {
                int slotIndex = startIndex + row * 9 + col;
                if (slotIndex < menu.slots.size()) {
                    rowContainer.addChild(SlotWidget.of(menu.slots.get(slotIndex)));
                }
            }
            mainInv.addChild(rowContainer);
        }
        addChild(mainInv);

        // Vertical spacer between main inventory and hotbar
        addChild(SizedBox.vertical(2));

        // 1x9 Hotbar Grid
        FlexContainer hotbar = new FlexContainer(FlexDirection.ROW);
        hotbar.getFlexNode().setGap(2);
        for (int col = 0; col < 9; col++) {
            int slotIndex = startIndex + 27 + col;
            if (slotIndex < menu.slots.size()) {
                hotbar.addChild(SlotWidget.of(menu.slots.get(slotIndex)));
            }
        }
        addChild(hotbar);
    }

    public static PlayerInventory of(AbstractContainerMenu menu) {
        int startIndex = Math.max(0, menu.slots.size() - 36);
        return new PlayerInventory(menu, startIndex, true);
    }

    public static PlayerInventory of(AbstractContainerMenu menu, int startIndex) {
        return new PlayerInventory(menu, startIndex, true);
    }

    public static PlayerInventory of(AbstractContainerMenu menu, boolean showLabel) {
        int startIndex = Math.max(0, menu.slots.size() - 36);
        return new PlayerInventory(menu, startIndex, showLabel);
    }
}
