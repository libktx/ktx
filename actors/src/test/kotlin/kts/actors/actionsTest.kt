package kts.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import ktx.actors.*
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
    assertEquals(stage.root, action.actor)
  }

  @Test
  fun shouldRemoveGlobalActionsFromStageWithMinus() {
    val stage = getMockStage()
    val action = MockAction()
    stage.addAction(action)
    assertTrue(action in stage.root.actions)
    assertEquals(stage.root, action.actor)
    stage - action
    assertFalse(action in stage.root.actions)
    assertNotEquals(stage.root, action.actor)
  }

  @Test
  fun shouldAttachActionsToActorWithPlus() {
    val actor = Actor()
    val action = MockAction()
    assertFalse(action in actor.actions)
    actor + action
    assertTrue(action in actor.actions)
    assertEquals(actor, action.actor)
  }

  @Test
  fun shouldRemoveActionsFromActorWithMinus() {
    val actor = Actor()
    val action = MockAction()
    actor.addAction(action)
    assertTrue(action in actor.actions)
    assertEquals(actor, action.actor)
    actor - action
    assertFalse(action in actor.actions)
    assertNotEquals(actor, action.actor)
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
    // Note that the second "then" is a different extension function - it prevents from creating multiple sequences.
    val sequence = firstAction then secondAction then thirdAction
    assertTrue(sequence is SequenceAction)
    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(thirdAction, sequence.actions[2])
    assertEquals(3, sequence.actions.size)
  }

  @Test
  fun shouldCreateParallelActions() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val parallel = firstAction.parallelTo(secondAction) // === firstAction parallelTo secondAction
    assertTrue(parallel is ParallelAction)
    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertEquals(2, parallel.actions.size)
  }

  @Test
  fun shouldChainParallelActions() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    // Note that the second "parallelTo" is a different extension function - it prevents from creating multiple parallels.
    val parallel = firstAction parallelTo secondAction parallelTo thirdAction
    assertTrue(parallel is ParallelAction)
    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertTrue(thirdAction in parallel.actions)
    assertEquals(3, parallel.actions.size)
  }

  @Test
  fun shouldCreateActionsRepeatingForever() {
    val action = MockAction().repeatForever()
    assertTrue(action is RepeatAction)
    assertEquals(RepeatAction.FOREVER, action.count)
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
