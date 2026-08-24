package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.core.element.Column;
import io.github.gtbauke.modernmachines.client.gui.core.element.PlayerInventoryElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.ProgressBarElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Row;
import io.github.gtbauke.modernmachines.client.gui.core.element.SideTabElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.SlotElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Spacer;
import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class TinkeringTableScreen extends ModularContainerScreen<TinkeringTableMenu> {

    private UIElement currentContent;
    private final List<SideTabElement> modeTabs = new ArrayList<>();
    private TinkeringStatsWindow statsWindow;

    public TinkeringTableScreen(TinkeringTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected UIElement buildContent() {
        if (this.menu.slots.size() < 5) {
            return null;
        }

        int activeTab = this.menu.getActiveTab();
        UIElement craftingSection;

        if (activeTab == 0) {
            var headSlot = new SlotElement(this.menu.slots.get(0));
            var handleSlot = new SlotElement(this.menu.slots.get(1));
            var bindingSlot = new SlotElement(this.menu.slots.get(2));
            var attachSlot = new SlotElement(this.menu.slots.get(3));
            var resultSlot = new SlotElement(this.menu.slots.get(4));

            var middleRow = Row.of(2, AlignItems.CENTER, bindingSlot, attachSlot);
            var diamondGrid = Column.of(2, AlignItems.CENTER, headSlot, middleRow, handleSlot);

            craftingSection = Row.of(12, AlignItems.CENTER,
                    diamondGrid,
                    ProgressBarElement.arrow(() -> 1.0),
                    resultSlot
            );
        } else if (activeTab == 1) {
            var toolSlot = new SlotElement(this.menu.slots.get(0));
            var mod1Slot = new SlotElement(this.menu.slots.get(1));
            var mod2Slot = new SlotElement(this.menu.slots.get(2));
            var resultSlot = new SlotElement(this.menu.slots.get(4));

            var modInputs = Column.of(2, AlignItems.CENTER, mod1Slot, mod2Slot);

            craftingSection = Row.of(10, AlignItems.CENTER,
                    toolSlot,
                    modInputs,
                    ProgressBarElement.arrow(() -> 1.0),
                    resultSlot
            );
        } else {
            var toolSlot = new SlotElement(this.menu.slots.get(0));
            var repairSlot = new SlotElement(this.menu.slots.get(1));
            var resultSlot = new SlotElement(this.menu.slots.get(4));

            craftingSection = Row.of(12, AlignItems.CENTER,
                    toolSlot,
                    repairSlot,
                    ProgressBarElement.arrow(() -> 1.0),
                    resultSlot
            );
        }

        var root = Column.of(0, AlignItems.CENTER,
                Spacer.vertical(14),
                craftingSection,
                Spacer.vertical(12),
                new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
        );

        root.setSize(new Size(this.imageWidth, this.imageHeight));
        root.setBackground(io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper.ORE_BG_PRIMARY, io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper.ORE_BORDER_DARK);

        this.currentContent = root;
        return root;
    }

    @Override
    protected void initWindows() {
        modeTabs.clear();

        // 1. Assemble Tab (Hammer icon)
        SideTabElement assembleTab = new SideTabElement(
                this.mainWindow,
                new ItemStack(ModItems.ENGINEER_HAMMER.get()),
                Component.literal("Assemble Tool"),
                true
        );
        assembleTab.updateDockedPosition(0);
        assembleTab.setOnClick(() -> switchTab(0));
        this.mainWindow.addChild(assembleTab);
        modeTabs.add(assembleTab);

        // 2. Modify Tab (Upgrade icon)
        SideTabElement modifyTab = new SideTabElement(
                this.mainWindow,
                new ItemStack(ModItems.SPEED_UPGRADE.get()),
                Component.literal("Modify Tool"),
                true
        );
        modifyTab.updateDockedPosition(28);
        modifyTab.setOnClick(() -> switchTab(1));
        this.mainWindow.addChild(modifyTab);
        modeTabs.add(modifyTab);

        // 3. Repair Tab (Wire cutter / Anvil icon)
        SideTabElement repairTab = new SideTabElement(
                this.mainWindow,
                new ItemStack(ModItems.WIRE_CUTTER.get()),
                Component.literal("Repair Tool"),
                true
        );
        repairTab.updateDockedPosition(56);
        repairTab.setOnClick(() -> switchTab(2));
        this.mainWindow.addChild(repairTab);
        modeTabs.add(repairTab);

        // 4. Live Stats Drawer Window (Right side)
        Position statsPos = new Position(
                this.mainWindow.getPosition().x() + this.mainWindow.getSize().width() + 4,
                this.mainWindow.getPosition().y()
        );
        this.statsWindow = new TinkeringStatsWindow(this.menu, statsPos);
        this.windowManager.addWindow(this.statsWindow);

        // Stats Tab (Right side, tablet icon)
        SideTabElement statsTab = new SideTabElement(
                this.mainWindow,
                this.statsWindow,
                new ItemStack(ModItems.ENGINEERS_TABLET.get()),
                Component.literal("Tool Stats & Preview"),
                false
        );
        statsTab.updateDockedPosition(0);
        this.mainWindow.addChild(statsTab);

        updateTabHighlights();
    }

    public void switchTab(int newTab) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, newTab);
        }
        this.menu.setActiveTab(newTab);

        if (this.currentContent != null) {
            this.mainWindow.removeChild(this.currentContent);
        }

        UIElement newContent = buildContent();
        if (newContent != null) {
            this.mainWindow.addChild(newContent);
        }

        updateTabHighlights();

        this.windowManager.calculateSize();
        this.windowManager.calculateLayout();
    }

    private void updateTabHighlights() {
        int active = this.menu.getActiveTab();
        for (int i = 0; i < modeTabs.size(); i++) {
            modeTabs.get(i).setActive(i == active);
        }
    }
}
