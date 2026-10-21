package com.leclowndu93150.heavier_weapons.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityFreezeManager {

    private static final Map<Integer, FreezeState> frozenEntities = new HashMap<>();

    private static final double SHAKE_SPEED = 5.0;

    public static void freeze(Entity entity, int ticks, Entity attacker, double shakeMagnitude) {
        float bodyYaw = entity.getYRot();
        float headYaw = entity.getYRot();
        if (entity instanceof LivingEntity living) {
            bodyYaw = living.yBodyRot;
            headYaw = living.yHeadRot;
        }
        double shakeAngle = attacker != null && attacker != entity
                ? Math.atan2(entity.getZ() - attacker.getZ(), entity.getX() - attacker.getX())
                : entity.level().random.nextDouble() * Math.PI * 2;
        frozenEntities.put(entity.getId(), new FreezeState(
                ticks,
                entity.getX(), entity.getY(), entity.getZ(),
                bodyYaw, entity.getYRot(), entity.getXRot(),
                headYaw,
                Math.cos(shakeAngle) * shakeMagnitude, Math.sin(shakeAngle) * shakeMagnitude
        ));
    }

    public static void tick() {
        Iterator<Map.Entry<Integer, FreezeState>> it = frozenEntities.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            entry.getValue().remainingTicks--;
            if (entry.getValue().remainingTicks <= 0) {
                it.remove();
            }
        }
    }

    public static boolean isFrozen(int entityId) {
        return frozenEntities.containsKey(entityId);
    }

    public static FreezeState getFreezeState(int entityId) {
        return frozenEntities.get(entityId);
    }

    public static class FreezeState {
        public int remainingTicks;
        public final int totalTicks;
        public final double x, y, z;
        public final float bodyYaw, yaw, pitch, headYaw;
        public final double shakeX, shakeZ;

        public FreezeState(int ticks, double x, double y, double z, float bodyYaw, float yaw, float pitch, float headYaw, double shakeX, double shakeZ) {
            this.remainingTicks = ticks;
            this.totalTicks = ticks;
            this.x = x;
            this.y = y;
            this.z = z;
            this.bodyYaw = bodyYaw;
            this.yaw = yaw;
            this.pitch = pitch;
            this.headYaw = headYaw;
            this.shakeX = shakeX;
            this.shakeZ = shakeZ;
        }

        public double getShake(float partialTick) {
            float elapsed = totalTicks - remainingTicks + partialTick;
            float fade = (remainingTicks - partialTick) / totalTicks;
            return Math.sin(elapsed * SHAKE_SPEED) * fade;
        }
    }
}
