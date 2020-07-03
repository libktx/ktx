package ktx.assets

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.IdentityMap
import com.badlogic.gdx.utils.ObjectSet
import java.util.*
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

/**
 * Interface describing a container for [Disposable]s that provides functions for disposing all
 * its registered items. An implementing class's Disposable declarations can be tagged with
 * [.alsoRegister()][alsoRegister] to conveniently register them as they are instantiated and assigned.
 *
 * Calling [dispose] on the registry will call [dispose] on all its registered members.
 *
 * The existing implementation [DisposableContainer] can be attached to a class as a delegate to
 * attach these convenience functions to any class.
 */
interface DisposableRegistry : Disposable {

  /**
   * A copy of the of the registered Disposables.
   */
  val registeredDisposables: Set<Disposable>

  /**
   * Registers [disposable] with this registry.
   * @return true if the item was successfully registered or false if it was already registered.
   */
  fun register(disposable: Disposable): Boolean

  /**
   * Removes [disposable] from this registry.
   * @return true if the item was successfully removed or false if it was not in the registry.
   */
  fun deregister(disposable: Disposable): Boolean

  /**
   * Removes all disposables from the registry without disposing them.
   * @return true if any items were in the registry.
   */
  fun deregisterAll(): Boolean

  /**
   * Calls [dispose][Disposable.dispose] on each registered Disposable.
   */
  override fun dispose()

  /**
   * Register this [Disposable] with the [DisposableRegistry].
   * @return this same object
   */
  fun <T : Disposable> T.alsoRegister(): T {
    register(this)
    return this
  }

  /**
   * Remove this [Disposable] from the [DisposableRegistry] if it is already registered.
   * @return this same object
   */
  fun <T : Disposable> T.alsoDeregister(): T {
    deregister(this)
    return this
  }
}

/**
 * An implementation of [DisposableRegistry] that can be subclassed or attached as a delegate to
 * extend DisposableRegistry's convenience functions to [Disposable] declarations within the class.
 */
open class DisposableContainer : DisposableRegistry {

  private val registry: MutableSet<Disposable> = Collections.newSetFromMap(IdentityHashMap())

  override val registeredDisposables: Set<Disposable> get() = registry.toSet()

  override fun register(disposable: Disposable): Boolean = registry.add(disposable)

  override fun deregister(disposable: Disposable): Boolean = registry.remove(disposable)

  override fun deregisterAll(): Boolean = registry.isNotEmpty().also { registry.clear() }

  override fun dispose() = registry.forEach(Disposable::dispose)

}
