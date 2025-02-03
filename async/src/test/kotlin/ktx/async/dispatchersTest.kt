@file:OptIn(DelicateCoroutinesApi::class, InternalCoroutinesApi::class)

package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.async.AsyncExecutor
import io.kotlintest.mock.mock
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.verify
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Base class with coroutine dispatcher tests.
 */
abstract class CoroutineDispatcherTest : AsyncTest() {
  abstract val tested: KtxDispatcher

  abstract fun getExecutorThread(): Thread

  open val isConcurrent: Boolean = false

  @Test
  fun `should support asynchronous execution of tasks`() {
    // Given:
    val tested = tested
    val result = AtomicInteger()

    // When:
    runBlocking {
      launch(tested) {
        val a = async { 2 }
        val b = async { 3 }
        result.set(a.await() + b.await())
      }
    }

    // Then:
    assertEquals(5, result.get())
  }

  @Test
  fun `should support delaying and resuming on the execution thread`() {
    // Given:
    val tested = tested
    val executorThread = getExecutorThread()
    val initialThread = CompletableFuture<Thread>()
    val finalThread = CompletableFuture<Thread>()
    val executionTime = AtomicLong()

    // When:
    runBlocking {
      launch(tested) {
        initialThread.complete(Thread.currentThread())
        val start = System.currentTimeMillis()
        delay(50L)
        executionTime.set(System.currentTimeMillis() - start)
        finalThread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertSame(initialThread.get(), finalThread.get())
    assertNotSame(Thread.currentThread(), finalThread.get())
    if (!isConcurrent) {
      assertSame(executorThread, finalThread.get())
    }
    // Tolerance adjusted for the testing environment:
    assertTrue("${executionTime.get()} must be around 50 millis.", executionTime.get() in 45L..200L)
  }

  @Test
  fun `should support invocation on timeout`() {
    // Note: normally you'd use withTimeout in coroutine scope to execute tasks with timeout.
    // This tests the internal invokeOnTimeout API.

    // Given:
    val tested = tested
    val start = System.currentTimeMillis()
    val executionTime = AtomicLong()

    // When:
    tested.invokeOnTimeout(
      50L,
      Runnable {
        executionTime.set(System.currentTimeMillis() - start)
      },
      GlobalScope.coroutineContext,
    )

    // Then:
    delay(100L)
    // Tolerance adjusted for the testing environment:
    assertTrue("${executionTime.get()} must be around 50 millis.", executionTime.get() in 45L..200L)
  }

  @Test
  fun `should support timeout task cancellation`() {
    // Note: normally you'd use withTimeout in coroutine scope to execute tasks with timeout.
    // This tests the internal invokeOnTimeout API.

    // Given:
    val tested = tested
    val executed = AtomicBoolean()
    val handle = tested.invokeOnTimeout(50L, Runnable { executed.set(true) }, GlobalScope.coroutineContext)

    // When:
    handle.dispose()

    // Then:
    delay(100L)
    assertFalse(executed.get())
  }

  @Test
  fun `should skip frame`() {
    // Given:
    val tested = tested
    val initialFrame = AtomicLong()
    val finalFrame = AtomicLong()
    val executorThread = getExecutorThread()
    val threadAfterSkip = CompletableFuture<Thread>()

    // When:
    runBlocking {
      launch(tested) {
        initialFrame.set(Gdx.graphics.frameId)
        skipFrame()
        finalFrame.set(Gdx.graphics.frameId)
        threadAfterSkip.complete(Thread.currentThread())
      }
    }

    // Then:
    val thread = threadAfterSkip.get()
    if (!isConcurrent) {
      assertSame(executorThread, thread)
    }
    // There is no guarantee that only a single frame by the time we try to read it was skipped due to async execution:
    assert(initialFrame.get() < finalFrame.get())
  }
}

class AsyncExecutorDispatcherTest : CoroutineDispatcherTest() {
  override val tested = AsyncExecutorDispatcher(AsyncExecutor(1), threads = 1)

  override fun getExecutorThread(): Thread = getExecutionThread(tested.executor)

  @Test
  fun `should execute tasks outside of the rendering thread`() {
    // Given:
    val renderingThread = getMainRenderingThread()
    val executorThread = getExecutionThread(tested.executor)
    val thread = CompletableFuture<Thread>()

    // When:
    runBlocking {
      launch(tested) {
        thread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertNotSame(Thread.currentThread(), thread.get())
    assertNotSame(renderingThread, thread.get())
    assertSame(executorThread, thread.get())
  }
}

class ConcurrentAsyncExecutorDispatcherTest : CoroutineDispatcherTest() {
  override val tested = AsyncExecutorDispatcher(AsyncExecutor(4), threads = 4)

  override fun getExecutorThread(): Thread = getExecutionThread(tested.executor)

  override val isConcurrent: Boolean = true

  @Test
  fun `should execute tasks outside of the rendering thread`() {
    // Given:
    val renderingThread = getMainRenderingThread()
    val thread = CompletableFuture<Thread>()

    // When:
    runBlocking {
      launch(tested) {
        thread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertNotSame(Thread.currentThread(), thread.get())
    assertNotSame(renderingThread, thread.get())
  }
}

class RenderingThreadDispatcherTest : CoroutineDispatcherTest() {
  override val tested = MainDispatcher

  override fun getExecutorThread(): Thread = getMainRenderingThread()

  @Test
  fun `should execute tasks on the rendering thread`() {
    // Given:
    val renderingThread = getMainRenderingThread()
    val thread = CompletableFuture<Thread>()

    // When:
    runBlocking {
      launch(tested) {
        thread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertSame(renderingThread, thread.get())
    assertNotSame(Thread.currentThread(), thread.get())
  }
}

class DisposableTimerTaskTest {
  @Test
  fun `should cancel task on dispose`() {
    // Given:
    val task = mock<Timer.Task>()
    val tested = DisposableTimerTask(task)

    // When:
    tested.dispose()

    // Then:
    verify(task).cancel()
  }
}

class RenderingThreadDispatcherFactoryTest {
  @Test
  fun `should provide MainDispatcher instance`() {
    // Given:
    val tested = RenderingThreadDispatcherFactory()

    // When:
    val dispatcher = tested.createDispatcher(emptyList())

    // Then:
    assertSame(MainDispatcher, dispatcher)
  }
}
