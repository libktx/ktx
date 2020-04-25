package ktx.freetype.async

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import ktx.assets.async.AssetStorage
import ktx.freetype.freeTypeFontParameters
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Registers all loaders necessary to load [BitmapFont] and [FreeTypeFontGenerator]
 * instances from TTF and OTF files.
 *
 * [fileExtensions] is a collection of supported file extensions. If an empty array is passed,
 * [BitmapFont] loaders will not be registered. Defaults to ".ttf" and ".otf".
 *
 * If [replaceDefaultBitmapFontLoader] is true, default [BitmapFont] loader will be replaced
 * and any attempts to load [BitmapFont] will result in the use of [FreetypeFontLoader] instead.
 * [fileExtensions] will be ignored and FreeType loader will be used by default for all font
 * assets unless overridden.
 */
fun AssetStorage.registerFreeTypeFontLoaders(
  fileExtensions: Array<String> = arrayOf(".ttf", ".otf"),
  replaceDefaultBitmapFontLoader: Boolean = false
) {
  setLoader<FreeTypeFontGenerator> { FreeTypeFontGeneratorLoader(fileResolver) }

  if (replaceDefaultBitmapFontLoader) {
    setLoader<BitmapFont> { FreetypeFontLoader(fileResolver) }
  } else {
    fileExtensions.forEach { extension ->
      setLoader<BitmapFont>(suffix = extension) { FreetypeFontLoader(fileResolver) }
    }
  }
}

/**
 * Allows to customize parameters of a loaded FreeType font.
 *
 * [path] is the file path to the FreeType font file.
 * Must be compatible with the [AssetStorage.fileResolver].
 *
 * [setup] can be used to specify and customize the parameters of the loaded font.
 * It will be inlined and invoked on a [FreeTypeFontParameter].
 *
 * Returns the result of font loading. See [AssetStorage.load] for lists of possible outcomes.
 *
 * Note that you can also call [AssetStorage.loadSync] or [AssetStorage.loadAsync] directly if needed,
 * but you must pass [FreeTypeFontParameter]. See [freeTypeFontParameters] utility.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun AssetStorage.loadFreeTypeFont(
  path: String,
  setup: FreeTypeFontParameter.() -> Unit = {}
): BitmapFont {
  contract { callsInPlace(setup, InvocationKind.EXACTLY_ONCE) }
  return load<BitmapFont>(path, parameters = freeTypeFontParameters(path, setup))
}
