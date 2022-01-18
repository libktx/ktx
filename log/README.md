[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-log.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-log)

# KTX: Logging

Minimal overhead logging for libGDX applications.

### Why?

Many Java logging frameworks provide string templates to avoid creation of unnecessary `String` objects and (potentially
costly) `toString()` calls. Most Java programmers should recognize the syntax immediately:
`log.info("Message: {}, from: {}, at: {}", message, user, date)`. Still, *vararg* methods used in the process create
new object arrays for each logging call - even if the logging level is turned off, and the message is never formatted
and logged. Formatting itself also features some small overhead - every message has to be processed and built.

While libGDX does address the problem of cross-platform logging, but does not provide *any* mechanism for handling logs
with multiple custom parameters. (Possibly because of the *vararg* method call overhead.) Even its `Logger` utility
class provides no methods consuming optional lazy-evaluated arguments - nor does it log class name and current time.

The most efficient way of logging with the default libGDX mechanisms would look somewhat like this:
```kotlin
if (Gdx.app.logLevel >= Application.LOG_DEBUG) Gdx.app.debug("someTag", "My message: $someObject")
```

In the example above, no new strings (or argument arrays) are created in the process if debug logs are turned off.
A smart JVM would likely notice that log level never changes and completely remove the calls at runtime. However,
this is a pretty verbose way of logging. Fortunately, Kotlin allows us to avoid verbosity with `inline` functions.

### Guide

`ktx-log` introduces methods with inlined lambdas to address the problems of unnecessary string building and *vararg* calls.
They expect a simple function returning a `String` instance - the function will be inlined during the compilation (a new
function object is *never* created) and proceeded with a check of current logging level. If the logging level is turned
off, the string will never be built.

#### Global logging methods

`debug`, `info` and `error` methods can be used to log messages in libGDX applications. If no tag is given, they will
log the exception under `"DEBUG"`, `"INFO"` or `"ERROR"` tags (matching their logging level). Every method supports
an exception parameter, which is used to print the stack traces.

#### `Logger`

`Logger` class is a thin wrapper over the usual libGDX logging API. It remembers its tag and is usually more convenient
to use than global logging methods, as you know the exact source of the logs. It has 2 operator `invoke` methods - one
with and one without an exception parameter - which allows for `logger { "Message." }` syntax as a synonym for info logging.
With  the `logger` factory method, you can create logger instances as easily as `logger<MyClass>()`.

Note that `Logger` instances are not cached. While its relatively cheap to create new loggers, you should keep in mind
that it is best to store them in companion objects ("static" variable equivalent).

### Usage examples

Global message logging:

```kotlin
import ktx.log.*

debug { "Example." }
info { "Example." }
error { "Example." }
```

Global message logging with custom logging tags:

```kotlin
import ktx.log.*

debug("TRACE") { "Example." }
info("WARN") { "Example." }
error("FATAL") { "Example." }
```

Global message logging with parameters (`String` built *only* if the respective logging level is turned on):

```kotlin
import ktx.log.*

debug { "Found ${entities.size} entities in ${id} category." }
info { "Found ${entities.size} entities in ${id} category." }
error { "Found ${entities.size} entities in ${id} category." }
```

Global exception logging:
```kotlin
import ktx.log.*

try {
  problematicMethod()
} catch (exception: Exception) {
  debug(exception) { "Error!" }
  info(exception) { "Error!" }
  error(exception) { "Error!" }
}
```

Getting logger for a specific class:
```kotlin
import ktx.log.*

val log = logger<MyClass>()
```

Using static logger instance for class (recommended):
```kotlin
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

Logging messages with a logger:
```kotlin
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

Extending `Logger` implementation with a custom message format - prepending date to logged messages:
```kotlin
import java.util.Date
import ktx.log.Logger

/** Logs current date additionally to the log message.
 * Uses "FATAL" tag for error messages. */
class TimeLogger(name: String) : Logger(name, errorTag = "FATAL") {
  // Logger message format: Logger name [current date] Original message.
  override fun buildMessage(message: String): String = "$name [${Date()}] $message"
}

/** Creates loggers that include current time in logs. */
inline fun <reified T : Any> myLogger(): Logger = TimeLogger(T::class.java.name)

// Usage:
class MyClass {
  val logger = myLogger<MyClass>()

  fun example() {
    logger.info { "Works like the usual logger." }
  }
}
```

### Alternatives

- [SLF4J](http://www.slf4j.org/) is a common logging facade for Java applications. Note that each libGDX platform would
most likely require a separate logging library, as *SLF4J* (clearly) does not rely on libGDX logging mechanism and there
seem to be no cross-platform *SLF4J* implementation for libGDX applications (yet).
- [Kotlin logging](https://github.com/MicroUtils/kotlin.logging) is an extension of *SLF4J* for Kotlin applications. It
relies on a similar inlining mechanism to avoid runtime overhead of logging.
- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) contains an alternative logging mechanism that wraps around
libGDX cross-platform logging. It can include class names and current time in logs. Similarly to *SLF4J*, it uses string
templates to avoid creation of new strings (unless necessary). However, it still suffers from
*vararg* method calls.

#### Additional documentation

- [Official libGDX logging article.](https://libgdx.com/wiki/app/logging)
