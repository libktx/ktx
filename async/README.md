# KTX: async

[Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) support and general asynchronous operations
utilities for LibGDX applications.

### Why?

Coroutines-based APIs greatly simplify asynchronous operations and allow to avoid so-called callback hell. Some LibGDX
APIs - like the `Timer` - were not written with functional expressions in mind (often using abstract classes) and they
are tedious to call with vanilla Kotlin. This module aims to hide asynchronous code complexity with coroutines, as well
as improve existing asynchronous APIs to feel more like Kotlin.

### Guide

#### Coroutines

`ktx-async` provides a coroutines context implementation: `KtxAsync`. It allows to launch suspending, non-blocking
coroutines that resume operations on the main rendering thread. `ktx-async` makes it possible to write code that looks
more or less like simple synchronous code that would normally keep your application from rendering, but actually
executes on multiple threads (when needed) and resumes on the rendering thread using `Gdx.app.postRunnable` only when
necessary.

To use KTX coroutines, you have to call `enableKtxCoroutines` first - preferably in the `ApplicationListener.create`
method. It consumes an optional amount of executor threads: if set to 1 or more, `KtxAsync` will create an internal
`AsyncExecutor`, which will be used to perform additional asynchronous operations outside of the main rendering thread.

`ktxAsync` is a convenience method that launches a non-blocking coroutine with the `KtxAsync` context.

Currently supported suspending utility methods available from the `KtxAsync` context:
- `delay`: non-blocking suspension of the coroutine for a given period of time (in seconds) using LibGDX `Timer` API.
- `skipFrame`: suspends the coroutine and resumes it on the next frame using `Gdx.app.postRunnable`. Can be used only
    directly in the coroutine block on the main rendering thread - using it in asynchronous actions executed on
    different threads yields undefined behavior.
- `asynchronous`: performs an operation on a different thread using `AsyncExecutor`, resuming coroutine with its result.
    If an `AsyncExecutor` is not passed as a parameter, context's default executor will be used instead. Note that
    context must have been initialized with a non-zero and non-negative amount of threads in order to use this method
    with default executor.
- `httpRequest`: wraps around `Gdx.net` API, allowing to perform suspending HTTP requests that resume once the response
    is received.

All implemented suspending functions are cancellable. _Note:_ KTX does its best to cancel tasks, but due to asynchronous
nature of suspending methods, cancelling might not take immediate effect. In most cases, it will not interrupt currently
executed operations.

As a rule of thumb: every action in the coroutine scope will be invoked on the main rendering thread, unless it is a
suspending function that explicitly states otherwise (e.g. `asynchronous`, `httpRequest`). See usage examples below for
more info on coroutines API.

#### Utilities

Other asynchronous operations utilities include:
- `schedule` and `interval` functions were added to simplify LibGDX `Timer` API. Normally it requires the user to extend
an abstract class - these utility functions allow to use idiomatic Kotlin lambdas syntax.
- `HttpRequestResult` is a thread-safe wrapper of `HttpResponse` that reads and caches HTTP response content. These
objects are returned by the coroutines-based HTTP requests API.

### Usage examples

Initiating coroutines context upon application creation:

```Kotlin
import ktx.app.KtxApplicationAdapter
import ktx.async.*

class MyApp : KtxApplicationAdapter {
  override fun create() {
    enableKtxCoroutines()
  }
}
```

Initiating coroutines context with internal `AsyncExecutor` with 1 thread for asynchronous tasks:

```Kotlin
import ktx.app.KtxApplicationAdapter
import ktx.async.*

class MyApp : KtxApplicationAdapter {
  override fun create() {
    enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
  }
}
```

Starting a simple coroutine on the main rendering thread:

```Kotlin
import ktx.async.*

ktxAsync {
  println("Hello from the main thread! ${Thread.currentThread()}")
}
```

Starting a coroutine with a non-blocking suspension lasting 2 seconds:

```Kotlin
import ktx.async.*

ktxAsync {
  println("Before delay...")
  val start = System.currentTimeMillis()
  delay(seconds = 2f)
  val end = System.currentTimeMillis()
  println("After delay: ${end - start} milliseconds passed.")
}
```

Starting a coroutine that skips a single rendering frame by suspension and resumes on the next render call:

```Kotlin
import ktx.app.KtxApplicationAdapter
import ktx.async.*

class MyApp : KtxApplicationAdapter {
  var frame = 0

  override fun create() {
    enableKtxCoroutines()

    ktxAsync {
      delay(seconds = 1f)
      println("On frame: $frame.")
      skipFrame()
      println("On frame: $frame.")
    }
  }

  override fun render() {
    frame++
  }
}
```

Starting a coroutine, which performs operation on the context's `AsyncExecutor` thread:

```Kotlin
import ktx.async.*

ktxAsync {
  println("Before async: ${Thread.currentThread()}")
  val result = asynchronous {
    println("During async: ${Thread.currentThread()}")
    "Hello from the async executor!"
  }
  println("After async: $result, ${Thread.currentThread()}")
}
```

Starting a coroutine, which performs operation on a custom `AsyncExecutor` thread:

```Kotlin
import ktx.async.*
import com.badlogic.gdx.utils.async.AsyncExecutor

val executor = AsyncExecutor(1)
ktxAsync {
  println("Before async: ${Thread.currentThread()}")
  val result = asynchronous(executor) {
    println("During async: ${Thread.currentThread()}")
    "Hello from a custom async executor!"
  }
  println("After async: $result, ${Thread.currentThread()}")
}
```

Starting a coroutine, which performs an asynchronous HTTP request and resumes on the main rendering thread after
receiving the response (_requires internet connection to run_):

```Kotlin
import ktx.async.*

ktxAsync {
  val response = httpRequest(url = "https://example.com")
  println("""Reading response on ${Thread.currentThread()}. Website content:
${response.contentAsString}""")
}
```

Cancelling a coroutine:
```Kotlin
import ktx.async.*

val job = ktxAsync {
  delay(5f)
  println("Should not execute this.")
}
job.cancel()
```

Scheduling a task executed on the main rendering thread after one second:

```Kotlin
import ktx.async.schedule

val taskCallback = schedule(delaySeconds = 1f) {
  println("Hello!")
}
```

Scheduling a task repeatedly executed on the main rendering thread after one second:

```Kotlin
import ktx.async.interval

val taskCallback = interval(delaySeconds = 1f, intervalSeconds = 1f) {
  println("Hello again!")
}
```

### Alternatives

- Standard Kotlin coroutines libraries might be used along with custom thread pools. They do not offer the same level
of compatibility with existing LibGDX APIs though.

#### Additional documentation

- [LibGDX threading article.](https://github.com/libgdx/libgdx/wiki/Threading)
- [Coroutines language reference.](https://kotlinlang.org/docs/reference/coroutines.html)
- [Coroutines repository.](https://github.com/Kotlin/kotlin-coroutines)
