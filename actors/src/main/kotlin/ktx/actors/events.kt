package ktx.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyDown
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyTyped
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyUp
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.keyboard
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.scroll
import com.badlogic.gdx.scenes.scene2d.utils.Selection

/**
 * Attaches a [ChangeListener] to this actor.
 * @param listener invoked each time a [ChangeEvent] is fired for this actor. Consumes the [Actor] as `this`.
 * @return [ChangeListener] instance.
 */
inline fun <T : Actor> T.onChange(crossinline listener: T.() -> Unit): ChangeListener {
  val changeListener =
    object : ChangeListener() {
      override fun changed(
        event: ChangeEvent,
        actor: Actor,
      ) = listener()
    }
  addListener(changeListener)
  return changeListener
}

/**
 * Attaches a [ChangeListener] to this actor.
 * @param listener invoked each time a [ChangeEvent] is fired for this actor. Consumes the triggered [ChangeEvent] and
 * the [Actor] that the listener was originally attached to as `this`.
 * @return [ChangeListener] instance.
 */
inline fun <T : Actor> T.onChangeEvent(crossinline listener: T.(event: ChangeEvent) -> Unit): ChangeListener {
  val changeListener =
    object : ChangeListener() {
      override fun changed(
        event: ChangeEvent,
        actor: Actor,
      ) = listener(event)
    }
  addListener(changeListener)
  return changeListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this [Actor] is clicked. Consumes the [Actor] as `this`.
 * @return [ClickListener] instance.
 */
inline fun <T : Actor> T.onClick(crossinline listener: T.() -> Unit): ClickListener {
  val clickListener =
    object : ClickListener() {
      override fun clicked(
        event: InputEvent,
        x: Float,
        y: Float,
      ) = listener()
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to as `this`.
 * @return [ClickListener] instance.
 */
inline fun <T : Actor> T.onClickEvent(crossinline listener: T.(event: InputEvent) -> Unit): ClickListener {
  val clickListener =
    object : ClickListener() {
      override fun clicked(
        event: InputEvent,
        x: Float,
        y: Float,
      ) = listener(event)
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time this actor is clicked. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to as `this`. The received floats are local X and Y coordinates of the actor.
 * @return [ClickListener] instance.
 */
inline fun <T : Actor> T.onClickEvent(crossinline listener: T.(event: InputEvent, x: Float, y: Float) -> Unit): ClickListener {
  val clickListener =
    object : ClickListener() {
      override fun clicked(
        event: InputEvent,
        x: Float,
        y: Float,
      ) = listener(event, x, y)
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time the mouse cursor or finger touch enters the actor.
 * On desktop this will occur even when no mouse buttons are pressed. Consumes the [Actor] as `this`.
 * @return [ClickListener] instance.
 */
inline fun <T : Actor> T.onEnter(crossinline listener: T.() -> Unit): InputListener {
  val clickListener =
    object : ClickListener() {
      override fun enter(
        event: InputEvent?,
        x: Float,
        y: Float,
        pointer: Int,
        fromActor: Actor?,
      ) {
        super.enter(event, x, y, pointer, fromActor)
        listener()
      }
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches an [ClickListener] to this actor.
 * @param listener invoked each time the mouse cursor or finger touch is moved out of an actor.
 * On desktop this will occur even when no mouse buttons are pressed. Consumes the [Actor] as `this`.
 * @return [ClickListener] instance.
 */
inline fun <T : Actor> T.onExit(crossinline listener: T.() -> Unit): InputListener {
  val clickListener =
    object : ClickListener() {
      override fun exit(
        event: InputEvent?,
        x: Float,
        y: Float,
        pointer: Int,
        toActor: Actor?,
      ) {
        super.exit(event, x, y, pointer, toActor)
        listener()
      }
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time the mouse cursor or finger touch enters the actor.
 * Consumes the triggered [InputEvent] and the [Actor] that the listener was originally attached to as `this`.
 * The received floats are local X and Y coordinates of the actor.
 * @return [ClickListener] instance.
 * @see onEnter
 */
inline fun <T : Actor> T.onEnterEvent(crossinline listener: T.(event: InputEvent, x: Float, y: Float) -> Unit): ClickListener {
  val clickListener =
    object : ClickListener() {
      override fun enter(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        fromActor: Actor?,
      ) {
        super.enter(event, x, y, pointer, fromActor)
        listener(event, x, y)
      }
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ClickListener] to this actor.
 * @param listener invoked each time the mouse cursor or finger touch is moved out of an actor.
 * Consumes the triggered [InputEvent] and the [Actor] that the listener was originally attached to as `this`.
 * The received floats are local X and Y coordinates of the actor.
 * @return [ClickListener] instance.
 * @see onExit
 */
inline fun <T : Actor> T.onExitEvent(crossinline listener: T.(event: InputEvent, x: Float, y: Float) -> Unit): ClickListener {
  val clickListener =
    object : ClickListener() {
      override fun exit(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        toActor: Actor?,
      ) {
        super.exit(event, x, y, pointer, toActor)
        listener(event, x, y)
      }
    }
  addListener(clickListener)
  return clickListener
}

/**
 * Attaches a [ChangeListener] to this [Tree].
 * @param listener invoked each time the node [Selection] is changed. Receives the [Selection] object
 * which can be used to obtain all selected items with [Selection.items] or the latest selected item
 * with [Selection.getLastSelected].
 * @return [ChangeListener] instance.
 */
inline fun <N : Tree.Node<N, *, *>> Tree<N, *>.onSelectionChange(crossinline listener: ((Selection<N>) -> Unit)): ChangeListener {
  val changeListener =
    object : ChangeListener() {
      override fun changed(
        event: ChangeEvent,
        actor: Actor,
      ) {
        listener(this@onSelectionChange.selection)
      }
    }
  addListener(changeListener)
  return changeListener
}

/**
 * Attaches an [InputListener] to this actor.
 * @param listener invoked each time this actor is touched. Consumes the [Actor] as `this`.
 * @return [InputListener] instance.
 */
inline fun <T : Actor> T.onTouchDown(crossinline listener: T.() -> Unit): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        listener()
        return true
      }
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [InputListener] to this actor.
 * @param listener invoked each time the touch of the actor is released. Consumes the [Actor] as `this`.
 * @return [InputListener] instance.
 */
inline fun <T : Actor> T.onTouchUp(crossinline listener: T.() -> Unit): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = listener()
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [InputListener] to this actor.
 * @param onDown invoked each time this actor is touched. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to as `this`.
 * @param onUp invoked each time the touch of the actor is released. Consumes the triggered [InputEvent] and the
 * [Actor] that the listener was originally attached to as `this`.
 * @return [InputListener] instance.
 * @see InputListener.touchDown
 * @see InputListener.touchUp
 */
inline fun <T : Actor> T.onTouchEvent(
  crossinline onDown: T.(event: InputEvent) -> Unit,
  crossinline onUp: T.(event: InputEvent) -> Unit,
): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        onDown(event)
        return true
      }

      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = onUp(event)
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [InputListener] to this actor. Retrieve the [InputEvent.type] to distinguish between
 * [touchDown][InputEvent.Type.touchDown] and [touchUp][InputEvent.Type.touchUp] events.
 * @param listener invoked each time this actor is touched or the touch is released. Consumes the triggered [InputEvent]
 * and the [Actor] that the listener was originally attached to as `this`.
 * @return [InputListener] instance.
 * @see InputListener.touchDown
 * @see InputListener.touchUp
 */
inline fun <T : Actor> T.onTouchEvent(crossinline listener: T.(event: InputEvent) -> Unit): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        listener(event)
        return true
      }

      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = listener(event)
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [InputListener] to this actor.
 * @param onDown invoked each time this actor is touched. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to as `this`. Float parameters are local actor coordinates of the event.
 * @param onUp invoked each time the touch of the actor is released. Consumes the triggered [InputEvent] and the [Actor]
 * that the listener was originally attached to as `this`. Float parameters are local actor coordinates of the event.
 * @return [InputListener] instance.
 * @see InputListener.touchDown
 * @see InputListener.touchUp
 */
inline fun <T : Actor> T.onTouchEvent(
  crossinline onDown: T.(event: InputEvent, x: Float, y: Float) -> Unit,
  crossinline onUp: T.(event: InputEvent, x: Float, y: Float) -> Unit,
): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        onDown(event, x, y)
        return true
      }

      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = onUp(event, x, y)
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [InputListener] to this actor. Retrieve the [InputEvent.type] to distinguish between
 * [touchDown][InputEvent.Type.touchDown] and [touchUp][InputEvent.Type.touchUp] events.
 * @param listener invoked each time this actor is touched or the touch is released. Consumes the triggered [InputEvent]
 * and the [Actor] that the listener was originally attached to as `this`. Float parameters are local actor coordinates
 * of the event.
 * @return [InputListener] instance.
 * @see InputListener.touchDown
 * @see InputListener.touchUp
 */
inline fun <T : Actor> T.onTouchEvent(crossinline listener: T.(event: InputEvent, x: Float, y: Float) -> Unit): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        listener(event, x, y)
        return true
      }

      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = listener(event, x, y)
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [InputListener] to this actor.
 * @param onDown invoked each time this actor is touched. Consumes the triggered [InputEvent] and the [Actor] that
 * the listener was originally attached to as `this`. Refer to [InputListener.touchDown] parameters documentation.
 * @param onUp invoked each time the touch of the actor is released. Consumes the triggered [InputEvent] and the [Actor]
 * that the listener was originally attached to as `this`. Refer to [InputListener.touchUp] parameters documentation.
 * @return [InputListener] instance.
 * @see InputListener.touchDown
 * @see InputListener.touchUp
 */
inline fun <T : Actor> T.onTouchEvent(
  crossinline onDown: T.(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) -> Unit,
  crossinline onUp: T.(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) -> Unit,
): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        onDown(event, x, y, pointer, button)
        return true
      }

      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = onUp(event, x, y, pointer, button)
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches a [InputListener] to this actor. Retrieve the [InputEvent.type] to distinguish between
 * [touchDown][InputEvent.Type.touchDown] and [touchUp][InputEvent.Type.touchUp] events.
 * @param listener invoked each time this actor is touched or the touch is released. Consumes the triggered [InputEvent]
 * and the [Actor] that the listener was originally attached to as `this`. Refer to [InputListener.touchDown] and
 * [InputListener.touchUp] for parameter details.
 * @return [InputListener] instance.
 * @see InputListener.touchDown
 * @see InputListener.touchUp
 */
inline fun <T : Actor> T.onTouchEvent(
  crossinline listener: T.(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) -> Unit,
): InputListener {
  val inputListener =
    object : KtxInputListener() {
      override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ): Boolean {
        listener(event, x, y, pointer, button)
        return true
      }

      override fun touchUp(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int,
      ) = listener(event, x, y, pointer, button)
    }
  addListener(inputListener)
  return inputListener
}

/**
 * Attaches an [EventListener] optimized to listen for `keyTyped` type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener invoked each time this actor is keyboard-focused and a key is typed. Consumes the typed character
 * and the [Actor] as `this`.
 * @return [EventListener] instance.
 */
inline fun <T : Actor> T.onKey(
  catchEvent: Boolean = false,
  crossinline listener: T.(character: Char) -> Unit,
): EventListener {
  val keyListener =
    EventListener { event ->
      if (event is InputEvent && event.type === keyTyped) {
        listener(event.character)
        catchEvent
      } else {
        false
      }
    }
  addListener(keyListener)
  return keyListener
}

/**
 * Attaches an [EventListener] optimized to listen for `keyTyped` type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener invoked each time this actor is keyboard-focused and a key is typed. Consumes the triggered
 * [InputEvent], the typed character, and the [Actor] that the listener was originally attached to as `this`.
 * @return [EventListener] instance.
 */
inline fun <T : Actor> T.onKeyEvent(
  catchEvent: Boolean = false,
  crossinline listener: T.(event: InputEvent, character: Char) -> Unit,
): EventListener {
  val keyListener =
    EventListener { event ->
      if (event is InputEvent && event.type === keyTyped) {
        listener(event, event.character)
        catchEvent
      } else {
        false
      }
    }
  addListener(keyListener)
  return keyListener
}

/**
 * Attaches an [EventListener] optimized to listen for `keyDown` type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener invoked each time this actor is keyboard-focused and a key is pressed. Consumes the pressed key code
 * and the [Actor] as `this`.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun <T : Actor> T.onKeyDown(
  catchEvent: Boolean = false,
  crossinline listener: T.(keyCode: Int) -> Unit,
): EventListener {
  val keyDownListener =
    EventListener { event ->
      if (event is InputEvent && event.type === keyDown) {
        listener(event.keyCode)
        catchEvent
      } else {
        false
      }
    }
  addListener(keyDownListener)
  return keyDownListener
}

/**
 * Attaches an [EventListener] optimized to listen for `keyDown` type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener invoked each time this actor is keyboard-focused and a key is pressed. Consumes the triggered
 * [InputEvent], the pressed key code, and the [Actor] that the listener was originally attached to as `this`.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun <T : Actor> T.onKeyDownEvent(
  catchEvent: Boolean = false,
  crossinline listener: T.(event: InputEvent, keyCode: Int) -> Unit,
): EventListener {
  val keyDownListener =
    EventListener { event ->
      if (event is InputEvent && event.type === keyDown) {
        listener(event, event.keyCode)
        catchEvent
      } else {
        false
      }
    }
  addListener(keyDownListener)
  return keyDownListener
}

/**
 * Attaches an [EventListener] optimized to listen for `keyUp` type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener invoked each time this actor is keyboard-focused and a key is released.Consumes the released key code
 * and the [Actor] as `this`.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun <T : Actor> T.onKeyUp(
  catchEvent: Boolean = false,
  crossinline listener: T.(keyCode: Int) -> Unit,
): EventListener {
  val keyUpListener =
    EventListener { event ->
      if (event is InputEvent && event.type === keyUp) {
        listener(event.keyCode)
        catchEvent
      } else {
        false
      }
    }
  addListener(keyUpListener)
  return keyUpListener
}

/**
 * Attaches an [EventListener] optimized to listen for `keyUp` type events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener invoked each time this actor is keyboard-focused and a key is released. Consumes the triggered
 * [InputEvent], the released key code, and the [Actor] that the listener was originally attached to as `this`.
 * @return [EventListener] instance.
 * @see com.badlogic.gdx.Input.Keys
 */
inline fun <T : Actor> T.onKeyUpEvent(
  catchEvent: Boolean = false,
  crossinline listener: T.(event: InputEvent, keyCode: Int) -> Unit,
): EventListener {
  val keyUpListener =
    EventListener { event ->
      if (event is InputEvent && event.type === keyUp) {
        listener(event, event.keyCode)
        catchEvent
      } else {
        false
      }
    }
  addListener(keyUpListener)
  return keyUpListener
}

/**
 * Attaches an [EventListener] optimized to listen for scroll focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener will be invoked each time scroll focus status changes on this actor. Consumes a flag marking whether
 * the actor is currently focused and the [Actor] as `this`.
 * @return [EventListener] instance.
 */
inline fun <T : Actor> T.onScrollFocus(
  catchEvent: Boolean = false,
  crossinline listener: T.(focused: Boolean) -> Unit,
): EventListener {
  val focusListener =
    EventListener { event ->
      if (event is FocusEvent && event.type === scroll) {
        listener(event.isFocused)
        catchEvent
      } else {
        false
      }
    }
  addListener(focusListener)
  return focusListener
}

/**
 * Attaches an [EventListener] optimized to listen for scroll focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener will be invoked each time scroll focus status changes on this actor. [FocusEvent.isFocused] can be
 * used to the determine current status. Consumes the triggered [FocusEvent] and the [Actor] that the listener was
 * originally attached to as `this`.
 * @return [EventListener] instance.
 */
inline fun <T : Actor> T.onScrollFocusEvent(
  catchEvent: Boolean = false,
  crossinline listener: T.(event: FocusEvent) -> Unit,
): EventListener {
  val focusListener =
    EventListener { event ->
      if (event is FocusEvent && event.type === scroll) {
        listener(event)
        catchEvent
      } else {
        false
      }
    }
  addListener(focusListener)
  return focusListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyboard focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener will be invoked each time keyboard focus status changes on this actor. Consumes a flag marking
 * whether the actor is currently focused and the [Actor] as `this`.
 * @return [EventListener] instance.
 */
inline fun <T : Actor> T.onKeyboardFocus(
  catchEvent: Boolean = false,
  crossinline listener: T.(focused: Boolean) -> Unit,
): EventListener {
  val focusListener =
    EventListener { event ->
      if (event is FocusEvent && event.type === keyboard) {
        listener(event.isFocused)
        catchEvent
      } else {
        false
      }
    }
  addListener(focusListener)
  return focusListener
}

/**
 * Attaches an [EventListener] optimized to listen for keyboard focus events fired for this actor.
 * @param catchEvent if true, the event will not be passed further after it is handled by this listener.
 * Defaults to `false`.
 * @param listener will be invoked each time keyboard focus status changes on this actor. [FocusEvent.isFocused] can be
 * used to determine the current status. Consumes the triggered [FocusEvent] and the [Actor] that the listener was
 * originally attached to as `this`.
 * @return [EventListener] instance.
 */
inline fun <T : Actor> T.onKeyboardFocusEvent(
  catchEvent: Boolean = false,
  crossinline listener: T.(event: FocusEvent) -> Unit,
): EventListener {
  val focusListener =
    EventListener { event ->
      if (event is FocusEvent && event.type === keyboard) {
        listener(event)
        catchEvent
      } else {
        false
      }
    }
  addListener(focusListener)
  return focusListener
}

/**
 * Extends [com.badlogic.gdx.scenes.scene2d.InputListener] for the sole purpose of getting guarantees on nullability.
 * Provides no-op implementations of all methods, making them optional to implement.
 * @see InputListener
 */
open class KtxInputListener : InputListener() {
  override fun enter(
    event: InputEvent,
    x: Float,
    y: Float,
    pointer: Int,
    fromActor: Actor?,
  ) = Unit

  override fun exit(
    event: InputEvent,
    x: Float,
    y: Float,
    pointer: Int,
    toActor: Actor?,
  ) = Unit

  override fun keyDown(
    event: InputEvent,
    keycode: Int,
  ) = false

  override fun keyTyped(
    event: InputEvent,
    character: Char,
  ) = false

  override fun keyUp(
    event: InputEvent,
    keycode: Int,
  ) = false

  override fun mouseMoved(
    event: InputEvent,
    x: Float,
    y: Float,
  ) = false

  override fun scrolled(
    event: InputEvent,
    x: Float,
    y: Float,
    amountX: Float,
    amountY: Float,
  ) = false

  override fun touchDown(
    event: InputEvent,
    x: Float,
    y: Float,
    pointer: Int,
    button: Int,
  ) = false

  override fun touchDragged(
    event: InputEvent,
    x: Float,
    y: Float,
    pointer: Int,
  ) = Unit

  override fun touchUp(
    event: InputEvent,
    x: Float,
    y: Float,
    pointer: Int,
    button: Int,
  ) = Unit
}
