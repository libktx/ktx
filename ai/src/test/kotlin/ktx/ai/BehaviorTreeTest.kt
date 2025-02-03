package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BehaviorTreeTest {
  @Test
  fun `behaviorTree function without arguments should return BehaviorTree object`() {
    val behaviorTree = behaviorTree<Cat>()

    assertNotNull(behaviorTree)
  }

  @Test
  fun `behaviorTree function with non-null arguments should return BehaviorTree object with correct properties`() {
    val rootTask = mock<Task<Cat>>()
    val blackboard = mock<Cat>()
    val tree = behaviorTree(rootTask, blackboard)

    assertNotNull(tree)
    assertEquals(rootTask, tree.getChild(0))
    assertEquals(blackboard, tree.`object`)
  }

  @Test
  fun `behaviorTree function init block should configure BehaviorTree exactly once`() {
    val variable: Int

    behaviorTree<Cat> {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `add function should add task to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    val task = mock<Task<Cat>>()
    tree.add(task)

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `add function should return index where task is added`() {
    val selector = GdxAiSelector<Cat>()
    val index1 = selector.add(mock<Task<Cat>>())
    val index2 = selector.add(mock<Task<Cat>>())

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `add function inside of behaviorTree function's init block should add task to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree {
        initialChildCount = childCount
        add(mock<Task<Cat>>())
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `add function's init block allows editing properties of Task subclass`() {
    val receiverTask = mock<Task<Cat>>()

    val eatTask = EatTask(hunger = 1)
    val initialHunger = eatTask.hunger

    receiverTask.add(eatTask) {
      hunger -= 1
    }

    assertEquals(initialHunger - 1, eatTask.hunger)
  }

  @Test
  fun `add function's init block should configure Task exactly once`() {
    val variable: Int
    val receiverTask = mock<Task<Cat>>()
    val task = mock<Task<Cat>>()

    receiverTask.add(task) {
      variable = 42
    }

    assertEquals(42, variable)
  }
}
