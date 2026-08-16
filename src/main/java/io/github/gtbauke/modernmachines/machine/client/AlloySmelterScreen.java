package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.client.gui.core.element.BurningElement;
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
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

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
}
