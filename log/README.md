[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-log.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-log)

# KTX: logging

Minimal overhead logging for LibGDX applications.

### Why?

Many Java logging frameworks provide string templates to avoid creation of unnecessary `String` objects and (potentially
costly) `toString()` calls. Most Java programmers should recognize the syntax immediately:
`log.info("Message: {}, from: {}, at: {}", message, user, date)`. Still, *vararg* methods used in the process create
new object arrays for each logging call - even if the logging level is turned off and the message is never formatted
and logged. Formatting itself also features some small overhead - every message has to be processed and built.

While LibGDX does address the problem of cross-platform logging, but does not provide *any* mechanism for handling logs
with multiple custom parameters. (Maybe because of the *vararg* method call overhead.) Even its `Logger` utility class
provides no methods consuming optional lazy-evaluated arguments - nor does it log class name and current time.

The most efficient way of logging with the default LibGDX mechanisms seems to look somewhat like this:
```Kotlin
if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug("someTag", "My message: " + someObject);
```

In the example above, no new strings (or argument arrays) are created in the process if debug logs are turned off.
A smart JVM would probably notice that log level will never change and completely remove the calls at runtime.
Needless to say, this is a pretty verbose way of logging. Fortunately, Kotlin fights verbosity with `inline` functions.

### Guide

`ktx-log` introduces methods with inlined lambdas to address the unnecessary string building and *vararg* calls problems.
They expect a simple function returning a `String` instance - the function will be inlined during the compilation (a new
function object is *never* created) and proceeded with a check of current logging level. If the logging level is turned
off, the string will never be built.

#### Global logging methods

`debug`, `info` and `error` methods can be used to log messages in LibGDX applications. If no tag is given, they will
log the exception under `"[DEBUG]"`, `"[INFO] "` or `"[ERROR]"` tag (matching their logging level). Every method
supports exception parameters, which are used to print the stack traces.

#### `Logger`

`Logger` class is a thin wrapper over the usual LibGDX logging API. It remembers its tag and is usually more convenient
to use than global logging methods, as you know the exact source of the logs. It has 2 operator `invoke` methods (one
with and one without exceptions) allow for pleasant `logger { "Message." }` syntax as a synonym for info logging. With
the `logger` factory method, you can create logger instances as easily as `logger<MyClass>()`.

Note that `Logger` instances are not cached. While its relatively cheap to create new loggers, you should keep in mind
that it's best to store them in companion objects ("static" variable equivalent).

### Usage examples

Global message logging:

```Kotlin
import ktx.log.*

debug { "Example." }
info { "Example." }
error { "Example." }
```

Global message logging with custom logging tags:

```Kotlin
import ktx.log.*

debug("[TRACE]") { "Example." }
info("[WARN] ") { "Example." }
error("[FATAL]") { "Example." }
```

Global message logging with parameters (`String` built *only* if logging level is turned on):

```Kotlin
import ktx.log.*

debug { "Found ${entities.size} entities in ${id} category." }
info { "Found ${entities.size} entities in ${id} category." }
error { "Found ${entities.size} entities in ${id} category." }
```

Global exception logging:
```Kotlin
import ktx.log.*

try {
  problematicMethod()
} catch (exception: Exception) {
  debug(exception) { "Error!" }
  info(exception) { "Error!" }
  error(exception) { "Error!" }
  
  debug(tag = "[TRACE]", cause = exception) { "Error!" }
  info(tag = "[WARN] ", cause = exception) { "Error!" }
  error(tag = "[FATAL]", cause = exception) { "Error!" }
}
```

Getting logger for a specific class:
```Kotlin
import ktx.log.*

val log = logger<MyClass>()
```

Using static logger instance for class (recommended):
```Kotlin
import ktx.log.*

class MyClass {
  companion object {
    val log = logger<MyClass>()
  }

  fun sayHello() {
    log.info { "Hello!" }
  }
}
```

Logging with a logger:
```Kotlin
import ktx.log.*

val log = logger<MyClass>()

try {
  log.debug { "Example." }
  log.info { "Found ${entities.size} entities in ${id} category." }
  log { "Equivalent to info logging." }
} catch (exception: Exception) {
  log.error(exception) { "Error! Unexpected category: ${id}." }
}
```

Extending `Logger` implementation with a custom tag format:
```Kotlin
import ktx.log.Logger

/** Logs current date additionally to the log message. */
class TimeLogger(tag: String) : Logger(tag) {
  override val debugTag: String
    get() = "[DEBUG] ${Date()} $tag"
  override val infoTag: String
    get() = "[INFO]  ${Date()} $tag"
  override val errorTag: String
    get() = "[ERROR] ${Date()} $tag"
}

/** Creates loggers that include current time in logs. */
inline fun <reified T : Any> myLogger(): Logger = TimeLogger(T::class.java.name)

// Usage:
val logger = myLogger<MyClass>()
logger.info { "Works like the usual logger." }
```

### Alternatives

- [SLF4J](http://www.slf4j.org/) is a common logging facade for Java applications. Note that each LibGDX platform would
most likely require a separate logging library, as *SLF4J* (clearly) does not rely on LibGDX logging mechanism and there
seem to be no cross-platform *SLF4J* implementation for LibGDX applications (yet).
- [Kotlin logging](https://github.com/MicroUtils/kotlin.logging) is an extension of *SLF4J* for Kotlin applications. It
relies on a similar inlining mechanism to avoid runtime overhead of logging.
- [LibGDX Kiwi utilities](https://github.com/czyzby/gdx-lml/tree/master/kiwi) contain an alternative logging mechanism
that wraps around LibGDX cross-platform logging. It allows to include class names and current time in logs. Similarly
to *SLF4J*, it uses string templates to avoid creation of new strings (unless necessary). However, it still suffers from
*vararg* method calls.

#### Additional documentation

- [LibGDX logging article.](https://github.com/libgdx/libgdx/wiki/Logging)

