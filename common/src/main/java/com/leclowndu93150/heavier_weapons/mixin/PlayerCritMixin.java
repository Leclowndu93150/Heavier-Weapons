package com.leclowndu93150.heavier_weapons.mixin;

import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerCritMixin {

    @Inject(method = "crit", at = @At("HEAD"))
    private void heavier_weapons$onCrit(Entity target, CallbackInfo ci) {
        HitstopServerHandler.markCrit();
    }
}
