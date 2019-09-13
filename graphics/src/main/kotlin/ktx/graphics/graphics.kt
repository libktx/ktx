package ktx.graphics

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4

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
 * @param projectionMatrix A projection matrix to set on the batch before [Batch.begin]. If null, the batch's matrix
 * remains unchanged.
 * @param action inlined. Executed after [Batch.begin] and before [Batch.end].
 */
inline fun <B : Batch> B.use(projectionMatrix: Matrix4? = null, action: (B) -> Unit) {
  if (projectionMatrix != null)
    this.projectionMatrix = projectionMatrix
  begin()
  action(this)
  end()
}

/**
 * Automatically calls [Batch.begin] and [Batch.end].
 * @param camera The camera's [Camera.combined] matrix will be set to the batch's projection matrix before [Batch.begin]
 * @param action inlined. Executed after [Batch.begin] and before [Batch.end].
 */
inline fun <B : Batch> B.use(camera: Camera, action: (B) -> Unit) = use(camera.combined, action)

/**
 * Automatically calls [Batch.begin] with the provided matrix
 * @param projectionMatrix A projection matrix to set on the batch before [Batch.begin].
 */
fun <B : Batch> B.begin(projectionMatrix: Matrix4) {
  this.projectionMatrix = projectionMatrix
  begin()
}

/**
 * Sets the batch's projection matrix to the camera's [Camera.combined] matrix and calls [Batch.begin].
 * @param camera The camera's [Camera.combined] matrix will be set to the batch's projection matrix before [Batch.begin].
 */
fun <B : Batch> B.begin(camera: Camera) = begin(camera.combined)

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
 * Automatically calls [GLFrameBuffer.begin] and [GLFrameBuffer.end].
 * @param action inlined. Executed after [GLFrameBuffer.begin] and before [GLFrameBuffer.end].
 */
inline fun <B : GLFrameBuffer<*>> B.use(action: (B) -> Unit) {
  begin()
  action(this)
  end()
}
