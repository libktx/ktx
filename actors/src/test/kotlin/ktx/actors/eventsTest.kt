package ktx.actors

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.keyboard
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.scroll
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests events and listeners utilities.
 */
@Suppress("UNUSED_PARAMETER", "UNUSED_ANONYMOUS_PARAMETER") // Unused lambda parameters showcase the listeners API.
class EventsTest {
  @Test
  fun `should attach ChangeListener`() {
    val actor = Actor()
    var changed = false

    val listener = actor.onChange { changed = true }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    actor.fire(ChangeEvent())
    assertTrue(changed)
  }

  @Test
  fun `should attach ChangeListener consuming ChangeEvent`() {
    val actor = Actor()
    var changed = false

    val listener = actor.onChangeEvent { event, widget -> changed = true }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    actor.fire(ChangeEvent())
    assertTrue(changed)
  }

  @Test
  fun `should attach ClickListener`() {
    val actor = Actor()

    val listener = actor.onClick { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming InputEvent`() {
    val actor = Actor()

    val listener = actor.onClickEvent { event, widget -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming InputEvent with local coordinates`() {
    val actor = Actor()

    val listener = actor.onClickEvent { event, widget, x, y -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener for touchDown`() {
    val actor = Actor()

    val listener = actor.onTouchDown { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener for touchUp`() {
    val actor = Actor()

    val listener = actor.onTouchUp { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming InputEvent for touch events`() {
    val actor = Actor()

    val listener = actor.onTouchEvent(
      downListener = { event, widget -> },
      upListener = { event, widget -> }
    )

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming InputEvent for touch events with local coordinates`() {
    val actor = Actor()

    val listener = actor.onTouchEvent(
      downListener = { event, widget, x, y -> },
      upListener = { event, widget, x, y -> }
    )

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming InputEvent for touch events with local coordinates, pointer and button`() {
    val actor = Actor()

    val listener = actor.onTouchEvent(
      downListener = { event, widget, x, y, pointer, button -> },
      upListener = { event, widget, x, y, pointer, button -> }
    )

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener and trigger touchDown event`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, widget -> result = event.type == touchDown }
    listener.touchDown(InputEvent().apply { type = touchDown }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach ClickListener and trigger touchUp event`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, widget -> result = event.type == touchUp }
    listener.touchDown(InputEvent().apply { type = touchUp }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach ClickListener and trigger touchDown event with local coordinates, pointer and button`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, widget, x, y, pointer, button -> result = event.type == touchDown }
    listener.touchDown(InputEvent().apply { type = touchDown }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach ClickListener and trigger touchUp event with local coordinates, pointer and button`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, widget, x, y, pointer, button -> result = event.type == touchUp }
    listener.touchDown(InputEvent().apply { type = touchUp }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach ClickListener and trigger touchDown event with local coordinates`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, widget, x, y -> result = event.type == touchDown }
    listener.touchDown(InputEvent().apply { type = touchDown }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach ClickListener and trigger touchUp event with local coordinates`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, widget, x, y -> result = event.type == touchUp }
    listener.touchDown(InputEvent().apply { type = touchUp }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach key listener`() {
    val actor = Actor()
    var typed: Char? = null

    val listener = actor.onKey { key -> typed = key }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertNull(typed)
    val event = InputEvent()
    event.character = 'a'
    event.type = keyTyped
    actor.fire(event)
    assertEquals('a', typed)
  }

  @Test
  fun `should attach key listener consuming InputEvent`() {
    val actor = Actor()
    var typed: Char? = null

    val listener = actor.onKeyEvent { event, widget, key -> typed = key }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertNull(typed)
    val event = InputEvent()
    event.character = 'a'
    event.type = keyTyped
    actor.fire(event)
    assertEquals('a', typed)
  }

  @Test
  fun `should attach key down listener`() {
    val actor = Actor()
    var pressed: Int? = null

    val listener = actor.onKeyDown { keyCode -> pressed = keyCode }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertNull(pressed)
    val event = InputEvent()
    event.keyCode = Keys.A
    event.type = keyDown
    actor.fire(event)
    assertEquals(Keys.A, pressed)
  }

  @Test
  fun `should attach key down listener consuming InputEvent`() {
    val actor = Actor()
    var pressed: Int? = null

    val listener = actor.onKeyDownEvent { event, widget, keyCode -> pressed = keyCode }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertNull(pressed)
    val event = InputEvent()
    event.keyCode = Keys.A
    event.type = keyDown
    actor.fire(event)
    assertEquals(Keys.A, pressed)
  }

  @Test
  fun `should attach key up listener`() {
    val actor = Actor()
    var released: Int? = null

    val listener = actor.onKeyUp { keyCode -> released = keyCode }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)

    assertNull(released)
    val event = InputEvent()
    event.keyCode = Keys.A
    event.type = keyUp
    actor.fire(event)
    assertEquals(Keys.A, released)
  }

  @Test
  fun `should attach key up listener consuming InputEvent`() {
    val actor = Actor()
    var released: Int? = null

    val listener = actor.onKeyUpEvent { event, widget, keyCode -> released = keyCode }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)

    assertNull(released)
    val event = InputEvent()
    event.keyCode = Keys.A
    event.type = keyUp
    actor.fire(event)
    assertEquals(Keys.A, released)
  }

  @Test
  fun `should attach scroll focus listener`() {
    val actor = Actor()
    var focused = false

    val listener = actor.onScrollFocus { focused = it }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    val event = FocusEvent()
    event.type = scroll
    event.isFocused = true
    actor.fire(event)
    assertTrue(focused)
  }

  @Test
  fun `should attach scroll focus listener consuming FocusEvent`() {
    val actor = Actor()
    var focused = false

    val listener = actor.onScrollFocusEvent { event, widget -> focused = event.isFocused }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    val event = FocusEvent()
    event.type = scroll
    event.isFocused = true
    actor.fire(event)
    assertTrue(focused)
  }

  @Test
  fun `should attach keyboard focus listener`() {
    val actor = Actor()
    var focused = false

    val listener = actor.onKeyboardFocus { focused = it }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    val event = FocusEvent()
    event.type = keyboard
    event.isFocused = true
    actor.fire(event)
    assertTrue(focused)
  }

  @Test
  fun `should attach keyboard focus listener consuming FocusEvent`() {
    val actor = Actor()
    var focused = false

    val listener = actor.onKeyboardFocusEvent { event, widget -> focused = event.isFocused }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    val event = FocusEvent()
    event.type = keyboard
    event.isFocused = true
    actor.fire(event)
    assertTrue(focused)
  }

  @Suppress("unused", "ClassName")
  class `should extend KtxInputListener with no methods overridden` : KtxInputListener() {
    // Guarantees all KtxInputListener methods are optional to implement.
  }
}
