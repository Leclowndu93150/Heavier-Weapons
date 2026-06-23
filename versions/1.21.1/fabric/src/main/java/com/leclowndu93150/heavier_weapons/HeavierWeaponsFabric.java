package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.network.FabricNetworking;
import com.leclowndu93150.heavier_weapons.network.HitstopServerHandler;
import net.fabricmc.api.ModInitializer;

public class HeavierWeaponsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        HeavierWeapons.init();
        FabricNetworking.init();
        HitstopServerHandler.init();
    }
}
