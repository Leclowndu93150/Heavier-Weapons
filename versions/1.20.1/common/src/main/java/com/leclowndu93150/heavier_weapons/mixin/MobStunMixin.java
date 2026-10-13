package com.leclowndu93150.heavier_weapons.mixin;

import com.leclowndu93150.heavier_weapons.stun.StunManager;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobStunMixin {

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void heavier_weapons$skipAiWhileStunned(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob.level().isClientSide || !StunManager.isStunned(mob)) return;
        mob.setXxa(0);
        mob.setYya(0);
        mob.setZza(0);
        mob.setJumping(false);
        ci.cancel();
    }
}
