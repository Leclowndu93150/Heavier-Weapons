package com.leclowndu93150.heavier_weapons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeavierWeapons {
    public static final String MODID = "heavier_weapons";
    public static final Logger LOG = LoggerFactory.getLogger("Heavier Weapons");

    public static boolean enabled = true;

    public static void init() {
        HWConfig.load();
    }
}
