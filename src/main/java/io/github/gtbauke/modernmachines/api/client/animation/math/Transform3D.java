package io.github.gtbauke.modernmachines.api.client.animation.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class Transform3D {
    public final Vector3f translation = new Vector3f(0.0f, 0.0f, 0.0f);
    public final Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
    public final Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    public float alpha = 1.0f;
    public int emissiveLight = -1;

    public Transform3D() {
    }

    public void reset() {
        this.translation.zero();
        this.rotation.zero();
        this.scale.set(1.0f, 1.0f, 1.0f);
        this.alpha = 1.0f;
        this.emissiveLight = -1;
    }

    public void set(Transform3D other) {
        this.translation.set(other.translation);
        this.rotation.set(other.rotation);
        this.scale.set(other.scale);
        this.alpha = other.alpha;
        this.emissiveLight = other.emissiveLight;
    }

    public void add(Transform3D other) {
        this.translation.add(other.translation);
        this.rotation.add(other.rotation);
        this.scale.mul(other.scale);
        this.alpha *= other.alpha;

        if (other.emissiveLight >= 0) {
            this.emissiveLight = Math.max(this.emissiveLight, other.emissiveLight);
        }
    }

    public void lerp(Transform3D target, float delta, Transform3D dest) {
        dest.translation.set(
                Mth.lerp(delta, this.translation.x, target.translation.x),
                Mth.lerp(delta, this.translation.y, target.translation.y),
                Mth.lerp(delta, this.translation.z, target.translation.z)
        );

        dest.rotation.set(
                Mth.rotLerp(delta, this.rotation.x, target.rotation.x),
                Mth.rotLerp(delta, this.rotation.y, target.rotation.y),
                Mth.rotLerp(delta, this.rotation.z, target.rotation.z)
        );

        dest.scale.set(
                Mth.lerp(delta, this.scale.x, target.scale.x),
                Mth.lerp(delta, this.scale.y, target.scale.y),
                Mth.lerp(delta, this.scale.z, target.scale.z)
        );

        dest.alpha = Mth.lerp(delta, this.alpha, target.alpha);
        dest.emissiveLight = target.emissiveLight >= 0 ? target.emissiveLight : this.emissiveLight;
    }

    public void apply(PoseStack poseStack, Pivot pivot) {
        poseStack.translate(pivot.x(), pivot.y(), pivot.z());
        poseStack.translate(this.translation.x, this.translation.y, this.translation.z);

        if (this.rotation.x != 0.0f) {
            poseStack.mulPose(Axis.XP.rotationDegrees(this.rotation.x));
        }

        if (this.rotation.y != 0.0f) {
            poseStack.mulPose(Axis.YP.rotationDegrees(this.rotation.y));
        }

        if (this.rotation.z != 0.0f) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(this.rotation.z));
        }

        if (this.scale.x != 1.0f || this.scale.y != 1.0f || this.scale.z != 1.0f) {
            poseStack.scale(this.scale.x, this.scale.y, this.scale.z);
        }

        poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
    }
}
