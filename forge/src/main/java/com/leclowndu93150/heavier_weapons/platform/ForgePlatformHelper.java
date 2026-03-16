package com.leclowndu93150.heavier_weapons.platform;

import com.leclowndu93150.heavier_weapons.network.ForgeNetworking;
import com.leclowndu93150.heavier_weapons.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void sendHitstopPacket(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit) {
        ForgeNetworking.sendToTrackingAndSelf(attacker, targetIds, durationTicks, isCrit);
    }
}
