package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.reflect.KClass

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
   * @throws [CreateComponentException] if the engine was unable to create the component
   * @see [create]
   */
  inline fun <reified T : Component> with(configure: (@AshleyDsl T).() -> Unit = {}): T {
    val component = engine.create<T>()
    component.configure()
    entity.add(component)
    return component
  }
}

/**
 * Get or create a [Component] by calling [Engine.createComponent].
 *
 * The [Component] must have a visible no-arg constructor.
 *
 * @param T the type of [Component] to get or create.
 * @param configure inlined function with [T] as the receiver to allow further configuration.
 * @return an [Component] instance of the selected type.
 * @throws [CreateComponentException] if the engine was unable to create the component
 */
inline fun <reified T : Component> Engine.create(configure: T.() -> Unit = {}): T {
  return try {
    createComponent(T::class.java) ?: throw NullPointerException("The component of ${T::class.java} type is null.")
  } catch (exception: Throwable) {
    throw CreateComponentException(T::class, exception)
  }.apply(configure)
}

/**
 * Builder function for [Engine].
 *
 * @param configure inlined function with *this* [Engine] as the receiver to allow further configuration.
 */
inline fun Engine.add(configure: (@AshleyDsl Engine).() -> Unit) = configure()

/**
 * Create and add an [Entity] to the [Engine].
 *
 * @param configure inlined function with the created [Entity] as the receiver to allow further configuration of
 *  the [Entity]. The [Entity] holds the [Entity] created and the [Engine] that created it.
 * @return the created [Entity].
 */
inline fun Engine.entity(configure: EngineEntity.() -> Unit = {}): Entity {
  val entity = createEntity()
  EngineEntity(this, entity).configure()
  addEntity(entity)
  return entity
}

/**
 * Quick [EntitySystem] retrieval.
 *
 * @return the [EntitySystem] of the given type. May be null if it does not exist.
 * @see Engine.getSystem
 */
inline fun <reified T : EntitySystem> Engine.getSystem(): T =
  getSystem(T::class.java) ?: throw MissingEntitySystemException(T::class)

/**
 * Quick [EntitySystem] retrieval.
 *
 * @return the [EntitySystem] of the given type. May be null if it does not exist.
 * @see Engine.getSystem
 */
operator fun <T : EntitySystem> Engine.get(type: KClass<T>): T? = getSystem(type.java)

/**
 * Thrown when unable to create a component of given type.
 */
class CreateComponentException(type: KClass<*>, cause: Throwable? = null) : RuntimeException(
  "Could not create component ${type.javaObjectType} - is a visible no-arg constructor available?", cause
)

/**
 * Thrown when accessing an [EntitySystem] via [getSystem] that does not exist in the [Engine].
 */
class MissingEntitySystemException(type: KClass<out EntitySystem>) : GdxRuntimeException(
  "Could not access system of type ${type.qualifiedName} - is it added to the engine?"
)
