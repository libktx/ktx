package ktx.app

import com.badlogic.gdx.utils.GdxRuntimeException

/**
 * Throws a [GdxRuntimeException]. The [message] will be converted to string and passed as the exception message.
 * The [cause] is an optional exception cause. See also: [error].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun gdxError(
  message: Any? = null,
  cause: Throwable? = null,
): Nothing = throw GdxRuntimeException(message.toString(), cause)
