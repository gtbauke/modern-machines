package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.client.gui.core.element.BurningElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Column;
import io.github.gtbauke.modernmachines.client.gui.core.element.PlayerInventoryElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.ProgressBarElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Row;
import io.github.gtbauke.modernmachines.client.gui.core.element.SideTabElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.SlotElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Spacer;
import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AlloySmelterScreen extends ModularContainerScreen<AlloySmelterMenu> {
    public static final int VANILLA_BG = 0xFFC6C6C6;
    public static final int VANILLA_BORDER = 0xFF373737;

    public AlloySmelterScreen(AlloySmelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected UIElement buildContent() {
        if (this.menu.slots.size() < 4) {
            return null;
        }

        // Left crafting column: Dual inputs -> Flame burn indicator -> Fuel slot
        Column inputAndFuelColumn = Column.of(2, AlignItems.CENTER,
            Row.of(2,
                new SlotElement(this.menu.slots.get(0)),
                new SlotElement(this.menu.slots.get(1))
            ),
            BurningElement.flame(() -> (double) this.menu.getBurnProgressScaled(100) / 100.0),
            new SlotElement(this.menu.slots.get(2))
        );

        // Crafting section Row: [Inputs+Flame+Fuel] -> [Progress Arrow] -> [Output Slot]
        Row craftingSection = Row.of(12, AlignItems.CENTER,
            inputAndFuelColumn,
            ProgressBarElement.arrow(() -> (double) this.menu.getProgressScaled(100) / 100.0),
            new SlotElement(this.menu.slots.get(3))
        );

        // Top-level Column covering the full window with vanilla-styled background
        Column root = Column.of(0, AlignItems.CENTER,
            // Top margin spacer
            Spacer.vertical(17),

            // Machine Crafting Section
            craftingSection,

            // Vertical spacing between crafting section and player inventory (17 + 53 + 14 = 84px)
            Spacer.vertical(14),

            // Player Inventory & Hotbar (36 slots, perfectly centered)
            new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
        );

        root.setSize(new Size(this.imageWidth, this.imageHeight));
        root.setBackground(VANILLA_BG, VANILLA_BORDER);

        return root;
    }

    @Override
    protected void initWindows() {
        if (this.menu.slots.size() >= 8) {
            int winWidth = 80;
            int winHeight = 72;

            // 2x2 Upgrade Slots Grid (40x40 px)
            Column upgradeGrid = Column.of(4, AlignItems.CENTER,
                Row.of(4,
                    new SlotElement(this.menu.slots.get(4)),
                    new SlotElement(this.menu.slots.get(5))
                ),
                Row.of(4,
                    new SlotElement(this.menu.slots.get(6)),
                    new SlotElement(this.menu.slots.get(7))
                )
            );

            // Centered content column filling window body
            Column winContent = Column.of(0, AlignItems.CENTER,
                Spacer.vertical(4),
                upgradeGrid
            );
            winContent.setSize(new Size(winWidth, winHeight - 18));

            // Floating Draggable Upgrade Window (80 x 72 px, positioned on left of main screen)
            Window upgradeWindow = new Window(
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
            upgradeWindow.setVisible(false); // Closed initially
            upgradeWindow.setContent(winContent);

            this.windowManager.addWindow(upgradeWindow);

            // Left-sided Tab docked at the very top of the main window (0 gap)
            SideTabElement tab = new SideTabElement(
                this.mainWindow,
                upgradeWindow,
                new ItemStack(ModItems.SPEED_UPGRADE.get()),
                Component.literal("Upgrades"),
                true // left-sided
            );
            tab.updateDockedPosition(0); // 0 gap at top
            this.mainWindow.addChild(tab);
        }
    }
}
