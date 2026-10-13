package com.leclowndu93150.heavier_weapons.platform;

import com.leclowndu93150.heavier_weapons.network.HitstopPacket;
import com.leclowndu93150.heavier_weapons.network.HitstopType;
import com.leclowndu93150.heavier_weapons.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
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
        var packet = new HitstopPacket(player.getId(), targetIds, durationTicks, type);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
    }
}
