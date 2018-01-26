package ktx.freetype.async

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import ktx.async.assets.AssetStorage
import ktx.freetype.freeTypeFontParameters

/**
 * Registers all loaders necessary to load [BitmapFont] and [FreeTypeFontGenerator] instances from TTF and OTF files.
 * @param fileExtensions a collection of supported file extensions. If an empty array is passed, [BitmapFont] loaders
 * will not be registered. Defaults to ".ttf" and ".otf".
 * @param replaceDefaultBitmapFontLoader if true, default [BitmapFont] loader will be replaced and any attempts to load
 * [BitmapFont] will result in use of [FreetypeFontLoader] instead. [fileExtensions] will be ignored and FreeType loader
 * will be used by default unless overridden.
 */
fun AssetStorage.registerFreeTypeFontLoaders(
    fileExtensions: Array<String> = arrayOf(".ttf", ".otf"),
    replaceDefaultBitmapFontLoader: Boolean = false) {
  val fontGeneratorLoader = FreeTypeFontGeneratorLoader(fileResolver)
  setLoader<FreeTypeFontGenerator>(fontGeneratorLoader)

  val fontLoader = FreetypeFontLoader(fileResolver)
  if (replaceDefaultBitmapFontLoader) {
    setLoader<BitmapFont>(fontLoader)
  } else {
    fileExtensions.forEach { extension ->
      setLoader<BitmapFont>(fontLoader, suffix = extension)
    }
  }
}

/**
 * Allows to customize parameters of a loaded FreeType font.
 * @param file path to the FreeType font file.
 * @param setup should specify font parameters. Will be invoked on a new instance of [FreeTypeFontParameter]. Inlined.
 * @return fully loaded BitmapFont. Note that this method will suspend the current coroutine to perform asynchronous
 * font loading.
 */
inline suspend fun AssetStorage.loadFreeTypeFont(
    file: String,
    setup: FreeTypeFontParameter.() -> Unit = {}): BitmapFont =
    load<BitmapFont>(file, parameters = freeTypeFontParameters(file, setup))
