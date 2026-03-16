package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.HWConfig;
import com.leclowndu93150.heavier_weapons.platform.Services;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;

public class HitstopServerHandler {

    private static boolean lastAttackWasCrit = false;

    public static void markCrit() {
        lastAttackWasCrit = true;
    }

    public static boolean consumeCrit() {
        boolean was = lastAttackWasCrit;
        lastAttackWasCrit = false;
        return was;
    }

    public static void onPlayerDamageEntity(ServerPlayer attacker, int targetId, DamageSource source) {
        if (!HWConfig.hitstopEnabled) return;

        var mainHandStack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
        var attributes = WeaponRegistry.getAttributes(mainHandStack);
        if (attributes == null) return;
        String category = attributes.category();

        int hitstopTicks = HWConfig.getHitstopTicks(category);

        boolean isCrit = consumeCrit();

        if (isCrit) {
            hitstopTicks += HWConfig.critBonusTicks;
        }

        if (hitstopTicks <= 0) return;

        Services.PLATFORM.sendHitstopPacket(attacker, new int[]{targetId}, hitstopTicks, isCrit);
    }
}
