package io.github.gtbauke.modernmachines.api.machine.side;

import java.util.Set;
import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface ISideConfigurable {
    MachineSideConfig getSideConfig();

    Set<MachineCapabilityType> getSupportedCapabilities();

    Direction getMachineFacing();

    BlockPos getMachinePos();

    void onSideConfigChanged();
}
