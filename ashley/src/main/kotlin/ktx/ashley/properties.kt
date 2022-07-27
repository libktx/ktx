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
 * @see optionalPropertyFor
 * @see ComponentDelegate
 **/
inline fun <reified T : Component> propertyFor(): ComponentDelegate<T> =
  ComponentDelegate(mapper = mapperFor())

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
 * @see propertyFor
 * @see OptionalComponentDelegate
 **/
inline fun <reified T : Component> optionalPropertyFor(): OptionalComponentDelegate<T> =
  OptionalComponentDelegate(mapperFor(), T::class.java)
