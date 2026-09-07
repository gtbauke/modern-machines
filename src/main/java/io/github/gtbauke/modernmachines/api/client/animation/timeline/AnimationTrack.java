package io.github.gtbauke.modernmachines.api.client.animation.timeline;

import java.util.ArrayList;
import java.util.List;

import io.github.gtbauke.modernmachines.api.client.animation.math.AnimationMath;
import io.github.gtbauke.modernmachines.api.client.animation.math.Easing;
import io.github.gtbauke.modernmachines.api.client.animation.math.Transform3D;
import org.jspecify.annotations.NonNull;

public class AnimationTrack {
    private final String partName;
    private final List<Keyframe> keyframes = new ArrayList<>();
    private int cursorTick = 0;

    public AnimationTrack(@NonNull String partName) {
        this.partName = partName;
    }

    public @NonNull String getPartName() {
        return partName;
    }

    public AnimationTrack atTick(int tick) {
        this.cursorTick = tick;
        return this;
    }

    public AnimationTrack then() {
        return this;
    }

    public AnimationTrack translateY(float fromY, float toY, int duration, @NonNull Easing easing) {
        var from = new Transform3D();
        from.translation.y = fromY;

        var to = new Transform3D();
        to.translation.y = toY;

        keyframes.add(new Keyframe(cursorTick, duration, from, to, easing, Channel.TRANSLATION_Y));
        cursorTick += duration;
        return this;
    }

    public AnimationTrack rotateY(float fromY, float toY, int duration, @NonNull Easing easing) {
        var from = new Transform3D();
        from.rotation.y = fromY;

        var to = new Transform3D();
        to.rotation.y = toY;

        keyframes.add(new Keyframe(cursorTick, duration, from, to, easing, Channel.ROTATION_Y));
        cursorTick += duration;
        return this;
    }

    public AnimationTrack scale(float fromScale, float toScale, int duration, @NonNull Easing easing) {
        var from = new Transform3D();
        from.scale.set(fromScale, fromScale, fromScale);

        var to = new Transform3D();
        to.scale.set(toScale, toScale, toScale);

        keyframes.add(new Keyframe(cursorTick, duration, from, to, easing, Channel.SCALE));
        cursorTick += duration;
        return this;
    }

    public AnimationTrack transform(@NonNull Transform3D from, @NonNull Transform3D to, int duration, @NonNull Easing easing) {
        keyframes.add(new Keyframe(cursorTick, duration, from, to, easing, Channel.ALL));
        cursorTick += duration;
        return this;
    }

    public int getTotalDuration() {
        var max = 0;

        for (var kf : keyframes) {
            max = Math.max(max, kf.startTick() + kf.durationTicks());
        }

        return max;
    }

    public void evaluate(int currentTick, float partialTick, @NonNull Transform3D outTransform) {
        var tickTime = currentTick + partialTick;

        for (var kf : keyframes) {
            if (tickTime >= kf.startTick() && tickTime <= (kf.startTick() + kf.durationTicks())) {
                var localProgress = (tickTime - kf.startTick()) / (float) Math.max(1, kf.durationTicks());
                var eased = kf.easing().ease(AnimationMath.clamp(localProgress, 0.0f, 1.0f));

                kf.apply(eased, outTransform);
            }
        }
    }

    public enum Channel {
        TRANSLATION_Y,
        ROTATION_Y,
        SCALE,
        ALL
    }

    public record Keyframe(
            int startTick,
            int durationTicks,
            Transform3D from,
            Transform3D to,
            Easing easing,
            Channel channel
    ) {
        public void apply(float progress, Transform3D dest) {
            if (channel == Channel.TRANSLATION_Y) {
                dest.translation.y += AnimationMath.lerp(progress, from.translation.y, to.translation.y);
            } else if (channel == Channel.ROTATION_Y) {
                dest.rotation.y += AnimationMath.rotLerp(progress, from.rotation.y, to.rotation.y);
            } else if (channel == Channel.SCALE) {
                dest.scale.mul(
                        AnimationMath.lerp(progress, from.scale.x, to.scale.x),
                        AnimationMath.lerp(progress, from.scale.y, to.scale.y),
                        AnimationMath.lerp(progress, from.scale.z, to.scale.z)
                );
            } else if (channel == Channel.ALL) {
                from.lerp(to, progress, dest);
            }
        }
    }
}
