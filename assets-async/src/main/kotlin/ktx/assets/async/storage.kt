package ktx.assets.async

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.*
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.async.AsyncExecutor
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ktx.assets.TextAssetLoader
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.async.onRenderingThread
import kotlin.coroutines.CoroutineContext
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader as ParticleEffect3dLoader

/**
 * Asynchronous asset loader based on coroutines API. An [AssetManager] alternative.
 *
 * Note that [KtxAsync.initiate] must be called on the rendering thread before creating an [AssetStorage].
 *
 * [asyncContext] is used to perform asynchronous file loading. Defaults to a single-threaded context using an
 * [AsyncExecutor]. See [newSingleThreadAsyncContext] or [ktx.async.newAsyncContext] functions to create a custom
 * loading context. Multi-threaded contexts are fully supported and might boost loading performance if the assets
 * are loaded asynchronously in parallel.
 *
 * [fileResolver] determines how file paths are interpreted. Defaults to [InternalFileHandleResolver], which loads
 * internal files.
 *
 * If `useDefaultLoaders` is true (which is the default), all default LibGDX [AssetLoader] implementations
 * will be registered.
 */
class AssetStorage(
  val asyncContext: CoroutineContext = newSingleThreadAsyncContext(threadName = "AssetStorage-Thread"),
  val fileResolver: FileHandleResolver = InternalFileHandleResolver(),
  useDefaultLoaders: Boolean = true
) : Disposable {
  @Suppress("LeakingThis")
  private val asAssetManager: AssetManager = AssetManagerWrapper(this)
  private val loaderStorage = AssetLoaderStorage()

  private val lock = Mutex()
  private val assets = mutableMapOf<Identifier<*>, Asset<*>>()

  /**
   * Allows to track progress of the loaded assets.
   *
   * The values stored by the [LoadingProgress] are _eventually consistent._
   * The progress can go slightly out of sync of the actual amounts of loaded assets,
   * as it is not protected by the [lock].
   *
   * Due to the asynchronous nature of [AssetStorage], some assets that will eventually
   * be scheduled by coroutines might not be counted by [LoadingProgress] yet.
   * Calling [load] and [loadAsync] is not guaranteed to immediately update the
   * [LoadingProgress.total] number of assets.
   *
   * Use the [progress] for display only and base your actual application logic on [AssetStorage] API.
   */
  val progress = LoadingProgress()

  /** LibGDX [Logger] used internally, usually to report issues. */
  var logger: Logger
    get() = asAssetManager.logger
    set(value) {
      asAssetManager.logger = value
    }

  init {
    if (useDefaultLoaders) {
      setLoader { TextAssetLoader(fileResolver) }
      setLoader { BitmapFontLoader(fileResolver) }
      setLoader { MusicLoader(fileResolver) }
      setLoader { PixmapLoader(fileResolver) }
      setLoader { SoundLoader(fileResolver) }
      setLoader { TextureAtlasLoader(fileResolver) }
      setLoader { TextureLoader(fileResolver) }
      setLoader { SkinLoader(fileResolver) }
      setLoader { ParticleEffectLoader(fileResolver) }
      setLoader { ParticleEffect3dLoader(fileResolver) }
      setLoader { I18NBundleLoader(fileResolver) }
      setLoader(suffix = ".g3dj") { G3dModelLoader(JsonReader(), fileResolver) }
      setLoader(suffix = ".g3db") { G3dModelLoader(UBJsonReader(), fileResolver) }
      setLoader(suffix = ".obj") { ObjLoader(fileResolver) }
      setLoader { ShaderProgramLoader(fileResolver) }
      setLoader { CubemapLoader(fileResolver) }
    }
  }

  /**
   * Creates a new [Identifier] that allows to uniquely describe an asset by [path] and class.
   * Uses reified [T] type to obtain the asset class.
   *
   * [T] is type of the loaded asset.
   * [path] to the file must be consistent with [fileResolver] asset type.
   */
  inline fun <reified T> getIdentifier(path: String): Identifier<T> = Identifier(path.normalizePath(), T::class.java)

  /**
   * Creates a new [AssetDescriptor] for the selected asset.
   *
   * [T] is type of the loaded asset.
   * [path] to the file should be consistent with [fileResolver] asset type.
   * Loading [parameters] are optional and passed to the associated [AssetLoader].
   * Returns a new instance of [AssetDescriptor] with a resolved [FileHandle].
   *
   * If the asset requires a [FileHandle] incompatible with the storage [fileResolver],
   * use the [fileHandle] parameter to set it.
   *
   * Top-level (static) alternatives can be found in `ktx-assets`. See [ktx.assets.assetDescriptor].
   */
  inline fun <reified T> getAssetDescriptor(
    path: String,
    parameters: AssetLoaderParameters<T>? = null,
    fileHandle: FileHandle? = null
  ): AssetDescriptor<T> {
    val descriptor = AssetDescriptor(path.normalizePath(), T::class.java, parameters)
    descriptor.file = fileHandle ?: fileResolver.resolve(path)
    return descriptor
  }

  /**
   * Returns a loaded asset of type [T] loaded from selected [path] or throws [MissingAssetException]
   * if the asset is not loaded yet or was never scheduled for loading. Rethrows any exceptions
   * encountered during asset loading.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [path] must match the asset path passed during loading.
   *
   * This method might throw the following exceptions:
   * - [MissingAssetException] if the asset of [T] type with the given [path] was never added with [load] or [add].
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * See also [getOrNull] and [getAsync].
   */
  inline operator fun <reified T> get(path: String): T = this[getIdentifier(path)]

  /**
   * Returns a loaded asset of type [T] described by [descriptor] or throws [MissingAssetException]
   * if the asset is not loaded yet or was never scheduled for loading. Rethrows any exceptions
   * encountered during asset loading.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [descriptor] contains the asset data. See [getAssetDescriptor].
   *
   * This method might throw the following exceptions:
   * - [MissingAssetException] if the asset of [T] type described by [descriptor] was never added with [load] or [add].
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * See also [getOrNull] and [getAsync].
   */
  operator fun <T> get(descriptor: AssetDescriptor<T>): T = this[descriptor.toIdentifier()]

  /**
   * Returns a loaded asset of type [T] identified by [identifier] or throws [MissingAssetException]
   * if the asset is not loaded yet or was never scheduled for loading. Rethrows any exceptions
   * encountered during asset loading.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [identifier] uniquely identifies a file by its path and type. See [Identifier].
   *
   * This method might throw the following exceptions:
   * - [MissingAssetException] if the asset of [T] type identified by [identifier] was never added with [load] or [add].
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * See also [getOrNull] and [getAsync].
   */
  operator fun <T> get(identifier: Identifier<T>): T {
    val reference = getAsync(identifier)
    return getOrThrow(identifier, reference)
  }

  private fun <T> getOrThrow(asset: Asset<T>): T = getOrThrow(asset.identifier, asset.reference)

  private fun <T> getOrThrow(identifier: Identifier<T>, reference: Deferred<T>): T =
    @Suppress("EXPERIMENTAL_API_USAGE") // Avoids runBlocking call.
    if (reference.isCompleted) reference.getCompleted() else throw MissingAssetException(identifier)

  /**
   * Returns a loaded asset of type [T] loaded from selected [path] or `null`
   * if the asset is not loaded yet or was never scheduled for loading.
   * Rethrows any exceptions encountered during asset loading.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [path] must match the asset path passed during loading.
   *
   * This method might throw the following exceptions:
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * See also [get] and [getAsync].
   */
  inline fun <reified T> getOrNull(path: String): T? = getOrNull(getIdentifier(path))

  /**
   * Returns a loaded asset of type [T] described by [descriptor] or `null`
   * if the asset is not loaded yet or was never scheduled for loading.
   * Rethrows any exceptions encountered during asset loading.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [descriptor] contains the asset data. See [getAssetDescriptor].
   *
   * This method might throw the following exceptions:
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * See also [get] and [getAsync].
   */
  fun <T> getOrNull(descriptor: AssetDescriptor<T>): T? = getOrNull(descriptor.toIdentifier())

  /**
   * Returns a loaded asset of type [T] identified by [identifier] or `null`
   * if the asset is not loaded yet or was never scheduled for loading.
   * Rethrows any exceptions encountered during asset loading.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [identifier] uniquely identifies a file by its path and type. See [Identifier].
   *
   * This method might throw the following exceptions:
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * See also [get] and [getAsync].
   */
  fun <T> getOrNull(identifier: Identifier<T>): T? {
    val asset = assets[identifier]
    @Suppress("UNCHECKED_CAST", "EXPERIMENTAL_API_USAGE") // Avoids runBlocking call.
    return if (asset == null || !asset.reference.isCompleted) null else asset.reference.getCompleted() as T
  }

  /**
   * Returns the reference to the asset wrapped with [Deferred].
   * Use [Deferred.await] to obtain the instance.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [path] must match the asset path passed during loading.
   *
   * Note that while the result is a [CompletableDeferred], it should never be completed manually.
   * Instead, rely on the [AssetStorage] to load the asset.
   *
   * Using [Deferred.await] might throw the following exceptions:
   * - [MissingAssetException] if the asset at [path] was never added with [load] or [add].
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * Otherwise, using [Deferred.await] will suspend the coroutine until the asset is loaded
   * and return its instance.
   *
   * See also [get] and [getOrNull] for synchronous alternatives.
   */
  inline fun <reified T> getAsync(path: String): Deferred<T> = getAsync(getIdentifier(path))

  /**
   * Returns the reference to the asset wrapped with [Deferred]. Use [Deferred.await] to obtain the instance.
   * Throws [AssetStorageException] if the asset was unloaded or never scheduled to begin with.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [descriptor] contains the asset data. See [getAssetDescriptor].
   *
   * Note that while the result is a [CompletableDeferred], it should never be completed manually.
   * Instead, rely on the [AssetStorage] to load the asset.
   *
   * Using [Deferred.await] might throw the following exceptions:
   * - [MissingAssetException] if the asset was never added with [load] or [add].
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * Otherwise, using [Deferred.await] will suspend the coroutine until the asset is loaded
   * and return its instance.
   *
   * See also [get] and [getOrNull] for synchronous alternatives.
   */
  fun <T> getAsync(descriptor: AssetDescriptor<T>): Deferred<T> = getAsync(descriptor.toIdentifier())

  /**
   * Returns the reference to the asset wrapped with [Deferred].
   * Use [Deferred.await] to obtain the instance.
   *
   * [T] is the type of the asset. Must match the type requested during loading.
   * [identifier] uniquely identifies a file by its path and type. See [Identifier].
   *
   * Note that while the result is a [CompletableDeferred], it should never be completed manually.
   * Instead, rely on the [AssetStorage] to load the asset.
   *
   * Using [Deferred.await] might throw the following exceptions:
   * - [MissingAssetException] if the asset with [identifier] was never added with [load] or [add].
   * - [UnloadedAssetException] if the asset was already unloaded asynchronously.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * Otherwise, using [Deferred.await] will suspend the coroutine until the asset is loaded
   * and return its instance.
   *
   * See also [get] and [getOrNull] for synchronous alternatives.
   */
  fun <T> getAsync(identifier: Identifier<T>): Deferred<T> {
    val asset = assets[identifier]
    @Suppress("UNCHECKED_CAST")
    return if (asset != null) asset.reference as Deferred<T> else getMissingAssetAsync(identifier)
  }

  private fun <T> getMissingAssetAsync(identifier: Identifier<T>): Deferred<T> = CompletableDeferred<T>().apply {
    completeExceptionally(MissingAssetException(identifier))
  }

  /**
   * Checks whether an asset in the selected [path] with [T] type is already loaded.
   * Returns false if the asset is not loaded yet, is unloaded or was never loaded to begin with.
   *
   * Note that assets that loaded exceptionally (i.e. asset loader threw an exception) will
   * also report as loaded, but trying to obtain their instance will cause rethrowing of that
   * exception, forcing the user to handle it.
   */
  inline fun <reified T> isLoaded(path: String): Boolean = isLoaded(getIdentifier<T>(path))

  /**
   * Checks whether an asset described with [descriptor] is already loaded.
   * Returns false if the asset is not loaded yet, is unloaded or was never loaded to begin with.
   *
   * Note that assets that loaded exceptionally (i.e. asset loader threw an exception) will
   * also report as loaded, but trying to obtain their instance will cause rethrowing of that
   * exception, forcing the user to handle it.
   */
  fun isLoaded(descriptor: AssetDescriptor<*>): Boolean = isLoaded(descriptor.toIdentifier())

  /**
   * Checks whether an asset identified with [identifier] is already loaded.
   * Returns false if the asset is not loaded yet, is unloaded or was never loaded to begin with.
   *
   * Note that assets that loaded exceptionally (i.e. asset loader threw an exception) will
   * also report as loaded, but trying to obtain their instance will cause rethrowing of that
   * exception, forcing the user to handle it.
   */
  fun isLoaded(identifier: Identifier<*>): Boolean =
    assets[identifier]?.reference?.isCompleted ?: false

  /**
   * Checks whether an asset in the selected [path] and [T] type is currently managed by the storage.
   * This will return true for assets that are currently being loaded or
   */
  inline operator fun <reified T> contains(path: String): Boolean = contains(getIdentifier<T>(path))

  /**
   * Checks whether an asset described by [descriptor] is currently managed by the storage.
   * This will return true for assets that are currently being loaded or
   */
  operator fun contains(descriptor: AssetDescriptor<*>): Boolean = contains(descriptor.toIdentifier())

  /**
   * Checks whether an asset identified by [identifier] is currently managed by the storage.
   * This will return true for assets that are currently being loaded or
   */
  operator fun contains(identifier: Identifier<*>): Boolean = identifier in assets

  /**
   * Adds a fully loaded [asset] to the storage. Allows to avoid loading the asset with the [AssetStorage]
   * and to manually add it to storage context.
   *
   * [T] is the type of the [asset]. Note that a superclass of the asset can be chosen to associate the file with.
   * [path] must be a unique ID that will be used to retrieve the asset. Since the [asset] is loaded manually,
   * it does not have to be an actual file path.
   *
   * Throws [AlreadyLoadedAssetException] if an asset with the same path is already loaded or scheduled for loading.
   */
  suspend inline fun <reified T> add(path: String, asset: T) =
    add(getAssetDescriptor(path), asset)

  /**
   * Adds a fully loaded [asset] to the storage. Allows to avoid loading the asset with the [AssetStorage]
   * and to manually add it to storage context.
   *
   * [T] is the type of the [asset]. Note that a superclass of the asset can be chosen to associate the file with.
   * [identifier] uniquely identifies the assets and defines its type. Since the [asset] is loaded manually,
   * [Identifier.path] does not have to be an actual file path. See [getIdentifier].
   *
   * Throws [AlreadyLoadedAssetException] if an asset with the same path is already loaded or scheduled for loading.
   */
  suspend fun <T> add(identifier: Identifier<T>, asset: T) =
    add(identifier.toAssetDescriptor(), asset)

  /**
   * Adds a fully loaded [asset] to the storage. Allows to avoid loading the asset with the [AssetStorage]
   * and to manually add it to storage context.
   *
   * [T] is the type of the [asset]. Note that a superclass of the asset can be chosen to associate the file with.
   * [descriptor] contains the asset data. See [getAssetDescriptor].
   *
   * Throws [AlreadyLoadedAssetException] if an asset with the same path is already loaded or scheduled for loading.
   */
  suspend fun <T> add(descriptor: AssetDescriptor<T>, asset: T) {
    val identifier = descriptor.toIdentifier()
    lock.withLock {
      @Suppress("UNCHECKED_CAST")
      val existingAsset = assets[identifier] as? Asset<T>
      if (existingAsset != null) {
        // Asset is already stored. Will fail to replace.
        throw AlreadyLoadedAssetException(identifier)
      }
      // Asset is currently not stored. Creating.
      @Suppress("UNCHECKED_CAST")
      assets[identifier] = Asset(
        descriptor = descriptor,
        reference = CompletableDeferred(asset),
        dependencies = emptyList(),
        referenceCount = 1,
        loader = ManualLoader as Loader<T>
      )
      progress.registerAddedAsset()
    }
  }

  /**
   * Schedules asynchronous loading of an asset of [T] type located at [path].
   * Return a [Deferred] reference which will eventually point to a fully loaded instance of [T].
   *
   * [path] must be compatible with the [fileResolver].
   * Loading [parameters] are optional and can be used to configure the loaded asset.
   *
   * [Deferred.await] might throw the following exceptions:
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will not fail or throw
   * an exception (unless the original loading fails). Instead, the coroutine will be suspended until
   * the original loading is finished and then return the same result.
   *
   * Note that to unload an asset, [unload] method should be called the same amount of times as [load]
   * or [loadAsync]. Asset dependencies should not be unloaded directly; instead, unload the asset that
   * required them and caused them to load in the first place.
   *
   * If the [parameters] define a [AssetLoaderParameters.loadedCallback], it will be invoked on the main
   * rendering thread after the asset is loaded successfully with this [AssetStorage] wrapped as an
   * [AssetManager] with [AssetManagerWrapper]. Note that the wrapper supports a limited number of methods.
   * It is encouraged not to rely on [AssetLoaderParameters.LoadedCallback] and use coroutines instead.
   * Exceptions thrown by callbacks will not be propagated, and will be logged with [logger] instead.
   */
  inline fun <reified T> loadAsync(path: String, parameters: AssetLoaderParameters<T>? = null): Deferred<T> =
    loadAsync(getAssetDescriptor(path, parameters))

  /**
   * Schedules loading of an asset with path and type specified by [identifier].
   * Suspends the coroutine until an asset is loaded and returns a fully loaded instance of [T].
   *
   * [Identifier.path] must be compatible with the [fileResolver].
   * Loading [parameters] are optional and can be used to configure the loaded asset.
   *
   * [Deferred.await] might throw the following exceptions:
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will not fail or throw
   * an exception (unless the original loading fails). Instead, the coroutine will be suspended until
   * the original loading is finished and then return the same result.
   *
   * Note that to unload an asset, [unload] method should be called the same amount of times as [load]
   * or [loadAsync]. Asset dependencies should not be unloaded directly; instead, unload the asset that
   * required them and caused them to load in the first place.
   *
   * If the [parameters] define a [AssetLoaderParameters.loadedCallback], it will be invoked on the main
   * rendering thread after the asset is loaded successfully with this [AssetStorage] wrapped as an
   * [AssetManager] with [AssetManagerWrapper]. Note that the wrapper supports a limited number of methods.
   * It is encouraged not to rely on [AssetLoaderParameters.LoadedCallback] and use coroutines instead.
   * Exceptions thrown by callbacks will not be propagated, and will be logged with [logger] instead.
   */
  fun <T> loadAsync(identifier: Identifier<T>, parameters: AssetLoaderParameters<T>? = null): Deferred<T> =
    loadAsync(identifier.toAssetDescriptor(parameters))

  /**
   * Schedules loading of an asset of [T] type described by the [descriptor].
   * Suspends the coroutine until an asset is loaded and returns a fully loaded instance of [T].
   *
   * [Deferred.await] might throw the following exceptions:
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will not fail or throw
   * an exception (unless the original loading fails). Instead, the coroutine will be suspended until
   * the original loading is finished and then return the same result.
   *
   * Note that to unload an asset, [unload] method should be called the same amount of times as [load]
   * or [loadAsync]. Asset dependencies should not be unloaded directly; instead, unload the asset that
   * required them and caused them to load in the first place.
   *
   * If the [AssetDescriptor.params] define a [AssetLoaderParameters.loadedCallback], it will be invoked on
   * the main rendering thread after the asset is loaded successfully with this [AssetStorage] wrapped as an
   * [AssetManager] with [AssetManagerWrapper]. Note that the wrapper supports a limited number of methods.
   * It is encouraged not to rely on [AssetLoaderParameters.LoadedCallback] and use coroutines instead.
   * Exceptions thrown by callbacks will not be propagated, and will be logged with [logger] instead.
   */
  fun <T> loadAsync(descriptor: AssetDescriptor<T>): Deferred<T> = KtxAsync.async(asyncContext) {
    load(descriptor)
  }

  /**
   * Schedules loading of an asset of [T] type located at [path].
   * Suspends the coroutine until an asset is loaded and returns a fully loaded instance of [T].
   *
   * [path] must be compatible with the [fileResolver].
   * Loading [parameters] are optional and can be used to configure the loaded asset.
   *
   * Might throw the following exceptions:
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will not fail or throw
   * an exception (unless the original loading fails). Instead, the coroutine will be suspended until
   * the original loading is finished and then return the same result.
   *
   * Note that to unload an asset, [unload] method should be called the same amount of times as [load].
   * Asset dependencies should not be unloaded directly; instead, unload the asset that required them
   * and caused them to load in the first place.
   *
   * If the [parameters] define a [AssetLoaderParameters.loadedCallback], it will be invoked on the main
   * rendering thread after the asset is loaded successfully with this [AssetStorage] wrapped as an
   * [AssetManager] with [AssetManagerWrapper]. Note that the wrapper supports a limited number of methods.
   * It is encouraged not to rely on [AssetLoaderParameters.LoadedCallback] and use coroutines instead.
   * Exceptions thrown by callbacks will not be propagated, and will be logged with [logger] instead.
   */
  suspend inline fun <reified T> load(path: String, parameters: AssetLoaderParameters<T>? = null): T =
    load(getAssetDescriptor(path, parameters))

  /**
   * Schedules loading of an asset with path and type specified by [identifier].
   * Suspends the coroutine until an asset is loaded and returns a fully loaded instance of [T].
   *
   * [Identifier.path] must be compatible with the [fileResolver].
   * Loading [parameters] are optional and can be used to configure the loaded asset.
   *
   * Might throw the following exceptions:
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will not fail or throw
   * an exception (unless the original loading fails). Instead, the coroutine will be suspended until
   * the original loading is finished and then return the same result.
   *
   * Note that to unload an asset, [unload] method should be called the same amount of times as [load].
   * Asset dependencies should not be unloaded directly; instead, unload the asset that required them
   * and caused them to load in the first place.
   *
   * If the [parameters] define a [AssetLoaderParameters.loadedCallback], it will be invoked on the main
   * rendering thread after the asset is loaded successfully with this [AssetStorage] wrapped as an
   * [AssetManager] with [AssetManagerWrapper]. Note that the wrapper supports a limited number of methods.
   * It is encouraged not to rely on [AssetLoaderParameters.LoadedCallback] and use coroutines instead.
   * Exceptions thrown by callbacks will not be propagated, and will be logged with [logger] instead.
   */
  suspend fun <T> load(identifier: Identifier<T>, parameters: AssetLoaderParameters<T>? = null): T =
    load(identifier.toAssetDescriptor(parameters))

  /**
   * Schedules loading of an asset of [T] type described by the [descriptor].
   * Suspends the coroutine until an asset is loaded and returns a fully loaded instance of [T].
   *
   * Might throw the following exceptions:
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will not fail or throw
   * an exception (unless the original loading fails). Instead, the coroutine will be suspended until
   * the original loading is finished and then return the same result.
   *
   * Note that to unload an asset, [unload] method should be called the same amount of times as [load].
   * Asset dependencies should not be unloaded directly; instead, unload the asset that required them
   * and caused them to load in the first place.
   *
   * If the [AssetDescriptor.params] define a [AssetLoaderParameters.loadedCallback], it will be invoked on
   * the main rendering thread after the asset is loaded successfully with this [AssetStorage] wrapped as an
   * [AssetManager] with [AssetManagerWrapper]. Note that the wrapper supports a limited number of methods.
   * It is encouraged not to rely on [AssetLoaderParameters.LoadedCallback] and use coroutines instead.
   * Exceptions thrown by callbacks will not be propagated, and will be logged with [logger] instead.
   */
  suspend fun <T> load(descriptor: AssetDescriptor<T>): T {
    lateinit var newAssets: List<Asset<*>>
    lateinit var asset: Asset<T>
    lock.withLock {
      asset = obtainAsset(descriptor)
      newAssets = updateReferences(asset)
    }
    newAssets.forEach { assetToLoad ->
      // Loading new assets asynchronously:
      progress.registerScheduledAsset()
      KtxAsync.launch(asyncContext) {
        withAssetLoadingErrorHandling(assetToLoad) {
          loadAsset(assetToLoad)
        }
      }
    }
    // Awaiting for our asset to load:
    return asset.reference.await()
  }

  /** Must be called with [lock]. */
  private suspend fun <T> obtainAsset(descriptor: AssetDescriptor<T>): Asset<T> {
    val identifier = descriptor.toIdentifier()
    val asset = assets[identifier]
    if (asset != null) {
      // Asset already exists and identifier ensures same type - returning:
      @Suppress("UNCHECKED_CAST")
      return asset as Asset<T>
    }
    return createNewAsset(descriptor).also {
      assets[identifier] = it
    }
  }

  private suspend fun <T> createNewAsset(descriptor: AssetDescriptor<T>): Asset<T> =
    withContext(asyncContext) {
      resolveFileHandle(descriptor)
      val loader = getLoader(descriptor.type, descriptor.fileName) ?: throw MissingLoaderException(descriptor)
      val dependencies = loader.getDependencies(descriptor)
      Asset(
        descriptor = descriptor,
        dependencies = dependencies.map { obtainAsset(it) },
        loader = loader,
        referenceCount = 0
      )
    }

  private fun resolveFileHandle(descriptor: AssetDescriptor<*>) {
    if (descriptor.file == null) {
      descriptor.file = fileResolver.resolve(descriptor.fileName)
    }
  }

  /**
   * Must be executed with [lock].
   * Updates reference counts of entire dependency tree starting with [root].
   * Returns a list of new assets that have to be loaded.
   */
  private fun updateReferences(root: Asset<*>): List<Asset<*>> {
    val queue = Queue<Asset<*>>()
    queue.addLast(root)
    val newAssets = mutableListOf<Asset<*>>()
    while (!queue.isEmpty) {
      val asset = queue.removeFirst()
      asset.referenceCount++
      if (asset.referenceCount == 1) {
        newAssets.add(asset)
      }
      asset.dependencies.forEach(queue::addLast)
    }
    return newAssets
  }

  private suspend fun <T> loadAsset(
    asset: Asset<T>
  ): T {
    asset.dependencies.forEach { dependency ->
      withAssetLoadingErrorHandling(asset) {
        dependency.reference.await()
      }
    }
    if (asset.reference.isCompleted) {
      // The asset failed to load due to its dependencies or asynchronous unloading:
      return asset.reference.await()
    }
    withAssetLoadingErrorHandling(asset) {
      when (val loader = asset.loader) {
        is SynchronousLoader<T> -> loadWithSynchronousLoader(loader, asset)
        is AsynchronousLoader<T> -> loadWithAsynchronousLoader(loader, asset)
        else -> throw InvalidLoaderException(loader)
      }
    }
    return asset.reference.await()
  }

  private inline fun withAssetLoadingErrorHandling(asset: Asset<*>, operation: () -> Unit) {
    try {
      operation()
    } catch (exception: AssetStorageException) {
      setLoadedExceptionally(asset, exception)
    } catch (exception: Throwable) {
      setLoadedExceptionally(asset, AssetLoadingException(asset.descriptor, cause = exception))
    }
  }

  private fun setLoadedExceptionally(asset: Asset<*>, exception: AssetStorageException) {
    if (asset.reference.completeExceptionally(exception)) {
      // This the passed exception managed to complete the loading, we record a failed asset loading:
      progress.registerFailedAsset()
    }
  }

  private suspend fun <T> loadWithSynchronousLoader(
    synchronousLoader: SynchronousLoader<T>,
    asset: Asset<T>
  ) {
    // If any of the isCompleted checks returns true, asset is likely to be unloaded asynchronously.
    if (asset.reference.isCompleted) {
      return
    }
    onRenderingThread {
      if (!asset.reference.isCompleted) {
        val value = synchronousLoader.load(asAssetManager, asset.descriptor)
        setLoaded(asset, value)
      }
    }
  }

  private suspend fun <T> loadWithAsynchronousLoader(
    asynchronousLoader: AsynchronousLoader<T>,
    asset: Asset<T>
  ) {
    // If any of the isCompleted checks returns true, asset is likely to be unloaded asynchronously.
    withContext(asyncContext) {
      if (!asset.reference.isCompleted) {
        asynchronousLoader.loadAsync(asAssetManager, asset.descriptor)
      }
    }
    if (asset.reference.isCompleted) {
      return
    }
    onRenderingThread {
      if (!asset.reference.isCompleted) {
        val value = asynchronousLoader.loadSync(asAssetManager, asset.descriptor)
        setLoaded(asset, value)
      }
    }
  }

  private fun <T> setLoaded(asset: Asset<T>, value: T) {
    if (asset.reference.complete(value)) {
      // The asset was correctly loaded and assigned.
      progress.registerLoadedAsset()
      try {
        // Notifying the LibGDX loading callback to support AssetManager behavior:
        asset.descriptor.params?.loadedCallback?.finishedLoading(
          asAssetManager, asset.identifier.path, asset.identifier.type
        )
      } catch (exception: Throwable) {
        // We are unable to propagate the exception at this point, so we just log it:
        logger.error(
          "Exception occurred during execution of loaded callback of asset: ${asset.identifier}",
          exception
        )
      }
    } else {
      // The asset was unloaded asynchronously. The deferred was likely completed with an exception.
      // Now we have to take care of the loaded value or it will remain loaded and unreferenced.
      value.dispose(asset.identifier)
    }
  }

  /**
   * Blocks the current thread until the asset with [T] type is loaded from the given [path].
   *
   * This method is safe to call from the main rendering thread, as well as other application threads.
   * However, avoid loading the same asset or assets with the same dependencies with both synchronous
   * [loadSync] and asynchronous [load] or [loadAsync], and avoid running this method from within
   * coroutines.
   *
   * This method should be used only to load crucial assets that are needed to initiate the application,
   * e.g. assets required to display the loading screen. Whenever possible, prefer [load] and [loadAsync].
   *
   * Might throw the following exceptions:
   * - [MissingAssetException] when attempting to load an asset that was already scheduled for asynchronous loading.
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will either return the asset
   * immediately if it is loaded, or throw [MissingAssetException] if it is unloaded. In either case, it will
   * increase the reference count of the asset - see [getReferenceCount] and [unload] for details.
   */
  inline fun <reified T> loadSync(path: String): T = loadSync(getAssetDescriptor(path))

  /**
   * Blocks the current thread until the asset with [T] type is loaded with data specified by the [identifier]
   * and optional loading [parameters].
   *
   * This method is safe to call from the main rendering thread, as well as other application threads.
   * However, avoid loading the same asset or assets with the same dependencies with both synchronous
   * [loadSync] and asynchronous [load] or [loadAsync], and avoid running this method from within
   * coroutines.
   *
   * This method should be used only to load crucial assets that are needed to initiate the application,
   * e.g. assets required to display the loading screen. Whenever possible, prefer [load] and [loadAsync].
   *
   * Might throw the following exceptions:
   * - [MissingAssetException] when attempting to load an asset that was already scheduled for asynchronous loading.
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will either return the asset
   * immediately if it is loaded, or throw [MissingAssetException] if it is unloaded. In either case, it will
   * increase the reference count of the asset - see [getReferenceCount] and [unload] for details.
   */
  fun <T> loadSync(identifier: Identifier<T>, parameters: AssetLoaderParameters<T>? = null) =
    loadSync(identifier.toAssetDescriptor(parameters))

  /**
   * Blocks the current thread until the asset with [T] type is loaded using the asset [descriptor].
   *
   * This method is safe to call from the main rendering thread, as well as other application threads.
   * However, avoid loading the same asset or assets with the same dependencies with both synchronous
   * [loadSync] and asynchronous [load] or [loadAsync], and avoid running this method from within
   * coroutines.
   *
   * This method should be used only to load crucial assets that are needed to initiate the application,
   * e.g. assets required to display the loading screen. Whenever possible, prefer [load] and [loadAsync].
   *
   * Might throw the following exceptions:
   * - [MissingAssetException] when attempting to load an asset that was already scheduled for asynchronous loading.
   * - [UnloadedAssetException] if the asset unloaded asynchronously by another coroutine.
   * - [MissingLoaderException] if the [AssetLoader] for asset of requested type is not registered.
   * - [InvalidLoaderException] if the [AssetLoader] implementation of requested type is invalid.
   * - [AssetLoadingException] if the [AssetLoader] has thrown an exception during loading.
   * - [MissingDependencyException] is the [AssetLoader] is unable to obtain an instance of asset's dependency.
   * - [UnsupportedMethodException] is the [AssetLoader] uses unsupported operation on [AssetManagerWrapper].
   *
   * If the asset was already loaded, added or scheduled for loading, this method will either return the asset
   * immediately if it is loaded, or throw [MissingAssetException] if it is unloaded. In either case, it will
   * increase the reference count of the asset - see [getReferenceCount] and [unload] for details.
   */
  fun <T> loadSync(descriptor: AssetDescriptor<T>): T = runBlocking {
    lateinit var asset: Asset<T>
    val newAssets = lock.withLock {
      asset = obtainAsset(descriptor)
      updateReferences(asset)
    }
    loadSync(newAssets)
    getOrThrow(asset)
  }

  private suspend fun loadSync(assets: List<Asset<*>>) {
    val queue = Queue<Asset<*>>(assets.size)
    assets.forEach {
      progress.registerScheduledAsset()
      // Adding assets in reversed order - dependencies should be first:
      queue.addFirst(it)
    }
    verifyDependenciesForSynchronousLoading(assets)
    while (!queue.isEmpty) {
      val asset = queue.removeFirst()
      // "Awaiting" for dependencies to be loaded without suspending:
      if (asset.dependencies.any { !it.reference.isCompleted }) {
        queue.addLast(asset)
        continue
      }
      onRenderingThread {
        withAssetLoadingErrorHandling(asset) {
          loadAssetSync(asset)
        }
      }
    }
  }

  private fun verifyDependenciesForSynchronousLoading(assets: List<Asset<*>>) {
    val identifiers = assets.map { it.identifier }.toSet()
    val exceptions = mutableListOf<AssetStorageException>()
    assets.forEach { asset ->
      // Gathering all dependencies that are not loaded and were scheduled for asynchronous loading:
      asset.dependencies.filter { !it.reference.isCompleted && it.identifier !in identifiers }
        // Preparing an exception if such a dependency occurs:
        .map { MissingDependencyException(it.identifier) }
        // Setting parent asset as exceptionally loaded:
        .forEach { exception -> setLoadedExceptionally(asset, exception); exceptions.add(exception) }
    }
    // Throwing one of the exceptions if any occurs:
    exceptions.firstOrNull()?.let { throw it }
  }

  private fun <T> loadAssetSync(asset: Asset<T>) =
    when (val loader = asset.loader) {
      is SynchronousLoader<T> -> setLoaded(asset, loader.load(asAssetManager, asset.descriptor))
      is AsynchronousLoader<T> -> {
        loader.loadAsync(asAssetManager, asset.descriptor)
        setLoaded(asset, loader.loadSync(asAssetManager, asset.descriptor))
      }
      else -> throw InvalidLoaderException(loader)
    }

  /**
   * Removes asset loaded with the given [path] and [T] type and all of its dependencies.
   * Does nothing if asset was not loaded in the first place.
   * Will not dispose of the asset if it still is referenced by any other assets.
   * Any removed assets that implement [Disposable] will be disposed.
   *
   * Note: only assets that were explicitly scheduled for loading with [load], [loadAsync] or
   * [loadSync], or manually added to storage with [add] should be unloaded.
   * Dependencies of assets will be removed automatically along with the original assets
   * that caused them to load in the first place.
   *
   * Assets scheduled for loading multiple times must be explicitly unloaded multiple times -
   * until the asset is unloaded as many times as it was referenced, it is assumed that it is
   * still used. Manually unloading dependencies of other assets (that were not scheduled
   * for loading explicitly) might lead to unexpected runtime exceptions.
   *
   * Will log all exceptions related to unloading of the assets. Silence the [logger]
   * to avoid exception logging.
   *
   * Returns `true` if the asset was present in the [AssetStorage]. Note that if the asset
   * is still referenced (i.e. [load] was called multiple times or the asset is a dependency
   * of an asset that is still loaded), the asset will not be disposed of and will remain
   * in the storage even if `true` is returned.
   */
  suspend inline fun <reified T> unload(path: String): Boolean = unload(getIdentifier<T>(path))

  /**
   * Removes asset described by the [descriptor] and all of its dependencies.
   * Does nothing if asset was not loaded in the first place.
   * Will not dispose of the asset if it still is referenced by any other assets.
   * Any removed assets that implement [Disposable] will be disposed.
   *
   * Note: only assets that were explicitly scheduled for loading with [load], [loadAsync] or
   * [loadSync], or manually added to storage with [add] should be unloaded.
   * Dependencies of assets will be removed automatically along with the original assets
   * that caused them to load in the first place.
   *
   * Assets scheduled for loading multiple times must be explicitly unloaded multiple times -
   * until the asset is unloaded as many times as it was referenced, it is assumed that it is
   * still used. Manually unloading dependencies of other assets (that were not scheduled
   * for loading explicitly) might lead to unexpected runtime exceptions.
   *
   * Will log all exceptions related to unloading of the assets. Silence the [logger]
   * to avoid exception logging.
   *
   * Returns `true` if the asset was present in the [AssetStorage]. Note that if the asset
   * is still referenced (i.e. [load] was called multiple times or the asset is a dependency
   * of an asset that is still loaded), the asset will not be disposed of and will remain
   * in the storage even if `true` is returned.
   */
  suspend fun unload(descriptor: AssetDescriptor<*>): Boolean = unload(descriptor.toIdentifier())

  /**
   * Removes asset loaded with the given [identifier] and all of its dependencies.
   * Does nothing if asset was not loaded in the first place.
   * Will not dispose of the asset if it still is referenced by any other assets.
   * Any removed assets that implement [Disposable] will be disposed.
   *
   * Note: only assets that were explicitly scheduled for loading with [load], [loadAsync] or
   * [loadSync], or manually added to storage with [add] should be unloaded.
   * Dependencies of assets will be removed automatically along with the original assets
   * that caused them to load in the first place.
   *
   * Assets scheduled for loading multiple times must be explicitly unloaded multiple times -
   * until the asset is unloaded as many times as it was referenced, it is assumed that it is
   * still used. Manually unloading dependencies of other assets (that were not scheduled
   * for loading explicitly) might lead to unexpected runtime exceptions.
   *
   * Will log all exceptions related to unloading of the assets. Silence the [logger]
   * to avoid exception logging.
   *
   * Returns `true` if the asset was present in the [AssetStorage]. Note that if the asset
   * is still referenced (i.e. [load] was called multiple times or the asset is a dependency
   * of an asset that is still loaded), the asset will not be disposed of and will remain
   * in the storage even if `true` is returned.
   */
  suspend fun unload(identifier: Identifier<*>): Boolean = lock.withLock {
    val root = assets[identifier]
    if (root == null) {
      // Asset is absent in the storage. Returning false - unsuccessful unload:
      false
    } else {
      val queue = Queue<Asset<*>>()
      queue.addLast(root)
      while (!queue.isEmpty) {
        val asset = queue.removeFirst()
        asset.referenceCount--
        if (asset.referenceCount == 0) {
          // The asset is no longer referenced by the user or any dependencies. Removing and disposing.
          assets.remove(asset.identifier)
          disposeOf(asset)
        }
        asset.dependencies.forEach(queue::addLast)
      }
      // Asset was present in the storage. Returning true - successful unload:
      true
    }
  }

  @Suppress("EXPERIMENTAL_API_USAGE") // Allows to dispose of assets without suspending calls.
  private fun disposeOf(asset: Asset<*>) {
    if (!asset.reference.isCompleted) {
      val exception = UnloadedAssetException(asset.identifier)
      // If the asset is not loaded yet, we complete the reference with exception:
      val cancelled = asset.reference.completeExceptionally(exception)
      if (cancelled) {
        progress.removeScheduledAsset()
        // We managed to complete the reference exceptionally. The loading coroutine will dispose of the asset.
        return
      }
    }
    val exception = asset.reference.getCompletionExceptionOrNull()
    if (exception != null) {
      // The asset was not loaded successfully. Nothing to dispose of.
      progress.removeFailedAsset()
    } else {
      progress.removeLoadedAsset()
      asset.reference.getCompleted().dispose(asset.identifier)
    }
  }

  /**
   * Performs cast to [Disposable] if possible and disposes of the object with [Disposable.dispose].
   * Logs any disposing errors.
   */
  private fun Any?.dispose(identifier: Identifier<*>) {
    try {
      (this as? Disposable)?.dispose()
    } catch (exception: Throwable) {
      logger.error("Failed to dispose of asset: $identifier", exception)
    }
  }

  /**
   * Returns the [AssetLoader] associated with the file. [Asset] is used to determine the type
   * of the loaded file. [path] might be necessary to choose the correct loader, as some loaders
   * might be assigned to specific file  suffixes or extensions.
   */
  inline fun <reified Asset> getLoader(path: String? = null): Loader<Asset>? =
    getLoader(Asset::class.java, path)

  /**
   * Internal API exposed for inlined method. See inlined [getLoader] with generics.
   * [type] is the class of the loaded asset, while path is used to determine if
   * a loader specifically assigned to a file suffix or extension is necessary.
   */
  fun <Asset> getLoader(type: Class<Asset>, path: String?): Loader<Asset>? =
    loaderStorage.getLoader(type, path?.normalizePath())

  /**
   * Associates the [AssetLoader] with specific asset type determined by [T].
   * [loaderProvider] should create a new instance of loader of the selected types.
   * Optional [suffix] can be passed if the loader should handle only the files
   * with a specific file name suffix or extension.
   *
   * Throws [InvalidLoaderException] if the [AssetLoader] does not extend
   * [SynchronousAssetLoader] or [AsynchronousAssetLoader].
   */
  inline fun <reified T> setLoader(suffix: String? = null, noinline loaderProvider: () -> Loader<T>) =
    setLoader(T::class.java, suffix, loaderProvider)

  /**
   * Internal API exposed for inlined method. See inlined [setLoader] with reified generics.
   * Associates the [AssetLoader] [loaderProvider] with [type] under the specified optional [suffix].
   *
   * Throws [InvalidLoaderException] if the [AssetLoader] does not extend
   * [SynchronousAssetLoader] or [AsynchronousAssetLoader].
   */
  fun <T> setLoader(type: Class<T>, suffix: String? = null, loaderProvider: () -> Loader<T>) {
    loaderStorage.setLoaderProvider(type, suffix, loaderProvider)
  }

  /**
   * Matches [AssetDescriptor] pre-processing. Return this [String] with normalized file separators.
   */
  fun String.normalizePath() = replace('\\', '/')

  /**
   * Returns the amount of references to the asset under the given [path] of [T] type.
   *
   * References include manual registration of the asset with [add],
   * scheduling the asset for loading with [load], [loadAsync] or [loadSync],
   * and the amount of times the asset was referenced as a dependency of other assets.
   */
  inline fun <reified T> getReferenceCount(path: String): Int = getReferenceCount(getIdentifier<T>(path))

  /**
   * Returns the amount of references to the asset described by [descriptor].
   *
   * References include manual registration of the asset with [add],
   * scheduling the asset for loading with [load], [loadAsync] or [loadSync],
   * and the amount of times the asset was referenced as a dependency of other assets.
   */
  fun getReferenceCount(descriptor: AssetDescriptor<*>): Int = getReferenceCount(descriptor.toIdentifier())

  /**
   * Returns the amount of references to the asset identified by [identifier].
   *
   * References include manual registration of the asset with [add],
   * scheduling the asset for loading with [load], [loadAsync] or [loadSync],
   * and the amount of times the asset was referenced as a dependency of other assets.
   */
  fun getReferenceCount(identifier: Identifier<*>): Int = assets[identifier]?.referenceCount ?: 0

  /**
   * Returns a copy of the list of dependencies of the asset under [path] with [T] type.
   * If the asset is not loaded or has no dependencies, an empty list is returned.
   */
  inline fun <reified T> getDependencies(path: String): List<Identifier<*>> =
    getDependencies(getIdentifier<T>(path))

  /**
   * Returns a copy of the list of dependencies of the asset described by [descriptor].
   * If the asset is not loaded or has no dependencies, an empty list is returned.
   */
  fun getDependencies(descriptor: AssetDescriptor<*>): List<Identifier<*>> =
    getDependencies(descriptor.toIdentifier())

  /**
   * Returns a copy of the list of dependencies of the asset identified by [identifier].
   * If the asset is not loaded or has no dependencies, an empty list is returned.
   */
  fun getDependencies(identifier: Identifier<*>): List<Identifier<*>> {
    val dependencies = assets[identifier]?.dependencies
    return dependencies?.map { it.identifier } ?: emptyList()
  }

  /**
   * Unloads all assets. Blocks current thread until are assets are unloaded.
   * Logs all disposing exceptions.
   *
   * Prefer suspending [dispose] method that takes an error handler as parameter.
   *
   * Calling [dispose] is not guaranteed to keep the eventual consistency of [progress]
   * if [dispose] is called during asynchronous asset loading.
   * If exact loading progress is crucial, prefer creating another instance of [AssetStorage]
   * than reusing existing one that has been disposed.
   */
  override fun dispose() {
    runBlocking {
      dispose { identifier, cause ->
        logger.error("Unable to dispose of $identifier.", cause)
      }
    }
  }

  /**
   * Unloads all assets. Cancels loading of all scheduled assets.
   * [onError] will be invoked on every caught disposing exception.
   *
   * Calling [dispose] is not guaranteed to keep the eventual consistency of [progress]
   * if [dispose] is called during asynchronous asset loading.
   * If exact loading progress is crucial, prefer creating another instance of [AssetStorage]
   * than reusing existing one that has been disposed.
   */
  suspend fun dispose(onError: (identifier: Identifier<*>, cause: Throwable) -> Unit) {
    lock.withLock {
      for (asset in assets.values) {
        if (!asset.reference.isCompleted) {
          val exception = UnloadedAssetException(asset.identifier)
          if (asset.reference.completeExceptionally(exception)) {
            // We managed to complete the deferred exceptionally,
            // so the loading coroutine will take care of the rest.
            continue
          }
        }
        try {
          (asset.reference.await() as? Disposable)?.dispose()
        } catch (exception: Throwable) {
          onError(asset.identifier, exception)
        }
        asset.referenceCount = 0
      }
      assets.clear()
      progress.reset()
    }
  }

  override fun toString(): String = "AssetStorage(assets=${
  assets.keys.sortedBy { it.path }.joinToString(separator = ", ", prefix = "[", postfix = "]")
  })"
}

/**
 * Container for a single asset of type [T] managed by [AssetStorage].
 */
internal data class Asset<T>(
  /** Stores asset loading data. */
  val descriptor: AssetDescriptor<T>,
  /** Unique identifier of the asset. */
  val identifier: Identifier<T> = descriptor.toIdentifier(),
  /** Stores reference to the actual asset once it is loaded. */
  val reference: CompletableDeferred<T> = CompletableDeferred(),
  /** Lists asset dependencies that require loading. */
  val dependencies: List<Asset<*>>,
  /** Used to load the asset. */
  val loader: Loader<T>,
  /** Control variable. Lists how many times the asset is referenced by other assets as dependency
   * or by direct manual load requests. */
  @Volatile var referenceCount: Int = 0
)

/**
 * Uniquely identifies a single asset stored in an [AssetStorage] by its [type] and [path].
 *
 * Multiple assets with the same [path] can be stored in an [AssetStorage] as long as they
 * have a different [type]. Similarly, [AssetStorage] can store multiple assets of the same
 * [type], as long as each has a different [path].
 *
 * Avoid using [Identifier] constructor directly. Instead, rely on [AssetStorage.getIdentifier]
 * or [AssetDescriptor.toIdentifier].
 */
data class Identifier<T>(
  /** File path to the asset compatible with the [AssetStorage.fileResolver]. Must be normalized. */
  val path: String,
  /** [Class] of the asset specified during loading. */
  val type: Class<T>
) {
  /**
   * Converts this [Identifier] to an [AssetDescriptor] that describes the asset and its loading data.
   *
   * If the returned [AssetDescriptor] is used to load an asset, and the asset requires specific loading
   * instructions, make sure to pass the loading [parameters] to set [AssetDescriptor.parameters]. Similarly,
   * if the asset requires a custom [FileHandle] incompatible with [AssetStorage.fileResolver], pass the
   * [fileHandle] parameter to set it as [AssetDescriptor.file].
   *
   * If the [AssetDescriptor] is used to simply identify an asset similarly to an [Identifier],
   * [parameters] and [fileHandle] are not required. You can retrieve a loaded asset from the
   * [AssetStorage] with either its [Identifier] or an [AssetDescriptor] without loading data -
   * the parameters and file are only used when calling [AssetStorage.load].
   */
  fun toAssetDescriptor(
    parameters: AssetLoaderParameters<T>? = null, fileHandle: FileHandle? = null
  ): AssetDescriptor<T> =
    AssetDescriptor(path, type, parameters).apply {
      if (fileHandle != null) {
        file = fileHandle
      }
    }
}

/**
 * Converts this [AssetDescriptor] to an [AssetStorage] [Identifier].
 * Copies [AssetDescriptor.type] to [Identifier.type] and [AssetDescriptor.fileName] to [Identifier.path].
 *
 * Note that loading parameters from [AssetDescriptor.parameters] are ignored. If the returned [Identifier]
 * is used to load an asset, and the asset requires specific loading instructions, make sure to pass the
 * loading parameters to the [AssetStorage.load] method.
 *
 * Similarly, [AssetDescriptor.file] is not used by the [Identifier]. Instead, [AssetDescriptor.fileName]
 * will be used to resolve the file using [AssetStorage.fileResolver]. If a [FileHandle] of a different type
 * is required, use [AssetDescriptor] for loading instead.
 */
fun <T> AssetDescriptor<T>.toIdentifier(): Identifier<T> = Identifier(fileName, type)
