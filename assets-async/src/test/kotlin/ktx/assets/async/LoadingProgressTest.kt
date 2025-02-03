package ktx.assets.async

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadingProgressTest {
  @Test
  fun `should increase count of scheduled assets`() {
    // Given:
    val progress = LoadingProgress()

    // When:
    progress.registerScheduledAsset()

    // Then:
    assertEquals(1, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should increase count of loaded assets`() {
    // Given:
    val progress = LoadingProgress()
    progress.registerScheduledAsset()

    // When:
    progress.registerLoadedAsset()

    // Then:
    assertEquals(1, progress.total)
    assertEquals(1, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should increase count of added assets`() {
    // Given:
    val progress = LoadingProgress()

    // When:
    progress.registerAddedAsset()

    // Then:
    assertEquals(1, progress.total)
    assertEquals(1, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should increase count of failed assets`() {
    // Given:
    val progress = LoadingProgress()
    progress.registerScheduledAsset()

    // When:
    progress.registerFailedAsset()

    // Then:
    assertEquals(1, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(1, progress.failed)
  }

  @Test
  fun `should decrease count of scheduled assets`() {
    // Given:
    val progress = LoadingProgress()
    progress.registerScheduledAsset()

    // When:
    progress.removeScheduledAsset()

    // Then:
    assertEquals(0, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should decrease count of loaded assets`() {
    // Given:
    val progress = LoadingProgress()
    progress.registerScheduledAsset()
    progress.registerLoadedAsset()

    // When:
    progress.removeLoadedAsset()

    // Then:
    assertEquals(0, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should decrease count of added assets`() {
    // Given:
    val progress = LoadingProgress()
    progress.registerAddedAsset()

    // When:
    progress.removeLoadedAsset()

    // Then:
    assertEquals(0, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should decrease count of failed assets`() {
    // Given:
    val progress = LoadingProgress()
    progress.registerScheduledAsset()
    progress.registerFailedAsset()

    // When:
    progress.removeFailedAsset()

    // Then:
    assertEquals(0, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(0, progress.failed)
  }

  @Test
  fun `should report progress`() {
    // Given:
    val progress = LoadingProgress()

    // When:
    repeat(5) { progress.registerScheduledAsset() }
    // Then:
    assertEquals(0f, progress.percent)
    assertFalse(progress.isFinished)
    assertFalse(progress.isFailed)

    // When:
    progress.registerLoadedAsset()
    // Then:
    assertEquals(0.2f, progress.percent)
    assertFalse(progress.isFinished)
    assertFalse(progress.isFailed)

    // When:
    progress.registerLoadedAsset()
    // Then:
    assertEquals(0.4f, progress.percent)
    assertFalse(progress.isFinished)
    assertFalse(progress.isFailed)

    // When:
    progress.registerLoadedAsset()
    // Then:
    assertEquals(0.6f, progress.percent)
    assertFalse(progress.isFinished)
    assertFalse(progress.isFailed)

    // When:
    progress.registerLoadedAsset()
    // Then:
    assertEquals(0.8f, progress.percent)
    assertFalse(progress.isFinished)
    assertFalse(progress.isFailed)

    // When:
    progress.registerLoadedAsset()
    // Then:
    assertEquals(1f, progress.percent)
    assertTrue(progress.isFinished)
    assertFalse(progress.isFailed)
  }

  @Test
  fun `should report progress within 0, 1 range even when out of sync`() {
    // Given:
    val progress = LoadingProgress()

    // When: loaded assets count is higher than scheduled assets:
    progress.registerScheduledAsset()
    progress.registerLoadedAsset()
    progress.registerLoadedAsset()

    // Then:
    assertEquals(1f, progress.percent)
  }

  @Test
  fun `should not include failed assets in progress`() {
    // Given:
    val progress = LoadingProgress()
    repeat(4) { progress.registerScheduledAsset() }
    repeat(3) { progress.registerLoadedAsset() }

    // When:
    progress.registerFailedAsset()

    // Then:
    assertEquals(0.75f, progress.percent)
    assertTrue(progress.isFailed)
    assertTrue(progress.isFinished)
  }

  @Test
  fun `should reset the progress`() {
    // Given:
    val progress = LoadingProgress()
    repeat(100) { progress.registerScheduledAsset() }
    repeat(75) { progress.registerLoadedAsset() }
    repeat(25) { progress.registerFailedAsset() }
    repeat(50) { progress.registerAddedAsset() }

    // When:
    progress.reset()

    // Then:
    assertEquals(0, progress.total)
    assertEquals(0, progress.loaded)
    assertEquals(0, progress.failed)
    assertFalse(progress.isFinished)
    assertFalse(progress.isFailed)
  }

  @Test
  @OptIn(DelicateCoroutinesApi::class)
  fun `should handle concurrent updates`() {
    // Given:
    val progress = LoadingProgress()

    // When:
    val jobs =
      (1..1000).map { id ->
        GlobalScope.launch {
          when (id % 4) {
            0 -> progress.registerScheduledAsset()
            1 -> progress.registerLoadedAsset()
            2 -> progress.registerFailedAsset()
            3 -> progress.registerAddedAsset()
          }
        }
      }

    // Then:
    runBlocking { jobs.joinAll() }
    assertEquals(500, progress.total)
    assertEquals(500, progress.loaded)
    assertEquals(250, progress.failed)
  }

  @Test
  @OptIn(DelicateCoroutinesApi::class)
  fun `should handle concurrent registration and removal`() {
    // Given:
    val progress = LoadingProgress()

    // When:
    val jobs =
      (1..1000).map { id ->
        GlobalScope.launch {
          when (id % 4) {
            0 -> progress.registerScheduledAsset()
            1 -> progress.registerLoadedAsset()
            2 -> progress.registerFailedAsset()
            3 -> progress.registerAddedAsset()
          }
          when (id % 5) {
            0 -> progress.removeScheduledAsset()
            1 -> progress.removeLoadedAsset()
            2 -> progress.removeFailedAsset()
            else -> progress.registerScheduledAsset()
          }
        }
      }

    // Then:
    runBlocking { jobs.joinAll() }
    assertEquals((900 - 600), progress.total)
    assertEquals((500 - 200), progress.loaded)
    assertEquals((250 - 200), progress.failed)
  }
}
