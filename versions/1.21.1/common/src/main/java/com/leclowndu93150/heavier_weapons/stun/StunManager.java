package com.leclowndu93150.heavier_weapons.stun;

import com.leclowndu93150.heavier_weapons.HWConfig;
import com.leclowndu93150.heavier_weapons.mixin.MobAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public class StunManager {

    private static final Map<LivingEntity, StunState> states = new WeakHashMap<>();

    public static boolean isStunned(LivingEntity entity) {
        StunState state = states.get(entity);
        return state != null && state.stunnedUntil > entity.level().getGameTime();
    }

    public static boolean isVulnerable(LivingEntity entity) {
        StunState state = states.get(entity);
        return state != null && state.vulnerableUntil > entity.level().getGameTime();
    }

    public static void onHit(Player attacker, LivingEntity target, int hitstopTicks) {
        if (!(target instanceof Mob mob) || HWConfig.entityBlacklist.matches(mob)) return;
        StunState state = states.computeIfAbsent(mob, entity -> new StunState());
        long now = mob.level().getGameTime();
        if (state.stunnedUntil > now) return;

        double multiplier = isBackstab(attacker, mob) ? HWConfig.backstabMultiplier : 1.0;
        state.stagger += hitstopTicks * multiplier;
        if (state.stagger < staggerThreshold(mob)) return;

        state.stagger = 0;
        stun(mob, state, hitstopTicks * HWConfig.stunDurationMultiplier * multiplier, now);
    }

    public static void stun(LivingEntity target, int ticks) {
        if (ticks <= 0 || !(target instanceof Mob mob) || HWConfig.entityBlacklist.matches(mob)) return;
        StunState state = states.computeIfAbsent(mob, entity -> new StunState());
        stun(mob, state, ticks, mob.level().getGameTime());
    }

    public static void makeVulnerable(LivingEntity target, int ticks) {
        if (ticks <= 0 || HWConfig.entityBlacklist.matches(target)) return;
        StunState state = states.computeIfAbsent(target, entity -> new StunState());
        state.vulnerableUntil = target.level().getGameTime() + ticks;
    }

    private static void stun(Mob mob, StunState state, double ticks, long now) {
        double resistance = state.resistance(now);
        long duration = Math.round(ticks / (1 + resistance * HWConfig.stunResistancePerStun));
        if (duration <= 0) return;

        state.stunnedUntil = Math.max(state.stunnedUntil, now + duration);
        state.resistance = resistance + 1;
        state.lastStun = now;

        mob.getNavigation().stop();
        ((MobAccessor) mob).heavier_weapons$getGoalSelector().getAvailableGoals().stream()
                .filter(WrappedGoal::isRunning)
                .forEach(WrappedGoal::stop);
    }

    private static double staggerThreshold(Mob mob) {
        Double override = HWConfig.staggerThresholds.get(EntityType.getKey(mob.getType()));
        double threshold = override != null ? override : mob.getMaxHealth() * HWConfig.staggerThresholdPerHealth;
        return threshold * (1 + mob.getArmorValue() * HWConfig.armorStaggerResistance);
    }

    private static boolean isBackstab(Player attacker, Mob target) {
        if (target.getTarget() != attacker) return true;
        Vec3 facing = Vec3.directionFromRotation(0, target.yBodyRot);
        Vec3 toAttacker = attacker.position().subtract(target.position());
        return facing.dot(new Vec3(toAttacker.x, 0, toAttacker.z).normalize()) < -0.5;
    }

    private static class StunState {
        private long stunnedUntil;
        private long vulnerableUntil;
        private long lastStun;
        private double stagger;
        private double resistance;

        private double resistance(long now) {
            double decayed = (now - lastStun) / (double) Math.max(1, HWConfig.stunResistanceDecayTicks);
            return Math.max(0, resistance - decayed);
        }
    }
}
