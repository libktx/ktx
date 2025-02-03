package ktx.assets

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pool.Poolable

/**
 * Allows to use a [Pool] instance as a functional object. When invoked with no parameters, [Pool] will provide an
 * instance of the pooled object type.
 * @return an instance of class obtained from the pool.
 * @see Pool.obtain
 */
operator fun <Type> Pool<Type>.invoke(): Type = this.obtain()

/**
 * Allows to use a [Pool] instance as a functional object. When invoked with a parameter, [Pool] will treat the passed
 * parameter as an object freed to the pool.
 * @param free will be returned to the pool. Might be reset if it implements the [Poolable] interface.
 * @see Pool.free
 */
operator fun <Type> Pool<Type>.invoke(free: Type) = this.free(free)

/**
 * @param initialCapacity initial size of the backing collection.
 * @param max max amount stored in the pool. When exceeded, freed objects are no longer accepted.
 * @param discard invoked each time an object is rejected or removed from the pool. This might happen if an object is
 * freed with [Pool.free] or [Pool.freeAll] if the pool is full, or when [Pool.clear] is called. Optional, defaults
 * to resetting the objects implementing the [Poolable] interface to replicate the default behavior. If the objects are
 * [Disposable], this lambda should be used to dispose of them.
 * @param provider creates instances of the requested objects.
 * @return a new [Pool] instance, creating the object with the passed provider.
 */
inline fun <Type> pool(
  initialCapacity: Int = 16,
  max: Int = Int.MAX_VALUE,
  crossinline discard: (Type) -> Unit = {
    if (it is Poolable) {
      it.reset()
    }
  },
  crossinline provider: () -> Type,
): Pool<Type> =
  object : Pool<Type>(initialCapacity, max) {
    override fun newObject(): Type = provider()

    override fun discard(element: Type) {
      discard(element)
    }
  }
