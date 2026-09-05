package io.github.gtbauke.modernmachines.worldgen.feature;

import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class LargeOreVeinFeature extends Feature<LargeOreVeinConfiguration> {
    public static final TagKey<Block> LARGE_VEIN_REPLACEABLE = TagKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "large_vein_replaceable")
    );

    public LargeOreVeinFeature(Codec<LargeOreVeinConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NonNull FeaturePlaceContext<LargeOreVeinConfiguration> context) {
        var level = context.level();
        var origin = context.origin();
        var random = context.random();
        var config = context.config();

        int chunkStartX = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(origin.getX()));
        int chunkStartZ = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(origin.getZ()));

        int minY = Math.max(config.minY(), level.getMinY());
        int maxY = Math.min(config.maxY(), level.getMaxY());
        if (minY >= maxY) {
            return false;
        }

        var noiseRandom1 = RandomSource.create(level.getSeed() ^ 0x4B3A2C1DL);
        var noiseRandom2 = RandomSource.create(level.getSeed() ^ 0x9E3779B9L);
        var noise1 = NormalNoise.create(noiseRandom1, -3, 1.0, 0.5, 0.25);
        var noise2 = NormalNoise.create(noiseRandom2, -3, 1.0, 0.5, 0.25);

        double frequency = 1.0 / (double) Math.max(16, config.noiseLength());
        float thicknessFactor = (float) config.noiseThickness() / 16.0f;
        double veinWidth = 0.18 * thicknessFactor;
        double densityThreshold = 0.35 - (config.noiseDensity() * 0.2);

        boolean placedAny = false;
        var mutablePos = new BlockPos.MutableBlockPos();

        for (int x = chunkStartX; x < chunkStartX + 16; x++) {
            for (int z = chunkStartZ; z < chunkStartZ + 16; z++) {
                for (int y = minY; y <= maxY; y++) {
                    double nx = x * frequency;
                    double ny = y * frequency * 1.5;
                    double nz = z * frequency;

                    double sample1 = noise1.getValue(nx, ny, nz);
                    if (Math.abs(sample1) > veinWidth) {
                        continue;
                    }

                    double sample2 = noise2.getValue(nx, ny, nz);
                    if (sample2 < densityThreshold) {
                        continue;
                    }

                    mutablePos.set(x, y, z);
                    var currentState = level.getBlockState(mutablePos);
                    if (!isReplaceable(currentState)) {
                        continue;
                    }

                    var chosenState = selectVeinBlock(config, currentState, y, random);
                    level.setBlock(mutablePos, chosenState, Block.UPDATE_CLIENTS);
                    placedAny = true;
                }
            }
        }

        if (placedAny && config.surfaceIndicatorBlock().isPresent() && config.surfaceIndicatorChance() > 0.0f) {
            placeSurfaceIndicators(level, config, chunkStartX, chunkStartZ, random);
        }

        return placedAny;
    }

    private static @NonNull BlockState selectVeinBlock(
            LargeOreVeinConfiguration config,
            BlockState currentState,
            int y,
            RandomSource random
    ) {
        float roll = random.nextFloat();
        boolean isDeepslate = y < 0 || currentState.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES) || currentState.is(Blocks.DEEPSLATE);

        if (roll < config.rawBlockChance() && config.rawBlock().isPresent()) {
            return config.rawBlock().get();
        }

        float secondaryThreshold = config.rawBlockChance() + config.secondaryOreChance();
        if (roll < secondaryThreshold && config.secondaryOre().isPresent()) {
            if (isDeepslate && config.secondaryDeepslateOre().isPresent()) {
                return config.secondaryDeepslateOre().get();
            }

            return config.secondaryOre().get();
        }

        float primaryThreshold = secondaryThreshold + config.primaryOreChance();
        if (roll < primaryThreshold) {
            if (isDeepslate && config.primaryDeepslateOre().isPresent()) {
                return config.primaryDeepslateOre().get();
            }

            return config.primaryOre();
        }

        return config.fillerBlock();
    }

    private static void placeSurfaceIndicators(
            WorldGenLevel level,
            LargeOreVeinConfiguration config,
            int chunkStartX,
            int chunkStartZ,
            RandomSource random
    ) {
        if (random.nextFloat() >= config.surfaceIndicatorChance()) {
            return;
        }

        var indicatorState = config.surfaceIndicatorBlock().get();
        int count = 1 + random.nextInt(4);
        var mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < count; i++) {
            int indX = chunkStartX + random.nextInt(16);
            int indZ = chunkStartZ + random.nextInt(16);
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, indX, indZ);

            mutablePos.set(indX, surfaceY, indZ);
            var groundPos = mutablePos.below();
            var groundState = level.getBlockState(groundPos);
            var surfaceState = level.getBlockState(mutablePos);

            if (groundState.isSolid() && !groundState.is(Blocks.BEDROCK) && !groundState.hasBlockEntity() && surfaceState.isAir()) {
                level.setBlock(groundPos, indicatorState, Block.UPDATE_CLIENTS);
            }
        }
    }

    private static boolean isReplaceable(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty() || state.is(Blocks.BEDROCK) || state.hasBlockEntity()) {
            return false;
        }

        if (state.is(LARGE_VEIN_REPLACEABLE)) {
            return true;
        }

        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.BASE_STONE_NETHER)
                || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || state.is(Blocks.END_STONE)
                || state.is(Blocks.NETHERRACK)
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.TUFF);
    }
}
