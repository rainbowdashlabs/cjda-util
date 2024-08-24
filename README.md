
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/rainbowdashlabs/cjda-util/publish_to_nexus.yml?style=for-the-badge&label=Publishing&branch=master)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/rainbowdashlabs/cjda-util/verify.yml?style=for-the-badge&label=Building&branch=master)
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


### Configuration

ENV always takes precedence over PROP

Prop: `-Dcjda.interactions.cleanguildcommands=true`  
Env: `CJDA_INTERACTIONS_CLEANGUILDCOMMANDS`  
_default false_  

Clean guild commands on bot start

Prop: `-Dcjda.interactions.testmode=true`   
Env: `CJDA_INTERACTIONS_TESTMODE`  
_default false_

Set the testmode. When active all global commands will be deployed on guilds and not globally.

Prop: `-Dcjda.localisation.error.name=false`  
Env: `CJDA_LOCALISATION_NAME_ERROR`  
_default true_

Allows to disable the name error on command localisation.


[nexus_releases]: https://eldonexus.de/#browse/browse:maven-releases:de%2Fchojo%2Fcjda-util
[nexus_snapshots]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Fchojo%2Fcjda-util
