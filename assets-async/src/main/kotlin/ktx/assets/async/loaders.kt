package ktx.assets.async

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Stores [AssetLoader] instances mapped by loaded asset type. Internal [AssetStorage] utility.
 *
 * Implementation note: LibGDX loaders are not thread-safe. Instead, they assume that only a single asset is loaded
 * at a time and use internal, unsynchronized fields to store temporary variables like the dependencies. To avoid
 * threading issues, we use a separate loader for each loaded asset instead of singleton instances - hence
 * the functional loader providers.
 */
internal class AssetLoaderStorage {
  private val loaders: ObjectMap<Class<*>, AssetLoaderContainer<*>> = ObjectMap()

  /**
   * Provides a [Loader] for the given asset [type]. Optionally, file [path] can be given,
   * as depending on the file suffix, a different loader might be used for the same asset type.
   */
  fun <Asset> getLoader(type: Class<Asset>, path: String? = null): Loader<Asset>? {
    @Suppress("UNCHECKED_CAST")
    val loadersForType = loaders[type] as AssetLoaderContainer<Asset>? ?: return null
    if (path == null || loadersForType.loadersBySuffix.size == 0) {
      return loadersForType.mainLoader?.invoke()
    }
    var maxMatchingSuffixLength = 0
    var loaderProvider = loadersForType.mainLoader
    loadersForType.loadersBySuffix.forEach {
      val suffix = it.key
      if (maxMatchingSuffixLength < suffix.length && path.endsWith(suffix)) {
        maxMatchingSuffixLength = suffix.length
        loaderProvider = it.value
      }
    }
    return loaderProvider?.invoke()
  }

  /**
   * Adds or replaces [Loader] for the given class. [loaderProvider] is invoked
   * each time an instance of the selected loader is requested. The loader will be
   * associated with the given asset [type]. Optionally, a [suffix] can be given
   * to a associate the loader with specific file paths.
   */
  fun <Asset> setLoaderProvider(type: Class<Asset>, suffix: String? = null, loaderProvider: () -> Loader<Asset>) {
    validate(loaderProvider)
    getOrCreateLoadersContainer(type).apply {
      if (suffix.isNullOrEmpty()) {
        mainLoader = loaderProvider
      } else {
        loadersBySuffix.put(suffix, loaderProvider)
      }
    }
  }

  private fun <Asset> validate(loaderProvider: () -> Loader<Asset>) {
    val loader = loaderProvider()
    if (loader !is SynchronousAssetLoader<*, *> && loader !is AsynchronousAssetLoader<*, *>) {
      throw InvalidLoaderException(loader)
    }
  }

  private fun <Asset> getOrCreateLoadersContainer(type: Class<Asset>): AssetLoaderContainer<Asset> {
    val loadersForType = loaders[type]
    if (loadersForType == null) {
      val container = AssetLoaderContainer<Asset>()
      loaders.put(type, container)
      return container
    }
    @Suppress("UNCHECKED_CAST")
    return loadersForType as AssetLoaderContainer<Asset>
  }

  override fun toString(): String = "AssetLoaderStorage[loaders=$loaders]"

  private class AssetLoaderContainer<Asset> {
    val loadersBySuffix: ObjectMap<String, () -> Loader<Asset>> = ObjectMap()
    var mainLoader: (() -> Loader<Asset>)?
      get() = loadersBySuffix[""]
      set(value) {
        loadersBySuffix.put("", value)
      }

    override fun toString(): String = loadersBySuffix.toString()
  }
}

// Workarounds for LibGDX generics API.

/** [AssetLoader] with improved generics. */
typealias Loader<Asset> = AssetLoader<Asset, out AssetLoaderParameters<Asset>>

/** [SynchronousAssetLoader] with improved generics. */
typealias SynchronousLoader<Asset> = SynchronousAssetLoader<Asset, out AssetLoaderParameters<Asset>>

/** [AsynchronousAssetLoader] with improved generics. */
typealias AsynchronousLoader<Asset> = AsynchronousAssetLoader<Asset, out AssetLoaderParameters<Asset>>

/** Casts [AssetDescriptor.params] stored with raw type. */
private val <Asset> AssetDescriptor<Asset>.parameters: AssetLoaderParameters<Asset>?
  @Suppress("UNCHECKED_CAST")
  get() = params as AssetLoaderParameters<Asset>?

/**
 * Allows to use [AssetLoader.getDependencies] method with [AssetDescriptor].
 * [assetDescriptor] contains asset data.
 * Returns a [com.badlogic.gdx.utils.Array] with asset dependencies described
 * with [AssetDescriptor] instances. Null if here are no dependencies.
 */
fun Loader<*>.getDependencies(assetDescriptor: AssetDescriptor<*>): GdxArray<AssetDescriptor<*>> =
    @Suppress("UNCHECKED_CAST")
    (this as AssetLoader<*, AssetLoaderParameters<*>>)
        .getDependencies(assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters) ?: GdxArray(0)

/**
 * Allows to use [SynchronousAssetLoader.load] method with [AssetDescriptor].
 * [assetManager] provides asset dependencies for the loader.
 * [assetDescriptor] contains asset data. Returns fully loaded [Asset] instance.
 */
fun <Asset> SynchronousLoader<Asset>.load(assetManager: AssetManager, assetDescriptor: AssetDescriptor<Asset>): Asset =
    @Suppress("UNCHECKED_CAST")
    (this as SynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>)
        .load(assetManager, assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)

/**
 * Allows to use [AsynchronousAssetLoader.loadAsync] method with [AssetDescriptor].
 * Performs the asynchronous asset loading part without yielding results.
 * [assetManager] provides asset dependencies for the loader.
 * [assetDescriptor] contains asset data.
 */
fun <Asset> AsynchronousLoader<Asset>.loadAsync(assetManager: AssetManager, assetDescriptor: AssetDescriptor<Asset>) =
    @Suppress("UNCHECKED_CAST")
    (this as AsynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>)
        .loadAsync(assetManager, assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)

/**
 * Allows to use [AsynchronousAssetLoader.loadSync] method with [AssetDescriptor].
 * Note that [loadAsync] must have been called first with the same asset data.
 * [assetManager] provides asset dependencies for the loader.
 * [assetDescriptor] contains asset data. Returns fully loaded [Asset] instance.
 */
fun <Asset> AsynchronousLoader<Asset>.loadSync(assetManager: AssetManager, assetDescriptor: AssetDescriptor<Asset>): Asset =
    @Suppress("UNCHECKED_CAST")
    (this as AsynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>)
        .loadSync(assetManager, assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)

/** Required for [ManualLoader] by LibGDX API. */
internal class ManualLoadingParameters : AssetLoaderParameters<Any>()

/** Mocks [AssetLoader] API for assets manually added to the [AssetStorage]. See [AssetStorage.add]. */
internal object ManualLoader : AssetLoader<Any, ManualLoadingParameters>(AbsoluteFileHandleResolver()) {
  private val emptyDependencies = GdxArray<AssetDescriptor<Any>>(0)
  override fun getDependencies(
    fileName: String?, file: FileHandle?,
    parameter: ManualLoadingParameters?
  ): GdxArray<AssetDescriptor<Any>> = emptyDependencies
}
