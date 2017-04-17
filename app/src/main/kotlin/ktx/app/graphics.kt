package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram

/**
 * Automatically calls [Batch.begin] and [Batch.end].
 * @param action inlined. Executed after [Batch.begin] and before [Batch.end].
 */
inline fun <B : Batch> B.use(action: (B) -> Unit) {
  begin()
  action(this)
  end()
}

/**
 * Automatically calls [ShaderProgram.begin] and [ShaderProgram.end].
 * @param action inlined. Executed after [ShaderProgram.begin] and before [ShaderProgram.end].
 */
inline fun <S : ShaderProgram> S.use(action: (S) -> Unit) {
  begin()
  action(this)
  end()
}
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
  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
}
