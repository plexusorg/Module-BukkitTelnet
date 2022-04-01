plugins {
    java
    `maven-publish`
}

group = "dev.plex"
description = "Module-BukkitTelnet"
version = "1.0"

repositories {
    mavenCentral()

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://nexus.telesphoreo.me/repository/plex")
    }

    maven {
        url = uri("https://nexus.telesphoreo.me/repository/totalfreedom")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("dev.plex:Plex:0.10-SNAPSHOT")
    compileOnly("me.totalfreedom:BukkitTelnet:4.7") {
        exclude("org.spigotmc", "spigot-api")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.getByName<Jar>("jar") {
    archiveBaseName.set("Plex-BukkitTelnet")
    archiveVersion.set("")
}