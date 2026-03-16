package com.leclowndu93150.heavier_weapons.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.leclowndu93150.heavier_weapons.compat.CriticalStrikeCompat;
import net.critical_strike.internal.CritLogic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Player.class, priority = 1500)
public abstract class CriticalStrikeCritMixin {

    @TargetHandler(
            mixin = "net.critical_strike.mixin.PlayerEntityMixin",
            name = "applyCriticalStrikeDamage"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/critical_strike/internal/CritLogic;playFxAt(Lnet/minecraft/world/entity/Entity;F)V"),
            remap = false,
            require = 0
    )
    private void heavier_weapons$onCriticalStrikeCrit(Entity target, float volume) {
        CriticalStrikeCompat.markCrit();
        CritLogic.playFxAt(target, volume);
    }
}
