package ktx.ashley.engine

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.AshleyDsl

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
 * @param configure inlined function with the pooled [Entity] as the receiver to allow further configuration of the [Entity].
 * @return the created [Entity]
 */
inline fun Engine.entity(configure: Entity.() -> Unit): Entity {
  val entity = Entity().also(configure)
  addEntity(entity)
  return entity
}

