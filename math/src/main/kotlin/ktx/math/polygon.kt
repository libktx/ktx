package ktx.math

import com.badlogic.gdx.math.Polygon

/**
 * Operator function that allows to deconstruct this polygon.
 * @return X component.
 */
operator fun Polygon.component1() = this.x

/**
 * Operator function that allows to deconstruct this polygon.
 * @return Y component.
 */
operator fun Polygon.component2() = this.y
