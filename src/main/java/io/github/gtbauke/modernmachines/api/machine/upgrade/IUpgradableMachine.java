package io.github.gtbauke.modernmachines.api.machine.upgrade;

import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import io.github.gtbauke.modernmachines.machine.upgrade.UpgradeContainer;

public interface IUpgradableMachine {
    /**
     * Get the machine's upgrade inventory container (typically 4 slots).
     */
    UpgradeContainer getUpgradeContainer();

    /**
     * Get the live machine stats container.
     */
    MachineStats getMachineStats();

    /**
     * Called when upgrades change to update machine behavior, sync data, or recalculate.
     */
    void onUpgradesChanged();
}
