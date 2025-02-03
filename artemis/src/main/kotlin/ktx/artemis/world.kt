package ktx.artemis

import com.artemis.BaseSystem
import com.artemis.World
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.reflect.KClass

/**
 * Retrieves a system from the [World].
 *
 * @receiver the [World].
 * @param T type of the system to retrieve.
 * @return the [BaseSystem] of the given type.
 * @throws MissingBaseSystemException if no system under [T] type is registered.
 */
inline fun <reified T : BaseSystem> World.getSystem(): T = getSystem(T::class.java) ?: throw MissingBaseSystemException(T::class.java)

/**
 * Retrieves a system from the [World].
 *
 * @receiver the [World].
 * @param type type of the system to retrieve.
 * @return the [BaseSystem] of the given type. May be null if it does not exist.
 */
operator fun <T : BaseSystem> World.get(type: KClass<T>): T? = getSystem(type.java)

/**
 * Thrown when accessing an [BaseSystem] via [getSystem] that does not exist in the [World].
 */
class MissingBaseSystemException(
  type: Class<out BaseSystem>,
) : GdxRuntimeException(
    "Could not access system of type ${type.simpleName} - is it added to the world?",
  )
