package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.Config;
import com.leclowndu93150.heavier_weapons.compat.CriticalStrikeCompat;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

public class HitstopServerHandler {

    private static boolean lastAttackWasCrit = false;
    private static boolean criticalStrikeLoaded = false;

    public static void init() {
        criticalStrikeLoaded = ModList.get().isLoaded("critical_strike");
    }

    public static void markCrit() {
        lastAttackWasCrit = true;
    }

    public static boolean consumeCrit() {
        boolean was = lastAttackWasCrit;
        lastAttackWasCrit = false;
        return was;
    }

    public static void onPlayerDamageEntity(ServerPlayer attacker, int targetId, DamageSource source) {
        if (!Config.hitstopEnabled) return;

        var mainHandStack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
        var attributes = WeaponRegistry.getAttributes(mainHandStack);
        String category = attributes != null ? attributes.category() : null;

        int hitstopTicks = Config.getHitstopTicks(category);

        boolean isCrit = consumeCrit();

        if (!isCrit && Config.criticalStrikeCompat && criticalStrikeLoaded) {
            isCrit = CriticalStrikeCompat.isCritical(source);
        }

        if (isCrit) {
            hitstopTicks += Config.critBonusTicks;
        }

        if (hitstopTicks <= 0) return;

        var packet = new HitstopPacket(attacker.getId(), new int[]{targetId}, hitstopTicks, isCrit);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(attacker, packet);
    }
}
