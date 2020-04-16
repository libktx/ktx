[![GitHub Build](https://github.com/libktx/ktx/workflows/build/badge.svg)](https://github.com/libktx/ktx/actions?query=workflow%3Abuild)
[![Kotlin](https://img.shields.io/badge/kotlin-1.3.72-orange.svg)](http://kotlinlang.org/)
[![LibGDX](https://img.shields.io/badge/libgdx-1.9.10-red.svg)](https://libgdx.badlogicgames.com/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-async.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)

[![KTX](.github/ktx-logo.png "KTX")](http://libktx.github.io)

_**K**o**t**lin extensions for LibGD**X**._

## Introduction

**KTX** aims to make [LibGDX](http://libgdx.badlogicgames.com/) as [Kotlin](http://kotlinlang.org/)-friendly as possible
without completely rewriting the API. It provides modular utilities and extensions for selected parts of LibGDX with
poor native Kotlin support.

Examples of Kotlin language features used to improve usability, performance and readability of LibGDX APIs include:

* *Operator overloads* for collections and mathematical operations.
* *Extension methods* with sensible *default parameters*.
* *Inline methods* with reduced runtime overhead for various listeners, builders and loggers.
* *Nullable types* which improve typing information of selected interfaces and functions.
* *Default interface methods* for common interfaces, simplifying their implementations.
* *Type-safe builders* for GUI, styling and physics engine.
* *Coroutines context* providing concurrency utilities and non-blocking asset loading.
* *Reified types* that simplify usage of methods normally consuming `Class` parameters.

See the [_Choosing KTX_](https://github.com/libktx/ktx/wiki/Choosing-KTX) article for pros and cons of this framework.

## Modules

**KTX** was designed to be modular from day one - in fact, some of these libraries are just a single Kotlin file.
You can include selected **KTX** modules based on the needs of your application.

Module | Dependency name | Description
:---: | :--- | ---
[actors](actors) | `ktx-actors` | [`Scene2D`](https://github.com/libgdx/libgdx/wiki/Scene2d) GUI extensions for stages, actors, actions and event listeners.
[app](app) | `ktx-app` | `ApplicationListener` implementations and general application utilities.
[ashley](ashley) | `ktx-ashley` | [`Ashley`](https://github.com/libgdx/ashley) entity-component-system utilities.
[assets](assets) | `ktx-assets` | Resources management utilities.
[assets-async](assets-async) | `ktx-assets-async` | Non-blocking asset loading using coroutines.
[async](async) | `ktx-async` | [Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) context based on LibGDX threading model.
[box2d](box2d) | `ktx-box2d` | [`Box2D`](https://github.com/libgdx/libgdx/wiki/Box2d) physics engine utilities.
[collections](collections) | `ktx-collections` | Extensions for LibGDX custom collections.
[freetype](freetype) | `ktx-freetype` | `FreeType` fonts loading utilities.
[freetype-async](freetype-async) | `ktx-freetype-async` | Non-blocking `FreeType` fonts loading using coroutines.
[graphics](graphics) | `ktx-graphics` | Utilities related to rendering tools and graphics.
[i18n](i18n) | `ktx-i18n` | Internationalization API utilities.
[inject](inject) | `ktx-inject` | A dependency injection system with low overhead and no reflection usage.
[json](json) | `ktx-json` | Utilities for LibGDX [JSON](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON) serialization API.
[log](log) | `ktx-log` | Minimal runtime overhead cross-platform logging using inlined functions.
[math](math) | `ktx-math` | Operator functions for LibGDX math API and general math utilities.
[preferences](preferences) | `ktx-preferences` | Improved API for accessing and saving [preferences](https://github.com/libgdx/libgdx/wiki/Preferences).
[scene2d](scene2d) | `ktx-scene2d` | Type-safe Kotlin builders for [`Scene2D`](https://github.com/libgdx/libgdx/wiki/Scene2d) GUI.
[style](style) | `ktx-style` | Type-safe Kotlin builders for `Scene2D` widget styles extending `Skin` API.
[tiled](tiled) | `ktx-tiled` | Utilities for [Tiled](https://www.mapeditor.org/) maps.
[vis](vis) | `ktx-vis` | Type-safe Kotlin builders for [`VisUI`](https://github.com/kotcrab/vis-ui/).
[vis-style](vis-style) | `ktx-vis-style` | Type-safe Kotlin builders for `VisUI` widget styles.

### Installation

**KTX** modules are uploaded to _Maven Central_ and are fully compatible with the Gradle build tool, which is used
in LibGDX projects by default.

All libraries follow the same naming schema:

```Groovy
compile "io.github.libktx:$module:$ktxVersion"
```

Replace `$module` with the name of the selected **KTX** library (see table above).

For example, including the [app](app) module with the `ktx-app` identifier would require the following changes
in your `build.gradle` file:

```Groovy
ext {
  // Update this version to match the latest KTX release:
  ktxVersion = '1.9.10-b5'
}

dependencies {
  compile "io.github.libktx:ktx-app:$ktxVersion"
}
```

You can find the latest KTX version on Maven Central:

* [![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-app.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)

As a side note, defining `ktxVersion` as a property in `ext` is not necessary, as versions can be set directly in the
`dependencies` section. However, extracting the dependencies versions is a good practice, especially if they can be
reused throughout the build files. This will speed up updating of your project if you include multiple KTX modules.

**KTX** modules should generally be added to the dependencies of the shared `core` module of your LibGDX application.

#### Versioning

**KTX** versions match the LibGDX versions that they were compiled against. `$ktxVersion` will usually match your LibGDX
version, but it might end with `-b` postfix if it is a beta release or `-SNAPSHOT` if you are using the development branch.

For example, the first official beta release with the current group ID `io.github.libktx` was compiled against
LibGDX `1.9.6` and since it was the second beta release, its version was `1.9.6-b2`. The corresponding snapshot release
of this version was `1.9.6-SNAPSHOT`.

You can browse through our official releases [on Maven](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)
and [on GitHub](https://github.com/libktx/ktx/releases).

Unfortunately, LibGDX does not follow the [semantic versioning](https://semver.org/) guidelines. Both minor and patch
versions can introduce breaking changes. Please read the LibGDX and [**KTX** change logs](CHANGELOG.md) before updating.

Although **KTX** is still in late beta, the official beta releases are stable enough for production use.
All modules are thoroughly tested with unit tests.

#### Latest changes

The [`master`](https://github.com/libktx/ktx/tree/master/) branch is the default branch of the repository. However,
it represents the last stable release of **KTX**. The latest changes can be found on the
[`develop`](https://github.com/libktx/ktx/tree/develop/) branch.

You do not have to compile the sources manually to use the latest features. The preview snapshot releases are uploaded
to the `https://oss.sonatype.org/content/repositories/snapshots/` repository. To use them in your application, add
the following Maven repository and modify the prefix of `ktxVersion` to `-SNAPSHOT`:

```Groovy
repositories {
  // Include your default repositories here.
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}

ext {
  // Update this version to match the latest LibGDX release:
  ktxVersion = '1.9.6-SNAPSHOT'
}

```

The latest snapshot version name can be found on the [`develop`](https://github.com/libktx/ktx/blob/develop/version.txt)
branch.

Even the snapshots should be more or less stable, as the libraries are not pushed to _Maven Central_ unless they pass
their extensive test suites.

## Documentation

### Official guides

Each module contains a `README.md` file with a list of all features or a guide with some code snippets. Browse through
the directories in the root folder to find out more about each library.

### Source documentation

All public classes and functions are also documented with standard Kotlin _KDocs_. GitHub releases contain archives
with generated Dokka documentation for each module, although you can go through the documentation by viewing the sources
directly.

### Links

[**KTX** wiki](https://github.com/libktx/ktx/wiki) lists some useful resources that can help you get started.

Note that most official guides and examples in this repository assume that the reader is at least a bit familiar with
the LibGDX API. If you are just getting to know the framework, it might be helpful to go through
[the official LibGDX wiki](https://github.com/libgdx/libgdx/wiki).

### `android-ktx`

Note that [`android-ktx`](https://github.com/android/android-ktx) is a separate project with official Android utilities.
The "**KTX**" name was chosen long before the Android project was announced.

## [Contribution](.github/CONTRIBUTING.md)

Suggestions, questions, typo fixes, documentation improvements and code contributions are always welcome.
If you would like to contribute, please read [the contribution guideline](.github/CONTRIBUTING.md) and browse through
[the active issues](https://github.com/libktx/ktx/issues). Don't hesitate to create issues just to ask a question or
make a request for any kind of improvement.

The [`develop`](https://github.com/libktx/ktx/tree/develop/) is the active development branch. When creating pull
requests, make sure to choose `develop` as the target branch.

You can check the list of the contributors via [GitHub insights](https://github.com/libktx/ktx/graphs/contributors)
and on [the contributors list](.github/CONTRIBUTORS.md).

### Licensing

Before creating any pull requests, be aware that the code is dedicated to [public domain](LICENSE.txt).

### Working from sources

See [this section](.github/CONTRIBUTING.md#working-from-sources) of the contribution guideline to get started.
