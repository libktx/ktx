package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine

/**
 * An [Entity] created by a [PooledEngine].
 *
 * Provides methods for adding [Component]s to the [PooledEngine] and the [Entity].
 *
 * @property engine the [PooledEngine] providing [Components][Component].
 * @property entity the [Entity] to add [Components][Component] to.
 */
@AshleyDsl
class PooledEntity(
    val engine: PooledEngine,
    val entity: Entity) {
  /**
   * Get or creates a pooled instance of the component [T] and adds it to this [entity][PooledEntity].
   *
   * @param T the [Component] type to get or create.
   * @param configure inlined function with [T] as the receiver to allow additional configuration of the [Component].
   * @return the pooled [Component].
   * @see [create]
   */
  inline fun <reified T : Component> with(configure: (@AshleyDsl T).() -> Unit): T = with<T>().also(configure)

  /**
   * Get or creates a pooled instance of the component [T] and adds it to this [entity][PooledEntity].
   *
   * @param T the [Component] type to get or create.
   * @return the pooled [Component].
   * @see [create]
   */
  inline fun <reified T : Component> with(): T {
    val component = engine.create<T>()
    entity.add(component)
    return component
  }
}

/**
 * Builder function for [PooledEngine].
 *
 * @param configure inlined function with *this* [PooledEngine] as the receiver to allow further configuration.
 */
inline fun PooledEngine.add(configure: (@AshleyDsl PooledEngine).() -> Unit) {
  configure(this)
}

/**
 * Create and add a pooled [Entity] to the [PooledEngine].
 *
 * @param configure inlined function with the pooled [PooledEntity] as the receiver to allow further configuration of
 *  the [Entity]. The [PooledEntity] holds the [Entity] created and the [PooledEngine] that created it.
 * @return the pooled [Entity].
 */
inline fun PooledEngine.entity(configure: PooledEntity.() -> Unit): Entity {
  val entity = createEntity()
  configure(PooledEntity(this, entity))
  addEntity(entity)
  return entity
}
