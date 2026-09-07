package io.github.gtbauke.modernmachines.api.client.animation.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gtbauke.modernmachines.api.client.animation.controller.AnimationController;
import io.github.gtbauke.modernmachines.api.client.animation.model.ModelPartNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AnimatedBlockEntityRenderer<T extends BlockEntity, S extends AnimatedBlockEntityRenderState> implements BlockEntityRenderer<T, S> {
    protected final BlockEntityRendererProvider.Context context;

    public AnimatedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    protected abstract @Nullable AnimationController getAnimationController(T blockEntity);

    protected abstract @Nullable ModelPartNode getRootNode(T blockEntity);

    @Override
    public void extractRenderState(
            T blockEntity,
            S state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        var root = getRootNode(blockEntity);
        var controller = getAnimationController(blockEntity);

        if (root != null) {
            if (controller != null) {
                controller.apply(partialTicks, root);
            } else {
                root.interpolate(partialTicks);
            }

            state.rootNode = root;
        }
    }

    @Override
    public void submit(
            S state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (state.rootNode == null || !state.rootNode.visible) {
            return;
        }

        renderNode(state.rootNode, poseStack, submitNodeCollector, state.lightCoords, state.breakProgress);
    }

    protected void renderNode(
            @NonNull ModelPartNode node,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        if (!node.visible) {
            return;
        }

        poseStack.pushPose();
        try {
            node.renderTransform.apply(poseStack, node.getPivot());

            var customRenderer = node.getCustomRenderer();
            if (customRenderer != null) {
                customRenderer.accept(node);
            }

            var parts = node.getModelParts();
            if (parts != null && !parts.isEmpty()) {
                submitNodeCollector.submitBlockModel(
                        poseStack,
                        RenderTypes.cutoutMovingBlock(),
                        parts,
                        null,
                        lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        -1
                );
            } else if (node.getBlockState() != null) {
                var blockState = node.getBlockState();
                var blockModelSet = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
                var blockModel = blockModelSet.get(blockState);
                if (blockModel != null) {
                    List<BlockStateModelPart> collectedParts = new ArrayList<>();
                    blockModel.collectParts(RandomSource.create(42L), collectedParts);
                    if (!collectedParts.isEmpty()) {
                        submitNodeCollector.submitBlockModel(
                                poseStack,
                                RenderTypes.cutoutMovingBlock(),
                                collectedParts,
                                null,
                                lightCoords,
                                OverlayTexture.NO_OVERLAY,
                                -1
                        );
                    }
                }
            }

            for (var child : node.getChildren()) {
                renderNode(child, poseStack, submitNodeCollector, lightCoords, breakProgress);
            }
        } finally {
            poseStack.popPose();
        }
    }
}
