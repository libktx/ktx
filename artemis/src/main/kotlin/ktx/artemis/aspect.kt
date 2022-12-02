package ktx.artemis

import com.artemis.Aspect
import com.artemis.Component
import kotlin.reflect.KClass

/**
 * Includes entities to an [Aspect] if they have at least one of the specified components.
 *
 * @param components – one of the components the entities must have.
 * @return an [Aspect.Builder] for the [Aspect].
 */
fun oneOf(vararg components: KClass<out Component>): Aspect.Builder = Aspect.one(*toJavaClassArray(components))

/**
 * Includes entities to an [Aspect] if they have all the specified components.
 *
 * @param components - all the components the entities must have.
 * @return an [Aspect.Builder] for the [Aspect].
 */
fun allOf(vararg components: KClass<out Component>): Aspect.Builder = Aspect.all(*toJavaClassArray(components))

/**
 * Excludes entities from an [Aspect] if they have at least one of the specified components.
 *
 * @param components - all the components the entities can't have.
 * @return an [Aspect.Builder] for the [Aspect].
 */
fun exclude(vararg components: KClass<out Component>): Aspect.Builder = Aspect.exclude(*toJavaClassArray(components))

/**
 * Includes entities to an [Aspect] if they have at least one of the specified components.
 *
 * @receiver the [Aspect.Builder] for creating an [Aspect].
 * @param components – one of the components the entities must have.
 * @return an [Aspect.Builder] for the [Aspect].
 */
fun Aspect.Builder.oneOf(vararg components: KClass<out Component>): Aspect.Builder = one(*toJavaClassArray(components))

/**
 * Includes entities to an [Aspect] if they have all the specified components.
 *
 * @receiver the [Aspect.Builder] for creating an [Aspect].
 * @param components - all the components the entities must have.
 * @return an [Aspect.Builder] for the [Aspect].
 */
fun Aspect.Builder.allOf(vararg components: KClass<out Component>): Aspect.Builder = all(*toJavaClassArray(components))

/**
 * Excludes entities from an [Aspect] if they have at least one of the specified components.
 *
 * @receiver the [Aspect.Builder] for creating an [Aspect].
 * @param components - all the components the entities can't have.
 * @return an [Aspect.Builder] for the [Aspect].
 */
fun Aspect.Builder.exclude(vararg components: KClass<out Component>): Aspect.Builder =
  exclude(*toJavaClassArray(components))
