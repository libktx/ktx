package ktx.math

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4

/**
 * Constructs a new [Vector4] instance. An equivalent of [Vector4] constructor that supports Kotlin syntax features:
 * named parameters with default values.
 * @param x the X component. Defaults to 0f.
 * @param y the Y component. Defaults to 0f.
 * @param z the Z component. Defaults to 0f.
 * @param w the W component. Defaults to 0f.
 * @return a new [Vector4] instance storing the passed values.
 */
fun vec4(
  x: Float = 0f,
  y: Float = 0f,
  z: Float = 0f,
  w: Float = 0f,
): Vector4 = Vector4(x, y, z, w)

/**
 * Constructs a new [Vector4] instance. An equivalent of [Vector4] constructor that supports Kotlin syntax features:
 * named parameters with default values.
 * @param xy stores the X and Y components. Will be copied.
 * @param z the Z component. Defaults to 0f.
 * @param w the W component. Defaults to 0f.
 * @return a new [Vector4] instance storing the passed values.
 */
fun vec4(
  xy: Vector2,
  z: Float = 0f,
  w: Float = 0f,
): Vector4 = Vector4(xy, z, w)

/**
 * Constructs a new [Vector4] instance copying values from the passed [Vector2] instances.
 * @param xy stores the X and Y components. Will be copied.
 * @param zw its X component will be used as Z, while the Y component will be copied as W.
 * @return a new [Vector4] instance storing the passed values.
 */
fun vec4(
  xy: Vector2,
  zw: Vector2,
): Vector4 = Vector4(xy, zw.x, zw.y)

/**
 * Constructs a new [Vector4] instance copying values from the passed [Vector3] instance. An equivalent of [Vector4]
 * constructor that supports Kotlin syntax features: named parameters with default values.
 * @param xyz stores the X, Y, and Z components. Will be copied.
 * @param w the W component. Defaults to 0f.
 * @return a new [Vector4] instance storing the passed values.
 */
fun vec4(
  xyz: Vector3,
  w: Float = 0f,
): Vector4 = Vector4(xyz, w)

/**
 * Inverts stored X, Y, Z, and W values.
 * @return a new [Vector4] with negated values.
 */
operator fun Vector4.unaryMinus(): Vector4 = Vector4(-x, -y, -z, -w)

/**
 * @param vector4 values from this vector will be added to this vector.
 */
operator fun Vector4.plusAssign(vector4: Vector4) {
  add(vector4)
}

/**
 * Modifies X and Y components of this vector.
 * @param vector2 values from this vector will be added to this vector.
 */
operator fun Vector4.plusAssign(vector2: Vector2) {
  x += vector2.x
  y += vector2.y
}

/**
 * Modifies X, Y, and Z components of this vector.
 * @param vector3 values from this vector will be added to this vector.
 */
operator fun Vector4.plusAssign(vector3: Vector3) {
  x += vector3.x
  y += vector3.y
  z += vector3.z
}

/**
 * @param addend this value will be added to X, Y, Z and W components of this vector.
 */
operator fun Vector4.plusAssign(addend: Float) {
  add(addend)
}

/**
 * @param addend this value will be added to X, Y, Z and W components of this vector.
 */
operator fun Vector4.plusAssign(addend: Int) {
  add(addend.toFloat())
}

/**
 * @param vector4 values from this vector will be subtracted from this vector.
 */
operator fun Vector4.minusAssign(vector4: Vector4) {
  sub(vector4)
}

/**
 * Modifies X and Y components of this vector.
 * @param vector2 values from this vector will be subtracted from this vector.
 */
operator fun Vector4.minusAssign(vector2: Vector2) {
  x -= vector2.x
  y -= vector2.y
}

/**
 * Modifies X, Y, and Z components of this vector.
 * @param vector3 values from this vector will be subtracted from this vector.
 */
operator fun Vector4.minusAssign(vector3: Vector3) {
  x -= vector3.x
  y -= vector3.y
  z -= vector3.z
}

/**
 * @param subtrahend this value will be subtracted from X, Y, Z and W components of this vector.
 */
operator fun Vector4.minusAssign(subtrahend: Float) {
  sub(subtrahend)
}

/**
 * @param subtrahend this value will be subtracted from X, Y, Z and W components of this vector.
 */
operator fun Vector4.minusAssign(subtrahend: Int) {
  sub(subtrahend.toFloat())
}

/**
 * @param vector4 values from this vector will multiply this vector using [Vector4.scl].
 */
operator fun Vector4.timesAssign(vector4: Vector4) {
  scl(vector4)
}

/**
 * @param scalar will be used to multiply all vector values using [Vector4.scl].
 */
operator fun Vector4.timesAssign(scalar: Float) {
  scl(scalar)
}

/**
 * @param scalar will be used to multiply all vector values using [Vector4.scl].
 */
operator fun Vector4.timesAssign(scalar: Int) {
  scl(scalar.toFloat())
}

/**
 * @param vector4 these values will be used to individually divide this vector.
 */
operator fun Vector4.divAssign(vector4: Vector4) {
  x /= vector4.x
  y /= vector4.y
  z /= vector4.z
  w /= vector4.w
}

/**
 * @param scalar will be used to divide all vector values.
 */
operator fun Vector4.divAssign(scalar: Float) {
  x /= scalar
  y /= scalar
  z /= scalar
  w /= scalar
}

/**
 * @param scalar will be used to divide all vector values.
 */
operator fun Vector4.divAssign(scalar: Int) {
  x /= scalar
  y /= scalar
  z /= scalar
  w /= scalar
}

/**
 * @param vector4 values from this vector will be added to this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.plus(vector4: Vector4): Vector4 = Vector4(x + vector4.x, y + vector4.y, z + vector4.z, w + vector4.w)

/**
 * @param vector2 values from this vector will be added to this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.plus(vector2: Vector2): Vector4 = Vector4(x + vector2.x, y + vector2.y, z, w)

/**
 * @param vector3 values from this vector will be added to this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.plus(vector3: Vector3): Vector4 = Vector4(x + vector3.x, y + vector3.y, z + vector3.z, w)

/**
 * @param addend will be added to X, Y, Z and W components of this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.plus(addend: Float): Vector4 = Vector4(x + addend, y + addend, z + addend, w + addend)

/**
 * @param addend will be added to X, Y, Z and W components of this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.plus(addend: Int): Vector4 = plus(addend.toFloat())

/**
 * @param vector4 values from this vector will be subtracted from this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.minus(vector4: Vector4): Vector4 = Vector4(x - vector4.x, y - vector4.y, z - vector4.z, w - vector4.w)

/**
 * @param vector2 values from this vector will be subtracted from this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.minus(vector2: Vector2): Vector4 = Vector4(x - vector2.x, y - vector2.y, z, w)

/**
 * @param vector3 values from this vector will be subtracted from this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.minus(vector3: Vector3): Vector4 = Vector4(x - vector3.x, y - vector3.y, z - vector3.z, w)

/**
 * @param subtrahend will be subtracted from to X, Y, Z and W componentsof this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.minus(subtrahend: Float): Vector4 = Vector4(x - subtrahend, y - subtrahend, z - subtrahend, w - subtrahend)

/**
 * @param subtrahend will be subtracted from to X, Y, Z and W components of this vector.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.minus(subtrahend: Int): Vector4 = minus(subtrahend.toFloat())

/**
 * @param vector4 values from this vector will multiply this vector element-wise similarly to [Vector4.scl].
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.times(vector4: Vector4): Vector4 = Vector4(x * vector4.x, y * vector4.y, z * vector4.z, w * vector4.w)

/**
 * @param scalar will be used to multiply all vector values.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.times(scalar: Float): Vector4 = Vector4(x * scalar, y * scalar, z * scalar, w * scalar)

/**
 * @param scalar will be used to multiply all vector values.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.times(scalar: Int): Vector4 = Vector4(x * scalar, y * scalar, z * scalar, w * scalar)

/**
 * @param vector4 values from this vector will divide this vector element-wise.
 * @return a new [vector4] instance with the operation result.
 */
operator fun Vector4.div(vector4: Vector4): Vector4 = Vector4(x / vector4.x, y / vector4.y, z / vector4.z, w / vector4.w)

/**
 * @param scalar will be used to divide all vector values.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.div(scalar: Float): Vector4 = Vector4(x / scalar, y / scalar, z / scalar, w / scalar)

/**
 * @param scalar will be used to divide all vector values.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.div(scalar: Int): Vector4 = Vector4(x / scalar, y / scalar, z / scalar, w / scalar)

/**
 * Increments all vector values - adds 1 to X, Y, Z and W components.
 * To avoid creating a new vector instance, use [Vector4.plusAssign] instead.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.inc(): Vector4 = Vector4(x + 1f, y + 1f, z + 1f, w + 1f)

/**
 * Decrements all vector values - subtracts 1 from X, Y, Z and W components.
 * To avoid creating a new vector instance, use [Vector4.minusAssign] instead.
 * @return a new [Vector4] instance with the operation result.
 */
operator fun Vector4.dec(): Vector4 = Vector4(x - 1f, y - 1f, z - 1f, w - 1f)

/**
 * Operator function that allows to deconstruct this vector.
 * @return X component.
 */
operator fun Vector4.component1(): Float = x

/**
 * Operator function that allows to deconstruct this vector.
 * @return Y component.
 */
operator fun Vector4.component2(): Float = y

/**
 * Operator function that allows to deconstruct this vector.
 * @return Z component.
 */
operator fun Vector4.component3(): Float = z

/**
 * Operator function that allows to deconstruct this vector.
 * @return W component.
 */
operator fun Vector4.component4(): Float = w

/**
 * Allows to compare which [Vector4] has greater overall length.
 * @param vector4 will be compared to this vector.
 * @return 1 if this vector has greater length. 0 if vectors are equal. -1 if the other vector is greater.
 */
operator fun Vector4.compareTo(vector4: Vector4): Int = len2().compareTo(vector4.len2())

/**
 * Custom operator to apply dot multiplication.
 * @param vector4 will be used to calculate the dot product of this vector.
 * @return the dot product.
 */
infix fun Vector4.dot(vector4: Vector4): Float = dot(vector4)
