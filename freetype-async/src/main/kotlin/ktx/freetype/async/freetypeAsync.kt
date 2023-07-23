package ktx.freetype.async

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import kotlinx.coroutines.Deferred
import ktx.assets.Asset
import ktx.assets.async.AssetStorage
import ktx.assets.async.AsyncAssetManager
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
  replaceDefaultBitmapFontLoader: Boolean = false,
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
 * Loads a FreeType font via a coroutine. Allows customizing parameters of a loaded FreeType font.
 *
 * [path] is the file path to the FreeType font file.
 * Must be compatible with the [AssetStorage.fileResolver].
 *
 * [setup] can be used to specify and customize the parameters of the loaded font.
 * It will be inlined and invoked on a [FreeTypeFontParameter].
 *
 * Returns the result of font loading. See [AssetStorage.load] for lists of possible outcomes.
 *
 * See also [loadFreeTypeFontAsync] and [loadFreeTypeFontSync].
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun AssetStorage.loadFreeTypeFont(
  path: String,
  setup: FreeTypeFontParameter.() -> Unit = {},
): BitmapFont {
  contract { callsInPlace(setup, InvocationKind.EXACTLY_ONCE) }
  return load<BitmapFont>(path, parameters = freeTypeFontParameters(path, setup))
}

/**
 * Loads a FreeType font asynchronously. Allows customizing parameters of a loaded FreeType font.
 *
 * [path] is the file path to the FreeType font file.
 * Must be compatible with the [AssetStorage.fileResolver].
 *
 * [setup] can be used to specify and customize the parameters of the loaded font.
 * It will be inlined and invoked on a [FreeTypeFontParameter].
 *
 * Returns a [Deferred] reference to the loaded font. See [AssetStorage.loadAsync] for lists of possible outcomes.
 *
 * See also [loadFreeTypeFont] and [loadFreeTypeFontSync].
 */
@OptIn(ExperimentalContracts::class)
inline fun AssetStorage.loadFreeTypeFontAsync(
  path: String,
  setup: FreeTypeFontParameter.() -> Unit = {},
): Deferred<BitmapFont> {
  contract { callsInPlace(setup, InvocationKind.EXACTLY_ONCE) }
  return loadAsync<BitmapFont>(path, parameters = freeTypeFontParameters(path, setup))
}

/**
 * Loads a FreeType font synchronously. Blocks the current thread until the font is loaded.
 * Allows customizing parameters of a loaded FreeType font.
 *
 * This method is safe to call from the main rendering thread, as well as other application threads.
 * However, make sure that the same font is not loaded asynchronously by other thread with the same
 * [AssetStorage]. See [AssetStorage.loadSync] for details.
 *
 * [path] is the file path to the FreeType font file.
 * Must be compatible with the [AssetStorage.fileResolver].
 *
 * [setup] can be used to specify and customize the parameters of the loaded font.
 * It will be inlined and invoked on a [FreeTypeFontParameter].
 *
 * Returns the result of font loading. See [AssetStorage.loadSync] for lists of possible outcomes.
 *
 * See also [loadFreeTypeFont] and [loadFreeTypeFontAsync].
 */
@OptIn(ExperimentalContracts::class)
inline fun AssetStorage.loadFreeTypeFontSync(
  path: String,
  setup: FreeTypeFontParameter.() -> Unit = {},
): BitmapFont {
  contract { callsInPlace(setup, InvocationKind.EXACTLY_ONCE) }
  return loadSync<BitmapFont>(path, parameters = freeTypeFontParameters(path, setup))
}

/**
 * Allows customizing parameters of a FreeType font loaded asynchronously.
 * @param file path to the FreeType font file.
 * @param setup should specify font parameters. Will be invoked on a new instance of [FreeTypeFontParameter]. Inlined.
 * @return [Asset] wrapper which allows to access the font once it is loaded.
 */
@OptIn(ExperimentalContracts::class)
inline fun AsyncAssetManager.loadFreeTypeFontAsync(
  file: String,
  setup: FreeTypeFontParameter.() -> Unit = {},
): Deferred<BitmapFont> {
  contract { callsInPlace(setup, InvocationKind.EXACTLY_ONCE) }
  return loadAsync<BitmapFont>(file, parameters = freeTypeFontParameters(file, setup))
}
