[![GitHub Build](https://github.com/libktx/ktx/workflows/build/badge.svg)](https://github.com/libktx/ktx/actions?query=workflow%3Abuild)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.10-orange.svg)](http://kotlinlang.org/)
[![libGDX](https://img.shields.io/badge/libgdx-1.13.1-red.svg)](https://libgdx.com/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-async.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)

[![KTX](.github/ktx-logo.png "KTX")](http://libktx.github.io)

_**K**o**t**lin extensions for libGD**X**._

# Table of contents

* [Introduction](#introduction)
* [Modules](#modules)
* [Installation](#installation)
* [Documentation](#documentation)
* [Contribution](#contribution)

## Introduction

**KTX** is a Kotlin game framework extending [libGDX](http://libgdx.badlogicgames.com/). It aims to make libGDX as
[Kotlin](http://kotlinlang.org/)-friendly as possible without completely rewriting the API. It provides modular
utilities and extensions for selected parts of libGDX with poor Kotlin support.

Examples of Kotlin language features used to improve usability, performance, and readability of libGDX include:

- *Operator overloads* for collections and mathematical operations.
- *Extension methods* expanding and improving the original libGDX APIs without the use of inheritance.
- *Inline methods* with reduced runtime overhead for various listeners, builders, and loggers.
- *Nullable types* which improve typing information of selected interfaces and functions.
- *Default parameters* reducing boilerplate code and providing sensible defaults for various operations.
- *Type-safe builders* for GUI, interface styling, ECS, and physics engine setup.
- *Default interface methods* simplifying their implementation.
- *Coroutines context* providing concurrency utilities and non-blocking asset loading.
- *Reified types* simplifying usage of methods normally consuming `Class` parameters.

See the [_Choosing **KTX**_](https://github.com/libktx/ktx/wiki/Choosing-KTX) article for pros and cons of this framework.

## Modules

**KTX** was designed to be modular from day one. In fact, some of its libraries consist of just a single Kotlin file.
You can include the selected **KTX** modules based on the needs of your application.

|                 Module                 | Description                                                                                                                       |
|:--------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------|
|         [`ktx-actors`](actors)         | [`Scene2D`](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d) GUI extensions for stages, actors, actions, and event listeners. |
|             [`ktx-ai`](ai)             | Type-safe Kotlin builders and utilities for [`gdxAI`](https://github.com/libgdx/gdx-ai).                                          |
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

#### New projects

New projects with support for KTX can be generated with the [`gdx-liftoff` tool](https://github.com/tommyettinger/gdx-liftoff).
In contrary to the official `gdx-setup` tool, `gdx-liftoff` provides greater support for libGDX extensions and
a wider set of platforms, as well as custom project templates. You can download the latest release of the tool
[here](https://github.com/tommyettinger/gdx-liftoff/releases).

Click on the sections below for instructions on how to set up a new KTX project with `gdx-liftoff`.

<details><summary><b>General</b></summary><dl><dd>

---

Fill the basic information about your project such as its name, root package or main class name.
Provide an empty folder to generate the project into. If you want to target the Android platform,
define the path to the Android SDK.

The following sections describe each tab of the setup tool available below the basic project info.

---

</dd></dl></details>

<details><summary><b>Platforms</b></summary><dl><dd>

---

**KTX** supports the following platforms:

* **Core:** mandatory shared module.
* **Desktop:** the default desktop platform based on LWJGL3.
* **Android:** native Android mobile platform.
* **iOS:** mobile platform using RoboVM to support iOS.
* **HTML (TeaVM):** unofficial experimental web platform using TeaVM.
* Headless: a desktop platform without a graphical interface.
* Server: a separate server application without libGDX APIs.
* Shared: a module for sharing code between the Server and Core.

The following platforms are unsupported or untested:
* HTML: the default web platform. Supports only Java projects.
* Desktop (Legacy): legacy desktop platform built upon LWJGL2. Might not work with modern JVMs.
* iOS Multi-OS Engine: an alternative iOS platform. Untested.

---

</dd></dl></details>

<details><summary><b>Languages</b></summary><dl><dd>

---

You can select the **Kotlin** language support to ensure it is correctly set up in the generated project.
If a Kotlin project template is selected, it will automatically add the necessary Kotlin libraries and plugins.

---

</dd></dl></details>

<details><summary><b>Extensions</b></summary><dl><dd>

---

This section includes the official libGDX extensions. Each of these should be compatible with Kotlin
projects. However, some extensions might be unavailable on specific platforms. In particular, the TeaVM
backend might be unable to compile libraries relying on native code or reflection.

---

</dd></dl></details>

<details><summary><b>Third-party extensions</b></summary><dl><dd>

---

This section contains all verified third-party extensions for libGDX. All **KTX** modules are listed
in this tab.

To include a **KTX** module, scroll down to the **KTX** libraries list and click on the corresponding
checkbox. This will ensure that the module is properly installed and includes all of its dependencies
in the latest available versions.

The `gdx-liftoff` tool will also set up a Gradle property named `ktxVersion` that will be shared across
all **KTX** libraries. To upgrade your project after a **KTX** release, update to the latest version
in the `gradle.properties` file.

---

</dd></dl></details>

<details><summary><b>Templates</b></summary><dl><dd>

---

Choosing a template for the project determines the initial implementation of the libGDX `ApplicationListener`,
as well as the application launchers on each platform. Some templates also showcase specific parts of the framework,
such as the Scene2D GUI or event handling. You can generate several projects and check out various templates,
but for working with Kotlin and KTX these are the best starting points:
  
* **Kotlin**: a basic project template that generates Kotlin application launchers.
* **Kotlin Logo**: a simple project that generates Kotlin application launchers and draws the libGDX logo on the screen.
* **Kotlin + KTX** *(recommended)*: a project template that generates the `ApplicationListener` using **KTX**
  utilities. When launched, the application draws the **KTX** logo on the screen. Some modules that require additional
  setup, such as `ktx-async`, are properly initiated by the template if selected.

---

</dd></dl></details>

<details><summary><b>Advanced</b></summary><dl><dd>

---

This section can be used to specify versions of core dependencies. If you are just starting with libGDX,
these settings can be mostly left untouched. However, if you wish to have a basic Scene2D GUI Skin that you
can use to test the available widgets, mark the *Add GUI assets* checkbox.

---

</dd></dl></details>

Example **KTX** projects:

* [`ktx-sample-project`](https://github.com/libktx/ktx-sample-project): includes all **KTX** modules and the official
  libGDX extensions. Targets the desktop and mobile platforms.
* [`ktx-sample-web-project`](https://github.com/libktx/ktx-sample-web-project): includes most **KTX** modules that are
  at least partially supported by the web platform, as well as the official libGDX extensions. Targets the desktop,
  mobile and web platforms.

When using the official `gdx-setup` tool instead of the recommended `gdx-liftoff`, generate a project with Kotlin
support and refer to the next section.

#### Existing projects

**KTX** libraries can be added to existing Kotlin libGDX projects. Please refer to the
[libGDX wiki](https://libgdx.com/wiki/jvm-langs/using-libgdx-with-kotlin) for more information on how to add Kotlin
support to a libGDX application.

All **KTX** modules are uploaded to _Maven Central_ and are fully compatible with the Gradle build tool, which is used
in libGDX projects by default.

The libraries are published under the `io.github.libktx` group and are named with the `ktx-` prefix. You can find
a complete list of KTX modules in the [previous section](#modules). As an example, including the [app](app) module
with the `ktx-app` identifier would require the following changes in your `build.gradle` or `build.gradle.kts` file:

<details><summary><code>build.gradle</code> <sub><b>Gradle Groovy DSL</b></sub></summary>

```groovy
// Groovy DSL:
ext {
  // Update this version to match the latest KTX release:
  ktxVersion = '1.13.1-rc1'
}

dependencies {
  api group: 'io.github.libktx', name: 'ktx-app', version: ktxVersion
}
```

</details>

<details><summary><code>build.gradle.kts</code> <sub><b>Gradle Kotlin DSL</b></sub></summary>

```kotlin
// Update this version to match the latest KTX release:
val ktxVersion = "1.13.1-rc1"

dependencies {
  api(group = "io.github.libktx", name = "ktx-app", version = ktxVersion)
}
```

</details>

**KTX** modules should generally be added to the dependencies of the shared `core` module of your libGDX application.

You can find the latest **KTX** version on Maven Central:

[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-app.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)

#### Platforms

**KTX** currently supports the following platforms:

| Platform |    Status    | Description                                                                                                                    |
|:--------:|:------------:|--------------------------------------------------------------------------------------------------------------------------------|
| Desktop  |   Complete   | All major desktop platforms are supported by the official libGDX LWJGL3 backend.                                               |
| Android  |   Complete   | Supported natively by the official libGDX Android backend.                                                                     |
|   iOS    |   Complete   | Supported by the official libGDX iOS backend using [RoboVM](http://robovm.mobidevelop.com/).                                   |
|   Web    | Experimental | Partially supported by the unofficial [web backend](https://github.com/xpenatan/gdx-teavm/) using [TeaVM](https://teavm.org/). |

> Note that platforms other than desktop might provide limited support for features such as reflection, coroutines
> or Java standard library emulation. In particular, mobile platforms might not support the entire Java standard
> library including the newer additions, while the web platform currently does not support Kotlin coroutines or more
> advanced reflection features. Please refer to the documentation of the respective libGDX backends, as well as
> the tools that they are based on.

#### Versioning

Each **KTX** version is based on the matching libGDX release. **KTX** uses suffixes to differentiate multiple releases
made against a single libGDX version. The `-rc` suffix is reserved for stable releases.

Unfortunately, libGDX does not follow the [semantic versioning](https://semver.org/) guidelines. Both minor and patch
versions can introduce breaking changes. Please read the [libGDX](https://github.com/libgdx/libgdx/blob/master/CHANGES)
and [**KTX** change logs](CHANGELOG.md) before updating. When choosing the appropriate **KTX** version, always pick
the latest release matching your current libGDX version.

You can browse through our official releases [on Maven](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)
and [on GitHub](https://github.com/libktx/ktx/releases).

Although **KTX** technically uses beta release tags, the official releases are considered suitable for production use.
All modules are thoroughly tested with comprehensive test suites.

#### Latest changes

The [`master`](https://github.com/libktx/ktx/tree/master/) branch is the default branch of the repository. It represents
the latest stable release of **KTX**. It ensures that the documentation in the repository is in sync with the latest
released version.

The newest changes can be found on the [`develop`](https://github.com/libktx/ktx/tree/develop/) branch instead.

The preview snapshot releases with the latest changes are uploaded automatically to the
`https://oss.sonatype.org/content/repositories/snapshots/` repository. To use them in your application, add
the following Maven repository, and change the suffix of the **KTX** version to `-SNAPSHOT`:

<details><summary><code>build.gradle</code> <sub><b>Gradle Groovy DSL</b></sub></summary>
  
```groovy
repositories {
  // Include your other repositories here.
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}

ext {
  // Update this version to match the latest libGDX release:
  ktxVersion = '1.13.1-SNAPSHOT'
}
```

</details>

<details><summary><code>build.gradle.kts</code> <sub><b>Gradle Kotlin DSL</b></sub></summary>

```kotlin
repositories {
  // Include your other repositories here.
  maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

// Update this version to match the latest libGDX release:
val ktxVersion = "1.13.1-SNAPSHOT"
```

</details>

The full version of the latest snapshot release can be found on the
[`develop`](https://github.com/libktx/ktx/blob/develop/version.txt) branch, and usually matches the latest
stable libGDX release. Snapshot releases for the nightly libGDX builds are not available.

Note that even the snapshots are rather stable, as the libraries are not pushed to _Maven Central_ unless they pass
their extensive test suites. However, the public APIs in snapshot libraries might be changed prior to a stable release.

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
or [the contributors list](.github/CONTRIBUTORS.md).

### Licensing

This project is dedicated to [public domain](LICENSE.txt).

### Working from sources

See [this section](.github/CONTRIBUTING.md#working-from-sources) of the contribution guideline to get started.
