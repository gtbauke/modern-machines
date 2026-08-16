package io.github.gtbauke.modernmachines.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModernMachines.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        // Workstation Tags
        tag(BlockTags.MINEABLE_WITH_AXE).add(
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.PART_BUILDER.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.TINKERING_TABLE.get()).orElseThrow()
        );

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.ENGINEERS_TERMINAL.get()).orElseThrow()
        );

        for (Material material : ModMaterials.getAllMaterials()) {
            for (ResourceForm form : material.supportedForms()) {
                if (form.isBlock()) {
                    Block block = material.getBlock(form);
                    if (block != null) {
                        ResourceKey<Block> blockKey = BuiltInRegistries.BLOCK.getResourceKey(block).orElse(null);
                        if (blockKey != null) {
                            if (material.isRegisteredLocally(form)) {
                                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blockKey);

                                TagKey<Block> miningLevel = material.miningLevelTag();
                                if (miningLevel != null) {
                                    tag(miningLevel).add(blockKey);
                                }
                            }

                            // Specific and common plural tags
                            tag(material.getBlockTag(form)).add(blockKey);
                            tag(form.getPluralBlockTag()).addTag(material.getBlockTag(form));
                        }
                    }
                }
            }
        }
    }
}
