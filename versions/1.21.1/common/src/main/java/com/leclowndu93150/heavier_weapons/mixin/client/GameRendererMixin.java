package com.leclowndu93150.heavier_weapons.mixin.client;

import com.leclowndu93150.heavier_weapons.client.CameraShakeManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "bobHurt", at = @At("HEAD"))
    private void heavier_weapons$applyCameraShake(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        CameraShakeManager.applyCameraShake(poseStack, partialTick);
    }
}
