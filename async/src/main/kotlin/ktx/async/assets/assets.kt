package ktx.async.assets

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
import ktx.async.KtxAsync
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect as ParticleEffect3D
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader as ParticleEffect3dLoader
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Asynchronous asset loader based on coroutines API. An [AssetManager] alternative.
 *
 * Note that [KtxAsync] coroutines context should be initiated before creating an [AssetStorage].
 *
 * @param fileResolver determines how file paths are interpreted. Defaults to [InternalFileHandleResolver].
 * @param executor performs asynchronous file loading. Defaults to [AsyncExecutor] of [KtxAsync] coroutine context.
 * @param useDefaultLoaders if true (the default), all default LibGDX asset loaders will be registered.
 */
class AssetStorage(
    val fileResolver: FileHandleResolver = InternalFileHandleResolver(),
    val executor: AsyncExecutor = KtxAsync.asyncExecutor,
    useDefaultLoaders: Boolean = true
) : Disposable {
  @Suppress("LeakingThis")
  private val asAssetManager: AssetManager = AssetManagerWrapper(this)
  private val loaderStorage = AssetLoaderStorage()

  /** Internal assets storage. Exposed for inlined methods. Should be accessed only from the rendering thread. Do not
   * modify manually.
   * @see load
   * @see add */
  val assets = ObjectMap<String, Any>()
  /** Stores path to the currently loaded asset. `null` if no asset is currently in the loading stage. */
  var currentlyLoadedAsset: String? = null
    private set

  init {
    if (useDefaultLoaders) {
      setLoader(TextAssetLoader(fileResolver))
      setLoader(BitmapFontLoader(fileResolver))
      setLoader(MusicLoader(fileResolver))
      setLoader(PixmapLoader(fileResolver))
      setLoader(SoundLoader(fileResolver))
      setLoader(TextureAtlasLoader(fileResolver))
      setLoader(TextureLoader(fileResolver))
      setLoader(SkinLoader(fileResolver))
      setLoader(ParticleEffectLoader(fileResolver))
      setLoader(ParticleEffect3dLoader(fileResolver))
      setLoader(I18NBundleLoader(fileResolver))
      setLoader(G3dModelLoader(JsonReader(), fileResolver), suffix = ".g3dj")
      setLoader(G3dModelLoader(UBJsonReader(), fileResolver), suffix = ".g3db")
      setLoader(ObjLoader(fileResolver), suffix = ".obj")
      setLoader(ShaderProgramLoader(fileResolver))
    }
  }

  /**
   * @param Asset type of asset scheduled for loading.
   * @param path path to the asset consistent with the [fileResolver] file type.
   * @param parameters optional loading parameters passed to the appropriate [AssetLoader] instance.
   * @return fully loaded instance of [Asset].
   * @throws AssetStorageException if unable to load the asset.
   */
  inline suspend fun <reified Asset : Any> load(path: String, parameters: AssetLoaderParameters<Asset>? = null): Asset
      = load(getAssetDescriptor(path, parameters))

  /**
   * @param assetDescriptor stores data of the asset scheduled for loading.
   * @return fully loaded instance of [Asset].
   * @throws AssetStorageException if unable to load the asset.
   */
  suspend fun <Asset : Any> load(assetDescriptor: AssetDescriptor<Asset>): Asset {
    val path = assetDescriptor.fileName
    assets[path]?.apply {
      @Suppress("UNCHECKED_CAST")
      return this as Asset
    }
    resolveFile(assetDescriptor)
    val loader = getLoader(assetDescriptor.type, path)
        ?: throw AssetStorageException("No loader available for assets of type: ${assetDescriptor.type} for file: $path.")
    loader.getDependencies(assetDescriptor)?.forEach {
      if (!isLoaded(it.fileName)) load(it)
    }
    currentlyLoadedAsset = path
    val asset = try {
      when (loader) {
        is SynchronousLoader<Asset> -> loadWithSynchronousLoader(loader, assetDescriptor)
        is AsynchronousLoader<Asset> -> loadWithAsynchronousLoader(loader, assetDescriptor)
        else -> throw IllegalStateException("Invalid loader: $loader. It must extend Synchronous or AsynchronousAssetLoader.")
      }
    } catch (error: Throwable) {
      currentlyLoadedAsset = null
      throw AssetStorageException("Unable to load asset at: $path", cause = error)
    }
    add(path, asset)
    currentlyLoadedAsset = null
    return asset
  }

  private fun resolveFile(assetDescriptor: AssetDescriptor<*>): FileHandle {
    val file = assetDescriptor.file
    if (file == null) {
      val resolvedFile = fileResolver.resolve(assetDescriptor.fileName)
      assetDescriptor.file = resolvedFile
      return resolvedFile
    }
    return file
  }

  private suspend fun <Asset : Any> loadWithSynchronousLoader(
      synchronousLoader: SynchronousLoader<Asset>,
      descriptor: AssetDescriptor<Asset>): Asset =
      KtxAsync.asynchronous(executor) {
        // Yes, LibGDX AssetManager handles SynchronousAssetLoader loading on a separate thread. Go figure.
        synchronousLoader.load(asAssetManager, descriptor)
      }

  private suspend fun <Asset : Any> loadWithAsynchronousLoader(
      asynchronousLoader: AsynchronousLoader<Asset>,
      descriptor: AssetDescriptor<Asset>): Asset {
    KtxAsync.asynchronous(executor) { asynchronousLoader.loadAsync(asAssetManager, descriptor) }
    return asynchronousLoader.loadSync(asAssetManager, descriptor)
  }

  /**
   * @param Asset type of the requested asset.
   * @param path file path used to load the asset.
   * @return [Asset] instance if loaded and matching selected type. `null` otherwise.
   */
  inline operator fun <reified Asset> get(path: String): Asset? = assets[path.normalizePath()] as? Asset?

  /**
   * @param Asset type of the requested asset.
   * @param path path to the file represented by the asset.
   * @param alternative returned if the asset is not loaded yet or does not exist.
   * @return [Asset] instance if loaded and matching selected type. [alternative] otherwise.
   */
  inline fun <reified Asset> getOrElse(path: String, alternative: Asset): Asset = get<Asset>(path) ?: alternative

  /**
   * @param path path an asset consistent with the [fileResolver] file type.
   * @return true if the asset is loaded and managed by this [AssetStorage].
   */
  fun isLoaded(path: String): Boolean = assets.containsKey(path.normalizePath())

  /**
   * Allows to add an already loaded asset to the [AssetStorage].
   * @param path original path to the asset. Should be consistent with [fileResolver] type.
   * @param asset asset instance that will be associated with the given path.
   * @throws AssetStorageException if a different asset with the same path is already managed by the [AssetStorage].
   */
  fun add(path: String, asset: Any) {
    val normalized = path.normalizePath()
    val previousAsset = assets[normalized]
    if (previousAsset != null && previousAsset !== asset) {
      throw AssetStorageException("Asset with given path already loaded: $path. Did you use AssetStorage on multiple " +
          "threads? Did one of used AssetLoader instances explicitly called 'load' rather than declaring dependencies" +
          " through AssetLoader#getDependencies?")
    } else {
      assets.put(normalized, asset)
    }
  }

  /**
   * Matches [AssetDescriptor] pre-processing.
   * @return this string with normalized directory separators.
   */
  fun String.normalizePath() = replace('\\', '/')

  /**
   * Creates a new [AssetDescriptor] for the selected asset.
   * @param Asset target type of asset after loading.
   * @param path path to the file consistent with [fileResolver] asset type.
   * @param parameters optional loading parameters passed to [AssetLoader].
   * @return a new instance of [AssetDescriptor] with resolved [FileHandle].
   */
  inline fun <reified Asset : Any> getAssetDescriptor(
      path: String,
      parameters: AssetLoaderParameters<Asset>? = null): AssetDescriptor<Asset> {
    val descriptor = AssetDescriptor(path.normalizePath(), Asset::class.java, parameters)
    descriptor.file = fileResolver.resolve(path)
    return descriptor
  }

  /**
   * Removes asset loaded with the given path. Does nothing if asset was not loaded in the first place. Asset will be
   * disposed if it implements [Disposable].
   * @param path used to [load] the asset.
   */
  fun unload(path: String) {
    (assets.remove(path.normalizePath()) as? Disposable)?.dispose()
  }

  /**
   * Removes asset loaded with the given path. Does nothing if asset was not loaded in the first place. Does _not_
   * dispose of the asset - it simply removes it from the [AssetStorage]. Use this method if you want to load the asset
   * with the loader, but fully control the asset lifecycle.
   * @param Asset type of the loaded asset.
   * @param path used to [load] the asset.
   * @return asset of the selected type if the asset was a fully loaded instance of [Asset].
   */
  inline fun <reified Asset> remove(path: String): Asset? = assets.remove(path.normalizePath()) as? Asset?

  /**
   * Disposes of all loaded [assets]. Rough equivalent of calling [unload] on each asset.
   * @throws AssetStorageException if unable to dispose an asset.
   */
  override fun dispose() {
    dispose { path, error ->
      throw AssetStorageException("Unable to dispose of the asset: $path", cause = error)
    }
  }

  /**
   * Disposes of all loaded [assets]. Rough equivalent of calling [unload] on each asset.
   * @param onError will be invoked each time disposing of an asset throws an exception.
   */
  inline fun dispose(onError: (path: String, error: Throwable) -> Unit) {
    assets.forEach {
      val path = it.key
      val asset = it.value
      try {
        (asset as? Disposable)?.dispose()
      } catch (error: Throwable) {
        onError(path, error)
      }
    }
    assets.clear()
  }

  /**
   * @param Asset type of loaded assets.
   * @param path optional path of the assets to load. Depending on the file extension, loader might be different.
   * @return [AssetLoader] associated with the selected type and path or null.
   */
  inline fun <reified Asset : Any> getLoader(path: String? = null): Loader<Asset>? = getLoader(Asset::class.java, path)

  /**
   * Internal API exposed for inlined method. See [getLoader] with generics.
   * @param type type of loaded assets.
   * @param path optional path of the assets to load. Depending on the file extension, loader might be different.
   * @return [AssetLoader] associated with the selected type and path or null.
   */
  fun <Asset> getLoader(type: Class<Asset>, path: String?): Loader<Asset>?
      = loaderStorage.getLoader(type, path?.normalizePath())

  /**
   * @param Asset type of loaded assets.
   * @param loader will handle loading of instances of the selected asset type.
   * @param suffix optional suffix (extension) of files that will be handled by the passed loader. If null or empty,
   *    passed loader will be the default loader, requiring no specific file extension.
   */
  inline fun <reified Asset : Any> setLoader(loader: Loader<Asset>, suffix: String? = null)
      = setLoader(Asset::class.java, loader, suffix)

  /**
   * Internal API exposed for inlined method. See [setLoader] with generics.
   * @param type type of loaded assets.
   * @param loader will handle loading of instances of the selected asset type.
   * @param suffix optional suffix (extension) of files that will be handled by the passed loader. If null or empty,
   *    passed loader will be the default loader, requiring no specific file extension.
   */
  fun <Asset> setLoader(type: Class<Asset>, loader: Loader<Asset>, suffix: String?) {
    loaderStorage.setLoader(type, loader, suffix)
  }

  override fun toString(): String
      = "AssetStorage[assets=$assets, loaders=$loaderStorage, executor=$executor, fileResolver=$fileResolver]"
}

/**
 * Thrown by [AssetStorage] on unexpected operations.
 * @param message describes the problem.
 * @param cause optional cause of the exception.
 */
class AssetStorageException(message: String, cause: Throwable? = null) : GdxRuntimeException(message, cause)
