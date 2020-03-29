[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-assets-async.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-assets-async)

# KTX: asynchronous file loading

Asset manager using coroutines to load assets asynchronously.

### Why?

LibGDX provides an `AssetManager` class for loading and managing assets. Even with [KTX extensions](../assets),
`AssetManager` is not compatible with Kotlin concurrency model based on coroutines. While it does support
asynchronous asset loading, it uses only a single thread for asynchronous operations and achieves its thread
safety by synchronizing all of the methods. To achieve truly multi-threaded loading with multiple threads
for asynchronous loading, one must maintain multiple manager instances. Besides, it does not support
event listeners and its API relies on polling instead - one must repeatedly update its state until
the assets are loaded.

This **KTX** module brings an `AssetManager` alternative - `AssetStorage`. It leverages Kotlin coroutines
for asynchronous operations. It ensures thread safety by using a single non-blocking `Mutex` for
a minimal set of operations mutating its state, while supporting truly multi-threaded asset loading
on any `CoroutineScope`.

Feature | **KTX** `AssetStorage` | LibGDX `AssetManager`
--- | --- | ---
*Asynchronous loading* | **Supported.** Loading that can be done asynchronously is performed in the chosen coroutine context. Parts that require OpenGL context are performed on the main rendering thread. | **Supported.** Loading that can be performed asynchronously is done a dedicated thread, with necessary sections executed on the main rendering thread.
*Synchronous loading* | **Limited.** A blocking coroutine can be launched to selected assets eagerly, but it cannot block the rendering thread or loader threads to work correctly. | **Limited.** `finishLoading(String fileName)` method can be used to block the thread until the asset is loaded, but since it has no effect on loading order, all _other_ assets can be loaded before the requested one.
*Thread safety* | **Excellent.** Forces [`ktx-async`](../async) threading model based on coroutines. Executes blocking IO operations in a separate coroutine context and - when necessary - finishes loading on the main rendering thread. Same asset - or assets with same dependencies - can be safely scheduled for loading by multiple coroutines concurrently. Multi-threaded coroutine context can be used for asynchronous loading, possibly achieving loading performance boost. Concurrent `AssetStorage` usage is tested extensively by unit tests. | **Good.** Achieved through synchronizing most methods, which unfortunately blocks the threads that use them. Thread blocking might affect application performance, especially since even the basic `get` method is synchronized.
*Concurrency* | **Supported.** Multiple asset loading coroutines can be launched in parallel. Coroutine context used for asynchronous loading can have multiple threads that will be used concurrently. | **Not supported.** `update()` loads assets one by one. `AsyncExecutor` with a single thread is used internally by the `AssetManager`. To utilize multiple threads for loading, one must use multiple manager instances.
*Loading order* | **Controlled by the user.** `AssetStorage` starts loading assets as soon as the `load` method is called, giving the user full control over the order of asset loading. Selected assets can be loaded one after another or in parallel, depending on the need. | **Unpredictable.** If multiple assets are scheduled at once, it is difficult to reason about their loading order. `finishLoading` has no effect on loading order and instead blocks the thread until the asset is loaded.
*Exceptions* | **Customized.** All expected issues are given separate exception classes with common root type for easier handling. Each loading issue can be handled differently. | **Generic.** Throws either `GdxRuntimeException` or a built-in Java runtime exception. Specific issues are difficult to handle separately.
*Error handling* | **Build-in language syntax.** Use a regular try-catch block within coroutine body to handle loading errors. Provides a clean way to handle exceptions thrown by each asset separately. | **Via listener.** One can register a global error handling listener that will be notified if a loading exception is thrown. Flow of the application is undisturbed, which makes it difficult to handle exceptions.
*File name collisions* | **Multiple assets of different types can be loaded from same path.** For example, you can load both a `Texture` and a `Pixmap` from the same PNG file. | **File paths act as unique identifiers.** `AssetManager` cannot store multiple assets with the same path, even if they have different types.
*Progress tracking* | **Limited.** Since `AssetStorage` does not force the users to schedule loading of all assets up front, it does not know the exact percent of loaded assets. Progress must be tracked externally. | **Supported.** Since all loaded assets have to be scheduled up front, `AssetManager` can track total loading progress.
*Usage* | **Launch coroutine, load assets, use as soon as loaded.** Asynchronous complexity is hidden by the coroutines and looks like regular synchronous code. | **Schedule loading, update in loop until loaded, extract from manager.** API based on polling _(are you done yet?)_ rather than callbacks, which might prove tedious during loading phase. Event listeners or callbacks are not supported.

#### Usage comparison

Here's an example of a simple application that loads three assets and switches to the next view,
passing the loaded assets.

Implemented using LibGDX `AssetManager`:

```kotlin
class Application: ApplicationAdapter() {
  private lateinit var assetManager: AssetManager

  override fun create() {
    assetManager = AssetManager().apply {
      // Assets scheduled for loading up front. Notice no returns.
      load("logo.png", Texture::class.java)
      load("i18n.properties", I18NBundle::class.java)
      load("ui.json", Skin::class.java)
    }
  }

  override fun render() {
    // Manager requires constant updating: 
    if (assetManager.update()) {
      // When update() returns true, all scheduled assets are loaded:
      finishLoading()
    }
    // Render loading prompt here.
    // Other than slight performance impact of calling update() each frame,
    // AssetManager does not block the rendering thread.
  }

  private fun finishLoading() {
    assetManager.apply {
      // Assets have to be retrieved from the manager manually:
      val logo = get<Texture>("logo.png")
      val bundle = get<I18NBundle>("i18n.properties")
      val skin = get<Skin>("ui.json")
      goToNextView(logo, bundle, skin)
    }
  }

  private fun goToNextView(logo: Texture, bundle: I18NBundle, skin: Skin) { TODO() }
}
```

The same use case rewritten with **KTX** `AssetStorage`:

```kotlin
class Application: ApplicationAdapter() {
  override fun create() {
    // ktx-async module requires initiating Kotlin coroutines context:
    KtxAsync.initiate()
    val assetStorage = AssetStorage()

    // Launching asynchronous coroutine that will _not_ block the rendering thread:
    KtxAsync.launch {
      assetStorage.apply {
        // Loading assets. Notice the immediate returns.
        // The coroutine will suspend until each asset is loaded:
        val logo = load<Texture>("logo.png")
        val bundle = load<I18NBundle>("i18n.properties")
        val skin = load<Skin>("ui.json")
        // Assets are loaded and we already have references to all of them:
        goToNextView(logo, bundle, skin)
      }
    }
  }

  override fun render() {
    // Render loading prompt. AssetStorage does not block the rendering thread.
  }

  private fun goToNextView(logo: Texture, bundle: I18NBundle, skin: Skin) { TODO() }
}
```

Without the polling-based `AssetManager` API with its constant `update()` calls and
non-returning `load`, application using `AssetStorage` is shorter and easier to read.

After the assets are loaded, `AssetStorage` and `AssetManager` behave more or less the same:
they store assets mapped by their file path that can be retrieved or disposed on demand.
In case of the `AssetManager`, assets are uniquely identified by their paths; `AssetStorage`
identifies assets by their paths and types, i.e. you can load multiple assets with different
classes from the same file.

The key difference between **KTX** storage and LibGDX manager is the threading model:
`AssetStorage` provides suspending methods executed via coroutines that resume the
coroutine as soon as the asset is loaded, while `AssetManager` requires scheduling
of asset loading up front, continuous updating until the assets are loaded and
retrieving the assets once the loading is finished. `AssetManager` leverages
a single thread for asynchronous loading operations, while `AssetStorage` can utilize
any chosen number of threads by specifying a coroutine context.

### Guide

#### Setup

See [`ktx-async`](../async) setup section to enable coroutines in your project.

`KtxAsync.initiate()` must be called on the main rendering thread before `AssetStorage` is used.

#### API

`AssetStorage` contains the following core methods:

- `get: Deferred<T>` - returns a `Deferred` reference to the asset if it was scheduled for loading.
Suspending `await()` can be called to obtain the asset instance. `isCompleted` can be used to check
if the asset loading was finished.
- `load: T` _(suspending)_ - schedules asset for asynchronous loading. Suspends the coroutine until
the asset is fully loaded. Resumes the coroutine and returns the asset once it is loaded.
- `unload: Boolean` _(suspending)_ - unloads the selected asset. If the asset is no longer referenced,
it will be removed from the storage and disposed of. Suspends the coroutine until the asset is unloaded.
Returns `true` is the selected asset was present in storage or `false` if the asset was absent.
- `add` _(suspending)_ - manually adds a fully loaded asset to storage. The storage will take care of
disposing of the asset.
- `dispose` (blocking and suspending variants available) - unloads all assets. Cancels all current
loadings. Depending on the variant, will block the current thread or suspend the coroutine until
all of the assets are unloaded.

Additional debugging and management methods are available:

- `getLoader` - allows to obtain `AssetLoader` instance for the given file.
- `setLoader` - allows to associate a custom `AssetLoader` with the selected file and asset types.
- `isLoaded: Boolean` - checks if the selected asset is fully loaded.
- `contains: Boolean` - checks if the selected asset is present in storage, loaded or not.
- `getReferenceCount: Int` - allows to check how many times the asset was loaded, added or required
as dependency by other assets. Returns 0 if the asset is not present in storage.
- `getDependencies: List<Identifier>` - returns list of dependencies of the selected asset.
If the asset is not present in the storage, an empty list will be returned.

Assets are uniquely identified by their path and `Class` by the storage.
Since these values can be passed in 3 basic ways, most methods are available in 3 variants:

- Inlined, with reified type and `String` path parameter.
- With `Identifier` parameter, which stores `Class` and path of the asset.
- With LibGDX `AssetDescriptor` storing `Class`, path and loading data of the asset.

All three variants behave identically and are available for convenience.
To ease the API usage, the following utilities are provided:

- `AssetStorage.getAssetDescriptor` - creates an `AssetDescriptor` instance that has loading data of an asset.
- `AssetStorage.getIdentifier` - creates an `Identifier` instance that uniquely identifies a stored asset.
- `AssetDescriptor.toIdentifier` - converts an `AssetDescriptor` to an `Identifier`.

### Usage examples

Creating an `AssetStorage` with default settings:

```kotlin
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun create() {
  // Necessary to initiate the coroutines context:
  KtxAsync.initiate()
  val assetStorage = AssetStorage()
}
```

Customizing `AssetStorage`. In this example a multi-threaded coroutine context
was assigned to storage, so the assets will be loaded in parallel on multiple threads:

```kotlin
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext

fun create() {
  KtxAsync.initiate()
  val assetStorage = AssetStorage(
    // Used to asynchronous file loading:
    asyncContext = newAsyncContext(threads = 4),
    // Used for resolving file paths:
    fileResolver = InternalFileHandleResolver(),
    // Whether to add standard LibGDX loaders for common assets:
    useDefaultLoaders = true
  )
}
```

TODO

### Alternatives

There seem to be no other coroutines-based asset loaders available.
However, LibGDX `AssetManager` is still viable when multi-threading is not a requirement.
Alternatives include:

- Using [`AssetManager`](https://github.com/libgdx/libgdx/wiki/Managing-your-assets) directly.
- Using [`ktx-assets`](../assets) extensions for `AssetManager`.
- [`AnnotationAssetManager`](https://bitbucket.org/dermetfan/libgdx-utils/wiki/net.dermetfan.gdx.assets.AnnotationAssetManager)
from [`libgdx-utils`](https://bitbucket.org/dermetfan/libgdx-utils) that extends `AssetManager` and allows
to specify assets for loading by marking fields with annotations. However, it's annotation-based API relies
on reflection and is not really idiomatic in Kotlin.

#### Additional documentation

- [`ktx-async` module](../async), which is used extensively by this extension.
- [Official `AssetManager` article.](https://github.com/libgdx/libgdx/wiki/Managing-your-assets)
