package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworking {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(HitstopPacket.TYPE, HitstopPacket.STREAM_CODEC);
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(HitstopPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> HitstopHandler.handlePacket(
                    payload.attackerId(), payload.targetIds(), payload.durationTicks(), payload.isCrit()));
        });
    }

    public static void sendToTrackingAndSelf(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit) {
        HitstopPacket packet = new HitstopPacket(attacker.getId(), targetIds, durationTicks, isCrit);
        ServerPlayNetworking.send(attacker, packet);
        for (ServerPlayer tracker : PlayerLookup.tracking(attacker)) {
            if (tracker != attacker) {
                ServerPlayNetworking.send(tracker, packet);
            }
        }
    }
}
