package io.github.gtbauke.modernmachines.worldgen;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_ORES = registerKey("add_ores");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_ORES = registerKey("add_nether_ores");
    public static final ResourceKey<BiomeModifier> ADD_END_ORES = registerKey("add_end_ores");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> overworldBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderSet<Biome> netherBiomes = biomes.getOrThrow(BiomeTags.IS_NETHER);
        HolderSet<Biome> endBiomes = biomes.getOrThrow(BiomeTags.IS_END);

        HolderSet<PlacedFeature> oreFeatures = HolderSet.direct(
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_TIN_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_LEAD_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_SILVER_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NICKEL_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_ALUMINUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_URANIUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_TITANIUM_PLACED)
        );

        HolderSet<PlacedFeature> netherOreFeatures = HolderSet.direct(
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_TIN_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_LEAD_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_SILVER_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_NICKEL_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_ALUMINUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_URANIUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NETHERRACK_TITANIUM_PLACED)
        );

        HolderSet<PlacedFeature> endOreFeatures = HolderSet.direct(
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_TIN_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_LEAD_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_SILVER_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_NICKEL_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_ALUMINUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_URANIUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_END_STONE_TITANIUM_PLACED)
        );

        context.register(
                ADD_ORES,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        overworldBiomes,
                        oreFeatures,
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(
                ADD_NETHER_ORES,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        netherBiomes,
                        netherOreFeatures,
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(
                ADD_END_ORES,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        endBiomes,
                        endOreFeatures,
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }

    public static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, name));
    }
}
