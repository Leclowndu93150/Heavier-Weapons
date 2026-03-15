package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.client.CameraShakeManager;
import com.leclowndu93150.heavier_weapons.client.EntityFreezeManager;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import com.leclowndu93150.heavier_weapons.client.HitstopManager;
import com.leclowndu93150.heavier_weapons.network.HitstopPacket;
import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import com.mojang.brigadier.Command;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static net.minecraft.commands.Commands.literal;

@Mod(HeavierWeapons.MODID)
public class HeavierWeapons {
    public static final String MODID = "heavier_weapons";
    public static boolean enabled = true;

    public HeavierWeapons(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::registerPayloads);
        HitstopServerHandler.init();
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(MODID).versioned("1");
        registrar.playToClient(HitstopPacket.TYPE, HitstopPacket.STREAM_CODEC, this::handleHitstopPacket);
    }

    private void handleHitstopPacket(HitstopPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> HitstopHandler.handlePacket(packet));
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
                                enabled = true;
                                ctx.getSource().sendSuccess(() -> Component.literal("Hitstop enabled"), false);
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(literal("off").executes(ctx -> {
                                enabled = false;
                                ctx.getSource().sendSuccess(() -> Component.literal("Hitstop disabled"), false);
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(literal("status").executes(ctx -> {
                                ctx.getSource().sendSuccess(() -> Component.literal("Hitstop: " + (enabled ? "ON" : "OFF")), false);
                                return Command.SINGLE_SUCCESS;
                            }))
            );
        }
    }
}
