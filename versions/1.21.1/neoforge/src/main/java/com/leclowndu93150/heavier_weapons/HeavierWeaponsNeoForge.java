package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.client.CameraShakeManager;
import com.leclowndu93150.heavier_weapons.client.EntityFreezeManager;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import com.leclowndu93150.heavier_weapons.client.HitstopManager;
import com.leclowndu93150.heavier_weapons.network.HitstopPacket;
import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import com.mojang.brigadier.Command;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static net.minecraft.commands.Commands.literal;

@Mod(HeavierWeapons.MODID)
public class HeavierWeaponsNeoForge {

    public HeavierWeaponsNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(HeavierWeaponsNeoForge::onLivingDamage);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        HeavierWeapons.init();
        HitstopServerHandler.init();
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(HeavierWeapons.MODID).versioned("1");
        registrar.playToClient(HitstopPacket.TYPE, HitstopPacket.STREAM_CODEC, this::handleHitstopPacket);
    }

    private void handleHitstopPacket(HitstopPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> HitstopHandler.handlePacket(
                packet.attackerId(), packet.targetIds(), packet.durationTicks(), packet.isCrit()));
    }

    private static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!HeavierWeapons.enabled) return;
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof ServerPlayer attacker) {
            HitstopServerHandler.onPlayerDamageEntity(attacker, event.getEntity().getId(), source);
        }
    }

    @EventBusSubscriber(modid = HeavierWeapons.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            HitstopHandler.init();
            NeoForge.EVENT_BUS.addListener(ClientModEvents::onClientTick);
            NeoForge.EVENT_BUS.addListener(ClientModEvents::onRegisterCommands);
        }

        private static void onClientTick(ClientTickEvent.Post event) {
            HitstopManager.tick();
            CameraShakeManager.tick();
            EntityFreezeManager.tick();
        }

        private static void onRegisterCommands(RegisterClientCommandsEvent event) {
            event.getDispatcher().register(
                    literal("hitstop")
                            .then(literal("on").executes(ctx -> {
                                HeavierWeapons.enabled = true;
                                ctx.getSource().sendSuccess(() -> Component.literal("Hitstop enabled"), false);
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(literal("off").executes(ctx -> {
                                HeavierWeapons.enabled = false;
                                ctx.getSource().sendSuccess(() -> Component.literal("Hitstop disabled"), false);
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(literal("status").executes(ctx -> {
                                ctx.getSource().sendSuccess(() -> Component.literal("Hitstop: " + (HeavierWeapons.enabled ? "ON" : "OFF")), false);
                                return Command.SINGLE_SUCCESS;
                            }))
            );
        }
    }
}
