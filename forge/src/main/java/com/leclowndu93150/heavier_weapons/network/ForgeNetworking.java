package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.Constants;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ForgeNetworking {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        CHANNEL.registerMessage(0, HitstopMessage.class, HitstopMessage::encode, HitstopMessage::decode, HitstopMessage::handle);
    }

    public static void sendToTrackingAndSelf(ServerPlayer attacker, int[] targetIds, int durationTicks, boolean isCrit) {
        HitstopMessage msg = new HitstopMessage(attacker.getId(), targetIds, durationTicks, isCrit);
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> attacker), msg);
    }

    public static class HitstopMessage {
        public final int attackerId;
        public final int[] targetIds;
        public final int durationTicks;
        public final boolean isCrit;

        public HitstopMessage(int attackerId, int[] targetIds, int durationTicks, boolean isCrit) {
            this.attackerId = attackerId;
            this.targetIds = targetIds;
            this.durationTicks = durationTicks;
            this.isCrit = isCrit;
        }

        public static void encode(HitstopMessage msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.attackerId);
            buf.writeVarInt(msg.targetIds.length);
            for (int id : msg.targetIds) {
                buf.writeVarInt(id);
            }
            buf.writeVarInt(msg.durationTicks);
            buf.writeBoolean(msg.isCrit);
        }

        public static HitstopMessage decode(FriendlyByteBuf buf) {
            int attackerId = buf.readVarInt();
            int count = buf.readVarInt();
            int[] targetIds = new int[count];
            for (int i = 0; i < count; i++) {
                targetIds[i] = buf.readVarInt();
            }
            int duration = buf.readVarInt();
            boolean isCrit = buf.readBoolean();
            return new HitstopMessage(attackerId, targetIds, duration, isCrit);
        }

        public static void handle(HitstopMessage msg, Supplier<net.minecraftforge.network.NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                HitstopHandler.handlePacket(msg.attackerId, msg.targetIds, msg.durationTicks, msg.isCrit);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
