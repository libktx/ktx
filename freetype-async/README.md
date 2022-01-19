[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-freetype-async.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-freetype-async)

# KTX: FreeType font asynchronous loading utilities

A tiny modules that makes it easier to use [`gdx-freetype`](https://libgdx.com/wiki/extensions/gdx-freetype)
library along with the coroutines-based `AssetStorage` from [`ktx-assets-async`](../assets-async).

### Why?

`gdx-freetype` requires quite a bit of setup before it can be fully integrated with `AssetManager` or `AssetStorage`
due to how libGDX `AssetManager` loaders are implemented. This module aims to limit the boilerplate necessary to load
FreeType fonts in libGDX applications with the asynchronous **KTX** `AssetStorage`.

See also: [`ktx-freetype`](../freetype).

### Guide

This module consists of the following utilities:

* Extension method `AssetStorage.registerFreeTypeFontLoaders` allows to register all loaders required to load FreeType
font assets. It should be called right after constructing a `AssetStorage` instance and before loading any assets.
* Extension methods that allow to easily configure loaded `BitmapFont` instances with Kotlin DSL:
  * `AssetStorage.loadFreeTypeFont`: suspending loading method that works similarly to `AssetStorage.load`.
  * `AssetStorage.loadFreeTypeFontAsync`: loading method that returns `Deferred<BitmapFont>` similarly to `AssetStorage.loadAsync`.
  * `AssetStorage.loadFreeTypeFontSync`: blocking loading method that works similarly to `AssetStorage.loadSync`.

Since it depends on the [`ktx-freetype`](../freetype) module, it also comes with the following utilities that might
prove useful even when using `AssetStorage`:

* `ktx.freetype.freeTypeFontParameters` function is a Kotlin DSL for customizing font loading parameters.
* `FreeTypeFontGenerator.generateFont` extension function generates `BitmapFont` instances using a
`FreeTypeFontGenerator` with Kotlin DSL.

The module also provides `AsyncAssetManager.loadFreeTypeFontAsync` that works similarly to the
`AssetManager.loadFreeTypeFont` extension from [`ktx-freetype`](../freetype), but returns a `Deferred<BitmapFont>`
instance instead.

In order to use this module, `com.badlogicgames.gdx:gdx-freetype` dependency has to be added to the `core` project.

### Usage examples

Creating `AssetStorage` with registered FreeType font loaders:

```kotlin
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.freetype.async.registerFreeTypeFontLoaders

fun initiateAssetStorage(): AssetStorage {
  // Coroutines have to be enabled.
  // This has to be called on the main rendering thread:
  KtxAsync.initiate()

  val assetStorage = AssetStorage()
  // Registering TTF/OTF file loaders:
  assetStorage.registerFreeTypeFontLoaders()

  // AssetStorage is now ready to load FreeType fonts.
  return assetStorage
}
```

Registering `BitmapFont` loaders only for custom file extensions:

```kotlin
import ktx.freetype.async.*

assetStorage.registerFreeTypeFontLoaders(fileExtensions = arrayOf(".custom"))
```

Replacing default `BitmapFont` loader with FreeType font loader:

```kotlin
import ktx.freetype.async.*

assetStorage.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)
```

Loading a FreeType font using `AssetStorage` in a coroutine:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.freetype.async.loadFreeTypeFont

fun loadFont(assetStorage: AssetStorage) {
  // Launching a coroutine:
  KtxAsync.launch {
    // Loading a font:
    val font: BitmapFont = assetStorage.loadFreeTypeFont("font.ttf")
    // Font is now ready to use.
  } 
}
```

Loading a FreeType font asynchronously using `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.freetype.async.loadFreeTypeFontAsync

fun loadFont(assetStorage: AssetStorage) {
  // Scheduling font loading:
  val deferred = assetStorage.loadFreeTypeFontAsync("font.ttf")
  // Launching a coroutine:
  KtxAsync.launch {
    // Waiting until the font is loaded:
    val font: BitmapFont = deferred.await()
    // Font is now ready to use.
  } 
}
```

Loading a FreeType font synchronously using `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.assets.async.AssetStorage
import ktx.freetype.async.loadFreeTypeFontSync

fun loadFont(assetStorage: AssetStorage) {
  // Blocking the current thread until the font is loaded:
  val font: BitmapFont = assetStorage.loadFreeTypeFontSync("font.ttf")
  // Since this is a blocking method, it should be used cautiously,
  // e.g. only to load assets necessary to display the loading screen.
}
```

Loading a FreeType font with custom parameters using `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.Color
import ktx.freetype.async.loadFreeTypeFontAsync

val font = assetStorage.loadFreeTypeFontAsync("font.ttf") {
  size = 14
  borderWidth = 1.5f
  color = Color.ORANGE
  borderColor = Color.BLUE
}
```

Accessing a fully loaded font in a coroutine:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont

val font = assetStorage.get<BitmapFont>("font.ttf").await()
```

Loading a `FreeTypeFontGenerator`:

```kotlin
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

val generator = assetStorage.load<FreeTypeFontGenerator>("font.ttf")
```

Creating a `FreeTypeFontLoaderParameter` with customized font parameters:

```kotlin
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import ktx.freetype.*

fun getFontParameters(): FreeTypeFontLoaderParameter = freeTypeFontParameters("font.ttf") {
  size = 14
  borderWidth = 1.5f
  color = Color.ORANGE
  borderColor = Color.BLUE
}
```

Generating a new `BitmapFont` using libGDX `FreeTypeFontGenerator`:

```kotlin
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.freetype.*

val generator: FreeTypeFontGenerator
// Default parameters:
val fontA = generator.generateFont()
// Customized:
val fontB = generator.generateFont {
  size = 42
}
```

Loading a font asynchronously with `AsyncAssetManager`:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import kotlinx.coroutines.launch
import ktx.assets.async.AsyncAssetManager
import ktx.async.KtxAsync
import ktx.freetype.async.loadFreeTypeFontAsync

fun loadFont(assetManager: AsyncAssetManager) {
  // Schedule font loading:
  val deferred = assetManager.loadFreeTypeFontAsync("font.ttf")

  // In the meanwhile, the assetManager should be updated on
  // the rendering thread with assetManager.update() calls.

  // Launching a coroutine:
  KtxAsync.launch { 
    // Awaiting until the font is loaded:
    val font: BitmapFont = deferred.await()
    // Now the font is loaded and can be used.
  }
}
```

### Alternatives

FreeType font loaders can be registered manually. See
[this article](https://libgdx.com/wiki/managing-your-assets#loading-a-ttf-using-the-assethandler).
`setLoader` method can be used to add new libGDX loaders to an `AssetStorage`.

#### Additional documentation

- [Official `gdx-freetype` article.](https://libgdx.com/wiki/extensions/gdx-freetype)
- [`ktx-async` module.](../async)
- [`ktx-assets-async` module.](../assets-async)
