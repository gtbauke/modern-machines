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
import com.mojang.math.Quadrant;
import io.github.gtbauke.modernmachines.machine.block.CopperPipeBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
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
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");

    public ModModelProvider(PackOutput packOutput) {
        super(packOutput, ModernMachines.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Register manual crafting tools
        itemModels.generateFlatItem(ModItems.ENGINEER_HAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.WIRE_CUTTER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.ENGINEERS_TABLET.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.ADOBE_BRICKS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ADOBE_MIXTURE.get(), ModelTemplates.FLAT_ITEM);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ModBlocks.ADOBE_BRICK.get(),
                BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create(ModBlocks.ADOBE_BRICK.get(), TextureMapping.cube(ModBlocks.ADOBE_BRICK.get()), blockModels.modelOutput))
        ));

        // Copper Pipe Model & Blockstate (Multipart: Center + 6 Directional Arms)
        Identifier pipeCenterId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/pipe_center");
        Identifier pipeArmId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/pipe_arm");

        MultiVariant center = BlockModelGenerators.plainVariant(pipeCenterId);
        MultiVariant armNorth = BlockModelGenerators.plainVariant(pipeArmId);
        MultiVariant armEast = armNorth.with(VariantMutator.Y_ROT.withValue(Quadrant.R90));
        MultiVariant armSouth = armNorth.with(VariantMutator.Y_ROT.withValue(Quadrant.R180));
        MultiVariant armWest = armNorth.with(VariantMutator.Y_ROT.withValue(Quadrant.R270));
        MultiVariant armUp = armNorth.with(VariantMutator.X_ROT.withValue(Quadrant.R270));
        MultiVariant armDown = armNorth.with(VariantMutator.X_ROT.withValue(Quadrant.R90));

        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(ModBlocks.COPPER_PIPE.get())
                .with(center)
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true), armNorth)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true), armEast)
                .with(new ConditionBuilder().term(CopperPipeBlock.SOUTH, true), armSouth)
                .with(new ConditionBuilder().term(CopperPipeBlock.WEST, true), armWest)
                .with(new ConditionBuilder().term(CopperPipeBlock.UP, true), armUp)
                .with(new ConditionBuilder().term(CopperPipeBlock.DOWN, true), armDown)
        );
        itemModels.itemModelOutput.accept(
                ModItems.COPPER_PIPE.get().asItem(),
                ItemModelUtils.plainModel(pipeCenterId)
        );

        // Steel Pipe Model & Blockstate (Multipart: Center + 6 Directional Arms with Steel Tint)
        Identifier steelPipeCenterId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/steel_pipe_center");
        Identifier steelPipeArmId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/steel_pipe_arm");

        MultiVariant steelCenter = BlockModelGenerators.plainVariant(steelPipeCenterId);
        MultiVariant steelArmNorth = BlockModelGenerators.plainVariant(steelPipeArmId);
        MultiVariant steelArmEast = steelArmNorth.with(VariantMutator.Y_ROT.withValue(Quadrant.R90));
        MultiVariant steelArmSouth = steelArmNorth.with(VariantMutator.Y_ROT.withValue(Quadrant.R180));
        MultiVariant steelArmWest = steelArmNorth.with(VariantMutator.Y_ROT.withValue(Quadrant.R270));
        MultiVariant steelArmUp = steelArmNorth.with(VariantMutator.X_ROT.withValue(Quadrant.R270));
        MultiVariant steelArmDown = steelArmNorth.with(VariantMutator.X_ROT.withValue(Quadrant.R90));

        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(ModBlocks.STEEL_PIPE.get())
                .with(steelCenter)
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true), steelArmNorth)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true), steelArmEast)
                .with(new ConditionBuilder().term(CopperPipeBlock.SOUTH, true), steelArmSouth)
                .with(new ConditionBuilder().term(CopperPipeBlock.WEST, true), steelArmWest)
                .with(new ConditionBuilder().term(CopperPipeBlock.UP, true), steelArmUp)
                .with(new ConditionBuilder().term(CopperPipeBlock.DOWN, true), steelArmDown)
        );
        itemModels.itemModelOutput.accept(
                ModItems.STEEL_PIPE.get().asItem(),
                ItemModelUtils.tintedModel(steelPipeCenterId, ItemModelUtils.constantTint(0xFF000000 | ModMaterials.STEEL.colorHex()))
        );

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

        ModelTemplate orientableTemplate = new ModelTemplate(
                java.util.Optional.of(Identifier.withDefaultNamespace("block/orientable")),
                java.util.Optional.empty(),
                TextureSlot.TOP,
                TextureSlot.SIDE,
                TextureSlot.FRONT
        );

        TexturedModel.Provider heaterModelProvider = block -> {
            Identifier adobeBrickTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/adobe_brick");
            Identifier heaterFrontOff = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/basic_alloy_smelter_heater");
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.FRONT, new net.minecraft.client.resources.model.sprite.Material(heaterFrontOff))
                    .put(TextureSlot.SIDE, new net.minecraft.client.resources.model.sprite.Material(adobeBrickTex))
                    .put(TextureSlot.TOP, new net.minecraft.client.resources.model.sprite.Material(adobeBrickTex));
            return new TexturedModel(mapping, orientableTemplate);
        };

        blockModels.createFurnace(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get(), heaterModelProvider);

        // 4. Engineer's Terminal Model & Blockstate (Cube Bottom Top: top, bottom, side)
        TextureMapping terminalMapping = TextureMapping.cubeBottomTop(ModBlocks.ENGINEERS_TERMINAL.get());
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ModBlocks.ENGINEERS_TERMINAL.get(),
                BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(ModBlocks.ENGINEERS_TERMINAL.get(), terminalMapping, blockModels.modelOutput))
        ));

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
        itemModels.generateFlatItem(ModItems.SWORD_GUARD_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.POMMEL_PATTERN.get(), ModelTemplates.FLAT_ITEM);

        // -------------------------------------------------------------
        // Modular Tools (Multi-Layer Tinted Handheld Models)
        // -------------------------------------------------------------
        ModelTemplate twoLayerHandheld = new ModelTemplate(
                java.util.Optional.of(Identifier.withDefaultNamespace("item/handheld")),
                java.util.Optional.empty(),
                TextureSlot.LAYER0,
                TextureSlot.LAYER1
        );

        ModelTemplate fourLayerHandheld = new ModelTemplate(
                java.util.Optional.of(Identifier.withDefaultNamespace("item/handheld")),
                java.util.Optional.empty(),
                TextureSlot.LAYER0,
                TextureSlot.LAYER1,
                TextureSlot.LAYER2,
                LAYER3
        );

        // Pickaxe (4 layers: handle, head, tip, binding)
        Identifier pickaxeHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_handle");
        Identifier pickaxeHeadTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_head");
        Identifier pickaxeTipTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_tip");
        Identifier pickaxeBindingTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_binding");

        registerModularFourPartModel(itemModels, ModItems.MODULAR_PICKAXE.get(), fourLayerHandheld,
                pickaxeHandleTex, pickaxeHeadTex, pickaxeTipTex, pickaxeBindingTex);

        // Axe (4 layers: handle, head, tip, binding)
        Identifier axeHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_axe_handle");
        Identifier axeHeadTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_axe_head");
        Identifier axeTipTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_axe_tip");
        Identifier axeBindingTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_axe_binding");

        registerModularFourPartModel(itemModels, ModItems.MODULAR_AXE.get(), fourLayerHandheld,
                axeHandleTex, axeHeadTex, axeTipTex, axeBindingTex);

        // Sword (4 layers: handle, pommel, blade, guard)
        Identifier swordHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_handle");
        Identifier swordPommelTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_pommel");
        Identifier swordBladeTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_blade");
        Identifier swordGuardTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_guard");

        registerModularSwordModel(itemModels, ModItems.MODULAR_SWORD.get(), fourLayerHandheld,
                swordHandleTex, swordPommelTex, swordBladeTex, swordGuardTex);

        // Shovel (2 layers: handle, head)
        Identifier shovelHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_shovel_handle");
        Identifier shovelHeadTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_shovel_head");

        registerModularTwoPartModel(itemModels, ModItems.MODULAR_SHOVEL.get(), twoLayerHandheld,
                shovelHandleTex, shovelHeadTex);

        // Hoe (2 layers: handle, head)
        Identifier hoeHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_hoe_handle");
        Identifier hoeHeadTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_hoe_head");

        registerModularTwoPartModel(itemModels, ModItems.MODULAR_HOE.get(), twoLayerHandheld,
                hoeHandleTex, hoeHeadTex);

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
                        String overlaySuffix = String.format("%03d", material.overlayIndex());
                        Identifier templateBlockModelId;
                        if (form == ResourceForm.ORE) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/stone_ore_" + overlaySuffix);
                        } else if (form == ResourceForm.DEEPSLATE_ORE) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/deepslate_ore_" + overlaySuffix);
                        } else if (form == ResourceForm.NETHERRACK_ORE) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/netherrack_ore_" + overlaySuffix);
                        } else if (form == ResourceForm.END_STONE_ORE) {
                            templateBlockModelId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/template/end_stone_ore_" + overlaySuffix);
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

        // -------------------------------------------------------------
        // Molten Fluid Buckets & Liquid Blocks
        // -------------------------------------------------------------
        Identifier moltenBucketTemplateId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/molten_bucket");
        TextureMapping bucketMapping = new TextureMapping()
                .put(TextureSlot.LAYER0, new net.minecraft.client.resources.model.sprite.Material(Identifier.fromNamespaceAndPath("minecraft", "item/bucket")))
                .put(TextureSlot.LAYER1, new net.minecraft.client.resources.model.sprite.Material(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/molten_resource_bucket_overlay")));
        ModelTemplates.TWO_LAYERED_ITEM.create(moltenBucketTemplateId, bucketMapping, itemModels.modelOutput);

        for (Material material : ModMaterials.getAllMaterials()) {
            if (material.hasForm(ResourceForm.MOLTEN) && material.isRegisteredLocally(ResourceForm.MOLTEN)) {
                Item bucketItem = material.getItem(ResourceForm.MOLTEN);
                if (bucketItem != null) {
                    int tintColor = 0xFF000000 | material.colorHex();
                    itemModels.itemModelOutput.accept(
                            bucketItem,
                            ItemModelUtils.tintedModel(
                                    moltenBucketTemplateId,
                                    ItemModelUtils.constantTint(0xFFFFFFFF),
                                    ItemModelUtils.constantTint(tintColor)
                            )
                    );
                }

                Block liquidBlock = material.getBlock(ResourceForm.MOLTEN);
                if (liquidBlock != null) {
                    blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                            liquidBlock,
                            BlockModelGenerators.plainVariant(Identifier.fromNamespaceAndPath("minecraft", "block/air"))
                    ));
                }
            }
        }
    }

    private void registerModularTwoPartModel(ItemModelGenerators itemModels, Item toolItem, ModelTemplate template,
                                             Identifier handleTexture, Identifier headTexture) {
        Identifier modelId = ModelLocationUtils.getModelLocation(toolItem);
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, new net.minecraft.client.resources.model.sprite.Material(handleTexture))
                .put(TextureSlot.LAYER1, new net.minecraft.client.resources.model.sprite.Material(headTexture));

        template.create(modelId, mapping, itemModels.modelOutput);

        itemModels.itemModelOutput.accept(
                toolItem,
                ItemModelUtils.tintedModel(
                        modelId,
                        new ModularToolPartTintSource(PartSlot.HANDLE),
                        new ModularToolPartTintSource(PartSlot.HEAD)
                )
        );
    }

    private void registerModularSwordModel(ItemModelGenerators itemModels, Item toolItem, ModelTemplate template,
                                           Identifier handleTexture, Identifier pommelTexture, Identifier bladeTexture, Identifier guardTexture) {
        Identifier modelId = ModelLocationUtils.getModelLocation(toolItem);
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, new net.minecraft.client.resources.model.sprite.Material(handleTexture))
                .put(TextureSlot.LAYER1, new net.minecraft.client.resources.model.sprite.Material(pommelTexture))
                .put(TextureSlot.LAYER2, new net.minecraft.client.resources.model.sprite.Material(bladeTexture))
                .put(LAYER3, new net.minecraft.client.resources.model.sprite.Material(guardTexture));

        template.create(modelId, mapping, itemModels.modelOutput);

        itemModels.itemModelOutput.accept(
                toolItem,
                ItemModelUtils.tintedModel(
                        modelId,
                        new ModularToolPartTintSource(PartSlot.HANDLE),
                        new ModularToolPartTintSource(PartSlot.POMMEL),
                        new ModularToolPartTintSource(PartSlot.HEAD),
                        new ModularToolPartTintSource(PartSlot.BINDING)
                )
        );
    }

    private void registerModularFourPartModel(ItemModelGenerators itemModels, Item toolItem, ModelTemplate template,
                                              Identifier handleTexture, Identifier headTexture, Identifier tipTexture, Identifier bindingTexture) {
        Identifier modelId = ModelLocationUtils.getModelLocation(toolItem);
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, new net.minecraft.client.resources.model.sprite.Material(handleTexture))
                .put(TextureSlot.LAYER1, new net.minecraft.client.resources.model.sprite.Material(headTexture))
                .put(TextureSlot.LAYER2, new net.minecraft.client.resources.model.sprite.Material(tipTexture))
                .put(LAYER3, new net.minecraft.client.resources.model.sprite.Material(bindingTexture));

        template.create(modelId, mapping, itemModels.modelOutput);

        itemModels.itemModelOutput.accept(
                toolItem,
                ItemModelUtils.tintedModel(
                        modelId,
                        new ModularToolPartTintSource(PartSlot.HANDLE),
                        new ModularToolPartTintSource(PartSlot.HEAD),
                        new ModularToolPartTintSource(PartSlot.TIP),
                        new ModularToolPartTintSource(PartSlot.BINDING)
                )
        );
    }
}
