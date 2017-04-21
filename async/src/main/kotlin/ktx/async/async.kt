package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net.HttpRequest
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.async.AsyncExecutor
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import ktx.async.KtxAsync.isOnRenderingThread
import java.io.InputStream
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.ContinuationInterceptor

/**
 * Uses LibGDX threading model to support Kotlin coroutines. All basic operations are executed on the main rendering
 * thread, resuming with [com.badlogic.gdx.Application.postRunnable] when necessary. Optional [AsyncExecutor] is
 * provided for execution of asynchronous tasks. Uses [com.badlogic.gdx.utils.Timer] API for non-blocking delays and
 * [com.badlogic.gdx.Net.HttpRequest] API for asynchronous HTTP requests executed on a separate thread. Allows to check
 * if the current action is executed on the rendering thread with [isOnRenderingThread].
 */
object KtxAsync : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  /** Main rendering thread. Do not modify manually.
   * @see initiate */
  lateinit var mainThread: Thread
    internal set
  /** [AsyncExecutor] instance used to execute tasks outside of main rendering thread.
   * @see createExecutor */
  lateinit var asyncExecutor: AsyncExecutor
    internal set

  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T>
      = KtxContinuation(continuation)

  /** Call this method _once_ on the main rendering thread of the application.
   * @see enableKtxCoroutines */
  internal fun initiate() {
    mainThread = Thread.currentThread()
  }

  /** Initiates [AsyncExecutor] used for [asynchronous] operations.
   * @see enableKtxCoroutines */
  internal fun createExecutor(concurrencyLevel: Int = 1) {
    asyncExecutor = AsyncExecutor(concurrencyLevel)
  }

  /** @return true if current thread is the main rendering thread of the application. */
  fun isOnRenderingThread() = Thread.currentThread() == mainThread

  /** Suspends the current execution. Schedules an asynchronous task with LibGDX [Timer] API, which resumes the
   * execution after the chosen delay has passed.
   * @param seconds delay used to schedule [Timer.Task]. If not higher than zero, coroutine will be resumed without
   *    delay. */
  suspend fun delay(seconds: Float): Unit = suspendCancellableCoroutine { continuation ->
    if (seconds > 0f) {
      schedule(delaySeconds = seconds) {
        if (continuation.isActive) {
          continuation.resume(Unit)
        }
      }
    } else if (continuation.isActive) {
      continuation.resume(Unit)
    }
  }

  /** Suspends the execution of the coroutine until the next application rendering frame. Resumes the execution
   * using _Gdx.app.postRunnable_ mechanism.
   *
   * This method should be used only **directly** in the coroutines launched in [KtxAsync] context - the behavior when
   * called from within asynchronous blocks (e.g. [asynchronous]) is undefined, and might change the execution thread
   * to the main rendering thread, modifying the original suspending method behavior.
   *
   * _Note:_ due to [com.badlogic.gdx.Application] implementations on some platforms, this method might not skip frame
   * on the first call in `create` method of application listener, as all runnables are executed before actually
   * starting the rendering. It should work as expected otherwise. */
  suspend fun skipFrame(): Unit = suspendCancellableCoroutine { continuation ->
    Gdx.app.postRunnable {
      if (continuation.isActive) {
        continuation.resume(Unit)
      }
    }
  }

  /**
   * Suspends execution and submits a task to an [AsyncExecutor]. After the task is finished, resumes the execution on
   * the rendering thread.
   * @param executor will execute the asynchronous action. Defaults to [asyncExecutor].
   * @param action inlined. Any thrown exceptions are caught and rethrown on the rendering thread.
   */
  suspend fun <Result> asynchronous(
      executor: AsyncExecutor = asyncExecutor,
      action: () -> Result): Result = suspendCancellableCoroutine { continuation ->
    executor.submit {
      if (continuation.isActive) {
        try {
          continuation.resume(action())
        } catch (exception: Throwable) {
          continuation.resumeWithException(exception)
        }
      }
    }
  }

  /**
   * Executes a HTTP request asynchronously.
   * @param url URL that will be queried.
   * @param method HTTP method. GET by default. See [com.badlogic.gdx.Net.HttpMethods].
   * @param headers HTTP request headers mapped to their values. Empty by default.
   * @param timeout time before the request is cancelled. If the request was cancelled, an exception will be thrown
   *    (type of exception might vary on each platform).
   * @param content body of the request.
   * @param contentStream body of the request. Alternative to [content]. Pair of an [InputStream] along with its size.
   * @param followRedirects whether 301 and 302 redirects are followed. Defaults to true.
   * @param includeCredentials whether a cross-origin request will include credentials. Relevant only on web platforms.
   * @param onCancel executed when the HTTP request is cancelled through coroutine cancellation. Optional, should be
   *    passed only if coroutine's [Job.cancel] can be called.
   * @return [HttpRequestResult] storing HTTP response data.
   * @see HttpRequest
   * */
  suspend fun httpRequest(
      url: String,
      method: String = "GET",
      headers: Map<String, String> = emptyMap(),
      timeout: Int = 0,
      content: String? = null,
      contentStream: Pair<InputStream, Long>? = null,
      followRedirects: Boolean = true,
      includeCredentials: Boolean = false,
      onCancel: ((HttpRequest) -> Unit)? = null
  ): HttpRequestResult = suspendCancellableCoroutine { continuation ->
    val httpRequest = HttpRequest(method).apply {
      this.url = url
      this.timeOut = timeout
      this.content = content
      this.followRedirects = followRedirects
      this.includeCredentials = includeCredentials
      contentStream?.let { setContent(it.first, it.second) }
      headers.forEach { header, value -> setHeader(header, value) }
    }
    val listener = KtxHttpResponseListener(httpRequest, continuation, onCancel)
    Gdx.net.sendHttpRequest(httpRequest, listener)
    continuation.invokeOnCompletion {
      if (continuation.isCancelled && !listener.completed) {
        Gdx.net.cancelHttpRequest(httpRequest)
      }
    }
  }
}

/**
 * Executes operations on the main rendering thread of the application.
 */
private class KtxContinuation<in T>(val continuation: Continuation<T>) : Continuation<T> by continuation {
  override fun resume(value: T) {
    if (KtxAsync.isOnRenderingThread()) {
      continuation.resume(value)
    } else {
      Gdx.app.postRunnable {
        continuation.resume(value)
      }
    }
  }

  override fun resumeWithException(exception: Throwable) {
    if (KtxAsync.isOnRenderingThread()) {
      continuation.resumeWithException(exception)
    } else {
      Gdx.app.postRunnable {
        continuation.resumeWithException(exception)
      }
    }
  }
}

/** Must be called _before_ using any features implemented using Kotlin coroutines API. Must be called on the main
 * rendering thread.
 * @param asynchronousExecutorConcurrencyLevel if this value is higher than 0, an [AsyncExecutor] with the selected
 *    amount of threads will be constructed to execute tasks outside of the main rendering thread. The initiation is
 *    necessary only if [KtxAsync.asynchronous] method is used.
 * @param validate if true, a validation task will be scheduled via [com.badlogic.gdx.Application.postRunnable]. If the
 *    KTX coroutines was not initiated on the main rendering thread and your application was otherwise setup correctly,
 *    it should throw an exception on the main thread. */
fun enableKtxCoroutines(
    asynchronousExecutorConcurrencyLevel: Int = 0,
    validate: Boolean = true) {
  Gdx.app != null || throw IllegalStateException("Cannot create KTX coroutines context before the LibGDX application.")
  KtxAsync.initiate()
  if (asynchronousExecutorConcurrencyLevel > 0) {
    KtxAsync.createExecutor(asynchronousExecutorConcurrencyLevel)
  }
  if (validate) {
    Gdx.app.postRunnable {
      KtxAsync.isOnRenderingThread() || throw GdxRuntimeException("KTX coroutines context was not initiated on the " +
          "main rendering thread. Thread which initiated the context: ${KtxAsync.mainThread}, thread performing the " +
          "context validation check: ${Thread.currentThread()}.")
    }
  }
}

/**
 * A convenience wrapper over [launch] call. Uses [KtxAsync] coroutines context.
 * @param action executed as a coroutine. Might be suspended and executed on the main rendering thread. May use
 *    asynchronous operations provided by [KtxAsync] API.
 * @return [Job] instance, which can be used to cancel, check on or wait for the coroutine execution.
 */
fun ktxAsync(action: suspend KtxAsync.(scope: CoroutineScope) -> Unit): Job =
    launch(KtxAsync) {
      KtxAsync.action(this)
    }
