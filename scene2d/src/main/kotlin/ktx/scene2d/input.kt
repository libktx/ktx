package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

/**
 * Extends [com.badlogic.gdx.scenes.scene2d.InputListener] for the sole purpose of getting
 * guarantees on nullability as well as override optionality.
 * Methods not implemented have a default implementation.
 */
open class KtxInputListener : InputListener() {
  override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) = Unit
  override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) = Unit
  override fun keyDown(event: InputEvent, keycode: Int) = false
  override fun keyTyped(event: InputEvent, character: Char) = false
  override fun keyUp(event: InputEvent, keycode: Int) = false
  override fun mouseMoved(event: InputEvent, x: Float, y: Float) = false
  override fun scrolled(event: InputEvent, x: Float, y: Float, amount: Int) = false
  override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = false
  override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) = Unit
  override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = Unit
}
