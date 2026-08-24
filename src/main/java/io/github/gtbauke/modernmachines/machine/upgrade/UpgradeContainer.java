package io.github.gtbauke.modernmachines.machine.upgrade;

import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradeItem;
import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradableMachine;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class UpgradeContainer extends SimpleContainer {
    public static final int UPGRADE_SLOTS_COUNT = 4;
    private final IUpgradableMachine machine;

    public UpgradeContainer(IUpgradableMachine machine) {
        super(UPGRADE_SLOTS_COUNT);
        this.machine = machine;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.getItem() instanceof IUpgradeItem;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return UpgradeItem.MAX_UPGRADE_STACK;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        recalculateUpgrades();
        if (machine != null) {
            machine.onUpgradesChanged();
        }
    }

    public void recalculateUpgrades() {
        if (machine == null) {
            return;
        }

        var stats = machine.getMachineStats();
        stats.clearModifiers();

        for (int i = 0; i < getContainerSize(); i++) {
            var stack = getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeItem upgradeItem) {
                upgradeItem.applyUpgrade(stats, stack, stack.getCount());
            }
        }
    }
}
