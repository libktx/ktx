package ktx.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.reflect.KProperty

/**
 * Common interface for asset wrappers. Provides access to an asset instance which might or might not be loaded.
 * @see ManagedAsset
 * @see DelayedAsset
 */
interface Asset<out Type> {
  /**
   * Instance of the wrapped asset. Might not be loaded yet. Calling getter of this property without making sure that
   * the asset is loaded might cause an exception.
   * @see isLoaded
   */
  val asset: Type

  /**
   * @return true if the asset is already loaded. If this asset is not loaded on demand and this method returns false,
   *    trying to obtain the [asset] instance might cause an exception.
   * @see finishLoading
   */
  fun isLoaded(): Boolean

  /**
   * Forces eager loading of the asset. Note that due to [AssetManager] implementation, eager asset loading causes
   * all scheduled assets to be loaded synchronously until the desired asset is reported as loaded. It is usually
   * sensible to use a separate [AssetManager] for eagerly loaded assets or load such assets after all regular assets
   * are already loaded.
   * @see DelayedAsset
   */
  fun finishLoading()

  /**
   * Explicitly marks that the asset should be loaded. If this asset is managed by an [AssetManager] instance, will
   * schedule asset loading.
   */
  fun load()

  /**
   * Attempts to unload the asset. Might throw an exception if the asset is not loaded at all due to [AssetManager]
   * implementation.
   */
  fun unload()
}

/**
 * Extension function that allows [Asset] instances to be delegates using the "by" keyword.
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun <Type> Asset<Type>.getValue(receiver: Any?, property: KProperty<*>): Type = asset

/**
 * Default implementation of [Asset]. Keeps asset data in an [AssetDescriptor] and delegates asset loading to an
 * [AssetManager]. Assumes the asset was already scheduled for loading.
 */
class ManagedAsset<Type>(val manager: AssetManager, val assetDescriptor: AssetDescriptor<Type>) : Asset<Type> {
  override val asset: Type
    get() = manager[assetDescriptor]

  override fun isLoaded(): Boolean = manager.isLoaded(assetDescriptor.fileName, assetDescriptor.type)
  override fun load() = manager.load(assetDescriptor)
  override fun unload() = manager.unload(assetDescriptor.fileName)
  override fun finishLoading() {
    if (!isLoaded()) manager.finishLoadingAsset(assetDescriptor.fileName)
  }
}

/**
 * Represents an [Asset] wrapper of resource loaded at demand. The first asset getter call causes the asset to be
 * scheduled for loading and loaded eagerly - the [AssetManager] is updated until the asset is reported as loaded.
 * Note that eager asset loading might cause other assets to be loaded synchronously rather than asynchronously,
 * so it is advised to load eager assets with another [AssetManager] instance or use them after all regular assets are
 * already loaded.
 */
class DelayedAsset<Type>(val manager: AssetManager, val assetDescriptor: AssetDescriptor<Type>) : Asset<Type> {
  override val asset: Type
    get() {
      if (!isLoaded()) finishLoading()
      return manager[assetDescriptor]
    }

  override fun isLoaded(): Boolean = manager.isLoaded(assetDescriptor.fileName, assetDescriptor.type)
  override fun load() = manager.load(assetDescriptor)
  override fun unload() = manager.unload(assetDescriptor.fileName)
  override fun finishLoading() {
    if (!isLoaded()) {
      manager.load(assetDescriptor)
      manager.finishLoadingAsset(assetDescriptor.fileName)
    }
  }
}

/**
 * @param path path of the asset. Its file type must match [AssetManager] file handle resolver (internal by default).
 * @param parameters optional asset loading parameters which might affect how the assets are loaded. Can be null.
 * @return [Asset] wrapper which allows to access the asset once it is loaded.
 */
inline fun <reified Type : Any> AssetManager.load(path: String, parameters: AssetLoaderParameters<Type>? = null): Asset<Type> {
  val assetDescriptor = AssetDescriptor(path, Type::class.java, parameters)
  this.load(assetDescriptor)
  return ManagedAsset(this, assetDescriptor)
}

/**
 * @param assetDescriptor contains data necessary to load the asset.
 * @return [Asset] wrapper which allows to access the asset once it is loaded.
 */
fun <Type> AssetManager.loadAsset(assetDescriptor: AssetDescriptor<Type>): Asset<Type> {
  this.load(assetDescriptor)
  return ManagedAsset(this, assetDescriptor)
}

/**
 * @param path path of the asset. Its file type must match [AssetManager] file handle resolver (internal by default).
 * @param parameters optional asset loading parameters which might affect how the assets are loaded. Can be null.
 * @return [Asset] wrapper which will eagerly load the asset on first request.
 * @see DelayedAsset
 */
inline fun <reified Type : Any> AssetManager.loadOnDemand(path: String,
                                                          parameters: AssetLoaderParameters<Type>? = null): Asset<Type> =
    DelayedAsset(this, AssetDescriptor(path, Type::class.java, parameters))

/**
 * @param assetDescriptor contains data necessary to load the asset.
 * @return [Asset] wrapper which will eagerly load the asset on first request.
 * @see DelayedAsset
 */
fun <Type> AssetManager.loadOnDemand(assetDescriptor: AssetDescriptor<Type>): Asset<Type> =
    DelayedAsset(this, assetDescriptor)

/**
 * Allows to quickly prepare a typed [AssetDescriptor] instance with more Kotlin-friendly syntax.
 * @param path path to the asset file. Is likely to be resolved by an [AssetManager].
 * @param parameters optional loading parameters that might affect how the asset is loaded.
 * @return typed [AssetDescriptor] instance storing the passed data.
 */
inline fun <reified Type : Any> assetDescriptor(path: String, parameters: AssetLoaderParameters<Type>? = null):
    AssetDescriptor<Type> = AssetDescriptor(path, Type::class.java, parameters)

/**
 * Allows to quickly prepare a typed [AssetDescriptor] instance with more Kotlin-friendly syntax.
 * @param file file representation of the asset.
 * @param parameters optional loading parameters that might affect how the asset is loaded.
 * @return typed [AssetDescriptor] instance storing the passed data.
 */
inline fun <reified Type : Any> assetDescriptor(file: FileHandle, parameters: AssetLoaderParameters<Type>? = null):
    AssetDescriptor<Type> = AssetDescriptor(file, Type::class.java, parameters)

/**
 * @param path path of the asset. Note that the asset must have been already scheduled for loading and fully loaded for
 *    this method to work.
 * @return requested asset instance.
 * @throws GdxRuntimeException if asset was not loaded yet.
 */
inline fun <reified Type : Any> AssetManager.getAsset(path: String): Type
    = this[path, Type::class.java]

/**
 * @param path path of a loaded asset. Asset associated with this path will be unloaded. Any thrown exceptions will be
 *    ignored.
 */
fun AssetManager.unloadSafely(path: String) {
  try {
    unload(path)
  } catch (exception: Exception) {
    exception.ignore()
  }
}

/**
 * @param path path of a loaded asset. Asset associated with this path will be unloaded.
 * @param onError any thrown exceptions will be passed to this handler.
 */
inline fun AssetManager.unload(path: String, onError: (Exception) -> Unit) {
  try {
    unload(path)
  } catch (exception: Exception) {
    onError(exception)
  }
}

/**
 * Utility for accessing [AssetLoader] instances stored by the [AssetManager].
 * @param Type type of loaded assets.
 * @param suffix suffix required by the loader. Pass null to look for the default loader. Optional, null by default.
 * @return [AssetLoader] associated with the given type or null if none are registered.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified Type : Any> AssetManager.getLoader(suffix: String? = null): AssetLoader<Type, *>? =
    getLoader(Type::class.java, suffix) as AssetLoader<Type, *>?

/**
 * @param Type type of the handled asset.
 * @param Parameters optional parameters used to load the assets.
 * @param assetLoader will be used to load files associated with the selected type.
 * @param suffix if set, file names end with this suffix for the passed loader to be used. Pass `null` to choose the
 *    default loader. Optional, null by default.
 * @see AssetManager.setLoader
 * @see AssetLoader
 */
inline fun <reified Type : Any, Parameters : AssetLoaderParameters<Type>> AssetManager.setLoader(
    assetLoader: AssetLoader<Type, Parameters>,
    suffix: String? = null) {
  setLoader(Type::class.java, suffix, assetLoader)
}
