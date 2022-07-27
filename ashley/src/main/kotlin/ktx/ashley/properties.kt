import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import kotlin.reflect.KProperty

/**
 * Property delegate for an [Entity] for the given [ComponentMapper]
 */

class ComponentDelegate<T : Component>(val mapper: ComponentMapper<T>) {

  operator fun getValue(thisRef: Entity, property: KProperty<*>): T =
    mapper[thisRef]
  operator fun setValue(thisRef: Entity, property: KProperty<*>, value: T) {
    thisRef.add(value)
  }
}

/**
 * Returns a delegated property for the [Entity] class to access the given [Component]
 **/

inline fun <reified T : Component> propertyFor() = ComponentDelegate<T>(mapperFor())

/**
Nullable version of the ComponentDelegate class, for internal use
**/

class OptionalComponentDelegate<T : Component>(val mapper: ComponentMapper<T>, private val classs: Class<T>?) {
  operator fun getValue(thisRef: Entity, property: KProperty<*>): T? =
    if (mapper.has(thisRef)) mapper[thisRef] else null

  operator fun setValue(thisRef: Entity, property: KProperty<*>, value: T?) {
    if (value != null) thisRef.add(value)
    else if (mapper.has(thisRef)) thisRef.remove(classs)
  }
}

/**
Nullable version of the propertyFor function
 **/

inline fun <reified T : Component> optionalPropertyFor() = OptionalComponentDelegate(mapperFor(), T::class.java)
