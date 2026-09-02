package io.github.gtbauke.modernmachines.modular.menu;

import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.core.menu.BaseContainerMenu;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.modular.item.PatternItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class PartBuilderMenu extends BaseContainerMenu {
    private final ContainerLevelAccess access;
    private final Container inputContainer = new SimpleContainer(2);
    private final Container resultContainer = new SimpleContainer(1);

    public PartBuilderMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public PartBuilderMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuTypes.PART_BUILDER.get(), containerId, 3);
        this.access = access;

        this.addSlot(new Slot(inputContainer, 0, 48, 34) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return stack.getItem() instanceof PatternItem;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                PartBuilderMenu.this.slotsChanged(inputContainer);
            }
        });

        // Input 1: Material Slot (x=68, y=34)
        this.addSlot(new Slot(inputContainer, 1, 68, 34) {
            @Override
            public void setChanged() {
                super.setChanged();
                PartBuilderMenu.this.slotsChanged(inputContainer);
            }
        });

        // Output Slot 2: (x=124, y=34)
        this.addSlot(new Slot(resultContainer, 0, 124, 34) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NonNull Player player, @NonNull ItemStack stack) {
                var patternStack = inputContainer.getItem(0);
                var matStack = inputContainer.getItem(1);

                if (patternStack.getItem() instanceof PatternItem pattern) {
                    pattern.getTargetPart().ifPresent(partType -> {
                        matStack.shrink(partType.getMaterialCost());
                    });
                }

                super.onTake(player, stack);
                PartBuilderMenu.this.slotsChanged(inputContainer);
            }
        });

        addStandardPlayerInventory(playerInventory);
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        var patternStack = inputContainer.getItem(0);
        var matStack = inputContainer.getItem(1);

        if (patternStack.isEmpty() || matStack.isEmpty() || !(patternStack.getItem() instanceof PatternItem pattern)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var optPart = pattern.getTargetPart();
        if (optPart.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var partType = optPart.get();
        if (matStack.getCount() < partType.getMaterialCost()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var matchedMaterial = findMaterialFromItem(matStack.getItem());
        if (matchedMaterial != null) {
            var partItem = ModItems.getToolPart(partType, matchedMaterial);
            if (partItem != null) {
                resultContainer.setItem(0, new ItemStack(partItem));
                return;
            }
        }

        var optDatapackStats = MaterialStatsManager.getMaterialForIngredient(matStack);
        if (optDatapackStats.isPresent()) {
            var stats = optDatapackStats.get();
            var basePartItem = ModItems.getToolPart(partType, ModMaterials.IRON);
            if (basePartItem != null) {
                var dynamicPartStack = new ItemStack(basePartItem);
                dynamicPartStack.set(ModDataComponents.MATERIAL_ID.get(), stats.materialId());
                resultContainer.setItem(0, dynamicPartStack);
                return;
            }
        }

        resultContainer.setItem(0, ItemStack.EMPTY);
    }

    private Material findMaterialFromItem(Item item) {
        for (var material : ModMaterials.getAllMaterials()) {
            for (var form : material.supportedForms()) {
                if (material.isForm(form, item)) {
                    return material;
                }
            }
        }

        return null;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return isStillValid(this.access, player, ModBlocks.PART_BUILDER.get());
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot.hasItem()) {
            var stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 2) {
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.getItem() instanceof PatternItem) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        if (moveBetweenInventoryAndHotbar(stackInSlot, index)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    if (!this.moveItemStackTo(stackInSlot, 1, 2, false)) {
                        if (moveBetweenInventoryAndHotbar(stackInSlot, index)) {
                            return ItemStack.EMPTY;
                        }
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

    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputContainer));
    }
}
