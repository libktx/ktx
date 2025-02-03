package ktx.artemis

import kotlin.reflect.KClass

internal fun <T : Any> Array<out KClass<out T>>.asJavaClasses(): Array<Class<out T>> = Array(size) { index -> this[index].java }
