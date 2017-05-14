package ktx.async.assets

import com.badlogic.gdx.Application
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.async.ktxAsync
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Extends [AssetManager], delegating all of its asset-related method calls to [AssetStorage]. Allows to use classic
 * [AssetLoader] implementations with [AssetStorage].
 * @param assetStorage all [AssetManager] API calls will be delegated to this storage.
 */
internal class AssetManagerWrapper(val assetStorage: AssetStorage) : AssetManager(assetStorage.fileResolver, false) {
  // Implementation note:
  var initiated = false

  init {
    // Shutting down super's executor:
    super.dispose()
    logger.level = Application.LOG_ERROR
    initiated = true
  }

  override fun clear() = dispose()
  override fun dispose() {
    if (initiated) {
      assetStorage.dispose { path, error -> logger.error("Unable to dispose of the asset: $path", error) }
    }
  }

  override fun <T : Any> containsAsset(asset: T): Boolean {
    return assetStorage.assets.values().any { it == asset }
  }

  override fun <Asset : Any> get(assetDescriptor: AssetDescriptor<Asset>): Asset = get(assetDescriptor.fileName)
  override fun <Asset : Any> get(fileName: String, type: Class<Asset>?): Asset = get(fileName)
  @Suppress("UNCHECKED_CAST")
  override fun <Asset : Any> get(fileName: String): Asset
      = assetStorage.assets[fileName.normalize()] as Asset? ?: throw AssetStorageException("Asset not loaded: $fileName")

  override fun getAssetNames(): GdxArray<String> = assetStorage.assets.keys().toArray()
  override fun getAssetType(fileName: String): Class<*>? = assetStorage.assets[fileName.normalize()]?.javaClass
  private fun String.normalize() = replace('\\', '/')

  @Suppress("UNCHECKED_CAST")
  override fun <Asset : Any> getAll(type: Class<Asset>, out: GdxArray<Asset>): GdxArray<Asset> {
    assetStorage.assets.values()
        .filter { type.isInstance(it) }
        .forEach { out.add(it as Asset) }
    return out
  }

  override fun <T> getAssetFileName(asset: T): String? = assetStorage.assets.firstOrNull { it.value == asset }?.key
  override fun getDiagnostics(): String = assetStorage.toString()
  override fun getFileHandleResolver(): FileHandleResolver = assetStorage.fileResolver
  override fun getLoadedAssets(): Int = assetStorage.assets.size
  override fun <Asset : Any> getLoader(type: Class<Asset>): AssetLoader<*, *>? = getLoader(type, "")
  override fun <T : Any> getLoader(type: Class<T>, fileName: String): AssetLoader<*, *>?
      = assetStorage.getLoader(type, fileName)

  override fun isLoaded(fileName: String): Boolean = assetStorage.isLoaded(fileName)
  override fun isLoaded(fileName: String, type: Class<*>): Boolean =
      assetStorage.assets.get(fileName.normalize())?.let { type.isInstance(fileName) } ?: false

  override fun unload(fileName: String) = assetStorage.unload(fileName)
  override fun <T : Any> load(fileName: String, type: Class<T>) = load(fileName, type, null)
  override fun <T : Any> load(fileName: String, type: Class<T>, parameters: AssetLoaderParameters<T>?) {
    val descriptor = AssetDescriptor(fileName, type, parameters)
    descriptor.file = assetStorage.fileResolver.resolve(fileName)
    load(descriptor)
  }

  override fun load(descriptor: AssetDescriptor<*>) {
    ktxAsync {
      assetStorage.load(descriptor)
    }
  }

  override fun <T : Any?, P : AssetLoaderParameters<T>?> setLoader(type: Class<T>, loader: AssetLoader<T, P>)
      = setLoader(type, null, loader)

  override fun <T : Any?, P : AssetLoaderParameters<T>?> setLoader(type: Class<T>, suffix: String?, loader: AssetLoader<T, P>) {
    @Suppress("UNCHECKED_CAST")
    assetStorage.setLoader(type, loader as Loader<T>, suffix)
  }

  override fun getDependencies(fileName: String): GdxArray<String> =
      GdxArray.with(*assetStorage.getDependencies(fileName).toTypedArray())

  override fun getReferenceCount(fileName: String): Int = assetStorage.getReferencesCount(fileName)

  @Deprecated("AssetStorage does not have to be updated.", ReplaceWith("Nothing"))
  override fun update(millis: Int): Boolean = assetStorage.currentlyLoadedAsset == null

  @Deprecated("AssetStorage does not have to be updated.", ReplaceWith("Nothing"))
  override fun update(): Boolean = assetStorage.currentlyLoadedAsset == null

  @Deprecated("Unsupported operation.", ReplaceWith("Nothing"))
  override fun setReferenceCount(fileName: String, refCount: Int) = Unit

  @Deprecated("Since AssetStorage does not force asset scheduling up front, it cannot track the file loading progress.",
      ReplaceWith("AssetStorage.currentlyLoadedAsset"))
  override fun getProgress(): Float = 0f

  @Deprecated("AssetStorage does not maintain an assets queue.", ReplaceWith("Nothing"))
  override fun getQueuedAssets(): Int = 0

  @Deprecated("AssetStorage loads assets via coroutines. It does not maintain an assets queue.", ReplaceWith("Nothing"))
  override fun finishLoading() = throw GdxRuntimeException("Unable to force loading of all assets.")

  @Deprecated("AssetStorage loads assets via coroutines. It does not maintain an assets queue.", ReplaceWith("Nothing"))
  override fun finishLoadingAsset(fileName: String) {
    if (!assetStorage.isLoaded(fileName)) {
      throw GdxRuntimeException("Unable to force loading of asset: $fileName")
    }
  }

  override fun equals(other: Any?): Boolean = other is AssetManagerWrapper && other.assetStorage === assetStorage
  override fun hashCode(): Int = assetStorage.hashCode()
  override fun toString(): String = assetStorage.toString()
}
