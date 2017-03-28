[![Travis CI](https://travis-ci.org/czyzby/ktx.svg?branch=master)](https://travis-ci.org/czyzby/ktx)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.czyzby/ktx-style.svg)](https://search.maven.org/#search%7Cga%7C1%7Cktx)
[![Kotlin](https://img.shields.io/badge/kotlin-1.1.1-orange.svg)](http://kotlinlang.org/)

# KTX

**K**o**t**lin utilities for LibGD**X** applications.

### About the project

**KTX** aims to make [LibGDX](http://libgdx.badlogicgames.com/) [Kotlin](http://kotlinlang.org/)-friendly, without turning
the API upside down. This is **not** a new framework by any means. Most guides and examples assume that the user of these
libraries is at least a bit familiar with the LibGDX API.

### Modules

Needless to say, you might not want or need all the features provided by **KTX**. That is why it was designed to be highly
modular from day one: some of its libraries are just a single Kotlin file. When possible, the extensions do not include
the standard Kotlin library, and even if they do - it is marked as a `provided` dependency, so you can choose the Kotlin
version that suits you best (since Kotlin is already pretty stable, this should not cause any issues as long as you use
a recent release).

The modules present in **KTX** are:

- [actors](actors): general `Scene2D` utilities for stages, actors, actions and event listeners.
- [app](app): `ApplicationListener` abstract implementations and other general LibGDX utilities.
- [assets](assets): assets and heavy resources management utilities.
- [collections](collections): utilities for LibGDX custom collections. Based on Kotlin standard library utilities.
- [i18n](i18n): some simple functions that make internationalization less verbose and easier to use.
- [inject](inject): unsettlingly simple dependency injection with nearly zero runtime overhead.
- [log](log): minimal runtime overhead cross-platform logging.
- [math](math): operator overloads for LibGDX math API and general math utilities.
- [scene2d](scene2d): type-safe Kotlin builders for Scene2D GUI.
- [style](style): enhances `Skin` API with type-safe builders of official Scene2D widget styles.
- [vis](vis): type-safe Kotlin builders for VisUI. Alternative to the [scene2d](scene2d) extension.
- [vis-style](vis-style): enhances `Skin` API with type-safe builders of VisUI widget styles. Extension of [style](style) module.

Afraid to use some third-party code? Run the test suites yourself. *Every* function and class added by these extensions
features in at least one unit test. *Reported code coverage is not impressive though, as the tools are unable to calculate
coverage of inlined methods. (And there are plenty of these in some modules.)*

### Dependencies

**KTX** libraries are currently in beta. While tested and stable enough, we want to give them a little bit more time
and get some user feedback before a stable release. All versions are available through Maven Central and
[here](https://github.com/czyzby/ktx/releases). You can also use the preview snapshot releases from
`https://oss.sonatype.org/content/repositories/snapshots/` repository.

All libraries follow the same naming schema - this is an example Gradle dependency:

```Groovy
compile "com.github.czyzby:ktx-$module:$ktxVersion"
```

Replace `$module` with the name of required **KTX** library. `$ktxVersion` usually matches LibGDX version it was
compiled against - although it might end with `-b1` (if it is a beta release) or `-SNAPSHOT` (if you are using
the snapshots). For example, the first release was `1.9.4-b1`. You can browse through our releases
[here](https://search.maven.org/#search%7Cga%7C1%7Cktx).

Note that even snapshots should be more or less stable, as libraries are not pushed to Maven Central if they do not pass
the unit tests.

### Gradle

The project itself is managed by [Gradle](http://gradle.org/). Gradle wrapper is not included, so you might want to
install it locally. Scripts should be compatible with Gradle `2.1+` and `3.+`. If you consider working from sources,
these are some useful Gradle tasks that you can look into:

- `gradle build install` - builds the libraries' archives and pushes them to Maven Local.
- `gradle test` - runs unit tests in all projects.
- `gradle clean` - removes build directories.
- `gradle distZip` - prepares a zip archive with all jars in `build/distributions` folder. Useful for releases.
- `gradle uploadArchives` - pushes the archives to Maven Central. Requires proper `gradle.properties` with signing and
logging data.
- `gradle closeAndPromoteRepository` - closes and promotes Nexus repository. Should be run after `uploadArchives` in
case of a non-snapshot upload to Maven Central.

### [Contribution](CONTRIBUTING.md)

Want to help? Browse through the issues to see what's currently missing or broken. Before creating any pull requests,
be aware that the code is dedicated to [public domain](LICENSE.md).
