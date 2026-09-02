package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.Column;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.Row;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.Spacer;
import io.github.gtbauke.modernmachines.api.client.gui.elements.LabelElement;
import io.github.gtbauke.modernmachines.api.client.gui.elements.OrePanel;
import io.github.gtbauke.modernmachines.api.client.gui.elements.PlayerInventoryElement;
import io.github.gtbauke.modernmachines.api.client.gui.elements.ProgressBarElement;
import io.github.gtbauke.modernmachines.api.client.gui.elements.SlotElement;
import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;
import io.github.gtbauke.modernmachines.api.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartBuilderScreen extends ModularContainerScreen<PartBuilderMenu> {

    public PartBuilderScreen(PartBuilderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public UIElement buildContent() {
        var patternSlot = new SlotElement(this.menu.slots.get(0))
                .withGhostIcon(ModItems.HANDLE_PATTERN.get());
        var materialSlot = new SlotElement(this.menu.slots.get(1));
        var resultSlot = new SlotElement(this.menu.slots.get(2));

        var inputsColumn = Column.of(4, AlignItems.CENTER,
                patternSlot,
                materialSlot
        );

        var craftingRow = Row.of(12, AlignItems.CENTER, JustifyContent.CENTER,
                inputsColumn,
                ProgressBarElement.arrow(() -> 1.0),
                resultSlot
        );

        var root = OrePanel.background()
                .setSize(new Size(this.imageWidth, this.imageHeight))
                .addChild(
                        Column.of(0, AlignItems.CENTER,
                                Row.of(0, AlignItems.START, LabelElement.title(this.title))
                                        .setSize(new Size(162, 12)),
                                Spacer.vertical(6),
                                craftingRow,
                                Spacer.vertical(8),
                                new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
                        )
                );

        return root;
    }
}
