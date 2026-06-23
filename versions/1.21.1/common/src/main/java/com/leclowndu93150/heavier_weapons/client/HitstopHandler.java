package com.leclowndu93150.heavier_weapons.client;

import com.leclowndu93150.heavier_weapons.HWConfig;
import com.leclowndu93150.heavier_weapons.HeavierWeapons;
import com.leclowndu93150.heavier_weapons.compat.CriticalStrikeCompat;
import com.leclowndu93150.heavier_weapons.platform.Services;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HitstopHandler {

    private static boolean criticalStrikeLoaded = false;

    public static void init() {
        criticalStrikeLoaded = Services.PLATFORM.isModLoaded("critical_strike");

        BetterCombatClientEvents.ATTACK_HIT.register((player, attackHand, targets, cursorTarget) -> {
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

            HitstopManager.startHitstop(hitstopTicks);

            if (HWConfig.cameraShakeEnabled) {
                int shakeDuration = hitstopTicks + 1;
                float shakeIntensity = computeShakeIntensity(hitstopTicks) * (float) HWConfig.cameraShakeIntensity;
                CameraShakeManager.startShake(shakeDuration, shakeIntensity);
            }
        });
    }

    public static void handlePacket(int attackerId, int[] targetIds, int durationTicks, boolean isCrit) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity attackerEntity = mc.level.getEntity(attackerId);

        if (attackerEntity == mc.player && isCrit) {
            HitstopManager.startHitstop(durationTicks);
            if (HWConfig.cameraShakeEnabled) {
                int shakeDuration = durationTicks + 1;
                float shakeIntensity = computeShakeIntensity(durationTicks) * (float) HWConfig.cameraShakeIntensity;
                CameraShakeManager.startShake(shakeDuration, shakeIntensity);
            }
        }

        if (HWConfig.entityFreezeEnabled) {
            for (int targetId : targetIds) {
                Entity target = mc.level.getEntity(targetId);
                if (target != null) {
                    EntityFreezeManager.freeze(target, durationTicks);
                }
            }
        }
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
