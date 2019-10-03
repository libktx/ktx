# KTX : Graphics utilities

General utilities for handling LibGDX graphics-related API.

### Why?

While LibGDX API is not particularly bad in this case and the **KTX** module provides only minor improvements, 
Kotlin build-in features can greatly simplify some common operations related to graphics and prevent some
common bugs such as forgetting to start or end batch rendering.

### Guide

#### Miscellaneous utilities

- `color` factory methods allows to use idiomatic named parameters to construct LibGDX `Color` instances.
- `copy` extension method added to `Color`. It allows to create a new `Color` with copied color values. Supports values
overriding with optional, named parameters.
- `use` inlined extension methods added to `Batch`, `ShaderProgram` and `GLFrameBuffer`. They allow safe omission of the 
`begin()` and `end()` calls when using batches, shader programs and buffers. Note that a camera or projection matrix can
also be passed to the `Batch.use` extension function to have it automatically applied to the batch's projection matrix.
- `begin` extension methods that automatically set projection matrix from a `Camera` or `Matrix4` were added to `Batch`.

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

### Usage examples

Using a `Batch`:

```Kotlin
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

```Kotlin
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

Using a `ShapeRenderer`:

```kotlin
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.graphics.*

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

Using `ShapeRenderer` with vectors: 

```Kotlin
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import ktx.graphics.circle

fun drawCircle(renderer: ShapeRenderer) {
    val position = Vector2(1f, 0f)
    renderer.circle(position, radius=5f)
}
```

#### Synergy

Use [`ktx-math`](../math) for `Vector2` and `Vector3` extensions, including idiomatic Kotlin factory
methods and operator overloads.

### Alternatives

There are some general purpose LibGDX utility libraries out there, but most lack first-class Kotlin support.

- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired LibGDX Java utilities
library with some utilities similar to `ktx-graphics`.

#### Additional documentation

- [`SpriteBatch` official article.](https://github.com/libgdx/libgdx/wiki/Spritebatch%2C-Textureregions%2C-and-Sprites)
- [Official article on shaders.](https://github.com/libgdx/libgdx/wiki/Shaders)
- [`ShapeRenderer` official article.](https://github.com/libgdx/libgdx/wiki/Rendering-shapes)
