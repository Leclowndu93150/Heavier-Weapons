package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.network.FabricNetworking;
import net.fabricmc.api.ModInitializer;

public class HeavierWeaponsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        FabricNetworking.init();
    }
}
