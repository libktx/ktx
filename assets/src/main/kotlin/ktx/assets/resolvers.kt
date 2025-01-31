package ktx.assets

import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.Files.FileType.Absolute
import com.badlogic.gdx.Files.FileType.Classpath
import com.badlogic.gdx.Files.FileType.External
import com.badlogic.gdx.Files.FileType.Internal
import com.badlogic.gdx.Files.FileType.Local
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.PrefixFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution

/**
 * Creates a [FileHandleResolver].
 * @return a new [FileHandleResolver] instance providing files matching this type.
 */
fun FileType.getResolver(): FileHandleResolver =
  when (this) {
    Classpath -> ClasspathFileHandleResolver()
    Internal -> InternalFileHandleResolver()
    Local -> LocalFileHandleResolver()
    External -> ExternalFileHandleResolver()
    Absolute -> AbsoluteFileHandleResolver()
  }

/**
 * Decorates this [FileHandleResolver] with a [PrefixFileHandleResolver].
 * @param prefix will be added to file paths before passing them to the original resolver.
 * @return a new [PrefixFileHandleResolver] decorating this resolver.
 * @see PrefixFileHandleResolver
 */
fun FileHandleResolver.withPrefix(prefix: String) = PrefixFileHandleResolver(this, prefix)

/**
 * Decorates this [FileHandleResolver] with a [ResolutionFileResolver].
 * @param resolutions each [Resolution] points to a folder with assets specific to the chosen bounds. During asset
 *    loading, screen width and height are chosen to select the closest matching [Resolution], which will be used to
 *    select the assets directory.
 * @return a new [ResolutionFileResolver] decorating this resolver.
 * @see ResolutionFileResolver
 * @see resolution
 */
fun FileHandleResolver.forResolutions(vararg resolutions: Resolution) = ResolutionFileResolver(this, *resolutions)

/**
 * Factory method for [ResolutionFileResolver.Resolution] that allows to used named parameters.
 * @param width portrait width of the resolution.
 * @param height portrait height of the resolution.
 * @param folder name of the folder with assets for the given resolution. Defaults to "[width]x[height]".
 * @return a new [Resolution] instance with the given size.
 * @see Resolution
 * @see ResolutionFileResolver
 */
fun resolution(
  width: Int,
  height: Int,
  folder: String = "${width}x$height",
) = Resolution(width, height, folder)
