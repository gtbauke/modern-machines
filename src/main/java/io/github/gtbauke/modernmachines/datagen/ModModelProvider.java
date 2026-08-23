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
import net.minecraft.core.Direction;
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

        itemModels.generateFlatItem(ModItems.ADOBE_BRICKS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ADOBE_MIXTURE.get(), ModelTemplates.FLAT_ITEM);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ModBlocks.ADOBE_BRICK.get(),
                BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create(ModBlocks.ADOBE_BRICK.get(), TextureMapping.cube(ModBlocks.ADOBE_BRICK.get()), blockModels.modelOutput))
        ));

        // Copper Pipe Model & Blockstate
        Identifier pipeStraightId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/copper_pipe");
        Identifier pipeSideId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/copper_pipe_side");
        Identifier pipeCrossId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "block/pipe/copper_pipe_cross");

        MultiVariant straight = BlockModelGenerators.plainVariant(pipeStraightId);
        MultiVariant straightX = straight.with(VariantMutator.Y_ROT.withValue(Quadrant.R90));
        MultiVariant straightY = straight.with(VariantMutator.X_ROT.withValue(Quadrant.R90));
        MultiVariant side = BlockModelGenerators.plainVariant(pipeSideId);
        MultiVariant side90 = side.with(VariantMutator.Y_ROT.withValue(Quadrant.R90));
        MultiVariant side180 = side.with(VariantMutator.Y_ROT.withValue(Quadrant.R180));
        MultiVariant side270 = side.with(VariantMutator.Y_ROT.withValue(Quadrant.R270));
        MultiVariant cross = BlockModelGenerators.plainVariant(pipeCrossId);

        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(ModBlocks.COPPER_PIPE.get())
                // Isolated pipes rotated by placement AXIS
                .with(new ConditionBuilder().term(CopperPipeBlock.AXIS, Direction.Axis.Z)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straight)
                .with(new ConditionBuilder().term(CopperPipeBlock.AXIS, Direction.Axis.X)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straightX)
                .with(new ConditionBuilder().term(CopperPipeBlock.AXIS, Direction.Axis.Y)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straightY)
                // Straight connections
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.SOUTH, true)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false), straight)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.WEST, true)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false), straightX)
                .with(new ConditionBuilder().term(CopperPipeBlock.UP, true).term(CopperPipeBlock.DOWN, true)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false), straightY)
                // Single-end connection straight pipes
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straight)
                .with(new ConditionBuilder().term(CopperPipeBlock.SOUTH, true).term(CopperPipeBlock.NORTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straight)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.WEST, false)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straightX)
                .with(new ConditionBuilder().term(CopperPipeBlock.WEST, true).term(CopperPipeBlock.EAST, false)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.UP, false).term(CopperPipeBlock.DOWN, false), straightX)
                .with(new ConditionBuilder().term(CopperPipeBlock.UP, true).term(CopperPipeBlock.DOWN, false)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false), straightY)
                .with(new ConditionBuilder().term(CopperPipeBlock.DOWN, true).term(CopperPipeBlock.UP, false)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.SOUTH, false)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.WEST, false), straightY)
                // Corner / Side connections
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.EAST, true)
                        .term(CopperPipeBlock.SOUTH, false).term(CopperPipeBlock.WEST, false), side)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.SOUTH, true)
                        .term(CopperPipeBlock.WEST, false).term(CopperPipeBlock.NORTH, false), side90)
                .with(new ConditionBuilder().term(CopperPipeBlock.SOUTH, true).term(CopperPipeBlock.WEST, true)
                        .term(CopperPipeBlock.NORTH, false).term(CopperPipeBlock.EAST, false), side180)
                .with(new ConditionBuilder().term(CopperPipeBlock.WEST, true).term(CopperPipeBlock.NORTH, true)
                        .term(CopperPipeBlock.EAST, false).term(CopperPipeBlock.SOUTH, false), side270)
                // 3-way T-junctions
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.SOUTH, true).term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.WEST, false), cross)
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.SOUTH, true).term(CopperPipeBlock.WEST, true).term(CopperPipeBlock.EAST, false), cross)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.WEST, true).term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.SOUTH, false), cross)
                .with(new ConditionBuilder().term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.WEST, true).term(CopperPipeBlock.SOUTH, true).term(CopperPipeBlock.NORTH, false), cross)
                // 4-way Cross connection
                .with(new ConditionBuilder().term(CopperPipeBlock.NORTH, true).term(CopperPipeBlock.SOUTH, true).term(CopperPipeBlock.EAST, true).term(CopperPipeBlock.WEST, true), cross)
        );
        itemModels.itemModelOutput.accept(
                ModItems.COPPER_PIPE.get().asItem(),
                ItemModelUtils.plainModel(pipeStraightId)
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
        itemModels.generateFlatItem(ModItems.SWORD_GUARD_PATTERN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.POMMEL_PATTERN.get(), ModelTemplates.FLAT_ITEM);

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

        ModelTemplate fourLayerHandheld = new ModelTemplate(
                java.util.Optional.of(Identifier.withDefaultNamespace("item/handheld")),
                java.util.Optional.empty(),
                TextureSlot.LAYER0,
                TextureSlot.LAYER1,
                TextureSlot.LAYER2,
                LAYER3
        );

        Identifier handleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/handle");
        Identifier bindingTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/binding");

        Identifier pickaxeHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_handle");
        Identifier pickaxeHeadTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_head");
        Identifier pickaxeTipTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_tip");
        Identifier pickaxeBindingTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_pickaxe_binding");

        registerModularPickaxeModel(itemModels, ModItems.MODULAR_PICKAXE.get(), fourLayerHandheld,
                pickaxeHandleTex, pickaxeHeadTex, pickaxeTipTex, pickaxeBindingTex);

        registerModularToolModel(itemModels, ModItems.MODULAR_AXE.get(), threeLayerHandheld,
                handleTex, bindingTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/axe_head"));

        registerModularToolModel(itemModels, ModItems.MODULAR_SHOVEL.get(), threeLayerHandheld,
                handleTex, bindingTex, Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/part/shovel_head"));

        Identifier swordHandleTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_handle");
        Identifier swordPommelTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_pommel");
        Identifier swordBladeTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_blade");
        Identifier swordGuardTex = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "item/template/assembled_tool_part/assembled_sword_guard");

        registerModularSwordModel(itemModels, ModItems.MODULAR_SWORD.get(), fourLayerHandheld,
                swordHandleTex, swordPommelTex, swordBladeTex, swordGuardTex);

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

    private void registerModularPickaxeModel(ItemModelGenerators itemModels, Item toolItem, ModelTemplate template,
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
