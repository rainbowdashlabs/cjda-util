import org.gradle.internal.impldep.org.apache.commons.lang.CharEncoding

plugins {
    java
    `maven-publish`
    `java-library`
    id("de.chojo.publishdata") version "1.4.0"
    id("org.cadixdev.licenser") version "0.6.1"
}

repositories {
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api("net.dv8tion", "JDA", "5.0.0")
    api("org.apache.commons", "commons-text", "1.12.0")
    api("club.minnced", "discord-webhooks", "0.8.4")

    // Serialization
    api("com.google.guava", "guava", "33.2.1-jre")
    api("com.fasterxml.jackson.core", "jackson-databind", "2.17.2")

    // web api
    api("io.javalin", "javalin", "4.6.8")
    api("io.javalin", "javalin-openapi", "4.6.8")

    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("org.mockito", "mockito-core", "5.12.0")
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

group = "de.chojo"
version = "2.9.8+jda-5.0.0"
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
