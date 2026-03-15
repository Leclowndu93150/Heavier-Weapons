package com.leclowndu93150.heavier_weapons.client;

import com.leclowndu93150.heavier_weapons.Config;
import com.leclowndu93150.heavier_weapons.HeavierWeapons;
import com.leclowndu93150.heavier_weapons.network.HitstopPacket;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HitstopHandler {

    public static void init() {
        BetterCombatClientEvents.ATTACK_HIT.register((player, attackHand, targets, cursorTarget) -> {
            if (!HeavierWeapons.enabled || !Config.hitstopEnabled) return;
            if (targets.isEmpty()) return;

            String category = attackHand.attributes() != null ? attackHand.attributes().category() : null;
            int hitstopTicks = Config.getHitstopTicks(category);

            boolean isCrit = isCriticalHit(player, targets.stream().anyMatch(e -> e instanceof LivingEntity));
            if (isCrit) {
                hitstopTicks += Config.critBonusTicks;
            }

            if (hitstopTicks <= 0) return;

            HitstopManager.startHitstop(hitstopTicks);

            if (Config.cameraShakeEnabled) {
                int shakeDuration = hitstopTicks + 1;
                float shakeIntensity = computeShakeIntensity(hitstopTicks) * (float) Config.cameraShakeIntensity;
                CameraShakeManager.startShake(shakeDuration, shakeIntensity);
            }
        });
    }

    public static void handlePacket(HitstopPacket packet) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int ticks = packet.durationTicks();

        Entity attackerEntity = mc.level.getEntity(packet.attackerId());
        if (attackerEntity instanceof AbstractClientPlayer && attackerEntity != mc.player) {
            // TODO: remote player hitstop via their AnimationStack
        }

        if (Config.entityFreezeEnabled) {
            for (int targetId : packet.targetIds()) {
                Entity target = mc.level.getEntity(targetId);
                if (target != null) {
                    EntityFreezeManager.freeze(target, ticks);
                }
            }
        }
    }

    private static boolean isCriticalHit(AbstractClientPlayer player, boolean hasLivingTarget) {
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
