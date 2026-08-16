package io.github.gtbauke.modernmachines.datagen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

public class ModBlockLootSubProvider extends BlockLootSubProvider {

    public ModBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Workstations
        dropSelf(ModBlocks.PART_BUILDER.get());
        dropSelf(ModBlocks.TINKERING_TABLE.get());
        dropSelf(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get());
        dropSelf(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());
        dropSelf(ModBlocks.ENGINEERS_TERMINAL.get());

        for (Material material : ModMaterials.getAllMaterials()) {
            // Storage blocks
            if (material.isRegisteredLocally(ResourceForm.STORAGE_BLOCK)) {
                dropSelf(material.getBlock(ResourceForm.STORAGE_BLOCK));
            }
            if (material.isRegisteredLocally(ResourceForm.RAW_STORAGE_BLOCK)) {
                dropSelf(material.getBlock(ResourceForm.RAW_STORAGE_BLOCK));
            }

            // Ores
            Item dropItem = material.getItem(ResourceForm.RAW_ORE);
            if (dropItem == null) {
                dropItem = material.getItem(ResourceForm.GEM);
            }
            if (dropItem == null) {
                dropItem = material.getItem(ResourceForm.INGOT);
            }

            if (material.isRegisteredLocally(ResourceForm.ORE)) {
                Block oreBlock = material.getBlock(ResourceForm.ORE);
                if (dropItem != null) {
                    add(oreBlock, createOreDrop(oreBlock, dropItem));
                } else {
                    dropSelf(oreBlock);
                }
            }

            if (material.isRegisteredLocally(ResourceForm.DEEPSLATE_ORE)) {
                Block deepslateOreBlock = material.getBlock(ResourceForm.DEEPSLATE_ORE);
                if (dropItem != null) {
                    add(deepslateOreBlock, createOreDrop(deepslateOreBlock, dropItem));
                } else {
                    dropSelf(deepslateOreBlock);
                }
            }
        }
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks() {
        Set<Block> knownBlocks = new HashSet<>();
        knownBlocks.add(ModBlocks.PART_BUILDER.get());
        knownBlocks.add(ModBlocks.TINKERING_TABLE.get());
        knownBlocks.add(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get());
        knownBlocks.add(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());
        knownBlocks.add(ModBlocks.ENGINEERS_TERMINAL.get());

        for (Material material : ModMaterials.getAllMaterials()) {
            for (ResourceForm form : material.supportedForms()) {
                if (material.isRegisteredLocally(form) && form.isBlock()) {
                    Block block = material.getBlock(form);
                    if (block != null) {
                        knownBlocks.add(block);
                    }
                }
            }
        }
        return knownBlocks;
    }
}
