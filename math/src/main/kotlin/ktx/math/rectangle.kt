package ktx.math

import com.badlogic.gdx.math.Rectangle

/**
 * Operator function that allows to deconstruct this rectangle.
 * @return X component.
 */
operator fun Rectangle.component1() = this.x

/**
 * Operator function that allows to deconstruct this rectangle.
 * @return Y component.
 */
operator fun Rectangle.component2() = this.y

/**
 * Operator function that allows to deconstruct this rectangle.
 * @return Width component.
 */
operator fun Rectangle.component3() = this.width

/**
 * Operator function that allows to deconstruct this rectangle.
 * @return Height component.
 */
operator fun Rectangle.component4() = this.height
