package ktx.artemis

import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.World

/**
 * Checks if the entity has this type of [Component].
 *
 * @receiver the [ComponentMapper] for checking if the entity has the component.
 * @param entityId the ID of entity to check.
 * @return `true` if the entity has this component type, `false`` if it does not or is scheduled for removal.
 * @throws ArrayIndexOutOfBoundsException if the component was removed or never existed.
 */
operator fun ComponentMapper<out Component>.contains(entityId: Int): Boolean = has(entityId)

/**
 * Retrieves a ComponentMapper instance for a [Component].
 *
 * @receiver the [World].
 * @param T type of the [ComponentMapper] to retrieve.
 * @return the [ComponentMapper] of the given type.
 */
inline fun <reified T : Component> World.mapperFor(): ComponentMapper<T> = getMapper(T::class.java)
