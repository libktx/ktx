package ktx.vis

import kotlin.annotation.AnnotationTarget.*

/** Should annotate builders of VisUI widgets. */
@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class VisDsl
