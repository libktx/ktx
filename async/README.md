# KTX: coroutines support and threading utilities

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

#### Asset loading

`ktx-async` provides `AssetStorage` class: a lightweight coroutines-based alternative to LibGDX `AssetManager`.

After the loading phase, `AssetStorage` and `AssetManager` behave more or less the same: they store assets mapped by
their file path that can be retrieved or disposed on demand. The key difference between **KTX** storage and LibGDX
manager is the loading model: `AssetStorage` provides suspending methods executed via coroutines that resume the thread
as soon as the asset is loaded, while `AssetManager` requires scheduling of asset loading, updating and retrieving the
assets once the loading is finished.

If you decide to use coroutines based on the `KtxAsync` context, `AssetStorage` can greatly simplify your asset loading
code.

Usage comparison: a simple application that loads three assets and switches to the next view, passing the loaded assets.

LibGDX `AssetManager`:

```Kotlin
class Application: ApplicationAdapter() {
  lateinit var assetManager: AssetManager

  override fun create() {
    assetManager = AssetManager().apply {
      load("logo.png", Texture::class.java) // Notice no returns.
      load("i18n.properties", I18NBundle::class.java)
      load("ui.json", Skin::class.java)
    }
  }

  override fun render() {
    if (assetManager.update()) {
      // Assets loaded:
      finishLoading()
    }
    // Render loading prompt.
  }

  fun finishLoading() {
    assetManager.apply {
      val logo = get<Texture>("logo.png")
      val bundle = get<I18NBundle>("i18n.properties")
      val skin = get<Skin>("ui.json")
      goToNextView(logo, bundle, skin)
    }
  }
}
```

**KTX** `AssetStorage`:

```Kotlin
class Application: ApplicationAdapter() {
  lateinit var assetStorage: AssetStorage

  override fun create() {
    enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
    assetStorage = AssetStorage()

    ktxAsync {
      assetStorage.apply { 
        val logo = load<Texture>("logo.png")
        val bundle = load<I18NBundle>("i18n.properties")
        val skin = load<Skin>("ui.json")
        // Assets loaded:
        goToNextView(logo, bundle, skin)
      }
    }
  }

  override fun render() {
    // Render loading prompt. Note that rendering is not blocked by the loading.
  }
}
```

Feature | `AssetStorage` | `AssetManager`
--- | --- | ---
*Asynchronous loading* | **Supported.** Asset loading is performed on a separate thread, while the main rendering thread is suspended (_not blocked_) and resumed once the asset is fully loaded | **Supported.** All assets are loaded on a separate thread and are available after the loading is finished.
*Synchronous loading* | **Limited.** A blocking coroutine can be launched to load assets eagerly, but it cannot block the rendering thread. | **Limited.** `finishLoading(String fileName)` method can be used to block the thread until the asset is loaded, but since it has no effect on loading order, all _other_ assets can be loaded before the requested one.
*Error handling* | **Build-in language syntax.** Use a regular try-catch block within coroutine body to handle loading errors. Provides a clean way to separately handle exceptions thrown by different assets. | **Via listener.** One can register a global error handling listener that will be notified if a loading exception is thrown.
*Loading order* | **Controlled by the user.** `AssetStorage` starts loading assets as soon as the `load` method is called, giving the user full control over the order of asset loading. | **Unpredictable.** If multiple assets are scheduled at once it is difficult to reason about loading order. `finishLoading` has no effect on loading order.
*Thread safety* | **Good.** Forces `ktx-async` threading model based on coroutines. Executes blocking IO operations on a separate thread and - when necessary - finishes loading on main rendering thread. Trying to load the _same_ asset via multiple asynchronous coroutines can cause problems, though. | **Good.** Achieved through synchronizing most assets-related operations, which unfortunately blocks the threads. Thread blocking might affect application performance, especially since even the `get` method is synchronized.
*Progress tracking* | **Limited.** Since `AssetStorage` does not force the users to schedule loading of all assets up front, it does not know the exact percent of loaded assets. It provides only the name of currently loaded asset. Progress can be tracked externally. | **Supported.** Since all loaded assets have to be scheduled up front, `AssetManager` can track total loading progress.
*Usage* | **Launch coroutine, load assets, use them as soon as loaded.** Asynchronous complexity is "hidden" by coroutines. | **Schedule loading, update in loop until loaded, extract from manager.** API based on polling (_are you done yet?_) rather than callbacks, which might prove tedious during loading phase.

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

A simple application using `AssetStorage` to load a text file and print it into the console:

```Kotlin
import com.badlogic.gdx.ApplicationAdapter
import ktx.async.enableKtxCoroutines
import ktx.async.ktxAsync
import ktx.async.assets.AssetStorage

class App : ApplicationAdapter() {
  lateinit var storage: AssetStorage

  override fun create() {
    enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
    storage = AssetStorage()

    ktxAsync {
      val text = storage.load<String>("text.txt")
      println(text)
    }
  }

  override fun render() {
    println("Loading: " + (storage.currentlyLoadedAsset ?: "done!"))
  }

  override fun dispose() {
    storage.dispose { path, error ->
      println("Unable to dispose $path due to $error.")
    }
  }
}
```

Initiating an `AssetStorage`:

```Kotlin
import ktx.async.enableKtxCoroutines
import ktx.async.assets.AssetStorage

// Make sure to initiate coroutines context first - preferably in `create` method:
enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)

// Internal assets, default executor:
val storage = AssetStorage()

// Local assets, default executor:
val storage = AssetStorage(fileResolver = LocalFileHandleResolver())

// Internal assets, custom executor with 2 threads:
val storage = AssetStorage(executor = AsyncExecutor(2))

// Internal assets, default executor, no registered asset loaders:
val storage = AssetStorage(useDefaultLoaders = false)
```

Asynchronous assets loading:
```Kotlin
import ktx.async.ktxAsync

ktxAsync {
  // Assets have to be loaded inside a coroutine body:
  val text = storage.load<String>("file.txt")
  val image = storage.load<Texture>("image.png")
  // Note that asset loading does not block the rendering thread.
}
```

Retrieving fully loaded assets from the storage:

```Kotlin
val text = storage.get<String>("file.txt")
// `text` is String? - if asset is not fully loaded, it will return a null.

// You can use !! to explicitly say that the asset must be fully loaded at
// this point (might throw NPE if you're not correct):
val image: Texture = storage["image.png"]!!

// Using an alternative if asset is not loaded:
val text: String = storage.getOrElse("file.txt", "Alternative!")
```

Handling asynchronous assets loading exceptions:
```Kotlin
import ktx.async.ktxAsync

ktxAsync {
  try {
    val text = storage.load<String>("file.txt")
    println(text)
  } catch (exception: Exception) {
    // All asset loading exceptions are rethrown by the AssetStorage,
    // so you can handle the original loading exceptions with regular
    // try-catch blocks.
    exception.printStackTrace()
  }
}
```

### Alternatives

- Standard Kotlin coroutines libraries might be used along with custom thread pools. They do not offer the same level
of compatibility with existing LibGDX APIs though.
- [`ktx-assets`](../assets) provides utilities for the LibGDX asynchronous `AssetManager`. Even with the extensions, its
API might be still less convenient than **KTX** `AssetStorage` based on coroutines.

#### Additional documentation

- [LibGDX threading article.](https://github.com/libgdx/libgdx/wiki/Threading)
- [Coroutines language reference.](https://kotlinlang.org/docs/reference/coroutines.html)
- [Coroutines repository.](https://github.com/Kotlin/kotlin-coroutines)
