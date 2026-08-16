package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.declarative.Stack;
import io.github.gtbauke.modernmachines.client.gui.declarative.Visibility;
import io.github.gtbauke.modernmachines.client.gui.widget.IconButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.ProgressBarWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.client.gui.window.ModularContainerScreen;
import io.github.gtbauke.modernmachines.client.gui.window.SideTabWidget;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TinkeringTableScreen extends ModularContainerScreen<TinkeringTableMenu> {

    public TinkeringTableScreen(TinkeringTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected UiWidget buildContent() {
        // Setup side tab
        SideTabWidget codexTab = new SideTabWidget(Component.literal("Tinkering Codex"), 0, 96, false);
        codexTab.getContentContainer().addChild(
            Column.of(
                LabelWidget.of("Tinkering Guide").color(0xFFFFAA00),
                LabelWidget.of("Combine parts to assemble or upgrade modular tools.").color(0xFFAAAAAA)
            ).gap(4)
        );
        addSideTab(codexTab);

        IconButtonWidget assembleBtn = new IconButtonWidget(0, 96, 16, btn -> this.menu.setActiveTab(0));
        assembleBtn.addTooltip(Component.literal("Assemble Tool"));

        IconButtonWidget upgradeBtn = new IconButtonWidget(16, 96, 16, btn -> this.menu.setActiveTab(1));
        upgradeBtn.addTooltip(Component.literal("Modify / Upgrade"));

        IconButtonWidget repairBtn = new IconButtonWidget(32, 96, 16, btn -> this.menu.setActiveTab(2));
        repairBtn.addTooltip(Component.literal("Repair Tool"));

        return Column.of(
            // 1. Top Mode Selector Bar
            Row.of(assembleBtn, upgradeBtn, repairBtn).gap(4).center(),

            // 2. Mode-Based Dynamic Workspace (Stacked with reactive Visibility)
            Stack.of(
                // Tab 0: Assembly Area (Head, Handle, Binding, Attachment -> Result)
                Visibility.of(() -> this.menu.getActiveTab() == 0,
                    Row.of(
                        SlotWidget.of(this.menu.slots.get(0)),
                        SlotWidget.of(this.menu.slots.get(1)),
                        SlotWidget.of(this.menu.slots.get(2)),
                        SlotWidget.of(this.menu.slots.get(3)),
                        ProgressBarWidget.arrow(() -> 1.0),
                        SlotWidget.of(this.menu.slots.get(4))
                    ).gap(4).center()
                ),

                // Tab 1: Upgrade Area (Tool, Mod 1, Mod 2 -> Result)
                Visibility.of(() -> this.menu.getActiveTab() == 1,
                    Row.of(
                        SlotWidget.of(this.menu.slots.get(0)),
                        SlotWidget.of(this.menu.slots.get(1)),
                        SlotWidget.of(this.menu.slots.get(2)),
                        ProgressBarWidget.arrow(() -> 1.0),
                        SlotWidget.of(this.menu.slots.get(4))
                    ).gap(6).center()
                ),

                // Tab 2: Repair Area (Tool, Material -> Result)
                Visibility.of(() -> this.menu.getActiveTab() == 2,
                    Row.of(
                        SlotWidget.of(this.menu.slots.get(0)),
                        SlotWidget.of(this.menu.slots.get(1)),
                        ProgressBarWidget.arrow(() -> 1.0),
                        SlotWidget.of(this.menu.slots.get(4))
                    ).gap(8).center()
                )
            ).size(162, 52),

            // 3. Player Inventory
            playerInventory(5)
        ).center();
    }
}
