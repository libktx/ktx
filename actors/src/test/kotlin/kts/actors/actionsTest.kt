package kts.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import ktx.actors.minus
import ktx.actors.plus
import ktx.actors.then
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Action] utilities.
 * @author MJ
 */
class ActionsTest {
  @Test
  fun shouldAttachGlobalActionsToStageWithPlus() {
    val stage = getMockStage()
    val action = MockAction()
    assertFalse(action in stage.root.actions)
    stage + action
    assertTrue(action in stage.root.actions)
  }

  @Test
  fun shouldRemoveGlobalActionsFromStageWithMinus() {
    val stage = getMockStage()
    val action = MockAction()
    stage.addAction(action)
    assertTrue(action in stage.root.actions)
    stage - action
    assertFalse(action in stage.root.actions)
  }

  @Test
  fun shouldChainActionsIntoSequences() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val sequence = firstAction.then(secondAction) // === firstAction then secondAction
    assertTrue(sequence is SequenceAction)
    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(2, sequence.actions.size)
  }

  @Test
  fun shouldChainMultipleActionsIntoSequences() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    // The second "then" is a different extension function - it prevents from creating multiple sequences.
    val sequence = firstAction then secondAction then thirdAction
    assertTrue(sequence is SequenceAction)
    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(thirdAction, sequence.actions[2])
    assertEquals(3, sequence.actions.size)
  }

  /**
   * Action testing utility.
   * @author MJ
   */
  class MockAction : Action() {
    var acted = false
    override fun act(delta: Float): Boolean {
      acted = true
      return true
    }
  }
}
