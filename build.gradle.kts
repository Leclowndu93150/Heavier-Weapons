plugins {
    id("dev.prism")
}

group = "com.leclowndu93150"
version = "1.1.0"

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
        changelog = "fix crash without critical strike"

        curseforge {
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            projectId = "1487841"
        }

        dependencies {
            curseforge {
                requires("better-combat-by-daedelus")
                requires("cloth-config")
                requires("playeranimator")
            }
        }
    }

    version("1.20.1") {
        parchmentMinecraftVersion = "1.20.1"
        parchmentMappingsVersion = "2023.09.03"

        common {
            modCompileOnly("curse.maven:better-combat-by-daedelus-639842:7287334")
            modCompileOnly("curse.maven:playeranimator-658587:4587214")
        }

        fabric {
            loaderVersion = "0.16.9"
            fabricApi("0.92.1+1.20.1")

            dependencies {
                modImplementation("curse.maven:better-combat-by-daedelus-639842:7287333")
                modImplementation("curse.maven:cloth-config-348521:5729104")
                modImplementation("curse.maven:playeranimator-658587:4587215")
            }
        }

        forge {
            loaderVersion = "47.2.30"

            dependencies {
                modImplementation("curse.maven:better-combat-by-daedelus-639842:7287334")
                modImplementation("curse.maven:cloth-config-348521:5729105")
                modImplementation("curse.maven:playeranimator-658587:4587214")
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
                compileOnly("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
                annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.7-beta.1")
                jarJar("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:0.3.7-beta.1")
            }
        }
    }
}
