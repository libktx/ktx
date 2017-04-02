package kts.actors

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.keyboard
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.scroll
import ktx.actors.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests events and listeners utilities.
 * @author MJ
 */
@Suppress("UNUSED_PARAMETER") // Unused lambda parameters showcase the listeners API.
class EventsTest {
  @Test
  fun shouldAttachChangeListener() {
    val actor = Actor()
    var changed = false
    val listener = actor.onChange { event, actor -> changed = true }
    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    actor.fire(ChangeEvent())
    assertTrue(changed)
  }

  @Test
  fun shouldAttachClickListener() {
    val actor = Actor()
    val listener = actor.onClick { event, actor -> }
    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(listener is ClickListener)
  }

  @Test
  fun shouldAttachClickListenerWithLocalCoordinates() {
    val actor = Actor()
    val listener = actor.onClick { event, actor, x, y -> }
    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(listener is ClickListener)
  }

  @Test
  fun shouldAttachKeyListener() {
    val actor = Actor()
    var typed: Char? = null
    val listener = actor.onKey { event, actor, key -> typed = key }
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
  fun shouldAttachKeyDownListener() {
    val actor = Actor()
    var pressed: Int? = null
    val listener = actor.onKeyDown { event, actor, keyCode -> pressed = keyCode }
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
  fun shouldAttachKeyUpListener() {
    val actor = Actor()
    var released: Int? = null
    val listener = actor.onKeyUp { event, actor, keyCode -> released = keyCode }
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
  fun shouldScrollFocusListener() {
    val actor = Actor()
    var focused = false
    val listener = actor.onScrollFocus { event, actor -> focused = event.isFocused }
    assertNotNull(listener)
    assertTrue(listener in actor.listeners)

    val event = FocusEvent()
    event.type = scroll
    event.isFocused = true
    actor.fire(event)
    assertTrue(focused)
  }

  @Test
  fun shouldKeyboardFocusListener() {
    val actor = Actor()
    var focused = false
    val listener = actor.onKeyboardFocus { event, actor -> focused = event.isFocused }
    assertNotNull(listener)
    assertTrue(listener in actor.listeners)

    val event = FocusEvent()
    event.type = keyboard
    event.isFocused = true
    actor.fire(event)
    assertTrue(focused)
  }

  @Suppress("unused")
  class `Should extend KtxInputListener with no methods overridden` : KtxInputListener() {
    // Guarantees all KtxInputListener methods are optional to implement.
  }
}
