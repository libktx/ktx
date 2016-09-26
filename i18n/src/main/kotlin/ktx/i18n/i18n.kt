package ktx.i18n

import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import java.util.Locale

/**
 * Utility storage for global [I18NBundle] object.
 * @author MJ
 */
object I18n {
  private val listeners = com.badlogic.gdx.utils.Array<(I18NBundle?) -> Unit>(4)
  /**
   * Used as the default application's [I18NBundle] instance by [nls] methods. Has to be set explicitly.
   */
  var defaultBundle: I18NBundle? = null
    set(value) {
      field = value
      for (listener in listeners) {
        listener(value)
      }
    }

  /**
   * @param listener will be invoked each time the [I18NBundle] is reloaded.
   * @see removeListener
   */
  fun addListener(listener: (I18NBundle?) -> Unit) {
    listeners.add(listener)
  }

  /**
   * @param listener will no longer be invoked each time the default bundle is changed.
   */
  fun removeListener(listener: (I18NBundle?) -> Unit) {
    listeners.removeValue(listener, true)
  }

  /**
   * Removes all bundle reload listeners.
   */
  fun clearListeners() {
    listeners.clear()
  }

  /**
   * Will load the bundle at the selected path and set it as [defaultBundle] of the application.
   * @param path without extension or locale. Must include i18n bundle properties files.
   * @param locale will be used to load the bundle.
   * @param fileType type of the locale file. Defaults to internal.
   */
  fun load(path: String, locale: Locale, fileType: FileType = FileType.Internal) {
    defaultBundle = I18NBundle.createBundle(Gdx.files.getFileHandle(path, fileType), locale)
  }
}

/**
 * @param key property name in the i18n bundle.
 * @param args will replace the argument placeholders in the selected bundle line. The order is preserved and honored.
 * @return formatted value mapped to the key extracted from the bundle.
 */
operator fun I18NBundle.get(key: String, vararg args: Any?): String = this.format(key, *args)

/**
 * @param key property name in the i18n bundle.
 * @param bundle i18n bundle which must contain the key. Defaults to bundle stored in [I18n].
 * @return value mapped to the key extracted from the bundle.
 */
fun nls(key: String, bundle: I18NBundle = I18n.defaultBundle!!): String = bundle.get(key)

/**
 * @param key property name in the i18n bundle.
 * @param args will replace the argument placeholders in the selected bundle line. The order is preserved and honored.
 * @param bundle i18n bundle which must contain the key. Defaults to bundle stored in [I18n].
 * @return formatted value mapped to the key extracted from the bundle.
 */
fun nls(key: String, vararg args: Any?, bundle: I18NBundle = I18n.defaultBundle!!): String = bundle.format(key, *args)

/**
 * Adds default functions to an object, turning it into a [I18NBundle] line data container. Expects that its [toString]
 * method returns a valid bundle line ID. Advised to be implemented by an enum for extra utility.
 * @author MJ
 */
interface BundleLine {
  /**
   * [I18NBundle] instance storing the localized line.
   */
  val bundle: I18NBundle
    get() = I18n.defaultBundle!!

  /**
   * @return value mapped to the selected [I18NBundle] line ID without formatting.
   */
  operator fun invoke(): String = bundle.get(toString())

  /**
   * @param args will be used to format the line.
   * @return value mapped to the selected [I18NBundle] line ID formatted with the passed args.
   */
  operator fun invoke(vararg args: Any?): String = bundle.format(toString(), *args)
}

/**
 * @param line value mapped to its ID will be extracted. Its [toString] method should match property name in i18n bundle.
 * @return localized text from the selected bundle.
 */
fun nls(line: BundleLine): String = line()

/**
 * @param line value mapped to its ID will be extracted. Its [toString] method should match property name in i18n bundle.
 * @param args will be used to format the bundle line.
 * @return formatted value mapped to the key extracted from the bundle.
 */
fun nls(line: BundleLine, vararg args: Any?): String = line(*args)

/**
 * @param line value mapped to its ID will be extracted. Its [toString] method should match property name in i18n bundle.
 * @return localized text from the selected bundle.
 */
operator fun I18NBundle.get(line: BundleLine): String = this.get(line.toString())

/**
 * @param line value mapped to its ID will be extracted. Its [toString] method should match property name in i18n bundle.
 * @param args will replace the argument placeholders in the selected bundle line. The order is preserved and honored.
 * @return formatted value mapped to the key extracted from the bundle.
 */
operator fun I18NBundle.get(line: BundleLine, vararg args: Any?): String = this.format(line.toString(), *args)
