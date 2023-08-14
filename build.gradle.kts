plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
    api("net.wesjd:anvilgui:1.7.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    compileOnly ("me.clip:placeholderapi:2.11.3")
}

group = "me.foxikle"
version = "2.0.0-beta2"
description = "FoxRank"
java.sourceCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
tasks {
    assemble {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("C:/Users/tscal/Desktop/testserver/plugins/FoxRank-${project.version}.jar"))
    }

    shadowJar {
        relocate("org.bstats", "dev.foxikle.dependencies.bstats")
    }
}
