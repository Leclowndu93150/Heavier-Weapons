package com.leclowndu93150.heavier_weapons.compat;

import net.critical_strike.api.CriticalDamageSource;
import net.minecraft.world.damagesource.DamageSource;

public class CriticalStrikeCompat {

    private static boolean lastAttackWasCrit = false;

    public static void markCrit() {
        lastAttackWasCrit = true;
    }

    public static boolean consumeCrit() {
        boolean was = lastAttackWasCrit;
        lastAttackWasCrit = false;
        return was;
    }

    public static boolean isCritical(DamageSource source) {
        if (source instanceof CriticalDamageSource critSource) {
            return critSource.rng_isCritical();
        }
        return false;
    }
}
