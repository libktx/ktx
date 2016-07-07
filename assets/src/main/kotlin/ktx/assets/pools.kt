package ktx.assets

import com.badlogic.gdx.utils.Pool

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
 * @param free will be returned to the pool. Might be reset if it implements the Poolable interface.
 * @see Pool.free
 */
operator fun <Type> Pool<Type>.invoke(free: Type) = this.free(free)

/**
 * @param initialCapacity initial size of the backing collection.
 * @param max max amount stored in the pool. When exceeded, freed objects are no longer accepted.
 * @param provider creates instances of the requested objects.
 * @return a new [Pool] instance, creating the object with the passed provider.
 */
inline fun <Type> pool(initialCapacity: Int = 16, max: Int = Int.MAX_VALUE, crossinline provider: () -> Type): Pool<Type> =
    object : Pool<Type>(initialCapacity, max) {
      override fun newObject(): Type = provider()
    }
