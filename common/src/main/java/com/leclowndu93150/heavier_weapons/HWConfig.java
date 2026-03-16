package com.leclowndu93150.heavier_weapons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HWConfig {

    public static boolean hitstopEnabled = true;
    public static int defaultHitstopTicks = 2;
    public static int critBonusTicks = 2;
    public static boolean cameraShakeEnabled = true;
    public static double cameraShakeIntensity = 1.0;
    public static boolean entityFreezeEnabled = true;
    public static int hitstopDelayTicks = 0;
    public static Map<String, Integer> categoryDurations = new HashMap<>();

    private static final String[] DEFAULT_CATEGORIES = {
            "dagger:0", "claw:0", "fist:0", "sickle:0", "soul_knife:0", "wand:0",
            "rapier:1",
            "sword:2", "cutlass:2", "katana:2", "coral_blade:2", "twin_blade:2",
            "spear:2", "trident:2", "staff:2", "pickaxe:2",
            "axe:3", "mace:3", "glaive:3", "scythe:3", "battlestaff:3", "lance:3",
            "claymore:4", "heavy_axe:4", "double_axe:4", "halberd:4",
            "hammer:5", "anchor:5"
    };

    public static void load() {
        categoryDurations = new HashMap<>();
        for (String entry : DEFAULT_CATEGORIES) {
            String[] parts = entry.split(":");
            categoryDurations.put(parts[0], Integer.parseInt(parts[1]));
        }

        Path configDir = getConfigDir();
        Path configFile = configDir.resolve("heavier_weapons.properties");

        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                Properties props = new Properties();
                props.load(in);
                hitstopEnabled = Boolean.parseBoolean(props.getProperty("hitstopEnabled", "true"));
                defaultHitstopTicks = Integer.parseInt(props.getProperty("defaultHitstopTicks", "2"));
                critBonusTicks = Integer.parseInt(props.getProperty("critBonusTicks", "2"));
                cameraShakeEnabled = Boolean.parseBoolean(props.getProperty("cameraShakeEnabled", "true"));
                cameraShakeIntensity = Double.parseDouble(props.getProperty("cameraShakeIntensity", "1.0"));
                entityFreezeEnabled = Boolean.parseBoolean(props.getProperty("entityFreezeEnabled", "true"));
                hitstopDelayTicks = Integer.parseInt(props.getProperty("hitstopDelayTicks", "0"));

                String cats = props.getProperty("categoryDurations", "");
                if (!cats.isEmpty()) {
                    categoryDurations.clear();
                    for (String entry : cats.split(",")) {
                        String[] parts = entry.trim().split(":");
                        if (parts.length == 2) {
                            categoryDurations.put(parts[0], Integer.parseInt(parts[1]));
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                Constants.LOG.warn("Failed to load config, using defaults", e);
            }
        } else {
            save(configFile);
        }
    }

    private static void save(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
            Properties props = new Properties();
            props.setProperty("hitstopEnabled", String.valueOf(hitstopEnabled));
            props.setProperty("defaultHitstopTicks", String.valueOf(defaultHitstopTicks));
            props.setProperty("critBonusTicks", String.valueOf(critBonusTicks));
            props.setProperty("cameraShakeEnabled", String.valueOf(cameraShakeEnabled));
            props.setProperty("cameraShakeIntensity", String.valueOf(cameraShakeIntensity));
            props.setProperty("entityFreezeEnabled", String.valueOf(entityFreezeEnabled));
            props.setProperty("hitstopDelayTicks", String.valueOf(hitstopDelayTicks));
            StringBuilder sb = new StringBuilder();
            for (String entry : DEFAULT_CATEGORIES) {
                if (sb.length() > 0) sb.append(",");
                sb.append(entry);
            }
            props.setProperty("categoryDurations", sb.toString());
            try (OutputStream out = Files.newOutputStream(configFile)) {
                props.store(out, "Heavier Weapons Configuration");
            }
        } catch (IOException e) {
            Constants.LOG.warn("Failed to save config", e);
        }
    }

    private static Path getConfigDir() {
        return Path.of("config");
    }

    public static int getHitstopTicks(String category) {
        if (category == null) return defaultHitstopTicks;
        return categoryDurations.getOrDefault(category, defaultHitstopTicks);
    }
}
