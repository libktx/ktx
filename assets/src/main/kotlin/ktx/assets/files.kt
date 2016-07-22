@file:Suppress("NOTHING_TO_INLINE")

package ktx.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

/**
 * @return [FileHandle] instance pointing to a classpath file. Its path matches this string.
 */
inline fun String?.toClasspathFile(): FileHandle = Gdx.files.classpath(this)

/**
 * @return [FileHandle] instance pointing to an internal file. Its path matches this string.
 */
inline fun String?.toInternalFile(): FileHandle = Gdx.files.internal(this)

/**
 * @return [FileHandle] instance pointing to a local file. Its path matches this string.
 */
inline fun String?.toLocalFile(): FileHandle = Gdx.files.local(this)

/**
 * @return [FileHandle] instance pointing to an external file. Its path matches this string.
 */
inline fun String?.toExternalFile(): FileHandle = Gdx.files.external(this)

/**
 * @return [FileHandle] instance pointing to an absolute file. Its path matches this string.
 */
inline fun String?.toAbsoluteFile(): FileHandle = Gdx.files.absolute(this)
