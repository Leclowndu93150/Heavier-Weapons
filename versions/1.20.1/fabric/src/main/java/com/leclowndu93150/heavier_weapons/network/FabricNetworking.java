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
            HitstopType type = buf.readEnum(HitstopType.class);
            client.execute(() -> HitstopHandler.handlePacket(attackerId, targetIds, durationTicks, type));
        });
    }

    public static void sendToTrackingAndSelf(ServerPlayer player, int[] targetIds, int durationTicks, HitstopType type) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(player.getId());
        buf.writeVarInt(targetIds.length);
        for (int id : targetIds) {
            buf.writeVarInt(id);
        }
        buf.writeVarInt(durationTicks);
        buf.writeEnum(type);

        ServerPlayNetworking.send(player, HITSTOP_PACKET_ID, PacketByteBufs.duplicate(buf));

        for (ServerPlayer tracker : PlayerLookup.tracking(player)) {
            if (tracker != player) {
                ServerPlayNetworking.send(tracker, HITSTOP_PACKET_ID, PacketByteBufs.duplicate(buf));
            }
        }
    }
}
