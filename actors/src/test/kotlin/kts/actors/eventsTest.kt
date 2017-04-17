package kts.actors

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
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
 */
@Suppress("UNUSED_PARAMETER") // Unused lambda parameters showcase the listeners API.
class EventsTest {
  @Test
  fun `should attach ChangeListener`() {
    val actor = Actor()
    var changed = false

    val listener = actor.onChange { event, actor -> changed = true }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(listener is ChangeListener)
    actor.fire(ChangeEvent())
    assertTrue(changed)
  }

  @Test
  fun `should attach ClickListener`() {
    val actor = Actor()

    val listener = actor.onClick { event, actor -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(listener is ClickListener)
  }

  @Test
  fun `should attach ClickListener with local coordinates`() {
    val actor = Actor()

    val listener = actor.onClick { event, actor, x, y -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(listener is ClickListener)
  }

  @Test
  fun `should attach key listener`() {
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
  fun `should attach key down listener`() {
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
  fun `should attach key up listener`() {
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
  fun `should scroll focus listener`() {
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
  fun `should keyboard focus listener`() {
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
  class `should extend KtxInputListener with no methods overridden` : KtxInputListener() {
    // Guarantees all KtxInputListener methods are optional to implement.
  }
}
