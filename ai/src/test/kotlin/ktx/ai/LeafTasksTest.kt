package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Test

class LeafTasksTest {
  @Test
  fun `failureLeaf function should add Failure to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.failureLeaf()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `failureLeaf function inside of behaviorTree function's init block should add Failure to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        failureLeaf()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `failureLeaf function should return index where Failure is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.failureLeaf()
    val index2 = receiverTask.failureLeaf()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `failureLeaf function's init block should configure Failure exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.failureLeaf {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `successLeaf function should add Success to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.successLeaf()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `successLeaf function inside of behaviorTree function's init block should add Success to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        successLeaf()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `successLeaf function should return index where Success is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.successLeaf()
    val index2 = receiverTask.successLeaf()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `successLeaf function's init block should configure Success exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.successLeaf {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `waitLeaf function with explicit seconds parameter should add Wait to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.waitLeaf(1f)

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `waitLeaf function with explicit seconds parameter inside of behaviorTree function's init block should add Wait to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        waitLeaf(1f)
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `waitLeaf function should add Wait to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.waitLeaf()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `waitLeaf function inside of behaviorTree function's init block should add Wait to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        waitLeaf()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `waitLeaf function should return index where Wait is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.waitLeaf()
    val index2 = receiverTask.waitLeaf()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `waitLeaf function's init block should configure Wait exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.waitLeaf {
      variable = 42
    }

    assertEquals(42, variable)
  }
}
