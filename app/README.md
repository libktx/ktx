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

- [Official life cycle article.](https://github.com/libgdx/libgdx/wiki/The-life-cycle)
- [Official viewports article.](https://github.com/libgdx/libgdx/wiki/Viewports)
- [Official article on profiling.](https://github.com/libgdx/libgdx/wiki/Profiling)
