package com.leclowndu93150.heavier_weapons.platform.services;

import net.minecraft.server.level.ServerPlayer;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    void sendHitstopPacket(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit);
}
