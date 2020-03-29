package ktx.async

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.utils.async.AsyncExecutor
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit.SECONDS

/**
 * Base class for asynchronous API tests. Initiates a [HeadlessApplication] to handle the rendering loop and execution
 * of scheduled tasks.
 */
abstract class AsyncTest {
  @Before
  open fun `setup LibGDX application`() {
    Gdx.app = HeadlessApplication(object : ApplicationAdapter() {})
    val initTask = CompletableFuture<Unit>()
    Gdx.app.postRunnable {
      // Saving reference to the rendering thread:
      KtxAsync.initiate()
      initTask.complete(Unit)
    }
    initTask.join()
    assertSame(getExecutionThread(Gdx.app::postRunnable), getMainRenderingThread())
  }

  /**
   * Finds the main rendering [Thread] registered in [MainDispatcher].
   */
  protected fun getMainRenderingThread(): Thread = MainDispatcher.mainThread

  /**
   * Finds the [Thread] that [AsyncExecutor] executes tasks with. Note that if the executor uses more than a single
   * thread, this method will return only the first one that happens to execute the extraction task. When relying on
   * this method to find the executor thread, make sure the executor is single-threaded.
   */
  protected fun getExecutionThread(executor: AsyncExecutor): Thread {
    return getExecutionThread { task ->
      executor.submit { task.run() }
    }
  }

  /**
   * Finds the [Thread] that executes the passed [Runnable].
   */
  private fun getExecutionThread(executor: (Runnable) -> Unit): Thread {
    val thread = CompletableFuture<Thread>()
    executor(Runnable {
      thread.complete(Thread.currentThread())
    })
    return thread.get(5L, SECONDS)
  }

  /**
   * Blocks the current thread for the given amount of [seconds].
   */
  protected fun delay(seconds: Float) {
    delay((seconds * 1000).toLong())
  }

  /**
   * Blocks the current thread for the given amount of [millis].
   */
  protected fun delay(millis: Long) {
    Thread.sleep(millis)
  }

  @After
  open fun `exit LibGDX application`() {
    Gdx.app.exit()
  }
}
