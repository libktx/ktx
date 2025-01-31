package ktx.async

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.async.AsyncExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.resume

/**
 * Main KTX coroutine scope. Executes tasks on the main rendering thread. See [MainDispatcher].
 */
object KtxAsync : CoroutineScope {
  override val coroutineContext = MainDispatcher

  /**
   * Should be invoked on the main rendering thread before using KTX coroutines.
   * Failing to do so will cause some parts of the API to throw exceptions.
   */
  fun initiate() {
    MainDispatcher.initiate()
  }
}

/**
 * Main KTX coroutine dispatcher. Executes tasks on the main rendering thread. See [MainDispatcher].
 */
@Suppress("unused")
val Dispatchers.KTX
  get() = MainDispatcher

/**
 * Creates a coroutine scope in the rendering thread with a supervisor job. Allows to manage multiple
 * tasks executed on the main rendering thread within a single scope, providing mass actions such
 * as task cancelling that do not affect other scopes.
 *
 * An alternative to direct usage of the global [KtxAsync].
 *
 * @see kotlinx.coroutines.MainScope
 */
@Suppress("FunctionName")
fun RenderingScope() = CoroutineScope(SupervisorJob() + MainDispatcher)

/**
 * Creates a new [AsyncExecutorDispatcher] wrapping around an [AsyncExecutor] with a single thread to execute tasks
 * asynchronously outside of the main rendering thread.
 *
 * [AsyncExecutor] thread will be named according to the [threadName] pattern.
 */
fun newSingleThreadAsyncContext(threadName: String = "AsyncExecutor-Thread") = newAsyncContext(1, threadName)

/**
 * Creates a new [AsyncExecutorDispatcher] wrapping around an [AsyncExecutor] with the chosen amount of [threads]
 * to execute tasks asynchronously outside of the main rendering thread.
 *
 * [AsyncExecutor] threads will be named according to the [threadName] pattern.
 */
fun newAsyncContext(
  threads: Int,
  threadName: String = "AsyncExecutor-Thread",
) = AsyncExecutorDispatcher(AsyncExecutor(threads, threadName), threads)

/**
 * Suspends the coroutine to execute the defined [block] on the main rendering thread and return its result.
 */
suspend fun <T> onRenderingThread(block: suspend CoroutineScope.() -> T) = withContext(MainDispatcher, block = block)

/**
 * Returns true if the coroutine was launched from a rendering thread dispatcher.
 */
fun CoroutineScope.isOnRenderingThread() =
  coroutineContext[ContinuationInterceptor.Key] is RenderingThreadDispatcher &&
    Thread.currentThread() === MainDispatcher.mainThread

/**
 * Attempts to skip the current frame. Resumes the execution using a task scheduled with [Application.postRunnable].
 *
 * Due to asynchronous nature of the execution, there is no guarantee that this method will always skip only a *single*
 * frame before resuming, but it will always suspend the current coroutine until the [Runnable] instances scheduled
 * with [Application.postRunnable] are executed by the [Application].
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun skipFrame() {
  suspendCancellableCoroutine<Unit> { continuation ->
    Gdx.app.postRunnable {
      if (continuation.isActive) {
        val context = continuation.context[ContinuationInterceptor.Key]
        if (context is RenderingThreadDispatcher) {
          // Executed via main thread dispatcher and already on the main thread - resuming immediately:
          with(continuation) { context.resumeUndispatched(Unit) }
        } else {
          // Executed via a different dispatcher - getting off the main thread:
          continuation.resume(Unit)
        }
      }
    }
  }
}
