package com.leclowndu93150.heavier_weapons.mixin;

import com.leclowndu93150.heavier_weapons.HWConfig;
import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import com.leclowndu93150.heavier_weapons.stun.StunManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHurtMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float heavier_weapons$applyParryVulnerability(float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide || !StunManager.isVulnerable(self)) return amount;
        return amount * (float) HWConfig.parryDamageMultiplier;
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void heavier_weapons$onPlayerHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player && !player.level().isClientSide) {
            HitstopServerHandler.onPlayerHurt(player, source, amount);
        }
    }
}
