package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

/**
 * Creates a [ComponentMapper] for the specified [Component] type.
 *
 * Provides `O(1)` retrieval of [Component]s for an [com.badlogic.ashley.core.Entity].
 *
 * @param T the [Component] type to create a [ComponentMapper] for.
 * @return a  [ComponentMapper]
 * @see ComponentMapper
 * @see Component
 */
inline fun <reified T : Component> mapperFor(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)
