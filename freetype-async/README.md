# KTX: FreeType asynchronous font loading utilities

A tiny modules that makes it easier to use [`gdx-freetype`](https://github.com/libgdx/libgdx/wiki/Gdx-freetype) library
along with the coroutines-based `AssetStorage` from [`ktx-async`](../async).

### Why?

`gdx-freetype` requires quite a bit of setup before it can be fully integrated with LibGDX `AssetStorage` due to how
LibGDX `AssetManager` loaders are implemented. This module aims to limit the boilerplate necessary to load FreeType
fonts in LibGDX applications.

See also [`ktx-freetype`](../freetype).

### Guide

This module consists of the following functions:

* Extension method `AssetStorage.registerFreeTypeFontLoaders` allows to register all loaders required to load FreeType
font assets. It should be called right after constructing a `AssetStorage` instance and before loading any assets.
* Extension method `AssetStorage.loadFreeTypeFont` allows to easily configure loaded `BitmapFont` instances with Kotlin
DSL.

Since it depends on the [`ktx-freetype`](../freetype) module, it also comes with the following function:

* `ktx.freetype.freeTypeFontParameters` is a Kotlin DSL for customizing font loading parameters.

### Usage examples

Creating `AssetStorage` with registered FreeType font loaders:

```kotlin
import ktx.async.assets.AssetStorage
import ktx.async.enableKtxCoroutines
import ktx.freetype.async.*

fun initiateAssetStorage(): AssetStorage {
  // Coroutines have to be enabled. AssetStorage uses an asynchronous executor, so concurrency level has to be above 0.
  enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
  
  val assetStorage = AssetStorage()
  assetStorage.registerFreeTypeFontLoaders()
  return assetStorage
}
```

Registering `BitmapFont` loaders for custom file extensions:

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
import ktx.async.ktxAsync
import ktx.freetype.async.*

ktxAsync {
  val font = assetStorage.loadFreeTypeFont("font.ttf")
  // font is BitmapFont
}
```

Loading a FreeType font with custom parameters using `AssetStorage`:

```kotlin
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

val font = assetStorage.get<BitmapFont>("font.ttf")
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

### Alternatives

FreeType font loaders can be registered manually. See
[this article](https://github.com/libgdx/libgdx/wiki/Managing-your-assets#loading-a-ttf-using-the-assethandler).
`setLoader` method can be used to add new LibGDX loaders to an `AssetStorage`.

#### Additional documentation

- [`gdx-freetype` article.](https://github.com/libgdx/libgdx/wiki/Gdx-freetype)
- [`ktx-async` module.](../async)
