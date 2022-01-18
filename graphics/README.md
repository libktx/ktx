[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-graphics.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-graphics)

# KTX: Graphics utilities

General utilities for handling libGDX graphics-related API.

### Why?

While libGDX API is not particularly bad in this case and the **KTX** module provides only minor improvements,
Kotlin build-in features can greatly simplify some common operations related to graphics and prevent some
common bugs such as forgetting to start or end batch rendering.

### Guide

#### Miscellaneous utilities

- `use` inlined extension methods added to `Batch`, `ShaderProgram` and `GLFrameBuffer`. They allow safe omission of the
`begin()` and `end()` calls when using batches, shader programs and buffers. Note that a camera or a matrix can also
be passed to selected extension methods to have it automatically applied as the projection matrix.
- `begin` extension methods that automatically set projection matrix from a `Camera` or `Matrix4` were added to `Batch`.
- `takeScreenshot` allows to easily take a screenshot of current application screen.
- `BitmapFont.center` extension method allows calculating center position of text in order to draw it in the middle
of a chosen object.

#### `Color` utilities

- `color` factory methods allows using named parameters to construct libGDX `Color` instances.
- `copy` extension method added to `Color`. It allows to create a new `Color` with copied color values. Supports
overriding individual color values with optional named parameters.
- `Color` instances can be destructed into individual color values.

#### `ShapeRenderer`

`ShapeRenderer` API, which normally consumes `float` primitives, was extended to support `Vector2` and `Vector3`
instances instead of `x`, `y` and `z` position parameters. The extension methods have the same names as the
original `ShapeRenderer` methods and perform the same actions. The methods include:

- `arc`
- `box`
- `circle`
- `cone`
- `ellipse`
- `rect`
- `rectLine`
- `rotate`
- `scale`
- `translate`
- `triange`

A `use` inlined extension method is also available for `ShapeRenderer`, ensuring you won't have unbalanced `begin()`
and `end()` calls.

#### Cameras and viewports

- `Camera.center` extension method allows to center the camera's position to screen center or the center of the chosen rectangle.
- `Camera.moveTo` extension method allows to move the camera immediately at the chosen target position with optional offset.
- `Camera.lerpTo` extension method allows to move the camera smoothly to the chosen target position with optional offset.
- `Camera.update` inlined extension method changes camera state with automatic `Camera.update` call.
- `LetterboxingViewport` combines `ScreenViewport` and `FitViewport` behavior: it targets a specific aspect ratio and
applies letterboxing like `FitViewport`, but it does not scale rendered objects when resized, keeping them in fixed size
similarly to `ScreenViewport`. Thanks to customizable target PPI value, it is ideal for GUIs and can easily support
different screen sizes.

### Usage examples

Using a `Batch`:

```kotlin
import ktx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

val batch = SpriteBatch()
val font = BitmapFont()
// Drawing the font with batch:
batch.use {
  font.draw(it, "KTX!", 100f, 100f)
}

/* The snippet above is an equivalent to:

  batch.begin()
  font.draw(batch, "KTX!", 100f, 100f)
  batch.end()
*/
```

Using a `Batch` with a `Camera`:

```kotlin
import ktx.graphics.*
import com.badlogic.gdx.graphics.OrthographicCamera

val camera = OrthographicCamera()

// Projection matrix will be copied from the camera:
batch.use(camera) {
  font.draw(it, "KTX!", 100f, 100f)
}

/* Equivalent to:

  batch.projectionMatrix = camera.combined
  batch.begin()
  font.draw(batch, "KTX!", 100f, 100f)
  batch.end()
*/
```

Using a `ShaderProgram`:

```kotlin
import ktx.graphics.*

shaderProgram.use {
  // Operate on shaderProgram instance.
}

/* The snippet above is an equivalent to:

  shaderProgram.begin()
  // Operate on shaderProgram instance.
  shaderProgram.end()
*/
```

Creating `Color` instances:

```kotlin
import ktx.graphics.*

val myColor = color(red = 1f, green = 0.5f, blue = 0.75f, alpha = 0.25f)
// Fourth parameter - alpha - is optional and defaults to 1f:
val nonTransparentGray = color(0.8f, 0.8f, 0.8f)
```

Copying `Color` instances:

```kotlin
import ktx.graphics.*
import com.badlogic.gdx.graphics.Color

val blue = Color.BLUE.copy()
// `blue` has same values as `Color.BLUE`, but it's not the same instance.

// You can optionally override chosen copied values:
val violet = blue.copy(red = 1f)
```

Destructing `Color` instances:

```kotlin
import ktx.graphics.*
import com.badlogic.gdx.graphics.Color

fun destruct(color: Color) {
  val (red, green, blue, alpha) = color
}
```

Using a `ShapeRenderer`:

```kotlin
import ktx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

val shapeRenderer = ShapeRenderer()

shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
  // Operate on shapeRenderer instance
}

/* The snippet above is an equivalent to:

  shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
  // Operate on shapeRenderer instance
  shapeRenderer.end()
*/
```


Using a `ShapeRenderer` with a `Camera`:

```kotlin
import ktx.graphics.*
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

val camera = OrthographicCamera()
val shapeRenderer = ShapeRenderer()

// Projection matrix will be copied from the camera:
shapeRenderer.use(ShapeRenderer.ShapeType.Filled, camera) {
  // Operate on shapeRenderer instance
}

/* Equivalent to:

  shapeRenderer.projectionMatrix = camera.combined
  shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
  // Operate on shapeRenderer instance
  shapeRenderer.end()
*/
```

Using `ShapeRenderer` with vectors:

```kotlin
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import ktx.graphics.circle

fun drawCircle(renderer: ShapeRenderer) {
    val position = Vector2(1f, 0f)
    renderer.circle(position, radius=5f)
}
```

Taking a screenshot of the current game screen:

```kotlin
import com.badlogic.gdx.Gdx
import ktx.graphics.takeScreenshot

takeScreenshot(Gdx.files.external("mygame/screenshot.png"))
```

Finding out where to draw text in order to center it on a `Sprite`:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import ktx.graphics.center

fun getCenterAtSprite(
  bitmapFont: BitmapFont, text: String, sprite: Sprite
): Vector2 =
  bitmapFont.center(
    text,
    // Note that x or y can be modified if you want a slight offset:
    x = sprite.x, y = sprite.y,
    width = sprite.width, height = sprite.height
  )
```

Creating and customizing a new `LetterboxingViewport`:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.graphics.LetterboxingViewport

class Application: ApplicationAdapter() {
  val viewport: Viewport = LetterboxingViewport(targetPpiX = 96f, targetPpiY = 96f, aspectRatio = 4f / 3f)

  override fun resize(width: Int, height: Int) {
    // Updating viewport to the new screen size:
    viewport.update(width, height, true)
  }
}
```

Centering camera position:

```kotlin
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.graphics.center

fun centerCamera(camera: OrthographicCamera) {
  // Sets position to the middle of the screen:
  camera.center()

  // Sets position to the middle of the chosen rectangle:
  camera.center(x = 100f, y = 100f, width = 800f, height = 800f)
}
```

Moving the camera to a target:

```kotlin
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ktx.graphics.lerpTo
import ktx.graphics.moveTo

fun moveCamera(camera: OrthographicCamera, target: Vector2) {
  // Moves the camera immediately at the target:
  camera.moveTo(target)

  // Moves the camera smoothly to the target:
  camera.lerpTo(target, lerp = 0.1f)
}
```

Changing the camera state with automatic update:

```kotlin
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ktx.graphics.lerpTo
import ktx.graphics.update

fun moveCamera(camera: OrthographicCamera, target: Vector2) {
  camera.update {
    lerpTo(target, lerp = 0.1f)
    // camera.update() will be called automatically after this block.
  }
}
```

#### Synergy

Use [`ktx-math`](../math) for `Vector2` and `Vector3` extensions, including idiomatic Kotlin factory
methods and operator overloads.

### Alternatives

There are some general purpose libGDX utility libraries, but most lack first-class Kotlin support.

- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired libGDX Java utilities
library with some utilities similar to `ktx-graphics`.
- [Cyberpunk](https://github.com/ImXico/Cyberpunk) framework provides similar utilities for cameras, text and screenshots.

#### Additional documentation

- [`SpriteBatch` official article.](https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites)
- [Official article on shaders.](https://libgdx.com/wiki/graphics/opengl-utils/shaders)
- [`ShapeRenderer` official article.](https://libgdx.com/wiki/graphics/opengl-utils/rendering-shapes)
- [Official article on screenshots.](https://libgdx.com/wiki/graphics/taking-a-screenshot)
