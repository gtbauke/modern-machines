package io.github.gtbauke.modernmachines.client.gui.widget;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerInventoryWidget extends FlexContainer {

    public PlayerInventoryWidget(AbstractContainerMenu menu, int playerInvStartIndex) {
        super(FlexDirection.COLUMN);
        flexNode.setAlignItems(AlignItems.CENTER);
        flexNode.setGap(2);
        flexNode.setSize(162, 76);

        // Inventory title
        LabelWidget invLabel = new LabelWidget(Component.translatable("container.inventory"));
        invLabel.getFlexNode().setSize(162, 8);
        invLabel.setColor(0xFFCCCCCC);
        invLabel.setShadow(false);
        this.addChild(invLabel);

        // 3x9 Main inventory slots (exact 18px grid)
        FlexContainer mainInvGrid = new FlexContainer(FlexDirection.COLUMN);
        mainInvGrid.getFlexNode().setGap(0);
        for (int row = 0; row < 3; row++) {
            FlexContainer rowContainer = new FlexContainer(FlexDirection.ROW);
            rowContainer.getFlexNode().setGap(0);
            for (int col = 0; col < 9; col++) {
                int slotIndex = playerInvStartIndex + col + row * 9;
                if (slotIndex < menu.slots.size()) {
                    rowContainer.addChild(new SlotWidget(menu.slots.get(slotIndex)));
                }
            }
            mainInvGrid.addChild(rowContainer);
        }
        this.addChild(mainInvGrid);

        // 1x9 Hotbar slots (exact 18px grid)
        FlexContainer hotbarGrid = new FlexContainer(FlexDirection.ROW);
        hotbarGrid.getFlexNode().setGap(0);
        hotbarGrid.getFlexNode().setMargin(FlexInsets.of(4, 0, 0, 0));
        for (int col = 0; col < 9; col++) {
            int slotIndex = playerInvStartIndex + 27 + col;
            if (slotIndex < menu.slots.size()) {
                hotbarGrid.addChild(new SlotWidget(menu.slots.get(slotIndex)));
            }
        }
        this.addChild(hotbarGrid);
    }
}
