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

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> overworldBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

        HolderSet<PlacedFeature> oreFeatures = HolderSet.direct(
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_TIN_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_LEAD_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_SILVER_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_NICKEL_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_ALUMINUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_URANIUM_PLACED),
                placedFeatures.getOrThrow(ModPlacedFeatures.ORE_TITANIUM_PLACED)
        );

        context.register(
                ADD_ORES,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        overworldBiomes,
                        oreFeatures,
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }

    public static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, name));
    }
}
