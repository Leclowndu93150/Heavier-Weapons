package com.leclowndu93150.heavier_weapons.compat;

import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.world.entity.player.Player;

public class SwordBlockingCompat {

    public static boolean isBlocking(Player player) {
        return SwordBlockingHandler.isActiveItemStackBlocking(player);
    }

    public static boolean isParrying(Player player) {
        return SwordBlockingHandler.getParryStrengthScale(player) > 0.0;
    }
}
