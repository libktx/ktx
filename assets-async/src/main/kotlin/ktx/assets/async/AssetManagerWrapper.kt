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
 * Allows using classic [AssetLoader] implementations with [AssetStorage]. Internal API,
 * DO NOT use directly.
 */
@Suppress("DEPRECATION")
internal class AssetManagerWrapper(
  val assetStorage: AssetStorage,
) : AssetManager(assetStorage.fileResolver, false) {
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

  @Deprecated(
    "This operation is non-blocking. Assets might still be loaded after this call.",
    replaceWith = ReplaceWith("AssetStorage.dispose"),
  )
  override fun clear() = dispose()

  @Deprecated(
    "This operation is non-blocking. Assets might still be loaded after this call.",
    replaceWith = ReplaceWith("AssetStorage.dispose"),
  )
  override fun dispose() {
    if (initiated) {
      logIncompleteSupportWarning("dispose")
      KtxAsync.launch {
        assetStorage.dispose { path, error ->
          logWarning("Unable to dispose of the asset: $path", error)
        }
      }
    }
  }

  @Deprecated(
    "AssetStorage requires asset class to check if it is loaded.",
    replaceWith = ReplaceWith("contains(fileName, type)"),
  )
  override fun contains(fileName: String): Boolean =
    try {
      getIdentifier(fileName) != null
    } catch (exception: AssetLoadingException) {
      // Multiple assets with the same path.
      true
    }

  override fun contains(
    fileName: String,
    type: Class<*>?,
  ): Boolean = assetStorage.contains(AssetDescriptor(fileName, type))

  @Deprecated(
    "This operation is non-blocking. Assets might not be available in storage after call.",
    replaceWith = ReplaceWith("AssetStorage.add"),
  )
  override fun <T : Any> addAsset(
    fileName: String,
    type: Class<T>,
    asset: T,
  ) {
    logIncompleteSupportWarning("addAsset")
    KtxAsync.launch {
      assetStorage.add(AssetDescriptor(fileName, type), asset)
    }
  }

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun taskFailed(
    assetDesc: AssetDescriptor<*>?,
    ex: RuntimeException?,
  ) = throw UnsupportedMethodException("taskFailed")

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun setErrorListener(listener: AssetErrorListener?) {
    logNoSupportWarning("setErrorListener")
  }

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun <T : Any> containsAsset(asset: T): Boolean = throw UnsupportedMethodException("containsAsset")

  override fun <Asset : Any> get(
    fileName: String,
    type: Class<Asset>,
  ): Asset = get(fileName, type, required = true)!!

  override fun <Asset : Any> get(assetDescriptor: AssetDescriptor<Asset>): Asset =
    get(assetDescriptor.fileName, assetDescriptor.type, required = true)!!

  override fun <Asset : Any> get(
    fileName: String,
    type: Class<Asset>,
    required: Boolean,
  ): Asset? {
    val identifier = Identifier(fileName, type)
    return try {
      assetStorage[identifier]
    } catch (exception: Throwable) {
      if (required) {
        throw MissingDependencyException(identifier, exception)
      } else {
        logWarning("Missing asset requested: $fileName", exception)
        null
      }
    }
  }

  @Deprecated("AssetStorage requires type to find a stored asset.", replaceWith = ReplaceWith("get(fileName, type, required)"))
  override fun <Asset : Any> get(
    fileName: String,
    required: Boolean,
  ): Asset? {
    logCollisionWarning("get(String, Boolean)", fileName)
    @Suppress("UNCHECKED_CAST")
    val identifier = getIdentifier(fileName) as Identifier<Asset>?
    val asset =
      identifier?.let {
        try {
          assetStorage[it]
        } catch (exception: Throwable) {
          null
        }
      }
    if (asset == null) {
      if (required) {
        throw MissingDependencyException("Required asset not found: $fileName")
      } else {
        logWarning("Missing asset requested: $fileName")
      }
    }
    return asset
  }

  @Deprecated("AssetStorage requires type to find a stored asset.", replaceWith = ReplaceWith("get(fileName, type)"))
  override fun <Asset : Any> get(fileName: String): Asset = get(fileName, true)!!

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun getAssetNames(): GdxArray<String> = throw UnsupportedMethodException("getAssetNames")

  @Deprecated(
    "Multiple assets with different types can be listed under the same path.",
    replaceWith = ReplaceWith("Nothing"),
  )
  override fun getAssetType(fileName: String): Class<*>? {
    logCollisionWarning("getAssetType", fileName)
    val identifier = getIdentifier(fileName)
    return identifier?.type
  }

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun <Asset : Any> getAll(
    type: Class<Asset>,
    out: GdxArray<Asset>,
  ): GdxArray<Asset> = throw UnsupportedMethodException("getAll")

  @Deprecated("Not supported by AssetStorage.", replaceWith = ReplaceWith("Nothing"))
  override fun <T> getAssetFileName(asset: T): String? {
    logNoSupportWarning("getAssetFileName")
    return null
  }

  override fun getDiagnostics(): String = assetStorage.toString()

  override fun getFileHandleResolver(): FileHandleResolver = assetStorage.fileResolver

  override fun getProgress(): Float = assetStorage.progress.percent

  override fun getLoadedAssets(): Int = assetStorage.progress.loaded

  override fun isFinished(): Boolean = assetStorage.progress.isFinished

  override fun <Asset : Any> getLoader(type: Class<Asset>): AssetLoader<*, *>? = getLoader(type, "")

  override fun <T : Any> getLoader(
    type: Class<T>,
    fileName: String,
  ): AssetLoader<*, *>? = assetStorage.getLoader(type, fileName)

  override fun isLoaded(assetDesc: AssetDescriptor<*>): Boolean = assetStorage.isLoaded(assetDesc)

  override fun isLoaded(
    fileName: String,
    type: Class<*>,
  ): Boolean = isLoaded(AssetDescriptor(fileName, type))

  @Deprecated(
    "AssetStorage requires asset type to check if it is loaded.",
    replaceWith = ReplaceWith("isLoaded(fileName, type)"),
  )
  override fun isLoaded(fileName: String): Boolean {
    logCollisionWarning("isLoaded(String)", fileName)
    val identifier = getIdentifier(fileName)
    return identifier != null && assetStorage.isLoaded(identifier)
  }

  @Deprecated(
    "AssetStorage requires type of asset to unload.",
    replaceWith = ReplaceWith("AssetStorage.unload"),
  )
  override fun unload(fileName: String) {
    logCollisionWarning("unload", fileName)
    val identifier = getIdentifier(fileName) ?: return
    KtxAsync.launch(assetStorage.asyncContext) {
      assetStorage.unload(identifier)
    }
  }

  override fun <T : Any> load(
    fileName: String,
    type: Class<T>,
  ) = load(fileName, type, null)

  override fun <T : Any> load(
    fileName: String,
    type: Class<T>,
    parameters: AssetLoaderParameters<T>?,
  ) = load(AssetDescriptor(fileName, type, parameters))

  override fun load(descriptor: AssetDescriptor<*>) {
    KtxAsync.launch(assetStorage.asyncContext) {
      assetStorage.load(descriptor)
    }
  }

  @Deprecated(
    "AssetLoader instances can be mutable." +
      "AssetStorage requires functional providers of loaders rather than singular instances.",
    replaceWith = ReplaceWith("AssetStorage.setLoader"),
  )
  override fun <T : Any?, P : AssetLoaderParameters<T>?> setLoader(
    type: Class<T>,
    loader: AssetLoader<T, P>,
  ) = setLoader(type, null, loader)

  @Deprecated(
    "AssetLoader instances can be mutable." +
      "AssetStorage requires functional providers of loaders rather than singular instances.",
    replaceWith = ReplaceWith("AssetStorage.setLoader"),
  )
  override fun <T : Any?, P : AssetLoaderParameters<T>?> setLoader(
    type: Class<T>,
    suffix: String?,
    loader: AssetLoader<T, P>,
  ) {
    logIncompleteSupportWarning("setLoader")
    assetStorage.setLoader(type, suffix) {
      @Suppress("UNCHECKED_CAST")
      loader as Loader<T>
    }
  }

  @Deprecated(
    "AssetStorage requires type to find exact dependencies.",
    replaceWith = ReplaceWith("AssetStorage.getDependencies"),
  )
  override fun getDependencies(fileName: String): GdxArray<String> {
    logCollisionWarning("getDependencies", fileName)
    val identifier = getIdentifier(fileName)
    return if (identifier != null) {
      GdxArray(assetStorage.getDependencies(identifier).map { it.path }.toTypedArray())
    } else {
      GdxArray.with()
    }
  }

  @Deprecated(
    "AssetStorage requires type to find reference count.",
    replaceWith = ReplaceWith("AssetStorage.getReferenceCount"),
  )
  override fun getReferenceCount(fileName: String): Int {
    logCollisionWarning("getReferenceCount", fileName)
    val identifier = getIdentifier(fileName) ?: return 0
    return assetStorage.getReferenceCount(identifier)
  }

  @Deprecated("AssetStorage does not have to be updated.", ReplaceWith("Nothing"))
  override fun update(millis: Int): Boolean = isFinished

  @Deprecated("AssetStorage does not have to be updated.", ReplaceWith("Nothing"))
  override fun update(): Boolean = isFinished

  @Deprecated("Unsupported operation.", ReplaceWith("Nothing"))
  override fun setReferenceCount(
    fileName: String,
    refCount: Int,
  ) = throw UnsupportedMethodException("setReferenceCount")

  @Deprecated("AssetStorage does not maintain an assets queue.", ReplaceWith("Nothing"))
  override fun getQueuedAssets(): Int =
    0.also {
      logNoSupportWarning("getQueuedAssets")
    }

  @Deprecated("Unsupported operation.", ReplaceWith("Nothing"))
  override fun finishLoading() {
    logNoSupportWarning("finishLoading")
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> finishLoadingAsset(assetDesc: AssetDescriptor<*>): T = get(assetDesc as AssetDescriptor<T>)

  @Deprecated("Unsupported without asset type.", ReplaceWith("finishLoadingAsset(assetDescriptor)"))
  override fun <T : Any> finishLoadingAsset(fileName: String): T {
    logCollisionWarning("finishLoadingAsset(String)", fileName)
    val identifier = getIdentifier(fileName)!!
    @Suppress("UNCHECKED_CAST")
    return get(identifier.path, identifier.type as Class<T>)
  }

  private fun getIdentifier(fileName: String): Identifier<*>? {
    val identifiers = assetStorage.getAssetIdentifiers(fileName)
    return when (identifiers.size) {
      0 -> null
      1 -> identifiers.first()
      else -> throw AssetLoadingException("Found multiple assets with path $fileName: $identifiers")
    }
  }

  private fun logWarning(
    warning: String,
    cause: Throwable? = null,
  ) {
    if (!assetStorage.silenceAssetManagerWarnings) {
      if (cause != null) {
        logger.error(warning, cause)
      } else {
        logger.error(warning)
      }
    }
  }

  private fun logIncompleteSupportWarning(method: String) {
    logWarning("AssetManagerWrapper.$method method with incomplete support was called by an asset loader.")
  }

  private fun logNoSupportWarning(method: String) {
    logWarning("Unsupported AssetManagerWrapper.$method method was called by an asset loader.")
  }

  private fun logCollisionWarning(
    method: String,
    path: String,
  ) {
    logWarning(
      "AssetManagerWrapper.$method method called by an asset loader might throw an exception " +
        "if multiple assets from the same path are loaded with different types. " +
        "Warning issued for asset loaded from path: $path",
    )
  }

  override fun toString(): String = "AssetManagerWrapper(storage=$assetStorage)"

  override fun hashCode(): Int = assetStorage.hashCode()

  override fun equals(other: Any?): Boolean = other is AssetManagerWrapper && other.assetStorage === assetStorage
}
