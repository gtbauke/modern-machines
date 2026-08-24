package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.client.gui.core.element.BurningElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Column;
import io.github.gtbauke.modernmachines.client.gui.core.element.PlayerInventoryElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.ProgressBarElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Row;
import io.github.gtbauke.modernmachines.client.gui.core.element.SideTabElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.SlotElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Spacer;
import io.github.gtbauke.modernmachines.client.gui.core.layout.*;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.client.gui.windows.SideConfigWindow;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AlloySmelterScreen extends ModularContainerScreen<AlloySmelterMenu> {

    public AlloySmelterScreen(AlloySmelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 176);
    }

    @Override
    protected UIElement buildContent() {
        var root = Column.of(0, AlignItems.CENTER,
                Spacer.vertical(16),
                Row.of(2, AlignItems.CENTER,
                        Column.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                Row.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                        new SlotElement(this.menu.slots.get(0)),
                                        new SlotElement(this.menu.slots.get(1))
                                ).setFillParentWidth(true),
                                Row.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                        BurningElement.flame(() -> (double) this.menu.getBurnProgressScaled(100) / 100.0)
                                ).setFillParentWidth(true),
                                Row.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                        new SlotElement(this.menu.slots.get(2))
                                ).setFillParentWidth(true)
                        ).setFlowWeight(1).setFillParentHeight(true),
                        Column.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                ProgressBarElement.arrow(() -> (double) this.menu.getProgressScaled(100) / 100.0)
                        ).setFlowWeight(1).setFillParentHeight(true),
                        Column.of(2, AlignItems.CENTER, JustifyContent.CENTER,
                                new SlotElement(this.menu.slots.get(3))
                        ).setFlowWeight(1).setFillParentHeight(true)
                ).setSize(new Size(162, 68)),
                new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
        );

        root.setSize(new Size(this.imageWidth, this.imageHeight));

        return root;
    }

    @Override
    protected void initWindows() {
        // Upgrade Window & Tab
        if (this.menu.slots.size() >= 8) {
            int winWidth = 80;
            int winHeight = 72;
            var upgradeWindow = new Window(
                    Component.literal("Upgrades"),
                    new Bounds(
                            new Position(this.mainWindow.getPosition().x() - winWidth - 4, this.mainWindow.getPosition().y()),
                            new Size(winWidth, winHeight)
                    ),
                    new Padding(0)
            );
            upgradeWindow.setHasHeader(true);
            upgradeWindow.setHeaderHeight(18);
            upgradeWindow.setDraggable(true);
            upgradeWindow.setHasCloseButton(true);
            upgradeWindow.setVisible(false);

            var upgradeContent = Column.of(0, AlignItems.CENTER,
                    Spacer.vertical(4),
                    Column.of(4, AlignItems.CENTER,
                            Row.of(4,
                                    new SlotElement(this.menu.slots.get(4)),
                                    new SlotElement(this.menu.slots.get(5))
                            ),
                            Row.of(4,
                                    new SlotElement(this.menu.slots.get(6)),
                                    new SlotElement(this.menu.slots.get(7))
                            )
                    )
            );
            upgradeContent.setSize(new Size(winWidth, winHeight - 18));
            upgradeWindow.setContent(upgradeContent);

            this.windowManager.addWindow(upgradeWindow);

            var tab = new SideTabElement(
                    this.mainWindow,
                    upgradeWindow,
                    new ItemStack(ModItems.SPEED_UPGRADE.get()),
                    Component.literal("Upgrades"),
                    true
            );
            tab.updateDockedPosition(0);
            this.mainWindow.addChild(tab);
        }

        // Side Configuration Window & Tab
        if (this.menu.getSideConfigurable() != null) {
            var sideConfigPos = new Position(
                    this.mainWindow.getPosition().x() - SideConfigWindow.WINDOW_WIDTH - 4,
                    this.mainWindow.getPosition().y()
            );
            var sideConfigWindow = new SideConfigWindow(this.menu.getSideConfigurable(), sideConfigPos);
            this.windowManager.addWindow(sideConfigWindow);

            var sideConfigTab = new SideTabElement(
                    this.mainWindow,
                    sideConfigWindow,
                    new ItemStack(ModItems.ENGINEERS_TABLET.get()),
                    Component.literal("Side Configuration"),
                    true
            );
            sideConfigTab.updateDockedPosition(28);
            this.mainWindow.addChild(sideConfigTab);
        }
    }
}
