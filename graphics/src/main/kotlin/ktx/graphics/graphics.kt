package ktx.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram

/**
 * Factory methods for LibGDX [Color] class. Allows to use named parameters.
 * @param red red color value.
 * @param green green color value.
 * @param blue blue color value.
 * @param alpha color alpha value. Optional, defaults to 1f (non-transparent).
 * @return a new [Color] instance.
 */
fun color(red: Float, green: Float, blue: Float, alpha: Float = 1f) = Color(red, green, blue, alpha)

/**
 * Allows to copy this [Color] instance, optionally changing chosen properties.
 * @param red red color value. If null, will be copied from [Color.r]. Defaults to null.
 * @param green green color value. If null, will be copied from [Color.g]. Defaults to null.
 * @param blue blue color value. If null, will be copied from [Color.b]. Defaults to null.
 * @param alpha color alpha value. If null, will be copied from [Color.a]. Defaults to null.
 * @return a new [Color] instance with values copied from this color and optionally overridden by the parameters. */
fun Color.copy(red: Float? = null, green: Float? = null, blue: Float? = null, alpha: Float? = null) =
    Color(red ?: r, green ?: g, blue ?: b, alpha ?: a)

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
