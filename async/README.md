[![Kotlin Coroutines](https://img.shields.io/badge/kotlin--coroutines-1.10.1-orange.svg)](http://kotlinlang.org/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-async.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-async)

# KTX: Coroutines support and parallelization utilities

[Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) support and general asynchronous operations
utilities for libGDX applications.

### Why?

Coroutines-based APIs greatly simplify asynchronous operations and avoid so-called callback hell. Some libGDX APIs -
like the `Timer` - were not written with functional expressions in mind, often using abstract classes instead of
functional interfaces. Additionally, they are often tedious to call from within Kotlin. This module aims to hide
asynchronous code complexity with coroutines, as well as to improve existing asynchronous APIs with idiomatic Kotlin
extensions.

### Guide

#### Setup

Before using `ktx-async`, make sure to include the Kotlin coroutines library in your Gradle script:

```groovy
// Groovy DSL:
api group: 'org.jetbrains.kotlinx', name: 'kotlinx-coroutines-core', version: coroutinesVersion
```

```kotlin
// Kotlin DSL:
api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutinesVersion)
```

The `coroutinesVersion` _must_ match the coroutines version that the `ktx-async` library was compiled against -
otherwise it might cause runtime or compilation errors.

Before Kotlin 1.3, coroutines were an experimental feature and required the following Gradle configuration:

```groovy
kotlin {
  experimental {
    coroutines 'enable'
  }
}
```

If you are using a recent version of Kotlin and `ktx-async`, this declaration is no longer required.

##### Troubleshooting

If using any coroutines-related classes results in a `java.lang.NoClassDefFoundError` exception, make sure that
the backend module correctly picks up transitive dependencies of the core module. In particular, this error can be
seen on iOS with RoboVM if `kotlinx-coroutines-core` or `ktx-async` are included as `implementation` dependencies
in the core Gradle module instead of `api`.

`ktx-async` was tested on desktop, Android and iOS, and should work on every major platform. If you encounter
any issues with launching the coroutines, do not hesitate to create an [issue](https://github.com/libktx/ktx/issues).

> Note that Kotlin coroutines are not supported by the unofficial TeaVM web platform.

#### Coroutines

_Please refer to Kotlin coroutines documentation or tutorials if you are having trouble with this section._

`KtxAsync` is the default scope you should use instead of the `GlobalScope` in your KTX applications. By default, it
will execute tasks on the main rendering thread. However, if you want to perform some actions asynchronously on
different threads, you can use `AsyncExecutorDispatcher` that uses libGDX cross-platform `AsyncExecutor` to execute
the tasks.

Before using `KtxAsync` scope, make sure to call `KtxAsync.initiate()` on the main rendering thread. This is not
strictly required if immediate dispatcher is not used, but as a rule of thumb, you should invoke this method in `create`
of your `ApplicationListener`.

KTX providers 2 main implementations of coroutine dispatchers:

* `RenderingThreadDispatcher`: executes tasks on the main rendering thread. Available via `Dispatchers.KTX`. Default
    dispatcher used by the `KtxAsync` scope internally.
    * `RenderingScope` factory method allows to define a `CoroutineScope` using the `RenderingThreadDispatcher`.
* `AsyncExecutorDispatcher`: wraps libGDX `AsyncExecutor` to execute the tasks. Can be initiated in the following ways:
    * `newSingleThreadAsyncContext()` factory method: creates an `AsyncExecutor` with a single thread.
    * `newAsyncContext(threads)` factory method: creates an `AsyncExecutor` with the given amount of threads.
    * `AsyncExecutorDispatcher` constructor: allows to wrap an existing `AsyncExecutor`. Make sure to set the `threads`
        property to the actual threads amount if you rely on this value.

Additionally, `ktx-async` provides the following utility methods:

* `onRenderingThread`: suspends the coroutine to execute a task on the main rendering thread and return its result.
    Should be used if you dispatch a coroutine with a non-rendering thread dispatcher and need to execute some task
    on the main rendering thread. Syntax sugar for quick context switch.
* `isOnRenderingThread`: checks if the coroutine is being executed on the main rendering thread.
* `skipFrame`: attempts to suspend the coroutine for a single `render` frame of the application. The method is
    guaranteed to skip _at least one_ frame, but - depending on the thread it was invoked on - it might skip multiple
    frames. Do not rely on this method for precise frame measurements.
* `httpRequest`: allows to perform an asynchronous HTTP request.
* `RenderingScope` factory function is the KTX rendering-thread version of the official `MainScope`. It creates a scope
to launch coroutines in the rendering thread and that has a supervisor job so the whole scope can be cancelled at once.

#### Utilities

Other asynchronous operations utilities include:

- `schedule` and `interval` functions were added to simplify libGDX `Timer` API. Normally it requires the user to extend
    an abstract class - these utility functions allow using idiomatic Kotlin lambdas syntax.
- `HttpRequestResult` is a thread-safe wrapper of `HttpResponse` that reads and caches HTTP response content. These
    objects are returned by the coroutines-based HTTP requests API.

### Usage examples

Initiating coroutines context upon application creation:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import ktx.async.KtxAsync

class MyApp : ApplicationAdapter() {
    override fun create() {
        KtxAsync.initiate()
    }
}
```

Starting a simple coroutine on the main rendering thread:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

fun myFirstCoroutine() {
    KtxAsync.launch {
        println("Hello from the rendering thread! ${Thread.currentThread()}")
    }
}
```

Starting a coroutine with a non-blocking suspension lasting 2 seconds:

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

fun coroutineWithDelay() {
    KtxAsync.launch {
        println("Before delay...")
        val start = System.currentTimeMillis()
        delay(timeMillis = 2000L)
        val end = System.currentTimeMillis()
        println("After delay: ${end - start} milliseconds passed.")
    }
}
```

Starting a coroutine that skips a single rendering frame by suspension and resumes on the next render call:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.skipFrame

class MyApp : ApplicationAdapter() {
    var frame = 0

    override fun create() {
        KtxAsync.initiate()
        KtxAsync.launch {
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

Using `AsyncExecutor` to perform tasks outside the rendering thread:

```kotlin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

fun asyncTask() {
    val executor = newSingleThreadAsyncContext()
    KtxAsync.launch {
        println("Before async: ${Thread.currentThread()}")
        withContext(executor) {
            println("During async: ${Thread.currentThread()}")
        }
        println("After async:  ${Thread.currentThread()}")
    }
}
```

Using `AsyncExecutor` with multiple threads to perform sophisticated multithreaded mathematical operations:

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.newAsyncContext

fun multithreading() {
    val executor = newAsyncContext(threads = 4)
    KtxAsync.launch {
        val a = async(executor) { 20 }
        val b = async(executor) { 22 }
        println("The answer to everything: ${a.await() + b.await()}.")
    }
}
```

Manually switching between coroutine contexts:

```kotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktx.async.KTX
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

fun backAndForth() {
    val executor = newSingleThreadAsyncContext()
    KtxAsync.launch {
        println("On rendering thread: ${Thread.currentThread()}")
        withContext(executor) {
            println("On async thread: ${Thread.currentThread()}")
            withContext(Dispatchers.KTX) {
                println("On rendering thread: ${Thread.currentThread()}")
            }
        }
    }
}
```

Using utility methods to check and switch the threads:

```kotlin
import kotlinx.coroutines.launch
import ktx.async.*

fun threadSwitch() {
    val executor = newSingleThreadAsyncContext()
    KtxAsync.launch(executor) {
        println("Executor context. ${isOnRenderingThread()}")
        onRenderingThread {
            println("Main KTX context. ${isOnRenderingThread()}")
        }
        println("Executor context. ${isOnRenderingThread()}")
    }
}
```

Initiating `AsyncExecutorDispatchers` to perform asynchronous operations outside the rendering thread:

```kotlin
import com.badlogic.gdx.utils.async.AsyncExecutor
import ktx.async.AsyncExecutorDispatcher
import ktx.async.newAsyncContext
import ktx.async.newSingleThreadAsyncContext

// Context with a single thread:
val singleThreaded = newSingleThreadAsyncContext()

// Context with multiple threads:
val multiThreaded = newAsyncContext(threads = 4)

// Context with a custom thread name pattern:
val multiThreadedWithNamedThreads = newAsyncContext(threads = 4, threadName = "MyThread")

// Context with an existing executor:
val executor = AsyncExecutor(2)
val fromExistingExecutor = AsyncExecutorDispatcher(executor, threads = 2)
```

Starting a coroutine, which performs an HTTP request and resumes on the main rendering thread after
receiving the response (_requires internet connection to run_):

```kotlin
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.httpRequest

fun http() {
    KtxAsync.launch {
        val response = httpRequest(url = "https://example.com")
        println("""Reading response on ${Thread.currentThread()}. Website content:

${response.contentAsString}""")
    }
}
```

Performing an asynchronous HTTP request via `AsyncExecutor`:

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.httpRequest
import ktx.async.newSingleThreadAsyncContext

fun httpAsync() {
    val executor = newSingleThreadAsyncContext()
    KtxAsync.launch {
        val response = async(executor) { httpRequest(url = "https://example.com") }
        response.await()
    }
}
```

Cancelling a coroutine:

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

fun withCancel() {
    val job = KtxAsync.launch {
        delay(timeMillis = 1000L)
        println("Should not execute this.")
    }
    job.cancel()
}
```

Creating a coroutine scope to confine jobs' lives to a specific class:

```kotlin
import com.badlogic.gdx.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ktx.async.httpRequest
import ktx.async.RenderingScope

class MyScreen: Screen, CoroutineScope by RenderingScope() {
  // Implement your application screen here.

  override fun hide() {
    // Cancels any running coroutines when leaving the screen:
    cancel()
  }

  // Starts coroutine in this screen's scope:
  private fun loadSomething() = launch {
    val result = httpRequest(url = "https://example.com")
    webResultLabel.text = result.contentAsString
  }
}
```

Creating a custom cancellable scope with an `AsyncExecutor` dispatcher:

```kotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ktx.async.AsyncExecutorDispatcher
import ktx.async.newSingleThreadAsyncContext

class MyScope(dispatcher: AsyncExecutorDispatcher)
  : CoroutineScope by CoroutineScope(SupervisorJob() + dispatcher)

val myScope = MyScope(newSingleThreadAsyncContext())
```

Scheduling a task executed on the main rendering thread after one second:

```kotlin
import ktx.async.schedule

val taskCallback = schedule(delaySeconds = 1f) {
    println("Hello!")
}
```

Scheduling a task repeatedly executed on the main rendering thread after one second:

```kotlin
import ktx.async.interval

val taskCallback = interval(delaySeconds = 1f, intervalSeconds = 1f) {
    println("Hello again!")
}
```

### Alternatives

- Standard Kotlin coroutines libraries might be used along with custom thread pools. They do not offer the same level
of compatibility with existing libGDX APIs though.

#### Additional documentation

- [Official libGDX threading article.](https://libgdx.com/wiki/app/threading)
- [Coroutines language reference.](https://kotlinlang.org/docs/reference/coroutines.html)
- [Coroutines repository.](https://github.com/Kotlin/kotlin-coroutines)
