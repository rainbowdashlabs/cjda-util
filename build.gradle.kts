import org.gradle.internal.impldep.org.apache.commons.lang.CharEncoding

plugins {
    java
    `maven-publish`
    `java-library`
    id("de.chojo.publishdata") version "1.0.8"
    id("org.cadixdev.licenser") version "0.6.1"
}

repositories {
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api("net.dv8tion", "JDA", "5.0.0-alpha.16")
    api("org.apache.commons", "commons-text", "1.9")
    api("club.minnced", "discord-webhooks", "0.8.2")

    // Serialization
    api("com.google.guava", "guava", "31.1-jre")
    api("com.fasterxml.jackson.core", "jackson-databind", "2.13.3")

    // web api
    api("io.javalin", "javalin", "4.6.4")
    api("io.javalin", "javalin-openapi", "4.6.4")


    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

group = "de.chojo"
version = "2.6.2+alpha.15"
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

    toolchain{
        languageVersion.set(JavaLanguageVersion.of(17))
    }
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
        val options = options as StandardJavadocDocletOptions
        options.encoding = CharEncoding.UTF_8
        options.links(
            "https://ci.dv8tion.net/job/JDA/javadoc/",
            "https://javadoc.io/doc/io.javalin/javalin/latest/",
            "https://javadoc.io/doc/com.google.guava/guava/latest/"
        )
    }
}
