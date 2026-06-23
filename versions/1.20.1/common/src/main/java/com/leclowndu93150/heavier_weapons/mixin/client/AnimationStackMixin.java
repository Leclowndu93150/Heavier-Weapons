package com.leclowndu93150.heavier_weapons.mixin.client;

import com.leclowndu93150.heavier_weapons.client.HitstopManager;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = AnimationStack.class, remap = false)
public abstract class AnimationStackMixin {

    @Unique
    private final Map<Long, Vec3f> hw$frozenTransforms = new HashMap<>();

    @Unique
    private boolean hw$captured = false;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void heavier_weapons$freezeTick(CallbackInfo ci) {
        if (HitstopManager.isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "setupAnim", at = @At("HEAD"), cancellable = true)
    private void heavier_weapons$freezeSetupAnim(float tickDelta, CallbackInfo ci) {
        if (HitstopManager.isActive() && hw$captured) {
            ci.cancel();
        }
    }

    @Inject(method = "get3DTransform", at = @At("RETURN"), cancellable = true)
    private void heavier_weapons$freezeTransform(String modelName, TransformType type, float tickDelta, Vec3f value0, CallbackInfoReturnable<Vec3f> cir) {
        if (!HitstopManager.isActive()) {
            if (hw$captured) {
                hw$frozenTransforms.clear();
                hw$captured = false;
            }
            return;
        }

        long key = ((long) modelName.hashCode() << 32) | type.ordinal();

        if (!hw$captured) {
            hw$frozenTransforms.put(key, new Vec3f(cir.getReturnValue().getX(), cir.getReturnValue().getY(), cir.getReturnValue().getZ()));
            return;
        }

        Vec3f cached = hw$frozenTransforms.get(key);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void heavier_weapons$markCaptured(float tickDelta, CallbackInfo ci) {
        if (HitstopManager.isActive() && !hw$captured && !hw$frozenTransforms.isEmpty()) {
            hw$captured = true;
        }
    }
}
