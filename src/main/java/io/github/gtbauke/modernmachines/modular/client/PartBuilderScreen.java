package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.core.element.Column;
import io.github.gtbauke.modernmachines.client.gui.core.element.PlayerInventoryElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.ProgressBarElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Row;
import io.github.gtbauke.modernmachines.client.gui.core.element.SlotElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Spacer;
import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartBuilderScreen extends ModularContainerScreen<PartBuilderMenu> {
    public static final int VANILLA_BG = 0xFFC6C6C6;
    public static final int VANILLA_BORDER = 0xFF373737;

    public PartBuilderScreen(PartBuilderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected UIElement buildContent() {
        if (this.menu.slots.size() < 3) {
            return null;
        }

        // Crafting section: [Pattern Slot] + [Material Slot] -> [Arrow] -> [Output Part Slot]
        Row craftingSection = Row.of(12, AlignItems.CENTER,
                Row.of(4, AlignItems.CENTER,
                        new SlotElement(this.menu.slots.get(0)),
                        new SlotElement(this.menu.slots.get(1))
                ),
                ProgressBarElement.arrow(() -> 1.0),
                new SlotElement(this.menu.slots.get(2))
        );

        // Root container with player inventory
        Column root = Column.of(0, AlignItems.CENTER,
                Spacer.vertical(20),
                craftingSection,
                Spacer.vertical(20),
                new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
        );

        root.setSize(new Size(this.imageWidth, this.imageHeight));
        root.setBackground(VANILLA_BG, VANILLA_BORDER);

        return root;
    }
}
