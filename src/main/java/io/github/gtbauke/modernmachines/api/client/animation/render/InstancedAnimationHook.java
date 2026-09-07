package io.github.gtbauke.modernmachines.api.client.animation.render;

import io.github.gtbauke.modernmachines.api.client.animation.model.ModelPartNode;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;

public interface InstancedAnimationHook<T extends BlockEntity> {
    void setupInstancing(@NonNull T blockEntity, @NonNull ModelPartNode rootNode);

    void updateInstancing(@NonNull T blockEntity, @NonNull ModelPartNode rootNode, float partialTicks);

    void removeInstancing(@NonNull T blockEntity);
}
