package ktx.artemis

import com.artemis.Component
import kotlin.reflect.KClass

internal fun toJavaClassArray(components: Array<out KClass<out Component>>): Array<Class<out Component>> =
  Array(components.size) { index -> components[index].java }
