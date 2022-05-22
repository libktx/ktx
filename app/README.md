[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-app.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-app)

# KTX: Basic application utilities

Basic `ApplicationListener` implementations and general libGDX utilities.

### Why?

While libGDX offers some basic `ApplicationListener` implementations including `ApplicationAdapter` and `Game`, both are
pretty bare-bones. They do not handle screen clearing or manage views list, both of which often have to be set up
manually in libGDX applications. This module aims to provide a simple base for your custom `ApplicationListener`
implementations.

### Guide

#### `ApplicationListener` implementations

- `KtxApplicationAdapter` is an interface that extends `ApplicationListener`. Provides no-op implementations of all
methods, without being an abstract class like `com.badlogic.gdx.ApplicationAdapter`, which makes it more flexible.
- `KtxGame` is a bit more opinionated `Game` equivalent that not only delegates all game events to the current `Screen`
instance, but also ensures non-nullability of screens, manages screen clearing, and maintains screens collection, which
allows switching screens while knowing only their concrete class.
- `KtxScreen` is an interface extending `Screen` that provides no-op method implementations, making all methods optional
to override.

#### `InputProcessor` implementations

- `KtxInputAdapter` is an interface extending `InputProcessor`. Provides no-op implementations of all methods, without
being a class like `com.badlogic.gdx.InputAdapter`.

#### Miscellaneous utilities

- `clearScreen` is an inlined utility function that hides the OpenGL calls, allowing to clear the screen with a chosen
color.
- `emptyScreen` provides no-op implementations of `Screen`.
- `gdxError` throws a `GdxRuntimeException` with an optional message and cause.

#### Platform-specific utilities

- `Platform` is an object that exposes various utilities for platform-specific code.
  - `Platform.currentPlatform` returns current `ApplicationType` or throws `GdxRuntimeException` if unable to determine.
  - `Platform.version` returns the current version of the platform (e.g., Android API version, iOS major OS version).
  - Convenient checks that allow to determine current platform:
    - `Platform.isAndroid` checks if the current platform is Android.
    - `Platform.isDesktop` checks if the current platform is desktop with graphical application.
    - `Platform.isHeadless` checks if the current platform is desktop without graphical application.
    - `Platform.isiOS` checks if the current platform is iOS.
    - `Platform.isMobile` checks if the current platform is Android or iOS.
    - `Platform.isWeb` checks if the current platform is HTML/WebGL.
  - Inlined methods that allow to execute code on specific platforms:
    - `Platform.runOnAndroid` executes an action if the current platform is Android. Returns action result or null.
    - `Platform.runOnDesktop` executes an action if the current platform is desktop. Returns action result or null.
    - `Platform.runOnHeadless` executes an action if the current platform is headless desktop. Returns action result or null.
    - `Platform.runOniOS` executes an action if the current platform is iOS. Returns action result or null.
    - `Platform.runOnMobile` executes an action if the current platform is Android or iOS. Returns action result or null.
    - `Platform.runOnWeb` executes an action if the current platform is HTML/WebGL. Returns action result or null.
  - Inlined `runOnVersion` executes an action if the current platform version is within minimum and maximum values.

#### Profiling

- `profile` inlined function allows measuring performance of the chosen operation with libGDX `PerformanceCounter`.
- `PerformanceCounter.profile` inlined extension method eases direct usage of the `PerformanceCounter` class.
- `PerformanceCounter.prettyPrint` extension method allows to quickly log basic performance metrics.

### Usage examples

Implementing `KtxApplicationAdapter`:

```kotlin
import ktx.app.KtxApplicationAdapter

class MyApplicationListener : KtxApplicationAdapter {
  // Implementation of all ApplicationListener methods is optional. Override the ones you need.

  override fun create() {
    // Load the assets...
  }
  override fun render() {
    // ...and render your game.
  }
}
```

Implementing `KtxGame` with one screen that displays text with `Batch` utilities from `ktx-graphics`:

```kotlin
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use

class ExampleScreen : KtxScreen {
  // Notice no `lateinit var` - ExampleScreen has no create()
  // method and is constructed after libGDX is fully initiated
  // in ExampleGame.create method.
  val font = BitmapFont()
  val batch = SpriteBatch().apply {
    color = Color.WHITE
  }

  override fun render(delta: Float) {
    batch.use {
      font.draw(it, "Hello Kotlin!", 100f, 100f)
    }
  }

  override fun dispose() {
    // Will be automatically disposed of by the game instance.
    font.dispose()
    batch.dispose()
  }
}

/** ApplicationListener implementation. */
class ExampleGame : KtxGame<Screen>() {
  override fun create() {
    // Registering ExampleScreen in the game object: it will be
    // accessible through ExampleScreen class:
    addScreen(ExampleScreen())
    // Changing current screen to the registered instance of the
    // ExampleScreen class:
    setScreen<ExampleScreen>()
  }
}
```

Implementing `KtxInputAdapter`:

```kotlin
import ktx.app.KtxInputAdapter

class MyInputListener : KtxInputAdapter {
  // Implementation of all ApplicationListener methods is optional. Handle the events you plan on supporting.

  override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
    // Handle mouse click...
    return true
  }
}
```

Obtaining the current platform type:

```kotlin
import com.badlogic.gdx.Application.ApplicationType
import ktx.app.Platform

fun getCurrentPlatform(): ApplicationType {
  return Platform.currentPlatform
}
```

Verifying the current platform:

```kotlin
import ktx.app.Platform

fun checkPlatform() {
  if (Platform.isDesktop || Platform.isHeadless) {
    println("Will print only on desktop platforms!")
  }
}
```

Executing platform-specific code:

```kotlin
import ktx.app.Platform

fun runOnPlatform() {
  Platform.runOnMobile {
    println("Will print only on mobile platforms!")
  }
}
```

Executing platform-specific code with a return type:

```kotlin
import ktx.app.Platform

fun getForPlatform(): String {
  return Platform.runOnAndroid { "Android" } ?: "Not Android"
}
```

Executing code starting from a specific API version:

```kotlin
import com.badlogic.gdx.Application.ApplicationType
import ktx.app.Platform

fun executeOnSpecificVersion() {
  Platform.runOnVersion(minVersion = 8, platform = ApplicationType.iOS) {
    println("Will run only on iOS devices starting from 8 OS version.")
  }
}
```

Executing code on a specific API version range:

```kotlin
import com.badlogic.gdx.Application.ApplicationType
import ktx.app.Platform

fun executeOnSpecificVersion() {
  Platform.runOnVersion(minVersion = 20, maxVersion = 25, platform = ApplicationType.Android) {
    println("Will run only on specific Android devices.")
  }
}
```

Profiling an operation:

```kotlin
import ktx.app.profile

fun profileThreadSleep() {
  profile(name = "Thread.sleep", repeats = 10) {
    // Will be repeated 10 times to measure performance:
    Thread.sleep(10L)
  }
}
```

Profiling an operation with an existing `PerformanceCounter`:

```kotlin
import com.badlogic.gdx.utils.PerformanceCounter
import ktx.app.prettyPrint
import ktx.app.profile

fun profileThreadSleep() {
  // Window size passed to the constructor as the second argument
  // will be the default amount of repetitions during profiling:
  val profiler = PerformanceCounter("Thread.sleep", 10)
  profiler.profile {
    // Will be repeated 10 times to measure performance:
    Thread.sleep(10L)
  }

  // You can also print the report manually
  // with a custom number format:
  profiler.prettyPrint(decimalFormat = "%.4f s")
}
```

### Alternatives

There are some general purpose libGDX utility libraries out there, but most lack first-class Kotlin support.

- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired libGDX Java utilities
library with some classes similar to `ktx-app`.
- [LML](https://github.com/czyzby/gdx-lml/tree/master/lml) allows building `Scene2D` views using HTML-like syntax.
It also features a custom `ApplicationListener` implementation, which helps with managing `Scene2D` screens.
- [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) is a [Spring](https://spring.io/) inspired
model-view-controller framework built on top of libGDX. It features its own `ApplicationListener` implementation, which
initiates and handles annotated view instances.

#### Additional documentation

- [Official life cycle article.](https://libgdx.com/wiki/app/the-life-cycle)
- [Official viewports article.](https://libgdx.com/wiki/graphics/viewports)
- [Official article on profiling.](https://libgdx.com/wiki/graphics/profiling)
