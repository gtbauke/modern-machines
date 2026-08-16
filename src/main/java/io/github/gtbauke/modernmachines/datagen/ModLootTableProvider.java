package io.github.gtbauke.modernmachines.datagen;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(
                output,
                Collections.emptySet(),
                List.of(new SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
                registries
        );
    }
}
