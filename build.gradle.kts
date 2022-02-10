plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.0")
}

group = "ru.ckateptb"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok", "lombok", "1.18.22")
    annotationProcessor("org.projectlombok", "lombok", "1.18.22")
}

tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.${archiveExtension.getOrElse("jar")}")
    }
    build {
        dependsOn(shadowJar)
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    named<Copy>("processResources") {
        from("LICENSE") {
            rename { "${project.name.toUpperCase()}_${it}" }
        }
    }
}

publishing {
    publications {
        publications.create<MavenPublication>("maven") {
            artifacts {
                artifact(tasks.shadowJar) {
                    classifier = ""
                }
                artifact(tasks["sourcesJar"])
            }
        }
    }
}