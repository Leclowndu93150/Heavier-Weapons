package com.leclowndu93150.heavier_weapons.mixin;

import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerShieldMixin {

    @Inject(method = "blockUsingShield", at = @At("HEAD"), cancellable = true)
    private void heavier_weapons$parry(LivingEntity attacker, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (HitstopServerHandler.tryParry(player, attacker)) {
            ((LivingEntityInvoker) attacker).heavier_weapons$blockedByShield(player);
            ci.cancel();
        }
    }

    @Inject(method = "blockUsingShield", at = @At("TAIL"))
    private void heavier_weapons$shieldBlock(LivingEntity attacker, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        HitstopServerHandler.onShieldBlock(player, attacker, !player.isUsingItem());
    }
}
