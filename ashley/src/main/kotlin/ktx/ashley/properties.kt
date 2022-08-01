package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import kotlin.reflect.KProperty

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Allows accessing components assigned to entities with the property syntax.
 * Designed for non-nullable components that are available for all entities
 * without the risk of a [NullPointerException].
 *
 * @see OptionalComponentDelegate
 */
class ComponentDelegate<T : Component>(private val mapper: ComponentMapper<T>) {
  operator fun getValue(thisRef: Entity, property: KProperty<*>): T =
    mapper[thisRef]!!

  operator fun setValue(thisRef: Entity, property: KProperty<*>, value: T) {
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
inline fun <reified T : Component> propertyFor(mapper: ComponentMapper<T> = mapperFor()): ComponentDelegate<T> =
  ComponentDelegate(mapper)

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Designed for components that might not be defined for each entity and can be null.
 * Attempting to assign a null value to the property will remove the component from the entity.
 *
 * @see ComponentDelegate
 */
class OptionalComponentDelegate<T : Component>(
  private val mapper: ComponentMapper<T>,
  private val componentClass: Class<T>
) {
  operator fun getValue(thisRef: Entity, property: KProperty<*>): T? =
    if (mapper.has(thisRef)) mapper[thisRef] else null

  operator fun setValue(thisRef: Entity, property: KProperty<*>, value: T?) {
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
inline fun <reified T : Component> optionalPropertyFor(
  mapper: ComponentMapper<T> = mapperFor()
): OptionalComponentDelegate<T> =
  OptionalComponentDelegate(mapper, T::class.java)

/**
 * Property delegate for an [Entity] wrapping around a [ComponentMapper].
 * Allows checking the presence of a component on an Entity.
 * Allows assigning the result of the invocation of defaultValueProvider to the entity
 * and removing a component from an Entity with a Boolean value
 * Attempting to set a value when defaultValueProvider is null will throw a [NullPointerException]
 */

class TagDelegate<T : Component>(
  val mapper: ComponentMapper<T>,
  private val componentClass: Class<T>,
  private val defaultValueProvider: (() -> T)?
) {
  operator fun getValue(thisRef: Entity, property: KProperty<*>): Boolean =
    mapper.has(thisRef)

  operator fun setValue(thisRef: Entity, property: KProperty<*>, value: Boolean) {
    if (value) {
      thisRef.add(defaultValueProvider!!.invoke())
    } else {
      thisRef.remove(componentClass)
    }
  }
}

/**
 * Returns a delegated property for the [Entity] class to check if the given [Component] is present,
 * creating or removing it.
 * Assigning false to this property will remove the component from the entity.
 * Assigning true will assign a new [Component] with the result of the defaultValueProvider to the [Entity].
 * If the defaultValueProvider is not provided the value of the default no-arg
 * constructor will be used instead. If such constructor doesn't exist the property will throw a
 * [NullPointerException] when trying to set a value.
 * Passing a [Mapper] is optional; if no value is given, it will create a new [ComponentMapper] for
 * the chosen [Component] class.
 *
 * @see TagDelegate
 **/

/*
inline fun <reified T : Component> tagFor(noinline defaultValueProvider: () -> T) =
  TagDelegate(mapperFor(), T::class.java, defaultValueProvider)
*/

inline fun <reified T : Component> tagFor(noinline defaultValueProvider: (() -> T)? = null): TagDelegate<T> {
  if (defaultValueProvider != null) {
    return TagDelegate(mapperFor(), T::class.java, defaultValueProvider)
  }
  val defaultConstructorIndex = T::class.java.constructors.indexOfFirst { it.parameters.isEmpty() }
  if (defaultConstructorIndex >= 0) {
    return TagDelegate(mapperFor(), T::class.java) {
      T::class.java.constructors[defaultConstructorIndex].newInstance() as T
    }
  }
  return TagDelegate(mapperFor(), T::class.java, null)
}
