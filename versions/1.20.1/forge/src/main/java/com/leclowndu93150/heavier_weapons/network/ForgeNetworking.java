package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.Constants;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ForgeNetworking {

    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        CHANNEL.registerMessage(0, HitstopMessage.class, HitstopMessage::encode, HitstopMessage::decode, HitstopMessage::handle);
    }

    public static void sendToTrackingAndSelf(ServerPlayer player, int[] targetIds, int durationTicks, HitstopType type) {
        HitstopMessage msg = new HitstopMessage(player.getId(), targetIds, durationTicks, type);
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), msg);
    }

    public static class HitstopMessage {
        public final int attackerId;
        public final int[] targetIds;
        public final int durationTicks;
        public final HitstopType type;

        public HitstopMessage(int attackerId, int[] targetIds, int durationTicks, HitstopType type) {
            this.attackerId = attackerId;
            this.targetIds = targetIds;
            this.durationTicks = durationTicks;
            this.type = type;
        }

        public static void encode(HitstopMessage msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.attackerId);
            buf.writeVarInt(msg.targetIds.length);
            for (int id : msg.targetIds) {
                buf.writeVarInt(id);
            }
            buf.writeVarInt(msg.durationTicks);
            buf.writeEnum(msg.type);
        }

        public static HitstopMessage decode(FriendlyByteBuf buf) {
            int attackerId = buf.readVarInt();
            int count = buf.readVarInt();
            int[] targetIds = new int[count];
            for (int i = 0; i < count; i++) {
                targetIds[i] = buf.readVarInt();
            }
            int duration = buf.readVarInt();
            HitstopType type = buf.readEnum(HitstopType.class);
            return new HitstopMessage(attackerId, targetIds, duration, type);
        }

        public static void handle(HitstopMessage msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                HitstopHandler.handlePacket(msg.attackerId, msg.targetIds, msg.durationTicks, msg.type);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
