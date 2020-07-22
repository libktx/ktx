package ktx.tools

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

/**
 * Returns this [SLF4J Logger][org.slf4j.Logger] as a [Gradle API Logger][Logger], wrapping it if necessary. If it is
 * wrapped, the `QUIET` log level is mapped to `WARN` and the `LIFECYCLE` log level is mapped to `INFO`.
 */
fun org.slf4j.Logger.toGradleLogger(): Logger {
  return this as? Logger ?: object : Logger, org.slf4j.Logger by this {

    override fun log(level: LogLevel, message: String) = when (level) {
      LogLevel.DEBUG -> debug(message)
      LogLevel.INFO -> info(message)
      LogLevel.LIFECYCLE -> lifecycle(message)
      LogLevel.WARN -> warn(message)
      LogLevel.QUIET -> quiet(message)
      LogLevel.ERROR -> error(message)
    }

    override fun log(level: LogLevel, message: String, vararg objects: Any) = when (level) {
      LogLevel.DEBUG -> debug(message, *objects)
      LogLevel.INFO -> info(message, *objects)
      LogLevel.LIFECYCLE -> lifecycle(message, *objects)
      LogLevel.WARN -> warn(message, *objects)
      LogLevel.QUIET -> quiet(message, *objects)
      LogLevel.ERROR -> error(message, *objects)
    }

    override fun log(level: LogLevel, message: String, throwable: Throwable) = when (level) {
      LogLevel.DEBUG -> debug(message, throwable)
      LogLevel.INFO -> info(message, throwable)
      LogLevel.LIFECYCLE -> lifecycle(message, throwable)
      LogLevel.WARN -> warn(message, throwable)
      LogLevel.QUIET -> quiet(message, throwable)
      LogLevel.ERROR -> error(message, throwable)
    }

    override fun isEnabled(level: LogLevel): Boolean = when (level) {
      LogLevel.DEBUG -> isDebugEnabled
      LogLevel.INFO -> isInfoEnabled
      LogLevel.LIFECYCLE -> isLifecycleEnabled
      LogLevel.WARN -> isWarnEnabled
      LogLevel.QUIET -> isQuietEnabled
      LogLevel.ERROR -> isErrorEnabled
    }

    override fun lifecycle(message: String) = info(message)

    override fun lifecycle(message: String, vararg objects: Any) = info(message, *objects)

    override fun lifecycle(message: String, throwable: Throwable) = info(message, throwable)

    override fun isLifecycleEnabled(): Boolean = isInfoEnabled

    override fun quiet(message: String) = warn(message)

    override fun quiet(message: String, vararg objects: Any) = warn(message, *objects)

    override fun quiet(message: String, throwable: Throwable) = warn(message, throwable)

    override fun isQuietEnabled(): Boolean = isWarnEnabled
  }
}
