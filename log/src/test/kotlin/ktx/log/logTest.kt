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
 */
class LogTest {
  val application = MockApplication()

  @Before
  fun `clear logs`() {
    application.logLevel = LOG_DEBUG
    application.logs.clear()
  }

  // MockApplication logs messages to MockApplication.logs list. When given an exception, it logs user given message as
  // first log and exception message as second log. That's why tests check application.logs list.

  @Test
  fun `should log debug logs`() {
    debug { "Test." }

    assertEquals("[DEBUG]: Test.", application.logs[0])
  }

  @Test
  fun `should log debug logs with custom tag`() {
    debug("[TRACE]") { "Test." }

    assertEquals("[TRACE]: Test.", application.logs[0])
  }

  @Test
  fun `should log debug logs with exception`() {
    debug(RuntimeException("Error.")) { "Test." }

    assertEquals("[DEBUG]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should log debug logs with custom tag and exception`() {
    debug(RuntimeException("Error."), "[TRACE]") { "Test." }

    assertEquals("[TRACE]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore debug logs if level is off`() {
    application.logLevel = LOG_NONE

    debug {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore debug logs with exceptions if level is off`() {
    application.logLevel = LOG_NONE

    debug(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should log info logs`() {
    info { "Test." }

    assertEquals("[INFO] : Test.", application.logs[0])
  }

  @Test
  fun `should log info logs with custom tag`() {
    info("[WARN] ") { "Test." }

    assertEquals("[WARN] : Test.", application.logs[0])
  }

  @Test
  fun `should log info logs with exception`() {
    info(RuntimeException("Error.")) { "Test." }

    assertEquals("[INFO] : Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should log info logs with custom tag and exception`() {
    info(RuntimeException("Error."), "[WARN] ") { "Test." }

    assertEquals("[WARN] : Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore info logs if level is off`() {
    application.logLevel = LOG_NONE

    info {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore info logs with exceptions level is off`() {
    application.logLevel = LOG_NONE

    info(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should log error logs`() {
    error { "Test." }

    assertEquals("[ERROR]: Test.", application.logs[0])
  }

  @Test
  fun `should log error logs with custom tag`() {
    error("[FATAL]") { "Test." }

    assertEquals("[FATAL]: Test.", application.logs[0])
  }

  @Test
  fun `should log error logs with exception`() {
    error(RuntimeException("Error.")) { "Test." }

    assertEquals("[ERROR]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should log error logs with custom tag and exception`() {
    error(RuntimeException("Error."), "[FATAL]") { "Test." }

    assertEquals("[FATAL]: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore error logs if level is off`() {
    application.logLevel = LOG_NONE

    error {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore error logs with exceptions if level is off`() {
    application.logLevel = LOG_NONE

    error(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  /**
   * Used by the [Logger] tests.
   */
  class MockClass

  @Test
  fun `should create logger for class`() {
    val logger = logger<MockClass>()

    assertEquals(MockClass::class.java.name, logger.tag)
    assertEquals("[DEBUG] ${MockClass::class.java.name}", logger.debugTag)
    assertEquals("[INFO]  ${MockClass::class.java.name}", logger.infoTag)
    assertEquals("[ERROR] ${MockClass::class.java.name}", logger.errorTag)
  }

  @Test
  fun `should log debug logs via logger`() {
    val logger = logger<MockClass>()

    logger.debug { "Test." }

    assertEquals("[DEBUG] ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun `should log debug logs with exception via logger`() {
    val logger = logger<MockClass>()

    logger.debug(RuntimeException("Error.")) { "Test." }

    assertEquals("[DEBUG] ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore debug logs via logger if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger.debug {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore debug logs with exceptions via logger if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger.debug(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should log info logs via logger`() {
    val logger = logger<MockClass>()

    logger.info { "Test." }

    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun `should log info logs with exception via logger`() {
    val logger = logger<MockClass>()

    logger.info(RuntimeException("Error.")) { "Test." }

    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore info logs via logger if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger.info {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore info logs with exceptions via logger if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger.info(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should log error logs via logger`() {
    val logger = logger<MockClass>()

    logger.error { "Test." }

    assertEquals("[ERROR] ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun `should log error logs with exception via logger`() {
    val logger = logger<MockClass>()

    logger.error(RuntimeException("Error.")) { "Test." }

    assertEquals("[ERROR] ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore error logs via logger if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger.error {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore error logs with  exceptions via logger if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger.error(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should log info logs via logger operator`() {
    val logger = logger<MockClass>()

    logger { "Test." }

    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
  }

  @Test
  fun `should log info logs with exception via logger operator`() {
    val logger = logger<MockClass>()

    logger(RuntimeException("Error.")) { "Test." }

    assertEquals("[INFO]  ${MockClass::class.java.name}: Test.", application.logs[0])
    assertEquals("Error.", application.logs[1])
  }

  @Test
  fun `should ignore Info logs via logger operator if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  @Test
  fun `should ignore info logs with exceptions via logger operator if level is off`() {
    val logger = logger<MockClass>()
    application.logLevel = LOG_NONE

    logger(RuntimeException("Error.")) {
      fail("Should not build message.")
      "Test."
    }

    assertTrue(application.logs.isEmpty())
  }

  /**
   * Mocks a desktop application. Stores logs in [logs] list.
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
    override fun setApplicationLogger(applicationLogger: ApplicationLogger?) = Unit
    override fun getApplicationLogger(): ApplicationLogger? = null
  }
}
