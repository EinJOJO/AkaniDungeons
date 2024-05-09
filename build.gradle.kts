plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
}

group = "it.einjojo.akani"
version = "1.0.0"

repositories {

    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.akani.dev/releases")
    maven("https://repo.oraxen.com/releases")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(libs.paper)
    implementation(libs.acf)
    compileOnly(libs.akanicore)
    compileOnly("fr.minuskube.inv:smart-invs:1.2.7")
    compileOnly(libs.packeteventsspigot)
    annotationProcessor(libs.acf)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<Jar> {
        enabled = true
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(mapOf("version" to project.version.toString()))
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isIncremental = true
        options.compilerArgs.add("-parameters")

    }

    runServer {
        minecraftVersion("1.20.4")
    }

    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set("")
        //relocate("co.aikar.commands", "it.einjojo.akani.essentials.command.acf")
        //relocate("fr.mrmicky.fastboard", "it.einjojo.akani.essentials.scoreboard.fastboard")

    }
}