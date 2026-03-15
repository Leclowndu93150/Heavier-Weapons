package com.leclowndu93150.heavier_weapons.mixin.client;

import com.leclowndu93150.heavier_weapons.client.HitstopManager;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void heavier_weapons$freezeMovement(CallbackInfo ci) {
        if (HitstopManager.isActive()) {
            var player = (LocalPlayer) (Object) this;
            player.input.forwardImpulse = 0;
            player.input.leftImpulse = 0;
        }
    }
}
