package ktx.ashley.engine

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.AshleyDsl

/**
 * Creates an instance of the component [T] and adds it to this [Entity].
 *
 * @param T the [Component] type to get or create
 * @param configure inlined function with [T] as the receiver to allow additional configuration of the [Component]
 * @return the created [Component]
 */
inline fun <reified T: Component> Entity.with(configure: (@AshleyDsl T).() -> Unit): T {
  return with<T>().also(configure)
}

/**
 * Creates a pooled instance of the component [T] and adds it to this [Entity].
 *
 * @param T the [Component] type to get or create
 * @return the pooled [Component]
 */
inline fun <reified T: Component> Entity.with(): T {
  val component = T::class.java.newInstance()
  add(component)
  return component
}

/**
 * Builder function for [Engine].
 *
 * @param configure inlined function with *this* [Engine] as the receiver to allow further configuration.
 */
inline fun Engine.add(configure: (@AshleyDsl Engine).() -> Unit) {
  configure(this)
}

/**
 * Create and add an [Entity] to the [Engine].
 *
 * @param configure inlined function with the pooled [Entity] as the receiver to allow further configuration of
 *  the [Entity]. The [Entity] holds the [Entity] created and the [Engine] that created it.
 * @return the created [Entity]
 */
inline fun Engine.entity(configure: Entity.() -> Unit): Entity {
  val entity = Entity().also(configure)
  addEntity(entity)
  return entity
}

