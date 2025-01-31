package ktx.log

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Debug logging tag. */
const val DEBUG = "DEBUG"

/** Info logging tag. */
const val INFO = "INFO"

/** Error logging tag. */
const val ERROR = "ERROR"

/**
 * Logs a message on the debug level.
 * @param tag will proceed the message. Defaults to "DEBUG".
 * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_DEBUG
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun debug(
  tag: String = DEBUG,
  message: () -> String,
) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(tag, message())
}

/**
 * Logs a message on the debug level.
 * @param cause its stack trace will be printed.
 * @param tag will proceed the message. Defaults to "DEBUG".
 * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_DEBUG
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun debug(
  cause: Throwable,
  tag: String = DEBUG,
  message: () -> String,
) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(tag, message(), cause)
}

/**
 * Logs a message on the info level.
 * @param tag will proceed the message. Defaults to "INFO".
 * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_INFO
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun info(
  tag: String = INFO,
  message: () -> String,
) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(tag, message())
}

/**
 * Logs a message on the info level.
 * @param cause its stack trace will be printed.
 * @param tag will proceed the message. Defaults to "INFO".
 * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_INFO
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun info(
  cause: Throwable,
  tag: String = INFO,
  message: () -> String,
) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(tag, message(), cause)
}

/**
 * Logs a message on the error level.
 * @param tag will proceed the message. Defaults to "ERROR".
 * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_ERROR
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun error(
  tag: String = ERROR,
  message: () -> String,
) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(tag, message())
}

/**
 * Logs a message on the error level.
 * @param cause its stack trace will be printed.
 * @param tag will proceed the message. Defaults to "ERROR".
 * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
 *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
 * @see Application.LOG_ERROR
 * @see Application.getLogLevel
 */
@OptIn(ExperimentalContracts::class)
inline fun error(
  cause: Throwable,
  tag: String = ERROR,
  message: () -> String,
) {
  contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
  if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(tag, message(), cause)
}

/**
 * A simple logging utility class that includes its [name] in the logged messages.
 * @param name name of the logger prepended to each logged message.
 * @param debugTag will be passed as the message tag on debug logs.
 * @param infoTag will be passed as the message tag on info logs.
 * @param errorTag will be passed as the message tag on error logs.
 */
@Suppress("LeakingThis")
open class Logger(
  open val name: String,
  open val debugTag: String = DEBUG,
  open val infoTag: String = INFO,
  open val errorTag: String = ERROR,
) {
  // Implementation note: tags and some internal methods are not private as they are referenced by the inlined methods
  // and can be overridden.

  // TODO As of Kotlin 1.3, contracts are not allows in operators. Modify invoke methods to include contracts.

  /**
   * This method is called before passing the message to application logger. It can be overridden to modify the
   * pattern of the logged message. By default, it will prepend the logger name and a dash to the original message.
   * @param message original message that should be logged.
   * @return formatted message ready for logging.
   */
  open fun buildMessage(message: String): String = "$name - $message"

  /**
   * Logs a message on the info level.
   * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_INFO
   * @see Application.getLogLevel
   */
  inline operator fun invoke(message: () -> String) {
    if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, buildMessage(message()))
  }

  /**
   * Logs a message on the info level.
   * @param cause its stack trace will be printed.
   * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_INFO
   * @see Application.getLogLevel
   */
  inline operator fun invoke(
    cause: Throwable,
    message: () -> String,
  ) {
    if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, buildMessage(message()), cause)
  }

  /**
   * Logs a message on the debug level.
   * @param message inlined lambda which will be evaluated only if debug logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_DEBUG
   * @see Application.getLogLevel
   */
  @OptIn(ExperimentalContracts::class)
  inline fun debug(message: () -> String) {
    contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
    if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(debugTag, buildMessage(message()))
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
  inline fun debug(
    cause: Throwable,
    message: () -> String,
  ) {
    contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
    if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug(debugTag, buildMessage(message()), cause)
  }

  /**
   * Logs a message on the info level.
   * @param message inlined lambda which will be evaluated only if info logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_INFO
   * @see Application.getLogLevel
   */
  @OptIn(ExperimentalContracts::class)
  inline fun info(message: () -> String) {
    contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
    if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, buildMessage(message()))
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
  inline fun info(
    cause: Throwable,
    message: () -> String,
  ) {
    contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
    if (Gdx.app.logLevel >= Application.LOG_INFO) Gdx.app.log(infoTag, buildMessage(message()), cause)
  }

  /**
   * Logs a message on the error level.
   * @param message inlined lambda which will be evaluated only if error logs are currently on. The string result of this
   *    function will be created ONLY when needed, reducing the impact of creating new strings at runtime.
   * @see Application.LOG_ERROR
   * @see Application.getLogLevel
   */
  @OptIn(ExperimentalContracts::class)
  inline fun error(message: () -> String) {
    contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
    if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(errorTag, buildMessage(message()))
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
  inline fun error(
    cause: Throwable,
    message: () -> String,
  ) {
    contract { callsInPlace(message, InvocationKind.AT_MOST_ONCE) }
    if (Gdx.app.logLevel >= Application.LOG_ERROR) Gdx.app.error(errorTag, buildMessage(message()), cause)
  }
}

/**
 * Allows to create a [Logger] instance for a specific class. Note that loggers are not cached - each time this method
 * is called, a new instance of [Logger] is created, so it is advised to use loggers in companion objects.
 * @return a new instance of [Logger] which contains the passed class name in log tags.
 */
inline fun <reified T : Any> logger(): Logger = Logger(T::class.java.name)
