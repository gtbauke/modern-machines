package io.github.gtbauke.modernmachines.api.client.animation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.api.client.animation.math.Pivot;
import io.github.gtbauke.modernmachines.api.client.animation.math.Transform3D;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ModelPartNode {
    private final String name;
    private final Pivot pivot;
    private final List<ModelPartNode> children = new ArrayList<>();

    private @Nullable BlockState blockState;
    private @Nullable List<BlockStateModelPart> modelParts;
    private @Nullable Consumer<ModelPartNode> customRenderer;

    public final Transform3D transform = new Transform3D();
    public final Transform3D prevTransform = new Transform3D();
    public final Transform3D renderTransform = new Transform3D();

    public boolean visible = true;

    public ModelPartNode(@NonNull String name, @NonNull Pivot pivot) {
        this.name = name;
        this.pivot = pivot;
    }

    public ModelPartNode(@NonNull String name) {
        this(name, Pivot.CENTER);
    }

    public ModelPartNode setBlockState(@Nullable BlockState blockState) {
        this.blockState = blockState;
        return this;
    }

    public @Nullable BlockState getBlockState() {
        return blockState;
    }

    public ModelPartNode setModelParts(@Nullable List<BlockStateModelPart> modelParts) {
        this.modelParts = modelParts;
        return this;
    }

    public @Nullable List<BlockStateModelPart> getModelParts() {
        return modelParts;
    }

    public ModelPartNode setCustomRenderer(@Nullable Consumer<ModelPartNode> customRenderer) {
        this.customRenderer = customRenderer;
        return this;
    }

    public @Nullable Consumer<ModelPartNode> getCustomRenderer() {
        return customRenderer;
    }

    public ModelPartNode addChild(@NonNull ModelPartNode child) {
        this.children.add(child);
        return this;
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull Pivot getPivot() {
        return pivot;
    }

    public @NonNull List<ModelPartNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void tick() {
        prevTransform.set(transform);

        for (var child : children) {
            child.tick();
        }
    }

    public void reset() {
        transform.reset();
        prevTransform.reset();
        renderTransform.reset();

        for (var child : children) {
            child.reset();
        }
    }

    public void interpolate(float partialTick) {
        prevTransform.lerp(transform, partialTick, renderTransform);

        for (var child : children) {
            child.interpolate(partialTick);
        }
    }

    public @Nullable ModelPartNode findChild(@NonNull String childName) {
        if (this.name.equals(childName)) {
            return this;
        }

        for (var child : children) {
            var found = child.findChild(childName);
            if (found != null) {
                return found;
            }
        }

        return null;
    }
}
