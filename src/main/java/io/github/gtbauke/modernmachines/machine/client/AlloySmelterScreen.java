package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.client.gui.declarative.BurnIndicator;
import io.github.gtbauke.modernmachines.client.gui.declarative.Card;
import io.github.gtbauke.modernmachines.client.gui.declarative.Center;
import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.ProgressBar;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.declarative.Visibility;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.tab.SideConfigContentWidget;
import io.github.gtbauke.modernmachines.client.gui.tab.UpgradeContentWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
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
    protected UiWidget buildContent() {
        // 1. Upgrades Floating Tab Window (Auto-calculated height)
        addFloatingTab(
                Component.literal("Machine Upgrades"),
                0, 96, 160,
                window -> new UpgradeContentWidget(this.menu)
        );

        // 2. Side Configuration Floating Tab Window (Interactive 2D Face Net, Auto-calculated height)
        io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable sideConfigurable = this.menu.getSideConfigurable();
        if (sideConfigurable != null) {
            addFloatingTab(
                    Component.literal("Side Configuration"),
                    32, 96, 155,
                    window -> new SideConfigContentWidget(sideConfigurable)
            );
        }

        // 3. Smelter Guide Floating Tab Window (Auto-calculated height)
        addFloatingTab(
                Component.literal("Smelter Guide"),
                64, 96, 165,
                window -> Card.of(
                        Column.of(
                                LabelWidget.of("Basic Smelter").color(0xFFFFAA00).centered(),
                                Divider.horizontal(),
                                LabelWidget.of("Multiblock Structure:").color(0xFFAAAAAA).centered(),
                                LabelWidget.of("• Top: Controller\n• Bottom: Heater").color(0xFF00AAAA).centered(),
                                LabelWidget.of("Burns solid fuel to smelt alloys.").color(0xFFAAAAAA).centered()
                        ).gap(4).center()
                ).padding(FlexInsets.of(6, 8, 6, 8)).matchParentWidth()
        );

        return Column.of(
            // 1. Structure Status Warning Banner (Auto-hidden dynamically when formed)
            Visibility.of(() -> !this.menu.isFormed(),
                Center.of(LabelWidget.of("⚠ Place Heater Below!").style(ChatFormatting.RED, ChatFormatting.BOLD)).size(162, 10)
            ),

            // 2. Machine Processing Area
            Row.of(
                // 2-Inputs, Flame, and Fuel Column (Classic Vertical Furnace Stack)
                Column.of(
                    // Top: 2 Input Slots
                    Row.of(SlotWidget.of(this.menu.slots.get(0)), SlotWidget.of(this.menu.slots.get(1))).gap(2).center(),

                    // Middle: Official Minecraft Burn Indicator (13x13)
                    BurnIndicator.of(
                        () -> this.menu.isLit() ? (this.menu.getBurnProgressScaled(100) / 100.0) : 0.0,
                        () -> this.menu.isLit()
                    ),

                    // Bottom: Fuel Slot
                    SlotWidget.of(this.menu.slots.get(2))
                ).gap(2).center(),

                // Smooth Progress Arrow (22x15)
                Center.of(
                    ProgressBar.arrow(() -> this.menu.getProgressScaled(100) / 100.0)
                ).size(30, 60),

                // Output Slot (Slot 3)
                Center.of(
                    SlotWidget.of(this.menu.slots.get(3))
                ).size(26, 60)
            ).gap(8).center(),

            // 3. Player Inventory & Hotbar (36-slot standard inventory)
            playerInventory(8)
        ).gap(4).center();
    }
}
