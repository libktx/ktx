package ktx.async

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.async.AsyncExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.resume

/**
 * Main KTX coroutine scope. Executes tasks on the main rendering thread. See [MainDispatcher].
 */
object KtxAsync : CoroutineScope {
  override val coroutineContext = MainDispatcher

  /**
   * Should be invoked on the main rendering thread before using KTX coroutines. Might slightly affect performance
   * otherwise.
   */
  fun initiate() {
    ImmediateDispatcher.initiate()
  }
}

/**
 * Main KTX coroutine dispatcher. Executes tasks on the main rendering thread. See [MainDispatcher].
 */
@Suppress("unused")
val Dispatchers.KTX
  get() = MainDispatcher

/**
 * Creates a coroutine scope in the rendering thread with a supervisor job. An alternative to the global [KtxAsync].
 */
@Suppress("FunctionName")
fun RenderScope() = CoroutineScope(SupervisorJob() + MainDispatcher)

/**
 * Creates a new [AsyncExecutorDispatcher] wrapping around an [AsyncExecutor] with a single thread to execute tasks
 * asynchronously outside of the main rendering thread.
 */
fun newSingleThreadAsyncContext() = newAsyncContext(1)

/**
 * Creates a new [AsyncExecutorDispatcher] wrapping around an [AsyncExecutor] with the chosen amount of [threads]
 * to execute tasks asynchronously outside of the main rendering thread.
 */
fun newAsyncContext(threads: Int) = AsyncExecutorDispatcher(AsyncExecutor(threads), threads)

/**
 * Suspends the coroutine to execute the defined [block] on the main rendering thread and return its result.
 */
suspend fun <T> onRenderingThread(block: suspend CoroutineScope.() -> T) = withContext(MainDispatcher, block = block)

/**
 * Returns true if the coroutine was launched from a rendering thread dispatcher.
 */
fun CoroutineScope.isOnRenderingThread() = coroutineContext[ContinuationInterceptor.Key] is RenderingThreadDispatcher

/**
 * Attempts to skip the current frame. Resumes the execution using a task scheduled with [Application.postRunnable].
 * Due to asynchronous nature of the execution, there is no guarantee that this method will always skip only a *single*
 * frame before further method calls are executed, but it will always skip *at least one* frame.
 */
suspend fun skipFrame() {
  suspendCancellableCoroutine<Unit> { continuation ->
    Gdx.app.postRunnable {
      val context = continuation.context[ContinuationInterceptor.Key]
      if (continuation.isActive) {
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
