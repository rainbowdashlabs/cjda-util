plugins {
    java
    `maven-publish`
    `java-library`
    id("de.chojo.publishdata") version "1.4.0"
    alias(libs.plugins.spotless)
    id("org.openrewrite.rewrite") version "7.23.0"
}

repositories {
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    rewrite(libs.jda)
    api(libs.jda)
    api("org.apache.commons", "commons-text", "1.15.0")

    // Serialization
    api("com.google.guava", "guava", "33.5.0-jre")
    api("com.fasterxml.jackson.core", "jackson-databind", "2.21.0")

    // web api
    api(libs.bundles.javalin)
    annotationProcessor(libs.javalin.openapiannotation)

    // unit testing
    testImplementation(libs.bundles.junit)
    testImplementation("org.mockito", "mockito-core", "5.21.0")
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        target("**/*.java")
    }
}

group = "de.chojo"
version = "2.12.0+jda-${libs.versions.jda.get()}"
description = "Discord utilities for use with JDA"

publishData {
    useEldoNexusRepos(false)
    publishComponent("java")
}

rewrite {
    activeRecipe(
        "net.dv8tion.MigrateComponentsV2",
        "net.dv8tion.MigrateComponentsV2Packages",
        "net.dv8tion.MigrateComponentsV2CustomIdGetters"
    )
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
        options.links(
            "https://ci.dv8tion.net/job/JDA/javadoc/",
            "https://javadoc.io/doc/io.javalin/javalin/6.2.0/",
            "https://javadoc.io/doc/com.google.guava/guava/latest/"
        )
    }
}
