package io.github.gtbauke.modernmachines.core.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.MaterialBuilder;
import io.github.gtbauke.modernmachines.api.resource.MaterialType;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

public class ModMaterials {
    private static final Map<String, Material> MATERIALS_MAP = new LinkedHashMap<>();
    private static final List<Material> MATERIALS_LIST = new ArrayList<>();

    // Vanilla Extensions
    public static final Material IRON = register(
            MaterialBuilder.of("iron")
                    .displayName("Iron")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0xD8D8D8)
                    .mapColor(MapColor.METAL)
                    .miningLevel(BlockTags.NEEDS_STONE_TOOL)
                    .hardness(5.0f, 6.0f)
                    .delegate(ResourceForm.ORE, () -> Blocks.IRON_ORE)
                    .delegate(ResourceForm.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_IRON_ORE)
                    .delegate(ResourceForm.RAW_ORE, () -> Items.RAW_IRON)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.IRON_BLOCK)
                    .delegate(ResourceForm.RAW_STORAGE_BLOCK, () -> Blocks.RAW_IRON_BLOCK)
                    .delegate(ResourceForm.INGOT, () -> Items.IRON_INGOT)
                    .delegate(ResourceForm.NUGGET, () -> Items.IRON_NUGGET)
                    .forms(
                            ResourceForm.NETHERRACK_ORE,
                            ResourceForm.END_STONE_ORE,
                            ResourceForm.DUST,
                            ResourceForm.PLATE,
                            ResourceForm.ROD,
                            ResourceForm.SCREW,
                            ResourceForm.WIRE,
                            ResourceForm.GEAR
                    )
    );

    public static final Material GOLD = register(
            MaterialBuilder.of("gold")
                    .displayName("Gold")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0xFDF55F)
                    .mapColor(MapColor.GOLD)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(3.0f, 3.0f)
                    .delegate(ResourceForm.ORE, () -> Blocks.GOLD_ORE)
                    .delegate(ResourceForm.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_GOLD_ORE)
                    .delegate(ResourceForm.NETHERRACK_ORE, () -> Blocks.NETHER_GOLD_ORE)
                    .delegate(ResourceForm.RAW_ORE, () -> Items.RAW_GOLD)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.GOLD_BLOCK)
                    .delegate(ResourceForm.RAW_STORAGE_BLOCK, () -> Blocks.RAW_GOLD_BLOCK)
                    .delegate(ResourceForm.INGOT, () -> Items.GOLD_INGOT)
                    .delegate(ResourceForm.NUGGET, () -> Items.GOLD_NUGGET)
                    .forms(
                            ResourceForm.END_STONE_ORE,
                            ResourceForm.DUST,
                            ResourceForm.PLATE,
                            ResourceForm.ROD,
                            ResourceForm.SCREW,
                            ResourceForm.WIRE,
                            ResourceForm.GEAR
                    )
    );

    public static final Material COPPER = register(
            MaterialBuilder.of("copper")
                    .displayName("Copper")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0xE77C56)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .miningLevel(BlockTags.NEEDS_STONE_TOOL)
                    .hardness(3.0f, 3.0f)
                    .delegate(ResourceForm.ORE, () -> Blocks.COPPER_ORE)
                    .delegate(ResourceForm.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_COPPER_ORE)
                    .delegate(ResourceForm.RAW_ORE, () -> Items.RAW_COPPER)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.COPPER_BLOCK.weathering().unaffected())
                    .delegate(ResourceForm.RAW_STORAGE_BLOCK, () -> Blocks.RAW_COPPER_BLOCK)
                    .delegate(ResourceForm.INGOT, () -> Items.COPPER_INGOT)
                    .delegate(ResourceForm.NUGGET, () -> Items.COPPER_NUGGET)
                    .forms(
                            ResourceForm.NETHERRACK_ORE,
                            ResourceForm.END_STONE_ORE,
                            ResourceForm.DUST,
                            ResourceForm.PLATE,
                            ResourceForm.ROD,
                            ResourceForm.SCREW,
                            ResourceForm.WIRE,
                            ResourceForm.GEAR
                    )
    );

    public static final Material DIAMOND = register(
            MaterialBuilder.of("diamond")
                    .displayName("Diamond")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x4AEDD9)
                    .mapColor(MapColor.DIAMOND)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(5.0f, 6.0f)
                    .delegate(ResourceForm.ORE, () -> Blocks.DIAMOND_ORE)
                    .delegate(ResourceForm.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_DIAMOND_ORE)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.DIAMOND_BLOCK)
                    .delegate(ResourceForm.GEM, () -> Items.DIAMOND)
                    .forms(
                            ResourceForm.NETHERRACK_ORE,
                            ResourceForm.END_STONE_ORE,
                            ResourceForm.DUST,
                            ResourceForm.PLATE,
                            ResourceForm.ROD,
                            ResourceForm.GEAR
                    )
    );

    public static final Material LAPIS_LAZULI = register(
            MaterialBuilder.of("lapis_lazuli")
                    .displayName("Lapis Lazuli")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x2649B2)
                    .mapColor(MapColor.LAPIS)
                    .miningLevel(BlockTags.NEEDS_STONE_TOOL)
                    .hardness(3.0f, 3.0f)
                    .delegate(ResourceForm.ORE, () -> Blocks.LAPIS_ORE)
                    .delegate(ResourceForm.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_LAPIS_ORE)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.LAPIS_BLOCK)
                    .delegate(ResourceForm.GEM, () -> Items.LAPIS_LAZULI)
                    .forms(
                            ResourceForm.NETHERRACK_ORE,
                            ResourceForm.END_STONE_ORE,
                            ResourceForm.DUST,
                            ResourceForm.PLATE
                    )
    );

    public static final Material EMERALD = register(
            MaterialBuilder.of("emerald")
                    .displayName("Emerald")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x11B047)
                    .mapColor(MapColor.EMERALD)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(5.0f, 6.0f)
                    .delegate(ResourceForm.ORE, () -> Blocks.EMERALD_ORE)
                    .delegate(ResourceForm.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_EMERALD_ORE)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.EMERALD_BLOCK)
                    .delegate(ResourceForm.GEM, () -> Items.EMERALD)
                    .forms(
                            ResourceForm.NETHERRACK_ORE,
                            ResourceForm.END_STONE_ORE,
                            ResourceForm.DUST,
                            ResourceForm.PLATE,
                            ResourceForm.ROD,
                            ResourceForm.GEAR
                    )
    );

    public static final Material AMETHYST = register(
            MaterialBuilder.of("amethyst")
                    .displayName("Amethyst")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x9A5CC6)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(4.0f, 5.0f)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.AMETHYST_BLOCK)
                    .delegate(ResourceForm.GEM, () -> Items.AMETHYST_SHARD)
                    .forms(
                            ResourceForm.DUST,
                            ResourceForm.PLATE
                    )
    );

    public static final Material NETHERITE = register(
            MaterialBuilder.of("netherite")
                    .displayName("Netherite")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x4C444B)
                    .mapColor(MapColor.COLOR_BLACK)
                    .miningLevel(BlockTags.NEEDS_DIAMOND_TOOL)
                    .hardness(50.0f, 1200.0f)
                    .delegate(ResourceForm.STORAGE_BLOCK, () -> Blocks.NETHERITE_BLOCK)
                    .delegate(ResourceForm.INGOT, () -> Items.NETHERITE_INGOT)
                    .forms(
                            ResourceForm.DUST,
                            ResourceForm.PLATE,
                            ResourceForm.ROD,
                            ResourceForm.GEAR
                    )
    );

    // Custom Ores
    public static final Material TIN = register(
            MaterialBuilder.of("tin")
                    .displayName("Tin")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0xC0C8D0)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .miningLevel(BlockTags.NEEDS_STONE_TOOL)
                    .hardness(3.0f, 3.0f)
                    .smeltingXp(0.6f)
                    .oreDefaults()
                    .forms(ResourceForm.SCREW)
    );

    public static final Material LEAD = register(
            MaterialBuilder.of("lead")
                    .displayName("Lead")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0x545B73)
                    .mapColor(MapColor.COLOR_GRAY)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(3.5f, 3.5f)
                    .smeltingXp(0.8f)
                    .oreDefaults()
    );

    public static final Material SILVER = register(
            MaterialBuilder.of("silver")
                    .displayName("Silver")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0xE5F0F5)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(3.0f, 3.0f)
                    .smeltingXp(0.9f)
                    .oreDefaults()
                    .forms(ResourceForm.WIRE)
    );

    public static final Material NICKEL = register(
            MaterialBuilder.of("nickel")
                    .displayName("Nickel")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0xE0DDC5)
                    .mapColor(MapColor.SAND)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(3.5f, 3.5f)
                    .smeltingXp(0.7f)
                    .oreDefaults()
    );

    public static final Material ALUMINUM = register(
            MaterialBuilder.of("aluminum")
                    .displayName("Aluminum")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0xDCE4EC)
                    .mapColor(MapColor.SNOW)
                    .miningLevel(BlockTags.NEEDS_STONE_TOOL)
                    .hardness(3.0f, 3.0f)
                    .smeltingXp(0.6f)
                    .oreDefaults()
                    .forms(ResourceForm.WIRE)
    );

    public static final Material URANIUM = register(
            MaterialBuilder.of("uranium")
                    .displayName("Uranium")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0x438E37)
                    .mapColor(MapColor.COLOR_GREEN)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(4.5f, 5.0f)
                    .smeltingXp(1.2f)
                    .forms(
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
                            ResourceForm.ROD
                    )
    );

    public static final Material TITANIUM = register(
            MaterialBuilder.of("titanium")
                    .displayName("Titanium")
                    .type(MaterialType.METALLIC_ORE)
                    .color(0xC4B8C6)
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .miningLevel(BlockTags.NEEDS_DIAMOND_TOOL)
                    .hardness(6.0f, 8.0f)
                    .smeltingXp(1.5f)
                    .oreDefaults()
                    .forms(ResourceForm.SCREW)
    );

    // Custom Alloys
    public static final Material BRONZE = register(
            MaterialBuilder.of("bronze")
                    .displayName("Bronze")
                    .type(MaterialType.ALLOY)
                    .color(0xCD7F32)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(4.0f, 4.5f)
                    .alloyDefaults()
    );

    public static final Material STEEL = register(
            MaterialBuilder.of("steel")
                    .displayName("Steel")
                    .type(MaterialType.ALLOY)
                    .color(0x6A737D)
                    .mapColor(MapColor.METAL)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(5.0f, 6.0f)
                    .alloyDefaults()
                    .forms(ResourceForm.SCREW, ResourceForm.WIRE)
    );

    public static final Material INVAR = register(
            MaterialBuilder.of("invar")
                    .displayName("Invar")
                    .type(MaterialType.ALLOY)
                    .color(0x9E9E93)
                    .mapColor(MapColor.COLOR_GRAY)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(4.5f, 5.0f)
                    .alloyDefaults()
    );

    public static final Material ELECTRUM = register(
            MaterialBuilder.of("electrum")
                    .displayName("Electrum")
                    .type(MaterialType.ALLOY)
                    .color(0xF3DD60)
                    .mapColor(MapColor.GOLD)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(3.5f, 3.5f)
                    .alloyDefaults()
                    .forms(ResourceForm.WIRE)
    );

    public static final Material CONSTANTAN = register(
            MaterialBuilder.of("constantan")
                    .displayName("Constantan")
                    .type(MaterialType.ALLOY)
                    .color(0xD88E68)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .miningLevel(BlockTags.NEEDS_IRON_TOOL)
                    .hardness(3.5f, 3.5f)
                    .alloyDefaults()
                    .forms(ResourceForm.WIRE)
    );

    // Primitive Materials for Part Builder & Early-game Tooling
    public static final Material WOOD = register(
            MaterialBuilder.of("wood")
                    .displayName("Wood")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x855C38)
                    .mapColor(MapColor.WOOD)
                    .miningLevel(BlockTags.MINEABLE_WITH_AXE)
                    .hardness(2.0f, 2.0f)
                    .delegate(ResourceForm.ROD, () -> Items.STICK)
                    .delegate(ResourceForm.INGOT, () -> Blocks.OAK_PLANKS)
    );

    public static final Material STONE = register(
            MaterialBuilder.of("stone")
                    .displayName("Stone")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x7F7F7F)
                    .mapColor(MapColor.STONE)
                    .miningLevel(BlockTags.MINEABLE_WITH_PICKAXE)
                    .hardness(2.0f, 6.0f)
                    .delegate(ResourceForm.INGOT, () -> Blocks.COBBLESTONE)
    );

    public static final Material FLINT = register(
            MaterialBuilder.of("flint")
                    .displayName("Flint")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0x393939)
                    .mapColor(MapColor.COLOR_GRAY)
                    .miningLevel(BlockTags.MINEABLE_WITH_SHOVEL)
                    .hardness(1.0f, 1.0f)
                    .delegate(ResourceForm.INGOT, () -> Items.FLINT)
    );

    public static final Material BONE = register(
            MaterialBuilder.of("bone")
                    .displayName("Bone")
                    .type(MaterialType.VANILLA_EXTENSION)
                    .color(0xE1DEC3)
                    .mapColor(MapColor.SAND)
                    .miningLevel(BlockTags.MINEABLE_WITH_PICKAXE)
                    .hardness(2.0f, 2.0f)
                    .delegate(ResourceForm.ROD, () -> Items.BONE)
                    .delegate(ResourceForm.INGOT, () -> Items.BONE_MEAL)
    );

    private static Material register(MaterialBuilder builder) {
        Material material = builder.buildAndRegister(ModBlocks.BLOCKS, ModItems.ITEMS);
        MATERIALS_MAP.put(material.name(), material);
        MATERIALS_LIST.add(material);
        return material;
    }

    public static List<Material> getAllMaterials() {
        return Collections.unmodifiableList(MATERIALS_LIST);
    }

    public static Material getByName(String name) {
        return MATERIALS_MAP.get(name);
    }

    public static void init() {
        // Classloading trigger to register all materials
    }
}
