![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/de.chojo/cjda-util?nexusVersion=3&server=https%3A%2F%2Feldonexus.de&style=flat-square)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/de.chojo/cjda-util?server=https%3A%2F%2Feldonexus.de&style=flat-square&color=orange)

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