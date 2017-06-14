package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine

/**
 * Get or create a [Component] by calling [Engine.createComponent].
 *
 * @param T the type of [Component] to get or create.
 * @param configure inlined function with [T] as the receiver to allow further configuration.
 * @return an [Component] instance of the selected type.
 */
inline fun <reified T : Component> Engine.create(configure: T.() -> Unit): T = create<T>().also(configure)

/**
 * Get or create a [Component] by calling [Engine.createComponent].
 *
 * @param T the type of [Component] to get or create.
 * @return an [Component] instance of the selected type.
 */
inline fun <reified T : Component> Engine.create(): T = createComponent(T::class.java)