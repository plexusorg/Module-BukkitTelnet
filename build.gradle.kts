plugins {
    java
    `maven-publish`
}

group = "dev.plex"
description = "Module-BukkitTelnet"
version = "1.0"

repositories {
    mavenLocal()

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

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

}


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.getByName<Jar>("jar") {
    archiveBaseName.set("Module-BukkitTelnet")
}

