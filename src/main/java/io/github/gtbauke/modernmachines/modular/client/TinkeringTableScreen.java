package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.core.element.*;
import io.github.gtbauke.modernmachines.client.gui.core.layout.*;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TinkeringTableScreen extends ModularContainerScreen<TinkeringTableMenu> {
    private UIElement currentContent;
    private final List<SideTabElement> modeTabs = new ArrayList<>();
    private TinkeringStatsWindow statsWindow;

    private final TinkeringTableMenu.ActiveTool[] TOOL_TABS = TinkeringTableMenu.ActiveTool.values();
    private final int SCREEN_WIDTH = 176;
    private final int SCREEN_HEIGHT = 204;

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
            var headSlot = new SlotElement(this.menu.slots.get(0)).withGhostIcon(() -> this.menu.getActiveTool().getHeadPattern());
            var handleSlot = new SlotElement(this.menu.slots.get(1)).withGhostIcon(ModItems.HANDLE_PATTERN.get());
            var bindingSlot = new SlotElement(this.menu.slots.get(2))
                    .withGhostIcon(() -> this.menu.getActiveTool().getBindingPattern())
                    .withVisibility(() -> this.menu.getActiveTool().requiresBinding());
            var attachSlot = new SlotElement(this.menu.slots.get(3))
                    .withGhostIcon(() -> this.menu.getActiveTool().getAttachmentPattern())
                    .withVisibility(() -> this.menu.getActiveTool().acceptsAttachment());
            var resultSlot = new SlotElement(this.menu.slots.get(4)).withGhostIcon(() -> this.menu.getActiveTool().getItem());

            var toolButtons = Arrays.stream(TOOL_TABS).map(
                    tool -> (UIElement) new ButtonElement(new Size(16, 16),
                            () -> null,
                            () -> this.menu.getActiveTool() == tool,
                            () -> {
                                this.menu.setActiveTool(tool);
                                this.windowManager.calculateSize();
                                this.windowManager.calculateLayout();
                            },
                            () -> new IconElement(tool.getIcon())
                    )
            ).toArray(UIElement[]::new);

            var toolSelectionRow = Row.of(4, toolButtons)
                    .setAlignItems(AlignItems.CENTER)
                    .setJustifyContent(JustifyContent.CENTER)
                    .setPadding(new Padding(4));

            var middleRow = Row.of(4, AlignItems.CENTER, bindingSlot, attachSlot)
                    .withVisibility(() -> this.menu.getActiveTool().requiresBinding() || this.menu.getActiveTool().acceptsAttachment());

            var toolCraftingGrid = Column.of(4, AlignItems.CENTER, headSlot, middleRow, handleSlot);

            craftingSection = Column.of(
                    toolSelectionRow,
                    Row.of(12, AlignItems.CENTER,
                            toolCraftingGrid,
                            ProgressBarElement.arrow(() -> 1.0),
                            resultSlot
                    )
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

        root.setSize(new Size(SCREEN_WIDTH, SCREEN_HEIGHT));
        root.setBackground(GUIRenderHelper.ORE_BG_PRIMARY, GUIRenderHelper.ORE_BORDER_DARK);

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

        hideAllSlots();

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
