package ktx.async

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.Timer.Task
import com.badlogic.gdx.utils.async.AsyncExecutor
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.experimental.CoroutineScope
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicReference

/** Single-threaded executor for asynchronous timed tasks. */
val scheduler = Executors.newScheduledThreadPool(1)

/**
 * Stores posted [Runnable] instances in a thread-safe queuer and allows to run them en masse. Coroutines context
 * testing utility.
 * @see `coroutine test`
 */
class TestApplication : Application by mock() {
  val queue = ConcurrentLinkedQueue<Runnable>()

  override fun postRunnable(runnable: Runnable?) {
    runnable?.let { queue.offer(it) }
  }

  fun runAll() {
    while (queue.isNotEmpty()) {
      queue.poll().run()
    }
  }
}

/**
 * Mocks static LibGDX [Timer] instance. Abstracts the [Timer] implementation away from LibGDX applications, allowing
 * to test it without running a whole LibGDX app. Provides a way to choose how asynchronous tasks are executed with
 * [onSchedule] function. Default implementation executes tasks on a separate thread.
 * @see `no delay execution`
 */
fun `with timer`(
    onSchedule: (task: Task, delay: Float) -> Unit = { task, delay ->
      scheduler.schedule(task, (delay * 1000f).toLong(), MILLISECONDS)
    },
    test: (Timer) -> Unit) {
  val timer = mock<Timer> {
    on(it.scheduleTask(any(), any())) doAnswer {
      it.getArgument<Task>(0).apply {
        onSchedule(this, it.getArgument(1))
      }
    }
  }
  Timer::class.java.getDeclaredField("instance").let {
    it.isAccessible = true
    it.set(null, timer)
  }
  try {
    test(timer)
  } finally {
    Timer::class.java.getDeclaredField("instance").let {
      it.isAccessible = true
      it.set(null, null)
    }
  }
}

/**
 * Alternative implementation of [Timer] scheduling. Immediately executes the scheduled tasks, so KTX coroutines
 * context and resume the operations without having to way.
 * @see `with timer`
 */
fun `no delay execution`() = { task: Task, _: Float -> task.run() }

/**
 * Allows to test LibGDX coroutines context by running [Runnable] instances posted to the fake [Application] instance.
 * Simulates an actual application environment with constant [Runnable] handling in a loop, without resorting to
 * blocking operations. See asyncTest.kt for usage examples.
 * @param timeLimitMillis if a test execution takes longer than that, it fails. Avoids endless loops. Defaults to 2.5s.
 * @param executorConcurrencyLevel decides whether asynchronous executor is created by the context.
 * @param test prepare control variables and mocks in the first lambda. Then open the second block by invoking first
 *    lambda parameter. The second block is a suspending coroutine body.
 * @see AsyncTest
 */
fun `coroutine test`(
    timeLimitMillis: Long = 2500L,
    executorConcurrencyLevel: Int = 0,
    test: ((suspend KtxAsync.(CoroutineScope) -> Unit) -> Unit) -> Unit) {
  val testStatus = AtomicReference<TestStatus>(TestStatus.STARTED)
  val error = AtomicReference<Throwable>()
  val application = TestApplication()
  Gdx.app = application
  enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = executorConcurrencyLevel)

  test({ coroutine ->
    ktxAsync {
      try {
        KtxAsync.coroutine(it)
        testStatus.set(TestStatus.FINISHED)
      } catch(exception: Throwable) {
        error.set(exception)
        testStatus.set(TestStatus.FAILED)
      }
    }
  })

  val startTime = System.currentTimeMillis()
  loop@ while (true) {
    when (testStatus.get()) {
      TestStatus.STARTED -> application.runAll()
      TestStatus.FINISHED -> break@loop
      TestStatus.FAILED -> throw error.get()
      null -> throw IllegalStateException()
    }
    if (System.currentTimeMillis() - startTime > timeLimitMillis) {
      throw AssertionError("Test execution time exceeded the limit of $timeLimitMillis milliseconds.")
    }
  }
}

/** Resets [KtxAsync] coroutines context with reflection. */
fun `destroy coroutines context`() {
  KtxAsync.javaClass.getDeclaredField("mainThread").set(KtxAsync, null)
  KtxAsync.javaClass.getDeclaredField("asyncExecutor").apply {
    (get(KtxAsync) as AsyncExecutor?)?.dispose()
    set(KtxAsync, null)
  }
}

/** @see `coroutine test` */
private enum class TestStatus {
  STARTED,
  FINISHED,
  FAILED
}
