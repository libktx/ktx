package ktx.box2d

import kotlin.annotation.AnnotationTarget.*

/**
 * Marks KTX Box2D type-safe builders.
 * @see BodyDefinition
 * @see FixtureDefinition
 */
@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class Box2DDsl
