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
import kotlinx.coroutines.experimental.Job
import org.junit.Assert.assertTrue
import java.util.concurrent.*
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicReference

/** Single-threaded executor for asynchronous timed tasks. */
val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

/**
 * Stores posted [Runnable] instances in a thread-safe queuer and allows to run them en masse. Coroutines context
 * testing utility.
 * @see `coroutine test`
 */
class TestApplication : Application by mock() {
  var frameId = 0
  val queue = ConcurrentLinkedQueue<Runnable>()

  override fun postRunnable(runnable: Runnable?) {
    runnable?.let { queue.offer(it) }
  }

  fun runAll() {
    frameId++
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
 * @param concurrencyLevel decides whether asynchronous executor is created by the context.
 * @param test prepare control variables and mocks in the first lambda. Then open the second block by invoking first
 *    lambda parameter (ktxAsync) - this is the tested suspending coroutine body. Supports multiple coroutines.
 * @see AsyncTest
 */
fun `coroutine test`(
    timeLimitMillis: Long = 2500L,
    concurrencyLevel: Int = 0,
    test: (ktxAsync: (suspend KtxAsync.(CoroutineScope) -> Unit) -> Unit) -> Unit) {
  val testStatuses = ConcurrentHashMap<Any, TestStatus>()
  val error = AtomicReference<Throwable>()
  val application = TestApplication()
  Gdx.app = application
  enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = concurrencyLevel)

  test({ coroutine ->
    testStatuses[coroutine] = TestStatus.STARTED
    ktxAsync {
      try {
        KtxAsync.coroutine(it)
        testStatuses[coroutine] = TestStatus.FINISHED
      } catch(exception: Throwable) {
        error.set(exception)
        testStatuses[coroutine] = TestStatus.FAILED
      }
    }
  })

  val startTime = System.currentTimeMillis()
  while (true) {
    var finishedCount = 0
    testStatuses.values.forEach {
      when (it) {
        TestStatus.STARTED -> application.runAll()
        TestStatus.FINISHED -> finishedCount++
        TestStatus.FAILED -> throw error.get()
      }
    }
    if (finishedCount == testStatuses.size) break
    if (System.currentTimeMillis() - startTime > timeLimitMillis) {
      throw AssertionError("Test execution time exceeded the limit of $timeLimitMillis milliseconds.")
    }
  }
}

/**
 * Allows to test LibGDX coroutines cancelling by running [Runnable] instances posted to the fake [Application]
 * instance. Simulates an actual application environment with constant [Runnable] handling in a loop, without resorting
 * to blocking operations. Allows to choose total test duration, after which the tested test should have finished even
 * if not properly cancelled (failing the test) and cancellation delay. Contrary to [coroutine test], this utility runs
 * for the whole given test duration to make sure that the coroutines are cancelled properply. See asyncTest.kt or
 * httpTest.kt for usage examples.
 * @param testDurationMillis total estimated time needed to execute the whole coroutine without cancellation.
 * @param cancelAfterMillis delay after which the coroutine is cancelled.
 * @param concurrencyLevel decides whether asynchronous executor is created by the context.
 * @param test prepare control variables and mocks in the first lambda. Then open the second block by invoking first
 *      lambda parameter (ktxAsync) - this is a suspending coroutine body. Optionally open third block by invoking
 *      second lambda parameter (assert) to perform additional checks after the coroutines are fully executed. Does NOT
 *      support couroutines.
 * @see AsyncTest
 * @see AsynchronousHttpRequestsTest
 */
fun `cancelled coroutine test`(
    testDurationMillis: Long = 100L,
    cancelAfterMillis: Long = 10L,
    concurrencyLevel: Int = 0,
    test: (ktxAsync: (suspend KtxAsync.(CoroutineScope) -> Unit) -> Unit,
           assert: ((() -> Unit)) -> Unit) -> Unit) {
  val testStatus = AtomicReference<TestStatus>(TestStatus.STARTED)
  val error = AtomicReference<Throwable>()
  val job = AtomicReference<Job>()
  val assert = AtomicReference<() -> Unit>()
  val application = TestApplication()
  var cancellationExceptionThrown = false
  Gdx.app = application
  enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = concurrencyLevel)

  test({ coroutine ->
    job.set(ktxAsync {
      try {
        KtxAsync.coroutine(it)
        testStatus.set(TestStatus.FINISHED)
      } catch(exception: CancellationException) {
        cancellationExceptionThrown = true
      } catch(exception: Throwable) {
        error.set(exception)
        testStatus.set(TestStatus.FAILED)
      }
    })
  }, { assertionTask -> assert.set(assertionTask) })

  var cancelled = false
  val startTime = System.currentTimeMillis()
  loop@ while (true) {
    val totalTime = System.currentTimeMillis() - startTime
    if (!cancelled && totalTime >= cancelAfterMillis && job.get() != null) {
      job.get().cancel()
      cancelled = true
    }
    when (testStatus.get()) {
      TestStatus.STARTED -> application.runAll()
      TestStatus.FINISHED -> break@loop
      TestStatus.FAILED -> throw error.get()
      null -> throw IllegalStateException()
    }
    if (totalTime > testDurationMillis) {
      break
    }
  }

  assertTrue("This test expects that the coroutine is cancelled.", cancellationExceptionThrown)
  assert.get()?.invoke()
}

/** @see `coroutine test` */
private enum class TestStatus {
  STARTED,
  FINISHED,
  FAILED
}

/** Resets [KtxAsync] coroutines context with reflection. */
fun `destroy coroutines context`() {
  KtxAsync.javaClass.getDeclaredField("mainThread").set(KtxAsync, null)
  KtxAsync.javaClass.getDeclaredField("asyncExecutor").apply {
    (get(KtxAsync) as AsyncExecutor?)?.dispose()
    set(KtxAsync, null)
  }
}
