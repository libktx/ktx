package ktx.freetype

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import ktx.assets.Asset
import ktx.assets.load

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
  setLoader(FreeTypeFontGenerator::class.java, fontGeneratorLoader)

  val fontLoader = FreetypeFontLoader(fileHandleResolver)
  if (replaceDefaultBitmapFontLoader) {
    setLoader(BitmapFont::class.java, fontLoader)
  } else {
    fileExtensions.forEach { extension ->
      setLoader(BitmapFont::class.java, extension, fontLoader)
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
    setup: FreeTypeFontParameter.() -> Unit = {}): Asset<BitmapFont>
    = load<BitmapFont>(file, parameters = freeTypeFontParameters(file, setup))

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
