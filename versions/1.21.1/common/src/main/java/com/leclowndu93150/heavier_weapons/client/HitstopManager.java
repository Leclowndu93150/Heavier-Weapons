package com.leclowndu93150.heavier_weapons.client;

import com.leclowndu93150.heavier_weapons.HWConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class HitstopManager {

    private static int remainingTicks = 0;
    private static boolean frozen = false;
    private static int delayTicks = 0;
    private static int pendingDuration = 0;
    private static boolean pendingSound = false;

    public static void startHitstop(int ticks, int delay, boolean withSound) {
        if (ticks <= 0) return;

        boolean soundOnFreeze = withSound && HWConfig.hitstopSoundAfterDelay;
        if (withSound && !soundOnFreeze) {
            playSound(ticks);
        }

        if (frozen) {
            remainingTicks = Math.max(remainingTicks, ticks);
            if (soundOnFreeze) {
                playSound(ticks);
            }
            return;
        }

        if (delay > 0) {
            delayTicks = delay;
            pendingDuration = ticks;
            pendingSound = soundOnFreeze;
        } else {
            freeze(ticks, soundOnFreeze);
        }
    }

    public static void tick() {
        if (delayTicks > 0) {
            delayTicks--;
            if (delayTicks <= 0) {
                freeze(pendingDuration, pendingSound);
                pendingDuration = 0;
                pendingSound = false;
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

    private static void freeze(int ticks, boolean withSound) {
        remainingTicks = ticks;
        frozen = true;
        if (withSound) {
            playSound(ticks);
        }
    }

    private static void playSound(int ticks) {
        var mc = Minecraft.getInstance();
        if (!HWConfig.hitstopSoundEnabled || mc.level == null || mc.player == null) return;
        float pitch = (float) HWConfig.hitstopSoundPitch * (0.92f + mc.level.random.nextFloat() * 0.16f);
        for (ResourceLocation sound : HWConfig.getHitstopSounds(ticks)) {
            mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvent.createVariableRangeEvent(sound),
                    SoundSource.PLAYERS, (float) HWConfig.hitstopSoundVolume, pitch, false);
        }
    }
}
