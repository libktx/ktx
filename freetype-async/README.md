[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-freetype-async.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-freetype-async)

# KTX: FreeType font asynchronous loading utilities

A tiny modules that makes it easier to use [`gdx-freetype`](https://github.com/libgdx/libgdx/wiki/Gdx-freetype)
library along with the coroutines-based `AssetStorage` from [`ktx-assets-async`](../assets-async).

### Why?

`gdx-freetype` requires quite a bit of setup before it can be fully integrated with `AssetManager` or `AssetStorage`
due to how LibGDX `AssetManager` loaders are implemented. This module aims to limit the boilerplate necessary to load
FreeType fonts in LibGDX applications with the asynchronous **KTX** `AssetStorage`.

See also: [`ktx-freetype`](../freetype).

### Guide

This module consists of the following utilities:

* Extension method `AssetStorage.registerFreeTypeFontLoaders` allows to register all loaders required to load FreeType
font assets. It should be called right after constructing a `AssetStorage` instance and before loading any assets.
* Extension method `AssetStorage.loadFreeTypeFont` allows to easily configure loaded `BitmapFont` instances with Kotlin
DSL.

Since it depends on the [`ktx-freetype`](../freetype) module, it also comes with the following utilities that might
prove useful even when using `AssetStorage`:

* `ktx.freetype.freeTypeFontParameters` function is a Kotlin DSL for customizing font loading parameters.
* `FreeTypeFontGenerator.generateFont` extension function allows to generate `BitmapFont` instances using a
`FreeTypeFontGenerator` with Kotlin DSL.

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

Loading a FreeType font with custom parameters using `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.Color
import ktx.freetype.async.*

val font = assetStorage.loadFreeTypeFont("font.ttf") {
  size = 14
  borderWidth = 1.5f
  color = Color.ORANGE
  borderColor = Color.BLUE
}
```

Accessing a fully loaded font:

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

Generating a new `BitmapFont` using LibGDX `FreeTypeFontGenerator`:

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

### Alternatives

FreeType font loaders can be registered manually. See
[this article](https://github.com/libgdx/libgdx/wiki/Managing-your-assets#loading-a-ttf-using-the-assethandler).
`setLoader` method can be used to add new LibGDX loaders to an `AssetStorage`.

#### Additional documentation

- [Official `gdx-freetype` article.](https://github.com/libgdx/libgdx/wiki/Gdx-freetype)
- [`ktx-async` module.](../async)
- [`ktx-assets-async` module.](../assets-async)
