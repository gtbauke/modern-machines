package io.github.gtbauke.modernmachines.modular.menu;

import java.util.EnumMap;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.core.menu.BaseContainerMenu;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.modular.client.TinkeringTableScreen;
import io.github.gtbauke.modernmachines.modular.item.ModularToolItem;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.core.component.DataComponents;
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
import org.jspecify.annotations.NonNull;

public class TinkeringTableMenu extends BaseContainerMenu {
    public static enum ActiveTool {
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        SWORD,
        ;

        public ItemStack getIcon() {
            return switch (this) {
                case PICKAXE -> new ItemStack(ModItems.MODULAR_PICKAXE.get());
                case AXE -> new ItemStack(ModItems.MODULAR_AXE.get());
                case SHOVEL -> new ItemStack(ModItems.MODULAR_SHOVEL.get());
                case HOE -> new ItemStack(ModItems.MODULAR_HOE.get());
                case SWORD -> new ItemStack(ModItems.MODULAR_SWORD.get());
            };
        }

        public Item getItem() {
            return switch (this) {
                case PICKAXE -> ModItems.MODULAR_PICKAXE.get();
                case AXE -> ModItems.MODULAR_AXE.get();
                case SHOVEL -> ModItems.MODULAR_SHOVEL.get();
                case HOE -> ModItems.MODULAR_HOE.get();
                case SWORD -> ModItems.MODULAR_SWORD.get();
            };
        }

        public Item getHeadPattern() {
            return switch (this) {
                case PICKAXE -> ModItems.PICKAXE_HEAD_PATTERN.get();
                case AXE -> ModItems.AXE_HEAD_PATTERN.get();
                case SHOVEL -> ModItems.SHOVEL_HEAD_PATTERN.get();
                case HOE -> ModItems.HOE_HEAD_PATTERN.get();
                case SWORD -> ModItems.SWORD_BLADE_PATTERN.get();
            };
        }

        public boolean requiresBinding() {
            return switch (this) {
                case PICKAXE, AXE, SWORD -> true;
                case SHOVEL, HOE -> false;
            };
        }

        public boolean acceptsAttachment() {
            return switch (this) {
                case PICKAXE, AXE, SWORD -> true;
                case SHOVEL, HOE -> false;
            };
        }

        public Item getBindingPattern() {
            return switch (this) {
                case SWORD -> ModItems.SWORD_GUARD_PATTERN.get();
                default -> ModItems.BINDING_PATTERN.get();
            };
        }

        public Item getAttachmentPattern() {
            return switch (this) {
                case SWORD -> ModItems.POMMEL_PATTERN.get();
                default -> ModItems.TIP_PATTERN.get();
            };
        }
    }

    private static class TinkeringTableSlot extends Slot {
        private final TinkeringTableMenu menu;
        private final Container inputContainer;

        public TinkeringTableSlot(Container container, int slot, int x, int y, TinkeringTableMenu menu) {
            super(container, slot, x, y);

            this.menu = menu;
            this.inputContainer = container;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            this.menu.slotsChanged(inputContainer);
        }
    }

    private static class TinkeringTableResultSlot extends Slot {
        private final TinkeringTableMenu menu;
        private final Container inputContainer;

        public TinkeringTableResultSlot(Container resultContainer, Container inputContainer, int x, int y, TinkeringTableMenu menu) {
            super(resultContainer, 0, x, y);
            this.menu = menu;
            this.inputContainer = inputContainer;
        }

        @Override
        public boolean mayPlace(@NonNull ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(@NonNull Player player, @NonNull ItemStack stack) {
            int tab = this.menu.getActiveTab();
            if (tab == 0) {
                for (int i = 0; i < 4; i++) {
                    if (!inputContainer.getItem(i).isEmpty()) {
                        inputContainer.removeItem(i, 1);
                    }
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
            this.menu.slotsChanged(inputContainer);
        }
    }

    private final ContainerLevelAccess access;
    private final ContainerData data;

    private final Container inputContainer = new SimpleContainer(4);
    private final Container resultContainer = new SimpleContainer(1);

    private static final int HEAD_SLOT = 0;
    private static final int HANDLE_SLOT = 1;
    private static final int BINDING_SLOT = 2;
    private static final int ATTACHMENT_SLOT = 3;
    private static final int RESULT_SLOT = 4;

    private ActiveTool activeTool = ActiveTool.PICKAXE;

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

        for (var i = 0; i <= ATTACHMENT_SLOT; ++i) {
            this.addSlot(new TinkeringTableSlot(
                    inputContainer,
                    i,
                    48,
                    20 + (i * 28),
                    this
            ));
        }

        this.addSlot(
            new TinkeringTableResultSlot(
                resultContainer,
                inputContainer,
                134,
                34,
                this
            )
        );

        addStandardPlayerInventory(playerInventory);
    }

    public int getActiveTab() {
        return this.data.get(0);
    }

    public void setActiveTab(int tab) {
        this.data.set(0, tab);
        slotsChanged(inputContainer);
    }

    public void setActiveTool(ActiveTool activeTool) {
        this.activeTool = activeTool;
        slotsChanged(inputContainer);
    }

    public ActiveTool getActiveTool() {
        return this.activeTool;
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
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
        var headStack = inputContainer.getItem(0);
        var handleStack = inputContainer.getItem(1);
        var bindingStack = inputContainer.getItem(2);
        var attachStack = inputContainer.getItem(3);

        if (headStack.isEmpty() || handleStack.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (!(headStack.getItem() instanceof ToolPartItem headPart) ||
            !(handleStack.getItem() instanceof ToolPartItem handlePart)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (headPart.getPartType().getSlot() != PartSlot.HEAD ||
            handlePart.getPartType().getSlot() != PartSlot.HANDLE) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var targetTool = determineToolItem(headPart.getPartType());
        if (targetTool == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        boolean requiresBinding = (headPart.getPartType() == ToolPartType.PICKAXE_HEAD ||
                                    headPart.getPartType() == ToolPartType.AXE_HEAD ||
                                    headPart.getPartType() == ToolPartType.SWORD_BLADE);

        if (requiresBinding && bindingStack.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (!bindingStack.isEmpty()) {
            if (!(bindingStack.getItem() instanceof ToolPartItem bindingPart) ||
                bindingPart.getPartType().getSlot() != PartSlot.BINDING) {
                resultContainer.setItem(0, ItemStack.EMPTY);
                return;
            }
        }

        var headId = ToolPartItem.getMaterialId(headStack);
        var handleId = ToolPartItem.getMaterialId(handleStack);

        if (headId == null || handleId == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var parts = new EnumMap<PartSlot, Identifier>(PartSlot.class);
        parts.put(PartSlot.HEAD, headId);
        parts.put(PartSlot.HANDLE, handleId);

        if (!bindingStack.isEmpty()) {
            var bindingId = ToolPartItem.getMaterialId(bindingStack);
            if (bindingId != null) {
                parts.put(PartSlot.BINDING, bindingId);
            }
        }

        if (!attachStack.isEmpty() && attachStack.getItem() instanceof ToolPartItem attachPart) {
            var attachId = ToolPartItem.getMaterialId(attachStack);
            if (attachId != null) {
                parts.put(attachPart.getPartType().getSlot(), attachId);
            }
        }

        var toolData = new ModularToolData(parts, java.util.Collections.emptyList(), 0, 0);
        var resultStack = new ItemStack(targetTool);
        resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), toolData);
        ModularToolItem.recalculateComponents(resultStack);

        resultContainer.setItem(0, resultStack);
    }

    private void updateUpgradeResult() {
        var toolStack = inputContainer.getItem(0);
        if (toolStack.isEmpty() || !(toolStack.getItem() instanceof ModularToolItem)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var data = toolStack.get(ModDataComponents.MODULAR_TOOL_DATA.get());
        if (data == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var mod1 = inputContainer.getItem(1);
        var mod2 = inputContainer.getItem(2);

        if (mod1.isEmpty() && mod2.isEmpty()) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var modifiedData = data;

        if (!mod1.isEmpty()) {
            modifiedData = applyModifierItem(modifiedData, mod1.getItem());
        }

        if (!mod2.isEmpty() && modifiedData != null) {
            modifiedData = applyModifierItem(modifiedData, mod2.getItem());
        }

        if (modifiedData != null && modifiedData != data) {
            var resultStack = toolStack.copy();
            resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), modifiedData);
            ModularToolItem.recalculateComponents(resultStack);
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
        var toolStack = inputContainer.getItem(0);
        var matStack = inputContainer.getItem(1);

        if (toolStack.isEmpty() || matStack.isEmpty() || !(toolStack.getItem() instanceof ModularToolItem)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        var data = toolStack.get(ModDataComponents.MODULAR_TOOL_DATA.get());
        if (data == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (matStack.getItem() instanceof ToolPartItem newPart) {
            var newMatId = ToolPartItem.getMaterialId(matStack);
            if (newMatId != null) {
                var swappedData = data.withPart(newPart.getPartType().getSlot(), newMatId);
                var resultStack = toolStack.copy();
                resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), swappedData);
                ModularToolItem.recalculateComponents(resultStack);
                resultContainer.setItem(0, resultStack);
                return;
            }
        }

        int curDmg = toolStack.getOrDefault(DataComponents.DAMAGE, data.damage());
        if (curDmg > 0) {
            int repairAmount = (int) (ModularToolItem.getMaxDurability(toolStack) * 0.35f);
            int newDamage = Math.max(0, curDmg - repairAmount);
            var resultStack = toolStack.copy();
            resultStack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data.withDamage(newDamage));
            resultStack.set(DataComponents.DAMAGE, newDamage);
            ModularToolItem.recalculateComponents(resultStack);
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
    public boolean clickMenuButton(@NonNull Player player, int id) {
        if (id >= 0 && id <= 2) {
            setActiveTab(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return isStillValid(this.access, player, ModBlocks.TINKERING_TABLE.get());
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot.hasItem()) {
            var stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 4) {
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= 0 && index < containerSlotCount) {
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 4, false)) {
                    if (moveBetweenInventoryAndHotbar(stackInSlot, index)) {
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
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputContainer));
    }
}
