package io.github.gtbauke.modernmachines.api.machine.upgrade;

import java.util.List;

import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IUpgradeItem {
    /**
     * Apply modifiers to the machine stats based on the installed item stack and count.
     */
    void applyUpgrade(MachineStats stats, ItemStack stack, int count);

    /**
     * Tooltip lines describing the upgrade effects.
     */
    List<Component> getUpgradeTooltips(ItemStack stack);
}
