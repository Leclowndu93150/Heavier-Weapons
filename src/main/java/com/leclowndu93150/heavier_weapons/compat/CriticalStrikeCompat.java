package com.leclowndu93150.heavier_weapons.compat;

import net.critical_strike.api.CriticalDamageSource;
import net.minecraft.world.damagesource.DamageSource;

public class CriticalStrikeCompat {

    public static boolean isCritical(DamageSource source) {
        if (source instanceof CriticalDamageSource critSource) {
            return critSource.rng_isCritical();
        }
        return false;
    }
}
