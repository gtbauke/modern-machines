package io.github.gtbauke.modernmachines.machine.client;

import io.github.gtbauke.modernmachines.api.client.animation.controller.AnimationController;
import io.github.gtbauke.modernmachines.api.client.animation.math.AnimationAxis;
import io.github.gtbauke.modernmachines.api.client.animation.math.Easing;
import io.github.gtbauke.modernmachines.api.client.animation.math.Pivot;
import io.github.gtbauke.modernmachines.api.client.animation.model.ModelPartNode;
import io.github.gtbauke.modernmachines.api.client.animation.render.AnimatedBlockEntityRenderState;
import io.github.gtbauke.modernmachines.api.client.animation.render.AnimatedBlockEntityRenderer;
import io.github.gtbauke.modernmachines.api.client.animation.timeline.KinematicDriver;
import io.github.gtbauke.modernmachines.machine.blockentity.AlloySmelterBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AlloySmelterRenderer extends AnimatedBlockEntityRenderer<AlloySmelterBlockEntity, AnimatedBlockEntityRenderState> {
    private final AnimationController controller;
    private final ModelPartNode rootNode;

    public AlloySmelterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);

        this.rootNode = new ModelPartNode("root", Pivot.CENTER);
        var rotorNode = new ModelPartNode("rotor", Pivot.CENTER);
        this.rootNode.addChild(rotorNode);

        this.controller = new AnimationController();
        this.controller.registerDriver("rotor", KinematicDriver.continuousRotation(AnimationAxis.Y, () -> 4.0f, 1.0f));
        this.controller.timeline("smelt_burst", timeline -> {
            timeline.track("rotor", track -> {
                track.scale(1.0f, 1.2f, 5, Easing.EASE_OUT_BOUNCE)
                        .then().scale(1.2f, 1.0f, 5, Easing.EASE_IN_CUBIC);
            });
        });
        this.controller.registerTrigger(1, "smelt_burst");
    }

    @Override
    public AnimatedBlockEntityRenderState createRenderState() {
        return new AnimatedBlockEntityRenderState();
    }

    @Override
    protected @Nullable AnimationController getAnimationController(AlloySmelterBlockEntity blockEntity) {
        return controller;
    }

    @Override
    protected @Nullable ModelPartNode getRootNode(AlloySmelterBlockEntity blockEntity) {
        return rootNode;
    }
}
