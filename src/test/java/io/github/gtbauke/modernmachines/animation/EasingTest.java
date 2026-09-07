package io.github.gtbauke.modernmachines.animation;

import io.github.gtbauke.modernmachines.api.client.animation.math.Easing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EasingTest {

    @Test
    public void testBoundaryValues() {
        var easings = new Easing[]{
                Easing.LINEAR,
                Easing.EASE_IN_SINE, EASE_OUT_SINE(), EASE_IN_OUT_SINE(),
                Easing.EASE_IN_QUAD, Easing.EASE_OUT_QUAD, Easing.EASE_IN_OUT_QUAD,
                Easing.EASE_IN_CUBIC, Easing.EASE_OUT_CUBIC, Easing.EASE_IN_OUT_CUBIC,
                Easing.EASE_IN_EXPO, Easing.EASE_OUT_EXPO, Easing.EASE_IN_OUT_EXPO,
                Easing.EASE_IN_BOUNCE, Easing.EASE_OUT_BOUNCE, Easing.EASE_IN_OUT_BOUNCE
        };

        for (var easing : easings) {
            Assertions.assertEquals(0.0f, easing.ease(0.0f), 0.001f);
            Assertions.assertEquals(1.0f, easing.ease(1.0f), 0.001f);
        }
    }

    private static Easing EASE_OUT_SINE() {
        return Easing.EASE_OUT_SINE;
    }

    private static Easing EASE_IN_OUT_SINE() {
        return Easing.EASE_IN_OUT_SINE;
    }

    @Test
    public void testLinearInterpolation() {
        Assertions.assertEquals(0.5f, Easing.LINEAR.ease(0.5f), 0.0001f);
        Assertions.assertEquals(0.25f, Easing.LINEAR.ease(0.25f), 0.0001f);
    }

    @Test
    public void testQuadCurves() {
        Assertions.assertEquals(0.25f, Easing.EASE_IN_QUAD.ease(0.5f), 0.0001f);
        Assertions.assertEquals(0.75f, Easing.EASE_OUT_QUAD.ease(0.5f), 0.0001f);
        Assertions.assertEquals(0.5f, Easing.EASE_IN_OUT_QUAD.ease(0.5f), 0.0001f);
    }
}
