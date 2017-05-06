package ktx.async.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.utils.ObjectMap

typealias SynchronousLoader<Asset> = SynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>
typealias AsynchronousLoader<Asset> = AsynchronousAssetLoader<Asset, AssetLoaderParameters<Asset>>
typealias Loader<Asset> = AssetLoader<Asset, AssetLoaderParameters<Asset>>

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
    getOrCreateLoadersContainer(type).apply {
      if (suffix.isNullOrEmpty()) {
        mainLoader = loader
      } else {
        loadersBySuffix.put(suffix, loader)
      }
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
