package io.github.gtbauke.modernmachines.worldgen;

import java.util.List;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ORE_TIN_PLACED = registerKey("ore_tin_placed");
    public static final ResourceKey<PlacedFeature> ORE_LEAD_PLACED = registerKey("ore_lead_placed");
    public static final ResourceKey<PlacedFeature> ORE_SILVER_PLACED = registerKey("ore_silver_placed");
    public static final ResourceKey<PlacedFeature> ORE_NICKEL_PLACED = registerKey("ore_nickel_placed");
    public static final ResourceKey<PlacedFeature> ORE_ALUMINUM_PLACED = registerKey("ore_aluminum_placed");
    public static final ResourceKey<PlacedFeature> ORE_URANIUM_PLACED = registerKey("ore_uranium_placed");
    public static final ResourceKey<PlacedFeature> ORE_TITANIUM_PLACED = registerKey("ore_titanium_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Tin: Abundant mid-to-high elevations (vein size 9, 14 veins/chunk, Y: -20 to 112, peak at 48)
        register(context, ORE_TIN_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_TIN),
                commonOrePlacement(14, HeightRangePlacement.triangle(VerticalAnchor.absolute(-20), VerticalAnchor.absolute(112))));

        // Lead: Mid elevations (vein size 8, 8 veins/chunk, Y: -64 to 64, peak at 0)
        register(context, ORE_LEAD_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_LEAD),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64))));

        // Silver: Rare mid-to-deep elevations (vein size 7, 6 veins/chunk, Y: -64 to 32, peak at -16)
        register(context, ORE_SILVER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_SILVER),
                commonOrePlacement(6, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));

        // Nickel: Deep-to-mid elevations (vein size 7, 6 veins/chunk, Y: -48 to 48, peak at 0)
        register(context, ORE_NICKEL_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_NICKEL),
                commonOrePlacement(6, HeightRangePlacement.triangle(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(48))));

        // Aluminum: Wide distribution (vein size 8, 10 veins/chunk, Y: -16 to 128, peak at 64)
        register(context, ORE_ALUMINUM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_ALUMINUM),
                commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(128))));

        // Uranium: Rare deep elevations (vein size 4, 3 veins/chunk, Y: -64 to 0, peak at -32)
        register(context, ORE_URANIUM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_URANIUM),
                commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        // Titanium: Rare very deep elevations (vein size 4, 3 veins/chunk, Y: -64 to -16, peak at -48)
        register(context, ORE_TITANIUM_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ORE_TITANIUM),
                commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-16))));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier heightRange) {
        return List.of(count, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, name));
    }

    public static void register(
            BootstrapContext<PlacedFeature> context,
            ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers
    ) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
