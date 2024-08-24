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
            version("javalin", "6.3.0")
            library("javalin-core", "io.javalin", "javalin").versionRef("javalin")
            library(
                "javalin-openapiannotation",
                "io.javalin.community.openapi",
                "openapi-annotation-processor"
            ).versionRef("javalin")
            library("javalin-openapi", "io.javalin.community.openapi", "javalin-openapi-plugin").versionRef("javalin")
            bundle("javalin", listOf("javalin-core", "javalin-openapi"))

            version("jda", "5.1.0")
            library("jda", "net.dv8tion", "JDA").versionRef("jda")

            plugin("spotless", "com.diffplug.spotless").version("6.25.0")

        }
    }
}
