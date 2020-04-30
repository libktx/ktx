package ktx.scene2d

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.annotation.AnnotationTarget.TYPEALIAS
import kotlin.annotation.AnnotationTarget.TYPE_PARAMETER

/** Should annotate builders of Scene2D widgets. */
@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class Scene2dDsl
