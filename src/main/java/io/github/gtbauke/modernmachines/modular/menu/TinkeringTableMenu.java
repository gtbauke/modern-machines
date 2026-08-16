package io.github.gtbauke.modernmachines.modular.menu;

import java.util.EnumMap;
import java.util.Map;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.core.menu.BaseContainerMenu;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.modular.item.ModularToolItem;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TinkeringTableMenu extends BaseContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;

    private final Container inputContainer = new SimpleContainer(4);
    private final Container resultContainer = new SimpleContainer(1);

    public TinkeringTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, new SimpleContainerData(1));
    }

    public TinkeringTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        this(containerId, playerInventory, access, new SimpleContainerData(1));
    }

    public TinkeringTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, ContainerData data) {
        super(ModMenuTypes.TINKERING_TABLE.get(), containerId, 5);
        this.access = access;
        this.data = data;
        this.addDataSlots(data);

        // Assembly slots: 0=Head (x=48, y=20), 1=Handle (x=48, y=48), 2=Binding (x=26, y=34), 3=Attachment (x=70, y=34)
        this.addSlot(new Slot(inputContainer, 0, 48, 20) {
            @Override
            public void setChanged() {
                super.setChanged();
                TinkeringTableMenu.this.slotsChanged(inputContainer);
            }
        });
        this.addSlot(new Slot(inputContainer, 1, 48, 48) {
            @Override
            public void setChanged() {
                super.setChanged();
                TinkeringTableMenu.this.slotsChanged(inputContainer);
            }
        });
        this.addSlot(new Slot(inputContainer, 2, 26, 34) {
            @Override
            public void setChanged() {
                super.setChanged();
                TinkeringTableMenu.this.slotsChanged(inputContainer);
            }
        });
        this.addSlot(new Slot(inputContainer, 3, 70, 34) {
            @Override
            public void setChanged() {
                super.setChanged();
                TinkeringTableMenu.this.slotsChanged(inputContainer);
            }
        });

        // Output Slot 4: (x=134, y=34 - Strictly Read-Only)
        this.addSlot(new Slot(resultContainer, 0, 134, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                int tab = getActiveTab();
                if (tab == 0) {
                    for (int i = 0; i < 4; i++) {
                        inputContainer.removeItem(i, 1);
                    }
                } else if (tab == 1) {
                    inputContainer.removeItem(0, 1);
                    inputContainer.removeItem(1, 1);
                    inputContainer.removeItem(2, 1);
                } else if (tab == 2) {
                    inputContainer.removeItem(0, 1);
                    inputContainer.removeItem(1, 1);
                }

                super.onTake(player, stack);
                TinkeringTableMenu.this.slotsChanged(inputContainer);
            }
        });

        // Add 36-slot player inventory & hotbar
        addStandardPlayerInventory(playerInventory);
    }

    public int getActiveTab() {
        return this.data.get(0);
    }

    public void setActiveTab(int tab) {
        this.data.set(0, tab);
        slotsChanged(inputContainer);
    }

    @Override
    public void slotsChanged(Container container) {
        int tab = getActiveTab();
        if (tab == 0) {
            updateAssemblyResult();
        } else if (tab == 1) {
            updateUpgradeResult();
        } else if (tab == 2) {
            updateRepairResult();
        }
    }

    private void updateAssemblyResult() {
        ItemStack headStack = inputContainer.getItem(0);
        ItemStack handleStack = inputContainer.getItem(1);
        ItemStack bindingStack = inputContainer.getItem(2);
        ItemStack attachStack = inputContainer.getItem(3);

        if (headStack.isEmpty() || handleStack.isEmpty() || bindingStack.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (!(headStack.getItem() instanceof ToolPartItem headPart) ||
            !(handleStack.getItem() instanceof ToolPartItem handlePart) ||
            !(bindingStack.getItem() instanceof ToolPartItem bindingPart)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (headPart.getPartType().getSlot() != PartSlot.HEAD ||
            handlePart.getPartType().getSlot() != PartSlot.HANDLE ||
            bindingPart.getPartType().getSlot() != PartSlot.BINDING) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        Item targetTool = determineToolItem(headPart.getPartType());
        if (targetTool == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        Identifier headId = ToolPartItem.getMaterialId(headStack);
        Identifier handleId = ToolPartItem.getMaterialId(handleStack);
        Identifier bindingId = ToolPartItem.getMaterialId(bindingStack);

        if (headId == null || handleId == null || bindingId == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        Map<PartSlot, Identifier> parts = new EnumMap<>(PartSlot.class);
        parts.put(PartSlot.HEAD, headId);
        parts.put(PartSlot.HANDLE, handleId);
        parts.put(PartSlot.BINDING, bindingId);

        if (!attachStack.isEmpty() && attachStack.getItem() instanceof ToolPartItem attachPart) {
            Identifier attachId = ToolPartItem.getMaterialId(attachStack);
            if (attachId != null) {
                parts.put(attachPart.getPartType().getSlot(), attachId);
            }
        }

        ModularToolData toolData = new ModularToolData(parts, java.util.Collections.emptyList(), 0, 0);
        ItemStack resultStack = new ItemStack(targetTool);
        resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), toolData);

        resultContainer.setItem(0, resultStack);
    }

    private void updateUpgradeResult() {
        ItemStack toolStack = inputContainer.getItem(0);
        if (toolStack.isEmpty() || !(toolStack.getItem() instanceof ModularToolItem)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        ModularToolData data = toolStack.get(ModDataComponents.MODULAR_TOOL_DATA.get());
        if (data == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        ItemStack mod1 = inputContainer.getItem(1);
        ItemStack mod2 = inputContainer.getItem(2);

        if (mod1.isEmpty() && mod2.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        ModularToolData modifiedData = data;

        // Process Mod 1
        if (!mod1.isEmpty()) {
            modifiedData = applyModifierItem(modifiedData, mod1.getItem());
        }
        // Process Mod 2
        if (!mod2.isEmpty() && modifiedData != null) {
            modifiedData = applyModifierItem(modifiedData, mod2.getItem());
        }

        if (modifiedData != null && modifiedData != data) {
            ItemStack resultStack = toolStack.copy();
            resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), modifiedData);
            resultContainer.setItem(0, resultStack);
        } else {
            resultContainer.setItem(0, ItemStack.EMPTY);
        }
    }

    private ModularToolData applyModifierItem(ModularToolData currentData, Item item) {
        if (currentData.getUsedModifierSlots() >= currentData.getMaxModifierSlots()) {
            return null;
        }

        Identifier modId;
        if (item == Items.REDSTONE) {
            modId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "haste");
        } else if (item == Items.LAPIS_LAZULI) {
            modId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "luck");
        } else if (item == Items.QUARTZ) {
            modId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "sharpness");
        } else if (item == Items.DIAMOND) {
            modId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "diamond");
        } else if (item == Items.NETHERITE_INGOT) {
            modId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "reinforced");
        } else {
            return null;
        }

        int currentLvl = currentData.getModifierLevel(modId);
        return currentData.withModifier(modId, currentLvl + 1);
    }

    private void updateRepairResult() {
        ItemStack toolStack = inputContainer.getItem(0);
        ItemStack matStack = inputContainer.getItem(1);

        if (toolStack.isEmpty() || matStack.isEmpty() || !(toolStack.getItem() instanceof ModularToolItem)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        ModularToolData data = toolStack.get(ModDataComponents.MODULAR_TOOL_DATA.get());
        if (data == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (matStack.getItem() instanceof ToolPartItem newPart) {
            Identifier newMatId = ToolPartItem.getMaterialId(matStack);
            if (newMatId != null) {
                ModularToolData swappedData = data.withPart(newPart.getPartType().getSlot(), newMatId);
                ItemStack resultStack = toolStack.copy();
                resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), swappedData);
                resultContainer.setItem(0, resultStack);
                return;
            }
        }

        if (data.damage() > 0) {
            int repairAmount = (int) (ModularToolItem.getMaxDurability(toolStack) * 0.35f);
            int newDamage = Math.max(0, data.damage() - repairAmount);
            ItemStack resultStack = toolStack.copy();
            resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data.withDamage(newDamage));
            resultContainer.setItem(0, resultStack);
        } else {
            resultContainer.setItem(0, ItemStack.EMPTY);
        }
    }

    private Item determineToolItem(ToolPartType headType) {
        return switch (headType) {
            case PICKAXE_HEAD -> ModItems.MODULAR_PICKAXE.get();
            case AXE_HEAD -> ModItems.MODULAR_AXE.get();
            case SHOVEL_HEAD -> ModItems.MODULAR_SHOVEL.get();
            case SWORD_BLADE -> ModItems.MODULAR_SWORD.get();
            case HOE_HEAD -> ModItems.MODULAR_HOE.get();
            default -> null;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return isStillValid(this.access, player, ModBlocks.TINKERING_TABLE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 4) { // Output slot -> move to player inventory (hotbar first)
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= 0 && index < containerSlotCount) { // Workstation inputs -> move to player inventory
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // Player inventory / hotbar (5..40)
                // 1. Try to place into workstation inputs (0..4)
                if (!this.moveItemStackTo(stackInSlot, 0, 4, false)) {
                    // 2. Transfer between main inventory and hotbar
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputContainer));
    }
}
