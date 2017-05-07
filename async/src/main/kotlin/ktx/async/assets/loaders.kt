package ktx.async.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.ObjectMap
import ktx.async.assets.TextAssetLoader.TextAssetLoaderParameters
import com.badlogic.gdx.utils.Array as GdxArray

/** Stores [AssetLoader] instances mapped by loaded asset type. [AssetStorage] utility. */
internal class AssetLoaderStorage {
  private val loaders: ObjectMap<Class<*>, AssetLoaderContainer<*>> = ObjectMap()

  /**
   * @param type common base class of loaded assets.
   * @param path path of the class to load. Optional, if empty or not given, will return main loader.
   * @return [AssetLoader] instance that should be used to load the given asset class. Null if no loader is present for
   *    the selected type.
   */
  fun <Asset> getLoader(type: Class<Asset>, path: String? = null): Loader<Asset>? {
    @Suppress("UNCHECKED_CAST")
    val loadersForType = loaders[type] as AssetLoaderContainer<Asset>? ?: return null
    if (path == null || loadersForType.loadersBySuffix.size == 0) return loadersForType.mainLoader
    var closestSuffixLength = 0
    var loader = loadersForType.mainLoader
    loadersForType.loadersBySuffix.forEach {
      val suffix = it.key
      if (closestSuffixLength < suffix.length && path.endsWith(suffix)) {
        closestSuffixLength = suffix.length
        loader = it.value
      }
    }
    return loader
  }

  /**
   * Adds or replaces loader for the given class.
   * @param type common base class of loaded asset.
   * @param loader will be saved in the storage.
   * @param suffix optional suffix of handled files. If there are no loaders for the given assets type, [loader] will
   *    also become the main loader for the given type.
   */
  fun <Asset> setLoader(type: Class<Asset>, loader: Loader<Asset>, suffix: String? = null) {
    validate(loader)
    getOrCreateLoadersContainer(type).apply {
      if (suffix.isNullOrEmpty()) {
        mainLoader = loader
      } else {
        loadersBySuffix.put(suffix, loader)
      }
    }
  }

  private fun <Asset> validate(loader: Loader<Asset>) {
    if (loader !is SynchronousAssetLoader<*, *> && loader !is AsynchronousAssetLoader<*, *>) {
      throw AssetStorageException("Unable to register loader: $loader. " +
          "Asset loaders must extend either SynchronousAssetLoader or AsynchronousAssetLoader.")
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
    val loadersBySuffix: ObjectMap<String, Loader<Asset>> = ObjectMap()
    var mainLoader: Loader<Asset>?
      get() = loadersBySuffix[""]
      set(value) {
        loadersBySuffix.put("", value)
      }

    override fun toString(): String = loadersBySuffix.toString()
  }
}

/**
 * Allows to read text files with an [AssetManager] or an [AssetStorage].
 * @param fileResolver not used, required by the superclass.
 * @param charset name of the charset used to read text. Can be overridden with [TextAssetLoaderParameters]. Should
 *    match text files encoding. Defaults to UTF-8.
 */
class TextAssetLoader(
    fileResolver: FileHandleResolver,
    private val charset: String = "UTF-8"
) : SynchronousAssetLoader<String, TextAssetLoaderParameters>(fileResolver) {
  override fun load(
      assetManager: AssetManager?,
      fileName: String?,
      file: FileHandle,
      parameter: TextAssetLoaderParameters?): String = file.readString(parameter?.charset ?: charset)

  override fun getDependencies(fileName: String?, file: FileHandle?, parameter: TextAssetLoaderParameters?):
      GdxArray<AssetDescriptor<Any>>? = null

  /**
   * Optional parameters used to load text files.
   * @param charset name of the charset used to read text. Should match text file encoding. Defaults to UTF-8.
   */
  class TextAssetLoaderParameters(var charset: String = "UTF-8") : AssetLoaderParameters<String>()
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
 * @param assetDescriptor contains asset data.
 * @return [com.badlogic.gdx.utils.Array] with asset dependencies described with [AssetDescriptor] instances. Null if
 *    there are no dependencies.
 */
fun Loader<*>.getDependencies(assetDescriptor: AssetDescriptor<*>): GdxArray<AssetDescriptor<*>>? =
    @Suppress("UNCHECKED_CAST")
    (this as AssetLoader<*, AssetLoaderParameters<*>>).
        getDependencies(assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)

/**
 * Allows to use [SynchronousAssetLoader.load] method with [AssetDescriptor].
 * @param assetManager provides asset dependencies for the loader.
 * @param assetDescriptor contains asset data.
 * @return fully loaded [Asset] instance.
 */
fun <Asset> SynchronousLoader<Asset>.load(assetManager: AssetManager, assetDescriptor: AssetDescriptor<Asset>): Asset =
    @Suppress("UNCHECKED_CAST")
    (this as SynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>)
        .load(assetManager, assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)

/**
 * Allows to use [AsynchronousAssetLoader.loadAsync] method with [AssetDescriptor]. Performs the asynchronous asset
 * loading part without yielding results.
 * @param assetManager provides asset dependencies for the loader.
 * @param assetDescriptor contains asset data.
 */
fun <Asset> AsynchronousLoader<Asset>.loadAsync(assetManager: AssetManager, assetDescriptor: AssetDescriptor<Asset>) =
    @Suppress("UNCHECKED_CAST")
    (this as AsynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>)
        .loadAsync(assetManager, assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)

/**
 * Allows to use [AsynchronousAssetLoader.loadSync] method with [AssetDescriptor]. Note that [loadAsync] must have been
 * called first with the same asset data.
 * @param assetManager provides asset dependencies for the loader.
 * @param assetDescriptor contains asset data.
 * @return fully loaded [Asset] instance.
 */
fun <Asset> AsynchronousLoader<Asset>.loadSync(assetManager: AssetManager, assetDescriptor: AssetDescriptor<Asset>): Asset =
    @Suppress("UNCHECKED_CAST")
    (this as AsynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>)
        .loadSync(assetManager, assetDescriptor.fileName, assetDescriptor.file, assetDescriptor.parameters)
