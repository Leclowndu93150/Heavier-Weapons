package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.client.CameraShakeManager;
import com.leclowndu93150.heavier_weapons.client.EntityFreezeManager;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import com.leclowndu93150.heavier_weapons.client.HitstopManager;
import com.leclowndu93150.heavier_weapons.network.ForgeNetworking;
import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import com.mojang.brigadier.Command;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.minecraft.commands.Commands.literal;

@Mod(Constants.MOD_ID)
public class HeavierWeaponsForge {

    public HeavierWeaponsForge() {
        CommonClass.init();
        ForgeNetworking.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        HitstopHandler.init();
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (!CommonClass.enabled) return;
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof ServerPlayer attacker) {
            HitstopServerHandler.onPlayerDamageEntity(attacker, event.getEntity().getId(), source);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        HitstopManager.tick();
        CameraShakeManager.tick();
        EntityFreezeManager.tick();
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                literal("hitstop")
                        .then(literal("on").executes(ctx -> {
                            CommonClass.enabled = true;
                            ctx.getSource().sendSuccess(() -> Component.literal("Hitstop enabled"), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                        .then(literal("off").executes(ctx -> {
                            CommonClass.enabled = false;
                            ctx.getSource().sendSuccess(() -> Component.literal("Hitstop disabled"), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                        .then(literal("status").executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal("Hitstop: " + (CommonClass.enabled ? "ON" : "OFF")), false);
                            return Command.SINGLE_SUCCESS;
                        }))
        );
    }
}
