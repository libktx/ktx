package ktx.artemis

import com.artemis.Archetype
import com.artemis.ArchetypeBuilder
import com.artemis.Component
import kotlin.reflect.KClass

/**
 * Adds a [Component] to an [ArchetypeBuilder].
 *
 * @receiver the [ArchetypeBuilder] for creating an [Archetype].
 * @param T the component to add to the [ArchetypeBuilder].
 * @return the [ArchetypeBuilder].
 */
inline fun <reified T : Component> ArchetypeBuilder.add(): ArchetypeBuilder = add(T::class.java)

/**
 * Removes a [Component] from an [ArchetypeBuilder].
 *
 * @receiver the [ArchetypeBuilder] for creating an [Archetype].
 * @param T the component to remove from the [ArchetypeBuilder].
 * @return the [ArchetypeBuilder].
 */
inline fun <reified T : Component> ArchetypeBuilder.remove(): ArchetypeBuilder = remove(T::class.java)

/**
 * Adds multiple components to an [ArchetypeBuilder].
 *
 * @receiver the [ArchetypeBuilder] for creating an [Archetype].
 * @param components the components to add to the [ArchetypeBuilder].
 * @return the [ArchetypeBuilder].
 */
fun ArchetypeBuilder.add(vararg components: KClass<out Component>): ArchetypeBuilder = add(*components.asJavaClasses())

/**
 * Removes multiple components from an [ArchetypeBuilder].
 *
 * @receiver the [ArchetypeBuilder] for creating an [Archetype].
 * @param components - the components to remove from the [ArchetypeBuilder]
 * @return the [ArchetypeBuilder].
 */
fun ArchetypeBuilder.remove(vararg components: KClass<out Component>): ArchetypeBuilder = remove(*components.asJavaClasses())
