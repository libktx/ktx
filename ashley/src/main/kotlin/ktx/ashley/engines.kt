package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity

/**
 * Get or create a [Component] by calling [Engine.createComponent].
 *
 * @param T the type of [Component] to get or create.
 * @param configure inlined function with [T] as the receiver to allow further configuration.
 * @return an [Component] instance of the selected type.
 */
inline fun <reified T : Component> Engine.create(configure: T.() -> Unit): T = create<T>().also(configure)

/**
 * Get or create a [Component] by calling [Engine.createComponent].
 *
 * @param T the type of [Component] to get or create.
 * @return an [Component] instance of the selected type.
 */
inline fun <reified T : Component> Engine.create(): T = createComponent(T::class.java)

/**
 * An [Entity] created by the provided [Engine].
 *
 * Provides methods for adding [Component]s to the [Engine] and the [Entity].
 *
 * @property engine the [Engine] providing [Components][Component].
 * @property entity the [Entity] to add [Components][Component] to.
 */
@AshleyDsl
class EngineEntity(
    val engine: Engine,
    val entity: Entity) {
  /**
   * Get or creates an instance of the component [T] and adds it to this [entity][EngineEntity].
   *
   * @param T the [Component] type to get or create.
   * @param configure inlined function with [T] as the receiver to allow additional configuration of the [Component].
   * @return the created ƒ[Component].
   * @see [create]
   */
  inline fun <reified T : Component> with(configure: (@AshleyDsl T).() -> Unit): T = with<T>().also(configure)

  /**
   * Get or creates an instance of the component [T] and adds it to this [entity][EngineEntity].
   *
   * @param T the [Component] type to get or create.
   * @return the created [Component].
   * @see [create]
   */
  inline fun <reified T : Component> with(): T {
    val component = engine.create<T>()
    entity.add(component)
    return component
  }
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
 * @param configure inlined function with the created [Entity] as the receiver to allow further configuration of
 *  the [Entity]. The [Entity] holds the [Entity] created and the [Engine] that created it.
 * @return the created [Entity].
 */
inline fun Engine.entity(configure: EngineEntity.() -> Unit): Entity {
  val entity = createEntity()
  configure(EngineEntity(this, entity))
  addEntity(entity)
  return entity
}
