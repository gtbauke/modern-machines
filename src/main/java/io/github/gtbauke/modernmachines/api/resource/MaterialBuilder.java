package io.github.gtbauke.modernmachines.api.resource;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MaterialBuilder {
    private final String name;
    private String displayName;
    private MaterialType type = MaterialType.METALLIC_ORE;
    private int colorHex = 0xFFFFFF;
    private MapColor mapColor = MapColor.METAL;
    private TagKey<Block> miningLevelTag = BlockTags.NEEDS_STONE_TOOL;
    private float hardness = 3.0f;
    private float resistance = 3.0f;
    private float smeltingXp = 0.7f;
    private final Set<ResourceForm> forms = EnumSet.noneOf(ResourceForm.class);
    private final Map<ResourceForm, Supplier<? extends ItemLike>> delegates = new EnumMap<>(ResourceForm.class);

    public MaterialBuilder(String name) {
        this.name = name;
        this.displayName = capitalize(name);
    }

    public static MaterialBuilder of(String name) {
        return new MaterialBuilder(name);
    }

    public MaterialBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public MaterialBuilder type(MaterialType type) {
        this.type = type;
        return this;
    }

    public MaterialBuilder color(int colorHex) {
        this.colorHex = colorHex;
        return this;
    }

    public MaterialBuilder mapColor(MapColor mapColor) {
        this.mapColor = mapColor;
        return this;
    }

    public MaterialBuilder miningLevel(TagKey<Block> miningLevelTag) {
        this.miningLevelTag = miningLevelTag;
        return this;
    }

    public MaterialBuilder hardness(float hardness, float resistance) {
        this.hardness = hardness;
        this.resistance = resistance;
        return this;
    }

    public MaterialBuilder smeltingXp(float smeltingXp) {
        this.smeltingXp = smeltingXp;
        return this;
    }

    public MaterialBuilder forms(ResourceForm... forms) {
        this.forms.addAll(Arrays.asList(forms));
        return this;
    }

    public MaterialBuilder delegate(ResourceForm form, Supplier<? extends ItemLike> supplier) {
        this.forms.add(form);
        this.delegates.put(form, supplier);
        return this;
    }

    public MaterialBuilder oreDefaults() {
        return this.forms(
                ResourceForm.ORE,
                ResourceForm.DEEPSLATE_ORE,
                ResourceForm.RAW_ORE,
                ResourceForm.RAW_STORAGE_BLOCK,
                ResourceForm.STORAGE_BLOCK,
                ResourceForm.INGOT,
                ResourceForm.NUGGET,
                ResourceForm.DUST,
                ResourceForm.PLATE,
                ResourceForm.ROD,
                ResourceForm.GEAR
        );
    }

    public MaterialBuilder alloyDefaults() {
        this.type = MaterialType.ALLOY;
        return this.forms(
                ResourceForm.STORAGE_BLOCK,
                ResourceForm.INGOT,
                ResourceForm.NUGGET,
                ResourceForm.DUST,
                ResourceForm.PLATE,
                ResourceForm.ROD,
                ResourceForm.GEAR
        );
    }

    public Material buildAndRegister(DeferredRegister.Blocks blockRegister, DeferredRegister.Items itemRegister) {
        Map<ResourceForm, DeferredBlock<Block>> blockRegistry = new EnumMap<>(ResourceForm.class);
        Map<ResourceForm, DeferredItem<? extends Item>> itemRegistry = new EnumMap<>(ResourceForm.class);

        for (ResourceForm form : this.forms) {
            if (this.delegates.containsKey(form)) {
                continue;
            }

            String registryName = form.getRegistryName(this.name);

            if (form.isBlock()) {
                DeferredBlock<Block> deferredBlock = registerBlockForForm(form, registryName, blockRegister);
                blockRegistry.put(form, deferredBlock);
                DeferredItem<BlockItem> blockItem = itemRegister.registerSimpleBlockItem(registryName, deferredBlock);
                itemRegistry.put(form, blockItem);
            } else if (form.isItem()) {
                DeferredItem<Item> deferredItem = itemRegister.registerSimpleItem(registryName);
                itemRegistry.put(form, deferredItem);
            }
        }

        return new Material(
                this.name,
                this.displayName,
                this.type,
                this.colorHex,
                this.mapColor,
                this.miningLevelTag,
                this.hardness,
                this.resistance,
                this.smeltingXp,
                this.forms,
                blockRegistry,
                itemRegistry,
                this.delegates
        );
    }

    private DeferredBlock<Block> registerBlockForForm(ResourceForm form, String registryName, DeferredRegister.Blocks blockRegister) {
        return switch (form) {
            case DEEPSLATE_ORE -> blockRegister.registerSimpleBlock(registryName,
                    () -> BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .strength(this.hardness * 1.5f, this.resistance)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops());
            case ORE -> blockRegister.registerSimpleBlock(registryName,
                    () -> BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(this.hardness, this.resistance)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops());
            case RAW_STORAGE_BLOCK -> blockRegister.registerSimpleBlock(registryName,
                    () -> BlockBehaviour.Properties.of()
                            .mapColor(this.mapColor)
                            .strength(this.hardness, this.resistance)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops());
            default -> blockRegister.registerSimpleBlock(registryName,
                    () -> BlockBehaviour.Properties.of()
                            .mapColor(this.mapColor)
                            .strength(this.hardness, this.resistance)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops());
        };
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
