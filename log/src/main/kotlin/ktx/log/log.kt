package ktx.log

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Logs a message on the debug level.
 * @param tag will proceed the message. Defaults to "DEBUG" in square braces.
 * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_DEBUG
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun debug(tag: String = "[DEBUG]", message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(tag, message())
}

/**
 * Logs a message on the debug level.
 * @param cause its stack trace will be printed.
 * @param tag will proceed the message. Defaults to "DEBUG" in square braces.
 * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_DEBUG
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun debug(cause: Throwable, tag: String = "[DEBUG]", message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(tag, message(), cause)
}

/**
 * Logs a message on the info level.
 * @param tag will proceed the message. Defaults to "INFO" in square braces.
 * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_INFO
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun info(tag: String = "[INFO] ", message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(tag, message())
}

/**
 * Logs a message on the info level.
 * @param cause its stack trace will be printed.
 * @param tag will proceed the message. Defaults to "INFO" in square braces.
 * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_INFO
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun info(cause: Throwable, tag: String = "[INFO] ", message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(tag, message(), cause)
}

/**
 * Logs a message on the error level.
 * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_ERROR
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun error(tag: String = "[ERROR]", message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(tag, message())
}

/**
 * Logs a message on the error level.
 * @param cause its stack trace will be printed.
 * @param tag will proceed the message. Defaults to "ERROR" in square braces.
 * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_ERROR
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun error(cause: Throwable, tag: String = "[ERROR]", message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(tag, message(), cause)
}

/**
 * A simple logging utility class which caches its tags.
 * @param tag name of the logger included in log tags. Proceeded with a prefix of the specified log type.
 * @param debugPrefix will proceed [tag] in debug logs.
 * @param infoPrefix will proceed [tag] in info logs.
 * @param errorPrefix will proceed [tag] in error logs.
 */
@Suppress("LeakingThis")
open class Logger(
  open val tag: String,
  debugPrefix: String = "[DEBUG] ",
  infoPrefix: String = "[INFO]  ",
  errorPrefix: String = "[ERROR] "
) {
  // Implementation note: tags are not private as they are referenced by the inlined methods.
  /**
   * Will proceed all debug logs.
   */
  open val debugTag = "$debugPrefix$tag"

  /**
   * Will proceed all info logs.
   */
  open val infoTag = "$infoPrefix$tag"

  /**
   * Will proceed all error logs.
   */
  open val errorTag = "$errorPrefix$tag"

  // TODO As of Kotlin 1.3, contracts are not allows in operators. Modify invoke methods to include contracts.

  /**
   * Logs a message on the info level.
   * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_INFO
   * @see Application.getLogLevel
   */
  inline operator fun invoke(message: () -> String) {
    if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, message())
  }

  /**
   * Logs a message on the info level.
   * @param cause its stack trace will be printed.
   * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_INFO
   * @see Application.getLogLevel
   */
  inline operator fun invoke(cause: Throwable, message: () -> String) {
    if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, message(), cause)
  }
}

// TODO In Kotlin 1.4, inlined Logger extension methods should be added directly to the Logger class.

/**
 * Logs a message on the debug level.
 * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_DEBUG
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun Logger.debug(message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(debugTag, message())
}

/**
 * Logs a message on the debug level.
 * @param cause its stack trace will be printed.
 * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_DEBUG
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun Logger.debug(cause: Throwable, message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(debugTag, message(), cause)
}

/**
 * Logs a message on the info level.
 * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_INFO
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun Logger.info(message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, message())
}

/**
 * Logs a message on the info level.
 * @param cause its stack trace will be printed.
 * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_INFO
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun Logger.info(cause: Throwable, message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, message(), cause)
}

/**
 * Logs a message on the error level.
 * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_ERROR
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun Logger.error(message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(errorTag, message())
}

/**
 * Logs a message on the error level.
 * @param cause its stack trace will be printed.
 * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_ERROR
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun Logger.error(cause: Throwable, message: () -> String) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(errorTag, message(), cause)
}

/**
 * Allows to create a [Logger] instance for a specific class. Note that loggers are not cached - each time this method
 * is called, a new instance of [Logger] is created, so it is advised to use loggers in companion objects.
 * @return a new instance of [Logger] which contains the passed class name in log tags.
 */
inline fun <reified T : Any> logger(): Logger = Logger(T::class.java.name)
