package ktx.graphics

import com.badlogic.gdx.Application.ApplicationType.Android
import com.badlogic.gdx.Application.ApplicationType.iOS
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Centers this [Camera] in the middle of the given rectangle. [width] and [height]
 * represent the size of the rectangle in world (camera/viewport) units.
 * [x] and [y] are optional offsets in world units that will modify camera position.
 *
 * Note that [Camera.update] should be called in order to update to the new position.
 */
fun Camera.center(
  width: Float = Gdx.graphics.width.toFloat(),
  height: Float = Gdx.graphics.height.toFloat(),
  x: Float = 0f,
  y: Float = 0f,
) {
  position.set(x + width / 2f, y + height / 2f, 0f)
}

/**
 * Immediately moves [Camera] to the selected [target] coordinates.
 * Not suitable for all cases, as it may cause a very rough motion when used every frame.
 * [x] and [y] are optional offsets in world units that will modify camera position.
 *
 * Note that [Camera.update] should be called in order to update to the new position.
 */
fun Camera.moveTo(
  target: Vector2,
  x: Float = 0f,
  y: Float = 0f,
) {
  position.set(target.x + x, target.y + y, 0f)
}

/**
 * Will smoothly move the [Camera] at the selected [target] coordinated.
 * The [Camera] will follow a [target] with a smooth linear interpolation, based on a [lerp] amount.
 * [lerp] should be in range of 0 (slowest) to 1 (fastest).
 * The lower that interpolation amount, the smoother - and thus slower - the following motion.
 *
 * [x] and [y] are optional offsets in world units that will modify the final camera position.
 * Offsets are added as-is and are not interpolated.
 *
 * Note that [Camera.update] should be called in order to update to the new position.
 * [moveTo] can be used to set the initial position while honoring the same [x] and [y] offset.
 */
fun Camera.lerpTo(
  target: Vector2,
  lerp: Float,
  x: Float = 0f,
  y: Float = 0f,
) {
  val newX = this.position.x + ((target.x - position.x) * lerp) + x
  val newY = this.position.y + ((target.y - position.y) * lerp) + y
  position.set(newX, newY, 0f)
}

/**
 * Inlines the [operation], which can update the camera position.
 * Automatically calls [Camera.update] after the [operation] is finished.
 */
@OptIn(ExperimentalContracts::class)
inline fun Camera.update(operation: Camera.() -> Unit) {
  contract { callsInPlace(operation, InvocationKind.EXACTLY_ONCE) }
  operation()
  update()
}

/**
 * Combines [screen][com.badlogic.gdx.utils.viewport.ScreenViewport] and
 * [fit][com.badlogic.gdx.utils.viewport.FitViewport] viewports. Similarly to ScreenViewport, world size is changed on
 * each update (resize), so [update] should be generally called with `true` parameter (camera should be centered).
 * Tries to keep the passed aspect ratio by applying letterboxing - horizontal or vertical black bars.
 *
 * On contrary to regular ScreenViewport, this viewport analyzes screen density (pixel per inch ratio) to preserve
 * correct look on every platform, including mobiles. This is very convenient for GUIs (especially when using fit
 * viewport with the same aspect ratio for game logic rendering), as they will not be scaled when the screen is resized
 * (comparably to using similarly to screen viewport), and yet should still look acceptable on mobile devices
 * (comparably to using fit viewport with a fixed world size).
 *
 * It is advised to pair this viewport with FitViewport - LetterboxingViewport can be used for the GUI, while
 * FitViewport is excellent for the actual (2D) game rendering.

 * @param targetPpiX this is the targeted pixel per inch ratio on X axis, which allows to scale the viewport
 *    correctly on different devices. Usually about 96 for desktop and WebGL platforms, 160 for mobiles.
 *    Make sure to call [updateScale] after changing this variable.
 * @param targetPpiY targeted pixel per inch ratio on Y axis. Usually about 96 for desktop and WebGL platforms, 160
 *    for mobiles. Make sure to call [updateScale] after changing this variable.
 * @param aspectRatio width divided by height. Will preserve this aspect ratio by applying letterboxing.
 */
class LetterboxingViewport(
  var targetPpiX: Float = defaultTargetPpi,
  var targetPpiY: Float = defaultTargetPpi,
  var aspectRatio: Float = 4f / 3f,
) : ScalingViewport(Scaling.fit, 0f, 0f) {
  /** You can directly modify unit per pixel ratio (bypassing PPI check) by modifying this value.
   * @see updateScale */
  var scaleX = 0f

  /** You can directly modify unit per pixel ratio (bypassing PPI check) by modifying this value.
   * @see updateScale */
  var scaleY = 0f

  init {
    updateScale()
    updateWorldSize()
  }

  /**
   * Forces update of current pixel per unit ratio according to screen density.
   * @see com.badlogic.gdx.Graphics.getDensity
   * @see com.badlogic.gdx.Graphics.getPpiX
   * @see com.badlogic.gdx.Graphics.getPpiY
   */
  fun updateScale() {
    scaleX = targetPpiX / Gdx.graphics.ppiX
    scaleY = targetPpiY / Gdx.graphics.ppiY
  }

  override fun update(
    screenWidth: Int,
    screenHeight: Int,
    centerCamera: Boolean,
  ) {
    updateWorldSize(screenWidth, screenHeight)
    super.update(screenWidth, screenHeight, centerCamera)
  }

  /** Forces update of current world size according to window size. Will try to keep the set aspect ratio. */
  fun updateWorldSize() {
    updateWorldSize(Gdx.graphics.width, Gdx.graphics.height)
  }

  /**
   * Forces update of current world size according to window size. Will try to keep the set aspect ratio.
   * @param screenWidth current screen width.
   * @param screenHeight current screen height.
   */
  private fun updateWorldSize(
    screenWidth: Int,
    screenHeight: Int,
  ) {
    val width = screenWidth * scaleX
    val height = screenHeight * scaleY
    val fitHeight = width / aspectRatio
    if (fitHeight > height) {
      setWorldSize(height * aspectRatio, height)
    } else {
      setWorldSize(width, fitHeight)
    }
  }

  private companion object {
    private fun isMobile() = Gdx.app.type == Android || Gdx.app.type == iOS

    private val defaultTargetPpi: Float
      get() = if (isMobile()) 160f else 96f
  }
}
