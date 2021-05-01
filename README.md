[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/de.chojo/cjda-util?nexusVersion=3&server=https%3A%2F%2Feldonexus.de&style=flat-square)][nexus_releases]
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/de.chojo/cjda-util?server=https%3A%2F%2Feldonexus.de&style=flat-square&color=orange)][nexus_snapshots]

Gradle:
``` kotlin
repositories {
    maven {
        url = uri("https://eldonexus.de/repository/maven-releases")
    }
}

dependencies {
    implementation("de.chojo", "cjda-util", "1.0.0")
}
```

Maven:
``` xml
    <repositories>
        <repository>
            <id>EldoNexus</id>
            <url>https://eldonexus.de/repository/maven-releases</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>de.chojo</groupId>
            <artifactId>cjda-util</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
```

Snapshots are available via: **https://eldonexus.de/repository/maven-snapshots/**

[nexus_releases]: https://eldonexus.de/#browse/browse:maven-releases:de%2Fchojo%2Fcjda-util
[nexus_snapshots]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Fchojo%2Fcjda-util
