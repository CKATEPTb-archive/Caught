Caught [![License](https://img.shields.io/github/license/CKATEPTb/Caught)](https://github.com/CKATEPTb/Caught/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/925686623222505482.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/P7FaqjcATp)
[![Release](https://jitpack.io/v/ru.ckateptb/Caught.svg)](https://jitpack.io/#ru.ckateptb/Caught)
===========

Implementation of non-traditional minecraft colliders

Supported Colliders
------
- [x] Axis Aligned
- [x] Oriented
- [x] Sphere
- [x] Ray
- [x] Disk
- [x] Composite

Features
------
- [x] Easy to Use
- [x] Rotation utils
- [x] Immutable Vectors

How To
------
* Maven Repo:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
* Artifact Information:
```xml
<dependency>
    <groupId>ru.ckateptb</groupId>
    <artifactId>Caught</artifactId>
    <version>{{version}}</version>
</dependency>
 ```

**Or alternatively**

***with Gradle***:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'ru.ckateptb:Caught:{{version}}'
}
```
***with Kotlin:***
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("ru.ckateptb:Caught:{{version}}")
}
```

Usage
------

Create the collider you are interested in by filling in the understandable constructor arguments and use inspects to check for collision with another or use convenient handler methods