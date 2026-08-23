package io.github.gtbauke.modernmachines.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

public class ModFluidTagsProvider extends TagsProvider<Fluid> {

    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.FLUID, lookupProvider, ModernMachines.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        for (Material material : ModMaterials.getAllMaterials()) {
            if (material.hasForm(ResourceForm.MOLTEN) && material.isRegisteredLocally(ResourceForm.MOLTEN)) {
                Fluid sourceFluid = material.getFluid(ResourceForm.MOLTEN);
                Fluid flowingFluid = material.getFlowingFluid(ResourceForm.MOLTEN);
                if (sourceFluid != null) {
                    ResourceKey<Fluid> sourceKey = BuiltInRegistries.FLUID.getResourceKey(sourceFluid).orElse(null);
                    ResourceKey<Fluid> flowingKey = flowingFluid != null ? BuiltInRegistries.FLUID.getResourceKey(flowingFluid).orElse(null) : null;
                    if (sourceKey != null) {
                        var fluidTag = tag(material.getFluidTag(ResourceForm.MOLTEN)).add(sourceKey);
                        if (flowingKey != null) {
                            fluidTag.add(flowingKey);
                        }
                        tag(ResourceForm.MOLTEN.getPluralFluidTag()).addTag(material.getFluidTag(ResourceForm.MOLTEN));
                    }
                }
            }
        }
    }
}
