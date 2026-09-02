package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.*;
import io.github.gtbauke.modernmachines.api.client.gui.elements.*;
import io.github.gtbauke.modernmachines.api.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;

public class TinkeringTableScreen extends ModularContainerScreen<TinkeringTableMenu> {
    private static final int IMAGE_HEIGHT = 204;

    private final TinkeringTableMenu.ActiveTool[] TOOL_TABS = TinkeringTableMenu.ActiveTool.values();

    public TinkeringTableScreen(TinkeringTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public UIElement buildContent() {
        if (this.menu.slots.size() < 5) {
            return null;
        }

        int activeTab = this.menu.getActiveTab();
        UIElement craftingSection;

        if (activeTab == 0) {
            var headSlot = new SlotElement(this.menu.slots.get(0)).withGhostIcon(() -> this.menu.getActiveTool().getHeadPattern());
            var handleSlot = new SlotElement(this.menu.slots.get(1)).withGhostIcon(ModItems.HANDLE_PATTERN.get());
            var bindingSlot = new SlotElement(this.menu.slots.get(2))
                    .withGhostIcon(() -> this.menu.getActiveTool().getBindingPattern());

            var attachSlot = new SlotElement(this.menu.slots.get(3))
                    .withGhostIcon(() -> this.menu.getActiveTool().getAttachmentPattern());

            var resultSlot = new SlotElement(this.menu.slots.get(4)).withGhostIcon(() -> this.menu.getActiveTool().getItem());

            var toolButtons = Arrays.stream(TOOL_TABS).map(
                    tool -> (UIElement) new ButtonElement(new Size(16, 16),
                            () -> null,
                            () -> this.menu.getActiveTool() == tool,
                            () -> {
                                this.menu.setActiveTool(tool);
                            },
                            () -> new IconElement(tool.getIcon())
                    )
            ).toArray(UIElement[]::new);

            var toolSelectionRow = Row.of(4, toolButtons)
                    .setAlignItems(AlignItems.CENTER)
                    .setJustifyContent(JustifyContent.CENTER)
                    .setPadding(new Padding(4));

            var middleRow = Row.of(4, AlignItems.CENTER, bindingSlot, attachSlot);
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

        return OrePanel.background()
                .setSize(new Size(this.imageWidth, IMAGE_HEIGHT))
                .addChild(
                        Column.of(0, AlignItems.CENTER,
                                Row.of(0, AlignItems.START, LabelElement.title(this.title))
                                        .setSize(new Size(162, 12)),
                                craftingSection,
                                Spacer.vertical(8),
                                new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())
                        )
                );
    }
}
