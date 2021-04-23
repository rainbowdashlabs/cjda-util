
plugins {
    java
    `maven-publish`
    `java-library`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("net.dv8tion:JDA:4.2.0_229")
    api("net.sf.jopt-simple:jopt-simple:6.0-alpha-3")
    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

group = "de.chojo"
version = "1.0"
description = "CJDAUtil"
java.sourceCompatibility = JavaVersion.VERSION_11

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.test{
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}