package io.github.gtbauke.modernmachines.machine.blockentity;

import java.util.EnumSet;
import java.util.Set;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.MachineSideConfig;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradableMachine;
import io.github.gtbauke.modernmachines.machine.upgrade.UpgradeContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public abstract class BaseMachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, IUpgradableMachine, ISideConfigurable {
    protected final MachineStats stats = new MachineStats();
    protected final UpgradeContainer upgradeContainer = new UpgradeContainer(this);
    protected final MachineSideConfig sideConfig = new MachineSideConfig();

    protected int transferCooldown = 0;

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public MachineStats getMachineStats() {
        return stats;
    }

    @Override
    public UpgradeContainer getUpgradeContainer() {
        return upgradeContainer;
    }

    @Override
    public MachineSideConfig getSideConfig() {
        return sideConfig;
    }

    @Override
    public Set<MachineCapabilityType> getSupportedCapabilities() {
        return EnumSet.of(MachineCapabilityType.ITEM);
    }

    @Override
    public Direction getMachineFacing() {
        if (this.getBlockState().hasProperty(HorizontalDirectionalBlock.FACING)) {
            return this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        }
        return Direction.NORTH;
    }

    @Override
    public BlockPos getMachinePos() {
        return this.worldPosition;
    }

    @Override
    public void onSideConfigChanged() {
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onUpgradesChanged() {
        setChanged();
    }

    /**
     * Executes universal auto-ejection and auto-pulling for items every 4 ticks.
     */
    protected void tickAutoTransfer(Level level, BlockPos pos, BlockState state) {
        transferCooldown++;
        if (transferCooldown < 4) return;
        transferCooldown = 0;

        if (level.isClientSide()) return;

        Direction facing = getMachineFacing();

        // 1. Auto-Eject Items
        if (sideConfig.isAutoEject(MachineCapabilityType.ITEM)) {
            for (RelativeSide relSide : RelativeSide.values()) {
                SideIoMode mode = sideConfig.getMode(MachineCapabilityType.ITEM, relSide);
                if (mode.allowsOutput()) {
                    Direction worldDir = relSide.toAbsolute(facing);
                    int[] outputSlots = getSlotsForFace(worldDir);
                    if (outputSlots.length == 0) continue;

                    BlockPos targetPos = pos.relative(worldDir);
                    ResourceHandler<ItemResource> targetHandler = level.getCapability(Capabilities.Item.BLOCK, targetPos, worldDir.getOpposite());
                    if (targetHandler != null) {
                        for (int slot : outputSlots) {
                            ItemStack inSlot = getItem(slot);
                            if (!inSlot.isEmpty() && canTakeItemThroughFace(slot, inSlot, worldDir)) {
                                ItemStack toTransfer = inSlot.copyWithCount(Math.min(inSlot.getCount(), 8));
                                ItemStack remainder = ItemUtil.insertItemReturnRemaining(targetHandler, toTransfer, false, null);
                                int transferred = toTransfer.getCount() - remainder.getCount();
                                if (transferred > 0) {
                                    inSlot.shrink(transferred);
                                    if (inSlot.isEmpty()) {
                                        setItem(slot, ItemStack.EMPTY);
                                    }
                                    setChanged();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Auto-Pull Items
        if (sideConfig.isAutoPull(MachineCapabilityType.ITEM)) {
            for (RelativeSide relSide : RelativeSide.values()) {
                SideIoMode mode = sideConfig.getMode(MachineCapabilityType.ITEM, relSide);
                if (mode.allowsInput()) {
                    Direction worldDir = relSide.toAbsolute(facing);
                    int[] inputSlots = getSlotsForFace(worldDir);
                    if (inputSlots.length == 0) continue;

                    BlockPos targetPos = pos.relative(worldDir);
                    ResourceHandler<ItemResource> targetHandler = level.getCapability(Capabilities.Item.BLOCK, targetPos, worldDir.getOpposite());
                    if (targetHandler != null) {
                        for (int targetSlot = 0; targetSlot < targetHandler.size(); targetSlot++) {
                            ItemStack extracted = ItemUtil.getStack(targetHandler, targetSlot);
                            if (!extracted.isEmpty()) {
                                for (int slot : inputSlots) {
                                    if (canPlaceItemThroughFace(slot, extracted, worldDir)) {
                                        ItemStack existing = getItem(slot);
                                        int maxToTake = Math.min(extracted.getCount(), 8);
                                        if (existing.isEmpty() || (ItemStack.isSameItemSameComponents(existing, extracted) && existing.getCount() < existing.getMaxStackSize())) {
                                            if (!existing.isEmpty()) {
                                                maxToTake = Math.min(maxToTake, existing.getMaxStackSize() - existing.getCount());
                                            }
                                            if (maxToTake > 0) {
                                                try (var tx = Transaction.open(null)) {
                                                    int reallyExtracted = targetHandler.extract(targetSlot, ItemResource.of(extracted), maxToTake, tx);
                                                    if (reallyExtracted > 0) {
                                                        tx.commit();
                                                        if (existing.isEmpty()) {
                                                            setItem(slot, extracted.copyWithCount(reallyExtracted));
                                                        } else {
                                                            existing.grow(reallyExtracted);
                                                        }
                                                        setChanged();
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.sideConfig.load(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.sideConfig.save(output);
    }

    @Override
    public boolean stillValid(Player player) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, player);
    }
}
