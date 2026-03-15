package com.leclowndu93150.heavier_weapons.network;

import com.leclowndu93150.heavier_weapons.HeavierWeapons;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = HeavierWeapons.MODID)
public class HitstopEventListener {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!HeavierWeapons.enabled) return;

        var source = event.getSource();
        if (source.getEntity() instanceof ServerPlayer attacker) {
            HitstopServerHandler.onPlayerDamageEntity(attacker, event.getEntity().getId(), source);
        }
    }
}
