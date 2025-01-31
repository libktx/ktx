package ktx.async

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@OptIn(DelicateCoroutinesApi::class)
class KtxAsyncTest : AsyncTest() {
  @Test
  fun `should execute tasks on the main rendering thread when launched via KtxAsync`() {
    // Given:
    val thread = CompletableFuture<Thread>()

    // When:
    KtxAsync.launch {
      thread.complete(Thread.currentThread())
    }

    // Then:
    assertSame(thread.join(), getMainRenderingThread())
    assertNotSame(thread.get(), Thread.currentThread())
  }

  @Test
  fun `should expose main KTX dispatcher via Dispatchers object`() {
    // Expect:
    assertSame(MainDispatcher, Dispatchers.KTX)
  }

  @Test
  fun `should create a single-threaded AsyncExecutorDispatcher`() {
    // When:
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext()

    // Then:
    assertEquals(1, dispatcher.threads)
  }

  @Test
  fun `should create a single-threaded AsyncExecutorDispatcher with selected name`() {
    // When:
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext(threadName = "MyThreadName")

    // Then:
    assertEquals(1, dispatcher.threads)
    assertTrue("MyThreadName" in getExecutionThread(dispatcher.executor).name)
  }

  @Test
  fun `should create a multi-threaded AsyncExecutorDispatcher`() {
    // When:
    val dispatcher: AsyncExecutorDispatcher = newAsyncContext(threads = 4)

    // Then:
    assertEquals(4, dispatcher.threads)
  }

  @Test
  fun `should create a multi-threaded AsyncExecutorDispatcher with selected name`() {
    // When:
    val dispatcher: AsyncExecutorDispatcher = newAsyncContext(threads = 4, threadName = "MyThreadName")

    // Then:
    assertEquals(4, dispatcher.threads)
    assertTrue("MyThreadName" in getExecutionThread(dispatcher.executor).name)
  }

  @Test
  fun `should execute on the main rendering thread`() {
    // Given:
    val mainThread = CompletableFuture<Thread>()
    val executionThread = CompletableFuture<Thread>()

    // When:
    GlobalScope.launch {
      onRenderingThread {
        executionThread.complete(Thread.currentThread())
      }
      mainThread.complete(Thread.currentThread())
    }

    // Then:
    mainThread.join()
    assertNotSame(mainThread.get(), executionThread.get())
    assertSame(getMainRenderingThread(), executionThread.get())
  }

  @Test
  fun `should execute on the main rendering thread and return the result`() {
    // Given:
    val mainThread = CompletableFuture<Thread>()
    val executionThread = CompletableFuture<Thread>()
    val result = AtomicInteger()

    // When:
    GlobalScope.launch {
      val value =
        onRenderingThread {
          executionThread.complete(Thread.currentThread())
          42
        }
      result.set(value)
      mainThread.complete(Thread.currentThread())
    }

    // Then:
    mainThread.join()
    assertEquals(42, result.get())
    assertNotSame(mainThread.get(), executionThread.get())
    assertSame(getMainRenderingThread(), executionThread.get())
  }

  @Test
  fun `should skip a single frame on the main rendering thread`() {
    // Given:
    val initialFrame = AtomicLong()
    val finalFrame = AtomicLong()
    val threadAfterSkip = CompletableFuture<Thread>()

    // When:
    KtxAsync.launch {
      initialFrame.set(Gdx.graphics.frameId)
      skipFrame()
      finalFrame.set(Gdx.graphics.frameId)
      threadAfterSkip.complete(Thread.currentThread())
    }

    // Then:
    assertSame(threadAfterSkip.join(), getMainRenderingThread())
    assertEquals(initialFrame.get() + 1, finalFrame.get())
  }

  @Test
  fun `should detect rendering thread`() {
    // Given:
    val mainThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(false)

    // When:
    KtxAsync.launch {
      isOnRenderingThread.set(isOnRenderingThread())
      mainThread.complete(Thread.currentThread())
    }

    // Then:
    assertSame(getMainRenderingThread(), mainThread.join())
    assertTrue(isOnRenderingThread.get())
  }

  @Test
  fun `should detect rendering thread from coroutine with different context`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(true)
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext()

    // When:
    KtxAsync.launch(dispatcher) {
      onRenderingThread {
        isOnRenderingThread.set(isOnRenderingThread())
        executionThread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertSame(getMainRenderingThread(), executionThread.join())
    assertTrue(isOnRenderingThread.get())
  }

  @Test
  fun `should detect rendering thread from coroutine launched from global context`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(false)

    // When:
    GlobalScope.launch {
      onRenderingThread {
        isOnRenderingThread.set(isOnRenderingThread())
        executionThread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertSame(getMainRenderingThread(), executionThread.join())
    assertTrue(isOnRenderingThread.get())
  }

  @Test
  fun `should detect non-rendering threads`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(true)
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext()

    // When:
    KtxAsync.launch(dispatcher) {
      isOnRenderingThread.set(isOnRenderingThread())
      executionThread.complete(Thread.currentThread())
    }

    // Then:
    assertNotSame(getMainRenderingThread(), executionThread.join())
    assertFalse(isOnRenderingThread.get())
  }

  @Test
  fun `should detect non-rendering thread launched from global scope`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(true)

    // When:
    GlobalScope.launch {
      isOnRenderingThread.set(isOnRenderingThread())
      executionThread.complete(Thread.currentThread())
    }

    // Then:
    assertNotSame(getMainRenderingThread(), executionThread.join())
    assertFalse(isOnRenderingThread.get())
  }

  @Test
  fun `should detect nested non-rendering threads`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(true)
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext()

    // When:
    KtxAsync.launch(Dispatchers.KTX) {
      KtxAsync.launch(dispatcher) {
        isOnRenderingThread.set(isOnRenderingThread())
        executionThread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertNotSame(getMainRenderingThread(), executionThread.join())
    assertFalse(isOnRenderingThread.get())
  }

  @Test
  fun `should detect nested non-rendering threads with separate context`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(true)
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext()

    // When:
    KtxAsync.launch(Dispatchers.KTX) {
      withContext(dispatcher) {
        isOnRenderingThread.set(isOnRenderingThread())
        executionThread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertNotSame(getMainRenderingThread(), executionThread.join())
    assertFalse(isOnRenderingThread.get())
  }

  @Test
  fun `should detect non-rendering threads with context switch`() {
    // Given:
    val executionThread = CompletableFuture<Thread>()
    val isOnRenderingThread = AtomicBoolean(true)
    val dispatcher: AsyncExecutorDispatcher = newSingleThreadAsyncContext()

    // When:
    KtxAsync.launch(Dispatchers.KTX) {
      withContext(dispatcher) {
        isOnRenderingThread.set(isOnRenderingThread())
        executionThread.complete(Thread.currentThread())
      }
    }

    // Then:
    assertNotSame(getMainRenderingThread(), executionThread.join())
    assertFalse(isOnRenderingThread.get())
  }

  @Test
  fun `should create non-global scope`() {
    // Given:
    val scope = RenderingScope()
    val localJob =
      scope.launch {
        while (true) {
          delay(50)
        }
      }
    val globalJob =
      KtxAsync.launch {
        while (true) {
          delay(50)
        }
      }

    // When:
    scope.cancel()

    // Then:
    assert(localJob.isCancelled)
    assert(globalJob.isActive)
  }
}
