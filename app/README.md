# KTX: basic application utilities

Abstract `ApplicationListener` implementations and general LibGDX utilities.

### Why?

LibGDX offers some basic `ApplicationListener` implementations in form of `ApplicationAdapter` and `Game`, but both are
pretty basic. They do not handle screen clearing or fixed rendering time step, both of which often have to be set up
manually in LibGDX applications. This module aims to provide a simple base for your custom `ApplicationListener`: if you
do not have your favorite setup implemented just yet, it might be a good idea to base it on abstract classes provided
by `ktx-app`.

### Guide

#### `ApplicationListener` implementations

- `KotlinApplication` is an `ApplicationAdapter` equivalent. Additionally to providing empty implementations for all
optional `ApplicationListener` methods, it also automatically clears the screen and implements
[fixed rendering time steps](http://www.badlogicgames.com/forum/viewtopic.php?p=96803#p96803), allowing you to customize
time step duration and max time step with its constructor parameters. This is a solid base for your `ApplicationListener`
implementation if you like working from scratch.
- `KtxApplicationAdapter` is an `ApplicationListener` extension. Provides no-op implementations of all methods, without
being an abstract class like `com.badlogic.gdx.ApplicationAdapter`.
- `KtxGame` is a bit more opinionated `Game` equivalent that not only delegates all game events to the current `Screen`
instance, but also ensures non-nullability of screens, manages screen clearing and fixed rendering step, and maintains
screens collection, which allows to switch screens knowing only their concrete class. `KtxScreen` is an interface
extending `Screen` that provides no-op method implementations, making all methods optional to override.

#### `InputProcessor` implementations

- `KtxInputAdapter` is an `InputProcessor` extension. Provides no-op implementations of all methods, without
being an abstract class like `com.badlogic.gdx.InputAdapter`.

#### Miscellaneous utilities

- `clearScreen` is an inlined utility function that hides the OpenGL calls, allowing to clear the screen with a chosen
color.
- `LetterboxingViewport` combines `ScreenViewport` and `FitViewport` behavior: it targets a specific aspect ratio and
applies letterboxing like `FitViewport`, but it does not scale rendered objects when resized, keeping them in fixed size
similarly to `ScreenViewport`. Thanks to customizable target PPI value, it is ideal for GUIs and can easily support
different screen sizes.
- `color` factory methods allows to use idiomatic named parameters to construct LibGDX `Color` instances.
- `copy` extension method added to `Color`. It allows to create a new `Color` with copied color values. Supports values
overriding with optional, named parameters.
- `use` inlined extension methods added to `Batch` and `ShaderProgram`. They allow to omit the `begin()` and `end()`
calls before using batches and shader programs.
- `emptyScreen` provides no-op implementations of `Screen`.

### Usage examples

Creating a simple `ApplicationListener` based on `KotlinApplication`:

```Kotlin
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KotlinApplication

class MyApplication : KotlinApplication() {
  lateinit var batch: Batch
  lateinit var font: BitmapFont

  override fun create() {
    batch = SpriteBatch()
    font = BitmapFont()
  }

  override fun render(delta: Float) {
    batch.begin()
    font.draw(batch, "Hello world!", 100f, 100f)
    batch.end()
  }
}
```

Customizing fixed time steps of `KotlinApplication`:

```Kotlin
import ktx.app.KotlinApplication

class MyApplication : KotlinApplication(fixedTimeStep = 1f / 60f, maxDeltaTime = 1f / 15f) {
  // ...
}
```

Implementing `KtxApplicationAdapter`:

```Kotlin
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

Implementing `KtxGame` with one screen that displays text:

```Kotlin
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.use

class ExampleScreen : KtxScreen {
  // Notice no `lateinit var` - ExampleScreen has no create()
  // method and is constructed after LibGDX is fully initiated
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

```Kotlin
import ktx.app.KtxInputAdapter

class MyInputListener : KtxInputAdapter {
  // Implementation of all ApplicationListener methods is optional. Handle the events you plan on supporting.

  override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) {
    // Handle mouse click...
    return true
  }
}
```

Creating and customizing a new `LetterboxingViewport`:

```Kotlin
import ktx.app.LetterboxingViewport

val viewport: Viewport = LetterboxingViewport(targetPpiX = 96f, targetPpiY = 96f, aspectRatio = 4f / 3f)
// Updating viewport on resize:
viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
```

Using a `Batch`:

```Kotlin
import ktx.app.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

val batch = SpriteBatch()
val font = BitmapFont()
// Drawing the font with batch:
batch.use {
  font.draw(it, "KTX!", 100f, 100f)
}
// The snippet above is an equivalent to:
// batch.begin()
// font.draw(batch, "KTX!", 100f, 100f)
// batch.end()
```

Creating `Color` instances:
```Kotlin
import ktx.app.*

val color = color(red = 1f, green = 0.5f, blue = 0.75f, alpha = 0.25f)
// Fourth parameter - alpha - is optional and defaults to 1f:
val nonTransparentGray = color(0.8f, 0.8f, 0.8f)
```

Copying `Color` instances:
```Kotlin
import ktx.app.*
import com.badlogic.gdx.graphics.Color

val blue = Color.BLUE.copy()
// `blue` has same values as `Color.BLUE`, but it's not the same instance.

// You can optionally override chosen copied values:
val violet = blue.copy(red = 1f)
```

### Alternatives

There are some general purpose LibGDX utility libraries out there, but most lack first-class Kotlin support.

- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired LibGDX Java utilities
library with some classes similar to `ktx-app`.
- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) allows to build `Scene2D` views using
HTML-like syntax. It also features a custom `ApplicationListener` implementation, which helps with managing `Scene2D`
screens.
- [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) is a [Spring](https://spring.io/)-inspired
model-view-controller framework built on top of LibGDX. It features its own `ApplicationListener` implementation, which
initiates and handles annotated view instances.

#### Additional documentation

- [The life cycle article.](https://github.com/libgdx/libgdx/wiki/The-life-cycle)
- [Viewports article.](https://github.com/libgdx/libgdx/wiki/Viewports)
