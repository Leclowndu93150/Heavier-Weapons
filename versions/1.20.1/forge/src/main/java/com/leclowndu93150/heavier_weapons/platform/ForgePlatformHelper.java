package com.leclowndu93150.heavier_weapons.platform;

import com.leclowndu93150.heavier_weapons.network.ForgeNetworking;
import com.leclowndu93150.heavier_weapons.network.HitstopType;
import com.leclowndu93150.heavier_weapons.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

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
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public void sendHitstopPacket(ServerPlayer player, int[] targetIds, int durationTicks, HitstopType type) {
        ForgeNetworking.sendToTrackingAndSelf(player, targetIds, durationTicks, type);
    }
}
