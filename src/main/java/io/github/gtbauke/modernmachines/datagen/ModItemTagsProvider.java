package io.github.gtbauke.modernmachines.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import org.jspecify.annotations.NonNull;

public class ModItemTagsProvider extends BlockTagCopyingItemTagProvider {

    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags
    ) {
        super(output, lookupProvider, blockTags, ModernMachines.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        for (Material material : ModMaterials.getAllMaterials()) {
            for (ResourceForm form : material.supportedForms()) {
                if (form.isBlock()) {
                    copy(material.getBlockTag(form), material.getItemTag(form));
                    copy(form.getPluralBlockTag(), form.getPluralItemTag());
                } else {
                    Item item = material.getItem(form);

                    if (item != null) {
                        ResourceKey<Item> itemKey = BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);

                        if (itemKey != null) {
                            tag(material.getItemTag(form)).add(itemKey);
                            tag(form.getPluralItemTag()).addTag(material.getItemTag(form));
                        }
                    }
                }
            }
        }
    }
}
