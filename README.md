
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/rainbowdashlabs/cjda-util/Publish%20to%20Nexus?style=for-the-badge&label=Publishing)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/rainbowdashlabs/cjda-util/Verify%20state?style=for-the-badge&label=Building)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/maven-releases/de.chojo/cjda-util?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Development)](https://img.shields.io/nexus/maven-dev/de.chojo/cjda-util?label=DEV&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/de.chojo/cjda-util?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)

Gradle:
``` kotlin
repositories {
    maven {
        url = uri("https://eldonexus.de/repository/maven-public")
    }
}

dependencies {
    implementation("de.chojo", "cjda-util", "{version}")
}
```

Maven:
``` xml
    <repositories>
        <repository>
            <id>EldoNexus</id>
            <url>https://eldonexus.de/repository/maven-public</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>de.chojo</groupId>
            <artifactId>cjda-util</artifactId>
            <version>{version}</version>
        </dependency>
    </dependencies>
```

Snapshots are available via: **https://eldonexus.de/repository/maven-snapshots/**


### Properties
`-Dcjda.interactions.cleanguildcommands=true` _default false_

Clean guild commands on bot start

`-Dcjda.interactions.testmode=true` _default false_

Set the testmode. When active all global commands will be deployed on guilds and not globally.

`-Dcjda.localisation.error.name=false` _default true_

Allows to disable the name error on command localisation.


[nexus_releases]: https://eldonexus.de/#browse/browse:maven-releases:de%2Fchojo%2Fcjda-util
[nexus_snapshots]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Fchojo%2Fcjda-util
