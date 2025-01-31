package ktx.app

import com.badlogic.gdx.utils.ScreenUtils

/**
 * Clears current screen with the selected color. Inlined to lower the total method count. Assumes alpha is 1f.
 * Clears depth by default.
 * @param red red color value.
 * @param green green color value.
 * @param blue blue color value.
 * @param alpha color alpha value. Optional, defaults to 1f (non-transparent).
 * @param clearDepth adds the GL_DEPTH_BUFFER_BIT mask if true.
 * @see ScreenUtils.clear
 */
@Suppress("NOTHING_TO_INLINE")
inline fun clearScreen(
  red: Float,
  green: Float,
  blue: Float,
  alpha: Float = 1f,
  clearDepth: Boolean = true,
) {
  ScreenUtils.clear(red, green, blue, alpha, clearDepth)
}
