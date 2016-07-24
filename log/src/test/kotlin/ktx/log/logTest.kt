package ktx.log

import com.badlogic.gdx.*
import com.badlogic.gdx.Application.*
import com.badlogic.gdx.Application.ApplicationType.HeadlessDesktop
import com.badlogic.gdx.utils.Clipboard
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests LibGDX logging wrappers.
 * @author MJ
 */
class LogTest {
  val application = MockApplication()

  @Before
  fun clearLogs() {
    application.logLevel = LOG_DEBUG
    application.logs.clear()
  }

  @Test
  fun shouldLogDebugLogs() {
    debug { "Test." }
    assertEquals("[DEBUG]: Test.", application.logs[0])
  }

  @Test
  fun shouldLogDebugLogsWithCustomTag() {
    debug("[TRACE]") { "Test." }
    assertEquals("[TRACE]: Test.", application.logs[0])
  }

  @Test
  fun shouldLogDebugLogsWithException() {
    debug(RuntimeException("Error.")) { "Test." }
    assertEquals("[DEBUG]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldLogDebugLogsWithCustomTagAndException() {
    debug(RuntimeException("Error."), "[TRACE]") { "Test." }
    assertEquals("[TRACE]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreDebugLogsIfThisLevelIsOff() {
    application.logLevel = LOG_NONE
    var built = false
    debug {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreDebugLogsWithExceptionsIfThisLevelIsOff() {
    application.logLevel = LOG_NONE
    var built = false
    debug(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldLogInfoLogs() {
    info { "Test." }
    assertEquals("[INFO] : Test.", application.logs[0])
  }

  @Test
  fun shouldLogInfoLogsWithCustomTag() {
    info("[WARN] ") { "Test." }
    assertEquals("[WARN] : Test.", application.logs[0])
  }

  @Test
  fun shouldLogInfoLogsWithException() {
    info(RuntimeException("Error.")) { "Test." }
    assertEquals("[INFO] : Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldLogInfoLogsWithCustomTagAndException() {
    info(RuntimeException("Error."), "[WARN] ") { "Test." }
    assertEquals("[WARN] : Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreInfoLogsIfThisLevelIsOff() {
    application.logLevel = LOG_NONE
    var built = false
    info {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreInfoLogsWithExceptionsIfThisLevelIsOff() {
    application.logLevel = LOG_NONE
    var built = false
    info(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldLogErrorLogs() {
    error { "Test." }
    assertEquals("[ERROR]: Test.", application.logs[0])
  }

  @Test
  fun shouldLogErrorLogsWithCustomTag() {
    error("[FATAL]") { "Test." }
    assertEquals("[FATAL]: Test.", application.logs[0])
  }

  @Test
  fun shouldLogErrorLogsWithException() {
    error(RuntimeException("Error.")) { "Test." }
    assertEquals("[ERROR]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldLogErrorLogsWithCustomTagAndException() {
    error(RuntimeException("Error."), "[FATAL]") { "Test." }
    assertEquals("[FATAL]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreErrorLogsIfThisLevelIsOff() {
    application.logLevel = LOG_NONE
    var built = false
    error {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreErrorLogsWithExceptionsIfThisLevelIsOff() {
    application.logLevel = LOG_NONE
    var built = false
    error(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  /**
   * Used by the [Logger] tests.
   * @author MJ
   */
  class MockClass

  @Test
  fun shouldCreateLoggerForClass() {
    val logger = logger<MockClass>()
    assertEquals(MockClass::class.java.name, logger.tag)
    assertEquals("[DEBUG] ${MockClass::class.java.name}", logger.debugTag)
    assertEquals("[INFO]  ${MockClass::class.java.name}", logger.infoTag)
    assertEquals("[ERROR] ${MockClass::class.java.name}", logger.errorTag)
  }

  @Test
  fun shouldLogDebugLogsViaLogger() {
    val logger = logger<MockClass>()
    logger.debug { "Test." }
    assertEquals("[DEBUG] ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun shouldLogDebugLogsWithExceptionViaLogger() {
    val logger = logger<MockClass>()
    logger.debug(RuntimeException("Error.")) { "Test." }
    assertEquals("[DEBUG] ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreDebugLogsViaLoggerIfThisLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger.debug {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreDebugLogsWithExceptionsViaLoggerIfThisLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger.debug(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldLogInfoLogsViaLogger() {
    val logger = logger<MockClass>()
    logger.info { "Test." }
    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun shouldLogInfoLogsWithExceptionViaLogger() {
    val logger = logger<MockClass>()
    logger.info(RuntimeException("Error.")) { "Test." }
    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreInfoLogsViaLoggerIfThisLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger.info {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreInfoLogsWithExceptionsViaLoggerIfThisLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger.info(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldLogErrorLogsViaLogger() {
    val logger = logger<MockClass>()
    logger.error { "Test." }
    assertEquals("[ERROR] ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun shouldLogErrorLogsWithExceptionViaLogger() {
    val logger = logger<MockClass>()
    logger.error(RuntimeException("Error.")) { "Test." }
    assertEquals("[ERROR] ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreErrorLogsViaLoggerIfThisLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger.error {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreErrorLogsWithExceptionsViaLoggerIfThisLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger.error(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldLogInfoLogsViaLoggerOperator() {
    val logger = logger<MockClass>()
    logger { "Test." }
    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun shouldLogInfoLogsWithExceptionViaLoggerOperator() {
    val logger = logger<MockClass>()
    logger(RuntimeException("Error.")) { "Test." }
    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun shouldIgnoreInfoLogsViaLoggerOperatorIfInfoLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  @Test
  fun shouldIgnoreInfoLogsWithExceptionsViaLoggerOperatorIfInfoLevelIsOff() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE
    var built = false
    logger(RuntimeException("Error.")) {
      built = true
      "Test."
    }
    assertTrue(application.logs.isEmpty())
    assertFalse(built)
  }

  /**
   * Mocks a desktop application. Stores logs in [logs] list.
   * @author MJ
   */
  class MockApplication : Application {
    val logs = mutableListOf<String>()
    private var level: Int = LOG_DEBUG

    init {
      Gdx.app = this
    }

    override fun getLogLevel(): Int = level
    override fun setLogLevel(logLevel: Int) {
      level = logLevel
    }

    override fun debug(tag: String?, message: String?) {
      logs.add("$tag: $message")
    }

    override fun debug(tag: String?, message: String?, exception: Throwable) {
      logs.add("$tag: $message")
      logs.add(exception.message!!)
    }

    override fun log(tag: String?, message: String?) {
      logs.add("$tag: $message")
    }

    override fun log(tag: String?, message: String?, exception: Throwable) {
      logs.add("$tag: $message")
      logs.add(exception.message!!)
    }

    override fun error(tag: String?, message: String?) {
      logs.add("$tag: $message")
    }

    override fun error(tag: String?, message: String?, exception: Throwable) {
      logs.add("$tag: $message")
      logs.add(exception.message!!)
    }

    override fun getClipboard(): Clipboard? = null
    override fun getFiles(): Files? = null
    override fun getApplicationListener(): ApplicationListener? = null
    override fun removeLifecycleListener(listener: LifecycleListener?) = Unit
    override fun getPreferences(name: String?): Preferences? = null
    override fun addLifecycleListener(listener: LifecycleListener?) = Unit
    override fun getVersion(): Int = 0
    override fun postRunnable(runnable: Runnable?) = Unit
    override fun getGraphics(): Graphics? = null
    override fun getAudio(): Audio? = null
    override fun exit() = Unit
    override fun getType(): ApplicationType = HeadlessDesktop
    override fun getInput(): Input? = null
    override fun getNativeHeap(): Long = 0L
    override fun getNet(): Net? = null
    override fun getJavaHeap(): Long = 0L
  }
}
