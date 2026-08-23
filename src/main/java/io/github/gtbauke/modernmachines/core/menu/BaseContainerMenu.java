package io.github.gtbauke.modernmachines.core.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainerMenu extends AbstractContainerMenu {
    protected final int containerSlotCount;
    protected final int playerInventoryStart;
    protected final int playerInventoryEnd;
    protected final int hotbarStart;

    public BaseContainerMenu(@Nullable MenuType<?> menuType, int containerId, int containerSlotCount) {
        super(menuType, containerId);
        this.containerSlotCount = containerSlotCount;
        this.playerInventoryStart = containerSlotCount;
        this.playerInventoryEnd = containerSlotCount + 36;
        this.hotbarStart = containerSlotCount + 27;
    }

    /**
     * Adds the standard 36-slot player inventory (27 main storage slots + 9 hotbar slots).
     *
     * @param playerInventory The player's inventory
     * @param startX Left margin (standard 8)
     * @param startY Top position for 3x9 inventory grid (standard 84)
     */
    protected void addPlayerInventoryAndHotbar(Inventory playerInventory, int startX, int startY) {
        // Player Main Inventory (3 rows x 9 columns)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // Hotbar (1 row x 9 columns)
        int hotbarY = startY + 58;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, hotbarY));
        }
    }

    /**
     * Adds the player inventory at standard vanilla positions (x=8, y=84, hotbar at y=142).
     */
    protected void addStandardPlayerInventory(Inventory playerInventory) {
        addPlayerInventoryAndHotbar(playerInventory, 8, 84);
    }

    public int getContainerSlotCount() {
        return containerSlotCount;
    }

    public int getPlayerInventoryStart() {
        return playerInventoryStart;
    }

    public int getPlayerInventoryEnd() {
        return playerInventoryEnd;
    }

    public int getHotbarStart() {
        return hotbarStart;
    }

    /**
     * Standard Quick Move helper that transfers items between main player inventory and hotbar.
     */
    protected boolean moveBetweenInventoryAndHotbar(ItemStack stack, int slotIndex) {
        if (slotIndex >= playerInventoryStart && slotIndex < hotbarStart) {
            return !this.moveItemStackTo(stack, hotbarStart, playerInventoryEnd, false);
        } else if (slotIndex >= hotbarStart && slotIndex < playerInventoryEnd) {
            return !this.moveItemStackTo(stack, playerInventoryStart, hotbarStart, false);
        }

        return true;
    }

    /**
     * Checks if the menu is still valid using a block type and level access.
     */
    protected boolean isStillValid(ContainerLevelAccess access, Player player, Block validBlock) {
        return stillValid(access, player, validBlock);
    }

    /**
     * Checks if the menu is still valid using a Container object.
     */
    protected boolean isStillValid(Container container, Player player) {
        return container.stillValid(player);
    }
}
