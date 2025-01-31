package ktx.app

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.Disposable

/**
 * Wrapping interface around [com.badlogic.gdx.ApplicationListener]. Provides no-op implementations of all methods,
 * making them optional to implement.
 *
 * Explicitly extends the [Disposable] interface, matching the [ApplicationListener.dispose] method,
 * which allows to leverage [Disposable] utilities.
 */
interface KtxApplicationAdapter :
  ApplicationListener,
  Disposable {
  override fun resize(
    width: Int,
    height: Int,
  ) = Unit

  override fun create() = Unit

  override fun render() = Unit

  override fun resume() = Unit

  override fun dispose() = Unit

  override fun pause() = Unit
}

/**
 * Wrapping interface around [com.badlogic.gdx.InputProcessor]. Provides empty implementations of all methods,
 * making them optional to implement.
 */
interface KtxInputAdapter : InputProcessor {
  override fun touchUp(
    screenX: Int,
    screenY: Int,
    pointer: Int,
    button: Int,
  ) = false

  override fun keyDown(keycode: Int) = false

  override fun keyTyped(character: Char) = false

  override fun keyUp(keycode: Int) = false

  override fun mouseMoved(
    screenX: Int,
    screenY: Int,
  ) = false

  override fun scrolled(
    amountX: Float,
    amountY: Float,
  ) = false

  override fun touchDown(
    screenX: Int,
    screenY: Int,
    pointer: Int,
    button: Int,
  ) = false

  override fun touchDragged(
    screenX: Int,
    screenY: Int,
    pointer: Int,
  ) = false

  /** Calls [InputProcessor.touchUp] for consistency with libGDX 1.11.0 and older. Override for custom handling. */
  override fun touchCancelled(
    screenX: Int,
    screenY: Int,
    pointer: Int,
    button: Int,
  ) = touchUp(screenX, screenY, pointer, button)
}
