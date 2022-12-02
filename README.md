[![GitHub Build](https://github.com/libktx/ktx/workflows/build/badge.svg)](https://github.com/libktx/ktx/actions?query=workflow%3Abuild)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.22-orange.svg)](http://kotlinlang.org/)
[![libGDX](https://img.shields.io/badge/libgdx-1.11.0-red.svg)](https://libgdx.com/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-async.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)

[![KTX](.github/ktx-logo.png "KTX")](http://libktx.github.io)

_**K**o**t**lin extensions for libGD**X**._

## Introduction

**KTX** is a Kotlin game framework built on [libGDX](http://libgdx.badlogicgames.com/). It aims to make libGDX as
[Kotlin](http://kotlinlang.org/)-friendly as possible without completely rewriting the API. It provides modular
utilities and extensions for selected parts of libGDX with poor Kotlin support.

Examples of Kotlin language features used to improve usability, performance, and readability of libGDX include:

- *Operator overloads* for collections and mathematical operations.
- *Extension methods* improving original libGDX APIs without the use of inheritance.
- *Inline methods* with reduced runtime overhead for various listeners, builders, and loggers.
- *Nullable types* which improve typing information of selected interfaces and functions.
- *Default parameters* reducing boilerplate code.
- *Type-safe builders* for GUI, styling, and physics engine.
- *Default interface methods* for common interfaces, simplifying their implementations.
- *Coroutines context* providing concurrency utilities and non-blocking asset loading.
- *Reified types* that simplify usage of methods normally consuming `Class` parameters.

See the [_Choosing **KTX**_](https://github.com/libktx/ktx/wiki/Choosing-KTX) article for pros and cons of this framework.

## Modules

**KTX** was designed to be modular from day one - in fact, many of its libraries are just a single Kotlin file.
You can include selected **KTX** modules based on the needs of your application.

|                 Module                 | Description                                                                                                                       |
|:--------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------|
|         [`ktx-actors`](actors)         | [`Scene2D`](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d) GUI extensions for stages, actors, actions, and event listeners. |
|            [`ktx-app`](app)            | `ApplicationListener` implementations and general application utilities.                                                          |
|        [`ktx-artemis`](artemis)        | [`Artemis-odb`](https://github.com/junkdog/artemis-odb) entity-component-system utilities.                                        |
|         [`ktx-ashley`](ashley)         | [`Ashley`](https://github.com/libgdx/ashley) entity-component-system utilities.                                                   |
|         [`ktx-assets`](assets)         | Resources management utilities.                                                                                                   |
|   [`ktx-assets-async`](assets-async)   | Non-blocking asset loading using coroutines.                                                                                      |
|          [`ktx-async`](async)          | [Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) context based on libGDX threading model.                      |
|          [`ktx-box2d`](box2d)          | [`Box2D`](https://libgdx.com/wiki/extensions/physics/box2d) physics engine utilities.                                             |
|    [`ktx-collections`](collections)    | Extensions for libGDX custom collections.                                                                                         |
|       [`ktx-freetype`](freetype)       | `FreeType` fonts loading utilities.                                                                                               |
| [`ktx-freetype-async`](freetype-async) | Non-blocking `FreeType` fonts loading using coroutines.                                                                           |
|       [`ktx-graphics`](graphics)       | Utilities related to rendering tools and graphics.                                                                                |
|           [`ktx-i18n`](i18n)           | Internationalization API utilities.                                                                                               |
|         [`ktx-inject`](inject)         | A dependency injection system with low overhead and no reflection usage.                                                          |
|           [`ktx-json`](json)           | Utilities for libGDX [JSON](https://libgdx.com/wiki/utils/reading-and-writing-json) serialization API.                            |
|            [`ktx-log`](log)            | Minimal runtime overhead cross-platform logging using inlined functions.                                                          |
|           [`ktx-math`](math)           | Operator functions for libGDX math API and general math utilities.                                                                |
|    [`ktx-preferences`](preferences)    | Improved API for accessing and saving [preferences](https://libgdx.com/wiki/preferences).                                         |
|        [`ktx-reflect`](reflect)        | Utilities for libGDX [reflection API](https://libgdx.com/wiki/utils/reflection).                                                  |
|        [`ktx-scene2d`](scene2d)        | Type-safe Kotlin builders for [`Scene2D`](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d) GUI.                               |
|         [`ktx-script`](script)         | Kotlin scripting engine for desktop applications.                                                                                 |
|          [`ktx-style`](style)          | Type-safe Kotlin builders for `Scene2D` widget styles extending `Skin` API.                                                       |
|          [`ktx-tiled`](tiled)          | Utilities for [Tiled](https://www.mapeditor.org/) maps.                                                                           |
|            [`ktx-vis`](vis)            | Type-safe Kotlin builders for [`VisUI`](https://github.com/kotcrab/vis-ui/).                                                      |
|      [`ktx-vis-style`](vis-style)      | Type-safe Kotlin builders for `VisUI` widget styles.                                                                              |

### Installation

**KTX** modules are uploaded to _Maven Central_ and are fully compatible with the Gradle build tool, which is used
in libGDX projects by default.

All libraries follow the same naming schema:

```groovy
api "io.github.libktx:$module:$ktxVersion"
```

Replace `$module` with the name of the selected **KTX** library (see the table above).

For example, including the [app](app) module with the `ktx-app` identifier would require the following changes
in your `build.gradle` file:

```groovy
// Groovy DSL:
ext {
  // Update this version to match the latest KTX release:
  ktxVersion = '1.11.0-rc3'
}

dependencies {
  api group: 'io.github.libktx', name: 'ktx-app', version: ktxVersion
}
```

```kotlin
// Kotlin DSL:

// Update this version to match the latest KTX release:
val ktxVersion = "1.11.0-rc3"

dependencies {
  api(group = "io.github.libktx", name = "ktx-app", version = ktxVersion)
}
```

You can find the latest **KTX** version on Maven Central:

[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-app.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)

**KTX** modules should generally be added to the dependencies of the shared `core` module of your libGDX application.

> As a side note, defining `ktxVersion` as a property in `ext` is not necessary, as versions can be set directly in the
`dependencies` section. However, extracting the dependencies versions is a good practice, especially if they can be
reused throughout the build files. This will speed up updating of your project if you include multiple **KTX** modules.

#### Versioning

**KTX** versions match the libGDX versions that they were compiled against. `$ktxVersion` will usually match your libGDX
version, but it might end with `-rc` suffix if it is a stable release, or `-SNAPSHOT` if you are using the development
branch. Older **KTX** releases use the `-b` suffix to mark milestone releases.

You can browse through our official releases [on Maven](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)
and [on GitHub](https://github.com/libktx/ktx/releases).

Unfortunately, libGDX does not follow the [semantic versioning](https://semver.org/) guidelines. Both minor and patch
versions can introduce breaking changes. Please read the [libGDX](https://github.com/libgdx/libgdx/blob/master/CHANGES)
and [**KTX** change logs](CHANGELOG.md) before updating.

Although **KTX** still uses beta release tags, the official releases are stable enough for production use.
All modules are thoroughly tested with unit tests.

#### Latest changes

The [`master`](https://github.com/libktx/ktx/tree/master/) branch is the default branch of the repository. It represents
the latest stable release of **KTX**. It ensures that the documentation in the repository is in sync with the latest
version.

The newest changes can be found on the [`develop`](https://github.com/libktx/ktx/tree/develop/) branch instead.

You do not have to compile the sources manually to use the latest features. The preview snapshot releases are uploaded
to the `https://oss.sonatype.org/content/repositories/snapshots/` repository. To use them in your application, add
the following Maven repository, and modify the prefix of `ktxVersion` to `-SNAPSHOT`:

```groovy
// Groovy DSL:
repositories {
  // Include your other repositories here.
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}

ext {
  // Update this version to match the latest libGDX release:
  ktxVersion = '1.11.0-SNAPSHOT'
}
```

```kotlin
// Kotlin DSL:
repositories {
  // Include your other repositories here.
  maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

// Update this version to match the latest libGDX release:
val ktxVersion = "1.11.0-SNAPSHOT"
```

The latest snapshot version name can be found on the [`develop`](https://github.com/libktx/ktx/blob/develop/version.txt)
branch.

Even the snapshots are rather stable, as the libraries are not pushed to _Maven Central_ unless they pass their
extensive test suites. However, the public APIs in snapshot releases might be changed prior to a stable release.

## Documentation

### Official guides

Each module contains a `README.md` file with a list of all its features and a guide with useful code snippets.
Browse through the directories in the root folder to find out more about each library.

### Source documentation

All functionalities are documented with Kotlin _KDocs_. You can access the source documentation by:

- Viewing the generated Dokka files hosted on the [project website](https://libktx.github.io/docs/).
- Extracting the `doc` folders with Dokka files from the [release archives](https://github.com/libktx/ktx/releases).
- Reading the sources directly.

### Links

[**KTX** wiki](https://github.com/libktx/ktx/wiki) lists some useful resources that can help you get started.

Most official guides and code examples in this repository assume that the reader is at least a bit familiar with
the libGDX API. If you are just getting to know the framework, it might be helpful to go through
[the official libGDX wiki](https://libgdx.com/wiki/), and convert some Java examples to Kotlin.

## [Contribution](.github/CONTRIBUTING.md)

Suggestions, questions, typo fixes, documentation improvements and code contributions are always welcome.

Do not hesitate to [start a discussion](https://github.com/libktx/ktx/discussions) with questions about the framework.
Feel free to advertise your **KTX** project, propose new features, discuss game jams, or even create a personal devlog.

If you would like to contribute, please read [the contribution guideline](.github/CONTRIBUTING.md), and browse through
[the active issues](https://github.com/libktx/ktx/issues). The [`develop`](https://github.com/libktx/ktx/tree/develop/)
is the active development branch. When creating pull requests, make sure to choose `develop` as the target branch.

You can check the list of the contributors via [GitHub insights](https://github.com/libktx/ktx/graphs/contributors)
and on [the contributors list](.github/CONTRIBUTORS.md).

### Licensing

This project is dedicated to [public domain](LICENSE.txt).

### Working from sources

See [this section](.github/CONTRIBUTING.md#working-from-sources) of the contribution guideline to get started.
