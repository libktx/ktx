package ktx.freetype

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader.FreeTypeFontGeneratorParameters
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import ktx.assets.Asset
import ktx.assets.load
import ktx.assets.setLoader

/**
 * Registers all loaders necessary to load [BitmapFont] and [FreeTypeFontGenerator] instances from TTF and OTF files.
 * @param fileExtensions a collection of supported file extensions. If an empty array is passed, [BitmapFont] loaders
 * will not be registered. Defaults to ".ttf" and ".otf".
 * @param replaceDefaultBitmapFontLoader if true, default [BitmapFont] loader will be replaced and any attempts to load
 * [BitmapFont] will result in use of [FreetypeFontLoader] instead. [fileExtensions] will be ignored and FreeType loader
 * will be used by default unless overridden.
 */
fun AssetManager.registerFreeTypeFontLoaders(
    fileExtensions: Array<String> = arrayOf(".ttf", ".otf"),
    replaceDefaultBitmapFontLoader: Boolean = false) {
  val fontGeneratorLoader = FreeTypeFontGeneratorLoader(fileHandleResolver)
  setLoader<FreeTypeFontGenerator, FreeTypeFontGeneratorParameters>(fontGeneratorLoader)

  val fontLoader = FreetypeFontLoader(fileHandleResolver)
  if (replaceDefaultBitmapFontLoader) {
    setLoader<BitmapFont, FreeTypeFontLoaderParameter>(fontLoader)
  } else {
    fileExtensions.forEach { extension ->
      setLoader<BitmapFont, FreeTypeFontLoaderParameter>(fontLoader, suffix = extension)
    }
  }
}

/**
 * Allows to customize parameters of a loaded FreeType font.
 * @param file path to the FreeType font file.
 * @param setup should specify font parameters. Will be invoked on a new instance of [FreeTypeFontParameter]. Inlined.
 * @return [Asset] wrapper which allows to access the font once it is loaded.
 */
inline fun AssetManager.loadFreeTypeFont(
    file: String,
    setup: FreeTypeFontParameter.() -> Unit = {}): Asset<BitmapFont> =
    load<BitmapFont>(file, parameters = freeTypeFontParameters(file, setup))

/**
 * Syntax sugar for [FreeTypeFontLoaderParameter] initialization. Used internally by [loadFreeTypeFont].
 * @param file path to the font file. Must be the same as the path passed to the loader.
 * @param setup should specify font parameters. Will be invoked on a new instance of [FreeTypeFontParameter]. Inlined.
 */
inline fun freeTypeFontParameters(
    file: String,
    setup: FreeTypeFontParameter.() -> Unit = {}) = FreeTypeFontLoaderParameter().apply {
  fontFileName = file
  fontParameters.apply(setup)
}

/**
 * Syntax sugar for [FreeTypeFontGenerator.generateFont]. Allows to use Kotlin DSL to initiate font parameters.
 * @param setup will be applied to newly constructed [FreeTypeFontParameter]. Inlined. If not given, will create a font
 * with default parameters.
 */
inline fun FreeTypeFontGenerator.generateFont(setup: FreeTypeFontParameter.() -> Unit = {}): BitmapFont =
    generateFont(FreeTypeFontParameter().apply(setup))
