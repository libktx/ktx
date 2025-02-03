package ktx.i18n

import com.badlogic.gdx.utils.I18NBundle

/**
 * @param key property name in the i18n bundle.
 * @param args will replace the argument placeholders in the selected bundle line. The order is preserved and honored.
 * @return formatted value mapped to the key extracted from the bundle.
 */
operator fun I18NBundle.get(
  key: String,
  vararg args: Any?,
): String = this.format(key, *args)

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
operator fun I18NBundle.get(
  line: BundleLine,
  vararg args: Any?,
): String = this.format(line.toString(), *args)

/**
 * Represents a single [I18NBundle]. Expects that its [toString] method returns a valid bundle line ID. Advised to be
 * implemented by an enum to introduce type-safe bundle representation with compile time safety.
 */
interface BundleLine {
  /**
   * [I18NBundle] instance storing the localized line. Has to be overridden in order to use [nls] and [invoke] methods.
   */
  val bundle: I18NBundle
    get() = throw NotImplementedError("Override BundleLine#bundle to use formatting methods and properties.")

  /**
   * [I18NBundle] line value provided by [bundle]. Use this property if the bundle line has no arguments.
   */
  val nls: String
    get() = bundle[toString()]

  /**
   * Use this method if the bundle line has any arguments.
   * @param arguments will be used to replace placeholders in formatted the line.
   * @return formatted [I18NBundle] line value provided by [bundle].
   */
  fun nls(vararg arguments: Any?): String = bundle.format(toString(), *arguments)

  /**
   * [nls] alias for extra conciseness. Use this property if the bundle line has no arguments.
   * @return [I18NBundle] line value provided by [bundle].
   */
  operator fun invoke(): String = bundle[toString()]

  /**
   * [nls] alias for extra conciseness. Use this method if the bundle line has any arguments.
   * @param arguments will be used to replace placeholders in formatted the line.
   * @return formatted [I18NBundle] line value provided by [bundle].
   */
  operator fun invoke(vararg arguments: Any?): String = bundle.format(toString(), *arguments)
}
