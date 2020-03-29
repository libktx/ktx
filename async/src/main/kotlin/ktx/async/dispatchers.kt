package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.async.AsyncExecutor
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.MainDispatcherFactory
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

/**
 * Base interface of [CoroutineContext] for dispatchers using the LibGDX threading model.
 * Uses LibGDX [Timer] API to support [delay].
 */
interface KtxDispatcher : CoroutineContext, Delay {
  /**
   * Immediately executes or schedules execution of the passed [block].
   */
  @InternalCoroutinesApi
  fun execute(block: Runnable)

  override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
    schedule(delaySeconds = timeMillis.toSeconds()) { continuation.resume(Unit) }
  }

  override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
    val task = schedule(delaySeconds = timeMillis.toSeconds()) { execute(block) }
    return DisposableTimerTask(task)
  }

  /** Converts milliseconds to seconds. */
  private fun Long.toSeconds() = this.toFloat() / 1000f
}

/**
 * Base extension of [CoroutineDispatcher] for dispatchers using the LibGDX threading model.
 * Uses LibGDX [Timer] API to support [delay].
 */
abstract class AbstractKtxDispatcher : CoroutineDispatcher(), KtxDispatcher {
  override fun dispatch(context: CoroutineContext, block: Runnable) {
    execute(block)
  }
}

/**
 * Wraps around LibGDX [Timer.Task] to make it possible to cancel scheduled tasks. Holds a reference to the original
 * scheduled [task].
 */
class DisposableTimerTask(val task: Timer.Task) : DisposableHandle, Disposable {
  override fun dispose() {
    task.cancel()
  }
}

/**
 * A [CoroutineDispatcher] that wraps around LibGDX [AsyncExecutor] instance (available via [executor] property)
 * to execute tasks asynchronously. [threads] property is purely informational and cannot be verified, since
 * [AsyncExecutor] does not expose the amount of threads it uses internally; provide a valid [threads] amount during
 * initiation or use the official factory methods to prevent from mismatches with the actual max threads amount.
 *
 * Uses LibGDX [Timer] API to support [delay].
 */
class AsyncExecutorDispatcher(val executor: AsyncExecutor, val threads: Int = -1)
  : AbstractKtxDispatcher(), Closeable, Disposable {
  @InternalCoroutinesApi
  override fun execute(block: Runnable) {
    executor.submit(block::run)
  }

  override fun close() = dispose()
  override fun dispose() {
    try {
      executor.dispose()
    } catch (exception: GdxRuntimeException) {
      Gdx.app?.apply {
        error("KTX", "Unable to dispose of the AsyncExecutor.", exception)
      }
    }
  }

  override fun toString(): String = "AsyncExecutorDispatcher(threads=$threads, executor=$executor)"
}

/**
 * A [CoroutineDispatcher] that wraps around LibGDX runnable execution API to execute tasks
 * on the main rendering thread. Uses LibGDX [Timer] API to support [delay].
 */
sealed class RenderingThreadDispatcher : MainCoroutineDispatcher(), KtxDispatcher, Delay {
  @InternalCoroutinesApi
  override fun execute(block: Runnable) {
    Gdx.app.postRunnable(block)
  }

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    execute(block)
  }

  override fun toString(): String = "KtxRenderingThreadDispatcher"
}

/**
 * Implements [MainDispatcherFactory] to provide references to the [MainDispatcher].
 */
class RenderingThreadDispatcherFactory : MainDispatcherFactory {
  override val loadPriority: Int = 0
  override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher = MainDispatcher
}

/**
 * Executes tasks on the main rendering thread. See [RenderingThreadDispatcher].
 */
object MainDispatcher : RenderingThreadDispatcher() {
  @ExperimentalCoroutinesApi
  override val immediate = this
  lateinit var mainThread: Thread

  /** Must be called **on the rendering thread** before using KTX coroutines. */
  fun initiate() {
    mainThread = Thread.currentThread()
  }

  override fun isDispatchNeeded(context: CoroutineContext): Boolean = !KtxAsync.isOnRenderingThread()
}
