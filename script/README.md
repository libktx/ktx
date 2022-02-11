[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-script.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-script)

# KTX: Kotlin script engine

Kotlin script engine for the desktop platform.

### Why?

`ktx-script` can run Kotlin scripts at runtime - either passed as a string, or read from a `FileHandle`.
This can be used as a basis for a custom scripting engine for your application.

### Guide

To execute Kotlin scripts, create a `KotlinScriptEngine` instance. This class has the following methods:

- `evaluate(String)`: compiles and executes a script passed as a string.
Returns the last expression from the script as `Any?`.
- `evaluate(FileHandle)`: compiles and executes a script from the selected file.
Returns the last expression from the script as `Any?`.
- `evaluateAs<T>(String)`: compiles and executes a script passed as a string.
Returns the last expression from the script as `T`. Throws `ClassCastException` if the result does not match `T` type.
- `evaluateAs<T>(FileHandle)`: compiles and executes a script from the selected file.
Returns the last expression from the script as `T`. Throws `ClassCastException` if the result does not match `T` type.
- `evaluateOn(Any, String)`: compiles and executes a script passed as string with a custom receiver available as `this`.
- `evaluateOn(Any, FileHandle)`: compiles and executes a script from a file with a custom receiver available as `this`.

- `set(String, Any)`: adds a variable to the script execution context.
The variable will be available in the scripts under the given name.
- `get(String)`: returns the current value assigned to the selected variable.
- `remove(String)`: removes the variable registered under the given name.
Returns the removed value, or null if no variable was registered.

- `import(String, String?)`: adds an import to the script context. Wildcard imports are supported.
Takes an optional alias.
- `importAll(vararg String)`, `importAll(Iterable<String>)`: adds the selected imports to the script context.
- `setPackage(String)`: sets the package for the scripts. The scripts will have access to any classes and functions
within the selected package without the need for explicit imports.

This module depends on the `org.jetbrains.kotlin:kotlin-scripting-jsr223` package. To change the version of the
scripting engine, override this dependency in your Gradle or Maven setup.

#### Known issues

* **Limited to the desktop platform.** Other libGDX backends do not support script engines.
* **Slow startup time.** Evaluating initial scripts might take several seconds.
Performance improves with subsequent script evaluations, but it is still not on par with
precompiled Kotlin.
* **Script package can only be chosen once.** As of Kotlin 1.5.31, if multiple scripts define `package`,
or if the package was already set with `KotlinScriptEngine.setPackage`, it cannot be overridden. Once the package
is set, all scripts will be executed from the same package, even if they attempt to override it.
* **Lambdas cannot be set as script variables directly.**
Instead, define lambdas or functions in the global scope and import them, or pass objects with lambda variables.
* **Lambdas cannot be returned by the scripts directly.** Pass objects instead.
Note that returned objects can have lambda variables.
* **Defined variables stay in the script scope.** While you can declare a top-level `val` or `var` that will be
available for all future scripts, their values cannot be retrieved with `KotlinScriptEngine.get`. Instead, they
have to be returned as script results, or otherwise passed outside the script context.
* **IDE support for scripts might not be complete.** If you specify variables, imports, or package using
the engine instance, IDE might not be able to pick them up without additional setup.
* **Scripts might be unable to infer the generic types.** Avoid passing generic objects as variables to the scripts.
* **Targets Java 8.** Using newer language features might result in exceptions.
* **Scripts with receivers cannot contain any import statements.** This only affects the `KotlinScriptEngine.evaluateOn`
methods.

#### Advantages over using `ScriptEngine` directly

* Explicit Kotlin types with nullability info.
* `FileHandle` support.
* Import and package setters.

Note that this module uses JSR-223 `ScriptEngine` internally. For more customization options,
use the experimental scripting engine from the `org.jetbrains.kotlin:kotlin-scripting-jvm-host` package.

### Usage examples

Creating a new script engine:

```kotlin
import ktx.script.KotlinScriptEngine

val engine = KotlinScriptEngine()
```

Executing a basic script:

```kotlin
import ktx.script.KotlinScriptEngine

fun executeScript(engine: KotlinScriptEngine) {
  engine.evaluate("""
    println("Hello from script!")
  """)
}
```

Executing a script from a file:

```kotlin
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import ktx.script.KotlinScriptEngine

fun executeScript(engine: KotlinScriptEngine) {
  // You can also use ktx-assets utilities to obtain a file handle.
  val file: FileHandle = Gdx.files.internal("script.kts")
  engine.evaluate(file)
}
```

Using the returned value from a script:

```kotlin
import ktx.script.KotlinScriptEngine

fun executeScript(engine: KotlinScriptEngine) {
  // When you use evaluateAs<Type> instead of evaluate, the result
  // of the script is automatically cast to the chosen type:
  val text = engine.evaluateAs<String>("""
    // The result of the script is its last expression:
    "Hello from script!"
  """)
  println(text)
}
```

Adding a variable to the script context:

```kotlin
import ktx.script.KotlinScriptEngine

fun executeScript(engine: KotlinScriptEngine) {
  engine["myVariable"] = "Hello"
  engine.evaluate("""
    println(myVariable + " from script!")
  """)
}
```

Loading a `Texture` from within a script:

```kotlin
import com.badlogic.gdx.graphics.Texture
import ktx.script.KotlinScriptEngine

fun executeScript(engine: KotlinScriptEngine) {
  val texture = engine.evaluateAs<Texture>("""
    import com.badlogic.gdx.graphics.Texture
    
    Texture("image.png")
  """)
}
```

Adding an import to the script scope:

```kotlin
import com.badlogic.gdx.utils.Array as GdxArray
import ktx.script.KotlinScriptEngine

fun executeScript(engine: KotlinScriptEngine) {
  // You can automatically import any class (or package) for your scripts.
  // When importing individual classes or functions, you can assign an
  // optional alias:
  engine.import("com.badlogic.gdx.utils.Array", alias = "GdxArray")
  val texts = engine.evaluateAs<GdxArray<String>>("""
    GdxArray.with("Hello", "from", "script!")
  """)
  println(texts)
}
```

Setting the package of the scripts:

```kotlin
package ktx.script.example

import ktx.script.KotlinScriptEngine

data class Example(val text: String)

fun executeScript(engine: KotlinScriptEngine) {
  engine.setPackage("ktx.script.example")
  val example = engine.evaluateAs<Example>("""
    // When you set a package for your scripts,
    // explicit imports are no longer necessary:
    Example(text = "Hello from script!")
  """)
  println(example)
}
```

Executing a script with a receiver (`this`):

```kotlin
import ktx.script.KotlinScriptEngine

data class Data(var text: String = "")

fun executeScript(engine: KotlinScriptEngine) {
  val receiver = Data()

  engine.evaluateOn(
    receiver,
    """
      // The receiver is now available as `this` in the script:
      text = "Hello from script!"
      println(this.text)

      // Note that scripts with a receiver cannot have any imports.
    """
  )
  // Property modified in the script will persist
  // outside the script scope:
  println(receiver.text)
}
```

### Alternatives

- Using the JSR-223 `ScriptEngine` directly.
- Using the experimental `BasicJvmScriptingHost` from the `org.jetbrains.kotlin:kotlin-scripting-jvm-host` package.
- Using other scripting languages, via JSR-223 API or otherwise.

#### Additional documentation

- [The Kotlin scripting proposal](https://github.com/Kotlin/KEEP/blob/master/proposals/scripting-support.md).
- [The `Kotlin/kotlin-script-examples` repository](https://github.com/Kotlin/kotlin-script-examples).
