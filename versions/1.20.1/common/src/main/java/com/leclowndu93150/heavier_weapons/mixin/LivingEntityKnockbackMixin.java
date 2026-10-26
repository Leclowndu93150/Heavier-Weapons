package com.leclowndu93150.heavier_weapons.mixin;

import com.leclowndu93150.heavier_weapons.knockback.KnockbackDelayManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityKnockbackMixin {

    @Unique private Vec3 hw$velocityBeforeKnockback;

    @Inject(method = "tick", at = @At("HEAD"))
    private void heavier_weapons$tickHeldKnockback(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide) {
            KnockbackDelayManager.tick(self);
        }
    }

    @Inject(method = "knockback", at = @At("HEAD"))
    private void heavier_weapons$startHeldKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide || !KnockbackDelayManager.isHeld(self)) return;
        hw$velocityBeforeKnockback = self.getDeltaMovement();
        self.setDeltaMovement(KnockbackDelayManager.getHeldVelocity(self));
    }

    @Inject(method = "knockback", at = @At("RETURN"))
    private void heavier_weapons$holdKnockback(double strength, double x, double z, CallbackInfo ci) {
        if (hw$velocityBeforeKnockback == null) return;
        LivingEntity self = (LivingEntity) (Object) this;
        KnockbackDelayManager.setHeldVelocity(self, self.getDeltaMovement());
        self.setDeltaMovement(hw$velocityBeforeKnockback);
        hw$velocityBeforeKnockback = null;
    }
}
