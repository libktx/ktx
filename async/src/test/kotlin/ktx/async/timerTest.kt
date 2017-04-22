package ktx.async

import com.badlogic.gdx.Gdx
import com.nhaarman.mockito_kotlin.mock
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests [com.badlogic.gdx.utils.Timer] extensions.
 */
class TimerTest {
  @Before
  fun `setup mock application`() {
    Gdx.app = mock()
  }

  // Most Timer.Task properties are private, unfortunately. Only scheduled status can be tested reliably.
  @Test
  fun `should schedule asynchronous task`() {
    val task = schedule(delaySeconds = 1f) {
      println("test")
    }

    assertTrue(task.isScheduled)
  }

  @Test
  fun `should schedule repeated asynchronous task`() {
    val task = interval(delaySeconds = 1f, intervalSeconds = 1f) {
      println("test")
    }

    assertTrue(task.isScheduled)
  }

  @After
  fun `clear LibGDX statics`() {
    Gdx.app = null
  }
}
