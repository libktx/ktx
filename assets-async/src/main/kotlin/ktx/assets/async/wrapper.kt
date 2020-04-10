package ktx.assets.async

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.utils.Logger
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Extends [AssetManager], delegating all of its asset-related method calls to [assetStorage].
 * Allows to use classic [AssetLoader] implementations with [AssetStorage]. Internal API,
 * DO NOT use directly.
 */
@Suppress("DEPRECATION")
internal class AssetManagerWrapper(val assetStorage: AssetStorage)
  : AssetManager(assetStorage.fileResolver, false) {
  private var initiated = false

  init {
    // Shutting down super's executor:
    super.dispose()
    // Replacing logger:
    val logger = Logger("AssetStorage")
    logger.level = Logger.ERROR
    super.setLogger(logger)

    initiated = true
  }

  override fun clear() = dispose()

  @Deprecated("This operation is non-blocking. Assets might still be loaded after this call.",
    replaceWith = ReplaceWith("AssetStorage.dispose"))
  override fun dispose() {
    if (initiated) {
      logger.error("Not fully supported AssetManagerWrapper.dispose called by AssetLoader.")
      KtxAsync.launch {
        assetStorage.dispose { path, error ->
          logger.error("Unable to dispose of the asset: $path", error)
        }
      }
    }
  }

  @Deprecated("Not supported by AssetStorage.",
    replaceWith = ReplaceWith("contains(fileName, type)"))
  override fun contains(fileName: String): Boolean = false
  override fun contains(fileName: String, type: Class<*>?): Boolean =
    assetStorage.contains(AssetDescriptor(fileName, type))

  @Deprecated("This operation is non-blocking. Assets might not be available in storage after call.",
    replaceWith = ReplaceWith("AssetStorage.add"))
  override fun <T : Any> addAsset(fileName: String, type: Class<T>, asset: T) {
    logger.error("Not fully supported AssetManagerWrapper.addAsset called by AssetLoader.")
    KtxAsync.launch {
      assetStorage.add(AssetDescriptor(fileName, type), asset)
    }
  }

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun taskFailed(assetDesc: AssetDescriptor<*>?, ex: RuntimeException?) =
    throw UnsupportedMethodException("taskFailed")

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun setErrorListener(listener: AssetErrorListener?) {
    logger.error("Not fully supported AssetManagerWrapper.setErrorListener called by AssetLoader.")
  }

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun <T : Any> containsAsset(asset: T): Boolean = throw UnsupportedMethodException("containsAsset")

  override fun <Asset : Any> get(assetDescriptor: AssetDescriptor<Asset>): Asset =
    get(assetDescriptor.fileName, assetDescriptor.type)

  override fun <Asset : Any> get(fileName: String, type: Class<Asset>): Asset {
    val identifier = Identifier(fileName, type)
    return try {
      assetStorage[identifier]
    } catch (exception: Throwable) {
      throw MissingDependencyException(identifier, exception)
    }
  }

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("get(fileName, type)"))
  override fun <Asset : Any> get(fileName: String): Asset = throw UnsupportedMethodException("get(String)")

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun getAssetNames(): GdxArray<String> = throw UnsupportedMethodException("getAssetNames")

  @Deprecated("Multiple assets with different types can be listed under the same path.",
    replaceWith = ReplaceWith("Nothing"))
  override fun getAssetType(fileName: String): Class<*>? = throw UnsupportedMethodException("getAssetType")

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun <Asset : Any> getAll(type: Class<Asset>, out: GdxArray<Asset>): GdxArray<Asset> =
    throw UnsupportedMethodException("getAll")

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun <T> getAssetFileName(asset: T): String? {
    logger.error("Not supported AssetManagerWrapper.getAssetFileName called by AssetLoader.")
    return null
  }

  override fun getDiagnostics(): String = assetStorage.toString()
  override fun getFileHandleResolver(): FileHandleResolver = assetStorage.fileResolver

  override fun getProgress(): Float = assetStorage.progress.percent
  override fun getLoadedAssets(): Int = assetStorage.progress.loaded
  override fun isFinished(): Boolean = assetStorage.progress.isFinished

  override fun <Asset : Any> getLoader(type: Class<Asset>): AssetLoader<*, *>? = getLoader(type, "")
  override fun <T : Any> getLoader(type: Class<T>, fileName: String): AssetLoader<*, *>? =
    assetStorage.getLoader(type, fileName)

  override fun isLoaded(assetDesc: AssetDescriptor<*>): Boolean = assetStorage.isLoaded(assetDesc)
  override fun isLoaded(fileName: String, type: Class<*>): Boolean = isLoaded(AssetDescriptor(fileName, type))

  @Deprecated("Not supported by AssetStorage.",
    replaceWith = ReplaceWith("isLoaded(fileName, type)"))
  override fun isLoaded(fileName: String): Boolean = false.also {
    logger.error("Not supported AssetManagerWrapper.addAsset called by AssetLoader.")
  }

  @Deprecated("AssetStorage requires type of asset to unload.",
    replaceWith = ReplaceWith("AssetStorage.unload"))
  override fun unload(fileName: String) {
    logger.error("Not supported AssetManagerWrapper.unload called by AssetLoader.")
  }

  override fun <T : Any> load(fileName: String, type: Class<T>) = load(fileName, type, null)
  override fun <T : Any> load(fileName: String, type: Class<T>, parameters: AssetLoaderParameters<T>?) =
    load(AssetDescriptor(fileName, type, parameters))

  override fun load(descriptor: AssetDescriptor<*>) {
    KtxAsync.launch {
      assetStorage.load(descriptor)
    }
  }

  @Deprecated("AssetLoader instances can be mutable." +
    "AssetStorage requires functional providers of loaders rather than singular instances.",
    replaceWith = ReplaceWith("AssetStorage.setLoader"))
  override fun <T : Any?, P : AssetLoaderParameters<T>?> setLoader(type: Class<T>, loader: AssetLoader<T, P>) =
    setLoader(type, null, loader)

  @Deprecated("AssetLoader instances can be mutable." +
    "AssetStorage requires functional providers of loaders rather than singular instances.",
    replaceWith = ReplaceWith("AssetStorage.setLoader"))
  override fun <T : Any?, P : AssetLoaderParameters<T>?> setLoader(
    type: Class<T>, suffix: String?, loader: AssetLoader<T, P>
  ) {
    logger.error("Not fully supported AssetManagerWrapper.setLoader called by AssetLoader.")
    assetStorage.setLoader(type, suffix) {
      @Suppress("UNCHECKED_CAST")
      loader as Loader<T>
    }
  }

  @Deprecated("AssetStorage requires type to find dependencies.",
    replaceWith = ReplaceWith("AssetStorage.getDependencies"))
  override fun getDependencies(fileName: String): GdxArray<String> = GdxArray.with<String>().also {
    logger.error("Not supported AssetManagerWrapper.getDependencies called by AssetLoader.")
  }

  @Deprecated("AssetStorage requires type to find reference count.",
    replaceWith = ReplaceWith("AssetStorage.getReferenceCount"))
  override fun getReferenceCount(fileName: String): Int = 0.also {
    logger.error("Not supported AssetManagerWrapper.getReferenceCount called by AssetLoader.")
  }

  @Deprecated("AssetStorage does not have to be updated.", ReplaceWith("Nothing"))
  override fun update(millis: Int): Boolean = isFinished

  @Deprecated("AssetStorage does not have to be updated.", ReplaceWith("Nothing"))
  override fun update(): Boolean = isFinished

  @Deprecated("Unsupported operation.", ReplaceWith("Nothing"))
  override fun setReferenceCount(fileName: String, refCount: Int) =
    throw UnsupportedMethodException("setReferenceCount")

  @Deprecated("AssetStorage does not maintain an assets queue.", ReplaceWith("Nothing"))
  override fun getQueuedAssets(): Int = 0.also {
    logger.error("Not supported AssetManagerWrapper.getQueuedAssets called by AssetLoader.")
  }

  @Deprecated("Unsupported operation.", ReplaceWith("Nothing"))
  override fun finishLoading() {
    logger.error("Not supported AssetManagerWrapper.finishLoading called by AssetLoader.")
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> finishLoadingAsset(assetDesc: AssetDescriptor<*>): T =
    get(assetDesc as AssetDescriptor<T>)

  @Deprecated("Unsupported without asset type.", ReplaceWith("finishLoadingAsset(assetDescriptor)"))
  override fun <T : Any?> finishLoadingAsset(fileName: String): T =
    throw UnsupportedMethodException("finishLoadingAsset(String)")

  override fun toString(): String = "AssetManagerWrapper(storage=$assetStorage)"
  override fun hashCode(): Int = assetStorage.hashCode()
  override fun equals(other: Any?): Boolean =
    other is AssetManagerWrapper && other.assetStorage === assetStorage
}
