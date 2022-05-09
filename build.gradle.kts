plugins {
    java
    `maven-publish`
}

group = "dev.plex"
version = "1.0"
description = "Module-BukkitTelnet"

repositories {
    mavenCentral()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://nexus.telesphoreo.me/repository/plex-snapshots/")
    }

    maven {
        url = uri("https://nexus.telesphoreo.me/repository/totalfreedom/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("dev.plex:server:1.1-SNAPSHOT")
    compileOnly("dev.plex:api:1.1-SNAPSHOT")
    compileOnly("me.totalfreedom:BukkitTelnet:4.8") {
        exclude("org.papermc.paper", "paper-api")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
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
    archiveBaseName.set("Plex-BukkitTelnet")
    archiveVersion.set("")
}

