package ktx.graphics

import com.badlogic.gdx.graphics.Color

/**
 * Factory methods for libGDX [Color] class. Allows using named parameters.
 * @param red red color value.
 * @param green green color value.
 * @param blue blue color value.
 * @param alpha color alpha value. Optional, defaults to 1f (non-transparent).
 * @return a new [Color] instance.
 */
fun color(
  red: Float,
  green: Float,
  blue: Float,
  alpha: Float = 1f,
) = Color(red, green, blue, alpha)

/**
 * Allows to copy this [Color] instance, optionally changing chosen properties.
 * @param red red color value. If null, will be copied from [Color.r]. Defaults to null.
 * @param green green color value. If null, will be copied from [Color.g]. Defaults to null.
 * @param blue blue color value. If null, will be copied from [Color.b]. Defaults to null.
 * @param alpha color alpha value. If null, will be copied from [Color.a]. Defaults to null.
 * @return a new [Color] instance with values copied from this color and optionally overridden by the parameters. */
fun Color.copy(
  red: Float? = null,
  green: Float? = null,
  blue: Float? = null,
  alpha: Float? = null,
) = Color(red ?: r, green ?: g, blue ?: b, alpha ?: a)

/**
 * Returns the red component of the color.
 * Allows using destructuring declarations when working with libGDX [Color] class, for example:
 * ```
 * val (red, green, blue) = myColor
 * ```
 * @return [Color.r]
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun Color.component1() = r

/**
 * Returns the green component of the color.
 * Allows using destructuring declarations when working with libGDX [Color] class, for example:
 * ```
 * val (red, green, blue) = myColor
 * ```
 * @return [Color.g]
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun Color.component2() = g

/**
 * Returns the blue component of the color.
 * Allows using destructuring declarations when working with libGDX [Color] class, for example:
 * ```
 * val (red, green, blue) = myColor
 * ```
 * @return [Color.b]
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun Color.component3() = b

/**
 * Returns the alpha component of the color.
 * Allows using destructuring declarations when working with libGDX [Color] class, for example:
 * ```
 * val (red, green, blue, alpha) = myColor
 * ```
 * @return [Color.a]
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun Color.component4() = a
