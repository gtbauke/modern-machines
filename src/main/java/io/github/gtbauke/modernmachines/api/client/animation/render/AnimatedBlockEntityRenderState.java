package io.github.gtbauke.modernmachines.api.client.animation.render;

import io.github.gtbauke.modernmachines.api.client.animation.model.ModelPartNode;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jspecify.annotations.Nullable;

public class AnimatedBlockEntityRenderState extends BlockEntityRenderState {
    public @Nullable ModelPartNode rootNode;
}
