package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.HWConfig;
import com.leclowndu93150.heavier_weapons.compat.SwordBlockingCompat;
import com.leclowndu93150.heavier_weapons.platform.Services;
import com.leclowndu93150.heavier_weapons.stun.StunManager;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HitstopServerHandler {

    private static final int SHIELD_RAISE_TICKS = 5;

    private static boolean lastAttackWasCrit = false;
    private static boolean swordBlockingLoaded = false;

    public static void init() {
        swordBlockingLoaded = Services.PLATFORM.isModLoaded("swordblockingmechanics");
    }

    public static void markCrit() {
        lastAttackWasCrit = true;
    }

    public static boolean consumeCrit() {
        boolean was = lastAttackWasCrit;
        lastAttackWasCrit = false;
        return was;
    }

    public static void onPlayerDamageEntity(ServerPlayer attacker, LivingEntity target, DamageSource source) {
        HWConfig.reloadIfChanged();
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

        Services.PLATFORM.sendHitstopPacket(attacker, new int[]{target.getId()}, hitstopTicks, isCrit ? HitstopType.CRIT : HitstopType.HIT);

        if (HWConfig.stunEnabled) {
            StunManager.onHit(attacker, target, hitstopTicks);
        }
    }

    public static boolean tryParry(Player player, LivingEntity attacker) {
        HWConfig.reloadIfChanged();
        if (!HWConfig.parryEnabled || player.getTicksUsingItem() > SHIELD_RAISE_TICKS + HWConfig.parryWindowTicks) return false;
        parry(player, attacker);
        return true;
    }

    public static void onShieldBlock(Player player, LivingEntity attacker, boolean shieldDisabled) {
        if (!HWConfig.shieldStunEnabled) return;
        if (shieldDisabled) {
            StunManager.stun(attacker, HWConfig.shieldBreakStunTicks);
            sendShieldHitstop(player, attacker, HWConfig.shieldBreakHitstopTicks);
        } else {
            StunManager.stun(attacker, HWConfig.shieldBlockStunTicks);
        }
    }

    public static void onPlayerHurt(Player player, DamageSource source, float amount) {
        if (!swordBlockingLoaded || amount <= 0 || player.isBlocking() || source.is(DamageTypeTags.IS_PROJECTILE)) return;
        if (!(source.getDirectEntity() instanceof LivingEntity attacker) || !SwordBlockingCompat.isBlocking(player)) return;

        HWConfig.reloadIfChanged();
        if (HWConfig.parryEnabled && SwordBlockingCompat.isParrying(player)) {
            parry(player, attacker);
        } else {
            onShieldBlock(player, attacker, false);
        }
    }

    private static void parry(Player player, LivingEntity attacker) {
        StunManager.stun(attacker, HWConfig.parryStunTicks);
        StunManager.makeVulnerable(attacker, HWConfig.parryVulnerabilityTicks);
        sendShieldHitstop(player, attacker, HWConfig.parryHitstopTicks);
    }

    private static void sendShieldHitstop(Player player, LivingEntity attacker, int ticks) {
        if (ticks > 0 && player instanceof ServerPlayer serverPlayer) {
            Services.PLATFORM.sendHitstopPacket(serverPlayer, new int[]{attacker.getId()}, ticks, HitstopType.SHIELD);
        }
    }
}
