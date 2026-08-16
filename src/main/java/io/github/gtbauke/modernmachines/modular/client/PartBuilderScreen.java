package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.ProgressBarWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.client.gui.window.ModularContainerScreen;
import io.github.gtbauke.modernmachines.client.gui.window.SideTabWidget;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartBuilderScreen extends ModularContainerScreen<PartBuilderMenu> {

    public PartBuilderScreen(PartBuilderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected UiWidget buildContent() {
        // Setup side tab
        SideTabWidget infoTab = new SideTabWidget(Component.literal("Pattern Info"), 16, 96, false);
        infoTab.getContentContainer().addChild(
            Column.of(
                LabelWidget.of("Part Building").color(0xFFFFAA00),
                LabelWidget.of("Place pattern + material to form tool parts.").color(0xFFAAAAAA)
            ).gap(4)
        );
        addSideTab(infoTab);

        return Column.of(
            // 1. Crafting Processing Area
            Row.of(
                // Pattern Column
                Column.of(
                    SlotWidget.of(this.menu.slots.get(0)),
                    LabelWidget.of("Pattern").style(ChatFormatting.DARK_GRAY).centered().shadow(false).size(36, 8)
                ).gap(2).center(),

                // Plus Symbol
                LabelWidget.of("+").style(ChatFormatting.DARK_GRAY).centered().shadow(false).size(8, 10),

                // Material Column
                Column.of(
                    SlotWidget.of(this.menu.slots.get(1)),
                    LabelWidget.of("Material").style(ChatFormatting.DARK_GRAY).centered().shadow(false).size(38, 8)
                ).gap(2).center(),

                // Arrow Indicator
                ProgressBarWidget.arrow(() -> 1.0),

                // Output Result Column
                Column.of(
                    SlotWidget.of(this.menu.slots.get(2)),
                    LabelWidget.of("Result").style(ChatFormatting.DARK_GRAY).centered().shadow(false).size(32, 8)
                ).gap(2).center()
            ).gap(6).center().size(162, 52),

            // 2. Player Inventory
            playerInventory(3)
        ).center();
    }
}
