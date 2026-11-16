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
    api(libs.jspecify)
    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.javalin.openapi.annotation)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testRuntimeOnly(libs.junit.launcher)
}

spotless {
    java {
        palantirJavaFormat(libs.versions.palantir.get()).formatJavadoc(false)
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        removeUnusedImports()
        importOrder("", "java", "javax", "\\#")
        target("**/*.java")
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
        target("*.gradle.kts")
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
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 21
    }

    javadoc {
        val options = options as StandardJavadocDocletOptions
        options.encoding = "utf-8"
        options.links("https://docs.oracle.com/en/java/javase/21/docs/api/", "https://docs.jda.wiki/")
    }
}
