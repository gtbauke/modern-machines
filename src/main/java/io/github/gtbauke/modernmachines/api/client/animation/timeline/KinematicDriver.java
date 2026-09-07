package io.github.gtbauke.modernmachines.api.client.animation.timeline;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.api.client.animation.math.AnimationAxis;
import io.github.gtbauke.modernmachines.api.client.animation.math.Transform3D;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface KinematicDriver {
    void update(Transform3D transform, int currentTick);

    static KinematicDriver continuousRotation(@NonNull AnimationAxis axis, @NonNull Supplier<Float> speedSupplier, float multiplier) {
        final float[] currentAngle = {0.0f};

        return (transform, tick) -> {
            var speed = speedSupplier.get();
            if (speed != null && speed != 0.0f) {
                currentAngle[0] = (currentAngle[0] + speed * multiplier) % 360.0f;
            }

            if (axis == AnimationAxis.X) {
                transform.rotation.x = currentAngle[0];
            } else if (axis == AnimationAxis.Y) {
                transform.rotation.y = currentAngle[0];
            } else if (axis == AnimationAxis.Z) {
                transform.rotation.z = currentAngle[0];
            }
        };
    }

    static KinematicDriver sineTranslation(@NonNull AnimationAxis axis, @NonNull BooleanSupplier activeSupplier, float amplitude, float periodTicks) {
        return (transform, tick) -> {
            if (!activeSupplier.getAsBoolean()) {
                return;
            }

            var offset = amplitude * (float) Math.sin((tick * 2.0 * Math.PI) / periodTicks);

            if (axis == AnimationAxis.X) {
                transform.translation.x = offset;
            } else if (axis == AnimationAxis.Y) {
                transform.translation.y = offset;
            } else if (axis == AnimationAxis.Z) {
                transform.translation.z = offset;
            }
        };
    }

    static KinematicDriver sinePulseGlow(@NonNull BooleanSupplier activeSupplier, int minLight, int maxLight, float periodTicks) {
        return (transform, tick) -> {
            if (!activeSupplier.getAsBoolean()) {
                transform.emissiveLight = -1;
                return;
            }

            var factor = (float) (Math.sin((tick * 2.0 * Math.PI) / periodTicks) * 0.5 + 0.5);
            transform.emissiveLight = Math.round(minLight + (maxLight - minLight) * factor);
        };
    }
}
