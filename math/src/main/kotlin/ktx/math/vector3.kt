package ktx.math

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Constructs a new [Vector3] instance. An equivalent of [Vector3] constructor that supports Kotlin syntax features:
 * named parameters with default values.
 * @param x the X component. Defaults to 0f.
 * @param y the Y component. Defaults to 0f.
 * @param z the Z component. Defaults to 0f.
 * @return a new [Vector3] instance storing the passed values.
 */
fun vec3(x: Float = 0f, y: Float = 0f, z: Float = 0f): Vector3 = Vector3(x, y, z)

/**
 * Constructs a new [Vector3] instance. An equivalent of [Vector3] constructor that supports Kotlin syntax features:
 * named parameters with default values.
 * @param vector2 stores the X and Y components. Will be copied.
 * @param z the Z component. Defaults to 0f.
 * @return a new [Vector3] instance storing the passed values.
 */
fun vec3(vector2: Vector2, z: Float = 0f): Vector3 = Vector3(vector2, z)

/**
 * Inverts stored X, Y and Z values.
 * @return a new [Vector3] with negated values.
 */
operator fun Vector3.unaryMinus(): Vector3 = Vector3(-x, -y, -z)

/**
 * @param vector3 values from this vector will be added to this vector.
 */
operator fun Vector3.plusAssign(vector3: Vector3) {
  add(vector3)
}

/**
 * Modifies x and y components of this vector.
 * @param vector2 values from this vector will be added to this vector.
 */
operator fun Vector3.plusAssign(vector2: Vector2) {
  x += vector2.x
  y += vector2.y
}

/**
 * @param addend this value will be added to x, y and z of this vector.
 */
operator fun Vector3.plusAssign(addend: Float) {
  add(addend)
}

/**
 * @param addend this value will be added to x, y and z of this vector.
 */
operator fun Vector3.plusAssign(addend: Int) {
  add(addend.toFloat())
}

/**
 * @param vector3 values from this vector will be subtracted from this vector.
 */
operator fun Vector3.minusAssign(vector3: Vector3) {
  sub(vector3)
}

/**
 * Modifies x and y components of this vector.
 * @param vector2 values from this vector will be subtracted from this vector.
 */
operator fun Vector3.minusAssign(vector2: Vector2) {
  x -= vector2.x
  y -= vector2.y
}

/**
 * @param subtrahend this value will be subtracted from x, y and z of this vector.
 */
operator fun Vector3.minusAssign(subtrahend: Float) {
  sub(subtrahend)
}

/**
 * @param subtrahend this value will be subtracted from x, y and z of this vector.
 */
operator fun Vector3.minusAssign(subtrahend: Int) {
  sub(subtrahend.toFloat())
}

/**
 * @param vector3 values from this vector will multiply this vector.
 */
operator fun Vector3.timesAssign(vector3: Vector3) {
  scl(vector3)
}

/**
 * @param scalar will be used to multiply all vector values.
 */
operator fun Vector3.timesAssign(scalar: Float) {
  scl(scalar)
}

/**
 * @param scalar will be used to multiply all vector values.
 */
operator fun Vector3.timesAssign(scalar: Int) {
  scl(scalar.toFloat())
}

/**
 * @param vector3 values from this vector will divide this vector.
 */
operator fun Vector3.divAssign(vector3: Vector3) {
  x /= vector3.x
  y /= vector3.y
  z /= vector3.z
}

/**
 * @param scalar will be used to divide all vector values.
 */
operator fun Vector3.divAssign(scalar: Float) {
  x /= scalar
  y /= scalar
  z /= scalar
}

/**
 * @param scalar will be used to divide all vector values.
 */
operator fun Vector3.divAssign(scalar: Int) {
  x /= scalar
  y /= scalar
  z /= scalar
}

/**
 * @param vector3 values from this vector will be added to this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.plus(vector3: Vector3): Vector3 = Vector3(x + vector3.x, y + vector3.y, z + vector3.z)

/**
 * Modifies x and y components of this vector.
 * @param vector2 values from this vector will be added to this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.plus(vector2: Vector2): Vector3 = Vector3(x + vector2.x, y + vector2.y, z)

/**
 * @param addend will be added to x, y and z components of this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.plus(addend: Float): Vector3 = Vector3(x + addend, y + addend, z + addend)

/**
 * @param addend will be added to x, y and z components of this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.plus(addend: Int): Vector3 = plus(addend.toFloat())

/**
 * @param vector3 values from this vector will be subtracted from this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.minus(vector3: Vector3): Vector3 = Vector3(x - vector3.x, y - vector3.y, z - vector3.z)

/**
 * Modifies x and y components of this vector.
 * @param vector2 values from this vector will be subtracted from this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.minus(vector2: Vector2): Vector3 = Vector3(x - vector2.x, y - vector2.y, z)

/**
 * @param subtrahend will be subtracted from to x, y and z components of this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.minus(subtrahend: Float): Vector3 = Vector3(x - subtrahend, y - subtrahend, z - subtrahend)

/**
 * @param subtrahend will be subtracted from to x, y and z components of this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.minus(subtrahend: Int): Vector3 = minus(subtrahend.toFloat())

/**
 * @param vector3 values from this vector will multiply this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.times(vector3: Vector3): Vector3 = Vector3(x * vector3.x, y * vector3.y, z * vector3.z)

/**
 * @param scalar will be used to multiply all vector values.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.times(scalar: Float): Vector3 = Vector3(x * scalar, y * scalar, z * scalar)

/**
 * @param scalar will be used to multiply all vector values.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.times(scalar: Int): Vector3 = Vector3(x * scalar, y * scalar, z * scalar)

/**
 * @param vector3 values from this vector will divide this vector.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.div(vector3: Vector3): Vector3 = Vector3(x / vector3.x, y / vector3.y, z / vector3.z)

/**
 * @param scalar will be used to divide all vector values.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.div(scalar: Float): Vector3 = Vector3(x / scalar, y / scalar, z / scalar)

/**
 * @param scalar will be used to divide all vector values.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.div(scalar: Int): Vector3 = Vector3(x / scalar, y / scalar, z / scalar)

/**
 * Increments all vector values - adds 1 to x, y and z.
 * To avoid creating a new vector instance, use [Vector3.plusAssign] instead.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.inc(): Vector3 = Vector3(x + 1f, y + 1f, z + 1f)

/**
 * Decrements all vector values - subtracts 1 from x, y and z.
 * To avoid creating a new vector instance, use [Vector3.minusAssign] instead.
 * @return a new [Vector3] instance with the operation result.
 */
operator fun Vector3.dec(): Vector3 = Vector3(x - 1f, y - 1f, z - 1f)

/**
 * Operator function that allows to deconstruct this vector.
 * @return X component.
 */
operator fun Vector3.component1(): Float = x

/**
 * Operator function that allows to deconstruct this vector.
 * @return Y component.
 */
operator fun Vector3.component2(): Float = y

/**
 * Operator function that allows to deconstruct this vector.
 * @return Z component.
 */
operator fun Vector3.component3(): Float = z

/**
 * Allows to compare which [Vector3] has greater overall length.
 * @param vector3 will be compared to this vector.
 * @return 1 if this vector has greater length. 0 if vectors are equal. -1 if the other vector is greater.
 */
operator fun Vector3.compareTo(vector3: Vector3): Int = len2().compareTo(vector3.len2())

/**
 * Custom operator to apply dot multiplication.
 * @param vector3 will be used to calculate the dot product of this vector.
 * @return the dot product.
 */
infix fun Vector3.dot(vector3: Vector3): Float = dot(vector3)

/**
 * Custom operator to apply cross multiplication.
 * @param vector3 will be used to calculate the cross product of this vector.
 * @return the cross product.
 */
infix fun Vector3.x(vector3: Vector3): Vector3 = Vector3(
  y * vector3.z - z * vector3.y,
  z * vector3.x - x * vector3.z,
  x * vector3.y - y * vector3.x
)
