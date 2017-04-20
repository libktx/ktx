package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.GdxRuntimeException
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.matchers.shouldThrow
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CompletableFuture

class AsyncTest {
  @Before
  fun `initiate LibGDX application`() {
    Gdx.app = mock()
  }

  @Test
  fun `should prohibit accessing rendering thread and executor before initiation`() {
    shouldThrow<UninitializedPropertyAccessException> { KtxAsync.mainThread }
    shouldThrow<UninitializedPropertyAccessException> { KtxAsync.asyncExecutor }
    shouldThrow<UninitializedPropertyAccessException> {
      ktxAsync {
        fail("Should prohibit executing coroutines without context.")
      }
    }
  }

  @Test
  fun `should initiate coroutines context`() {
    KtxAsync.initiate()

    assertSame(Thread.currentThread(), KtxAsync.mainThread)
  }

  @Test
  fun `should create AsyncExecutor`() {
    KtxAsync.createExecutor(concurrencyLevel = 1)

    assertNotNull(KtxAsync.asyncExecutor)
  }

  @Test
  fun `should enable coroutines`() {
    enableKtxCoroutines()

    assertSame(Thread.currentThread(), KtxAsync.mainThread)
    shouldThrow<UninitializedPropertyAccessException> { KtxAsync.asyncExecutor }
  }

  @Test
  fun `should enable coroutines with AsyncExecutor given concurrency level`() {
    enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)

    assertSame(Thread.currentThread(), KtxAsync.mainThread)
    assertNotNull(KtxAsync.asyncExecutor)
  }

  @Test
  fun `should report if on thread that initiated the context`() {
    KtxAsync.initiate()

    assertTrue(KtxAsync.isOnRenderingThread())
  }

  @Test
  fun `should report if not on thread that initiated the context`() {
    CompletableFuture.runAsync { KtxAsync.initiate() }.join()

    assertNotNull(KtxAsync.mainThread)
    assertNotSame(Thread.currentThread(), KtxAsync.mainThread)
    assertFalse(KtxAsync.isOnRenderingThread())
  }

  @Test
  fun `should not add thread verification task if validation is turned off`() {
    enableKtxCoroutines(validate = false)

    verifyZeroInteractions(Gdx.app)
  }

  @Test
  fun `should add thread verification task`() {
    val tasks = mutableListOf<Runnable>()
    Gdx.app = mock {
      on(it.postRunnable(any())) doAnswer { tasks.add(it.getArgument(0)); Unit }
    }

    CompletableFuture.runAsync { enableKtxCoroutines() }.join()

    assertNotNull(KtxAsync.mainThread)
    assertNotSame(Thread.currentThread(), KtxAsync.mainThread)
    assertEquals(1, tasks.size)
    shouldThrow<GdxRuntimeException> { tasks.forEach(Runnable::run) }
  }

  @Test
  fun `should pass delay tasks to main LibGDX Timer`() = `with timer`(onSchedule = `no delay execution`()) { timer ->
    enableKtxCoroutines()

    ktxAsync {
      delay(1.5f)
    }

    verify(timer).scheduleTask(any(), eq(1.5f))
  }

  @Test
  fun `should delay coroutine execution`() = `with timer` { timer ->
    `coroutine test` { ktxAsync ->
      val beforeExecution = System.currentTimeMillis()
      ktxAsync {
        delay(0.1f)

        val executionTime = System.currentTimeMillis() - beforeExecution
        verify(timer).scheduleTask(any(), eq(0.1f))
        // Mocks, test runner, scheduler and coroutines overhead might affect execution time, but if the delay is longer
        // than 0.05s, something might be is wrong.
        assertTrue("100 milliseconds delay execution took $executionTime milliseconds.", executionTime in 100L..150L)
      }
    }
  }

  @Test
  fun `should perform asynchronous action`() = `coroutine test`(executorConcurrencyLevel = 1) { ktxAsync ->
    ktxAsync {
      val (result, thread) = asynchronous { "Test." to Thread.currentThread() }

      assertEquals("Test.", result)
      assertNotSame(KtxAsync.mainThread, thread)
    }
  }

  @Test
  fun `should handle asynchronous action exceptions`() = `coroutine test`(executorConcurrencyLevel = 1) { ktxAsync ->
    ktxAsync {
      shouldThrow<GdxRuntimeException> {
        asynchronous { throw GdxRuntimeException("Expected.") }
      }
    }
  }

  @After
  fun `clear context`() {
    Gdx.app = null
    `destroy coroutines context`()
  }
}
