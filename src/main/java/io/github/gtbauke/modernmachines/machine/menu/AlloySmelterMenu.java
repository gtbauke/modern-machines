package io.github.gtbauke.modernmachines.machine.menu;

import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradeItem;
import io.github.gtbauke.modernmachines.core.menu.BaseContainerMenu;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.machine.blockentity.AlloySmelterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AlloySmelterMenu extends BaseContainerMenu {
    public static final int UPGRADE_SLOT_START = 4;
    public static final int UPGRADE_SLOT_COUNT = 4;

    private final Container container;
    private final Container upgradeContainer;
    private final ContainerData data;
    private ISideConfigurable sideConfigurable;

    public AlloySmelterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBlockPos());
    }

    public AlloySmelterMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, pos));
    }

    private AlloySmelterMenu(int containerId, Inventory playerInventory, AlloySmelterBlockEntity be) {
        this(containerId, playerInventory,
                be != null ? be : new SimpleContainer(4),
                be != null ? be.getUpgradeContainer() : new SimpleContainer(4),
                be != null ? be.getDataAccess() : new SimpleContainerData(7));
        this.sideConfigurable = be;
    }

    public AlloySmelterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(4), new SimpleContainer(4), new SimpleContainerData(7));
    }

    public AlloySmelterMenu(int containerId, Inventory playerInventory, Container container, Container upgradeContainer, ContainerData data) {
        super(ModMenuTypes.ALLOY_SMELTER.get(), containerId, 8);
        checkContainerSize(container, 4);
        checkContainerSize(upgradeContainer, 4);
        checkContainerDataCount(data, 7);

        this.container = container;
        this.upgradeContainer = upgradeContainer;
        this.data = data;
        if (container instanceof ISideConfigurable sc) {
            this.sideConfigurable = sc;
        }
        this.addDataSlots(data);

        // 2 Inputs (Slots 0, 1)
        this.addSlot(new Slot(container, 0, 52, 20));
        this.addSlot(new Slot(container, 1, 72, 20));

        // Fuel (Slot 2)
        this.addSlot(new Slot(container, 2, 62, 50));

        // Output (Slot 3 - Strictly Read-Only)
        this.addSlot(new Slot(container, 3, 130, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 4 Upgrade Slots (Slots 4, 5, 6, 7)
        for (int i = 0; i < UPGRADE_SLOT_COUNT; i++) {
            this.addSlot(new UpgradeSlot(upgradeContainer, i, 185 + (i % 2) * 18, 20 + (i / 2) * 18));
        }

        // Add 36-slot player inventory & hotbar (Slots 8..43)
        addStandardPlayerInventory(playerInventory);
    }

    private static AlloySmelterBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        if (pos != null && playerInventory.player != null && playerInventory.player.level() != null) {
            if (playerInventory.player.level().getBlockEntity(pos) instanceof AlloySmelterBlockEntity be) {
                return be;
            }
        }
        return null;
    }

    public ISideConfigurable getSideConfigurable() {
        return this.sideConfigurable;
    }

    public Container getContainer() {
        return this.container;
    }

    public int getProgressScaled(int pixels) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 && progress != 0 ? progress * pixels / maxProgress : 0;
    }

    public int getBurnProgressScaled(int pixels) {
        int litTime = this.data.get(2);
        int litDuration = this.data.get(3);
        if (litDuration == 0) litDuration = 200;
        return litTime * pixels / litDuration;
    }

    public boolean isFormed() {
        return this.data.get(4) == 1;
    }

    public boolean isLit() {
        return this.data.get(2) > 0;
    }

    public int getSpeedMultiplierPercent() {
        int val = this.data.get(5);
        return val > 0 ? val : 100;
    }

    public int getEfficiencyMultiplierPercent() {
        int val = this.data.get(6);
        return val > 0 ? val : 100;
    }

    public Container getUpgradeContainer() {
        return upgradeContainer;
    }

    @Override
    public boolean stillValid(Player player) {
        return isStillValid(this.container, player) && isStillValid(this.upgradeContainer, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 3) { // Output slot -> move to player inventory (hotbar first)
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= 0 && index < containerSlotCount) { // Machine or Upgrade slots (0..7) -> player inventory
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // Player inventory / hotbar (8..43)
                // 1. If it's an upgrade item -> try to move to upgrade slots (4..7)
                if (stackInSlot.getItem() instanceof IUpgradeItem) {
                    if (!this.moveItemStackTo(stackInSlot, 4, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 2. Try to move into machine inputs/fuel (0..3)
                else if (!this.moveItemStackTo(stackInSlot, 0, 3, false)) {
                    // 3. Transfer between main inventory and hotbar
                    if (!moveBetweenInventoryAndHotbar(stackInSlot, index)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return itemstack;
    }
}
