package io.github.gtbauke.modernmachines.datagen;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import io.github.gtbauke.modernmachines.modular.client.tint.ModularToolPartTintSource;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput packOutput) {
        super(packOutput, ModernMachines.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Register manual crafting tools
        itemModels.generateFlatItem(ModItems.ENGINEER_HAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.WIRE_CUTTER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        // Register machine upgrades
        itemModels.generateFlatItem(ModItems.SPEED_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ENERGY_EFFICIENCY_UPGRADE.get(), ModelTemplates.FLAT_ITEM);

        // 1. Part Builder Model & Blockstate (Cube Bottom Top: top, bottom, side)
        TextureMapping partBuilderMapping = TextureMapping.cubeBottomTop(ModBlocks.PART_BUILDER.get());
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ModBlocks.PART_BUILDER.get(),
                BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(ModBlocks.PART_BUILDER.get(), partBuilderMapping, blockModels.modelOutput))
        ));

        // 2. Tinkering Table Model & Blockstate (Cube Bottom Top: top, bottom, side)
        TextureMapping tinkeringMapping = TextureMapping.cubeBottomTop(ModBlocks.TINKERING_TABLE.get());
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ModBlocks.TINKERING_TABLE.get(),
                BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(ModBlocks.TINKERING_TABLE.get(), tinkeringMapping, blockModels.modelOutput))
        ));

        // 3. Basic Alloy Smelter Multiblock (Furnace: Orientable horizontal facing + unlit / lit states)
        blockModels.createFurnace(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFurnace(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get(), TexturedModel.ORIENTABLE_ONLY_TOP);

        // 4. Engineer's Terminal Model & Blockstate (Cube Bottom Top: top, bottom, side)
        TextureMapping terminalMapping = TextureMapping.cubeBottomTop(ModBlocks.ENGINEERS_TERMINAL.get());
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ModBlocks.ENGINEERS_TERMINAL.get(),
                BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(ModBlocks.ENGINEERS_TERMINAL.get(), terminalMapping, blockModels.modelOutput))
        ));

        // Engineer's Tablet Item
        itemModels.generateFlatItem(ModItems.ENGINEERS_TABLET.get(), ModelTemplates.FLAT_ITEM);

        // Register Patterns
        itemModels.generateFlatItem(ModItems.BLANK_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PICKAXE_HEAD_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.AXE_HEAD_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SHOVEL_HEAD_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SWORD_BLADE_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.HOE_HEAD_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.HANDLE_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BINDING_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TIP_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GRIP_PATTERN.get(), ModelTemplates.FLAT_ITEM);

        // -------------------------------------------------------------
        // Modular Tools (Multi-Layer Tinted Handheld Models)
        // -------------------------------------------------------------
        ModelTemplate threeLayerHandheld = new ModelTemplate(
                java.util.Optional.of(Identifier.withDefaultNamespace("item/handheld")),
                java.util.Optional.empty(),
                TextureSlot.LAYER0,
                TextureSlot.LAYER1,
                TextureSlot.LAYER2
        );

        Identifier handleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/handle");
        Identifier bindingTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/binding");
        Identifier guardTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/sword_guard");

        registerModularToolModel(itemModels, ModItems.MODULAR_PICKAXE.get(), threeLayerHandheld,
                handleTex, bindingTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/pickaxe_head"));

        registerModularToolModel(itemModels, ModItems.MODULAR_AXE.get(), threeLayerHandheld,
                handleTex, bindingTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/axe_head"));

        registerModularToolModel(itemModels, ModItems.MODULAR_SHOVEL.get(), threeLayerHandheld,
                handleTex, bindingTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/shovel_head"));

        registerModularToolModel(itemModels, ModItems.MODULAR_SWORD.get(), threeLayerHandheld,
                handleTex, guardTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/sword_blade"));

        registerModularToolModel(itemModels, ModItems.MODULAR_HOE.get(), threeLayerHandheld,
                handleTex, bindingTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/hoe_head"));

        // -------------------------------------------------------------
        // Dynamic Template Layered Tinting for Tool Parts
        // -------------------------------------------------------------
        for (ToolPartType partType : ToolPartType.values()) {
            Identifier partTemplateModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/" + partType.getSerializedName());
            Identifier partTextureId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/" + partType.getSerializedName());
            net.minecraft.client.resources.model.sprite.Material spriteMat = new net.minecraft.client.resources.model.sprite.Material(partTextureId);
            ModelTemplates.FLAT_ITEM.create(partTemplateModelId, TextureMapping.layer0(spriteMat), itemModels.modelOutput);
        }

        for (Material material : ModMaterials.getAllMaterials()) {
            int tintColor = 0xFF000000 | material.colorHex();
            for (ToolPartType partType : ToolPartType.values()) {
                ToolPartItem partItem = ModItems.getToolPart(partType, material);
                if (partItem != null) {
                    Identifier partTemplateModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/" + partType.getSerializedName());
                    itemModels.itemModelOutput.accept(partItem, ItemModelUtils.tintedModel(partTemplateModelId, ItemModelUtils.constantTint(tintColor)));
                }
            }
        }

        // -------------------------------------------------------------
        // Dynamic Template Layered Tinting for Resource Items
        // -------------------------------------------------------------
        Set<ResourceForm> generatedItemTemplates = new HashSet<>();
        for (Material material : ModMaterials.getAllMaterials()) {
            for (ResourceForm form : material.supportedForms()) {
                if (form.isItem() && material.isRegisteredLocally(form)) {
                    String formName = form.name().toLowerCase(Locale.ROOT);
                    if (!generatedItemTemplates.contains(form)) {
                        generatedItemTemplates.add(form);
                        Identifier itemTemplateModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/" + formName);
                        Identifier itemTextureId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/" + formName);
                        net.minecraft.client.resources.model.sprite.Material spriteMat = new net.minecraft.client.resources.model.sprite.Material(itemTextureId);
                        ModelTemplates.FLAT_ITEM.create(itemTemplateModelId, TextureMapping.layer0(spriteMat), itemModels.modelOutput);
                    }

                    Item item = material.getItem(form);
                    if (item != null) {
                        int tintColor = 0xFF000000 | material.colorHex();
                        Identifier itemTemplateModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/" + formName);
                        itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(itemTemplateModelId, ItemModelUtils.constantTint(tintColor)));
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // Dynamic Template Tinting for Resource Blocks
        // -------------------------------------------------------------
        for (Material material : ModMaterials.getAllMaterials()) {
            int tintColor = 0xFF000000 | material.colorHex();
            for (ResourceForm form : material.supportedForms()) {
                if (form.isBlock() && material.isRegisteredLocally(form)) {
                    Block block = material.getBlock(form);
                    if (block != null) {
                        Identifier templateBlockModelId;
                        if (form == ResourceForm.ORE) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/stone_ore");
                        } else if (form == ResourceForm.DEEPSLATE_ORE) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/deepslate_ore");
                        } else if (form == ResourceForm.STORAGE_BLOCK) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/storage_block");
                        } else if (form == ResourceForm.RAW_STORAGE_BLOCK) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/raw_storage_block");
                        } else {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/storage_block");
                        }

                        // Blockstate pointing to template block model with tintindex
                        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                                block,
                                BlockModelGenerators.plainVariant(templateBlockModelId)
                        ));

                        // Block item model with constant tint
                        itemModels.itemModelOutput.accept(
                                block.asItem(),
                                ItemModelUtils.tintedModel(templateBlockModelId, ItemModelUtils.constantTint(tintColor))
                        );
                    }
                }
            }
        }
    }

    private void registerModularToolModel(ItemModelGenerators itemModels, Item toolItem, ModelTemplate template,
                                          Identifier handleTexture, Identifier bindingTexture, Identifier headTexture) {
        Identifier modelId = ModelLocationUtils.getModelLocation(toolItem);
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, new net.minecraft.client.resources.model.sprite.Material(handleTexture))
                .put(TextureSlot.LAYER1, new net.minecraft.client.resources.model.sprite.Material(bindingTexture))
                .put(TextureSlot.LAYER2, new net.minecraft.client.resources.model.sprite.Material(headTexture));

        template.create(modelId, mapping, itemModels.modelOutput);

        itemModels.itemModelOutput.accept(
                toolItem,
                ItemModelUtils.tintedModel(
                        modelId,
                        new ModularToolPartTintSource(PartSlot.HANDLE),
                        new ModularToolPartTintSource(PartSlot.BINDING),
                        new ModularToolPartTintSource(PartSlot.HEAD)
                )
        );
    }
}
