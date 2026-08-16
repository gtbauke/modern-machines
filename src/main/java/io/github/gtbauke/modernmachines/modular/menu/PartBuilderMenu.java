package io.github.gtbauke.modernmachines.modular.menu;

import java.util.Optional;

import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.menu.BaseContainerMenu;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.modular.item.PatternItem;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

        // Input 0: Pattern Slot (x=48, y=34)
        this.addSlot(new Slot(inputContainer, 0, 48, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
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

        // Output Slot 2: (x=124, y=34 - Strictly Read-Only)
        this.addSlot(new Slot(resultContainer, 0, 124, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                ItemStack patternStack = inputContainer.getItem(0);
                ItemStack matStack = inputContainer.getItem(1);

                if (patternStack.getItem() instanceof PatternItem pattern) {
                    pattern.getTargetPart().ifPresent(partType -> {
                        matStack.shrink(partType.getMaterialCost());
                    });
                }

                super.onTake(player, stack);
                PartBuilderMenu.this.slotsChanged(inputContainer);
            }
        });

        // Add 36-slot player inventory & hotbar
        addStandardPlayerInventory(playerInventory);
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack patternStack = inputContainer.getItem(0);
        ItemStack matStack = inputContainer.getItem(1);

        if (patternStack.isEmpty() || matStack.isEmpty() || !(patternStack.getItem() instanceof PatternItem pattern)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        Optional<ToolPartType> optPart = pattern.getTargetPart();
        if (optPart.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        ToolPartType partType = optPart.get();
        if (matStack.getCount() < partType.getMaterialCost()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        // 1. Check built-in Java materials
        Material matchedMaterial = findMaterialFromItem(matStack.getItem());
        if (matchedMaterial != null) {
            ToolPartItem partItem = ModItems.getToolPart(partType, matchedMaterial);
            if (partItem != null) {
                resultContainer.setItem(0, new ItemStack(partItem));
                return;
            }
        }

        // 2. Check dynamic datapack materials
        Optional<MaterialToolStats> optDatapackStats = MaterialStatsManager.getMaterialForIngredient(matStack);
        if (optDatapackStats.isPresent()) {
            MaterialToolStats stats = optDatapackStats.get();
            ToolPartItem basePartItem = ModItems.getToolPart(partType, ModMaterials.IRON);
            if (basePartItem != null) {
                ItemStack dynamicPartStack = new ItemStack(basePartItem);
                dynamicPartStack.set(ModDataComponents.MATERIAL_ID.get(), stats.materialId());
                resultContainer.setItem(0, dynamicPartStack);
                return;
            }
        }

        resultContainer.setItem(0, ItemStack.EMPTY);
    }

    private Material findMaterialFromItem(Item item) {
        for (Material material : ModMaterials.getAllMaterials()) {
            for (ResourceForm form : material.supportedForms()) {
                if (material.isForm(form, item)) {
                    return material;
                }
            }
        }
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return isStillValid(this.access, player, ModBlocks.PART_BUILDER.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 2) { // Output slot -> move to player inventory (hotbar first)
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index == 0 || index == 1) { // Machine inputs -> move to player inventory
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // Player inventory / hotbar (3..38)
                // 1. If pattern item, try pattern slot (0)
                if (stackInSlot.getItem() instanceof PatternItem) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        if (!moveBetweenInventoryAndHotbar(stackInSlot, index)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    // 2. Try material input slot (1)
                    if (!this.moveItemStackTo(stackInSlot, 1, 2, false)) {
                        if (!moveBetweenInventoryAndHotbar(stackInSlot, index)) {
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
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputContainer));
    }
}
