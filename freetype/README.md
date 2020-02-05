[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-freetype.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-freetype)

# KTX: FreeType font utilities

A tiny modules that makes it easier to use [`gdx-freetype`](https://github.com/libgdx/libgdx/wiki/Gdx-freetype) library.

### Why?

`gdx-freetype` requires quite a bit of setup before it can be fully integrated with LibGDX `AssetManager`. This module
aims to limit the boilerplate necessary to load FreeType fonts in LibGDX applications.

### Guide

This module consists of the following utilities:

* Extension method `AssetManager.registerFreeTypeFontLoaders` allows to register all loaders required to load FreeType
font assets. It should be called right after constructing a `AssetManager` instance and before loading any assets.
* Extension method `AssetManager.loadFreeTypeFont` allows to easily configure loaded `BitmapFont` instances with Kotlin
DSL.
* `freeTypeFontParameters` function is a Kotlin DSL for customizing font loading parameters.
* `FreeTypeFontGenerator.generateFont` extension function allows to generate `BitmapFont` instances using a
`FreeTypeFontGenerator` with Kotlin DSL.

### Usage examples

Creating `AssetManager` with registered FreeType font loaders:

```kotlin
import com.badlogic.gdx.assets.AssetManager
import ktx.freetype.*

fun initiateAssetManager(): AssetManager {
  val assetManager = AssetManager()
  // Calling registerFreeTypeFontLoaders is necessary in order to load TTF/OTF files:
  assetManager.registerFreeTypeFontLoaders()
  return assetManager
}
```

Registering `BitmapFont` loaders for custom file extensions:

```kotlin
import ktx.freetype.*

assetManager.registerFreeTypeFontLoaders(fileExtensions = arrayOf(".custom"))
```

Replacing default `BitmapFont` loader with FreeType font loader:

```kotlin
import ktx.freetype.*

assetManager.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)
```

Loading a FreeType font using `AssetManager`:

```kotlin
import ktx.freetype.*

assetManager.loadFreeTypeFont("font.ttf")
```

Loading a FreeType font with custom parameters using `AssetManager`:

```kotlin
import ktx.freetype.*

assetManager.loadFreeTypeFont("font.ttf") {
  size = 14
  borderWidth = 1.5f
  color = Color.ORANGE
  borderColor = Color.BLUE
}
```

Accessing a fully loaded font (note: `AssetManager` must finish loading the asset first):

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont

val font = assetManager.get<BitmapFont>("font.ttf")
```

Using delegation to schedule loading of a FreeType font:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.assets.getValue
import ktx.freetype.loadFreeTypeFont

val font: BitmapFont by assetManager.loadFreeTypeFont("font.ttf")

// `font` variable can be accessed once the asset is fully loaded. See ktx-assets README.
```

Loading `FreeTypeFontGenerator` with [`ktx-assets`](../assets):

```kotlin
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.assets.load

assetManager.load<FreeTypeFontGenerator>("font.tff")
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

val generator: FreeTypeFontGenerator = getGenerator()
// Default parameters:
val fontA = generator.generateFont()
// Customized:
val fontB = generator.generateFont {
  size = 42
}
```

### Synergy

This module uses [`ktx-assets`](../assets) internally to improve `AssetManager` API.

### Alternatives

FreeType font loaders can be registered manually. See
[this article](https://github.com/libgdx/libgdx/wiki/Managing-your-assets#loading-a-ttf-using-the-assethandler).

#### Additional documentation

- [`gdx-freetype` article.](https://github.com/libgdx/libgdx/wiki/Gdx-freetype)
- [`AssetManager` article.](https://github.com/libgdx/libgdx/wiki/Managing-your-assets#loading-a-ttf-using-the-assethandler)
