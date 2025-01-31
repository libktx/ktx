package ktx.actors

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyDown
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyTyped
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.keyUp
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.touchDown
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.touchUp
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.keyboard
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent.Type.scroll
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

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

    val listener = actor.onChangeEvent { event -> changed = true }

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

    val listener = actor.onClickEvent { event -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming InputEvent with local coordinates`() {
    val actor = Actor()

    val listener = actor.onClickEvent { event, x, y -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener triggered on enter events`() {
    val actor = Actor()

    val listener = actor.onEnter { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach InputListener triggered on exit events`() {
    val actor = Actor()

    val listener = actor.onExit { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming onEnter events with local coordinates`() {
    val actor = Actor()

    val listener = actor.onEnterEvent { event, x, y -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach ClickListener consuming onExit events with local coordinates`() {
    val actor = Actor()

    val listener = actor.onExitEvent { event, x, y -> }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  class SampleNode : Tree.Node<SampleNode, Any, Actor>(mock())

  @Test
  fun `should attach ChangeListener to a Tree consuming Selection with the selected Nodes`() {
    val style = mock<Tree.TreeStyle>()
    style.plus = mock()
    style.minus = mock()
    val tree = Tree<SampleNode, Any>(style)

    val listener = tree.onSelectionChange {}

    assertNotNull(listener)
    assertTrue(listener in tree.listeners)
  }

  @Test
  fun `should invoke attached ChangeListener when tree Selection changes`() {
    val style = mock<Tree.TreeStyle>()
    style.plus = mock()
    style.minus = mock()
    val tree = Tree<SampleNode, Any>(style)
    tree.selection.setProgrammaticChangeEvents(true)
    val nodes = Array(3) { SampleNode() }
    val selected = SampleNode()
    nodes.forEach(tree::add)
    tree.add(selected)
    val executions = AtomicInteger()
    val selectedNodes = mutableListOf<SampleNode>()
    tree.selection.clear()
    tree.onSelectionChange { selection ->
      executions.incrementAndGet()
      selectedNodes.clear()
      selectedNodes.addAll(selection.items())
    }

    tree.selection.add(selected)

    assertEquals(1, executions.get())
    assertEquals(1, selectedNodes.size)
    assertSame(selected, selectedNodes.first())
  }

  @Test
  fun `should attach InputListener for touchDown`() {
    val actor = Actor()

    val listener = actor.onTouchDown { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach InputListener for touchUp`() {
    val actor = Actor()

    val listener = actor.onTouchUp { }

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach InputListener consuming InputEvent for touch events`() {
    val actor = Actor()

    val listener =
      actor.onTouchEvent(
        onDown = { event -> },
        onUp = { event -> },
      )

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach InputListener consuming InputEvent for touch events with local coordinates`() {
    val actor = Actor()

    val listener =
      actor.onTouchEvent(
        onDown = { event, x, y -> },
        onUp = { event, x, y -> },
      )

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach InputListener consuming InputEvent for touch events with local coordinates, pointer and button`() {
    val actor = Actor()

    val listener =
      actor.onTouchEvent(
        onDown = { event, x, y, pointer, button -> },
        onUp = { event, x, y, pointer, button -> },
      )

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
  }

  @Test
  fun `should attach InputListener and trigger touchDown event`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event -> result = event.type == touchDown }
    listener.touchDown(InputEvent().apply { type = touchDown }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach InputListener and trigger touchUp event`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event -> result = event.type == touchUp }
    listener.touchDown(InputEvent().apply { type = touchUp }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach InputListener and trigger touchDown event with local coordinates, pointer and button`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, x, y, pointer, button -> result = event.type == touchDown }
    listener.touchDown(InputEvent().apply { type = touchDown }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach InputListener and trigger touchUp event with local coordinates, pointer and button`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, x, y, pointer, button -> result = event.type == touchUp }
    listener.touchDown(InputEvent().apply { type = touchUp }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach InputListener and trigger touchDown event with local coordinates`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, x, y -> result = event.type == touchDown }
    listener.touchDown(InputEvent().apply { type = touchDown }, 0f, 0f, 0, 0)

    assertNotNull(listener)
    assertTrue(listener in actor.listeners)
    assertTrue(result)
  }

  @Test
  fun `should attach InputListener and trigger touchUp event with local coordinates`() {
    val actor = Actor()
    var result = false

    val listener = actor.onTouchEvent { event, x, y -> result = event.type == touchUp }
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

    val listener = actor.onKeyEvent { event, key -> typed = key }

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

    val listener = actor.onKeyDownEvent { event, keyCode -> pressed = keyCode }

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

    val listener = actor.onKeyUpEvent { event, keyCode -> released = keyCode }

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

    val listener = actor.onScrollFocusEvent { event -> focused = event.isFocused }

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

    val listener = actor.onKeyboardFocusEvent { event -> focused = event.isFocused }

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
