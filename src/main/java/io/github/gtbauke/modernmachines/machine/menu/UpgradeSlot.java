package io.github.gtbauke.modernmachines.machine.menu;

import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradeItem;
import io.github.gtbauke.modernmachines.machine.upgrade.UpgradeItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UpgradeSlot extends Slot {
    public UpgradeSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof IUpgradeItem;
    }

    @Override
    public int getMaxStackSize() {
        return UpgradeItem.MAX_UPGRADE_STACK;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return UpgradeItem.MAX_UPGRADE_STACK;
    }
}
