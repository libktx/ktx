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
inline fun Actor.onChange(crossinline listener: () -> Unit): ChangeListener {
  val changeListener = object : ChangeListener() {
    override fun changed(event: ChangeEvent, actor: Actor) = listener()
  }
  this.addListener(changeListener)
  return changeListener
}

/**
 * Attaches a [ChangeListener] to this actor.
 * @param listener invoked each time a [ChangeEvent] is fired for this actor. Consumes the triggered [ChangeEvent] and
 * the [Actor] that the listener was originally attached to.
 * @return [ChangeListener] instance.
 */
inline fun <Widget : Actor> Widget.onChangeEvent(
    crossinline listener: (event: ChangeEvent, actor: Widget) -> Unit): ChangeListener {
  val changeListener = object : ChangeListener() {
    override fun changed(event: ChangeEvent, actor: Actor) = listener(event, this@onChangeEvent)
  }
  this.addListener(changeListener)
  return changeListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked.
 * @return [ClickListener] instance.
 */
inline fun Actor.onClick(crossinline listener: () -> Unit): ClickListener {
  val clickListener = object : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) = listener()
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onClickEvent(
    crossinline listener: (event: InputEvent, actor: Widget) -> Unit): ClickListener {
  val clickListener = object : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) = listener(event, this@onClickEvent)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. The received floats are local X and Y coordinates of the actor.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onClickEvent(
    crossinline listener: (event: InputEvent, actor: Widget, x: Float, y: Float) -> Unit): ClickListener {
  val clickListener = object : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) = listener(event, this@onClickEvent, x, y)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is touched.
 * @return [ClickListener] instance.
 */
inline fun Actor.onTouchDown(crossinline listener: () -> Unit): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      listener()
      return true
    }
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time the touch of the actor is released.
 * @return [ClickListener] instance.
 */
inline fun Actor.onTouchUp(crossinline listener: () -> Unit): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = listener()
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param downListener invoked each time this actor is touched. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchDown] for parameter details.
 * @param upListener invoked each time the touch of the actor is released. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchUp] for parameter details.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onTouchEvent(
  crossinline downListener: (event: InputEvent, actor: Widget) -> Unit,
  crossinline upListener: (event: InputEvent, actor: Widget) -> Unit
): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      downListener(event, this@onTouchEvent)
      return true
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) =
      upListener(event, this@onTouchEvent)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor. Retrieve the [InputEvent.type] to distinguish between [touchDown][InputEvent.Type.touchDown]
 * and [touchUp][InputEvent.Type.touchUp] events.
 * @param listener invoked each time this actor is touched or the touch is released. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchDown] and [ClickListener.touchUp] for parameter details.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onTouchEvent(
  crossinline listener: (event: InputEvent, actor: Widget) -> Unit
): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      listener(event, this@onTouchEvent)
      return true
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) =
      listener(event, this@onTouchEvent)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param downListener invoked each time this actor is touched. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchDown] for parameter details.
 * @param upListener invoked each time the touch of the actor is released. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchUp] for parameter details.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onTouchEvent(
  crossinline downListener: (event: InputEvent, actor: Widget, x: Float, y: Float) -> Unit,
  crossinline upListener: (event: InputEvent, actor: Widget, x: Float, y: Float) -> Unit
): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      downListener(event, this@onTouchEvent, x, y)
      return true
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) =
      upListener(event, this@onTouchEvent, x, y)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor. Retrieve the [InputEvent.type] to distinguish between [touchDown][InputEvent.Type.touchDown]
 * and [touchUp][InputEvent.Type.touchUp] events.
 * @param listener invoked each time this actor is touched or the touch is released. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchDown] and [ClickListener.touchUp] for parameter details.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onTouchEvent(
  crossinline listener: (event: InputEvent, actor: Widget, x: Float, y: Float) -> Unit
): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      listener(event, this@onTouchEvent, x, y)
      return true
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) =
      listener(event, this@onTouchEvent, x, y)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param downListener invoked each time this actor is touched. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchDown] for parameter details.
 * @param upListener invoked each time the touch of the actor is released. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchUp] for parameter details.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onTouchEvent(
  crossinline downListener: (event: InputEvent, actor: Widget, x: Float, y: Float, pointer: Int, button: Int) -> Unit,
  crossinline upListener: (event: InputEvent, actor: Widget, x: Float, y: Float, pointer: Int, button: Int) -> Unit
): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      downListener(event, this@onTouchEvent, x, y, pointer, button)
      return true
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) =
      upListener(event, this@onTouchEvent, x, y, pointer, button)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor. Retrieve the [InputEvent.type] to distinguish between [touchDown][InputEvent.Type.touchDown]
 * and [touchUp][InputEvent.Type.touchUp] events.
 * @param listener invoked each time this actor is touched or the touch is released. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to. Refer to [ClickListener.touchDown] and [ClickListener.touchUp] for parameter details.
 * @return [ClickListener] instance.
 */
inline fun <Widget : Actor> Widget.onTouchEvent(
  crossinline listener: (event: InputEvent, actor: Widget, x: Float, y: Float, pointer: Int, button: Int) -> Unit
): ClickListener {
  val clickListener = object : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
      listener(event, this@onTouchEvent, x, y, pointer, button)
      return true
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) =
      listener(event, this@onTouchEvent, x, y, pointer, button)
  }
  this.addListener(clickListener)
  return clickListener
}

/**
 * Attaches an [EventListener] optimized to listen for key type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is typed. Consumes the typed character.
 * @return [EventListener] instance.
 */
inline fun Actor.onKey(
    catchEvent: Boolean = false,
    crossinline listener: (character: Char) -> Unit): EventListener {
  val keyListener = EventListener { event ->
    if (event is InputEvent && event.type === keyTyped) {
      listener(event.character)
      catchEvent
    } else false
  }
  this.addListener(keyListener)
  return keyListener
}

/**
 * Attaches an [EventListener] optimized to listen for key type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is typed. Consumes the triggered
 * [InputEvent], the [Actor] that the listener was originally attached to and the typed character.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onKeyEvent(
    catchEvent: Boolean = false,
    crossinline listener: (event: InputEvent, actor: Widget, character: Char) -> Unit): EventListener {
  val keyListener = EventListener { event ->
    if (event is InputEvent && event.type === keyTyped) {
      listener(event, this@onKeyEvent, event.character)
      catchEvent
    } else false
  }
  this.addListener(keyListener)
  return keyListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyDown type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is pressed. Consumes the pressed key code.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun Actor.onKeyDown(
    catchEvent: Boolean = false,
    crossinline listener: (keyCode: Int) -> Unit): EventListener {
  val keyDownListener = EventListener { event ->
    if (event is InputEvent && event.type === keyDown) {
      listener(event.keyCode)
      catchEvent
    } else false
  }
  this.addListener(keyDownListener)
  return keyDownListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyDown type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is pressed. Consumes the triggered
 * [InputEvent], the [Actor] that the listener was originally attached to and the pressed key code.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun <Widget : Actor> Widget.onKeyDownEvent(
    catchEvent: Boolean = false,
    crossinline listener: (event: InputEvent, actor: Widget, keyCode: Int) -> Unit): EventListener {
  val keyDownListener = EventListener { event ->
    if (event is InputEvent && event.type === keyDown) {
      listener(event, this@onKeyDownEvent, event.keyCode)
      catchEvent
    } else false
  }
  this.addListener(keyDownListener)
  return keyDownListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyUp type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is released. Consumes the released key code.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun Actor.onKeyUp(
    catchEvent: Boolean = false,
    crossinline listener: (keyCode: Int) -> Unit): EventListener {
  val keyUpListener = EventListener { event ->
    if (event is InputEvent && event.type === keyUp) {
      listener(event.keyCode)
      catchEvent
    } else false
  }
  this.addListener(keyUpListener)
  return keyUpListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyUp type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener invoked each time this actor is keyboard-focused and a key is released. Consumes the triggered
 * [InputEvent], the [Actor] that the listener was originally attached to and the released key code.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun <Widget : Actor> Widget.onKeyUpEvent(
    catchEvent: Boolean = false,
    crossinline listener: (event: InputEvent, actor: Widget, keyCode: Int) -> Unit): EventListener {
  val keyUpListener = EventListener { event ->
    if (event is InputEvent && event.type === keyUp) {
      listener(event, this@onKeyUpEvent, event.keyCode)
      catchEvent
    } else false
  }
  this.addListener(keyUpListener)
  return keyUpListener
}

/**
 * Attaches an [EventListener] optimized to listen for scroll focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener will be invoked each time scroll focus status changes on this actor. Consumes a flag marking whether
 * the actor is currently focused.
 * @return [EventListener] instance.
 */
inline fun Actor.onScrollFocus(
    catchEvent: Boolean = false,
    crossinline listener: (focused: Boolean) -> Unit): EventListener {
  val focusListener = EventListener { event ->
    if (event is FocusEvent && event.type === scroll) {
      listener(event.isFocused)
      catchEvent
    } else false
  }
  this.addListener(focusListener)
  return focusListener
}

/**
 * Attaches an [EventListener] optimized to listen for scroll focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener will be invoked each time scroll focus status changes on this actor. [FocusEvent.isFocused] can be used
 * to determine current status. Consumes the triggered [FocusEvent] and the [Actor] that the listener was originally
 * attached to.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onScrollFocusEvent(
    catchEvent: Boolean = false,
    crossinline listener: (event: FocusEvent, actor: Widget) -> Unit): EventListener {
  val focusListener = EventListener { event ->
    if (event is FocusEvent && event.type === scroll) {
      listener(event, this@onScrollFocusEvent)
      catchEvent
    } else false
  }
  this.addListener(focusListener)
  return focusListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyboard focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener. Defaults to false.
 * @param listener will be invoked each time keyboard focus status changes on this actor. Consumes a flag marking whether
 * the actor is currently focused.
 * @return [EventListener] instance.
 */
inline fun Actor.onKeyboardFocus(
    catchEvent: Boolean = false,
    crossinline listener: (focused: Boolean) -> Unit): EventListener {
  val focusListener = EventListener { event ->
    if (event is FocusEvent && event.type === keyboard) {
      listener(event.isFocused)
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
 * to determine current status. Consumes the triggered [FocusEvent] and the [Actor] that the listener was originally
 * attached to.
 * @return [EventListener] instance.
 */
inline fun <Widget : Actor> Widget.onKeyboardFocusEvent(
    catchEvent: Boolean = false,
    crossinline listener: (event: FocusEvent, actor: Widget) -> Unit): EventListener {
  val focusListener = EventListener { event ->
    if (event is FocusEvent && event.type === keyboard) {
      listener(event, this@onKeyboardFocusEvent)
      catchEvent
    } else false
  }
  this.addListener(focusListener)
  return focusListener
}

/**
 * Extends [com.badlogic.gdx.scenes.scene2d.InputListener] for the sole purpose of getting guarantees on nullability.
 * Provides no-op implementations of all methods, making them optional to implement.
 * @see InputListener
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
