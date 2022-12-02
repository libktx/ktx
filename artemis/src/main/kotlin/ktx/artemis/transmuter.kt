package ktx.artemis

import com.artemis.Component
import com.artemis.EntityTransmuter
import com.artemis.EntityTransmuterFactory

/**
 * Adds a [Component] to an [EntityTransmuterFactory].
 *
 * @receiver the [EntityTransmuterFactory] for creating an [EntityTransmuter].
 * @param T the component to add when transmuting an entity.
 * @return the [EntityTransmuterFactory].
 */
inline fun <reified T : Component> EntityTransmuterFactory.add(): EntityTransmuterFactory = add(T::class.java)

/**
 * Removes a [Component] from an [EntityTransmuterFactory].
 *
 * @receiver the [EntityTransmuterFactory] for creating an [EntityTransmuter].
 * @param T the component to remove when transmuting an entity.
 * @return the [EntityTransmuterFactory].
 */
inline fun <reified T : Component> EntityTransmuterFactory.remove(): EntityTransmuterFactory = remove(T::class.java)
