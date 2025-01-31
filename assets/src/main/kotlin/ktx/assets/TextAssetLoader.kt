package ktx.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.assets.TextAssetLoader.TextAssetLoaderParameters

/**
 * Allows reading text files with an [AssetManager]. Must be registered via [setLoader].
 *
 * Note that [loadAsync] _must_ be called before [loadSync], as usual in case of [AsynchronousAssetLoader]
 * implementations. Similarly to standard libGDX loaders, this loader is not considered thread-safe and assumes that
 * a single file is loaded at a time.
 *
 * @param fileResolver not used, required by the superclass.
 * @param charset name of the charset used to read text. Can be overridden with
 *    [TextAssetLoader.TextAssetLoaderParameters]. Should match text files encoding. Defaults to UTF-8.
 */
class TextAssetLoader(
  fileResolver: FileHandleResolver = InternalFileHandleResolver(),
  private val charset: String = "UTF-8",
) : AsynchronousAssetLoader<String, TextAssetLoaderParameters>(fileResolver) {
  @Volatile
  var fileContent: String? = null

  override fun loadAsync(
    assetManager: AssetManager?,
    fileName: String?,
    file: FileHandle,
    parameter: TextAssetLoaderParameters?,
  ) {
    fileContent = file.readString(parameter?.charset ?: charset)
  }

  override fun loadSync(
    assetManager: AssetManager?,
    fileName: String?,
    file: FileHandle,
    parameter: TextAssetLoaderParameters?,
  ): String =
    try {
      fileContent ?: throw GdxRuntimeException("File $fileName was not loaded asynchronously. Call #loadAsync first.")
    } finally {
      fileContent = null
    }

  override fun getDependencies(
    fileName: String?,
    file: FileHandle?,
    parameter: TextAssetLoaderParameters?,
  ): Array<AssetDescriptor<Any>>? = null

  /**
   * Optional parameters used to load text files.
   * @param charset name of the charset used to read text. Should match text file encoding. Defaults to UTF-8.
   */
  class TextAssetLoaderParameters(
    var charset: String = "UTF-8",
  ) : AssetLoaderParameters<String>()
}
