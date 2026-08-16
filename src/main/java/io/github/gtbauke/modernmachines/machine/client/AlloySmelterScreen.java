package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.client.gui.declarative.BurnIndicator;
import io.github.gtbauke.modernmachines.client.gui.declarative.Card;
import io.github.gtbauke.modernmachines.client.gui.declarative.Center;
import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.FloatingTab;
import io.github.gtbauke.modernmachines.client.gui.declarative.GhostIcons;
import io.github.gtbauke.modernmachines.client.gui.declarative.MainWindow;
import io.github.gtbauke.modernmachines.client.gui.declarative.PlayerInventory;
import io.github.gtbauke.modernmachines.client.gui.declarative.ProgressBar;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.declarative.Scaffold;
import io.github.gtbauke.modernmachines.client.gui.declarative.SlotRow;
import io.github.gtbauke.modernmachines.client.gui.declarative.Tabs;
import io.github.gtbauke.modernmachines.client.gui.declarative.Visibility;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.tab.SideConfigContentWidget;
import io.github.gtbauke.modernmachines.client.gui.tab.UpgradeContentWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.window.ModularContainerScreen;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AlloySmelterScreen extends ModularContainerScreen<AlloySmelterMenu> {
    public AlloySmelterScreen(AlloySmelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected Scaffold buildScaffold() {
        return Scaffold.of(
            // 1. Floating Tab Windows (Zero pixel dimensions)
            Tabs.of(
                FloatingTab.of("Machine Upgrades", 0, 96, () -> new UpgradeContentWidget(this.menu)),
                FloatingTab.of("Side Configuration", 32, 96, () -> new SideConfigContentWidget(this.menu.getSideConfigurable())),
                FloatingTab.of("Smelter Guide", 64, 96, () -> Card.of(
                    Column.of(
                        LabelWidget.of("Basic Smelter").color(0xFFFFAA00).centered(),
                        Divider.horizontal(),
                        LabelWidget.of("Multiblock Structure:").color(0xFFAAAAAA).centered(),
                        LabelWidget.of("• Top: Controller\n• Bottom: Heater").color(0xFF00AAAA).centered(),
                        LabelWidget.of("Burns solid fuel to smelt alloys.").color(0xFFAAAAAA).centered()
                    ).gap(4).center()
                ).padding(FlexInsets.of(6, 8, 6, 8)))
            ),

            // 2. Main Window Machine Body
            MainWindow.of(
                // Dynamic Multiblock Status Warning Banner
                Visibility.of(() -> !this.menu.isFormed(),
                    Center.of(LabelWidget.of("⚠ Place Heater Below!").style(ChatFormatting.RED, ChatFormatting.BOLD)).size(162, 10)
                ),

                // Machine Processing Area
                Row.of(
                    // Inputs + Flame + Fuel Column
                    Column.of(
                        SlotRow.of(
                            SlotWidget.of(this.menu.slots.get(0)).ghostIcon(GhostIcons.INGOT),
                            SlotWidget.of(this.menu.slots.get(1)).ghostIcon(GhostIcons.INGOT)
                        ).gap(2).center(),

                        BurnIndicator.of(
                            () -> this.menu.isLit() ? (this.menu.getBurnProgressScaled(100) / 100.0) : 0.0,
                                this.menu::isLit
                        ),

                        SlotWidget.of(this.menu.slots.get(2)).ghostIcon(GhostIcons.SOLID_FUEL)
                    ).gap(2).center(),

                    // Progress Arrow (22x15)
                    Center.of(
                        ProgressBar.arrow(() -> this.menu.getProgressScaled(100) / 100.0)
                    ).size(30, 60),

                    // Output Slot
                    Center.of(
                        SlotWidget.of(this.menu.slots.get(3))
                    ).size(26, 60)
                ).gap(8).center(),

                // 3. Automated Player Inventory (36-slot standard inventory + hotbar)
                PlayerInventory.of(this.menu)
            )
        );
    }
}
