package com.leclowndu93150.heavier_weapons.mixin.client;

import com.leclowndu93150.heavier_weapons.client.EntityFreezeManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Unique private double hw$savedX, hw$savedY, hw$savedZ;
    @Unique private double hw$savedXOld, hw$savedYOld, hw$savedZOld;
    @Unique private float hw$savedYRot, hw$savedYRotO, hw$savedXRot, hw$savedXRotO;
    @Unique private float hw$savedYHeadRot, hw$savedYHeadRotO, hw$savedYBodyRot, hw$savedYBodyRotO;
    @Unique private boolean hw$didFreeze = false;
    @Unique private Entity hw$frozenEntity = null;

    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void heavier_weapons$preRender(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        var state = EntityFreezeManager.getFreezeState(entity.getId());
        if (state == null) {
            hw$didFreeze = false;
            return;
        }

        hw$didFreeze = true;
        hw$frozenEntity = entity;

        hw$savedX = entity.getX();
        hw$savedY = entity.getY();
        hw$savedZ = entity.getZ();
        hw$savedXOld = entity.xOld;
        hw$savedYOld = entity.yOld;
        hw$savedZOld = entity.zOld;
        hw$savedYRot = entity.getYRot();
        hw$savedYRotO = entity.yRotO;
        hw$savedXRot = entity.getXRot();
        hw$savedXRotO = entity.xRotO;

        entity.setPosRaw(state.x, state.y, state.z);
        entity.xOld = state.x;
        entity.yOld = state.y;
        entity.zOld = state.z;
        entity.setYRot(state.yaw);
        entity.yRotO = state.yaw;
        entity.setXRot(state.pitch);
        entity.xRotO = state.pitch;

        if (entity instanceof LivingEntity living) {
            hw$savedYHeadRot = living.yHeadRot;
            hw$savedYHeadRotO = living.yHeadRotO;
            hw$savedYBodyRot = living.yBodyRot;
            hw$savedYBodyRotO = living.yBodyRotO;

            living.yHeadRot = state.headYaw;
            living.yHeadRotO = state.headYaw;
            living.yBodyRot = state.bodyYaw;
            living.yBodyRotO = state.bodyYaw;
        }
    }

    @Inject(method = "renderEntity", at = @At("TAIL"))
    private void heavier_weapons$postRender(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        if (!hw$didFreeze || hw$frozenEntity != entity) return;

        entity.setPosRaw(hw$savedX, hw$savedY, hw$savedZ);
        entity.xOld = hw$savedXOld;
        entity.yOld = hw$savedYOld;
        entity.zOld = hw$savedZOld;
        entity.setYRot(hw$savedYRot);
        entity.yRotO = hw$savedYRotO;
        entity.setXRot(hw$savedXRot);
        entity.xRotO = hw$savedXRotO;

        if (entity instanceof LivingEntity living) {
            living.yHeadRot = hw$savedYHeadRot;
            living.yHeadRotO = hw$savedYHeadRotO;
            living.yBodyRot = hw$savedYBodyRot;
            living.yBodyRotO = hw$savedYBodyRotO;
        }

        hw$didFreeze = false;
        hw$frozenEntity = null;
    }
}
