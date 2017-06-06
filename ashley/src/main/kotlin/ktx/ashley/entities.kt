package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

/**
 * Gets the specified [Component] from the [Entity].
 *
 * Note that this function provides `O(logn)` performance for [Component] retrieval. It is recommended that retrieving
 * a [Component] is done using [get(ComponentMapper)].
 *
 * @see ComponentMapper
 * @see [get(ComponentMapper)]
 * @param T the [Component] type to search for
 * @return the specified [Component]. Otherwise `null` if the [Entity] does not have it.
 */
inline fun <reified T: Component> Entity.get(): T? = getComponent(T::class.java)

/**
 * Gets the specified [Component] from the [Entity] with a [ComponentMapper].
 *
 * @see ComponentMapper
 * @see mapperFor
 * @param T the [Component] type to get
 * @param mapper the [ComponentMapper] to retrieve the [Component] with
 * @return the specified [Component]. Otherwise `null` if the [Entity] does not have it.
 */
operator fun <T : Component> Entity.get(mapper: ComponentMapper<T>): T? = mapper.get(this)

/**
 * Whether the [Entity] has the specified [Component].
 *
 * @see ComponentMapper
 * @param T the [Component] type to inspect
 * @param mapper the [ComponentMapper] to check the [Component] with
 * @return `true` if the [Entity] has the specified component, and `false` otherwise
 */
fun <T: Component> Entity.has(mapper: ComponentMapper<T>): Boolean = mapper.has(this)

/**
 * Whether the [Entity] does not have the specified [Component].
 *
 * @see ComponentMapper
 * @param T the [Component] type to inspect
 * @param mapper the [ComponentMapper] to check the [Component] with
 * @return `true` if the [Entity] does not have the specified component, and `false` otherwise
 */
fun <T: Component> Entity.hasNot(mapper: ComponentMapper<T>): Boolean = !has(mapper)

/**
 * Removes the specified [Component] from the [Entity].
 *
 * @param T the [Component] type to remove
 */
inline fun <reified T: Component> Entity.remove() {
  remove(T::class.java)
}
