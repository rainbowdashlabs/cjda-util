plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.publishdata)
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    api(libs.jda)
    api(libs.apache.commons.text)
    api(libs.google.guava)
    api(libs.jackson.databind)
    api(libs.bundles.javalin)
    compileOnly(libs.jsr305)
    annotationProcessor(libs.javalin.openapi.annotation)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testRuntimeOnly(libs.junit.launcher)
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        target("**/*.java")
    }
}

group = "de.chojo"
version = "2.15.1+jda-${libs.versions.jda.get()}"
description = "Discord utilities for use with JDA"

publishData {
    useEldoNexusRepos(false)
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

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
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
        options.encoding = "utf-8"
    }

    javadoc {
        val options = options as StandardJavadocDocletOptions
        options.encoding = "utf-8"
        options.links("https://docs.jda.wiki/")
    }
}
