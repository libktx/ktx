package ktx.math

import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * A utility factory function that allows to create [Matrix3] instances with named parameters.
 * @param m00 first row, first column.
 * @param m01 first row, second column.
 * @param m02 first row, third column.
 * @param m10 second row, first column.
 * @param m11 second row, second column.
 * @param m12 second row, third column.
 * @param m20 third row, first column.
 * @param m21 third row, second column.
 * @param m22 third row, third column.
 * @return a new instance of [Matrix3].
 */
fun mat3(
  m00: Float = 0f,
  m01: Float = 0f,
  m02: Float = 0f,
  m10: Float = 0f,
  m11: Float = 0f,
  m12: Float = 0f,
  m20: Float = 0f,
  m21: Float = 0f,
  m22: Float = 0f,
): Matrix3 {
  val matrix = Matrix3()
  val values = matrix.`val`
  values[Matrix3.M00] = m00
  values[Matrix3.M01] = m01
  values[Matrix3.M02] = m02
  values[Matrix3.M10] = m10
  values[Matrix3.M11] = m11
  values[Matrix3.M12] = m12
  values[Matrix3.M20] = m20
  values[Matrix3.M21] = m21
  values[Matrix3.M22] = m22
  return matrix
}

/**
 * Inverts currently stored values.
 * @return a new [Matrix3] with the operation result.
 */
operator fun Matrix3.unaryMinus(): Matrix3 {
  val matrix = Matrix3()
  for (index in 0..8) {
    matrix.`val`[index] = -this.`val`[index]
  }
  return matrix
}

/**
 * Inverts the current matrix.
 * @return a new [Matrix3] with the operation result.
 * @see Matrix3.inv
 */
operator fun Matrix3.not(): Matrix3 = Matrix3(this).inv()

/**
 * @param matrix3 values from this matrix will be added to this matrix.
 */
operator fun Matrix3.plusAssign(matrix3: Matrix3) {
  for (index in 0..8) {
    this.`val`[index] += matrix3.`val`[index]
  }
}

/**
 * @param matrix3 values from this matrix will be subtracted from this matrix.
 */
operator fun Matrix3.minusAssign(matrix3: Matrix3) {
  for (index in 0..8) {
    this.`val`[index] -= matrix3.`val`[index]
  }
}

/**
 * @param matrix3 values from this matrix will right-multiply this matrix. A*B results in AB.
 * @see Matrix3.mulLeft
 */
operator fun Matrix3.timesAssign(matrix3: Matrix3) {
  this.mul(matrix3)
}

/**
 * @param scalar scales the matrix in the both the x and y components.
 */
operator fun Matrix3.timesAssign(scalar: Float) {
  this.scl(scalar)
}

/**
 * @param scale scales the matrix in the both the x and y components.
 */
operator fun Matrix3.timesAssign(scale: Vector2) {
  this.scl(scale)
}

/**
 * @param scale scales the matrix in the both the x and y components.
 */
operator fun Matrix3.timesAssign(scale: Vector3) {
  this.scl(scale)
}

/**
 * @param matrix3 this vector will be left-multiplied by this matrix assuming the last component is 1f.
 */
operator fun Vector2.timesAssign(matrix3: Matrix3) {
  this.mul(matrix3)
}

/**
 * @param matrix3 values from this matrix will be added to this matrix.
 * @return this matrix for chaining.
 */
operator fun Matrix3.plus(matrix3: Matrix3): Matrix3 {
  val result = Matrix3(this)
  for (index in 0..8) {
    result.`val`[index] += matrix3.`val`[index]
  }
  return result
}

/**
 * @param matrix3 values from this matrix will be subtracted from this matrix.
 * @return this matrix for chaining.
 */
operator fun Matrix3.minus(matrix3: Matrix3): Matrix3 {
  val result = Matrix3(this)
  for (index in 0..8) {
    result.`val`[index] -= matrix3.`val`[index]
  }
  return result
}

/**
 * @param matrix3 values from this matrix will right-multiply this matrix. A*B results in AB.
 * @return this matrix for chaining.
 * @see Matrix3.mulLeft
 */
operator fun Matrix3.times(matrix3: Matrix3): Matrix3 = Matrix3(this).mul(matrix3)

/**
 * @param scalar scales the matrix in the both the x and y components.
 * @return this matrix for chaining.
 */
operator fun Matrix3.times(scalar: Float): Matrix3 = Matrix3(this).scl(scalar)

/**
 * @param vector2 this vector will be left-multiplied by this matrix, assuming the third component is 1f.
 * @return this matrix for chaining.
 */
operator fun Matrix3.times(vector2: Vector2): Vector2 = Vector2(vector2).mul(this)

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, first column.
 */
operator fun Matrix3.component1(): Float = this.`val`[Matrix3.M00]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, second column.
 */
operator fun Matrix3.component2(): Float = this.`val`[Matrix3.M01]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, third column.
 */
operator fun Matrix3.component3(): Float = this.`val`[Matrix3.M02]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, first column.
 */
operator fun Matrix3.component4(): Float = this.`val`[Matrix3.M10]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, second column.
 */
operator fun Matrix3.component5(): Float = this.`val`[Matrix3.M11]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, third column.
 */
operator fun Matrix3.component6(): Float = this.`val`[Matrix3.M12]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, first column.
 */
operator fun Matrix3.component7(): Float = this.`val`[Matrix3.M20]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, second column.
 */
operator fun Matrix3.component8(): Float = this.`val`[Matrix3.M21]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, third column.
 */
operator fun Matrix3.component9(): Float = this.`val`[Matrix3.M22]
