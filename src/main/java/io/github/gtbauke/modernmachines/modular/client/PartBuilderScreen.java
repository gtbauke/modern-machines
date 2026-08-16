package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartBuilderScreen extends ModularContainerScreen<PartBuilderMenu> {

    public PartBuilderScreen(PartBuilderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
