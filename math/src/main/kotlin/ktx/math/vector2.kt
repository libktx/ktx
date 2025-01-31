package ktx.math

import com.badlogic.gdx.math.Vector2

/**
 * Constructs a new [Vector2] instance. An equivalent of [Vector2] constructor that supports Kotlin syntax features:
 * named parameters with default values.
 * @param x the X component. Defaults to 0f.
 * @param y the Y component. Defaults to 0f.
 * @return a new [Vector2] instance storing the passed values.
 */
fun vec2(
  x: Float = 0f,
  y: Float = 0f,
): Vector2 = Vector2(x, y)

/**
 * Inverts currently stored X and Y values.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.unaryMinus(): Vector2 = Vector2(-x, -y)

/**
 * @param vector2 values from vector will be added to vector.
 */
operator fun Vector2.plusAssign(vector2: Vector2) {
  add(vector2)
}

/**
 * @param addend this value will be added to both x and y of the vector.
 */
operator fun Vector2.plusAssign(addend: Float) {
  add(addend, addend)
}

/**
 * @param addend this value will be added to both x and y of the vector.
 */
operator fun Vector2.plusAssign(addend: Int) {
  plusAssign(addend.toFloat())
}

/**
 * @param vector2 values from vector will be subtracted from vector.
 */
operator fun Vector2.minusAssign(vector2: Vector2) {
  sub(vector2)
}

/**
 * @param subtrahend this value will be subtracted from both x and y of the vector.
 */
operator fun Vector2.minusAssign(subtrahend: Float) {
  sub(subtrahend, subtrahend)
}

/**
 * @param subtrahend this value will be subtracted from both x and y of the vector.
 */
operator fun Vector2.minusAssign(subtrahend: Int) {
  minusAssign(subtrahend.toFloat())
}

/**
 * @param vector2 values from vector will multiply vector.
 */
operator fun Vector2.timesAssign(vector2: Vector2) {
  scl(vector2)
}

/**
 * @param scalar will be used to multiply both vector values.
 */
operator fun Vector2.timesAssign(scalar: Float) {
  scl(scalar)
}

/**
 * @param scalar will be used to multiply both vector values.
 */
operator fun Vector2.timesAssign(scalar: Int) {
  scl(scalar.toFloat())
}

/**
 * @param vector2 values from vector will divide vector.
 */
operator fun Vector2.divAssign(vector2: Vector2) {
  x /= vector2.x
  y /= vector2.y
}

/**
 * @param scalar will be used to divide both vector values.
 */
operator fun Vector2.divAssign(scalar: Float) {
  x /= scalar
  y /= scalar
}

/**
 * @param scalar will be used to divide both vector values.
 */
operator fun Vector2.divAssign(scalar: Int) {
  x /= scalar
  y /= scalar
}

/**
 * @param vector2 values from vector will be added to vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.plus(vector2: Vector2): Vector2 = Vector2(x + vector2.x, y + vector2.y)

/**
 * @param addend this value will be added to both x and y of the vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.plus(addend: Float): Vector2 = Vector2(x + addend, y + addend)

/**
 * @param addend this value will be added to both x and y of the vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.plus(addend: Int): Vector2 = plus(addend.toFloat())

/**
 * @param vector2 values from vector will be subtracted from vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.minus(vector2: Vector2): Vector2 = Vector2(x - vector2.x, y - vector2.y)

/**
 * @param subtrahend this value will be subtracted from both x and y of the vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.minus(subtrahend: Float): Vector2 = Vector2(x - subtrahend, y - subtrahend)

/**
 * @param subtrahend this value will be subtracted from both x and y of the vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.minus(subtrahend: Int): Vector2 = minus(subtrahend.toFloat())

/**
 * @param vector2 values from vector will multiply vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.times(vector2: Vector2): Vector2 = Vector2(x * vector2.x, y * vector2.y)

/**
 * @param scalar will be used to multiply both vector values.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.times(scalar: Float): Vector2 = Vector2(x * scalar, y * scalar)

/**
 * @param scalar will be used to multiply both vector values.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.times(scalar: Int): Vector2 = Vector2(x * scalar, y * scalar)

/**
 * @param vector2 values from vector will divide vector.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.div(vector2: Vector2): Vector2 = Vector2(x / vector2.x, y / vector2.y)

/**
 * @param scalar will be used to divide both vector values.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.div(scalar: Float): Vector2 = Vector2(x / scalar, y / scalar)

/**
 * @param scalar will be used to divide both vector values.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.div(scalar: Int): Vector2 = Vector2(x / scalar, y / scalar)

/**
 * Increments both vector values - adds 1 to x and y.
 * To avoid creating a new vector instance, use [Vector2.plusAssign] instead.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.inc(): Vector2 = Vector2(x + 1f, y + 1f)

/**
 * Decrements both vector values - subtracts 1 from x and y.
 * To avoid creating a new vector instance, use [Vector2.minusAssign] instead.
 * @return a new [Vector2] instance with the operation result.
 */
operator fun Vector2.dec(): Vector2 = Vector2(x - 1f, y - 1f)

/**
 * Operator function that allows to deconstruct this vector.
 * @return X component.
 */
operator fun Vector2.component1(): Float = x

/**
 * Operator function that allows to deconstruct this vector.
 * @return Y component.
 */
operator fun Vector2.component2(): Float = y

/**
 * Allows to compare which [Vector2] has greater overall length.
 * @param vector2 will be compared to this vector.
 * @return 1 if this vector has greater length. 0 if vectors are equal. -1 if the other vector is greater.
 */
operator fun Vector2.compareTo(vector2: Vector2): Int = len2().compareTo(vector2.len2())

/**
 * Custom operator to apply dot multiplication.
 * @param vector2 will be used to calculate the dot product of this vector.
 * @return the dot product.
 */
infix fun Vector2.dot(vector2: Vector2): Float = dot(vector2)

/**
 * Custom operator to apply cross multiplication.
 * @param vector2 will be used to calculate the cross product of this vector.
 * @return the cross product.
 */
infix fun Vector2.x(vector2: Vector2): Float = crs(vector2)
