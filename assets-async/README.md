[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-assets-async.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-assets-async)

# KTX: asynchronous file loading

Asset manager using coroutines to load assets asynchronously.

### Why?

LibGDX provides an `AssetManager` class for loading and managing assets. Even with [KTX extensions](../assets),
`AssetManager` is not fully compatible with Kotlin concurrency model based on coroutines due to thread blocking.
While it does support asynchronous asset loading, it uses only a single thread for asynchronous operations and
achieves its thread safety by synchronizing all of its methods. To achieve truly multi-threaded loading with
multiple threads for asynchronous loading, one must maintain multiple manager instances. Besides, its API relies
on polling - one must repeatedly update its state until the assets are loaded.

This **KTX** module brings an `AssetManager` alternative - `AssetStorage`. It leverages Kotlin coroutines
for asynchronous operations. It ensures thread safety by using a single non-blocking `Mutex` for
a minimal set of operations mutating its state, while supporting truly multi-threaded asset loading
on any `CoroutineContext`.

Feature | **KTX** `AssetStorage` | LibGDX `AssetManager`
--- | --- | ---
*Asynchronous loading* | **Supported.** Loading that can be done asynchronously is performed in the chosen coroutine context. Parts that require OpenGL context are performed on the main rendering thread. | **Supported.** Loading that can be performed asynchronously is done a dedicated thread, with necessary sections executed on the main rendering thread.
*Synchronous loading* | **Supported.** `loadSync` blocks the current thread until a selected asset is loaded. A blocking coroutine can also be launched to load selected assets eagerly, but it cannot block the rendering thread or loader threads to work correctly. | **Limited.** `finishLoading` method can be used to block the thread until the asset is loaded, but since it has no effect on loading order, it requires precise scheduling or it will block the thread until some or all unselected assets are loaded.
*Thread safety* | **Excellent.** Uses [`ktx-async`](../async) threading model based on coroutines. Executes blocking IO operations in a separate coroutine context and - when necessary - finishes loading on the main rendering thread. Same asset - or assets with same dependencies - can be safely scheduled for loading by multiple coroutines concurrently. Multi-threaded coroutine context can be used for asynchronous loading, possibly achieving loading performance boost. Concurrent `AssetStorage` usage is tested extensively by unit tests. | **Good.** Achieved through synchronizing most methods, which unfortunately blocks the threads that use them. Thread blocking might affect application performance, especially since even the basic `get` method is synchronized. Some operations, such as `update` or `finishLoading`, must be called from specific threads (i.e. rendering thread).
*Concurrency* | **Supported.** Multiple asset loading coroutines can be launched in parallel. Coroutine context used for asynchronous loading can have multiple threads that will be used concurrently. | **Limited.** `update()` loads assets one by one. `AsyncExecutor` with only a single thread is used internally by the `AssetManager`. To utilize multiple threads for loading, one must use multiple manager instances.
*Loading order* | **Controlled by the user.** With suspending `load`, synchronous `loadSync` and `Deferred`-returning `loadAsync`, the user can have full control over asset loading order and parallelization. Selected assets can be loaded one after another within a single coroutine or in parallel with multiple coroutines, depending on the need. | **Unpredictable.** If multiple assets are scheduled at once, it is difficult to reason about their loading order. `finishLoading` has no effect on loading order and instead blocks the thread until the selected asset is loaded.
*Exceptions* | **Customized.** All expected issues are given separate exception classes with common root type for easier handling. Each loading issue can be handled differently. | **Generic.** Throws either `GdxRuntimeException` or a built-in Java runtime exception. Specific issues are difficult to handle separately.
*Error handling* | **Build-in language syntax.** A regular try-catch block within coroutine body can be used to handle asynchronous loading errors. Provides a clean way to handle exceptions thrown by each asset separately. | **Via listener.** One can register a global error handling listener that will be notified if a loading exception is thrown. Flow of the application is undisturbed, which makes it difficult to handle exceptions of specific assets.
*File name collisions* | **Multiple assets of different types can be loaded from same path.** For example, you can load both a `Texture` and a `Pixmap` from the same PNG file. | **File paths act as unique identifiers.** `AssetManager` cannot store multiple assets with the same path, even if they have different types.
*Progress tracking* | **Supported with caveats.** `AssetStorage` does not force the users to schedule loading of all assets up front. To get the exact percent of loaded assets, all assets must be scheduled first (e.g. with `loadAsync`). | **Supported.** Since all loaded assets have to be scheduled up front, `AssetManager` can track total loading progress.
*Usage* | **Launch coroutine, load assets, use as soon as loaded.** Asynchronous complexity is hidden by the coroutines. | **Schedule loading, update in loop until loaded, extract from manager.** API based on polling _(are you done yet?),_ which might prove tedious during loading phase. Loading callbacks for individual assets are available, but have obscure API and still require constant updating of the manager.

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
 `AssetManager` leverages only a single thread for asynchronous loading operations and ensures
thread safety by relying on the `synchronized` methods,
while `AssetStorage` can utilize any chosen number of threads specified by its coroutine
context and uses non-blocking coroutines to load the assets.

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
- `loadSync: T` - blocks the current thread until the selected asset is loaded. Use _only outside of coroutines_
for crucial assets that need to be loaded synchronously (e.g. loading screen assets).
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
- `progress` - allows to access loading progress data.
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

Note that usage example require basic understanding of the [`ktx-async`](../async) module and Kotlin 
[coroutines](https://kotlinlang.org/docs/reference/coroutines.html).

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
  // If you specify the variable type, you can use the braces operator as well:
  val thisIsA: Texture = assetStorage["images/logo.png"]

  // Immediately returns loaded asset or returns null if missing:
  val textureOrNull = assetStorage.getOrNull<Texture>("images/logo.png")
  
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

Accessing additional data about an asset:

```kotlin
import com.badlogic.gdx.graphics.Texture
import ktx.assets.async.AssetStorage

fun inspectAsset(assetStorage: AssetStorage) {
  // Returns true if asset is in the storage, loaded or not:
  assetStorage.contains<Texture>("images/logo.png")
  // Returns true if the asset loading has finished:
  assetStorage.isLoaded<Texture>("images/logo.png")
  // Checks how many times the asset was loaded or used as a dependency:
  assetStorage.getReferenceCount<Texture>("images/logo.png")
  // Returns a list of dependencies loaded along with the asset:
  assetStorage.getDependencies<Texture>("images/logo.png")
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
    // Now our `batch` will be available under "batch" path
    // and `Batch` class.
  }
}
```

Loading assets _synchronously_ with `AssetStorage`:

```kotlin
import com.badlogic.gdx.graphics.g2d.BitmapFont

// Sometimes you need to load assets immediately and coroutines just get in the way.
// A common example of this would be getting the assets for the loading screen.
// In this case you can use `loadSync`, which will block the current thread until
// the asset is loaded:
val font = assetStorage.loadSync<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt")

// Note that you should not use `loadSync` from within coroutines.

// Whenever possible, prefer `load` or `loadAsync`. Try not to mix synchronous and
// asynchronous loading, especially on the same assets or assets with same dependencies.
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

Loading assets with custom error handling:

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

##### Multiple calls of `load`, `loadAsync`, `loadSync` and `unload`

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

`loadSync` can also be used multiple times on the same asset in addition to `load` and `loadAsync`, but keep in mind
that mixing synchronous and asynchronous loading can cause loading exceptions. In particular, `loadSync` will not wait
for asset dependencies loaded by other asynchronous coroutines and will raise an exception instead. As a rule of thumb,
it is best not to mix synchronous and asynchronous loading of the same asset or assets with the same dependencies.

##### Loading methods comparison: `load` vs `loadAsync` vs `loadSync`

Loading method | Return type | Suspending | Loading type | When to use
:---: | :---: | :---: | --- | ---
`load` | `Asset` | Yes | Asynchronous. Schedules asset loading and awaits for the loaded asset, suspending the coroutine. | Within coroutines for controlled, sequential loading.
`loadAsync` | `Deferred<Asset>` | No, but can be with `Deferred.await` | Asynchronous. Schedules asset loading and returns a `Deferred` reference to the asset. | Outside of coroutines: for scheduling of assets that can be loaded non-sequentially and in parallel. Within coroutines: for controlled order of loading with `Deferred.await`.
`loadSync` | `Asset` | No | Synchronous. Blocks the current thread until an asset is loaded. Loading is performed on the rendering thread. | **Only** outside of coroutines, for crucial assets that need to be loaded synchronously (e.g. loading screen assets).

_`Asset` is the generic type of the loaded asset._

* Avoid mixing concurrent synchronous (`loadSync`) and asynchronous (`load`, `loadAsync`) loading of the same asset,
or assets with the same dependencies.
* Prefer asynchronous (`load`, `loadAsync`) asset loading methods whenever possible due to better performance and
compatibility with coroutines.
* Note that `loadSync` is _not necessary_ to load initial assets. You launch a coroutine with `KtxAsync.launch`,
load the assets sequentially with `load` or in parallel with `loadAsync`/`await`, and then switch to the first view
that uses the assets once they are loaded. Loading assets asynchronously might be faster, especially when done
in parallel - but ultimately it comes down to personal preference and code maintainability.

##### Avoid `runBlocking`

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
- For `load` and `getAsync.await` calls requesting already loaded assets. **Use with caution.**
- From within other threads than the main rendering thread and the `AssetStorage` loading threads. These threads
will be blocked until the operation is finished, which is not ideal, but at least the loading will remain possible.

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

Since usually applications have more than just 2 assets, many developers choose to treat `AssetManager` as
a container of assets: a map with file paths as keys and loaded assets are values. You typically load most
or all assets at the very beginning while showing the loading screen, and then just use `AssetManager.get(path)`
to obtain the assets after they are loaded.

However, this approach has some inconveniences and problems:

- The API is not very idiomatic to Kotlin, but in this particular case [ktx-assets](../assets) can help.
- `update` has to be called on rendering thread during loading.
- If you forget to stop updating the manager after the assets are loaded, the initiation code (such as `changeView`
in our example) can be ran multiple times. If you replace `TODO` with `println` in the example above, you will notice
that `changeView` is invoked on every `render` after the loading is finished.
- The majority of `AssetManager` methods are `synchronized`, which means they block the thread that they are
executed in and are usually more expensive to call than regular methods. This includes the `get` method, which does
not change the internal state of the manager at all. Even if the assets are fully loaded and you no longer modify
the `AssetManager` state, you still pay the cost of synchronization. This is especially relevant if you use multiple
threads, as they can slow each other down, both waiting for the lock.
- `AssetManager` stores assets mapped only by their paths. `manager.get<Texture>(path)` and `manager.get<Pixmap>(path)`
are both valid method calls that will throw a _runtime_ class cast exception.

`AssetStorage` avoids most of these problems.

Similarly to `AssetManager`, `AssetStorage` stores and allows you to `get` your loaded assets, so if you want to
migrate from `AssetManager` to `AssetStorage`, all you have to change initially is the loading code:

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
    TODO("Now the assets are loaded and can be accessed with $assetStorage.get!")
  }
}
```

As you can see, after the assets are loaded, the API of both `AssetManager` and `AssetStorage` is very similar.

While this example might seem almost identical to the `AssetManager` code, you already get the benefit of parallel
loading and using coroutines under the hood.

Now, this approach might just work in most cases, but there are still two things we should address:

- You will notice that your IDE warns you about not using the results of `loadAsync` which return `Deferred` instances.
This means we're launching asynchronous coroutines and ignore their results.
- `AssetStorage.progress` should be used only for display and debugging. You generally should not base your application
logic on `progress`, as it is only _eventually consistent_ with the `AssetStorage` state.

Let's rewrite it again - this time with proper coroutines:

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
      changeView()
    }
  }

  private fun changeView() {
    val texture: Texture = assetStorage["images/logo.png"]
    TODO("Now the assets are loaded and can be accessed with $assetStorage.get!")
  }
}
```

The IDE no longer warns us about ignoring `Deferred` results and we no longer have to check the progress all the time.
Instead, we suspend a coroutine to "wait" for the assets and run our initiation code once the assets are loaded.

The code using `AssetStorage` is not necessarily shorter in this case, but:

- You get the performance improvements of loading assets in parallel.
- `AssetStorage` does not have to be updated on render.
- Your code is reactive and `changeView` is guaranteed to be called only once as soon as the assets are loaded.
- `AssetStorage.get` is non-blocking and faster than `AssetManager.get`. `AssetStorage` does a better job of storing
your assets efficiently after the loading is finished.
- `AssetStorage` stores assets mapped by their path _and_ type. You will not have to deal with class cast exceptions.
- You can easily expand the launched coroutine or integrate more coroutines into your application later for other
asynchronous operations.

Besides, you get the additional benefits of other `AssetStorage` features and methods described in this file.

##### API equivalents

Closest equivalents in `AssetManager` and `AssetStorage` APIs:

`AssetManager` | `AssetStorage` | Note
:---: | :---: | ---
`get<T>(String)` | `get<T>(String)` |
`get(String, Class<T>)` | `get(Identifier<T>)` |
`get(AssetDescriptor<T>)` | `get(AssetDescriptor<T>)` |
`load(String, Class<T>)` | `loadAsync<T>(String)` | `load<T>(String)` can also be used as an alternative within coroutines.
`load(String, Class<T>, AssetLoaderParameters<T>)` | `loadAsync<T>(String, AssetLoaderParameters<T>)` | `load<T>(String, AssetLoaderParameters<T>)` can also be used as an alternative within coroutines.
`load(AssetDescriptor)` | `loadAsync(AssetDescriptor)` |  `load(AssetDescriptor)` can also be used as an alternative within coroutines.
`isLoaded(String)` | `isLoaded<T>(String)` | `AssetStorage` requires asset type, so the method is generic.
`isLoaded(String, Class)` | `isLoaded(Identifier)` |
`isLoaded(AssetDescriptor)` | `isLoaded(AssetDescriptor)` |
`unload(String)` | `unload<T>(String)`, `unload(Identifier)` | `AssetStorage` requires asset type, so the methods are generic.
`getProgress()` | `progress.percent` |
`isFinished()` | `progress.isFinished` |
`update()`, `update(Int)` | N/A | `AssetStorage` does not need to be updated. Rely on coroutines to execute code when the assets are loaded or use `progress.isFinished`.
`finishLoadingAsset(String)` | `loadSync<T>(String)` | Assets that need to be loaded immediately (e.g. loading screen assets) can be loaded with `loadSync` instead of asynchronous `load` or `loadAsync` for convenience.
`finishLoadingAsset(AssetDescriptor)` | `loadSync(AssetDescriptor)` |
`finishLoading()` | N/A | `AssetStorage` does not provide methods that block the thread until all assets are loaded. Rely on `progress.isFinished` instead. 
`addAsset(String, Class<T>, T)` | `add<T>(String, T)` |
`contains(String)` | `contains<T>(String)`, `contains(Identifier)` | `AssetStorage` requires asset type, so the methods are generic.
`setErrorHandler` | N/A, `try-catch` | With `AssetStorage` you can handle loading errors immediately with regular built-in `try-catch` syntax. Error listener is not required.
`clear()` | `dispose()` | `AssetStorage.dispose` will not kill `AssetStorage` threads and can be safely used multiple times like `AssetManager.clear`.
`dispose()` | `dispose()` | `AssetStorage` also provides a suspending variant with custom error handling.

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
Alternatives to the `AssetStorage` include:

- Using [`AssetManager`](https://github.com/libgdx/libgdx/wiki/Managing-your-assets) directly.
- Using [`ktx-assets`](../assets) extensions for `AssetManager`.
- [`AnnotationAssetManager`](https://bitbucket.org/dermetfan/libgdx-utils/wiki/net.dermetfan.gdx.assets.AnnotationAssetManager)
from [`libgdx-utils`](https://bitbucket.org/dermetfan/libgdx-utils) that extends `AssetManager` and allows
to specify assets for loading by marking fields with annotations.
- Loading assets without a manager.

#### Additional documentation

- [`ktx-async` module](../async), which is used extensively by this extension.
- [Official `AssetManager` article.](https://github.com/libgdx/libgdx/wiki/Managing-your-assets)
