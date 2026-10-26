package com.leclowndu93150.heavier_weapons.knockback;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public class KnockbackDelayManager {

    private static final Map<LivingEntity, HeldKnockback> held = new WeakHashMap<>();

    public static void hold(LivingEntity entity, int ticks) {
        if (ticks <= 0) return;
        HeldKnockback state = held.computeIfAbsent(entity, e -> new HeldKnockback());
        state.releaseAt = Math.max(state.releaseAt, entity.level().getGameTime() + ticks);
    }

    public static boolean isHeld(LivingEntity entity) {
        return !held.isEmpty() && held.containsKey(entity);
    }

    public static Vec3 getHeldVelocity(LivingEntity entity) {
        Vec3 velocity = held.get(entity).velocity;
        return velocity != null ? velocity : entity.getDeltaMovement();
    }

    public static void setHeldVelocity(LivingEntity entity, Vec3 velocity) {
        held.get(entity).velocity = velocity;
    }

    public static void tick(LivingEntity entity) {
        if (held.isEmpty()) return;
        HeldKnockback state = held.get(entity);
        if (state == null) return;

        if (state.releaseAt > entity.level().getGameTime()) {
            if (!(entity instanceof Player)) {
                if (state.velocity == null) {
                    state.velocity = entity.getDeltaMovement();
                }
                entity.setDeltaMovement(Vec3.ZERO);
            }
            return;
        }

        held.remove(entity);
        if (state.velocity == null) return;
        entity.setDeltaMovement(state.velocity);
        entity.hasImpulse = true;
        entity.hurtMarked = true;
    }

    private static class HeldKnockback {
        private long releaseAt;
        private Vec3 velocity;
    }
}
