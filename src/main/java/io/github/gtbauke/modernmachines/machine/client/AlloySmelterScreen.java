package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.*;
import io.github.gtbauke.modernmachines.api.client.gui.elements.*;
import io.github.gtbauke.modernmachines.api.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AlloySmelterScreen extends ModularContainerScreen<AlloySmelterMenu> {
    public AlloySmelterScreen(AlloySmelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 176);
    }

    @Override
    public UIElement buildContent() {
        var root = Column.of(0, AlignItems.CENTER,
                Spacer.vertical(16),
                Row.of(2, AlignItems.CENTER,
                        Column.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                Row.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                        new SlotElement(this.menu.slots.get(0)),
                                        new SlotElement(this.menu.slots.get(1))
                                ),
                                Row.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                        BurningElement.flame(() -> (double) this.menu.getBurnProgressScaled(100) / 100.0)
                                ),
                                Row.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                        new SlotElement(this.menu.slots.get(2))
                                )
                        ).setFlowWeight(1),
                        Column.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                ProgressBarElement.arrow(() -> (double) this.menu.getProgressScaled(100) / 100.0)
                        ).setFlowWeight(1),
                        Column.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                new SlotElement(this.menu.slots.get(3))
                        ).setFlowWeight(1)
                ).setSize(new Size(162, 68)),
                new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
        );

        root.setSize(new Size(this.imageWidth, this.imageHeight));

        return root;
    }
}
