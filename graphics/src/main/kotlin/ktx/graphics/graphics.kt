package ktx.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Automatically calls [Batch.begin] and [Batch.end].
 * @param projectionMatrix A projection matrix to set on the batch before [Batch.begin]. If null, the batch's matrix
 * remains unchanged.
 * @param action inlined. Executed after [Batch.begin] and before [Batch.end].
 */
@OptIn(ExperimentalContracts::class)
inline fun <B : Batch> B.use(
  projectionMatrix: Matrix4? = null,
  action: (B) -> Unit,
) {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  if (projectionMatrix != null) {
    this.projectionMatrix = projectionMatrix
  }
  begin()
  action(this)
  end()
}

/**
 * Automatically calls [Batch.begin] and [Batch.end].
 * @param camera The camera's [Camera.combined] matrix will be set to the batch's projection matrix before [Batch.begin]
 * @param action inlined. Executed after [Batch.begin] and before [Batch.end].
 */
@OptIn(ExperimentalContracts::class)
inline fun <B : Batch> B.use(
  camera: Camera,
  action: (B) -> Unit,
) {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  use(camera.combined, action)
}

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
 * Automatically calls [ShaderProgram.bind].
 * @param action inlined. Executed after [ShaderProgram.bind].
 */
@OptIn(ExperimentalContracts::class)
inline fun <S : ShaderProgram> S.use(action: (S) -> Unit) {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  bind()
  action(this)
}

/**
 * Automatically calls [GLFrameBuffer.begin] and [GLFrameBuffer.end].
 * @param action inlined. Executed after [GLFrameBuffer.begin] and before [GLFrameBuffer.end].
 */
@OptIn(ExperimentalContracts::class)
inline fun <B : GLFrameBuffer<*>> B.use(action: (B) -> Unit) {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  begin()
  action(this)
  end()
}

/**
 * Takes a screenshot of the entire screen and saves the image using the given [fileHandle].
 */
fun takeScreenshot(fileHandle: FileHandle) {
  val bufferWidth = Gdx.graphics.backBufferWidth
  val bufferHeight = Gdx.graphics.backBufferHeight
  val pixels = ScreenUtils.getFrameBufferPixels(0, 0, bufferWidth, bufferHeight, true)

  // Ensuring the screenshot is opaque:
  var i = 4
  val alpha = 255.toByte()
  while (i < pixels.size) {
    pixels[i - 1] = alpha
    i += 4
  }

  val screenshotImage = Pixmap(bufferWidth, bufferHeight, Pixmap.Format.RGBA8888)
  BufferUtils.copy(pixels, 0, screenshotImage.pixels, pixels.size)
  PixmapIO.writePNG(fileHandle, screenshotImage)
  screenshotImage.dispose()
}
