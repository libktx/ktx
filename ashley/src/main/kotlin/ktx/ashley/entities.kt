package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

/**
 * Gets the specified [Component] from the [Entity] with a [ComponentMapper].
 *
 * @param T the [Component] type to get
 * @param mapper the [ComponentMapper] to retrieve the [Component] with
 * @return the specified [Component]. Otherwise `null` if the [Entity] does not have it.
 * @see ComponentMapper.get
 * @see mapperFor
 */
operator fun <T : Component> Entity.get(mapper: ComponentMapper<T>): T? = mapper.get(this)

/**
 * Gets the specified [Component] from the [Entity].
 *
 * Note that this function provides `O(log n)` performance for [Component] retrieval. It is recommended that retrieving
 * a [Component] is done using [get] consuming a [ComponentMapper].
 *
 * @param T the [Component] type to search for.
 * @return the specified [Component]. Otherwise `null` if the [Entity] does not have it.
 * @see ComponentMapper
 */
inline fun <reified T : Component> Entity.get(): T? = getComponent(T::class.java)

/**
 * Checks whether the [Entity] has the specified [Component].
 *
 * @param T the [Component] type to inspect.
 * @param mapper the [ComponentMapper] to check the [Component] with.
 * @return `true` if the [Entity] has the specified component, and `false` otherwise.
 * @see ComponentMapper.has
 */
fun <T : Component> Entity.has(mapper: ComponentMapper<T>): Boolean = mapper.has(this)

/**
 * Checks whether the [Entity] does not have the specified [Component].
 *
 * @param T the [Component] type to inspect.
 * @param mapper the [ComponentMapper] to check the [Component] with.
 * @return `true` if the [Entity] does not have the specified component, and `false` otherwise.
 * @see ComponentMapper.has
 */
fun <T : Component> Entity.hasNot(mapper: ComponentMapper<T>): Boolean = !has(mapper)

/**
 * Checks whether the [Entity] has the specified [Component].
 *
 * @param T the [Component] type to inspect.
 * @param mapper the [ComponentMapper] to check the [Component] with.
 * @return `true` if the [Entity] has the specified component, and `false` otherwise.
 * @see ComponentMapper.has
 */
operator fun <T : Component> Entity.contains(mapper: ComponentMapper<T>): Boolean = mapper.has(this)

/**
 * Removes the specified [Component] from the [Entity].
 *
 * @param T the [Component] type to remove.
 * @return the removed [Component] instance (if was present and matched the specified type) or null.
 * @see Entity.remove
 */
inline fun <reified T : Component> Entity.remove(): Component? = remove(T::class.java) as? T
