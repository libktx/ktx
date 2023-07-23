package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.PerformanceCounter

/**
 * Profiles the given [operation] using a [PerformanceCounter].
 * The operation will be repeated [repeats] times to gather the performance data.
 * [PerformanceCounter.tick] will be called after each operation.
 * [repeats] will be used to set the window size of the [PerformanceCounter].
 * If [printResults] is set to true, a short summary will be printed by the application.
 *
 * [PerformanceCounter] used for the profiling will be returned, so that the profiling
 * data can be analyzed and further tests can be performed. Note that to perform further
 * profiling with this [PerformanceCounter] of a different operation,
 * [PerformanceCounter.reset] should be called.
 */
inline fun profile(
  name: String = "Profiler",
  repeats: Int = 10,
  printResults: Boolean = true,
  operation: () -> Unit,
): PerformanceCounter {
  val performanceCounter = PerformanceCounter(name, repeats)
  performanceCounter.profile(repeats, printResults, operation)
  return performanceCounter
}

/**
 * Profiles the given [operation] using this [PerformanceCounter].
 * The operation will be repeated [repeats] times to gather the performance data.
 * [PerformanceCounter.tick] will be called after each operation.
 * By default, [repeats] is set to the window size passed to the [PerformanceCounter]
 * constructor or 10 if the window size is set to 1.
 * If [printResults] is set to true, a short summary will be printed by the application.
 *
 * Note that to perform further profiling with this [PerformanceCounter] of a different
 * operation, [PerformanceCounter.reset] should be called.
 */
inline fun PerformanceCounter.profile(
  repeats: Int = if (time.mean != null) time.mean.windowSize else 10,
  printResults: Boolean = true,
  operation: () -> Unit,
) {
  if (this.time.count == 0) tick()
  repeat(repeats) {
    this.start()
    operation()
    this.stop()
    this.tick()
  }
  if (printResults) {
    prettyPrint()
  }
}

/**
 * Logs profiling information of this [PerformanceCounter] as an organized block.
 * Uses passed [decimalFormat] to format floating point numbers.
 */
fun PerformanceCounter.prettyPrint(decimalFormat: String = "%.6fs") {
  Gdx.app.log(name, "--------------------------------------------")
  Gdx.app.log(name, "Number of repeats: ${time.count}")
  val mean = time.mean
  val minimum: Float
  val maximum: Float
  if (mean != null && mean.hasEnoughData()) {
    Gdx.app.log(
      name,
      "Average OP time: ${decimalFormat.format(mean.mean)} " +
        "± ${decimalFormat.format(mean.standardDeviation())}",
    )
    minimum = mean.lowest
    maximum = mean.highest
  } else {
    Gdx.app.log(name, "Average OP time: ${decimalFormat.format(time.average)}")
    minimum = time.min
    maximum = time.max
  }
  Gdx.app.log(name, "Minimum OP time: ${decimalFormat.format(minimum)}")
  Gdx.app.log(name, "Maximum OP time: ${decimalFormat.format(maximum)}")
  Gdx.app.log(name, "--------------------------------------------")
}
