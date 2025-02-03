package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
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
  val entity: Entity,
)

/**
 * Get or creates an instance of the component [T] and adds it to this [entity][EngineEntity].
 *
 * @param T the [Component] type to get or create.
 * @param configure inlined function with [T] as the receiver to allow additional configuration of the [Component].
 * @return the created ƒ[Component].
 * @throws [CreateComponentException] if the engine was unable to create the component
 * @see [create]
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified T : Component> EngineEntity.with(configure: (@AshleyDsl T).() -> Unit = {}): T {
  contract { callsInPlace(configure, InvocationKind.EXACTLY_ONCE) }
  val component = engine.create<T>()
  component.configure()
  entity.add(component)
  return component
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
@OptIn(ExperimentalContracts::class)
inline fun <reified T : Component> Engine.create(configure: T.() -> Unit = {}): T {
  contract { callsInPlace(configure, InvocationKind.EXACTLY_ONCE) }
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
@OptIn(ExperimentalContracts::class)
inline fun Engine.add(configure: (@AshleyDsl Engine).() -> Unit) {
  contract { callsInPlace(configure, InvocationKind.EXACTLY_ONCE) }
  configure()
}

/**
 * Create and add an [Entity] to the [Engine].
 *
 * @param configure inlined function with the created [EngineEntity] as the receiver to allow further configuration of
 * the [Entity]. The [EngineEntity] holds the created [Entity] and this [Engine].
 * @return the created [Entity].
 */
@OptIn(ExperimentalContracts::class)
inline fun Engine.entity(configure: EngineEntity.() -> Unit = {}): Entity {
  contract { callsInPlace(configure, InvocationKind.EXACTLY_ONCE) }
  val entity = createEntity()
  EngineEntity(this, entity).configure()
  addEntity(entity)
  return entity
}

/**
 * Allows to configure an existing [Entity] with this [Engine].
 *
 * @param configure inlined function with an [EngineEntity] wrapping passed [Entity] as the receiver to allow further
 * configuration of the [Entity]. The [EngineEntity] holds the passed [Entity] and this [Engine].
 * @see with
 */
@OptIn(ExperimentalContracts::class)
inline fun Engine.configureEntity(
  entity: Entity,
  configure: EngineEntity.() -> Unit,
) {
  contract { callsInPlace(configure, InvocationKind.EXACTLY_ONCE) }
  EngineEntity(this, entity).configure()
}

/**
 * @param T type of the system to retrieve.
 * @return the [EntitySystem] of the given type.
 * @throws MissingEntitySystemException if no system under [T] type is registered.
 * @see Engine.getSystem
 */
inline fun <reified T : EntitySystem> Engine.getSystem(): T = getSystem(T::class.java) ?: throw MissingEntitySystemException(T::class)

/**
 * @param type type of the system to retrieve.
 * @return the [EntitySystem] of the given type. May be null if it does not exist.
 * @see Engine.getSystem
 */
operator fun <T : EntitySystem> Engine.get(type: KClass<T>): T? = getSystem(type.java)

/**
 * Thrown when unable to create a component of given type.
 */
class CreateComponentException(
  type: KClass<*>,
  cause: Throwable? = null,
) : RuntimeException(
    "Could not create component ${type.javaObjectType} - is a visible no-arg constructor available?",
    cause,
  )

/**
 * Thrown when accessing an [EntitySystem] via [getSystem] that does not exist in the [Engine].
 */
class MissingEntitySystemException(
  type: KClass<out EntitySystem>,
) : GdxRuntimeException(
    "Could not access system of type ${type.qualifiedName} - is it added to the engine?",
  )
