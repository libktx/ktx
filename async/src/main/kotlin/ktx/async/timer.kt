package ktx.async

import com.badlogic.gdx.utils.Timer

/**
 * Simplifies [Timer] API.
 * @param delaySeconds the execution will begin after this delay.
 * @param task will be executed on the rendering thread.
 * @return callback to the task.
 */
inline fun schedule(
  delaySeconds: Float,
  crossinline task: () -> Unit,
) = Timer.schedule(
  object : Timer.Task() {
    override fun run() {
      task()
    }
  },
  delaySeconds,
)!!

/**
 * Simplifies [Timer] API.
 * @param intervalSeconds time between each execution.
 * @param delaySeconds the execution will begin after this delay. Defaults to 0.
 * @param repeatCount **additional** task executions amount. For example, repeat count of 2 causes the task to be
 * executed 3 times. Optional. If not set, task will be repeated indefinitely.
 * @param task will be repeatedly executed on the rendering thread.
 * @return callback to the task.
 */
inline fun interval(
  intervalSeconds: Float,
  delaySeconds: Float = 0f,
  repeatCount: Int = -2, // Timer.FOREVER
  crossinline task: () -> Unit,
) = Timer.schedule(
  object : Timer.Task() {
    override fun run() {
      task()
    }
  },
  delaySeconds,
  intervalSeconds,
  repeatCount,
)!!
