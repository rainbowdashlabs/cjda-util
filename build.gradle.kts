import org.gradle.internal.impldep.org.apache.commons.lang.CharEncoding

plugins {
    java
    `maven-publish`
    `java-library`
    id("de.chojo.publishdata") version "1.0.4"
    id("org.cadixdev.licenser") version "0.6.1"
}

repositories {
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api("net.dv8tion", "JDA", "5.0.0-alpha.12")
    api("org.apache.commons", "commons-text", "1.9")
    api("club.minnced", "discord-webhooks", "0.8.0")

    // Serialization
    api("com.google.guava", "guava", "31.1-jre")
    api("com.fasterxml.jackson.core", "jackson-databind", "2.13.3")
    api("io.javalin", "javalin", "4.6.0")

    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

group = "de.chojo"
version = "2.3.4+alpha.11"
description = "Discord utilities for use with JDA"

publishData {
    useEldoNexusRepos()
    publishComponent("java")
}


publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            name = "EldoNexus"
            setUrl(publishData.getRepository())
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_17
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    compileJava {
        options.encoding = CharEncoding.UTF_8
    }

    javadoc {
        options.encoding = CharEncoding.UTF_8
    }
}
