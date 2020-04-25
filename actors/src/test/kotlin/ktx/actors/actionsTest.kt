package ktx.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
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
  fun `should chain actions into sequence with action then action`() {
    val firstAction = MockAction()
    val secondAction = MockAction()

    val sequence = firstAction then secondAction

    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(2, sequence.actions.size)
  }

  @Test
  fun `should chain underling actions into sequence with sequence then action`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()

    val sequence = Actions.sequence(firstAction, secondAction) then thirdAction

    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(thirdAction, sequence.actions[2])
    assertEquals(3, sequence.actions.size)
  }

  @Test
  fun `should use existing sequence with then`() {
    val firstAction = SequenceAction(MockAction(), MockAction())
    val secondAction = MockAction()

    val sequence = firstAction then secondAction

    assertEquals(3, sequence.actions.size)
    assertSame(firstAction, sequence)
  }

  @Test
  fun `should not mutate multiple actions with +`() {
    // Given:
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    val sequence = firstAction + secondAction

    // When:
    sequence + thirdAction

    // Then: should not mutate firstSequence.
    assertEquals(2, sequence.actions.size)
    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
  }

  @Test
  fun `should add action to sequence with +=`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    val sequence = Actions.sequence(firstAction, secondAction)

    sequence += thirdAction

    assertEquals(firstAction, sequence.actions[0])
    assertEquals(secondAction, sequence.actions[1])
    assertEquals(thirdAction, sequence.actions[2])
    assertEquals(3, sequence.actions.size)
  }

  @Test
  fun `should create parallel action given two actions`() {
    val firstAction = MockAction()
    val secondAction = MockAction()

    val parallel = firstAction along secondAction // === firstAction parallelTo secondAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertEquals(2, parallel.actions.size)
  }

  @Test
  fun `should create parallel action given a parallel action and regular action`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()

    val parallel = Actions.parallel(firstAction, secondAction) along thirdAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertTrue(thirdAction in parallel.actions)
    assertEquals(3, parallel.actions.size)
  }

  @Test
  fun `should wrap a regular action and parallel action with action along`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()

    val parallel = firstAction along Actions.parallel(secondAction, thirdAction)

    assertTrue(firstAction in parallel.actions)
    assertEquals(2, parallel.actions.size)
    assertSame(secondAction, (parallel.actions[1] as ParallelAction).actions[0])
    assertSame(thirdAction, (parallel.actions[1] as ParallelAction).actions[1])
  }

  @Test
  fun `should chain parallel actions`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()

    val parallel = firstAction along secondAction along thirdAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertTrue(thirdAction in parallel.actions)
    assertEquals(3, parallel.actions.size)
  }

  @Test
  fun `should not mutate parallel actions with +`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    val parallel = Actions.parallel(firstAction, secondAction)

    parallel + thirdAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertFalse(thirdAction in parallel.actions)
    assertEquals(2, parallel.actions.size)
  }

  @Test
  fun `should not mutate multiple actions with div`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    val sequence = Actions.sequence(firstAction, secondAction)

    sequence / thirdAction

    assertTrue(firstAction in sequence.actions)
    assertTrue(secondAction in sequence.actions)
    assertFalse(thirdAction in sequence.actions)
    assertEquals(2, sequence.actions.size)
  }

  @Test
  fun `should use existing parallel with along`() {
    val firstAction = ParallelAction(MockAction(), MockAction())
    val secondAction = MockAction()

    val parallel = firstAction along secondAction

    assertEquals(3, parallel.actions.size)
    assertSame(firstAction, parallel)
  }

  @Test
  fun `should add action to parallel action group with +=`() {
    val firstAction = MockAction()
    val secondAction = MockAction()
    val thirdAction = MockAction()
    val parallel = Actions.parallel(firstAction, secondAction)

    parallel += thirdAction

    assertTrue(firstAction in parallel.actions)
    assertTrue(secondAction in parallel.actions)
    assertTrue(thirdAction in parallel.actions)
    assertEquals(3, parallel.actions.size)
  }

  @Test
  fun `should not unwrap sequences with along`() {
    val firstSequence = SequenceAction(MockAction(), MockAction())
    val secondSequence = SequenceAction(MockAction(), MockAction())

    val parallel = firstSequence along secondSequence

    assertTrue(firstSequence in parallel.actions)
    assertTrue(secondSequence in parallel.actions)
    assertEquals(2, parallel.actions.size)
  }

  @Test
  fun `should make sequence actions parallel with div`() {
    val firstSequence = SequenceAction(MockAction(), MockAction())
    val secondSequence = SequenceAction(MockAction(), MockAction())

    val parallel = firstSequence / secondSequence

    assertTrue(firstSequence in parallel.actions)
    assertTrue(secondSequence in parallel.actions)
    assertEquals(2, parallel.actions.size)
  }

  @Test
  fun `should create actions repeating forever`() {
    val action = MockAction().repeatForever()

    assertEquals(RepeatAction.FOREVER, action.count)
  }

  @Test
  fun `should create actions repeating given amount of times`() {
    val action = MockAction().repeat(10)

    assertEquals(10, action.count)
  }

  @Test
  fun `should prohibit from using negative repetitions count`() {
    val action = MockAction().repeat(-10)

    // To avoid unexpected unlimited repetitions count, to switch negative numbers to 0.
    assertEquals(0, action.count)
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
