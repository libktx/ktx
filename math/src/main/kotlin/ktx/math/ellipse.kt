package ktx.math

import com.badlogic.gdx.math.Ellipse

/**
 * Operator function that allows to deconstruct this ellipse.
 * @return X component.
 */
operator fun Ellipse.component1() = this.x

/**
 * Operator function that allows to deconstruct this ellipse.
 * @return Y component.
 */
operator fun Ellipse.component2() = this.y

/**
 * Operator function that allows to deconstruct this ellipse.
 * @return Width component.
 */
operator fun Ellipse.component3() = this.width

/**
 * Operator function that allows to deconstruct this ellipse.
 * @return Height component.
 */
operator fun Ellipse.component4() = this.height
