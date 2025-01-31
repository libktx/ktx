package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.reflect.ClassReflection
import kotlin.reflect.KProperty

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Allows accessing components assigned to entities with the property syntax.
 * Designed for non-nullable components that are available for all entities
 * without the risk of a [NullPointerException].
 *
 * @see OptionalComponentDelegate
 */
class ComponentDelegate<T : Component>(
  private val mapper: ComponentMapper<T>,
) {
  operator fun getValue(
    thisRef: Entity,
    property: KProperty<*>,
  ): T = mapper[thisRef]!!

  operator fun setValue(
    thisRef: Entity,
    property: KProperty<*>,
    value: T,
  ) {
    thisRef.add(value)
  }
}

/**
 * Returns a delegated property for the [Entity] class to access the given [Component].
 * Allows accessing and setting mandatory components assigned to entities with the property
 * syntax.
 *
 * Passing a [mapper] is optional; if no value is given, it will create a new [ComponentMapper] for
 * the chosen [Component] class.
 *
 * @see optionalPropertyFor
 * @see ComponentDelegate
 **/
inline fun <reified T : Component> propertyFor(mapper: ComponentMapper<T> = mapperFor()): ComponentDelegate<T> = ComponentDelegate(mapper)

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Designed for components that might not be defined for each entity and can be null.
 * Attempting to assign a null value to the property will remove the component from the entity.
 *
 * @see ComponentDelegate
 */
class OptionalComponentDelegate<T : Component>(
  private val mapper: ComponentMapper<T>,
  private val componentClass: Class<T>,
) {
  operator fun getValue(
    thisRef: Entity,
    property: KProperty<*>,
  ): T? = if (mapper.has(thisRef)) mapper[thisRef] else null

  operator fun setValue(
    thisRef: Entity,
    property: KProperty<*>,
    value: T?,
  ) {
    if (value != null) {
      thisRef.add(value)
    } else if (mapper.has(thisRef)) {
      thisRef.remove(componentClass)
    }
  }
}

/**
 * Returns a delegated property for the [Entity] class to access the given [Component].
 * Allows accessing and setting optional components assigned to entities with the property syntax.
 * Attempting to assign a null value to the property will remove the component it from the entity.
 *
 * Passing a [mapper] is optional; if no value is given, it will create a new [ComponentMapper] for
 * the chosen [Component] class.
 *
 * @see propertyFor
 * @see OptionalComponentDelegate
 **/
inline fun <reified T : Component> optionalPropertyFor(mapper: ComponentMapper<T> = mapperFor()): OptionalComponentDelegate<T> =
  OptionalComponentDelegate(mapper, T::class.java)

/**
 * Common interface for property delegates wrapping around flag components.
 * These properties should generally wrap [Component]s that do not have any fields, and instead
 * their presence alone is used for filtering or associated logic. An example of such component
 * could be a `Visible` class that marks entities that should be rendered.
 */
interface TagDelegate<T : Component> {
  operator fun getValue(
    thisRef: Entity,
    property: KProperty<*>,
  ): Boolean

  operator fun setValue(
    thisRef: Entity,
    property: KProperty<*>,
    value: Boolean,
  )
}

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Allows checking the presence of a [Component] of an [Entity].
 * Automatically creates instances of the [T] [Component] if its value is set to `true`.
 */
class ProviderTagDelegate<T : Component>(
  private val mapper: ComponentMapper<T>,
  private val componentClass: Class<T>,
  private val defaultValueProvider: () -> T,
) : TagDelegate<T> {
  override operator fun getValue(
    thisRef: Entity,
    property: KProperty<*>,
  ): Boolean = mapper.has(thisRef)

  override operator fun setValue(
    thisRef: Entity,
    property: KProperty<*>,
    value: Boolean,
  ) {
    if (value) {
      thisRef.add(defaultValueProvider())
    } else {
      thisRef.remove(componentClass)
    }
  }
}

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Allows checking the presence of a [Component] of an [Entity].
 * Automatically assigns a singleton instance of the [T] [Component] if its value is set to `true`.
 * This property delegate should be used only for stateless components without any mutable properties.
 */
class SingletonTagDelegate<T : Component>(
  private val mapper: ComponentMapper<T>,
  private val componentClass: Class<T>,
  private val defaultValue: T,
) : TagDelegate<T> {
  override operator fun getValue(
    thisRef: Entity,
    property: KProperty<*>,
  ): Boolean = mapper.has(thisRef)

  override operator fun setValue(
    thisRef: Entity,
    property: KProperty<*>,
    value: Boolean,
  ) {
    if (value) {
      thisRef.add(defaultValue)
    } else {
      thisRef.remove(componentClass)
    }
  }
}

/**
 * Returns a delegated property for the [Entity] class that check if the given [Component] is present
 * within the entity. Changing the boolean value of the property will either add or remove the component
 * from the entity, depending on whether the value is `true` or `false` respectively.
 *
 * Assigning `true` to this property will obtain a [Component] instance with the passed [provider].
 *
 * If [singleton] setting is set to `true`, [provider] will be called once to obtain a component instance that will
 * be reused by the property. If the setting is set to `false`, [provider] will be called each time `true` is assigned
 * to the property.
 *
 * @see ProviderTagDelegate
 * @see SingletonTagDelegate
 **/
inline fun <reified T : Component> tagFor(
  singleton: Boolean = true,
  noinline provider: () -> T,
): TagDelegate<T> {
  val mapper = mapperFor<T>()
  val componentClass = T::class.java
  return if (singleton) {
    SingletonTagDelegate(mapper, componentClass, provider())
  } else {
    ProviderTagDelegate(mapper, componentClass, provider)
  }
}

/**
 * Returns a delegated property for the [Entity] class that check if the given [Component] is present
 * within the entity. Changing the boolean value of the property will either add or remove the component
 * from the entity, depending on whether the value is `true` or `false` respectively.
 *
 * If [singleton] setting is set to `true`, an instance of the [Component] will be created once with reflection
 * and reused by the property. If the setting is set to `false`, an instance will be created each time `true` is
 * assigned to the property.
 *
 * To use this property, ensure that the component class has a no-argument constructor. Otherwise,
 * [com.badlogic.gdx.utils.reflect.ReflectionException] might be thrown.
 *
 * @see ProviderTagDelegate
 * @see SingletonTagDelegate
 **/
inline fun <reified T : Component> tagFor(singleton: Boolean = true): TagDelegate<T> {
  val mapper = mapperFor<T>()
  val componentClass = T::class.java
  if (singleton) {
    try {
      val instance = ClassReflection.newInstance(componentClass)
      return SingletonTagDelegate(mapper, componentClass, instance)
    } catch (exception: Throwable) {
      Gdx.app?.log(
        "ktx-ashley",
        "$componentClass does not have a no-argument constructor. " +
          "tagFor can only be used in read-only mode. Singleton tag delegate cannot be constructed.",
        exception,
      )
    }
  }
  return ProviderTagDelegate(mapper, componentClass) { ClassReflection.newInstance(componentClass) }
}

/**
 * Returns a delegated property for the [Entity] class that check if the given [Component] is present
 * within the entity. Changing the boolean value of the property will either add or remove the component
 * from the entity, depending on whether the value is `true` or `false` respectively.
 *
 * Assigning `true` to this property will assign the [component] instance to the [Entity].
 *
 * This property delegate should be used only for stateless components without any mutable properties.
 *
 * @see SingletonTagDelegate
 **/
inline fun <reified T : Component> tagFor(component: T): TagDelegate<T> = SingletonTagDelegate(mapperFor(), T::class.java, component)
