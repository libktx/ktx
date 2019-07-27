package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

/**
 * Clears current screen with the selected color. Inlined to lower the total method count. Assumes alpha is 1f.
 * @param red red color value.
 * @param green green color value.
 * @param blue blue color value.
 * @param alpha color alpha value. Optional, defaults to 1f (non-transparent).
 */
@Suppress("NOTHING_TO_INLINE")
inline fun clearScreen(red: Float, green: Float, blue: Float, alpha: Float = 1f) {
  Gdx.gl.glClearColor(red, green, blue, alpha)
  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
}
