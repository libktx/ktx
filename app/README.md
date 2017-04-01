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

#### `KtxApplicationListener`
- `KtxApplicationListener` is an `ApplicationListener` equivalent.
Because libgdx does not utilize Java 8's 'default' for interface methods, this
is necessary to avoid having to implement each method, even ones you do not use. Keeps your codebase more clean.
The default implementation of each method does nothing.
####

#### `KtxInputProcessor`
- `KtxInputProcessor` is an `InputProcessor` equivalent, similar in vein as `KtxApplicationListener`
####

#### Miscellaneous utilities

- `clearScreen` is an inlined utility function that hides the OpenGL calls, allowing to clear the screen with a chosen
color.
- `LetterboxingViewport` combines `ScreenViewport` and `FitViewport` behavior: it targets a specific aspect ratio and
applies letterboxing like `FitViewport`, but it does not scale rendered objects when resized, keeping them in fixed size
similarly to `ScreenViewport`. Thanks to customizable target PPI value, it is ideal for GUIs and can easily support
different screen sizes.

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

Creating and customizing a new `LetterboxingViewport`:

```Kotlin
import ktx.app.LetterboxingViewport

val viewport: Viewport = LetterboxingViewport(targetPpiX = 96f, targetPpiY = 96f, aspectRatio = 4f / 3f)
// Updating viewport on resize:
viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
```

```Kotlin
class MyApplicationListener : KtxApplicationListener {
  // Maybe you only care about these two methods, only implement the ones you want to use.
  // This can be used used in place of `KotlinApplication` if you want full control over the game loop
  // and will write it yourself
  override fun create() { }
  override fun render() { }
}
```

```Kotlin
class MyApplicationListener : KtxInputProcessor {
  // Maybe you only care about these two methods, only implement the ones you want to use.
  override fun keyTyped(character: Char) { 
    return false
  }

  override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) { 
    return false
  }
}
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
