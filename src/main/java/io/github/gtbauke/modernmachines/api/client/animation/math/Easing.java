package io.github.gtbauke.modernmachines.api.client.animation.math;

@FunctionalInterface
public interface Easing {
    float ease(float t);

    Easing LINEAR = t -> t;

    // Sine
    Easing EASE_IN_SINE = t -> 1.0f - (float) Math.cos((t * Math.PI) / 2.0);
    Easing EASE_OUT_SINE = t -> (float) Math.sin((t * Math.PI) / 2.0);
    Easing EASE_IN_OUT_SINE = t -> -((float) Math.cos(Math.PI * t) - 1.0f) / 2.0f;

    // Quad
    Easing EASE_IN_QUAD = t -> t * t;
    Easing EASE_OUT_QUAD = t -> 1.0f - (1.0f - t) * (1.0f - t);
    Easing EASE_IN_OUT_QUAD = t -> t < 0.5f ? 2.0f * t * t : 1.0f - (float) Math.pow(-2.0 * t + 2.0, 2) / 2.0f;

    // Cubic
    Easing EASE_IN_CUBIC = t -> t * t * t;
    Easing EASE_OUT_CUBIC = t -> 1.0f - (float) Math.pow(1.0 - t, 3);
    Easing EASE_IN_OUT_CUBIC = t -> t < 0.5f ? 4.0f * t * t * t : 1.0f - (float) Math.pow(-2.0 * t + 2.0, 3) / 2.0f;

    // Quart
    Easing EASE_IN_QUART = t -> t * t * t * t;
    Easing EASE_OUT_QUART = t -> 1.0f - (float) Math.pow(1.0 - t, 4);
    Easing EASE_IN_OUT_QUART = t -> t < 0.5f ? 8.0f * t * t * t * t : 1.0f - (float) Math.pow(-2.0 * t + 2.0, 4) / 2.0f;

    // Quint
    Easing EASE_IN_QUINT = t -> t * t * t * t * t;
    Easing EASE_OUT_QUINT = t -> 1.0f - (float) Math.pow(1.0 - t, 5);
    Easing EASE_IN_OUT_QUINT = t -> t < 0.5f ? 16.0f * t * t * t * t * t : 1.0f - (float) Math.pow(-2.0 * t + 2.0, 5) / 2.0f;

    // Expo
    Easing EASE_IN_EXPO = t -> t == 0.0f ? 0.0f : (float) Math.pow(2.0, 10.0 * (t - 1.0));
    Easing EASE_OUT_EXPO = t -> t == 1.0f ? 1.0f : 1.0f - (float) Math.pow(2.0, -10.0 * t);
    Easing EASE_IN_OUT_EXPO = t -> {
        if (t == 0.0f) {
            return 0.0f;
        }

        if (t == 1.0f) {
            return 1.0f;
        }

        if (t < 0.5f) {
            return (float) Math.pow(2.0, 20.0 * t - 10.0) / 2.0f;
        }

        return (2.0f - (float) Math.pow(2.0, -20.0 * t + 10.0)) / 2.0f;
    };

    // Circ
    Easing EASE_IN_CIRC = t -> 1.0f - (float) Math.sqrt(1.0 - Math.pow(t, 2));
    Easing EASE_OUT_CIRC = t -> (float) Math.sqrt(1.0 - Math.pow(t - 1.0, 2));
    Easing EASE_IN_OUT_CIRC = t -> t < 0.5f
            ? (1.0f - (float) Math.sqrt(1.0 - Math.pow(2.0 * t, 2))) / 2.0f
            : ((float) Math.sqrt(1.0 - Math.pow(-2.0 * t + 2.0, 2)) + 1.0f) / 2.0f;

    // Back
    float BACK_C1 = 1.70158f;
    float BACK_C2 = BACK_C1 * 1.525f;
    float BACK_C3 = BACK_C1 + 1.0f;

    Easing EASE_IN_BACK = t -> BACK_C3 * t * t * t - BACK_C1 * t * t;
    Easing EASE_OUT_BACK = t -> 1.0f + BACK_C3 * (float) Math.pow(t - 1.0, 3) + BACK_C1 * (float) Math.pow(t - 1.0, 2);
    Easing EASE_IN_OUT_BACK = t -> t < 0.5f
            ? ((float) Math.pow(2.0 * t, 2) * ((BACK_C2 + 1.0f) * 2.0f * t - BACK_C2)) / 2.0f
            : ((float) Math.pow(2.0 * t - 2.0, 2) * ((BACK_C2 + 1.0f) * (t * 2.0f - 2.0f) + BACK_C2) + 2.0f) / 2.0f;

    // Elastic
    float ELASTIC_C4 = (float) ((2.0 * Math.PI) / 3.0);
    float ELASTIC_C5 = (float) ((2.0 * Math.PI) / 4.5);

    Easing EASE_IN_ELASTIC = t -> {
        if (t == 0.0f) {
            return 0.0f;
        }

        if (t == 1.0f) {
            return 1.0f;
        }

        return -(float) (Math.pow(2.0, 10.0 * (t - 1.0)) * Math.sin(((t - 1.0) * 10.0 - 0.75) * ELASTIC_C4));
    };

    Easing EASE_OUT_ELASTIC = t -> {
        if (t == 0.0f) {
            return 0.0f;
        }

        if (t == 1.0f) {
            return 1.0f;
        }

        return (float) (Math.pow(2.0, -10.0 * t) * Math.sin((t * 10.0 - 0.75) * ELASTIC_C4) + 1.0);
    };

    Easing EASE_IN_OUT_ELASTIC = t -> {
        if (t == 0.0f) {
            return 0.0f;
        }

        if (t == 1.0f) {
            return 1.0f;
        }

        if (t < 0.5f) {
            return -(float) (Math.pow(2.0, 20.0 * t - 10.0) * Math.sin((20.0 * t - 11.125) * ELASTIC_C5)) / 2.0f;
        }

        return (float) (Math.pow(2.0, -20.0 * t + 10.0) * Math.sin((20.0 * t - 11.125) * ELASTIC_C5)) / 2.0f + 1.0f;
    };

    // Bounce
    Easing EASE_OUT_BOUNCE = t -> {
        var n1 = 7.5625f;
        var d1 = 2.75f;

        if (t < 1.0f / d1) {
            return n1 * t * t;
        } else if (t < 2.0f / d1) {
            var shifted = t - 1.5f / d1;
            return n1 * shifted * shifted + 0.75f;
        } else if (t < 2.5f / d1) {
            var shifted = t - 2.25f / d1;
            return n1 * shifted * shifted + 0.9375f;
        } else {
            var shifted = t - 2.625f / d1;
            return n1 * shifted * shifted + 0.984375f;
        }
    };

    Easing EASE_IN_BOUNCE = t -> 1.0f - EASE_OUT_BOUNCE.ease(1.0f - t);

    Easing EASE_IN_OUT_BOUNCE = t -> t < 0.5f
            ? (1.0f - EASE_OUT_BOUNCE.ease(1.0f - 2.0f * t)) / 2.0f
            : (1.0f + EASE_OUT_BOUNCE.ease(2.0f * t - 1.0f)) / 2.0f;
}
