package com.leclowndu93150.heavier_weapons.platform.services;

import com.leclowndu93150.heavier_weapons.network.HitstopType;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    Path getConfigDir();

    void sendHitstopPacket(ServerPlayer player, int[] targetIds, int durationTicks, HitstopType type);
}
