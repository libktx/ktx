package kts.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import ktx.actors.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests general [Actor] utilities.
 * @author MJ
 */
class ActorsTest {
  private val floatTolerance = 0.00001f

  @Test
  fun shouldReportIfActorIsShown() {
    val stage = getMockStage()
    val actor = Actor()
    stage.addActor(actor)
    assertNotNull(actor.stage)
    assertTrue(actor.isShown())

    val notAddedActor = Actor()
    assertFalse(notAddedActor.isShown())

    val nullActor: Actor? = null
    assertFalse(nullActor.isShown())
  }

  @Test
  fun shouldSetPositionWithInts() {
    val actor = Actor()
    actor.setPosition(10, 20)
    assertEquals(10f, actor.x, floatTolerance)
    assertEquals(20f, actor.y, floatTolerance)
  }

  @Test
  fun shouldCenterActor() {
    val size = 201f
    val actor = Actor()
    actor.setSize(100f, 100f)

    actor.centerPosition(width = size, height = size, normalize = true)
    assertEquals(50f, actor.x, floatTolerance)
    assertEquals(50f, actor.y, floatTolerance)

    actor.centerPosition(width = size, height = size, normalize = false)
    assertEquals(50.5f, actor.x, floatTolerance)
    assertEquals(50.5f, actor.y, floatTolerance)
  }

  @Test
  fun shouldCheckIfActorIsInGroupWithInOperator() {
    val actor = Actor()
    val group = Group()
    assertFalse(actor in group)
    group.addActor(actor)
    assertTrue(actor in group)
    group.removeActor(actor)
    assertFalse(actor in group)
  }

  @Test
  fun shouldAddActorsToGroupWithPlus() {
    val actor = Actor()
    val group = Group()
    assertNull(actor.parent)
    group + actor
    assertNotNull(actor.parent)
    assertTrue(actor in group)
    assertTrue(actor in group.children)
  }

  @Test
  fun shouldRemoveActorsFromGroupWithMinus() {
    val actor = Actor()
    val group = Group()
    group.addActor(actor)
    assertNotNull(actor.parent)
    assertTrue(actor in group)
    group - actor
    assertNull(actor.parent)
    assertFalse(actor in group)
    assertFalse(actor in group.children)
  }

  @Test
  fun shouldCheckIfActorIsOnStageWithInOperator() {
    val actor = Actor()
    val stage = getMockStage()
    assertFalse(actor in stage)
    stage.addActor(actor)
    assertTrue(actor in stage)
    actor.remove()
    assertFalse(actor in stage)
  }

  @Test
  fun shouldAddActorsToStageWithPlus() {
    val actor = Actor()
    val stage = getMockStage()
    assertNull(actor.parent)
    stage + actor
    assertNotNull(actor.parent)
    assertTrue(actor in stage)
    assertTrue(actor in stage.root)
  }

  @Test
  fun shouldRemoveActorsFromStageWithMinus() {
    val actor = Actor()
    val stage = getMockStage()
    stage.addActor(actor)
    assertNotNull(actor.parent)
    assertTrue(actor in stage)
    stage - actor
    assertNull(actor.parent)
    assertFalse(actor in stage)
    assertFalse(actor in stage.root)
  }

  @Test
  fun shouldModifyActorAlphaValue() {
    val actor = Actor()
    val color = actor.color
    assertEquals(1f, color.a, floatTolerance)
    assertEquals(1f, actor.alpha, floatTolerance)
    actor.alpha = 0.5f
    assertEquals(0.5f, color.a, floatTolerance)
    assertEquals(0.5f, actor.alpha, floatTolerance)
  }

  @Test
  fun shouldModifyStageAlphaValue() {
    val stage = getMockStage()
    val color = stage.root.color
    assertEquals(1f, color.a, floatTolerance)
    assertEquals(1f, stage.alpha, floatTolerance)
    stage.alpha = 0.5f
    assertEquals(0.5f, color.a, floatTolerance)
    assertEquals(0.5f, stage.alpha, floatTolerance)
  }

  @Test
  fun shouldReportValidAlphaConstants() {
    val tolerance = 0f
    assertEquals(0f, MIN_ALPHA, tolerance)
    assertEquals(1f, MAX_ALPHA, tolerance)
  }

  @Test
  fun shouldChangeKeyboardFocus() {
    val stage = getMockStage()
    val actor = Actor()
    stage.addActor(actor)
    assertNotEquals(stage.keyboardFocus, actor)

    actor.setKeyboardFocus()
    assertEquals(stage.keyboardFocus, actor)

    actor.setKeyboardFocus(false)
    assertNotEquals(stage.keyboardFocus, actor)
    assertNull(stage.keyboardFocus)

    val newFocus = Actor()
    stage.keyboardFocus = newFocus
    actor.setKeyboardFocus(false) // Should not clear or change stage's focused actor if not currently focused.
    assertNotNull(stage.keyboardFocus)
    assertEquals(newFocus, stage.keyboardFocus)
  }

  @Test
  fun shouldChangeScrollFocus() {
    val stage = getMockStage()
    val actor = Actor()
    stage.addActor(actor)
    assertNotEquals(stage.scrollFocus, actor)

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
}
