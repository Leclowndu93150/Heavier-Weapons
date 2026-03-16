package com.leclowndu93150.heavier_weapons.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityFreezeManager {

    private static final Map<Integer, FreezeState> frozenEntities = new HashMap<>();

    public static void freeze(Entity entity, int ticks) {
        float bodyYaw = entity.getYRot();
        float headYaw = entity.getYRot();
        if (entity instanceof LivingEntity living) {
            bodyYaw = living.yBodyRot;
            headYaw = living.yHeadRot;
        }
        frozenEntities.put(entity.getId(), new FreezeState(
                ticks,
                entity.getX(), entity.getY(), entity.getZ(),
                bodyYaw, entity.getYRot(), entity.getXRot(),
                headYaw
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
        public final double x, y, z;
        public final float bodyYaw, yaw, pitch, headYaw;

        public FreezeState(int ticks, double x, double y, double z, float bodyYaw, float yaw, float pitch, float headYaw) {
            this.remainingTicks = ticks;
            this.x = x;
            this.y = y;
            this.z = z;
            this.bodyYaw = bodyYaw;
            this.yaw = yaw;
            this.pitch = pitch;
            this.headYaw = headYaw;
        }
    }
}
