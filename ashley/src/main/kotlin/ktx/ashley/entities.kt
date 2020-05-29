package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

/**
 * Adds a constructed [Component] to this [Entity]. If a [Component] of the same type exists, it will be replaced.
 * @param component will be added to this [Entity].
 */
operator fun Entity.plusAssign(component: Component) {
  add(component)
}

/**
 * Adds a [Component] to this [Entity]. If a [Component] of the same type already exists, it will be replaced.
 *
 * @param T type of component to add. Must have a no-argument constructor.
 * @return a [Component] instance of the chosen type.
 * @throws [CreateComponentException] if the engine was unable to create the component.
 * @see Entity.add
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified T : Component> Entity.addComponent(engine: Engine, configure: (@AshleyDsl T).() -> Unit = {}): T {
  contract { callsInPlace(configure, InvocationKind.EXACTLY_ONCE) }
  val component = engine.create<T>()
  component.configure()
  add(component)
  return component
}
