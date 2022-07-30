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
 * Allows assigning and removing a component from an Entity with a Boolean value
 */

class TagDelegate <T : Component> (val mapper: ComponentMapper<T>, private val componentClass: Class<T>) {

  operator fun getValue(thisRef: Entity, property: KProperty<*>): Boolean =
    mapper.has(thisRef)

  operator fun setValue(thisRef: Entity, property: KProperty<*>, value: Boolean) {
    if (value) {
      thisRef.add(componentClass.getConstructor().newInstance())
    } else {
      thisRef.remove(componentClass)
    }
  }
}

/**
 * Returns a delegated property for the [Entity] class to check if the given [Component] is present,
 * creating or removing it.
 * Assigning false to this property will remove the component from the entity.
 * Assigning true will assign a new [Component] of class T to the [Entity], built from the default no-arg constructor of
 * the component.
 * Passing a [Mapper] is optional; if no value is given, it will create a new [ComponentMapper] for
 * the chosen [Component] class.
 *
 *
 * @see TagDelegate
 **/

inline fun <reified T : Component> tagFor() = TagDelegate(mapperFor(), T::class.java)
