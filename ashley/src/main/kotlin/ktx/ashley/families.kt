package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.Family.Builder
import kotlin.reflect.KClass

/**
 * @param components matches [entities][com.badlogic.ashley.core.Entity] with at least one of the specified components.
 * @return a new [Builder] for a [Family]
 */
fun one(vararg components: KClass<out Component>): Builder {
  return Family.one(*toJavaClassArray(components) )
}

/**
 * @param components matches [entities][com.badlogic.ashley.core.Entity] with all of the specified components.
 * @return a new [Builder] for a [Family]
 */
fun all(vararg components: KClass<out Component>): Builder {
  return Family.all(*toJavaClassArray(components) )
}

/**
 * @param components does not match [entities][com.badlogic.ashley.core.Entity] with any of the specified components.
 * @return a new [Builder] for a [Family]
 */
fun exclude(vararg components: KClass<out Component>): Builder {
  return Family.exclude(*toJavaClassArray(components) )
}

/**
 * @receiver the [Builder] for creating a [Family]
 * @param components matches [entities][com.badlogic.ashley.core.Entity] with at least one of the specified components.
 * @return the received [Builder] for the [Family]
 */
fun Builder.one(vararg components: KClass<out Component>): Builder {
  return one(*toJavaClassArray(components) )
}

/**
 * @receiver the [Builder] for creating a [Family]
 * @param components matches [entities][com.badlogic.ashley.core.Entity] with all of the specified components.
 * @return the received [Builder] for the [Family]
 */
fun Builder.all(vararg components: KClass<out Component>): Builder {
  return all(*toJavaClassArray(components) )
}

/**
 * @receiver the [Builder] for creating a [Family]
 * @param components does not match [entities][com.badlogic.ashley.core.Entity] with any of the specified components.
 * @return the received [Builder] for the [Family]
 */
fun Builder.exclude(vararg components: KClass<out Component>): Builder {
  return exclude(*toJavaClassArray(components) )
}

private fun toJavaClassArray(components: Array<out KClass<out Component>>): Array<Class<out Component>> {
  return components.map { c -> c.java }.toTypedArray()
}
