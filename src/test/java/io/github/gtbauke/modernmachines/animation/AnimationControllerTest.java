package io.github.gtbauke.modernmachines.animation;

import io.github.gtbauke.modernmachines.api.client.animation.controller.AnimationController;
import io.github.gtbauke.modernmachines.api.client.animation.math.AnimationAxis;
import io.github.gtbauke.modernmachines.api.client.animation.math.Easing;
import io.github.gtbauke.modernmachines.api.client.animation.math.Pivot;
import io.github.gtbauke.modernmachines.api.client.animation.model.ModelPartNode;
import io.github.gtbauke.modernmachines.api.client.animation.timeline.KinematicDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnimationControllerTest {

    @Test
    public void testKinematicDriverAndHierarchy() {
        var root = new ModelPartNode("root", Pivot.CENTER);
        var gear = new ModelPartNode("gear", Pivot.CENTER);
        root.addChild(gear);

        var controller = new AnimationController();
        float[] speed = {10.0f};

        controller.registerDriver("gear", KinematicDriver.continuousRotation(AnimationAxis.Y, () -> speed[0], 1.0f));

        controller.tick(root);
        controller.apply(0.0f, root);

        Assertions.assertEquals(10.0f, gear.renderTransform.rotation.y, 0.001f);

        controller.tick(root);
        controller.apply(0.0f, root);

        Assertions.assertEquals(20.0f, gear.renderTransform.rotation.y, 0.001f);
    }

    @Test
    public void testTimelineActionPlayback() {
        var root = new ModelPartNode("root", Pivot.CENTER);
        var piston = new ModelPartNode("piston", Pivot.CENTER);
        root.addChild(piston);

        var controller = new AnimationController();
        controller.timeline("crush", timeline -> {
            timeline.track("piston", track -> {
                track.translateY(0.0f, 1.0f, 10, Easing.LINEAR);
            });
        });

        controller.trigger("crush");

        for (var i = 0; i < 5; i++) {
            controller.tick(root);
        }

        controller.apply(0.0f, root);

        // At 5 ticks out of 10, translation.y should be ~0.5
        Assertions.assertEquals(0.5f, piston.renderTransform.translation.y, 0.05f);
    }
}
