package ktx.scene2d

import kotlin.annotation.AnnotationTarget.*

/** Should annotate builders of Scene2D widgets. */
@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class Scene2dDsl
