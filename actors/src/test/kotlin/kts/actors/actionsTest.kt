package kts.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import ktx.actors.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Action] utilities.
 */
class ActionsTest {
  @Test
  fun `should attach global actions to stage with +=`() {
    val stage = getMockStage()
    val action = MockAction()

    stage += action

    assertTrue(action in stage.root.actions)
    assertSame(stage.root, action.actor)
  }

  @Test
  fun `should remove global actions from stage with -=`() {
    val stage = getMockStage()
    val action = MockAction()
    stage.addAction(action)

    stage -= action

    assertFalse(action in stage.root.actions)
    assertNotEquals(stage.root, action.actor)
  }

  @Test
  fun `should attach actions to actors with +=`() {
    val actor = Actor()
    val action = MockAction()

    actor += action

    assertTrue(action in actor.actions)
    assertEquals(actor, action.actor)
  }

  @Test
  fun `should remove actions from actors with -=`() {
    val actor = Actor()
    val action = MockAction()
    actor.addAction(action)

    actor -= action

    assertFalse(action in actor.actions)
    assertNotEquals(actor, action.actor)
  }

  @Test
  fun `should chain actions into sequences`() {
    val firstAction = MockAction()
    val secondAction = MockAction()

    val sequence = firstAction.then(secondAction) // === firstAction then secondAction

    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(2, sequence.actions.size)
  }

  @Test
  fun `should chain multiple actions into sequences`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()

    // / Note that the second "then" is a different extension function - it prevents from creating multiple sequences.
    val sequence = firstAction then secondAction then thirdAction

    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(thirdAction, sequence.actions[2])
    assertEquals(3, sequence.actions.size)
  }

  @Test
  fun `should create parallel actions`() {
    val firstAction = MockAction()
    val secondAction = MockAction()

    val parallel = firstAction.parallelTo(secondAction) // === firstAction parallelTo secondAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertEquals(2, parallel.actions.size)
  }

  @Test
  fun `should chain parallel actions`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()

    // Note that the second "parallelTo" is a different extension function - it prevents from creating multiple parallels.
    val parallel = firstAction parallelTo secondAction parallelTo thirdAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertTrue(thirdAction in parallel.actions)
    assertEquals(3, parallel.actions.size)
  }

  @Test
  fun `should create actions repeating forever`() {
    val action = MockAction().repeatForever()

    assertEquals(RepeatAction.FOREVER, action.count)
  }

  /** Action testing utility. */
  class MockAction : Action() {
    private var acted = false
    override fun act(delta: Float): Boolean {
      acted = true
      return true
    }
  }
}
