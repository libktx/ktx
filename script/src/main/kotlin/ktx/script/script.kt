package ktx.script

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.GdxRuntimeException
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Executes Kotlin scripts in runtime.
 *
 * Wraps around the official implementation of the JSR-223 [ScriptEngine] for the Kotlin language.
 */
class KotlinScriptEngine {
  /** Direct reference to the wrapped JSR-223 [ScriptEngine]. */
  val engine: ScriptEngine =
    ScriptEngineManager().getEngineByExtension("kts")
      ?: throw ScriptEngineException(
        "Unable to find engine for extension: kts. " +
          "Make sure to include the org.jetbrains.kotlin:kotlin-scripting-jsr223 dependency.",
      )

  /**
   * Imports the selected [import] using an optional [alias]. Wildcard imports using `*` are supported, but they
   * cannot have an [alias].
   *
   * If an [alias] is given, the [import] will be executed with a Kotlin import alias using the `as` operator.
   * The [import] will be available in future scripts.
   *
   * Example usage: `engine.import("com.badlogic.gdx.utils.*")`
   *
   * When performing multiple imports at once, use [importAll] instead.
   */
  fun import(
    import: String,
    alias: String? = null,
  ) {
    val script = if (alias.isNullOrBlank()) "import $import" else "import $import as $alias"
    evaluate(script)
  }

  /**
   * Imports the selected [imports] within the script context. Wildcard imports using `*` are accepted.
   * The [imports] will be available in future scripts.
   *
   * To assign an alias to a specific import, use Kotlin `as` operator after the qualified name.
   * For example: `engine.importAll("com.badlogic.gdx.utils.Array as GdxArray")`
   */
  fun importAll(vararg imports: String) {
    val script = imports.joinToString(separator = "\n") { "import $it" }
    evaluate(script)
  }

  /**
   * Imports the selected [imports] within the script context. Wildcard imports using `*` are accepted.
   * The [imports] will be available in future scripts.
   *
   * To assign an alias to a specific import, use Kotlin `as` operator after the qualified name.
   * For example: `engine.importAll("com.badlogic.gdx.utils.Array as GdxArray")`
   */
  fun importAll(imports: Iterable<String>) {
    val script = imports.joinToString(separator = "\n") { "import $it" }
    evaluate(script)
  }

  /**
   * Sets the package of the future scripts to [name].
   *
   * Note that the Kotlin script engine implementation might prohibit from changing the package.
   * As a result, subsequent calls of this method might have no effect.
   * To execute scripts from within multiple packages, create multiple script engines.
   */
  fun setPackage(name: String) {
    evaluate("package $name")
  }

  /**
   * Retrieves the value assigned to [variable] in the context of this [engine].
   */
  inline operator fun <reified T : Any?> get(variable: String): T? = engine.get(variable) as? T

  /**
   * Assigns the selected [value] to the [variable] name in the context of this [engine].
   * The [variable] will be available in the future scripts.
   */
  operator fun <T : Any?> set(
    variable: String,
    value: T,
  ) = engine.put(variable, value)

  /**
   * Removes the [variable] from the context of this [engine]. Returns the value assigned to the [variable].
   * Returns null if no value is assigned to [variable].
   */
  fun remove(variable: String): Any? = engine.context.removeAttribute(variable, ScriptContext.ENGINE_SCOPE)

  /**
   * Executes the selected [script]. Returns the last script's expression as the result.
   * If unable to execute the script, [ScriptEngineException] will be thrown.
   */
  fun evaluate(script: String): Any? =
    try {
      engine.eval(script)
    } catch (exception: Throwable) {
      throw ScriptEngineException("Unable to execute Kotlin script:\n$script", exception)
    }

  /**
   * Executes the selected [scriptFile]. Returns the last script's expression as the result.
   * If unable to execute the script, [ScriptEngineException] will be thrown.
   */
  fun evaluate(scriptFile: FileHandle): Any? =
    try {
      engine.eval(scriptFile.reader())
    } catch (exception: Throwable) {
      throw ScriptEngineException("Unable to execute Kotlin script from file: $scriptFile", exception)
    }

  /**
   * Executes the selected [script] on the [receiver] object. The [receiver] will be available as `this`
   * throughout the script, as well as under the value of [receiverVariableName].
   * If no variable name is given, it will be chosen randomly.
   *
   * Note that the script cannot contain any import statement, or it will fail with a [ScriptEngineException].
   * Use [import] or [importAll] instead.
   */
  fun evaluateOn(
    receiver: Any,
    script: String,
    receiverVariableName: String = getRandomReceiverName(),
  ) {
    try {
      this[receiverVariableName] = receiver
      evaluate("$receiverVariableName.apply{$script}")
    } finally {
      remove(receiverVariableName)
    }
  }

  /**
   * Executes the selected [scriptFile] on the [receiver] object. The [receiver] will be available as `this`
   * throughout the script, as well as under the value of [receiverVariableName].
   * If no variable name is given, it will be chosen randomly.
   *
   * Note that the script cannot contain any import statement, or it will fail with a [ScriptEngineException].
   * Use [import] or [importAll] instead.
   */
  fun evaluateOn(
    receiver: Any,
    scriptFile: FileHandle,
    receiverVariableName: String = getRandomReceiverName(),
  ) {
    try {
      evaluateOn(receiver, scriptFile.readString(), receiverVariableName)
    } catch (exception: Throwable) {
      throw ScriptEngineException("Unable to execute script with a receiver from file: $scriptFile", exception)
    }
  }

  private fun getRandomReceiverName(): String = "receiver" + MathUtils.random(0, Int.MAX_VALUE - 1)

  /**
   * Executes the selected [script] and returns an instance of [T]. If the script is not an instance of [T],
   * [ClassCastException] will be thrown. If unable to execute the script, [ScriptEngineException] will be thrown.
   */
  inline fun <reified T : Any?> evaluateAs(script: String): T = evaluate(script) as T

  /**
   * Executes the selected [scriptFile] and returns an instance of [T]. If the script is not an instance of [T],
   * [ClassCastException] will be thrown. If unable to execute the script, [ScriptEngineException] will be thrown.
   */
  inline fun <reified T : Any?> evaluateAs(scriptFile: FileHandle): T = evaluate(scriptFile) as T
}

/**
 * Thrown when unable to execute a script or configure the scripting engine.
 */
class ScriptEngineException(
  message: String,
  cause: Throwable? = null,
) : GdxRuntimeException(message, cause)
