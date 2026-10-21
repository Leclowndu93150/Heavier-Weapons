package com.leclowndu93150.heavier_weapons;

import com.leclowndu93150.heavier_weapons.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

public class HWConfig {

    private static final String DEFAULT_CATEGORIES = String.join(",",
            "dagger:0", "claw:0", "fist:0", "sickle:0", "soul_knife:0", "wand:0",
            "rapier:1",
            "sword:2", "cutlass:2", "katana:2", "coral_blade:2", "twin_blade:2",
            "spear:2", "trident:2", "staff:2", "pickaxe:2",
            "axe:3", "mace:3", "glaive:3", "scythe:3", "battlestaff:3", "lance:3",
            "claymore:4", "heavy_axe:4", "double_axe:4", "halberd:4",
            "hammer:5", "anchor:5"
    );
    private static final String DEFAULT_ENTITY_BLACKLIST = "#c:bosses,#forge:bosses";
    private static final String DEFAULT_LIGHT_SOUNDS = "heavier_weapons:hitstop.light";
    private static final String DEFAULT_MEDIUM_SOUNDS = "heavier_weapons:hitstop.medium,heavier_weapons:hitstop.crack";
    private static final String DEFAULT_HEAVY_SOUNDS = "heavier_weapons:hitstop.heavy,heavier_weapons:hitstop.heavy_crack,heavier_weapons:hitstop.slam";

    public static boolean hitstopEnabled = true;
    public static int defaultHitstopTicks = 2;
    public static int critBonusTicks = 2;
    public static int hitstopDelayTicks = 0;
    public static boolean scaleDelayWithAttackSpeed = false;
    public static Map<String, Integer> categoryDurations = parseCategories(DEFAULT_CATEGORIES);
    public static Map<String, Integer> categoryDelays = Map.of();
    public static boolean hitstopSoundEnabled = false;
    public static List<ResourceLocation> lightHitstopSounds = parseSounds(DEFAULT_LIGHT_SOUNDS);
    public static List<ResourceLocation> mediumHitstopSounds = parseSounds(DEFAULT_MEDIUM_SOUNDS);
    public static List<ResourceLocation> heavyHitstopSounds = parseSounds(DEFAULT_HEAVY_SOUNDS);
    public static double hitstopSoundVolume = 1.0;
    public static double hitstopSoundPitch = 1.0;
    public static boolean hitstopSoundAfterDelay = false;
    public static boolean cameraShakeEnabled = true;
    public static double cameraShakeIntensity = 1.0;
    public static boolean entityFreezeEnabled = true;
    public static int maxEntityFreezeTicks = -1;
    public static boolean entityShakeEnabled = true;
    public static double entityShakeIntensity = 1.0;
    public static EntityFilter entityBlacklist = EntityFilter.parse(DEFAULT_ENTITY_BLACKLIST);
    public static boolean stunEnabled = false;
    public static double stunDurationMultiplier = 3.0;
    public static double staggerThresholdPerHealth = 0.08;
    public static Map<ResourceLocation, Double> staggerThresholds = Map.of();
    public static double armorStaggerResistance = 0.05;
    public static double backstabMultiplier = 2.0;
    public static double stunResistancePerStun = 0.5;
    public static int stunResistanceDecayTicks = 100;
    public static boolean shieldStunEnabled = false;
    public static int shieldBlockStunTicks = 10;
    public static int shieldBreakStunTicks = 20;
    public static int shieldBreakHitstopTicks = 6;
    public static boolean parryEnabled = false;
    public static int parryWindowTicks = 4;
    public static int parryStunTicks = 40;
    public static int parryHitstopTicks = 4;
    public static int parryVulnerabilityTicks = 60;
    public static double parryDamageMultiplier = 1.5;

    private static Path configFile;
    private static FileTime lastModified;

    public static synchronized void load() {
        configFile = Services.PLATFORM.getConfigDir().resolve("heavier_weapons.properties");
        boolean exists = Files.exists(configFile);
        Properties props = exists ? read() : new Properties();
        if (props == null) return;
        apply(props);

        List<Entry> missing = entries().stream().filter(entry -> !props.containsKey(entry.key())).toList();
        if (!missing.isEmpty()) {
            write(missing, !exists);
        }
    }

    public static synchronized void reloadIfChanged() {
        try {
            if (Files.getLastModifiedTime(configFile).equals(lastModified)) return;
        } catch (IOException e) {
            return;
        }
        Properties props = read();
        if (props != null) {
            apply(props);
        }
    }

    public static int getHitstopTicks(String category) {
        if (category == null) return defaultHitstopTicks;
        return categoryDurations.getOrDefault(category, defaultHitstopTicks);
    }

    public static int getHitstopDelay(String category, float attackDelayTicks) {
        int delay = category == null ? hitstopDelayTicks : categoryDelays.getOrDefault(category, hitstopDelayTicks);
        if (!scaleDelayWithAttackSpeed) return delay;
        return Math.round(delay * attackDelayTicks / 20.0f);
    }

    public static List<ResourceLocation> getHitstopSounds(int hitstopTicks) {
        if (hitstopTicks <= 1) return lightHitstopSounds;
        if (hitstopTicks <= 3) return mediumHitstopSounds;
        return heavyHitstopSounds;
    }

    public static int getEntityFreezeTicks(int hitstopTicks) {
        if (maxEntityFreezeTicks < 0) return hitstopTicks;
        return Math.min(hitstopTicks, maxEntityFreezeTicks);
    }

    private static Properties read() {
        try (InputStream in = Files.newInputStream(configFile)) {
            Properties props = new Properties();
            props.load(in);
            lastModified = Files.getLastModifiedTime(configFile);
            return props;
        } catch (IOException e) {
            Constants.LOG.warn("Failed to read config", e);
            return null;
        }
    }

    private static void apply(Properties props) {
        hitstopEnabled = get(props, "hitstopEnabled", true, Boolean::parseBoolean);
        defaultHitstopTicks = get(props, "defaultHitstopTicks", 2, Integer::parseInt);
        critBonusTicks = get(props, "critBonusTicks", 2, Integer::parseInt);
        hitstopDelayTicks = get(props, "hitstopDelayTicks", 0, Integer::parseInt);
        scaleDelayWithAttackSpeed = get(props, "scaleDelayWithAttackSpeed", false, Boolean::parseBoolean);
        String categories = props.getProperty("categoryDurations", "").trim();
        categoryDurations = parseCategories(categories.isEmpty() ? DEFAULT_CATEGORIES : categories);
        categoryDelays = parseCategories(props.getProperty("categoryDelays", ""));
        hitstopSoundEnabled = get(props, "hitstopSoundEnabled", false, Boolean::parseBoolean);
        lightHitstopSounds = parseSounds(props.getProperty("hitstopSoundsLight", DEFAULT_LIGHT_SOUNDS));
        mediumHitstopSounds = parseSounds(props.getProperty("hitstopSoundsMedium", DEFAULT_MEDIUM_SOUNDS));
        heavyHitstopSounds = parseSounds(props.getProperty("hitstopSoundsHeavy", DEFAULT_HEAVY_SOUNDS));
        hitstopSoundVolume = get(props, "hitstopSoundVolume", 1.0, Double::parseDouble);
        hitstopSoundPitch = get(props, "hitstopSoundPitch", 1.0, Double::parseDouble);
        hitstopSoundAfterDelay = get(props, "hitstopSoundAfterDelay", false, Boolean::parseBoolean);
        cameraShakeEnabled = get(props, "cameraShakeEnabled", true, Boolean::parseBoolean);
        cameraShakeIntensity = get(props, "cameraShakeIntensity", 1.0, Double::parseDouble);
        entityFreezeEnabled = get(props, "entityFreezeEnabled", true, Boolean::parseBoolean);
        maxEntityFreezeTicks = get(props, "maxEntityFreezeTicks", -1, Integer::parseInt);
        entityShakeEnabled = get(props, "entityShakeEnabled", true, Boolean::parseBoolean);
        entityShakeIntensity = get(props, "entityShakeIntensity", 1.0, Double::parseDouble);
        entityBlacklist = EntityFilter.parse(props.getProperty("entityBlacklist", DEFAULT_ENTITY_BLACKLIST));
        stunEnabled = get(props, "stunEnabled", false, Boolean::parseBoolean);
        stunDurationMultiplier = get(props, "stunDurationMultiplier", 3.0, Double::parseDouble);
        staggerThresholdPerHealth = get(props, "staggerThresholdPerHealth", 0.08, Double::parseDouble);
        staggerThresholds = parseThresholds(props.getProperty("staggerThresholds", ""));
        armorStaggerResistance = get(props, "armorStaggerResistance", 0.05, Double::parseDouble);
        backstabMultiplier = get(props, "backstabMultiplier", 2.0, Double::parseDouble);
        stunResistancePerStun = get(props, "stunResistancePerStun", 0.5, Double::parseDouble);
        stunResistanceDecayTicks = get(props, "stunResistanceDecayTicks", 100, Integer::parseInt);
        shieldStunEnabled = get(props, "shieldStunEnabled", false, Boolean::parseBoolean);
        shieldBlockStunTicks = get(props, "shieldBlockStunTicks", 10, Integer::parseInt);
        shieldBreakStunTicks = get(props, "shieldBreakStunTicks", 20, Integer::parseInt);
        shieldBreakHitstopTicks = get(props, "shieldBreakHitstopTicks", 6, Integer::parseInt);
        parryEnabled = get(props, "parryEnabled", false, Boolean::parseBoolean);
        parryWindowTicks = get(props, "parryWindowTicks", 4, Integer::parseInt);
        parryStunTicks = get(props, "parryStunTicks", 40, Integer::parseInt);
        parryHitstopTicks = get(props, "parryHitstopTicks", 4, Integer::parseInt);
        parryVulnerabilityTicks = get(props, "parryVulnerabilityTicks", 60, Integer::parseInt);
        parryDamageMultiplier = get(props, "parryDamageMultiplier", 1.5, Double::parseDouble);
    }

    private static <T> T get(Properties props, String key, T fallback, Function<String, T> parser) {
        String value = props.getProperty(key);
        if (value == null) return fallback;
        T parsed = tryParse(value, parser);
        if (parsed != null) return parsed;
        Constants.LOG.warn("Invalid value '{}' for {}, using {}", value, key, fallback);
        return fallback;
    }

    private static <T> T tryParse(String value, Function<String, T> parser) {
        try {
            return parser.apply(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Map<String, Integer> parseCategories(String value) {
        Map<String, Integer> categories = new LinkedHashMap<>();
        for (String entry : value.split(",")) {
            if (entry.isBlank()) continue;
            String[] parts = entry.split(":");
            Integer ticks = parts.length == 2 ? tryParse(parts[1], Integer::parseInt) : null;
            if (ticks != null) {
                categories.put(parts[0].trim(), ticks);
            } else {
                Constants.LOG.warn("Invalid category entry '{}', expected category:ticks", entry.trim());
            }
        }
        return categories;
    }

    private static List<ResourceLocation> parseSounds(String value) {
        List<ResourceLocation> sounds = new ArrayList<>();
        for (String entry : value.split(",")) {
            if (entry.isBlank()) continue;
            ResourceLocation sound = ResourceLocation.tryParse(entry.trim());
            if (sound != null) {
                sounds.add(sound);
            } else {
                Constants.LOG.warn("Invalid sound '{}'", entry.trim());
            }
        }
        return sounds;
    }

    private static Map<ResourceLocation, Double> parseThresholds(String value) {
        Map<ResourceLocation, Double> thresholds = new HashMap<>();
        for (String entry : value.split(",")) {
            if (entry.isBlank()) continue;
            int split = entry.lastIndexOf(':');
            ResourceLocation id = split > 0 ? ResourceLocation.tryParse(entry.substring(0, split).trim()) : null;
            Double threshold = id != null ? tryParse(entry.substring(split + 1), Double::parseDouble) : null;
            if (threshold != null) {
                thresholds.put(id, threshold);
            } else {
                Constants.LOG.warn("Invalid staggerThresholds entry '{}', expected entity:threshold", entry.trim());
            }
        }
        return thresholds;
    }

    private static List<Entry> entries() {
        return List.of(
                new Entry("hitstopEnabled", "Enable hitstop (animation freeze) on weapon impact", hitstopEnabled),
                new Entry("defaultHitstopTicks", "Hitstop duration in ticks for weapons without a category mapping", defaultHitstopTicks),
                new Entry("critBonusTicks", "Extra hitstop ticks added on critical hits", critBonusTicks),
                new Entry("hitstopDelayTicks", "Delay in ticks after the attack connects before hitstop occurs (0 = immediate)", hitstopDelayTicks),
                new Entry("scaleDelayWithAttackSpeed", "Scale the hitstop delay with the weapon's attack speed (slower weapons get a longer delay, 1 attack per second = unchanged)", scaleDelayWithAttackSpeed),
                new Entry("categoryDurations", "Hitstop duration per weapon category, format: category:ticks,category:ticks", DEFAULT_CATEGORIES),
                new Entry("categoryDelays", "Hitstop delay per weapon category, overrides hitstopDelayTicks, format: category:ticks,category:ticks", ""),
                new Entry("hitstopSoundEnabled", "Play a sound when hitstop occurs", hitstopSoundEnabled),
                new Entry("hitstopSoundsLight", "Sounds layered together for light hits (1 hitstop tick or less), any sound ids", DEFAULT_LIGHT_SOUNDS),
                new Entry("hitstopSoundsMedium", "Sounds layered together for medium hits (2-3 hitstop ticks)", DEFAULT_MEDIUM_SOUNDS),
                new Entry("hitstopSoundsHeavy", "Sounds layered together for heavy hits (4+ hitstop ticks)", DEFAULT_HEAVY_SOUNDS),
                new Entry("hitstopSoundVolume", "Hitstop sound volume multiplier", hitstopSoundVolume),
                new Entry("hitstopSoundPitch", "Hitstop sound pitch multiplier", hitstopSoundPitch),
                new Entry("hitstopSoundAfterDelay", "Play the hitstop sound when the freeze starts after the delay instead of when the attack hits", hitstopSoundAfterDelay),
                new Entry("cameraShakeEnabled", "Enable camera shake on weapon impact", cameraShakeEnabled),
                new Entry("cameraShakeIntensity", "Camera shake intensity multiplier", cameraShakeIntensity),
                new Entry("entityFreezeEnabled", "Freeze hit entities in place during hitstop", entityFreezeEnabled),
                new Entry("maxEntityFreezeTicks", "Maximum ticks a hit entity stays frozen (-1 = same as the hitstop duration)", maxEntityFreezeTicks),
                new Entry("entityShakeEnabled", "Frozen entities shake in place along the hit direction before getting knocked back", entityShakeEnabled),
                new Entry("entityShakeIntensity", "Entity shake intensity multiplier", entityShakeIntensity),
                new Entry("entityBlacklist", "Entities that are never frozen or stunned, entity ids or #tags, format: #forge:bosses,minecraft:warden", DEFAULT_ENTITY_BLACKLIST),
                new Entry("stunEnabled", "Stun mobs (stop their AI) once enough hits break their stagger threshold", stunEnabled),
                new Entry("stunDurationMultiplier", "Stun duration in ticks = hitstop ticks of the breaking hit x this", stunDurationMultiplier),
                new Entry("staggerThresholdPerHealth", "Stagger needed to stun a mob per point of max health, each hit adds its hitstop ticks as stagger (0 = every hit stuns)", staggerThresholdPerHealth),
                new Entry("staggerThresholds", "Stagger threshold overrides per entity, format: minecraft:warden:40,minecraft:zombie:1", ""),
                new Entry("armorStaggerResistance", "Stagger threshold increase per armor point (0.05 = full diamond doubles it)", armorStaggerResistance),
                new Entry("backstabMultiplier", "Stagger and stun multiplier when hitting a mob from behind or a mob that is not targeting you", backstabMultiplier),
                new Entry("stunResistancePerStun", "Stun duration is divided by (1 + this x recent stuns), so repeated stuns get shorter (0 = no resistance)", stunResistancePerStun),
                new Entry("stunResistanceDecayTicks", "Ticks for one stack of stun resistance to wear off", stunResistanceDecayTicks),
                new Entry("shieldStunEnabled", "Blocking a melee attack with a shield stuns the attacker, a disabled shield stuns the attacker and gives you hitstop and camera shake", shieldStunEnabled),
                new Entry("shieldBlockStunTicks", "Attacker stun duration when you block", shieldBlockStunTicks),
                new Entry("shieldBreakStunTicks", "Attacker stun duration when your shield is disabled", shieldBreakStunTicks),
                new Entry("shieldBreakHitstopTicks", "Your hitstop duration when your shield is disabled", shieldBreakHitstopTicks),
                new Entry("parryEnabled", "Blocking right after raising a shield parries: longer attacker stun and damage vulnerability (also works with Sword Blocking Mechanics parries)", parryEnabled),
                new Entry("parryWindowTicks", "Ticks after the shield becomes active in which a block counts as a parry (Sword Blocking Mechanics uses its own window)", parryWindowTicks),
                new Entry("parryStunTicks", "Attacker stun duration on parry", parryStunTicks),
                new Entry("parryHitstopTicks", "Your hitstop duration on parry", parryHitstopTicks),
                new Entry("parryVulnerabilityTicks", "Ticks a parried attacker takes extra damage", parryVulnerabilityTicks),
                new Entry("parryDamageMultiplier", "Damage multiplier against a parried attacker", parryDamageMultiplier)
        );
    }

    private static void write(List<Entry> entries, boolean newFile) {
        List<String> lines = new ArrayList<>();
        if (newFile) {
            lines.add("# Heavier Weapons Configuration");
            lines.add("# Changes apply without restarting the game");
        }
        for (Entry entry : entries) {
            lines.add("");
            lines.add("# " + entry.comment());
            lines.add(entry.key() + "=" + entry.value());
        }

        try {
            Files.createDirectories(configFile.getParent());
            Files.write(configFile, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            lastModified = Files.getLastModifiedTime(configFile);
        } catch (IOException e) {
            Constants.LOG.warn("Failed to save config", e);
        }
    }

    private record Entry(String key, String comment, Object value) {
    }

    public record EntityFilter(Set<ResourceLocation> ids, List<TagKey<EntityType<?>>> tags) {

        public boolean matches(Entity entity) {
            EntityType<?> type = entity.getType();
            return ids.contains(EntityType.getKey(type)) || tags.stream().anyMatch(type::is);
        }

        private static EntityFilter parse(String value) {
            Set<ResourceLocation> ids = new HashSet<>();
            List<TagKey<EntityType<?>>> tags = new ArrayList<>();
            for (String entry : value.split(",")) {
                String trimmed = entry.trim();
                if (trimmed.isEmpty()) continue;
                boolean isTag = trimmed.startsWith("#");
                ResourceLocation id = ResourceLocation.tryParse(isTag ? trimmed.substring(1) : trimmed);
                if (id == null) {
                    Constants.LOG.warn("Invalid entityBlacklist entry '{}'", trimmed);
                } else if (isTag) {
                    tags.add(TagKey.create(Registries.ENTITY_TYPE, id));
                } else {
                    ids.add(id);
                }
            }
            return new EntityFilter(ids, tags);
        }
    }
}
