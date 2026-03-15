package com.leclowndu93150.heavier_weapons.client;

import com.leclowndu93150.heavier_weapons.Config;

public class HitstopManager {

    private static int remainingTicks = 0;
    private static boolean frozen = false;
    private static int delayTicks = 0;
    private static int pendingDuration = 0;

    public static void startHitstop(int ticks) {
        if (ticks <= 0) return;

        int delay = Config.hitstopDelayTicks;

        if (frozen) {
            remainingTicks = Math.max(remainingTicks, ticks);
            return;
        }

        if (delay > 0) {
            delayTicks = delay;
            pendingDuration = ticks;
        } else {
            remainingTicks = ticks;
            frozen = true;
        }
    }

    public static void tick() {
        if (delayTicks > 0) {
            delayTicks--;
            if (delayTicks <= 0) {
                remainingTicks = pendingDuration;
                frozen = true;
                pendingDuration = 0;
            }
            return;
        }

        if (!frozen) return;
        remainingTicks--;
        if (remainingTicks <= 0) {
            frozen = false;
        }
    }

    public static boolean isActive() {
        return frozen;
    }
}
