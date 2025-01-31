package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Test

class DecoratorsTest {
  @Test
  fun `alwaysFail function should add AlwaysFail to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.alwaysFail()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `alwaysFail function inside of behaviorTree function's init block should add AlwaysFail to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        alwaysFail()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `alwaysFail function should return index where AlwaysFail is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.alwaysFail()
    val index2 = receiverTask.alwaysFail()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `alwaysFail function's init block should configure AlwaysFail exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.alwaysFail {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `alwaysSucceed function should add AlwaysSucceed to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.alwaysSucceed()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `alwaysSucceed function inside of behaviorTree function's init block should add AlwaysSucceed to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        alwaysSucceed()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `alwaysSucceed function should return index where AlwaysSucceed is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.alwaysSucceed()
    val index2 = receiverTask.alwaysSucceed()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `alwaysSucceed function's init block should configure AlwaysSucceed exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.alwaysSucceed {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `include function should add Include to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.include()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `include function inside of behaviorTree function's init block should add Include to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        include()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `include function should return index where Include is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.include()
    val index2 = receiverTask.include()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `include function's init block should configure Include exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.include {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `invert function should add Invert to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.invert()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `invert function inside of behaviorTree function's init block should add Invert to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        invert()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `invert function should return index where Invert is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.invert()
    val index2 = receiverTask.invert()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `invert function's init block should configure Invert exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.invert {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `random function should add GdxAiRandom to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.random()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `random function inside of behaviorTree function's init block should add GdxAiRandom to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        random()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `random function should return index where GdxAiRandom is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.random()
    val index2 = receiverTask.random()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `random function's init block should configure GdxAiRandom exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.random {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `repeat function should add Repeat to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.repeat()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `repeat function inside of behaviorTree function's init block should add Repeat to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        repeat()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `repeat function should return index where Repeat is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.repeat()
    val index2 = receiverTask.repeat()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `repeat function's init block should configure Repeat exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.repeat {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `semaphoreGuard function should add SemaphoreGuard to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.semaphoreGuard()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `semaphoreGuard function inside of behaviorTree function's init block should add SemaphoreGuard to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        semaphoreGuard()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `semaphoreGuard function should return index where SemaphoreGuard is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.semaphoreGuard()
    val index2 = receiverTask.semaphoreGuard()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `semaphoreGuard function's init block should configure SemaphoreGuard exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.semaphoreGuard {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `untilFail function should add UntilFail to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.untilFail()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `untilFail function inside of behaviorTree function's init block should add UntilFail to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        untilFail()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `untilFail function should return index where UntilFail is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.untilFail()
    val index2 = receiverTask.untilFail()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `untilFail function's init block should configure UntilFail exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.untilFail {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `untilSuccess function should add UntilSuccess to BehaviorTree`() {
    val tree = BehaviorTree<Cat>()
    val initialChildCount = tree.childCount

    tree.untilSuccess()

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `untilSuccess function inside of behaviorTree function's init block should add UntilSuccess to BehaviorTree`() {
    val initialChildCount: Int

    val tree =
      behaviorTree<Cat> {
        initialChildCount = childCount
        untilSuccess()
      }

    assertEquals(initialChildCount + 1, tree.childCount)
  }

  @Test
  fun `untilSuccess function should return index where UntilSuccess is added`() {
    val receiverTask = GdxAiSequence<Cat>()

    val index1 = receiverTask.untilSuccess()
    val index2 = receiverTask.untilSuccess()

    assertEquals(0, index1)
    assertEquals(1, index2)
  }

  @Test
  fun `untilSuccess function's init block should configure UntilSuccess exactly once`() {
    val receiverTask = mock<Task<Cat>>()
    val variable: Int

    receiverTask.untilSuccess {
      variable = 42
    }

    assertEquals(42, variable)
  }
}
