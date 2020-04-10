package ktx.assets.async

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.utils.GdxRuntimeException

/**
 * Thrown by [AssetStorage] and related services.
 * [message] describes the problem, while [cause] is the optional cause of the exception.
 *
 * Note that [AssetStorage] usually throws subclasses of this exception, rather than
 * instances of this exception directly. This class acts as the common superclass
 * with which all [AssetStorage]-related exceptions can be caught and handled.
 */
open class AssetStorageException(message: String, cause: Throwable? = null) : GdxRuntimeException(message, cause)

/**
 * Thrown when the asset requested by an [AssetStorage.get] variant is not available
 * in the [AssetStorage] at all or has not been loaded yet.
 *
 * This exception can also be thrown by [AssetStorage.loadSync] when mixing synchronous asset
 * loading with asynchronous loading via [AssetStorage.load] or [AssetStorage.loadAsync].
 */
class MissingAssetException(identifier: Identifier<*>) :
  AssetStorageException(message = "Asset: $identifier is not loaded.")

/**
 * Thrown by [AssetStorage.load] or [AssetStorage.get] variant when the requested asset
 * was unloaded asynchronously.
 */
class UnloadedAssetException(identifier: Identifier<*>) :
  AssetStorageException(message = "Asset: $identifier was unloaded.")

/**
 * Thrown by [AssetStorage.add] when attempting to add an asset with [Identifier]
 * that is already present in the [AssetStorage].
 */
class AlreadyLoadedAssetException(identifier: Identifier<*>) :
  AssetStorageException(message = "Asset: $identifier was already added to storage.")

/**
 * Thrown by [AssetStorage.load] when the [AssetLoader] for the requested asset type
 * and path is unavailable. See [AssetStorage.setLoader].
 */
class MissingLoaderException(descriptor: AssetDescriptor<*>) :
  AssetStorageException(
    message = "No loader available for assets of type: ${descriptor.type} " +
      "with path: ${descriptor.fileName}."
  )

/**
 * Thrown by [AssetStorage.load] or [AssetStorage.get] when the asset failed to load
 * due to invalid loader implementation. Since loaders are pre-validated during
 * registration, normally this exception is extremely rare and caused by invalid
 * [AssetStorage.setLoader] usage.
 */
class InvalidLoaderException(loader: Loader<*>) :
  AssetStorageException(
    message = "Invalid loader: $loader. It must extend either " +
      "SynchronousAssetLoader or AsynchronousAssetLoader."
  )

/**
 * Thrown by [AssetStorage.load] or [AssetStorage.get] when the asset failed to load
 * due to an unexpected loading exception, usually thrown by the associated [AssetLoader].
 */
class AssetLoadingException(descriptor: AssetDescriptor<*>, cause: Throwable)
  : AssetStorageException(message = "Unable to load asset: $descriptor", cause = cause)

/**
 * Thrown when unsupported methods are called on the [AssetManagerWrapper].
 * It is typically only caused by [AssetLoader] instances or a [AssetLoaderParameters.LoadedCallback].
 *
 * [AssetStorage] reuses official [AssetLoader] implementations to load the assets.
 * [SynchronousAssetLoader] and [AsynchronousAssetLoader] both expect an instance of [AssetManager]
 * to perform some basic operations on assets. To support the loaders API, [AssetStorage] is wrapped
 * with an [AssetManagerWrapper] which delegates supported methods to [AssetStorage] and throws
 * this exception otherwise.
 *
 * Most official loaders only call [AssetManager.get] to obtain asset dependencies, but custom loaders
 * can perform operations that are unsupported by [AssetStorage] due to its asynchronous nature
 * and storing assets mapped by path and type rather than path alone. If this exception causes the loading
 * to fail, [AssetLoader] associated with the asset has to be refactored.
 */
class UnsupportedMethodException(method: String) :
  AssetStorageException(
    message = "AssetLoader used unsupported operation of AssetManager wrapper: $method " +
      "Please refactor AssetLoader not to call this method on AssetManager."
  )

/**
 * This exception can be thrown by [AssetStorage.loadSync] if dependencies of an asset were scheduled
 * for asynchronous loading, but are not loaded yet. [AssetStorage.loadSync] will not wait for the
 * assets and instead will throw this exception.
 *
 * If [AssetStorage.loadSync] was not used, this exception is only ever thrown when trying to access
 * assets via [AssetManagerWrapper]. It is then typically only caused by [AssetLoader] instances or
 * a [AssetLoaderParameters.LoadedCallback].
 *
 * If you did not use [AssetStorage.loadSync], it usually means that [AssetLoader] attempts to access
 * an asset that either:
 * - Is already unloaded.
 * - Failed to load with exception.
 * - Was not listed by [AssetLoader.getDependencies].
 * - Has not loaded yet, which should never happen if the dependency was listed correctly.
 *
 * It can also be caused by an [AssetLoaderParameters.LoadedCallback] assigned to an asset when it tries
 * to access unloaded assets with [AssetManagerWrapper.get].
 *
 * Normally this exception is only expected in case of concurrent loading and unloading of the same asset, or when
 * mixing synchronous [AssetStorage.loadSync] with asynchronous [AssetStorage.load] or [AssetStorage.loadAsync].
 * If it occurs otherwise, the [AssetLoader] associated with the asset might incorrectly list its dependencies.
 */
class MissingDependencyException(identifier: Identifier<*>, cause: Throwable? = null) :
  AssetStorageException(
    message = "A loader has requested an instance of ${identifier.type} at path ${identifier.path}. " +
      "This asset was either not listed in dependencies, loaded with exception, is not loaded yet " +
      "or was unloaded asynchronously.",
    cause = cause
  )
