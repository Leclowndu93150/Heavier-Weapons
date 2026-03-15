package com.leclowndu93150.heavier_weapons;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = HeavierWeapons.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue HITSTOP_ENABLED = BUILDER
            .comment("Enable hitstop (animation freeze) on weapon impact")
            .define("hitstopEnabled", true);

    private static final ModConfigSpec.IntValue DEFAULT_HITSTOP_TICKS = BUILDER
            .comment("Default hitstop duration in ticks for weapons without a category mapping")
            .defineInRange("defaultHitstopTicks", 2, 0, 10);

    private static final ModConfigSpec.IntValue CRIT_BONUS_TICKS = BUILDER
            .comment("Extra hitstop ticks added on critical hits")
            .defineInRange("critBonusTicks", 2, 0, 10);

    private static final ModConfigSpec.BooleanValue CAMERA_SHAKE_ENABLED = BUILDER
            .comment("Enable camera shake on weapon impact")
            .define("cameraShakeEnabled", true);

    private static final ModConfigSpec.DoubleValue CAMERA_SHAKE_INTENSITY = BUILDER
            .comment("Camera shake intensity multiplier")
            .defineInRange("cameraShakeIntensity", 1.0, 0.0, 3.0);

    private static final ModConfigSpec.BooleanValue ENTITY_FREEZE_ENABLED = BUILDER
            .comment("Freeze hit entities in place during hitstop")
            .define("entityFreezeEnabled", true);

    private static final ModConfigSpec.IntValue HITSTOP_DELAY_TICKS = BUILDER
            .comment("Delay in ticks after attack connects before hitstop occurs (0 = immediate)")
            .defineInRange("hitstopDelayTicks", 0, 0, 10);

    private static final ModConfigSpec.BooleanValue CRITICAL_STRIKE_COMPAT = BUILDER
            .comment("Enable Critical Strike mod compatibility (detect RNG crits for hitstop bonus)")
            .define("criticalStrikeCompat", true);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> CATEGORY_HITSTOP_DURATIONS = BUILDER
            .comment("Hitstop duration per weapon category, format: 'category:ticks'")
            .defineListAllowEmpty("categoryHitstopDurations", List.of(
                    "dagger:0", "claw:0", "fist:0", "sickle:0", "soul_knife:0", "wand:0",
                    "rapier:1",
                    "sword:2", "cutlass:2", "katana:2", "coral_blade:2", "twin_blade:2",
                    "spear:2", "trident:2", "staff:2", "pickaxe:2",
                    "axe:3", "mace:3", "glaive:3", "scythe:3", "battlestaff:3", "lance:3",
                    "claymore:4", "heavy_axe:4", "double_axe:4", "halberd:4",
                    "hammer:5", "anchor:5"
            ), Config::validateCategoryEntry);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean hitstopEnabled;
    public static int defaultHitstopTicks;
    public static int critBonusTicks;
    public static boolean cameraShakeEnabled;
    public static double cameraShakeIntensity;
    public static boolean entityFreezeEnabled;
    public static int hitstopDelayTicks;
    public static boolean criticalStrikeCompat;
    public static Map<String, Integer> categoryDurations = new HashMap<>();

    private static boolean validateCategoryEntry(final Object obj) {
        if (!(obj instanceof String str)) return false;
        String[] parts = str.split(":");
        if (parts.length != 2) return false;
        try {
            int ticks = Integer.parseInt(parts[1]);
            return ticks >= 0 && ticks <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        hitstopEnabled = HITSTOP_ENABLED.get();
        defaultHitstopTicks = DEFAULT_HITSTOP_TICKS.get();
        critBonusTicks = CRIT_BONUS_TICKS.get();
        cameraShakeEnabled = CAMERA_SHAKE_ENABLED.get();
        cameraShakeIntensity = CAMERA_SHAKE_INTENSITY.get();
        entityFreezeEnabled = ENTITY_FREEZE_ENABLED.get();
        hitstopDelayTicks = HITSTOP_DELAY_TICKS.get();
        criticalStrikeCompat = CRITICAL_STRIKE_COMPAT.get();

        categoryDurations = new HashMap<>();
        for (String entry : CATEGORY_HITSTOP_DURATIONS.get()) {
            String[] parts = entry.split(":");
            categoryDurations.put(parts[0], Integer.parseInt(parts[1]));
        }
    }

    public static int getHitstopTicks(String category) {
        if (category == null) return defaultHitstopTicks;
        return categoryDurations.getOrDefault(category, defaultHitstopTicks);
    }
}
