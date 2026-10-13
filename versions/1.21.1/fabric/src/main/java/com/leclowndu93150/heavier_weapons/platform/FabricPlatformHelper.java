package com.leclowndu93150.heavier_weapons.platform;

import com.leclowndu93150.heavier_weapons.network.FabricNetworking;
import com.leclowndu93150.heavier_weapons.network.HitstopType;
import com.leclowndu93150.heavier_weapons.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void sendHitstopPacket(ServerPlayer player, int[] targetIds, int durationTicks, HitstopType type) {
        FabricNetworking.sendToTrackingAndSelf(player, targetIds, durationTicks, type);
    }
}
