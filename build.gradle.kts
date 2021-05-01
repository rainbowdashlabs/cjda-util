plugins {
    java
    `maven-publish`
    `java-library`
}

repositories {
    mavenLocal {
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("net.dv8tion:JDA:4.2.0_229")
    api("org.apache.commons", "commons-text", "1.9")
    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

group = "de.chojo"
version = "1.0.1"
description = "Discord utilities for use with JDA"
java.sourceCompatibility = JavaVersion.VERSION_11

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        groupId = project.group as String?
        artifactId = project.name
        version = project.version as String?
    }

    repositories {
        maven {
            val isSnapshot = version.toString().endsWith("SNAPSHOT");
            val release = "https://eldonexus.de/repository/maven-releases/";
            val snapshot = "https://eldonexus.de/repository/maven-snapshots/";

            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            name = "EldoNexus"
            url = uri(if (isSnapshot) snapshot else release)
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}



tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}