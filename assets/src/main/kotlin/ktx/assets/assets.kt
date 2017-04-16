package ktx.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.reflect.KProperty

/**
 * Stores global [AssetManager] instance.
 * @author MJ
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
object Assets {
  /**
   * Global [AssetManager] instance, used by default by the utility loading methods. Should never be set to null and -
   * if necessary - should be replaced before invoking any asset loading methods.
   */
  var manager: AssetManager = AssetManager()
}

/**
 * Common interface for asset wrappers. Provides access to an asset instance which might or might not be loaded.
 * @author MJ
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
 * @author MJ
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun <Type> Asset<Type>.getValue(receiver: Any, property: KProperty<*>): Type = asset

/**
 * Default implementation of [Asset]. Keeps asset data in an [AssetDescriptor] and delegates asset loading to an
 * [AssetManager]. Assumes the asset was already scheduled for loading.
 * @author MJ
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
 * @author MJ
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
 * @param manager [AssetManager] instance that will be used to load the asset. Global asset manager instance is used by
 *    default.
 * @return [Asset] wrapper which allows to access the asset once it is loaded.
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
inline fun <reified Type : Any> load(path: String, parameters: AssetLoaderParameters<Type>? = null,
                                     manager: AssetManager = Assets.manager): Asset<Type> {
  val assetDescriptor = AssetDescriptor(path, Type::class.java, parameters)
  manager.load(assetDescriptor)
  return ManagedAsset(manager, assetDescriptor)
}

/**
 * @param assetDescriptor contains data necessary to load the asset.
 * @param manager [AssetManager] instance that will be used to load the asset. Global asset manager instance is used by
 *    default.
 * @return [Asset] wrapper which allows to access the asset once it is loaded.
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
fun <Type> load(assetDescriptor: AssetDescriptor<Type>, manager: AssetManager = Assets.manager): Asset<Type> {
  manager.load(assetDescriptor)
  return ManagedAsset(manager, assetDescriptor)
}

/**
 * @param path path of the asset. Its file type must match [AssetManager] file handle resolver (internal by default).
 * @param parameters optional asset loading parameters which might affect how the assets are loaded. Can be null.
 * @param manager [AssetManager] instance that will be _eventually_ used to load the asset. Global asset manager
 *    instance is used by default.
 * @return [Asset] wrapper which will eagerly load the asset on first request.
 * @see DelayedAsset
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
inline fun <reified Type : Any> loadOnDemand(path: String, parameters: AssetLoaderParameters<Type>? = null,
                                             manager: AssetManager = Assets.manager): Asset<Type> =
    DelayedAsset(manager, AssetDescriptor(path, Type::class.java, parameters))

/**
 * @param assetDescriptor contains data necessary to load the asset.
 * @param manager [AssetManager] instance that will be _eventually_ used to load the asset. Global asset manager
 *    instance is used by default.
 * @return [Asset] wrapper which will eagerly load the asset on first request.
 * @see DelayedAsset
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
fun <Type> loadOnDemand(assetDescriptor: AssetDescriptor<Type>, manager: AssetManager = Assets.manager): Asset<Type> =
    DelayedAsset(manager, assetDescriptor)

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
 * @param path path of the asset.
 * @param manager [AssetManager] instance that contains the requested asset. Note that the asset must have been already
 *    scheduled for loading and fully loaded for this method to work.
 * @return requested asset instance.
 * @throws GdxRuntimeException if asset was not loaded yet.
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
inline fun <reified Type : Any> asset(path: String, manager: AssetManager = Assets.manager): Type =
    manager[path, Type::class.java]

/**
 * @param assetDescriptor contains data necessary to access the asset.
 * @param manager [AssetManager] instance that contains the requested asset. Note that the asset must have been already
 *    scheduled for loading and fully loaded for this method to work.
 * @return requested asset instance.
 * @throws GdxRuntimeException if asset was not loaded yet.
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
fun <Type> asset(assetDescriptor: AssetDescriptor<Type>, manager: AssetManager = Assets.manager): Type =
    manager[assetDescriptor]

/**
 * @param path path of the asset.
 * @param manager [AssetManager] instance which might have been used to load the asset.
 * @return true if an asset of the selected type is currently loaded and stored in the asset manager. False otherwise.
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
inline fun <reified Type : Any> isLoaded(path: String, manager: AssetManager = Assets.manager): Boolean =
    manager.isLoaded(path, Type::class.java)

/**
 * @param path path of a loaded asset.
 * @param manager [AssetManager] instance which might have been used to load the asset. Will attempt to unload the
 *    asset instance with this manager.
 */
@Deprecated("Static access to AssetManager will be removed after the next release.")
fun unload(path: String, manager: AssetManager = Assets.manager) {
  try {
    manager.unload(path)
  } catch (exception: GdxRuntimeException) {
    // Thrown when asset is not loaded. Ignored.
  }
}
