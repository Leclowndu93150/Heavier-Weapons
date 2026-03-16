package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.client.CameraShakeManager;
import com.leclowndu93150.heavier_weapons.client.EntityFreezeManager;
import com.leclowndu93150.heavier_weapons.client.HitstopHandler;
import com.leclowndu93150.heavier_weapons.client.HitstopManager;
import com.leclowndu93150.heavier_weapons.network.FabricNetworking;
import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HeavierWeaponsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HitstopHandler.init();
        FabricNetworking.initClient();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HitstopManager.tick();
            CameraShakeManager.tick();
            EntityFreezeManager.tick();
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("hitstop")
                            .then(literal("on").executes(ctx -> {
                                CommonClass.enabled = true;
                                ctx.getSource().sendFeedback(Component.literal("Hitstop enabled"));
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(literal("off").executes(ctx -> {
                                CommonClass.enabled = false;
                                ctx.getSource().sendFeedback(Component.literal("Hitstop disabled"));
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(literal("status").executes(ctx -> {
                                ctx.getSource().sendFeedback(Component.literal("Hitstop: " + (CommonClass.enabled ? "ON" : "OFF")));
                                return Command.SINGLE_SUCCESS;
                            }))
            );
        });
    }
}
