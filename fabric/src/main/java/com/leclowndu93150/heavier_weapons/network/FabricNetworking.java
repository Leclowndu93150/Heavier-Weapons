package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.Constants;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworking {

    public static final ResourceLocation HITSTOP_PACKET_ID = new ResourceLocation(Constants.MOD_ID, "hitstop");

    public static void init() {
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(HITSTOP_PACKET_ID, (client, handler, buf, responseSender) -> {
            int attackerId = buf.readVarInt();
            int count = buf.readVarInt();
            int[] targetIds = new int[count];
            for (int i = 0; i < count; i++) {
                targetIds[i] = buf.readVarInt();
            }
            int durationTicks = buf.readVarInt();
            boolean isCrit = buf.readBoolean();
            client.execute(() -> HitstopHandler.handlePacket(attackerId, targetIds, durationTicks, isCrit));
        });
    }

    public static void sendToTrackingAndSelf(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(attacker.getId());
        buf.writeVarInt(targetIds.length);
        for (int id : targetIds) {
            buf.writeVarInt(id);
        }
        buf.writeVarInt(durationTicks);
        buf.writeBoolean(isCrit);

        ServerPlayNetworking.send(attacker, HITSTOP_PACKET_ID, PacketByteBufs.duplicate(buf));

        for (ServerPlayer tracker : PlayerLookup.tracking(attacker)) {
            if (tracker != attacker) {
                ServerPlayNetworking.send(tracker, HITSTOP_PACKET_ID, PacketByteBufs.duplicate(buf));
            }
        }
    }
}
