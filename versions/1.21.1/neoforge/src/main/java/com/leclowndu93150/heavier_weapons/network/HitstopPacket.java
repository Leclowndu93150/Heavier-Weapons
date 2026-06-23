package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.HeavierWeapons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HitstopPacket(int attackerId, int[] targetIds, int durationTicks, boolean isCrit) implements CustomPacketPayload {

    public static final Type<HitstopPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HeavierWeapons.MODID, "hitstop"));

    public static final StreamCodec<ByteBuf, HitstopPacket> STREAM_CODEC = StreamCodec.of(
            HitstopPacket::encode,
            HitstopPacket::decode
    );

    private static void encode(ByteBuf buf, HitstopPacket packet) {
        ByteBufCodecs.VAR_INT.encode(buf, packet.attackerId);
        ByteBufCodecs.VAR_INT.encode(buf, packet.targetIds.length);
        for (int id : packet.targetIds) {
            ByteBufCodecs.VAR_INT.encode(buf, id);
        }
        ByteBufCodecs.VAR_INT.encode(buf, packet.durationTicks);
        ByteBufCodecs.BOOL.encode(buf, packet.isCrit);
    }

    private static HitstopPacket decode(ByteBuf buf) {
        int attackerId = ByteBufCodecs.VAR_INT.decode(buf);
        int count = ByteBufCodecs.VAR_INT.decode(buf);
        int[] targetIds = new int[count];
        for (int i = 0; i < count; i++) {
            targetIds[i] = ByteBufCodecs.VAR_INT.decode(buf);
        }
        int duration = ByteBufCodecs.VAR_INT.decode(buf);
        boolean isCrit = ByteBufCodecs.BOOL.decode(buf);
        return new HitstopPacket(attackerId, targetIds, duration, isCrit);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
