package kts.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyTyped
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
    // Input events require too much mocks to work. It is assumed ClickListener is implemented properly.
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
}
