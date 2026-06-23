package com.leclowndu93150.heavier_weapons.platform;

import com.leclowndu93150.heavier_weapons.network.HitstopPacket;
import com.leclowndu93150.heavier_weapons.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

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
    public void sendHitstopPacket(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit) {
        var packet = new HitstopPacket(attacker.getId(), targetIds, durationTicks, isCrit);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(attacker, packet);
    }
}
