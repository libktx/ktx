package ktx.actors

import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.keyboard
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.scroll

/**
 * Attaches a [ChangeListener] to this actor.
 * @param listener invoked each time a [ChangeEvent] is fired for this actor.
 * @return [ChangeListener] instance.
 */
inline fun <Widget : Actor> Widget.onChange(crossinline listener: (event: ChangeEvent, actor: Widget) -> Unit): ChangeListener {
  val changeListener = object : ChangeListener() {
    override fun changed(event: ChangeEvent, actor: Actor) = listener(event, this@onChange)
  }
  this.addListener(changeListener)
  return changeListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked. The received floats are local x and y coordinates of the actor.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onClick(crossinline listener: (event: InputEvent, actor: Widget, x: Float, y: Float) -> Unit)
    : ClickListener {
  val clickListener = object : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) = listener(event, this@onClick, x, y)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onClick(crossinline listener: (event: InputEvent, actor: Widget) -> Unit): ClickListener {
  val clickListener = object : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) = listener(event, this@onClick)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches an [EventListener] optimized to listen for key type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is typed.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onKey(catchEvent: Boolean = false,
                                         crossinline listener: (event: InputEvent, actor: Widget, character: Char) -> Unit)
    : EventListener {
  val keyListener = EventListener { event ->
    if (event is InputEvent && event.type === keyTyped) {
      listener(event, this@onKey, event.character)
      catchEvent
    } else false
  }
  this.addListener(keyListener)
  return keyListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyDown type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is pressed.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onKeyDown(catchEvent: Boolean = false,
                                             crossinline listener: (event: InputEvent, actor: Widget, keyCode: Int) -> Unit)
    : EventListener {
  val keyDownListener = EventListener { event ->
    if (event is InputEvent && event.type === keyDown) {
      listener(event, this@onKeyDown, event.keyCode)
      catchEvent
    } else false
  }
  this.addListener(keyDownListener)
  return keyDownListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyUp type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is released.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onKeyUp(catchEvent: Boolean = false,
                                           crossinline listener: (event: InputEvent, actor: Widget, keyCode: Int) -> Unit)
    : EventListener {
  val keyUpListener = EventListener { event ->
    if (event is InputEvent && event.type === keyUp) {
      listener(event, this@onKeyUp, event.keyCode)
      catchEvent
    } else false
  }
  this.addListener(keyUpListener)
  return keyUpListener
}

/**
 * Attaches an [EventListener] optimized to listen for scroll focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener will be invoked each time scroll focus status changes on this actor. [FocusEvent.isFocused] can be used
 *    to determine current status.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onScrollFocus(catchEvent: Boolean = false,
                                                 crossinline listener: (event: FocusEvent, actor: Widget) -> Unit)
    : EventListener {
  val focusListener = EventListener { event ->
    if (event is FocusEvent && event.type === scroll) {
      listener(event, this@onScrollFocus)
      catchEvent
    } else false
  }
  this.addListener(focusListener)
  return focusListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyboard focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener will be invoked each time keyboard focus status changes on this actor. [FocusEvent.isFocused] can be used
 *    to determine current status.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onKeyboardFocus(catchEvent: Boolean = false,
                                                   crossinline listener: (event: FocusEvent, actor: Widget) -> Unit)
    : EventListener {
  val focusListener = EventListener { event ->
    if (event is FocusEvent && event.type === keyboard) {
      listener(event, this@onKeyboardFocus)
      catchEvent
    } else false
  }
  this.addListener(focusListener)
  return focusListener
}

/**
 * Extends [com.badlogic.gdx.scenes.scene2d.InputListener] for the sole purpose of getting guarantees on nullability.
 * Provides no-op implementations of all methods, making them optional to implement.
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
