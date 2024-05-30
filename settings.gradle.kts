rootProject.name = "cjda-util"

pluginManagement{
    repositories{
        gradlePluginPortal()
        maven{
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")

        }
    }
}

dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            version("javalin", "6.1.1")
            library("javalin-core", "io.javalin", "javalin").versionRef("javalin")
            library("javalin-plugin-openapi", "io.javalin.community.openapi", "javalin-openapi-plugin").versionRef("javalin")
            library("javalin-plugin-swagger", "io.javalin.community.openapi", "javalin-swagger-plugin").versionRef("javalin")
            library("javalin-annotations", ".javalin.community.openapi","openapi-annotation-processor").versionRef("javalin")
            bundle("javalin", listOf("javalin-core", "javalin-plugin-openapi", "javalin-plugin-swagger"))
        }
    }
}
