package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Test

class BranchTasksTest {
  @Test
  fun `dynamicGuardSelector function should add DynamicGuardSelector to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.dynamicGuardSelector()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `dynamicGuardSelector function inside of behaviorTree function's init block should add DynamicGuardSelector to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        dynamicGuardSelector()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `dynamicGuardSelector function should return index where DynamicGuardSelector is added`() {
    val receiverTask = GdxAiSelector<Cat>()
    val index1 = receiverTask.dynamicGuardSelector()
    val index2 = receiverTask.dynamicGuardSelector()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `dynamicGuardSelector function's init block should configure DynamicGuardSelector exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.dynamicGuardSelector {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `parallel function should add Parallel to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.parallel()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `parallel function inside of behaviorTree function's init block should add Parallel to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        parallel()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `parallel function should return index where Parallel is added`() {
    val receiverTask = GdxAiSelector<Cat>()
    val index1 = receiverTask.parallel()
    val index2 = receiverTask.parallel()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `parallel function's init block should configure Parallel exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.parallel {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `randomSelector function should add RandomSelector to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.randomSelector()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `randomSelector function inside of behaviorTree function's init block should add RandomSelector to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        randomSelector()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `randomSelector function should return index where RandomSelector is added`() {
    val receiverTask = GdxAiSelector<Cat>()
    val index1 = receiverTask.randomSelector()
    val index2 = receiverTask.randomSelector()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `randomSelector function's init block should configure RandomSelector exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.randomSelector {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `randomSequence function should add RandomSequence to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.randomSequence()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `randomSequence inside of behaviorTree function's init block should add RandomSequence to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        randomSequence()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `randomSequence function should return index where RandomSequence is added`() {
    val receiverTask = GdxAiSelector<Cat>()
    val index1 = receiverTask.randomSequence()
    val index2 = receiverTask.randomSequence()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `randomSequence function's init block should configure RandomSequence exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.randomSequence {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `selector function should add GdxSelector to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.selector()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `selector inside of behaviorTree function's init block adds GdxSelector to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        selector()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `selector function should return index where GdxSelector is added`() {
    val receiverTask = GdxAiSequence<Cat>()
    val index1 = receiverTask.selector()
    val index2 = receiverTask.selector()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `selector function's init block should configure GdxSelector exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.selector {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `sequence function should add GdxSequence to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.sequence()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `sequence inside of behaviorTree function's init block should add GdxSequence to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        sequence()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `sequence function should return index where GdxSequence is added`() {
    val receiverTask = GdxAiSelector<Cat>()
    val index1 = receiverTask.sequence()
    val index2 = receiverTask.sequence()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `sequence function's init block should configure GdxSequence exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.sequence {
      variable = 42
    }

    assertEquals(42, variable)
  }
}
