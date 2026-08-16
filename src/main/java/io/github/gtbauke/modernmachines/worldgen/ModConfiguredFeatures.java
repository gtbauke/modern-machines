package io.github.gtbauke.modernmachines.worldgen;

import java.util.List;
import java.util.Objects;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TIN = registerKey("ore_tin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LEAD = registerKey("ore_lead");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SILVER = registerKey("ore_silver");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_NICKEL = registerKey("ore_nickel");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ALUMINUM = registerKey("ore_aluminum");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_URANIUM = registerKey("ore_uranium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TITANIUM = registerKey("ore_titanium");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        registerOre(context, ORE_TIN, ModMaterials.TIN, stoneReplaceables, deepslateReplaceables, 9);
        registerOre(context, ORE_LEAD, ModMaterials.LEAD, stoneReplaceables, deepslateReplaceables, 8);
        registerOre(context, ORE_SILVER, ModMaterials.SILVER, stoneReplaceables, deepslateReplaceables, 7);
        registerOre(context, ORE_NICKEL, ModMaterials.NICKEL, stoneReplaceables, deepslateReplaceables, 7);
        registerOre(context, ORE_ALUMINUM, ModMaterials.ALUMINUM, stoneReplaceables, deepslateReplaceables, 8);
        registerOre(context, ORE_URANIUM, ModMaterials.URANIUM, stoneReplaceables, deepslateReplaceables, 4);
        registerOre(context, ORE_TITANIUM, ModMaterials.TITANIUM, stoneReplaceables, deepslateReplaceables, 4);
    }

    private static void registerOre(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            Material material,
            RuleTest stoneReplaceable,
            RuleTest deepslateReplaceable,
            int veinSize
    ) {
        List<OreConfiguration.TargetBlockState> targets = List.of(
                OreConfiguration.target(stoneReplaceable, Objects.requireNonNull(material.getBlock(ResourceForm.ORE)).defaultBlockState()),
                OreConfiguration.target(deepslateReplaceable, Objects.requireNonNull(material.getBlock(ResourceForm.DEEPSLATE_ORE)).defaultBlockState())
        );

        register(context, key, Feature.ORE, new OreConfiguration(targets, veinSize));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, name));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
