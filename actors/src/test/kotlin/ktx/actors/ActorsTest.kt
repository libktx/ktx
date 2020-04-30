package ktx.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests general [Actor] utilities.
 */
class ActorsTest {
  private val floatTolerance = 0.00001f

  @Test
  fun `should report if actor on stage is shown`() {
    val stage = getMockStage()
    val actor = Actor()

    stage.addActor(actor)

    assertNotNull(actor.stage)
    assertTrue(actor.isShown())
  }

  @Test
  fun `should report if actor without stage is shown`() {
    val actor = Actor()

    assertFalse(actor.isShown())
  }

  @Test
  fun `should report if null actor is shown`() {
    val actor: Actor? = null

    assertFalse(actor.isShown())
  }

  @Test
  fun `should set position with ints`() {
    val actor = Actor()

    actor.setPosition(10, 20)

    assertEquals(10f, actor.x, floatTolerance)
    assertEquals(20f, actor.y, floatTolerance)
  }

  @Test
  fun `should center actor with normalization`() {
    val actor = Actor()
    actor.setSize(100f, 100f)
    val newSize = 201f

    actor.centerPosition(width = newSize, height = newSize, normalize = true)

    assertEquals(50f, actor.x, floatTolerance)
    assertEquals(50f, actor.y, floatTolerance)
  }

  @Test
  fun `should center actor without normalization`() {
    val actor = Actor()
    actor.setSize(100f, 100f)
    val newSize = 201f

    actor.centerPosition(width = newSize, height = newSize, normalize = false)

    assertEquals(50.5f, actor.x, floatTolerance)
    assertEquals(50.5f, actor.y, floatTolerance)
  }

  @Test
  fun `should check if actor is in group with in operator`() {
    val actor = Actor()
    val group = Group()

    assertFalse(actor in group)
    group.addActor(actor)
    assertTrue(actor in group)
    group.removeActor(actor)
    assertFalse(actor in group)
  }

  @Test
  fun `should add actors to group with +=`() {
    val actor = Actor()
    val group = Group()

    group += actor

    assertNotNull(actor.parent)
    assertTrue(actor in group)
    assertTrue(actor in group.children)
  }

  @Test
  fun `should remove actors from group with -=`() {
    val actor = Actor()
    val group = Group()
    group.addActor(actor)

    group -= actor

    assertNull(actor.parent)
    assertFalse(actor in group)
    assertFalse(actor in group.children)
  }

  @Test
  fun `should check if actor is on stage with in operator`() {
    val actor = Actor()
    val stage = getMockStage()

    assertFalse(actor in stage)
    stage.addActor(actor)
    assertTrue(actor in stage)
    actor.remove()
    assertFalse(actor in stage)
  }

  @Test
  fun `should add actors to stage with +=`() {
    val actor = Actor()
    val stage = getMockStage()

    stage += actor

    assertNotNull(actor.parent)
    assertTrue(actor in stage)
    assertTrue(actor in stage.root)
    assertTrue(stage.root.hasChildren())
    assertSame(stage.root, actor.parent)
  }

  @Test
  fun `should remove actors from stage with -=`() {
    val actor = Actor()
    val stage = getMockStage()
    stage.addActor(actor)

    stage -= actor

    assertNull(actor.parent)
    assertFalse(actor in stage)
    assertFalse(actor in stage.root)
    assertFalse(stage.root.hasChildren())
  }

  @Test
  fun `should modify actor's color alpha value`() {
    val actor = Actor()
    actor.color = Color(1f, 1f, 1f, 1f)
    val color = actor.color

    actor.alpha = 0.5f

    assertEquals(0.5f, color.a, floatTolerance)
    assertEquals(0.5f, actor.alpha, floatTolerance)
  }

  @Test
  fun `should modify stage root actor's color alpha value`() {
    val stage = getMockStage()
    stage.root.color = Color(1f, 1f, 1f, 1f)
    val color = stage.root.color

    stage.alpha = 0.5f

    assertEquals(0.5f, color.a, floatTolerance)
    assertEquals(0.5f, stage.alpha, floatTolerance)
  }

  @Test
  fun `should report valid alpha constants`() {
    assertEquals(0f, MIN_ALPHA)
    assertEquals(1f, MAX_ALPHA)
  }

  @Test
  fun `should change keyboard focus`() {
    val stage = getMockStage()
    val actor = Actor()
    stage.addActor(actor)

    actor.setKeyboardFocus()
    assertSame(stage.keyboardFocus, actor)

    actor.setKeyboardFocus(false)
    assertNotEquals(stage.keyboardFocus, actor)
    assertNull(stage.keyboardFocus)

    val newFocus = Actor()
    stage.keyboardFocus = newFocus
    actor.setKeyboardFocus(false) // Should not clear or change stage's focused actor if not currently focused.
    assertNotNull(stage.keyboardFocus)
    assertSame(newFocus, stage.keyboardFocus)
  }

  @Test
  fun `should change scroll focus`() {
    val stage = getMockStage()
    val actor = Actor()
    stage.addActor(actor)

    actor.setScrollFocus()
    assertEquals(stage.scrollFocus, actor)

    actor.setScrollFocus(false)
    assertNotEquals(stage.scrollFocus, actor)
    assertNull(stage.scrollFocus)

    val newFocus = Actor()
    stage.scrollFocus = newFocus
    actor.setScrollFocus(false) // Should not clear or change stage's focused actor if not currently focused.
    assertNotNull(stage.scrollFocus)
    assertEquals(newFocus, stage.scrollFocus)
  }

  @Test
  fun `should keep actor within parent's bounds`() {
    val parent = Group()
    val actor = Actor()
    actor.setSize(100f, 100f)
    actor.setPosition(4000f, 4000f)
    parent.setSize(800f, 600f)

    parent.addActor(actor)
    assertTrue(actor.x + actor.width > parent.width)
    assertTrue(actor.y + actor.height > parent.height)

    actor.keepWithinParent()
    assertFalse(actor.x + actor.width > parent.width)
    assertFalse(actor.y + actor.height > parent.height)
    assertFalse(actor.x < 0f)
    assertFalse(actor.y < 0f)
    assertEquals(parent.width, actor.x + actor.width, floatTolerance)
    assertEquals(parent.height, actor.y + actor.height, floatTolerance)

    actor.setPosition(-4000f, -4000f)
    actor.keepWithinParent()
    assertFalse(actor.x + actor.width > parent.width)
    assertFalse(actor.y + actor.height > parent.height)
    assertFalse(actor.x < 0f)
    assertFalse(actor.y < 0f)
    assertEquals(0f, actor.x, floatTolerance)
    assertEquals(0f, actor.y, floatTolerance)
  }

  @Test
  fun `should keep actor within stage bounds`() {
    val stage = getMockStage()
    val actor = Actor()
    actor.setSize(100f, 100f)
    actor.setPosition(4000f, 4000f)

    stage.addActor(actor)
    assertTrue(actor.x + actor.width > stage.width)
    assertTrue(actor.y + actor.height > stage.height)

    actor.keepWithinParent()
    assertFalse(actor.x + actor.width > stage.width)
    assertFalse(actor.y + actor.height > stage.height)
    assertFalse(actor.x < 0f)
    assertFalse(actor.y < 0f)
    assertEquals(stage.width, actor.x + actor.width, floatTolerance)
    assertEquals(stage.height, actor.y + actor.height, floatTolerance)

    actor.setPosition(-4000f, -4000f)
    actor.keepWithinParent()
    assertFalse(actor.x + actor.width > stage.width)
    assertFalse(actor.y + actor.height > stage.height)
    assertFalse(actor.x < 0f)
    assertFalse(actor.y < 0f)
    assertEquals(0f, actor.x, floatTolerance)
    assertEquals(0f, actor.y, floatTolerance)
  }
}
