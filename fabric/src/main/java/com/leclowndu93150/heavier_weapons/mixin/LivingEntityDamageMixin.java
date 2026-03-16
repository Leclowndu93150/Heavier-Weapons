package com.leclowndu93150.heavier_weapons.mixin;

import com.leclowndu93150.heavier_weapons.CommonClass;
import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {

    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void heavier_weapons$afterDamage(DamageSource source, float amount, CallbackInfo ci) {
        if (!CommonClass.enabled) return;
        LivingEntity self = (LivingEntity) (Object) this;
        if (source.getEntity() instanceof ServerPlayer attacker) {
            HitstopServerHandler.onPlayerDamageEntity(attacker, self.getId(), source);
        }
    }
}
