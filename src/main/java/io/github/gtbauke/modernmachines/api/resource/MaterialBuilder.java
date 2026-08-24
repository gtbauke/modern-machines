package io.github.gtbauke.modernmachines.api.resource;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.core.registry.ModFluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
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
    private int meltingPoint = 1000;
    private int overlayIndex;
    private final Set<ResourceForm> forms = EnumSet.noneOf(ResourceForm.class);
    private final Map<ResourceForm, Supplier<? extends ItemLike>> delegates = new EnumMap<>(ResourceForm.class);

    public MaterialBuilder(String name) {
        this.name = name;
        this.displayName = capitalize(name);
        this.overlayIndex = (Math.abs(name.hashCode()) % 10) + 1;
    }

    public static MaterialBuilder of(String name) {
        return new MaterialBuilder(name);
    }

    public MaterialBuilder overlayIndex(int overlayIndex) {
        this.overlayIndex = overlayIndex;
        return this;
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

    public MaterialBuilder meltingPoint(int meltingPoint) {
        this.meltingPoint = meltingPoint;
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
                ResourceForm.NETHERRACK_ORE,
                ResourceForm.END_STONE_ORE,
                ResourceForm.RAW_ORE,
                ResourceForm.RAW_STORAGE_BLOCK,
                ResourceForm.STORAGE_BLOCK,
                ResourceForm.INGOT,
                ResourceForm.NUGGET,
                ResourceForm.DUST,
                ResourceForm.PLATE,
                ResourceForm.ROD,
                ResourceForm.GEAR,
                ResourceForm.MOLTEN
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
                ResourceForm.GEAR,
                ResourceForm.MOLTEN
        );
    }

    public Material buildAndRegister(DeferredRegister.Blocks blockRegister, DeferredRegister.Items itemRegister) {
        return buildAndRegister(blockRegister, itemRegister, ModFluids.FLUID_TYPES, ModFluids.FLUIDS);
    }

    @SuppressWarnings("unchecked")
    public Material buildAndRegister(
            DeferredRegister.Blocks blockRegister,
            DeferredRegister.Items itemRegister,
            DeferredRegister<FluidType> fluidTypeRegister,
            DeferredRegister<Fluid> fluidRegister
    ) {
        var blockRegistry = new EnumMap<ResourceForm, DeferredBlock<Block>>(ResourceForm.class);
        var itemRegistry = new EnumMap<ResourceForm, DeferredItem<? extends Item>>(ResourceForm.class);
        var fluidTypeRegistry = new EnumMap<ResourceForm, Supplier<? extends FluidType>>(ResourceForm.class);
        var fluidSourceRegistry = new EnumMap<ResourceForm, Supplier<? extends FlowingFluid>>(ResourceForm.class);
        var fluidFlowingRegistry = new EnumMap<ResourceForm, Supplier<? extends FlowingFluid>>(ResourceForm.class);

        for (var form : this.forms) {
            if (this.delegates.containsKey(form)) {
                continue;
            }

            var registryName = form.getRegistryName(this.name);

            if (form.isFluid()) {
                var fluidType = fluidTypeRegister.register(registryName, () ->
                        new FluidType(FluidType.Properties.create()
                                .density(3000)
                                .temperature(this.meltingPoint + 273)
                                .viscosity(6000)
                                .descriptionId("fluid_type." + ModernMachines.MOD_ID + "." + registryName)));
                fluidTypeRegistry.put(form, fluidType);

                @SuppressWarnings("unchecked")
                Supplier<FlowingFluid>[] sourceHolder = new Supplier[1];
                @SuppressWarnings("unchecked")
                Supplier<FlowingFluid>[] flowingHolder = new Supplier[1];
                @SuppressWarnings("unchecked")
                DeferredBlock<Block>[] blockHolder = new DeferredBlock[1];
                @SuppressWarnings("unchecked")
                DeferredItem<BucketItem>[] bucketHolder = new DeferredItem[1];

                var properties = new BaseFlowingFluid.Properties(
                        fluidType,
                        () -> sourceHolder[0].get(),
                        () -> flowingHolder[0].get()
                ).block(() -> (LiquidBlock) blockHolder[0].get()).bucket(() -> bucketHolder[0].get());

                sourceHolder[0] = fluidRegister.register(registryName, () -> new BaseFlowingFluid.Source(properties));
                fluidSourceRegistry.put(form, sourceHolder[0]);

                flowingHolder[0] = fluidRegister.register(registryName + "_flowing", () -> new BaseFlowingFluid.Flowing(properties));
                fluidFlowingRegistry.put(form, flowingHolder[0]);

                blockHolder[0] = blockRegister.registerBlock(registryName,
                        props -> new LiquidBlock(sourceHolder[0].get(), props),
                        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));
                blockRegistry.put(form, blockHolder[0]);

                bucketHolder[0] = itemRegister.registerItem(registryName + "_bucket",
                        props -> new BucketItem(sourceHolder[0].get(), props),
                        p -> p.craftRemainder(Items.BUCKET).stacksTo(1));
                itemRegistry.put(form, (DeferredItem<? extends Item>) (DeferredItem<?>) bucketHolder[0]);
            } else if (form.isBlock()) {
                var deferredBlock = registerBlockForForm(form, registryName, blockRegister);
                blockRegistry.put(form, deferredBlock);
                var blockItem = itemRegister.registerSimpleBlockItem(registryName, deferredBlock);
                itemRegistry.put(form, blockItem);
            } else if (form.isItem()) {
                var deferredItem = itemRegister.registerSimpleItem(registryName);
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
                this.meltingPoint,
                this.overlayIndex,
                this.forms,
                blockRegistry,
                itemRegistry,
                fluidTypeRegistry,
                fluidSourceRegistry,
                fluidFlowingRegistry,
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
            case NETHERRACK_ORE -> blockRegister.registerSimpleBlock(registryName,
                    () -> BlockBehaviour.Properties.of()
                            .mapColor(MapColor.NETHER)
                            .strength(Math.max(0.4f, this.hardness * 0.8f), this.resistance)
                            .sound(SoundType.NETHERRACK)
                            .requiresCorrectToolForDrops());
            case END_STONE_ORE -> blockRegister.registerSimpleBlock(registryName,
                    () -> BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SAND)
                            .strength(this.hardness * 1.5f, Math.max(9.0f, this.resistance * 1.5f))
                            .sound(SoundType.STONE)
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
