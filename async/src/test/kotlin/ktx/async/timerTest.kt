package ktx.async

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests [com.badlogic.gdx.utils.Timer] extensions.
 */
class TimerTest : AsyncTest() {
  // Most Timer.Task properties are private, unfortunately. Only scheduled status can be tested reliably.
  @Test
  fun `should schedule asynchronous task`() {
    // When:
    val task = schedule(delaySeconds = 1f) {}

    // Then:
    assertTrue(task.isScheduled)
  }

  @Test
  fun `should execute asynchronous task`() {
    // Given:
    val result = AtomicInteger()

    // When:
    schedule(delaySeconds = 0.05f) {
      result.set(5)
    }

    // Then:
    delay(0.1f)
    assertEquals(5, result.get())
  }

  @Test
  fun `should cancel asynchronous task`() {
    // Given:
    val executed = AtomicBoolean()
    val task = schedule(delaySeconds = 0.05f) { executed.set(true) }

    // When:
    task.cancel()

    // Then:
    delay(0.1f)
    assertFalse(executed.get())
  }

  @Test
  fun `should schedule repeated asynchronous task`() {
    // When:
    val task = interval(delaySeconds = 1f, intervalSeconds = 1f) {}

    // Then:
    assertTrue(task.isScheduled)
  }

  @Test
  fun `should execute repeated asynchronous task`() {
    // Given:
    val executions = AtomicInteger()

    // When:
    interval(delaySeconds = 0f, intervalSeconds = 0.05f) {
      executions.incrementAndGet()
    }

    // Then:
    delay(0.2f)
    assert(executions.get() > 2)
  }

  @Test
  fun `should execute repeated asynchronous task N times`() {
    // Given:
    val executions = AtomicInteger()

    // When:
    interval(delaySeconds = 0f, intervalSeconds = 0.05f, repeatCount = 1) {
      executions.incrementAndGet()
    }

    // Then: executions count should be equal to 1+repeatCount (initial execution + repetitions).
    delay(0.2f)
    assertEquals(executions.get(), 2)
  }
}
