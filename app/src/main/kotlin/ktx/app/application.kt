package ktx.app

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20

/**
 * Abstract implementation of [ApplicationListener] similar in scope to [com.badlogic.gdx.ApplicationAdapter]. Provides
 * empty implementations of most optional methods. Handles screen clearing (with black color) and rendering on a fixed
 * step value.
 * @param fixedTimeStep minimum time (in seconds) between two consequent render calls. Might not match the delta time
 *    reported by [Gdx.graphics]. Defaults to 1/60.
 * @param maxDeltaTime maximum time (in seconds) stored by the application listener for fixed time step calculations.
 *    Defaults to 1 second.
 */
abstract class KotlinApplication(protected val fixedTimeStep: Float = 1f / 60f,
                                 protected val maxDeltaTime: Float = 1f) : ApplicationListener {
  private var timeSinceLastRender = 0f

  override fun resize(width: Int, height: Int) {
  }

  override final fun render() {
    timeSinceLastRender = Math.min(timeSinceLastRender + Gdx.graphics.rawDeltaTime, maxDeltaTime)
    while (timeSinceLastRender >= fixedTimeStep) {
      timeSinceLastRender -= fixedTimeStep
      clearScreen(0f, 0f, 0f)
      render(fixedTimeStep)
    }
  }

  /**
   * Called by [render] function when the raw delta time reported by current [Gdx.graphics] reaches the chosen
   * [fixedTimeStep] value. Ensures smooth rendering on fixed time step value.
   * @param delta estimate of time since last method call.
   */
  abstract fun render(delta: Float)

  override fun pause() {
  }

  override fun resume() {
  }

  override fun dispose() {
  }
}

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

/**
 * Clears current screen with the selected color. Inlined to lower the total method count. Assumes alpha is 1f.
 * @param red red color value.
 * @param green green color value.
 * @param blue blue color value.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun clearScreen(red: Float, green: Float, blue: Float) {
  Gdx.gl.glClearColor(red, green, blue, 1f)
  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
}
