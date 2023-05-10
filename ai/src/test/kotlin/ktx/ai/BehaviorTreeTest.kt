package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BehaviorTreeTest {

  @Test
  fun `behaviorTree function without arguments should return a BehaviorTree object`() {
    val behaviorTree = behaviorTree<Cat>()

    assertNotNull(behaviorTree)
  }

  @Test
  fun `behaviorTree function with non-null arguments should return a BehaviorTree object with the correct properties`() {
    val rootTask = mock<Task<Cat>>()
    val blackboard = mock<Cat>()
    val tree = behaviorTree(rootTask, blackboard)

    val rootTaskField = BehaviorTree::class.java.getDeclaredField("rootTask").apply {
      isAccessible = true
    }
    val blackboardField = BehaviorTree::class.java.getDeclaredField("blackboard").apply {
      isAccessible = true
    }

    assertNotNull(tree)
    assertEquals(rootTask, rootTaskField.get(tree))
    assertEquals(blackboard, blackboardField.get(tree))
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
  fun `BehaviorTree's add function should add a task to the BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    val task = mock<Task<Cat>>()
    tree.add(task)

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `BehaviorTree's add function should return the index where the task is added`() {
    val tree = BehaviorTree<Cat>()
    val index = tree.add(mock<Task<Cat>>())

    assertNotNull(index)
  }

  @Test
  fun `add function inside of behaviorTree function init block should add task to BehaviorTree`() {
    val initialChildCount: Int

    val tree = behaviorTree {
      initialChildCount = childCount
      add(mock<Task<Cat>>())
    }

    assertEquals(initialChildCount + 1, tree.childCount)
  }
}
