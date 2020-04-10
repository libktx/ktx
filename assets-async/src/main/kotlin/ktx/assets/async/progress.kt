package ktx.assets.async

import kotlin.math.max
import kotlin.math.min
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tracks the loading progress of the [AssetStorage].
 *
 * Counts the [total], [loaded] and [failed] assets.
 *
 * [percent] allows to see current loading progress in range of [0, 1].
 *
 * The values stored by the [LoadingProgress] are _eventually consistent._
 * The progress can go slightly out of sync of the actual amounts of loaded assets,
 * as it is not protected by the [AssetStorage.lock].
 *
 * Due to the asynchronous nature of [AssetStorage], some assets that will eventually
 * be scheduled by coroutines might not be counted by [LoadingProgress] yet.
 * Calling [AssetStorage.load] or [AssetStorage.loadAsync] is not guaranteed
 * to immediately update the [total] number of assets.
 *
 * Use the [LoadingProgress] for display only and base your actual application
 * logic on [AssetStorage] API instead.
 */
class LoadingProgress {
  private val totalCounter = AtomicInteger()
  private val loadedCounter = AtomicInteger()
  private val failedCounter = AtomicInteger()

  /** Total number of scheduled assets. */
  val total: Int
    get() = totalCounter.get()
  /** Total number of successfully loaded assets. */
  val loaded: Int
    get() = loadedCounter.get()
  /** Total number of assets that failed to load. */
  val failed: Int
    get() = failedCounter.get()
  /** Current asset loading percent. Does not take [failed] assets into account. */
  val percent: Float
    get() {
      val total = max(totalCounter.get(), 0)
      val loaded = max(min(loadedCounter.get(), total), 0)
      return when {
        total == 0 || loaded == 0 -> 0f
        total == loaded -> 1f
        else -> loaded.toFloat() / total.toFloat()
      }
    }

  /**
   * True if all registered assets are loaded or failed to load.
   *
   * Remember that his value might not reflect the actual state of all assets that are being scheduled
   * due to the asynchronous nature of [AssetStorage].
   */
  val isFinished: Boolean
    get() = total > 0 && (loadedCounter.get() + failedCounter.get()) == totalCounter.get()
  /** True if there are any [failed] assets. */
  val isFailed: Boolean
    get() = failedCounter.get() > 0

  /** Must be called after a new asset was scheduled for loading. */
  internal fun registerScheduledAsset() {
    totalCounter.incrementAndGet()
  }

  /** Must be called after a new loaded asset was added manually. */
  internal fun registerAddedAsset() {
    totalCounter.incrementAndGet()
    loadedCounter.incrementAndGet()
  }

  /** Must be called after an asset has finished loading. */
  internal fun registerLoadedAsset() {
    loadedCounter.incrementAndGet()
  }

  /** Must be called after an asset failed to load successfully. */
  internal fun registerFailedAsset() {
    failedCounter.incrementAndGet()
  }

  /** Must be called after a fully loaded asset was unloaded. */
  internal fun removeLoadedAsset() {
    totalCounter.decrementAndGet()
    loadedCounter.decrementAndGet()
  }

  /** Must be called after an asset currently in the process of loading was cancelled and unloaded. */
  internal fun removeScheduledAsset() {
    totalCounter.decrementAndGet()
  }

  /** Must be called after an unsuccessfully loaded asset was unloaded. */
  internal fun removeFailedAsset() {
    totalCounter.decrementAndGet()
    failedCounter.decrementAndGet()
  }

  /** Must be called after disposal of all assets. */
  internal fun reset() {
    totalCounter.set(0)
    loadedCounter.set(0)
    failedCounter.set(0)
  }
}
