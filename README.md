# cjda-util

A toolbox for Discord bots built on JDA: slash, message and user commands with automatic deployment, localization,
menus, modals, pagination, conversations, premium checks and bot list integration.

[![Release](https://img.shields.io/nexus/maven-releases/de.chojo/cjda-util?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][release]
[![Snapshot](https://img.shields.io/nexus/s/de.chojo/cjda-util?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][snapshot]

## Requirements

Java 21 or newer. Built against JDA 6.7.0, which cjda-util brings with it.

## Dependency

Gradle:

```kotlin
repositories {
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    implementation("de.chojo:cjda-util:3.0.0")
}
```

Maven:

```xml
<repositories>
    <repository>
        <id>eldonexus</id>
        <url>https://eldonexus.de/repository/maven-public/</url>
    </repository>
</repositories>

<dependency>
    <groupId>de.chojo</groupId>
    <artifactId>cjda-util</artifactId>
    <version>3.0.0</version>
</dependency>
```

Documentation: https://docs.chojo.dev/cjda-util

[release]: https://eldonexus.de/#browse/browse:maven-releases:de%2Fchojo%2Fcjda-util
[snapshot]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Fchojo%2Fcjda-util
