package ktx.math

import com.badlogic.gdx.math.Circle

/**
 * Operator function that allows to deconstruct this circle.
 * @return X component.
 */
operator fun Circle.component1() = this.x

/**
 * Operator function that allows to deconstruct this circle.
 * @return Y component.
 */
operator fun Circle.component2() = this.y

/**
 * Operator function that allows to deconstruct this circle.
 * @return Radius component.
 */
operator fun Circle.component3() = this.radius
