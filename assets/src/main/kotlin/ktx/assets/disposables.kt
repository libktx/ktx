package ktx.assets

import com.badlogic.gdx.utils.Disposable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Allows to gracefully dispose a resource implementing [Disposable] interface. Will silently ignore nulls and exceptions
 * (except for JVM internal [Error]s, which should not be caught anyway).
 */
fun Disposable?.disposeSafely() {
  if (this != null) {
    try {
      this.dispose()
    } catch (exception: Exception) {
      exception.ignore()
    }
  }
}

/**
 * Allows to dispose a resource implementing [Disposable] interface. Will silently ignore nulls. Exceptions will be
 * caught and passed to [onError] function. This is basically an alternative to try-catch block usage.
 * @param onError will be invoked if an exception (except for JVM internal [Error]s, which should not be caught anyway)
 *    is thrown during asset disposing.
 */
@OptIn(ExperimentalContracts::class)
inline fun Disposable?.dispose(onError: (Exception) -> Unit) {
  contract { callsInPlace(onError, InvocationKind.AT_MOST_ONCE) }
  if (this != null) {
    try {
      this.dispose()
    } catch (exception: Exception) {
      onError(exception)
    }
  }
}

/**
 * Allows to dispose a collection of resources implementing [Disposable] interface. Will silently ignore stored nulls.
 * This method does not affect the [Iterable] structure in any way: no elements are removed from the collection.
 */
fun <Asset : Disposable> Iterable<Asset?>?.dispose() = this?.forEach { it?.dispose() }

/**
 * Allows to dispose a collection of resources implementing [Disposable] interface. Will silently ignore stored nulls
 * and exceptions thrown during disposing (except for JVM internal [Error]s, which should not be caught anyway). This
 * method does not affect the [Iterable] structure in any way: no elements are removed from the collection.
 */
fun <Asset : Disposable> Iterable<Asset?>?.disposeSafely() = this?.forEach { it.disposeSafely() }

/**
 * Allows to dispose a collection of resources implementing [Disposable] interface. Will silently ignore stored nulls.
 * Exceptions during asset disposing will be caught and passed to [onError] function. This method does not affect the
 * [Iterable] structure in any way: no elements are removed from the collection.
 * @param onError will be invoked each time an exception (except for JVM internal [Error]s, which should not be caught
 *    anyway) is thrown during asset disposing.
 */
inline fun <Asset : Disposable> Iterable<Asset?>?.dispose(onError: (Exception) -> Unit) = this?.forEach {
  it.dispose(onError)
}

/**
 * Allows to dispose a collection of resources implementing [Disposable] interface. Will silently ignore stored nulls.
 * This method does not affect the [Array] structure in any way: no elements are removed from the array.
 */
fun <Asset : Disposable> Array<Asset>?.dispose() = this?.forEach { it.dispose() }

/**
 * Allows to dispose a collection of resources implementing [Disposable] interface. Will silently ignore stored nulls
 * and exceptions thrown during disposing (except for JVM internal [Error]s, which should not be caught anyway). This
 * method does not affect the [Array] structure in any way: no elements are removed from the array.
 */
fun <Asset : Disposable> Array<Asset>?.disposeSafely() = this?.forEach { it.disposeSafely() }

/**
 * Allows to dispose a collection of resources implementing [Disposable] interface. Will silently ignore stored nulls.
 * Exceptions during asset disposing will be caught and passed to [onError] function. This method does not affect the
 * [Array] structure in any way: no elements are removed from the array.
 * @param onError will be invoked each time an exception (except for JVM internal [Error]s, which should not be caught
 *    anyway) is thrown during asset disposing.
 */
inline fun <Asset : Disposable> Array<Asset>?.dispose(onError: (Exception) -> Unit) = this?.forEach {
  it.dispose(onError)
}

/**
 * This method does nothing. This is a null-safe call that allows to clearly mark an exception as ignored. This approach
 * is preferred to an empty catch block, as at development time you can create custom ignore() methods for each specific
 * [Exception] types, import them and pass some debugging or logging code to each implementation. For example:
 *
 * `fun IOException.ignore() { println("This actually happens! $this") }`
 *
 * While other exceptions would still be silently ignored, by adding this method you can log all occurrences of ignored
 * IOException instances. Temporarily changing imports to a custom Throwable.ignore() implementation might also work.
 */
@Suppress("unused", "NOTHING_TO_INLINE")
inline fun Throwable?.ignore() {
}
