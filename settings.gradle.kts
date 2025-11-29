rootProject.name = "cjda-util"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")

        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("javalin", "6.7.0-5")
            library("javalin-core", "io.javalin", "javalin").versionRef("javalin")
            library(
                "javalin-openapiannotation",
                "io.javalin.community.openapi",
                "openapi-annotation-processor"
            ).versionRef("javalin")
            library("javalin-openapi", "io.javalin.community.openapi", "javalin-openapi-plugin").versionRef("javalin")
            bundle("javalin", listOf("javalin-core", "javalin-openapi"))

            version("jda", "6.0.0")
            library("jda", "net.dv8tion", "JDA").versionRef("jda")

            version("junit", "6.0.0")
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-platform", "org.junit.platform", "junit-platform-launcher").versionRef("junit")
            bundle("junit", listOf("junit-jupiter", "junit-platform"))

            plugin("spotless", "com.diffplug.spotless").version("8.0.0")

        }
    }
}
