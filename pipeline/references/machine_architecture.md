# Machine Architecture Guide (Layer 3 Reference)

This document details the machine interfaces, capability management, side configuration, and upgrade mechanics in **Modern Machines**.

---

## 1. Core Machine Interfaces

### `IUpgradableMachine` (`io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradableMachine`)
Every processing machine implements `IUpgradableMachine` to support modular upgrade items:
- `MachineStats getBaseStats()`: Returns baseline stats (speed, energy buffer, transfer rate).
- `MachineStats getModifiedStats()`: Calculates effective stats after applying upgrade modifiers.
- `int getUpgradeSlotCount()`: Number of upgrade cards accepted (typically 4).
- `boolean canAcceptUpgrade(ItemStack stack, int slot)`: Validates if an upgrade fits.

### `ISideConfigurable` (`io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable`)
Allows players to customize IO behavior per face via the Engineer's Terminal or Side Config Window:
- `MachineSideConfig getSideConfig()`: Holds the 6 relative faces (`RelativeSide`: `FRONT`, `BACK`, `LEFT`, `RIGHT`, `TOP`, `BOTTOM`).
- `SideIoMode getIoMode(RelativeSide side, MachineCapabilityType type)`:
  - `NONE`: Disconnected / Blocked.
  - `INPUT`: Accepts items/fluids/energy.
  - `OUTPUT`: Auto-ejects or allows extraction.
  - `BOTH`: Bidirectional IO.
- `void cycleIoMode(RelativeSide side, MachineCapabilityType type)`: Cycles modes on player click.

---

## 2. BlockEntity Architecture & Ticking

A standard Modern Machines processing machine follows this structure:

```java
public class BaseMachineBlockEntity extends BlockEntity implements IUpgradableMachine, ISideConfigurable, MenuProvider {
    protected final CustomEnergyStorage energyStorage;
    protected final ItemStackHandler itemHandler;
    protected final MachineSideConfig sideConfig;
    protected int progress = 0;
    protected int maxProgress = 200;

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.energyStorage = new CustomEnergyStorage(50000, 160, 0);
        this.itemHandler = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.sideConfig = new MachineSideConfig();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaseMachineBlockEntity be) {
        if (be.canProcess()) {
            if (be.energyStorage.extractEnergy(be.getEnergyPerTick(), false) > 0) {
                be.progress++;
                if (be.progress >= be.maxProgress) {
                    be.completeProcess();
                    be.progress = 0;
                }
                be.setChanged();
            }
        } else if (be.progress > 0) {
            be.progress = Math.max(0, be.progress - 2);
            be.setChanged();
        }

        be.handleAutoOutput(level, pos);
    }
}
```

---

## 3. Machine Stats & Modifiers (`MachineStats`)
Machine attributes use `MachineStatType`:
- `SPEED`: Process speed multiplier ($1.0 = 100\%$).
- `ENERGY_USAGE`: FE consumed per tick ($1.0 = \text{base draw}$).
- `ENERGY_CAPACITY`: Internal energy storage capacity.
- `EFFICIENCY`: Energy reduction ratio.

Upgrades implement `IUpgradeItem` and provide a list of `MachineStatModifier` instances (Additive or Multiplicative).
