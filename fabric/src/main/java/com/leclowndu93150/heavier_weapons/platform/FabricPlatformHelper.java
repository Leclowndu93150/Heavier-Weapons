package com.leclowndu93150.heavier_weapons.platform;

import com.leclowndu93150.heavier_weapons.network.FabricNetworking;
import com.leclowndu93150.heavier_weapons.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

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
    public void sendHitstopPacket(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit) {
        FabricNetworking.sendToTrackingAndSelf(attacker, targetIds, durationTicks, isCrit);
    }
}
