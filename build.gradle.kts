plugins {
    id("dev.prism")
}

group = "com.leclowndu93150"
version = "1.2.0"

prism {
    curseMaven()
    modrinthMaven()
    maven("bawnorton", "https://maven.bawnorton.com/releases")

    metadata {
        modId = "heavier_weapons"
        name = "Heavier Weapons"
        description = "Adds hitstop, camera shake, and entity freeze effects on weapon impact."
        license = "MIT"
        author("Leclowndu93150")
    }

    publishing {
        status = STABLE
        changelog = """
            Fixes
            - Config changes now apply: every value is parsed on its own (one bad value no longer resets the rest), edits apply without restarting, and the file is read from the loader's config folder
            - New config keys are added to existing config files automatically, the rest of the file is left untouched

            Additions (all optional, see heavier_weapons.properties)
            - maxEntityFreezeTicks: cap how long hit mobs freeze, so long hitstops don't break mob animations
            - entityBlacklist: entities that are never frozen or stunned, supports ids and #tags, bosses are blacklisted by default
            - Hitstop impact sounds that get heavier with the weapon (light, medium and heavy layers), played on hit or after the hitstop delay, every layer can be swapped for any sound
            - Per-category hitstop delays and hitstop delay scaling with attack speed
            - Stun system: mobs build up stagger from hits and get stunned once it breaks their threshold, with per-mob thresholds, armor resistance, a backstab bonus and resistance after repeated stuns
            - Shield stun: blocking a melee attack stuns the attacker, a disabled shield stuns the attacker and gives you hitstop and camera shake
            - Parry: blocking right after raising your shield stuns the attacker longer and makes them take extra damage, also works with Sword Blocking Mechanics parries
        """.trimIndent()

        curseforge {
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            projectId = "1487841"
        }

        dependencies {
            curseforge {
                requires("better-combat-by-daedelus")
                requires("cloth-config")
                requires("playeranimator")
                optional("sword-blocking-mechanics")
            }
        }
    }

    version("1.20.1") {
        parchmentMinecraftVersion = "1.20.1"
        parchmentMappingsVersion = "2023.09.03"

        common {
            modCompileOnly("curse.maven:better-combat-by-daedelus-639842:7287334")
            modCompileOnly("curse.maven:playeranimator-658587:4587214")
            modCompileOnly("curse.maven:sword-blocking-mechanics-969424:6043941")
        }

        fabric {
            loaderVersion = "0.16.9"
            fabricApi("0.92.1+1.20.1")

            dependencies {
                modImplementation("curse.maven:better-combat-by-daedelus-639842:7287333")
                modImplementation("curse.maven:cloth-config-348521:5729104")
                modImplementation("curse.maven:playeranimator-658587:4587215")
                modCompileOnly("curse.maven:sword-blocking-mechanics-969424:6043943")
            }
        }

        forge {
            loaderVersion = "47.2.30"

            dependencies {
                modImplementation("curse.maven:better-combat-by-daedelus-639842:7287334")
                modImplementation("curse.maven:cloth-config-348521:5729105")
                modImplementation("curse.maven:playeranimator-658587:4587214")
                modCompileOnly("curse.maven:sword-blocking-mechanics-969424:6043941")
            }
        }
    }

    version("1.21.1") {
        parchmentMinecraftVersion = "1.21.3"
        parchmentMappingsVersion = "2024.12.07"

        publishingDependencies {
            curseforge {
                requires("better-combat-by-daedelus")
                requires("cloth-config")
                requires("playeranimator")
                optional("critical-strike")
            }
        }

        common {
            modCompileOnly("curse.maven:better-combat-by-daedelus-639842:7721843")
            modCompileOnly("curse.maven:playeranimator-658587:7389814")
            modCompileOnly("curse.maven:critical-strike-1379562:7224268")
            modCompileOnly("curse.maven:sword-blocking-mechanics-969424:6043989")
            compileOnly("org.ow2.asm:asm-tree:9.7")
            compileOnly("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
            annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
        }

        fabric {
            loaderVersion = "0.16.9"
            fabricApi("0.116.6+1.21.1")

            dependencies {
                modImplementation("curse.maven:better-combat-by-daedelus-639842:7721846")
                modImplementation("curse.maven:cloth-config-348521:5729125")
                modImplementation("curse.maven:playeranimator-658587:7389821")
                modImplementation("maven.modrinth:tiny-config:3.1.0-fabric")
                modCompileOnly("curse.maven:critical-strike-1379562:7224269")
                modCompileOnly("curse.maven:sword-blocking-mechanics-969424:6043988")
                compileOnly("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
                annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
                modImplementation("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.3.7-beta.1")
            }
        }

        neoforge {
            loaderVersion = "21.1.219"

            dependencies {
                implementation("curse.maven:better-combat-by-daedelus-639842:7721843")
                implementation("curse.maven:cloth-config-348521:5729127")
                implementation("curse.maven:playeranimator-658587:7389814")
                implementation("maven.modrinth:tiny-config:3.1.0-neoforge")
                compileOnly("curse.maven:critical-strike-1379562:7224268")
                compileOnly("curse.maven:sword-blocking-mechanics-969424:6043989")
                compileOnly("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
                annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
                jarJar("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:0.3.7-beta.1")
            }
        }
    }
}
