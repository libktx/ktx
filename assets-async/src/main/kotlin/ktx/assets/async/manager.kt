package ktx.assets.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.cancelLoading
import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.assets.loaders.CubemapLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.I18NBundleLoader
import com.badlogic.gdx.assets.loaders.ModelLoader
import com.badlogic.gdx.assets.loaders.MusicLoader
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.assets.loaders.PixmapLoader
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.PolygonRegionLoader
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader
import com.badlogic.gdx.maps.tiled.TideMapLoader
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Queue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import ktx.assets.TextAssetLoader
import ktx.assets.setLoader
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader as ParticleEffect3dLoader
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * An extension of the [AssetManager] providing asynchronous file loading methods compatible
 * with the coroutine concurrency model.
 *
 * In addition to the standard asset loading methods, [AsyncAssetManager] allows scheduling
 * asset loading with [loadAsync] which returns a [Deferred] reference to the asset.
 * The reference can be [awaited][Deferred.await] to obtain a fully loaded asset instance.
 * Any errors that occur during loading can be handled with standard Kotlin try-catch clause
 * by using the [Deferred] API.
 *
 * To complete the [Deferred] references with loaded assets, [AsyncAssetManager] uses
 * [LoadedCallback] from the [AssetLoaderParameters] class. If no parameters are passed to
 * [loadAsync], they will be created with [getDefaultParameters]. If the manager is unable
 * to create a default instance of the parameters, an exception will be thrown. To prevent
 * that, register a parameter supplier for any custom loader with [setLoaderParameterSupplier],
 * or always pass non-null [AssetLoaderParameters] to [loadAsync].
 *
 * [AssetErrorListener] set with [setErrorListener] will only be invoked if an error is
 * not associated with any asset scheduled with [loadAsync]. Otherwise, the error will
 * be used to exceptionally complete the [Deferred] instance referencing a specific asset,
 * and all [Deferred] instances of assets that depend on it.
 *
 * Note that unlike [AssetStorage], [AsyncAssetManager] still has to be updated with [update]
 * until the assets are loaded.
 *
 * Other than the additions and listed changes necessary to implement the [Deferred] support,
 * [AsyncAssetManager] works like a regular [AssetManager], and it can be used as a drop-in
 * replacement. They both share the same concurrency model, which relies on thread blocking
 * with [synchronized] methods. Because of that, [AsyncAssetManager] is not expected to be
 * faster; in fact, the extra features add some overhead, decreasing the overall performance.
 *
 * The [AssetStorage] is advised over an [AsyncAssetManager], as it was designed to be entirely
 * non-blocking, and fully compatible with coroutines. It offers superior performance and better
 * coroutines support. [AsyncAssetManager] should be used instead only as an intermediate step
 * during migration from an [AssetManager] to the [AssetStorage], or if an [AssetManager] is
 * strictly required by an otherwise incompatible third-party API.
 */
class AsyncAssetManager(
  fileResolver: FileHandleResolver = InternalFileHandleResolver(),
  useDefaultLoaders: Boolean = true,
) : AssetManager(fileResolver, useDefaultLoaders) {
  private val callbacks = mutableMapOf<String, CompletableDeferred<*>>()
  private val loaderParameterSuppliers = mutableMapOf<Class<Loader<*>>, ParameterSupplier<*>>()

  init {
    // Standard loaders:
    setLoaderParameterSupplier<BitmapFontLoader> { BitmapFontLoader.BitmapFontParameter() }
    setLoaderParameterSupplier<CubemapLoader> { CubemapLoader.CubemapParameter() }
    setLoaderParameterSupplier<I18NBundleLoader> { I18NBundleLoader.I18NBundleParameter() }
    setLoaderParameterSupplier<ModelLoader<*>> { ModelLoader.ModelParameters() }
    setLoaderParameterSupplier<G3dModelLoader> { ModelLoader.ModelParameters() }
    setLoaderParameterSupplier<MusicLoader> { MusicLoader.MusicParameter() }
    setLoaderParameterSupplier<ObjLoader> { ObjLoader.ObjLoaderParameters() }
    setLoaderParameterSupplier<ParticleEffectLoader> { ParticleEffectLoader.ParticleEffectParameter() }
    setLoaderParameterSupplier<ParticleEffect3dLoader> { ParticleEffect3dLoader.ParticleEffectLoadParameter(GdxArray()) }
    setLoaderParameterSupplier<PixmapLoader> { PixmapLoader.PixmapParameter() }
    setLoaderParameterSupplier<PolygonRegionLoader> { PolygonRegionLoader.PolygonRegionParameters() }
    setLoaderParameterSupplier<ShaderProgramLoader> { ShaderProgramLoader.ShaderProgramParameter() }
    setLoaderParameterSupplier<SkinLoader> { SkinLoader.SkinParameter() }
    setLoaderParameterSupplier<SoundLoader> { SoundLoader.SoundParameter() }
    setLoaderParameterSupplier<TextureAtlasLoader> { TextureAtlasLoader.TextureAtlasParameter() }
    setLoaderParameterSupplier<TextureLoader> { TextureLoader.TextureParameter() }
    // Tiled map loaders:
    setLoaderParameterSupplier<AtlasTmxMapLoader> { AtlasTmxMapLoader.AtlasTiledMapLoaderParameters() }
    setLoaderParameterSupplier<BaseTmxMapLoader<*>> { BaseTiledMapLoader.Parameters() }
    setLoaderParameterSupplier<TideMapLoader> { TideMapLoader.Parameters() }
    setLoaderParameterSupplier<TmxMapLoader> { TmxMapLoader.Parameters() }
    // KTX loaders:
    setLoaderParameterSupplier<TextAssetLoader> { TextAssetLoader.TextAssetLoaderParameters() }

    if (useDefaultLoaders) {
      // Extra KTX loaders:
      setLoader(TextAssetLoader()) // Loads raw text files.
    }
  }

  /**
   * Schedules asynchronous loading of a selected asset with [T] class. [path] must point to an existing
   * file representing the asset, as it will be passed to the [FileHandleResolver] to obtain a file reference.
   * Loader [parameters] are optional, and can be used to customize loading of the asset. If no [parameters]
   * are given, they will be obtained with [getDefaultParameters].
   *
   * Returns a [Deferred] instance which will be completed with a reference to the loaded asset as soon
   * as it is fully loaded, or with an exception if it occurs during loading of the asset or any of its
   * dependencies. The possible exceptions include [DependencyLoadingException] if one of the asset's
   * dependencies failed to load, or any exception thrown by the asset loader.
   */
  inline fun <reified T> loadAsync(
    path: String,
    parameters: AssetLoaderParameters<T>? = null,
  ): Deferred<T> = loadAsync(AssetDescriptor(path, T::class.java, parameters))

  /**
   * Schedules asynchronous loading of a selected asset with [T] class specified by the [assetDescriptor].
   * If [AssetDescriptor.params] are not defined, they will be obtained with [getDefaultParameters].
   *
   * Returns a [Deferred] instance which will be completed with a reference to the loaded asset as soon
   * as it is fully loaded, or with an exception if it occurs during loading of the asset or any of its
   * dependencies. The possible exceptions include [DependencyLoadingException] if one of the asset's
   * dependencies failed to load, or any exception thrown by the asset loader.
   */
  fun <T> loadAsync(assetDescriptor: AssetDescriptor<T>): Deferred<T> {
    synchronized(this) {
      // isLoaded and get are both synchronized. We want to perform these in a single step.
      if (isLoaded(assetDescriptor)) {
        // Increasing reference count:
        load(assetDescriptor)
        // Returning loaded instance:
        return CompletableDeferred(this[assetDescriptor])
      } else if (assetDescriptor.fileName in callbacks) {
        // Increasing reference count:
        load(assetDescriptor)
        // Returning existing callback to the asset:
        @Suppress("UNCHECKED_CAST")
        return callbacks[assetDescriptor.fileName] as Deferred<T>
      }

      val result = CompletableDeferred<T>()

      // The Deferred can be completed via a LoadedCallback. However, AssetLoaderParameters can be null.
      @Suppress("UNCHECKED_CAST")
      val parameters: AssetLoaderParameters<T> =
        assetDescriptor.params as AssetLoaderParameters<T>?
          ?: getDefaultParameters(assetDescriptor)

      // Adding a custom LoadedCallback that completes the Deferred instance:
      val userDefinedCallback = parameters.loadedCallback
      parameters.loadedCallback =
        LoadedCallback { assetManager, fileName, type ->
          if (result.isCompleted) {
            // Executing the original user callback without resolving the deferred instance:
            userDefinedCallback?.finishedLoading(assetManager, fileName, type)
            // No error handling, since the deferred instance was already completed.
            parameters.loadedCallback = userDefinedCallback
            return@LoadedCallback
          }
          try {
            // If the user defined a custom callback, it should still be executed:
            userDefinedCallback?.finishedLoading(assetManager, fileName, type)
            // Completing the deferred:
            result.complete(assetManager.get(assetDescriptor))
            // Restoring original user callback:
            parameters.loadedCallback = userDefinedCallback
          } catch (exception: Throwable) {
            result.completeExceptionally(exception)
          } finally {
            synchronized(this) {
              callbacks.remove(fileName)
            }
          }
        }

      callbacks[assetDescriptor.fileName] = result
      // AssetDescriptor is final, and the load(AssetDescriptor) method destructs it either way.
      // In order to pass our (possibly new) parameters, `load` is called with individual properties:
      load(assetDescriptor.fileName, assetDescriptor.type, parameters)

      return result
    }
  }

  /**
   * Adds a custom [ParameterSupplier] that creates default instances of [AssetLoaderParameters]
   * for the asset loader with [L] class. The [supplier] will be invoked with an [AssetDescriptor]
   * each time default loader parameters are requested via [getDefaultParameters].
   */
  @Suppress("UNCHECKED_CAST")
  inline fun <reified L : Loader<*>> setLoaderParameterSupplier(noinline supplier: ParameterSupplier<*>) =
    setLoaderParameterSupplier(L::class.java as Class<Loader<Any>>, supplier as ParameterSupplier<Any>)

  /**
   * Adds a custom [ParameterSupplier] that creates default instances of [AssetLoaderParameters]
   * for the [T] asset loader with [loaderClass]. The [supplier] will be invoked with an [AssetDescriptor]
   * each time default loader parameters are requested via [getDefaultParameters].
   */
  fun <T> setLoaderParameterSupplier(
    loaderClass: Class<Loader<T>>,
    supplier: ParameterSupplier<T>,
  ) {
    @Suppress("UNCHECKED_CAST")
    loaderParameterSuppliers[loaderClass as Class<Loader<*>>] = supplier as ParameterSupplier<*>
  }

  /**
   * Attempts to create [AssetLoaderParameters] instance for the asset defined by [assetDescriptor].
   * The parameters will use default loading settings whenever possible, unless modified with
   * [setLoaderParameterSupplier].
   */
  fun <T> getDefaultParameters(assetDescriptor: AssetDescriptor<T>): AssetLoaderParameters<T> {
    val loader = getLoader(assetDescriptor.type, assetDescriptor.fileName)
    @Suppress("UNCHECKED_CAST")
    return loaderParameterSuppliers[loader.javaClass]?.invoke(assetDescriptor) as AssetLoaderParameters<T>?
      ?: throw GdxRuntimeException("Missing AssetLoaderParameters supplier for loader: $loader")
  }

  /** Called when [asset] fails to load with an [exception]. */
  override fun taskFailed(
    asset: AssetDescriptor<*>,
    exception: RuntimeException,
  ) {
    var handled = false
    val fileName = asset.fileName
    val callback = synchronized(this) { callbacks.remove(fileName) }
    if (callback != null) {
      Gdx.app?.error("KTX", "Unable to load asset: $asset", exception)
      handled = callback.completeExceptionally(exception)
      cancelLoading(fileName)
    }
    // Ensures that callbacks of assets depending on [asset] are also called:
    synchronized(this) {
      callbacks.entries.removeIf { (path, callback) ->
        val dependencies = gatherDependencies(path)
        if (fileName in dependencies) {
          Gdx.app?.error(
            "KTX",
            "Unable to load $path asset with $dependencies dependencies due to $asset exception",
            exception,
          )
          val error = DependencyLoadingException(path, asset.fileName, exception)
          handled = callback.completeExceptionally(error) || handled
          cancelLoading(path)
          cancelLoading(dependencies)
          true
        } else {
          false
        }
      }
    }
    // If no callback was completed, the asset was loaded non-asynchronously.
    if (!handled) {
      // Defaulting to standard error handling (rethrow as of 1.10.0):
      super.taskFailed(asset, exception)
    }
  }

  private fun gatherDependencies(path: String): Set<String> {
    val dependencies = mutableSetOf<String>()
    val assets = Queue<String>()
    assets.addFirst(path)
    while (!assets.isEmpty) {
      val asset = assets.removeFirst()
      getDependencies(asset)?.forEach { dependency ->
        dependencies.add(dependency)
        assets.addLast(dependency)
      }
    }
    return dependencies
  }
}

/** Returns a new instance of default [AssetLoaderParameters] for the given [AssetDescriptor].  */
private typealias ParameterSupplier<T> = (AssetDescriptor<T>) -> AssetLoaderParameters<T>
