package io.github.gtbauke.modernmachines.api.resource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public record Material(
        String name,
        String displayName,
        MaterialType type,
        int colorHex,
        MapColor mapColor,
        TagKey<Block> miningLevelTag,
        float hardness,
        float resistance,
        float smeltingXp,
        int meltingPoint,
        int overlayIndex,
        Set<ResourceForm> supportedForms,
        Map<ResourceForm, DeferredBlock<Block>> blockRegistry,
        Map<ResourceForm, DeferredItem<? extends Item>> itemRegistry,
        Map<ResourceForm, Supplier<? extends FluidType>> fluidTypeRegistry,
        Map<ResourceForm, Supplier<? extends FlowingFluid>> fluidSourceRegistry,
        Map<ResourceForm, Supplier<? extends FlowingFluid>> fluidFlowingRegistry,
        Map<ResourceForm, Supplier<? extends ItemLike>> delegates
) {
    public Material(
            String name,
            String displayName,
            MaterialType type,
            int colorHex,
            MapColor mapColor,
            TagKey<Block> miningLevelTag,
            float hardness,
            float resistance,
            float smeltingXp,
            int meltingPoint,
            int overlayIndex,
            Set<ResourceForm> supportedForms,
            Map<ResourceForm, DeferredBlock<Block>> blockRegistry,
            Map<ResourceForm, DeferredItem<? extends Item>> itemRegistry,
            Map<ResourceForm, Supplier<? extends FluidType>> fluidTypeRegistry,
            Map<ResourceForm, Supplier<? extends FlowingFluid>> fluidSourceRegistry,
            Map<ResourceForm, Supplier<? extends FlowingFluid>> fluidFlowingRegistry,
            Map<ResourceForm, Supplier<? extends ItemLike>> delegates
    ) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.colorHex = colorHex;
        this.mapColor = mapColor;
        this.miningLevelTag = miningLevelTag != null ? miningLevelTag : BlockTags.NEEDS_STONE_TOOL;
        this.hardness = hardness;
        this.resistance = resistance;
        this.smeltingXp = smeltingXp;
        this.meltingPoint = meltingPoint;
        this.overlayIndex = overlayIndex;
        this.supportedForms = Collections.unmodifiableSet(supportedForms);
        this.blockRegistry = Collections.unmodifiableMap(blockRegistry);
        this.itemRegistry = Collections.unmodifiableMap(itemRegistry);
        this.fluidTypeRegistry = Collections.unmodifiableMap(fluidTypeRegistry);
        this.fluidSourceRegistry = Collections.unmodifiableMap(fluidSourceRegistry);
        this.fluidFlowingRegistry = Collections.unmodifiableMap(fluidFlowingRegistry);
        this.delegates = Collections.unmodifiableMap(delegates);
    }

    public boolean hasForm(ResourceForm form) {
        return supportedForms.contains(form);
    }

    public boolean isRegisteredLocally(ResourceForm form) {
        return blockRegistry.containsKey(form) || itemRegistry.containsKey(form) || fluidSourceRegistry.containsKey(form);
    }

    public boolean isDelegated(ResourceForm form) {
        return delegates.containsKey(form);
    }

    public DeferredBlock<Block> getDeferredBlock(ResourceForm form) {
        return blockRegistry.get(form);
    }

    public DeferredItem<? extends Item> getDeferredItem(ResourceForm form) {
        return itemRegistry.get(form);
    }

    public FluidType getFluidType(ResourceForm form) {
        Supplier<? extends FluidType> supplier = fluidTypeRegistry.get(form);
        return supplier != null ? supplier.get() : null;
    }

    public FlowingFluid getFluid(ResourceForm form) {
        Supplier<? extends FlowingFluid> supplier = fluidSourceRegistry.get(form);
        return supplier != null ? supplier.get() : null;
    }

    public FlowingFluid getFlowingFluid(ResourceForm form) {
        Supplier<? extends FlowingFluid> supplier = fluidFlowingRegistry.get(form);
        return supplier != null ? supplier.get() : null;
    }

    public Supplier<? extends ItemLike> getItemLikeSupplier(ResourceForm form) {
        if (itemRegistry.containsKey(form)) {
            return itemRegistry.get(form);
        }
        if (blockRegistry.containsKey(form)) {
            return blockRegistry.get(form);
        }
        return delegates.get(form);
    }

    public Item getItem(ResourceForm form) {
        Supplier<? extends ItemLike> supplier = getItemLikeSupplier(form);
        return supplier != null ? supplier.get().asItem() : null;
    }

    public Block getBlock(ResourceForm form) {
        if (blockRegistry.containsKey(form)) {
            return blockRegistry.get(form).get();
        }

        if (delegates.containsKey(form)) {
            ItemLike itemLike = delegates.get(form).get();
            if (itemLike instanceof Block block) {
                return block;
            }
        }

        return null;
    }

    public TagKey<Item> getItemTag(ResourceForm form) {
        return form.getItemTag(this.name);
    }

    public TagKey<Block> getBlockTag(ResourceForm form) {
        return form.getBlockTag(this.name);
    }

    public TagKey<Fluid> getFluidTag(ResourceForm form) {
        return form.getFluidTag(this.name);
    }

    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, this.name);
    }

    public String getTranslationKey() {
        return "material.modernmachines." + this.name;
    }

    public boolean isForm(ResourceForm form, Item item) {
        Item formItem = getItem(form);
        return formItem != null && formItem == item;
    }
}
