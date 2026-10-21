package com.leclowndu93150.heavier_weapons.client;

import com.leclowndu93150.heavier_weapons.HWConfig;
import com.leclowndu93150.heavier_weapons.HeavierWeapons;
import com.leclowndu93150.heavier_weapons.compat.CriticalStrikeCompat;
import com.leclowndu93150.heavier_weapons.network.HitstopType;
import com.leclowndu93150.heavier_weapons.platform.Services;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HitstopHandler {

    private static final double ENTITY_SHAKE_DISTANCE = 0.15;

    private static boolean criticalStrikeLoaded = false;

    public static void init() {
        criticalStrikeLoaded = Services.PLATFORM.isModLoaded("critical_strike");

        BetterCombatClientEvents.ATTACK_HIT.register((player, attackHand, targets, cursorTarget) -> {
            HWConfig.reloadIfChanged();
            if (!HeavierWeapons.enabled || !HWConfig.hitstopEnabled) return;
            if (targets.isEmpty()) return;

            String category = attackHand.attributes() != null ? attackHand.attributes().category() : null;
            int hitstopTicks = HWConfig.getHitstopTicks(category);

            boolean isCrit = isVanillaCrit(player, targets.stream().anyMatch(e -> e instanceof LivingEntity));

            if (!isCrit && HWConfig.criticalStrikeCompat && criticalStrikeLoaded) {
                isCrit = CriticalStrikeCompat.consumeCrit();
            }

            if (isCrit) {
                hitstopTicks += HWConfig.critBonusTicks;
            }

            if (hitstopTicks <= 0) return;

            HitstopManager.startHitstop(hitstopTicks, HWConfig.getHitstopDelay(category, player.getCurrentItemAttackStrengthDelay()), true);
            startShake(hitstopTicks);
        });
    }

    public static void handlePacket(int attackerId, int[] targetIds, int durationTicks, HitstopType type) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        HWConfig.reloadIfChanged();

        Entity attackerEntity = mc.level.getEntity(attackerId);

        if (attackerEntity == mc.player && type != HitstopType.HIT) {
            HitstopManager.startHitstop(durationTicks, type == HitstopType.CRIT ? getHitstopDelay(mc.player) : 0, false);
            startShake(durationTicks);
        }

        int freezeTicks = HWConfig.getEntityFreezeTicks(durationTicks);
        if (HWConfig.entityFreezeEnabled && freezeTicks > 0) {
            double shakeMagnitude = HWConfig.entityShakeEnabled
                    ? computeShakeIntensity(durationTicks) * HWConfig.entityShakeIntensity * ENTITY_SHAKE_DISTANCE
                    : 0;
            for (int targetId : targetIds) {
                Entity target = mc.level.getEntity(targetId);
                if (target != null && !HWConfig.entityBlacklist.matches(target)) {
                    EntityFreezeManager.freeze(target, freezeTicks, attackerEntity, shakeMagnitude);
                }
            }
        }
    }

    private static int getHitstopDelay(Player player) {
        var attributes = WeaponRegistry.getAttributes(player.getMainHandItem());
        return HWConfig.getHitstopDelay(attributes != null ? attributes.category() : null, player.getCurrentItemAttackStrengthDelay());
    }

    private static void startShake(int hitstopTicks) {
        if (!HWConfig.cameraShakeEnabled) return;
        int shakeDuration = hitstopTicks + 1;
        float shakeIntensity = computeShakeIntensity(hitstopTicks) * (float) HWConfig.cameraShakeIntensity;
        CameraShakeManager.startShake(shakeDuration, shakeIntensity);
    }

    private static boolean isVanillaCrit(AbstractClientPlayer player, boolean hasLivingTarget) {
        return hasLivingTarget
                && player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.isPassenger()
                && !player.isSprinting();
    }

    private static float computeShakeIntensity(int hitstopTicks) {
        if (hitstopTicks <= 1) return 0.3f;
        if (hitstopTicks <= 3) return 0.6f;
        return 1.0f;
    }
}
