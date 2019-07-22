package ktx.app

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.InputProcessor

/**
 * Wrapping interface around [com.badlogic.gdx.ApplicationListener]. Provides no-op implementations of all methods,
 * making them optional to implement.
 */
interface KtxApplicationAdapter : ApplicationListener {
  override fun resize(width: Int, height: Int) = Unit
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
  override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
  override fun keyDown(keycode: Int) = false
  override fun keyTyped(character: Char) = false
  override fun keyUp(keycode: Int) = false
  override fun mouseMoved(screenX: Int, screenY: Int) = false
  override fun scrolled(amount: Int) = false
  override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
  override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false
}
