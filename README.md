[![Travis CI](https://travis-ci.org/libktx/ktx.svg?branch=master)](https://travis-ci.org/libktx/ktx)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-async.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22)
[![Kotlin](https://img.shields.io/badge/kotlin-1.1.2--3-orange.svg)](http://kotlinlang.org/)
[![LibGDX](https://img.shields.io/badge/libgdx-1.9.6-red.svg)](https://libgdx.badlogicgames.com/)

[![KTX](.github/ktx-logo.png "KTX")](http://libktx.github.io)

_**K**o**t**lin utilities for LibGD**X** applications._

### About the project

**KTX** aims to make [LibGDX](http://libgdx.badlogicgames.com/) as [Kotlin](http://kotlinlang.org/)-friendly as possible
without turning the API upside down. This is **not** a new framework by any means - but Kotlin sure makes LibGDX feel like one.

### Modules

**KTX** was designed to be modular from day one: in fact, some of these libraries are just a single Kotlin file. After all,
you might not want or need all modules in your application. When possible, the extensions do not depend on the standard
Kotlin library - and even if they do, it is marked as a `provided` dependency. You can choose the Kotlin version that suits
you best and it will _not_ be overridden by your build system.

Current **KTX** modules:

- [actors](actors): general `Scene2D` utilities for stages, actors, actions and event listeners.
- [app](app): `ApplicationListener` abstract implementations and other general LibGDX application utilities.
- [assets](assets): resources management utilities.
- [async](async): [coroutines](https://kotlinlang.org/docs/reference/coroutines.html) context based on LibGDX threading model.
- [box2d](box2d): `Box2D` physics engine utilities.
- [collections](collections): extensions for LibGDX custom collections. Based on Kotlin standard library utilities.
- [i18n](i18n): some simple extensions that make LibGDX internationalization API less verbose, safer and easier to use.
- [inject](inject): unsettlingly simple dependency injection with nearly zero runtime overhead and no reflection trickery.
- [log](log): minimal runtime overhead cross-platform logging using inlined blocks.
- [math](math): operator overloads for LibGDX math API and general math utilities.
- [scene2d](scene2d): type-safe Kotlin builders for `Scene2D` GUI.
- [style](style): enhances `Skin` API with type-safe builders of official Scene2D widget styles. Replace your JSON skin file today!
- [vis](vis): type-safe Kotlin builders for `VisUI`. An _alternative_ to the [scene2d](scene2d) module.
- [vis-style](vis-style): enhances `Skin` API with type-safe builders of `VisUI` widget styles. An _extension_ of [style](style) module.

Note that most guides and examples assume that the reader is at least a bit familiar with the LibGDX API.

Afraid to use some third-party code? Browse through the sources, run the test suites. While certainly not perfect, we try to keep to
keep the public API clean and *every* features fully unit tested. *Reported code coverage may not be so impressive though, as the
coverage tools are currently unable to calculate coverage of inlined methods. And there are plenty of these in some modules.*

### Dependencies

**KTX** libraries are currently almost out of beta. While tested and stable enough, we want to give them a little bit more time
and get some user feedback before a stable release. All versions are available through Maven Central and
[here](https://github.com/libktx/ktx/releases). You can also use the preview snapshot releases from
`https://oss.sonatype.org/content/repositories/snapshots/` repository.

All libraries follow the same naming schema - this is an example Gradle dependency:

```Groovy
compile "io.github.libktx:ktx-$module:$ktxVersion"
```

Replace `$module` with the name of required **KTX** library. `$ktxVersion` usually matches LibGDX version it was
compiled against - although it might end with `-b1` (if it is a beta release) or `-SNAPSHOT` (if you are using
the snapshots). For example, the first official beta release with the recent group ID was compiled against LibGDX
`1.9.6` and its version was `1.9.6-b2`. You can browse through our releases
[here](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.libktx%22).

Note that even snapshots should be more or less stable, as libraries are not pushed to _Maven Central_ unless they pass
the extensive tests.

### Documentation

Each module contains a `README.md` file with a list of all features or a guide with a few code snippets. Browse through the
directories in root folder to find out more about each library.

### [Contribution and working from sources](.github/CONTRIBUTING.md)

If you want to help, read the [contribution](.github/CONTRIBUTING.md) guideline and browse through the issues to see
what's currently to do. Don't be afraid to create issues just to ask a question or make a request for any kind of
improvements. Before creating any pull requests, be aware that the code is dedicated to [public domain](LICENSE.txt).
