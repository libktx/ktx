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
on any `CoroutineContext`.

Feature | **KTX** `AssetStorage` | LibGDX `AssetManager`
--- | --- | ---
*Asynchronous loading* | **Supported.** Loading that can be done asynchronously is performed in the chosen coroutine context. Parts that require OpenGL context are performed on the main rendering thread. | **Supported.** Loading that can be performed asynchronously is done a dedicated thread, with necessary sections executed on the main rendering thread.
*Synchronous loading* | **Limited.** A blocking coroutine can be launched to selected assets eagerly, but it cannot block the rendering thread or loader threads to work correctly. | **Limited.** `finishLoading(String fileName)` method can be used to block the thread until the asset is loaded, but since it has no effect on loading order, all _other_ assets can be loaded before the requested one.
*Thread safety* | **Excellent.** Forces [`ktx-async`](../async) threading model based on coroutines. Executes blocking IO operations in a separate coroutine context and - when necessary - finishes loading on the main rendering thread. Same asset - or assets with same dependencies - can be safely scheduled for loading by multiple coroutines concurrently. Multi-threaded coroutine context can be used for asynchronous loading, possibly achieving loading performance boost. Concurrent `AssetStorage` usage is tested extensively by unit tests. | **Good.** Achieved through synchronizing most methods, which unfortunately blocks the threads that use them. Thread blocking might affect application performance, especially since even the basic `get` method is synchronized. Some operations, such as `update` or `finishLoading`, must be called from specific threads (i.e. rendering thread).
*Concurrency* | **Supported.** Multiple asset loading coroutines can be launched in parallel. Coroutine context used for asynchronous loading can have multiple threads that will be used concurrently. | **Not supported.** `update()` loads assets one by one. `AsyncExecutor` with a single thread is used internally by the `AssetManager`. To utilize multiple threads for loading, one must use multiple manager instances.
*Loading order* | **Controlled by the user.** `AssetStorage` starts loading assets as soon as the `load` method is called, giving the user full control over the order of asset loading. Selected assets can be loaded one after another within a single coroutine or in parallel with multiple coroutines, depending on the need. | **Unpredictable.** If multiple assets are scheduled at once, it is difficult to reason about their loading order. `finishLoading` has no effect on loading order and instead blocks the thread until the asset is loaded.
*Exceptions* | **Customized.** All expected issues are given separate exception classes with common root type for easier handling. Each loading issue can be handled differently. | **Generic.** Throws either `GdxRuntimeException` or a built-in Java runtime exception. Specific issues are difficult to handle separately.
*Error handling* | **Build-in language syntax.** A regular try-catch block within coroutine body can be used to handle loading errors. Provides a clean way to handle exceptions thrown by each asset separately. | **Via listener.** One can register a global error handling listener that will be notified if a loading exception is thrown. Flow of the application is undisturbed, which makes it difficult to handle exceptions of specific assets.
*File name collisions* | **Multiple assets of different types can be loaded from same path.** For example, you can load both a `Texture` and a `Pixmap` from the same PNG file. | **File paths act as unique identifiers.** `AssetManager` cannot store multiple assets with the same path, even if they have different types.
*Progress tracking* | **Limited.** Since `AssetStorage` does not force the users to schedule loading of all assets up front, it does not know the exact percent of the loaded assets. Progress must be tracked externally. | **Supported.** Since all loaded assets have to be scheduled up front, `AssetManager` can track total loading progress.
*Usage* | **Launch coroutine, load assets, use as soon as loaded.** Asynchronous complexity is hidden by the coroutines. | **Schedule loading, update in loop until loaded, extract from manager.** API based on polling _(are you done yet?),_ which might prove tedious during loading phase. Loading callbacks are available, but have obscure API and still require constant updating of the manager.

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
    // Other than performance impact of calling synchronized update() each frame,
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

- `get: T` - returns a loaded asset or throws `MissingAssetException` if the asset is unavailable.
- `getOrNull: T?` - returns a loaded asset or `null` if the asset is unavailable.
- `getAsync: Deferred<T>` - returns a `Deferred` reference to the asset. Suspending `await()` can be
called to obtain the asset instance. `isCompleted` can be used to check if the asset loading was finished.
- `load: T` _(suspending)_ - schedules asset for asynchronous loading. Suspends the coroutine until
the asset is fully loaded. Resumes the coroutine and returns the asset once it is loaded.
- `loadAsync: Deferred<T>` - schedules asset for asynchronous loading. Returns a `Deferred` reference
to the asset which will be completed after the loading is finished.
- `unload: Boolean` _(suspending)_ - unloads the selected asset. If the asset is no longer referenced,
it will be removed from the storage and disposed of. Suspends the coroutine until the asset is unloaded.
Returns `true` is the selected asset was present in storage or `false` if the asset was absent.
- `add` _(suspending)_ - manually adds a fully loaded asset to storage. The storage will take care of
disposing of the asset.
- `dispose` (blocking and suspending variants available) - unloads all assets. Cancels all current
loadings. Depending on the variant, will block the current thread or suspend the coroutine until
all of the assets are unloaded.

Additional asset management methods include:

- `isLoaded: Boolean` - checks if the selected asset is fully loaded.
- `contains: Boolean` - checks if the selected asset is present in storage, loaded or not.
- `getReferenceCount: Int` - allows to check how many times the asset was loaded, added or required
as dependency by other assets. Returns 0 if the asset is not present in the storage.
- `getDependencies: List<Identifier>` - returns list of dependencies of the selected asset.
If the asset is not present in the storage, an empty list will be returned.
- `getLoader: AssetLoader` - allows to obtain `AssetLoader` instance for the given file.
- `setLoader` - allows to associate a custom `AssetLoader` with the selected file and asset types.

`AssetStorage` uniquely identifies assets by their path and `Class`.
Since these values can be passed in 3 basic ways, most methods are available in 3 variants:

- Inlined, with reified type and `String` path parameter.
- With `Identifier` parameter, which stores `Class` and `String` path of the asset.
- With LibGDX `AssetDescriptor` storing `Class`, `String` file name and loading data of the asset.

All three variants behave identically and are available for convenience.
If any asset data is missing from either `String` path or `Identifier`, additional parameters are available
to match the `AssetDescriptor` API.

To ease the API usage, the following utilities are provided:

- `AssetStorage.getAssetDescriptor` - creates an `AssetDescriptor` instance that has loading data of an asset.
- `AssetStorage.getIdentifier` - creates an `Identifier` instance that uniquely identifies a stored asset.
- `AssetDescriptor.toIdentifier` - converts an `AssetDescriptor` to an `Identifier`.
- `Identifier.toAssetDescriptor` - converts an `Identifier` to an `AssetDescriptor` with optional loading parameters.

#### Error handling

`AssetStorage` throws exceptions extending the `AssetStorageException` class. All of its subclasses are documented,
explaining when and why they are thrown.
Please refer to the [sources documentation](src/main/kotlin/ktx/assets/async/errors.kt) for further details.

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

Customizing an `AssetStorage`. In this example a multi-threaded coroutine context
was assigned to storage, so the assets can be loaded in parallel on multiple threads:

```kotlin
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext

fun create() {
  KtxAsync.initiate()

  val assetStorage = AssetStorage(
    // Used to perform asynchronous file loading:
    asyncContext = newAsyncContext(threads = 4),
    // Used for resolving file paths:
    fileResolver = InternalFileHandleResolver(),
    // Whether to add standard LibGDX loaders for common assets:
    useDefaultLoaders = true
  )
}
```

Loading assets using `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun loadAsset(assetStorage: AssetStorage) {
  // Launching a coroutine to load the assets asynchronously:
  KtxAsync.launch {
    // This will suspend the coroutine until the texture is loaded:
    val texture = assetStorage.load<Texture>("images/logo.png")
    // Now the coroutine resumes and the texture can be used.
  }
}
```

Loading assets with customized loading parameters:

```kotlin
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun loadAsset(assetStorage: AssetStorage) {
  KtxAsync.launch {
    // You can optionally specify loading parameters for each asset.
    // AssetStorage reuses default LibGDX asset loaders and their
    // parameters classes.
    val texture = assetStorage.load<Texture>(
      path = "images/logo.png",
      parameters = TextureParameter().apply {
        genMipMaps = true
      }
    )
    // Now the texture is loaded and can be used.
  }
}
```

Loading assets sequentially using `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun loadAssets(assetStorage: AssetStorage) {
  // Unlike AssetManager, AssetStorage allows to easily control the order of loading.
  KtxAsync.launch {
    // This will suspend the coroutine until the texture is loaded:
    val logo = assetStorage.load<Texture>("images/logo.png")
    // After the first image is loaded,
    // the coroutine resumes and loads the second asset:
    val background = assetStorage.load<Texture>("images/background.png")

    // Now both images are loaded and can be used.
  }
}
```

Loading assets asynchronously:

```kotlin
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun loadAssets(assetStorage: AssetStorage) {
  // You can also schedule the assets for asynchronous loading
  // without suspending the coroutine.
  KtxAsync.launch {
    // Launching asynchronous asset loading with Kotlin's built-in `async`:
    val texture = async { assetStorage.load<Texture>("images/logo.png") }
    val font = async { assetStorage.load<BitmapFont>("fonts/font.fnt") }

    // Suspending the coroutine until both assets are loaded:
    doSomethingWithTextureAndFont(texture.await(), font.await())
  }
}
```

Loading assets in parallel:

```kotlin
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext

fun loadAssets() {
  // Using Kotlin's `async` will ensure that the coroutine is not
  // immediately suspended and assets are scheduled asynchronously,
  // but to take advantage of really parallel asset loading, we have
  // to pass a context with multiple loading threads to AssetStorage:
  val assetStorage = AssetStorage(asyncContext = newAsyncContext(threads = 2))

  // Instead of using Kotlin's built-in `async`, you can also use
  // the `loadAsync` method of AssetStorage with is a shortcut for
  // `async(assetStorage.asyncContext) { assetStorage.load }`:
  val texture = assetStorage.loadAsync<Texture>("images/logo.png")
  val font = assetStorage.loadAsync<BitmapFont>("fonts/font.fnt")
  // Now both assets will be loaded asynchronously, in parallel.

  KtxAsync.launch {
    // Suspending the coroutine until both assets are loaded:
    doSomethingWithTextureAndFont(texture.await(), font.await())
  }
}
```

Unloading assets from `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun unloadAsset(assetStorage: AssetStorage) {
  KtxAsync.launch {
    // Suspends the coroutine until the asset is unloaded:
    assetStorage.unload<Texture>("images/logo.png")
    // When the coroutine resumes here, the asset is unloaded.
    // If no other assets use it as dependency, it will
    // be removed from the asset storage and disposed of.

    // Note that you can also do this asynchronously if you don't
    // want to suspend the coroutine:
    async { assetStorage.unload<Texture>("images/logo.png") }
  }
}
```

Accessing assets from `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun accessAsset(assetStorage: AssetStorage) {
  // Typically you can simply use assets returned by `load`,
  // but AssetStorage also allows you to access assets
  // already loaded by other coroutines.

  // Immediately returns loaded asset or throws an exception if missing:
  var texture = assetStorage.get<Texture>("images/logo.png")
  // Immediately returns loaded asset or returns null if missing:
  val textureOrNull = assetStorage.getOrNull<Texture>("images/logo.png")

  // Returns true is asset is in the storage, loaded or not:
  assetStorage.contains<Texture>("images/logo.png")
  // Returns true if the asset loading has finished:
  assetStorage.isLoaded<Texture>("images/logo.png")
  // Checks how many times the asset was loaded or used as a dependency:
  assetStorage.getReferenceCount<Texture>("images/logo.png")
  // Returns a list of dependencies loaded along with the asset:
  assetStorage.getDependencies<Texture>("images/logo.png")
  
  KtxAsync.launch {
    // There is also a special way to access your assets within coroutines
    // when you need to wait until they are loaded asynchronously.

    // When calling `getAsync`, AssetStorage will not throw an exception
    // or return null if the asset is still loading. Instead, it will
    // return a Kotlin Deferred reference. This allows you suspend the
    // coroutine until the asset is loaded:
    val asset: Deferred<Texture> = assetStorage.getAsync("images/logo.png")
    // Checking if the asset loading has finished:
    asset.isCompleted
    // Suspending the coroutine to obtain asset instance:
    texture = asset.await()
    
    // If you want to suspend the coroutine to wait for the asset,
    // you can do this in a single line:
    texture = assetStorage.getAsync<Texture>("images/logo.png").await()
    
    // Now the coroutine is resumed and `texture` can be used.
  }
}
```

Adding a fully loaded asset manually to the `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun addAsset(assetStorage: AssetStorage) {
  KtxAsync.launch {
    // You can store arbitrary objects in AssetStorage.
    // They will be marked as loaded and accessible with `get`.
    // If they are disposable, calling `unload` will also
    // dispose of these assets. This might be useful for
    // expensive objects such as the Batch.
    val batch: Batch = SpriteBatch()
    // Suspending the coroutine until the `batch` is added:
    assetStorage.add("batch", batch)
    // Now our `batch` will be available under "batch" path.
  }
}
```

Disposing of all assets stored by `AssetStorage`:

```kotlin
// Will block the current thread to unload all assets:
assetStorage.dispose()

// This will also disrupt loading of all unloaded assets.
// Disposing errors are logged by default, and do not stop the process.
```

Disposing of all assets asynchronously:

```kotlin
import com.badlogic.gdx.Gdx
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun unloadAllAssets(assetStorage: AssetStorage) {
  KtxAsync.launch {
    // Suspends the coroutine until all assets are disposed of:
    assetStorage.dispose { identifier, exception ->
      // This lambda will be invoked for each encountered disposing error:
      Gdx.app.error("KTX", "Unable to dispose of asset: $identifier", exception)
    }
  }
}
```

Loading assets with error handling:

```kotlin
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import ktx.assets.async.AssetLoadingException
import ktx.assets.async.AssetStorage
import ktx.assets.async.AssetStorageException
import ktx.async.KtxAsync

fun loadAsset(assetStorage: AssetStorage) {
  KtxAsync.launch {
    // You can handle loading errors with a classic try-catch block:
    try {
      val texture = assetStorage.load<Texture>("images/logo.png")
    } catch (exception: AssetLoadingException) {
      // Asset loader threw an exception - unable to load the asset.
    } catch (exception: AssetStorageException) {
      // Another error occurred. See AssetStorageException subclasses. 
    }

    // Note that if the asset loading ended with an exception,
    // the same exception will be rethrown each time the asset
    // is accessed with `get`, `getOrNull`, `getAsync.await` or `load`.
  }
}
```

Tracking loading progress:

```kotlin
import ktx.assets.async.AssetStorage

fun trackProgress(assetStorage: AssetStorage) {
  // Current loading progress can be tracked with the `progress` property:
  val progress = assetStorage.progress

  // Total number of scheduled assets:
  progress.total
  // Total number of loaded assets:
  progress.loaded
  // Current progress percentage in [0, 1]:
  progress.percent
  // Checks if all scheduled assets are loaded:
  progress.isFinished

  // Remember that due to the asynchronous nature of AssetStorage,
  // the progress is only _eventually consistent_ with the storage.
  // It will not know about the assets that are not fully scheduled
  // for loading yet. Use progress for display only and base your
  // application logic on coroutine callbacks instead.
}
```

Adding a custom `AssetLoader` to `AssetStorage`:

```kotlin
import ktx.assets.async.AssetStorage

fun createCustomAssetStorage(): AssetStorage {
  val assetStorage = AssetStorage()
  // Custom asset loaders should be added before loading any assets:
  assetStorage.setLoader(suffix = ".file.extension") {
    MyCustomAssetLoader(assetStorage.fileResolver)
  }
  // Remember to extend one of:
  // com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
  // com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader

  return assetStorage
}
```

#### Implementation notes

##### Multiple calls of `load`, `loadAsync` and `unload`

It is completely safe to call `load` and `loadAsync` multiple times with the same asset data, even just to obtain
asset instances. In that sense, they can be used as an alternative to `getAsync` inside coroutines.

Instead of loading the same asset multiple times, `AssetStorage` will just increase the reference count
to the asset and return the same instance on each request. This also works concurrently - the storage will
always load just _one_ asset instance, regardless of how many different threads and coroutines called `load`
in parallel.

However, to eventually unload the asset, you have to call `unload` the same number of times as `load`/`loadAsync`,
or simply dispose of all assets with `dispose`, which clears all reference counts and unloads everything
from the storage.

Unlike `load` and `loadAsync`, `add` should be called only once on a single asset, and when it is not stored
in the storage. You cannot add existing assets to the storage, even if they were previously loaded by it.
This is because if we are not sure that `AssetStorage` handled the loading (or creation) of an object, tracking
its dependencies and lifecycle is difficult and left to the user. Trying to `add` an asset with existing path
and type will not increase its reference count, and will throw an exception instead.

Adding assets that were previously loaded by the `AssetStorage` under different paths is also a misuse of the API
which might result in unloading the asset or its dependencies prematurely. True aliases are currently unsupported.

##### `runBlocking`

Kotlin's `runBlocking` function allows to launch a coroutine and block the current thread until the coroutine
is finished. In general, you should **avoid** `runBlocking` calls from the main rendering thread or the threads
assigned to `AssetStorage` for asynchronous loading.

This simple example will cause a deadlock:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.runBlocking
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

fun main() {
  LwjglApplication(App())
}

class App : ApplicationAdapter() {
  override fun create() {
    KtxAsync.initiate()
    val assetStorage = AssetStorage()
    runBlocking { // <- !!! Do NOT do this. !!!
      assetStorage.load<Texture>("images/logo.png")
    }
    println("Will never be printed.")
  }
}
```

This is because `AssetStorage` needs access to the main rendering thread to finish loading the `Texture`
with OpenGL context, but we have blocked the main rendering thread to wait for the asset.

In a similar manner, this example blocks the only thread assigned to `AssetStorage` for asynchronous operations:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

fun main() {
  LwjglApplication(App())
}

class App : ApplicationAdapter() {
  override fun create() {
    KtxAsync.initiate()
    val asyncContext = newSingleThreadAsyncContext()
    val assetStorage = AssetStorage(asyncContext = asyncContext)
    // Launching coroutine on the storage thread:
    KtxAsync.launch(asyncContext) {
      runBlocking { // <- !!! Do NOT do this. !!!
        assetStorage.load<Texture>("images/logo.png")
      }
      println("Will never be printed and AssetStorage will be unusable.")
    }
  }
}
```

As a rule of thumb, you should use suspending `AssetStorage` methods only from non-blocking coroutines, e.g. those
launched with `KtxAsync.launch` or `GlobalScope.launch`. If you change `runBlocking` to a proper coroutine launch
in either of the examples, you will notice that the deadlocks no longer occur.

It does not mean that `runBlocking` will always cause a deadlock, however. You can safely use `runBlocking`:

- For `dispose`, both suspending and non-suspending variants.
- For all non-suspending methods such as `get`, `getOrNull`, `contains`, `isLoaded`, `setLoader`, `getLoader`.
- For `add`. While `add` does suspend the coroutine, it requires neither the rendering thread nor the loading threads.
- For `load` and `get.await` calls requesting already loaded assets. **Use with caution.**
- From within other threads than the main rendering thread and the `AssetStorage` loading threads. These threads
will be blocked until the operation is finished, which isn't ideal, but at least the loading will remain possible.

##### Asynchronous operations

Most common operations - `get` and `load` - offer both synchronous/suspending and asynchronous variants.
To perform other methods asynchronously, use `KtxAsync.launch` if you do not need the result or `KtxAsync.async`
to get a `Deferred` reference to the result which will be completed after the operation is finished.

```kotlin
// Unloading an asset asynchronously:
KtxAsync.launch { storage.unload<AssetType>(path) }
```

##### `AssetStorage` as a drop-in replacement for `AssetManager`

Consider this typical application using `AssetManager`:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont

class WithAssetManager: ApplicationAdapter() {
  private lateinit var assetManager: AssetManager

  override fun create() {
    assetManager = AssetManager()

    // Scheduling assets for asynchronous loading:
    assetManager.load("images/logo.png", Texture::class.java)
    assetManager.load("com/badlogic/gdx/utils/arial-15.fnt", BitmapFont::class.java)
  }

  override fun render() {
    // Manager has to be constantly updated until the assets are loaded:
    if (assetManager.update()) {
      // Now the assets are loaded:
      changeView()
    }
    // Render loading prompt.
  }

  private fun changeView() {
    val texture: Texture = assetManager["images/logo.png"]
    TODO("Now the assets are loaded and can be accessed with $assetManager.get!")
  }
}
```

Since usually applications have more assets than just 2, many developers choose to treat `AssetManager` as a map
of loaded assets with file paths as keys and loaded assets are values. You typically call load all or most assets
on the loading screen and then just use `AssetManager.get(path)` to obtain the assets after they are loaded.

However, this approach has some inconveniences and problems:

- The API is not very idiomatic to Kotlin, but in this particular case [ktx-assets](../assets) can help with that.
- `update` has to be called on render during loading.
- If you forget to stop updating the manager after the assets are loaded, the initiation code (such as `changeView`
in our example) can be ran multiple times. If you replace `TODO` with `println` in the example, you will notice that
`changeView` is invoked on every render after the loading is finished.
- The majority of `AssetManager` methods are `synchronized`, which means they block the thread that they are
executed in and are usually more expensive to call than regular methods. This includes the `get` method, which does
not change the internal state of the manager at all. Even if the assets are fully loaded and you no longer modify
the `AssetManager` state, you still pay the cost of synchronization. This is especially relevant if you use multiple
threads, as they can block each other waiting for the assets.
- `AssetManager` stores assets mapped only by their paths. `manager.get<Texture>(path)` and `manager.get<Pixmap>(path)`
are both valid calls that will throw a runtime class cast exception.

`AssetStorage` avoids most of these problems.

Similarly to `AssetManager`, `AssetStorage` offers API to `get` your loaded assets, so if you want to migrate
from `AssetManager` to `AssetStorage`, all you have to change initially is the loading code:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext

class WithAssetStorageBasic: ApplicationAdapter() {
  // Using multiple threads to load the assets in parallel:
  private val assetStorage = AssetStorage(newAsyncContext(threads = 2))
  
  override fun create() {
    KtxAsync.initiate()

    // Scheduling assets for asynchronous loading:
    assetStorage.loadAsync<Texture>("images/logo.png")
    assetStorage.loadAsync<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt")
  }

  override fun render() {
    // The closest alternative to `update` would be to check the progress on each render:
    if (assetStorage.progress.isFinished) {
      changeView()
    }
  }

  private fun changeView() {
    val texture: Texture = assetStorage["images/logo.png"]
    println(texture)
    TODO("Now the assets are loaded and you can get them from $assetStorage.get!")
  }
}
```

As you can see, after the assets are loaded, the API of both `AssetManager` and `AssetStorage` is very similar.

Now, this example looks almost identically to the `AssetManager` code and it might just work in some cases,
but there are two things we should address:

- You will notice that your IDE warns you about not using the results of `loadAsync` which return `Deferred` instances.
This means we're launching asynchronous coroutines and ignore their results.
- `AssetStorage.progress` should be used only for display and debugging. You generally should not base your application
logic on `progress`, as it is only _eventually consistent_ with the `AssetStorage` state.

Let's rewrite it again - this time with coroutines:

```kotlin
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext

class WithAssetStorage: ApplicationAdapter() {
  // Using multiple threads to load the assets in parallel:
  private val assetStorage = AssetStorage(newAsyncContext(threads = 2))

  override fun create() {
    KtxAsync.initiate()

    // Scheduling assets for asynchronous loading:
    val assets = listOf(
      assetStorage.loadAsync<Texture>("images/logo.png"),
      assetStorage.loadAsync<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt")
    )

    // Instead of constantly updating or checking the progress,
    // we're launching a coroutine that "waits" for the assets:
    KtxAsync.launch {
      // Suspending coroutine until all assets are loaded:
      assets.joinAll()
      // Resuming! Now the assets are loaded and we can obtain them with `get`:
      changeView(assetStorage)
    }
  }

  private fun changeView() {
    val texture: Texture = assetStorage["images/logo.png"]
    TODO("Now the assets are loaded and can be accessed with $assetStorage.get!")
  }
}
```

The code using `AssetStorage` is not necessarily shorter in this case, but:

- You get the performance improvements of loading assets in parallel.
- `AssetStorage` does not have to be updated on render.
- Your code is reactive and `changeView` is called only once as soon as the assets are loaded.
- You can easily integrate more coroutines into your application later for other asynchronous operations.
- `AssetStorage.get` is non-blocking and faster than `AssetManager.get`. `AssetStorage` does a better job of storing
your assets efficiently after loading.
- `AssetStorage` stores assets mapped by their path _and_ type. You will not have to deal with class cast exceptions.

Besides, you get the additional benefits of other `AssetStorage` features and methods described in this file.

##### Integration with LibGDX and known unsupported features

`AssetStorage` does its best to integrate with LibGDX APIs - including the `AssetLoader` implementations, which were
designed for the `AssetManager`. [A dedicated wrapper](src/main/kotlin/ktx/assets/async/wrapper.kt) extends and
overrides `AssetManager`, delegating a subset of supported methods to `AssetStorage`. The official `AssetLoader`
implementations use supported methods such as `get`, but please note that some third-party loaders might not
work out of the box with `AssetStorage`. Exceptions related to broken loaders include `UnsupportedMethodException`
and `MissingDependencyException`.

`AssetStorage`, even with its wrapper, cannot be used as drop-in replacement for `AssetManager` throughout the
official APIs. In particular, `Texture.setAssetManager` and `Cubemap.setAssetManager` are both unsupported.

If you heavily rely on these unsupported APIs or custom asset loaders, you might need to use `AssetManager` instead.
See [`ktx-assets`](../assets) module for `AssetManager` utilities.

#### Synergy

While [`ktx-assets`](../assets) module does provide some extensions to the `AssetManager`, which is a direct
alternative to the `AssetStorage`, this module's other utilities for LibGDX assets and files APIs might still
prove useful.

### Alternatives

There seem to be no other coroutines-based asset loaders available.
However, LibGDX `AssetManager` is still viable when efficient parallel loading is not a requirement.
Alternatives include:

- Using [`AssetManager`](https://github.com/libgdx/libgdx/wiki/Managing-your-assets) directly.
- Using [`ktx-assets`](../assets) extensions for `AssetManager`.
- [`AnnotationAssetManager`](https://bitbucket.org/dermetfan/libgdx-utils/wiki/net.dermetfan.gdx.assets.AnnotationAssetManager)
from [`libgdx-utils`](https://bitbucket.org/dermetfan/libgdx-utils) that extends `AssetManager` and allows
to specify assets for loading by marking fields with annotations.
- Loading assets without a manager.

#### Additional documentation

- [`ktx-async` module](../async), which is used extensively by this extension.
- [Official `AssetManager` article.](https://github.com/libgdx/libgdx/wiki/Managing-your-assets)
