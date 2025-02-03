package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.reflect.ClassReflection

/**
 * Creates a [ComponentMapper] for the specified [Component] type.
 *
 * Provides `O(1)` retrieval of [Component]s for an [com.badlogic.ashley.core.Entity].
 *
 * @param T the [Component] type to create a [ComponentMapper] for.
 * @return a [ComponentMapper] matching the selected component type.
 * @see ComponentMapper
 * @see Component
 */
inline fun <reified T : Component> mapperFor(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)

/**
 * A utility abstract class for companion objects of [Component]s.
 * Defines a static [mapper] available of a chosen [Component] type.
 *
 * Extending this class outside nested objects in [Component] classes
 * will result in a [GdxRuntimeException], or alternatively a
 * [java.lang.ExceptionInInitializerError] during object initiation.
 * Using wrong generic type for [T] will result in runtime exceptions
 * due to type mismatch when using the [mapper].
 *
 * @see ComponentMapper
 */
abstract class Mapper<T : Component> {
  /** [ComponentMapper] for the [T] [Component]. */
  @Suppress("UNCHECKED_CAST")
  val mapper: ComponentMapper<T> = ComponentMapper.getFor(getComponentType()) as ComponentMapper<T>

  private fun getComponentType(): Class<T> {
    val enclosingClass =
      javaClass.enclosingClass
        ?: throw GdxRuntimeException(
          "Classes extending ktx.ashley.Mapper must be nested objects inside component classes. ${javaClass.name} " +
            "is a top-level class defined outside of the corresponding com.badlogic.ashley.core.Component.",
        )
    if (!ClassReflection.isAssignableFrom(Component::class.java, enclosingClass)) {
      throw GdxRuntimeException(
        "Classes extending ktx.ashley.Mapper must be nested objects inside component classes. ${javaClass.name} " +
          "is defined in ${enclosingClass.name}, which does not implement com.badlogic.ashley.core.Component.",
      )
    }
    @Suppress("UNCHECKED_CAST")
    return enclosingClass as Class<T>
  }
}
