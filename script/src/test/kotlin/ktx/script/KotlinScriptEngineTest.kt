package ktx.script

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files
import io.kotlintest.matchers.shouldThrow
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.BeforeClass
import org.junit.Test
import java.lang.ClassCastException
import kotlin.reflect.KClass
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests the [KotlinScriptEngine].
 */
class KotlinScriptEngineTest {
  data class Data(
    var text: String,
  )

  private val engine = KotlinScriptEngine()

  companion object {
    @JvmStatic
    @BeforeClass
    fun `initiate libGDX files`() {
      Gdx.files = Lwjgl3Files()
    }

    @JvmStatic
    @AfterClass
    fun `remove libGDX files`() {
      Gdx.files = null
    }
  }

  @Test
  fun `should create Kotlin scripting engine`() {
    // When:
    val scriptEngine = engine.engine

    // Then:
    assertEquals("kotlin", scriptEngine.factory.languageName)
  }

  @Test
  fun `should import class`() {
    // When:
    engine.import("ktx.script.KotlinScriptEngineTest.Data")

    // Then:
    assertEquals(Data("test"), engine.evaluateAs<Data>("""Data("test")"""))
  }

  @Test
  fun `should import class with alias`() {
    // When:
    engine.import("ktx.script.KotlinScriptEngineTest.Data", alias = "Alias")

    // Then:
    assertEquals(Data("test"), engine.evaluateAs<Data>("""Alias("test")"""))
  }

  @Test
  fun `should import classes`() {
    // When:
    engine.importAll("ktx.script.KotlinScriptEngineTest.Data")

    // Then:
    assertEquals(Data("test"), engine.evaluateAs<Data>("""Data("test")"""))
  }

  @Test
  fun `should import classes with aliases`() {
    // When:
    engine.importAll("ktx.script.KotlinScriptEngineTest.Data as Alias")

    // Then:
    assertEquals(Data("test"), engine.evaluateAs<Data>("""Alias("test")"""))
  }

  @Test
  fun `should import classes from iterable`() {
    // When:
    engine.importAll(listOf("ktx.script.KotlinScriptEngineTest.Data"))

    // Then:
    assertEquals(Data("test"), engine.evaluateAs<Data>("""Data("test")"""))
  }

  @Test
  fun `should import classes with aliases from iterable`() {
    // When:
    engine.importAll(listOf("ktx.script.KotlinScriptEngineTest.Data as Alias"))

    // Then:
    assertEquals(Data("test"), engine.evaluateAs<Data>("""Alias("test")"""))
  }

  @Test
  fun `should set script package`() {
    // When:
    engine.setPackage("ktx.script")

    // Then: should access classes without an import:
    assertEquals(KotlinScriptEngineTest::class, engine.evaluateAs<KClass<*>>("KotlinScriptEngineTest::class"))
  }

  @Test
  fun `should evaluate script`() {
    // When:
    val result = engine.evaluate("42")

    // Then:
    assertEquals(42, result)
  }

  @Test
  fun `should throw exception if unable to evaluate script`() {
    // Expect:
    shouldThrow<ScriptEngineException> {
      engine.evaluate("import")
    }
  }

  @Test
  fun `should evaluate script from file`() {
    // Given:
    val file = Gdx.files.classpath("ktx/script/script.kts")

    // When:
    val result = engine.evaluate(file)

    // Then:
    assertEquals("test", result)
  }

  @Test
  fun `should throw exception if unable to evaluate script from file`() {
    // Given:
    val file = Gdx.files.classpath("ktx/script/broken.script")

    // Expect:
    shouldThrow<ScriptEngineException> {
      engine.evaluate(file)
    }
  }

  @Test
  fun `should evaluate script and return its result`() {
    // When:
    val result = engine.evaluateAs<Int>("42")

    // Then:
    assertEquals(42, result)
  }

  @Test
  fun `should throw exception if the script result does not match the expected type`() {
    // Expect:
    shouldThrow<ClassCastException> {
      engine.evaluateAs<String>("1")
    }
  }

  @Test
  fun `should evaluate script from file and return its result`() {
    // Given:
    val file = Gdx.files.classpath("ktx/script/script.kts")

    // When:
    val result = engine.evaluateAs<String>(file)

    // Then:
    assertEquals("test", result)
  }

  @Test
  fun `should throw exception if the result of script from file does not match the expected type`() {
    // Given:
    val file = Gdx.files.classpath("ktx/script/script.kts")

    // Expect:
    shouldThrow<ClassCastException> {
      engine.evaluateAs<Int>(file)
    }
  }

  @Test
  fun `should execute script with custom functions, lambdas and classes`() {
    // Given:
    engine.setPackage("ktx.script")
    engine.import("com.badlogic.gdx.utils.Array", alias = "GdxArray")

    // When:
    val result =
      engine.evaluate(
        """
        class Helper {
          fun toArray(vararg objects: Any) = GdxArray.with(*objects)
        }
        fun data(text: String) = KotlinScriptEngineTest.Data(text)
        val lambda: () -> String = { "test" }

        Helper().toArray(data(lambda()), data(lambda()), data(lambda()))
        """.trimIndent(),
      )

    // Then:
    assertEquals(GdxArray.with(Data("test"), Data("test"), Data("test")), result)
  }

  @Test
  fun `should execute script with a receiver`() {
    // Given:
    val receiver = Data(text = "")

    // When:
    engine.evaluateOn(
      receiver,
      """
      text = "test"
      """.trimIndent(),
    )

    // Then:
    assertEquals("test", receiver.text)
  }

  @Test
  fun `should fail to execute script with a receiver if it contains an import`() {
    // Given:
    val receiver = Data(text = "")

    // Expect:
    shouldThrow<ScriptEngineException> {
      engine.evaluateOn(
        receiver,
        """
        import com.badlogic.gdx.Gdx

        text = "test"
        """.trimIndent(),
      )
    }
  }

  @Test
  fun `should execute script from a file with a receiver`() {
    // Given:
    val receiver = Data(text = "")
    val file = Gdx.files.classpath("ktx/script/receiver")

    // When:
    engine.evaluateOn(receiver, file)

    // Then:
    assertEquals("test", receiver.text)
  }

  @Test
  fun `should assign context variable`() {
    // Given:
    val variable = Data("test")

    // When:
    engine["variable"] = variable

    // Then:
    assertSame(variable, engine["variable"])
    assertEquals("test", engine.evaluateAs<String>("variable.text"))
  }

  @Test
  fun `should allow to modify context variable`() {
    // Given:
    val variable = Data("test")
    engine["variable"] = variable

    // When:
    engine.evaluate(
      """
      variable.text = "new"
      """.trimIndent(),
    )

    // Then:
    assertEquals("new", variable.text)
  }

  @Test
  fun `should remove context variable`() {
    // Given:
    val variable = Data("test")
    engine["variable"] = variable

    // When:
    val removed = engine.remove("variable")

    // Then:
    assertNull(engine["variable"])
    assertSame(variable, removed)
  }

  @Test
  fun `should not throw an exception when removing an absent variable`() {
    // When:
    val result = engine.remove("absent")

    // Then:
    assertNull(result)
  }
}
