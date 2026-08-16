package io.github.gtbauke.modernmachines.machine.blockentity;

import java.util.List;
import java.util.Optional;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradableMachine;
import io.github.gtbauke.modernmachines.core.registry.ModBlockEntities;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModRecipeTypes;
import io.github.gtbauke.modernmachines.machine.block.BasicAlloySmelterControllerBlock;
import io.github.gtbauke.modernmachines.machine.block.BasicAlloySmelterHeaterBlock;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import io.github.gtbauke.modernmachines.machine.recipe.AlloySmelterInput;
import io.github.gtbauke.modernmachines.machine.recipe.AlloySmeltingRecipe;
import io.github.gtbauke.modernmachines.machine.upgrade.UpgradeContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterBlockEntity extends BaseMachineBlockEntity {
    public static final int SLOT_INPUT_A = 0;
    public static final int SLOT_INPUT_B = 1;
    public static final int SLOT_FUEL = 2;
    public static final int SLOT_OUTPUT = 3;
    public static final int TOTAL_SLOTS = 4;

    public static final double BASE_SPEED = 1.0;
    public static final double BASE_EFFICIENCY = 1.0;
    public static final double BASE_CAPACITY = 1.0;

    private static final int[] SLOTS_INPUT = new int[]{SLOT_INPUT_A, SLOT_INPUT_B};
    private static final int[] SLOTS_FUEL = new int[]{SLOT_FUEL};
    private static final int[] SLOTS_OUTPUT = new int[]{SLOT_OUTPUT};
    private static final int[] SLOTS_ALL = new int[]{SLOT_INPUT_A, SLOT_INPUT_B, SLOT_FUEL, SLOT_OUTPUT};
    private static final int[] SLOTS_NONE = new int[0];

    private NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private int progress = 0;
    private int maxProgress = 200;
    private int litTime = 0;
    private int litDuration = 0;
    private boolean formed = false;

    private final RecipeManager.CachedCheck<AlloySmelterInput, AlloySmeltingRecipe> quickCheck =
            RecipeManager.createCheck(ModRecipeTypes.ALLOY_SMELTING.get());

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> litTime;
                case 3 -> litDuration;
                case 4 -> isFormed() ? 1 : 0;
                case 5 -> (int) Math.round(stats.getSpeedMultiplier() * 100);
                case 6 -> (int) Math.round(stats.getEfficiencyMultiplier() * 100);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> litTime = value;
                case 3 -> litDuration = value;
                case 4 -> formed = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public AlloySmelterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALLOY_SMELTER.get(), pos, blockState);
        this.stats.setBaseValue(io.github.gtbauke.modernmachines.api.machine.stat.MachineStatType.SPEED, BASE_SPEED);
        this.stats.setBaseValue(io.github.gtbauke.modernmachines.api.machine.stat.MachineStatType.ENERGY_EFFICIENCY, BASE_EFFICIENCY);
        this.stats.setBaseValue(io.github.gtbauke.modernmachines.api.machine.stat.MachineStatType.ENERGY_CAPACITY, BASE_CAPACITY);
    }

    public boolean isFormed() {
        if (this.level != null) {
            BlockPos heaterPos = this.worldPosition.below();
            this.formed = this.level.getBlockState(heaterPos).is(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());
        }
        return this.formed;
    }

    public boolean isLit() {
        return litTime > 0;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AlloySmelterBlockEntity blockEntity) {
        // Client particles
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlloySmelterBlockEntity blockEntity) {
        boolean wasLit = blockEntity.isLit();
        boolean changed = false;

        // 1. Multiblock Formation Check (Heater must be directly below)
        BlockPos heaterPos = pos.below();
        BlockState heaterState = level.getBlockState(heaterPos);
        boolean isHeaterBelow = heaterState.is(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());

        if (blockEntity.formed != isHeaterBelow) {
            blockEntity.formed = isHeaterBelow;
            changed = true;
            if (state.hasProperty(BasicAlloySmelterControllerBlock.FORMED) && state.getValue(BasicAlloySmelterControllerBlock.FORMED) != isHeaterBelow) {
                level.setBlock(pos, state.setValue(BasicAlloySmelterControllerBlock.FORMED, isHeaterBelow), 3);
            }
            if (isHeaterBelow && heaterState.hasProperty(BasicAlloySmelterHeaterBlock.FORMED) && !heaterState.getValue(BasicAlloySmelterHeaterBlock.FORMED)) {
                level.setBlock(heaterPos, heaterState.setValue(BasicAlloySmelterHeaterBlock.FORMED, true), 3);
            }
        }

        // 2. Consume Lit Time (Accelerated during active smelting under speed upgrades)
        if (blockEntity.litTime > 0) {
            int burnRate = (blockEntity.progress > 0) ? Math.max(1, (int) Math.round(blockEntity.stats.getSpeedMultiplier())) : 1;
            blockEntity.litTime = Math.max(0, blockEntity.litTime - burnRate);
        }

        // 3. Process Smelting if formed
        if (blockEntity.isFormed() && level instanceof ServerLevel serverLevel) {
            AlloySmelterInput recipeInput = new AlloySmelterInput(List.of(
                    blockEntity.items.get(SLOT_INPUT_A),
                    blockEntity.items.get(SLOT_INPUT_B)
            ));

            Optional<RecipeHolder<AlloySmeltingRecipe>> optionalRecipe = blockEntity.quickCheck.getRecipeFor(recipeInput, serverLevel);

            if (optionalRecipe.isPresent()) {
                AlloySmeltingRecipe recipe = optionalRecipe.get().value();

                if (blockEntity.canCraft(recipe, recipeInput)) {
                    // Ignite fuel if not lit
                    if (blockEntity.litTime <= 0) {
                        ItemStack fuelStack = blockEntity.items.get(SLOT_FUEL);
                        if (!fuelStack.isEmpty()) {
                            int rawBurnDuration = level.fuelValues().burnDuration(fuelStack);
                            if (rawBurnDuration > 0) {
                                int effectiveBurnDuration = (int) Math.round(rawBurnDuration * blockEntity.stats.getEfficiencyMultiplier());
                                blockEntity.litDuration = effectiveBurnDuration;
                                blockEntity.litTime = effectiveBurnDuration;
                                net.minecraft.world.item.ItemStackTemplate remainderTemplate = fuelStack.getItem().getCraftingRemainder();
                                ItemStack remainder = remainderTemplate != null ? remainderTemplate.create() : ItemStack.EMPTY;
                                fuelStack.shrink(1);
                                if (fuelStack.isEmpty()) {
                                    blockEntity.items.set(SLOT_FUEL, remainder);
                                }
                                changed = true;
                            }
                        }
                    }

                    // Progress cooking if active fire
                    if (blockEntity.litTime > 0) {
                        blockEntity.progress++;
                        int baseCookTime = recipe.cookingTime() > 0 ? recipe.cookingTime() : 200;
                        int scaledCookTime = Math.max(10, (int) Math.round(baseCookTime / blockEntity.stats.getSpeedMultiplier()));
                        blockEntity.maxProgress = scaledCookTime;

                        if (blockEntity.progress >= blockEntity.maxProgress) {
                            blockEntity.craftRecipe(recipe, recipeInput);
                            blockEntity.progress = 0;
                            changed = true;
                        }
                    } else {
                        // Cooldown slowly if no fire
                        if (blockEntity.progress > 0) {
                            blockEntity.progress = Math.max(0, blockEntity.progress - 2);
                        }
                    }
                } else {
                    blockEntity.progress = 0;
                }
            } else {
                blockEntity.progress = 0;
            }
        } else {
            // Unformed or broken -> reset progress
            blockEntity.progress = 0;
        }

        // 4. Update LIT blockstate properties on both controller and heater
        boolean isNowLit = blockEntity.isLit();
        if (wasLit != isNowLit) {
            changed = true;
            if (state.hasProperty(BasicAlloySmelterControllerBlock.LIT)) {
                level.setBlock(pos, state.setValue(BasicAlloySmelterControllerBlock.LIT, isNowLit), 3);
            }
            if (blockEntity.isFormed() && heaterState.hasProperty(BasicAlloySmelterHeaterBlock.LIT)) {
                level.setBlock(heaterPos, heaterState.setValue(BasicAlloySmelterHeaterBlock.LIT, isNowLit), 3);
            }
        }

        if (changed) {
            blockEntity.setChanged();
        }

        // 4. Auto-Eject and Auto-Pull Transfer
        blockEntity.tickAutoTransfer(level, pos, state);
    }

    private boolean canCraft(AlloySmeltingRecipe recipe, AlloySmelterInput input) {
        ItemStack outputSlot = this.items.get(SLOT_OUTPUT);
        ItemStack recipeResult = recipe.assemble(input);

        if (recipeResult.isEmpty()) return false;
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, recipeResult)) return false;

        return outputSlot.getCount() + recipeResult.getCount() <= outputSlot.getMaxStackSize();
    }

    private void craftRecipe(AlloySmeltingRecipe recipe, AlloySmelterInput input) {
        ItemStack recipeResult = recipe.assemble(input);

        // Consume required ingredient counts
        for (SizedIngredient sizedIngredient : recipe.inputs()) {
            int needed = sizedIngredient.count();
            for (int slotIdx : SLOTS_INPUT) {
                ItemStack stack = this.items.get(slotIdx);
                if (!stack.isEmpty() && sizedIngredient.ingredient().test(stack)) {
                    int take = Math.min(needed, stack.getCount());
                    stack.shrink(take);
                    if (stack.isEmpty()) {
                        this.items.set(slotIdx, ItemStack.EMPTY);
                    }
                    needed -= take;
                    if (needed <= 0) break;
                }
            }
        }

        // Output result
        ItemStack outputSlot = this.items.get(SLOT_OUTPUT);
        if (outputSlot.isEmpty()) {
            this.items.set(SLOT_OUTPUT, recipeResult.copy());
        } else {
            outputSlot.grow(recipeResult.getCount());
        }
    }

    @Override
    public int getContainerSize() {
        return TOTAL_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        setChanged();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.modernmachines.basic_alloy_smelter");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        isFormed();
        return new AlloySmelterMenu(containerId, playerInventory, this, this.upgradeContainer, this.dataAccess);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.progress = input.getIntOr("progress", 0);
        this.maxProgress = input.getIntOr("max_progress", 200);
        if (this.maxProgress == 0) this.maxProgress = 200;
        this.litTime = input.getIntOr("lit_time", 0);
        this.litDuration = input.getIntOr("lit_duration", 0);
        this.formed = input.getBooleanOr("formed", false);

        input.child("upgrades").ifPresent(child -> ContainerHelper.loadAllItems(child, this.upgradeContainer.getItems()));
        this.upgradeContainer.recalculateUpgrades();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("progress", this.progress);
        output.putInt("max_progress", this.maxProgress);
        output.putInt("lit_time", this.litTime);
        output.putInt("lit_duration", this.litDuration);
        output.putBoolean("formed", this.isFormed());

        ContainerHelper.saveAllItems(output.child("upgrades"), this.upgradeContainer.getItems());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side != null) {
            SideIoMode mode = this.sideConfig.getModeAbsolute(MachineCapabilityType.ITEM, getMachineFacing(), side);
            return switch (mode) {
                case INPUT -> new int[]{SLOT_INPUT_A, SLOT_INPUT_B, SLOT_FUEL};
                case OUTPUT -> SLOTS_OUTPUT;
                case BOTH -> SLOTS_ALL;
                case NONE -> SLOTS_NONE;
            };
        }
        return SLOTS_ALL;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        if (direction != null) {
            SideIoMode mode = this.sideConfig.getModeAbsolute(MachineCapabilityType.ITEM, getMachineFacing(), direction);
            if (!mode.allowsInput()) return false;
        }
        if (index == SLOT_OUTPUT) return false;
        if (index == SLOT_FUEL) {
            return this.level != null && this.level.fuelValues().burnDuration(itemStack) > 0;
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (direction != null) {
            SideIoMode mode = this.sideConfig.getModeAbsolute(MachineCapabilityType.ITEM, getMachineFacing(), direction);
            if (!mode.allowsOutput()) return false;
        }
        return index == SLOT_OUTPUT;
    }
}
