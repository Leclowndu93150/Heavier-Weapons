package com.leclowndu93150.heavier_weapons.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class CameraShakeManager {

    private static int shakeTicks = 0;
    private static int maxShakeTicks = 0;
    private static float intensity = 0;

    public static void startShake(int durationTicks, float shakeIntensity) {
        shakeTicks = durationTicks;
        maxShakeTicks = durationTicks;
        intensity = shakeIntensity;
    }

    public static void tick() {
        if (shakeTicks > 0) {
            shakeTicks--;
        }
    }

    public static void applyCameraShake(PoseStack poseStack, float partialTick) {
        if (shakeTicks <= 0 || maxShakeTicks <= 0) return;

        float progress = (shakeTicks - partialTick) / maxShakeTicks;
        float magnitude = intensity * progress;

        float time = (maxShakeTicks - shakeTicks + partialTick) * 4.0f;
        float shakeX = (float) (Math.sin(time * 1.7) * magnitude * 0.5);
        float shakeZ = (float) (Math.sin(time * 2.3) * magnitude);

        poseStack.mulPose(Axis.XP.rotationDegrees(shakeX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(shakeZ));
    }
}
