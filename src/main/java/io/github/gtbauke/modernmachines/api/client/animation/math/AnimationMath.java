package io.github.gtbauke.modernmachines.api.client.animation.math;

public final class AnimationMath {
    private AnimationMath() {
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static float rotLerp(float delta, float start, float end) {
        var diff = (end - start) % 360.0f;
        var shortestDiff = ((diff + 540.0f) % 360.0f) - 180.0f;
        return start + delta * shortestDiff;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }
}
