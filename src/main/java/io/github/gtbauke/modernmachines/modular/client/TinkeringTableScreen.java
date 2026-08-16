package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TinkeringTableScreen extends ModularContainerScreen<TinkeringTableMenu> {

    public TinkeringTableScreen(TinkeringTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
