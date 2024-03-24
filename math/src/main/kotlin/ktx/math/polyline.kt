package ktx.math

import com.badlogic.gdx.math.Polyline

/**
 * Operator function that allows to deconstruct this polyline.
 * @return X component.
 */
operator fun Polyline.component1() = this.x

/**
 * Operator function that allows to deconstruct this polyline.
 * @return Y component.
 */
operator fun Polyline.component2() = this.y
