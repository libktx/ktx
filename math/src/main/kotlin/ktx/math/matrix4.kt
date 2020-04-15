package ktx.math

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * A utility factory function that allows to create [Matrix4] instances with named parameters.
 * @param m00 first row, first column.
 * @param m01 first row, second column.
 * @param m02 first row, third column.
 * @param m03 first row, forth column.
 * @param m10 second row, first column.
 * @param m11 second row, second column.
 * @param m12 second row, third column.
 * @param m13 second row, forth column.
 * @param m20 third row, first column.
 * @param m21 third row, second column.
 * @param m22 third row, third column.
 * @param m23 third row, forth column.
 * @param m30 forth row, first column.
 * @param m31 forth row, second column.
 * @param m32 forth row, third column.
 * @param m33 forth row, forth column.
 * @return a new instance of [Matrix4].
 */
fun mat4(m00: Float = 0f, m01: Float = 0f, m02: Float = 0f, m03: Float = 0f,
         m10: Float = 0f, m11: Float = 0f, m12: Float = 0f, m13: Float = 0f,
         m20: Float = 0f, m21: Float = 0f, m22: Float = 0f, m23: Float = 0f,
         m30: Float = 0f, m31: Float = 0f, m32: Float = 0f, m33: Float = 0f): Matrix4 {
  val matrix = Matrix4()
  val values = matrix.`val`
  values[Matrix4.M00] = m00
  values[Matrix4.M01] = m01
  values[Matrix4.M02] = m02
  values[Matrix4.M03] = m03
  values[Matrix4.M10] = m10
  values[Matrix4.M11] = m11
  values[Matrix4.M12] = m12
  values[Matrix4.M13] = m13
  values[Matrix4.M20] = m20
  values[Matrix4.M21] = m21
  values[Matrix4.M22] = m22
  values[Matrix4.M23] = m23
  values[Matrix4.M30] = m30
  values[Matrix4.M31] = m31
  values[Matrix4.M32] = m32
  values[Matrix4.M33] = m33
  return matrix
}

/**
 * Inverts currently stored values.
 * @return a new [Matrix4] with the operation result.
 */
operator fun Matrix4.unaryMinus(): Matrix4 {
  val matrix = Matrix4()
  for (index in 0..15) {
    matrix.`val`[index] = -this.`val`[index]
  }
  return matrix
}

/**
 * Inverts the current matrix.
 * @return a new [Matrix4] with the operation result.
 * @see Matrix4.inv
 */
operator fun Matrix4.not(): Matrix4 = Matrix4(this).inv()

/**
 * @param matrix4 values from this matrix will be added to this matrix.
 */
operator fun Matrix4.plusAssign(matrix4: Matrix4) {
  for (index in 0..15) {
    this.`val`[index] += matrix4.`val`[index]
  }
}

/**
 * @param matrix4 values from this matrix will be subtracted from this matrix.
 */
operator fun Matrix4.minusAssign(matrix4: Matrix4) {
  for (index in 0..15) {
    this.`val`[index] -= matrix4.`val`[index]
  }
}

/**
 * @param matrix4 values from this matrix will right-multiply this matrix. A*B results in AB.
 * @see Matrix4.mulLeft
 */
operator fun Matrix4.timesAssign(matrix4: Matrix4) {
  this.mul(matrix4)
}

/**
 * @param scalar scales the matrix in the x, y and z components.
 */
operator fun Matrix4.timesAssign(scalar: Float) {
  this.scl(scalar)
}

/**
 * @param scale scales the matrix in the both the x and y components.
 */
operator fun Matrix4.timesAssign(scale: Vector2) {
  this.scl(vec3(scale, 1f))
}

/**
 * @param scale scales the matrix in the x, y and z components.
 */
operator fun Matrix4.timesAssign(scale: Vector3) {
  this.scl(scale)
}

/**
 * @param matrix4 this vector will be left-multiplied by this matrix, assuming the forth component is 1f.
 */
operator fun Vector3.timesAssign(matrix4: Matrix4) {
  this.mul(matrix4)
}

/**
 * @param matrix4 values from this matrix will be added to this matrix.
 * @return this matrix for chaining.
 */
operator fun Matrix4.plus(matrix4: Matrix4): Matrix4 {
  val result = Matrix4(this)
  for (index in 0..15) {
    result.`val`[index] += matrix4.`val`[index]
  }
  return result
}

/**
 * @param matrix4 values from this matrix will be subtracted from this matrix.
 * @return this matrix for chaining.
 */
operator fun Matrix4.minus(matrix4: Matrix4): Matrix4 {
  val result = Matrix4(this)
  for (index in 0..15) {
    result.`val`[index] -= matrix4.`val`[index]
  }
  return result
}

/**
 * @param matrix4 values from this matrix will right-multiply this matrix. A*B results in AB.
 * @return this matrix for chaining.
 * @see Matrix4.mulLeft
 */
operator fun Matrix4.times(matrix4: Matrix4): Matrix4 = Matrix4(this).mul(matrix4)

/**
 * @param scalar scales the matrix in the x, y and z components.
 * @return this matrix for chaining.
 */
operator fun Matrix4.times(scalar: Float): Matrix4 = Matrix4(this).scl(scalar)

/**
 * @param vector3 this vector will be left-multiplied by this matrix, assuming the forth component is 1f.
 * @return this matrix for chaining.
 */
operator fun Matrix4.times(vector3: Vector3): Vector3 = Vector3(vector3).mul(this)

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, first column.
 */
operator fun Matrix4.component1(): Float = this.`val`[Matrix4.M00]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, second column.
 */
operator fun Matrix4.component2(): Float = this.`val`[Matrix4.M01]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, third column.
 */
operator fun Matrix4.component3(): Float = this.`val`[Matrix4.M02]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return first row, forth column.
 */
operator fun Matrix4.component4(): Float = this.`val`[Matrix4.M03]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, first column.
 */
operator fun Matrix4.component5(): Float = this.`val`[Matrix4.M10]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, second column.
 */
operator fun Matrix4.component6(): Float = this.`val`[Matrix4.M11]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, third column.
 */
operator fun Matrix4.component7(): Float = this.`val`[Matrix4.M12]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return second row, forth column.
 */
operator fun Matrix4.component8(): Float = this.`val`[Matrix4.M13]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, first column.
 */
operator fun Matrix4.component9(): Float = this.`val`[Matrix4.M20]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, second column.
 */
operator fun Matrix4.component10(): Float = this.`val`[Matrix4.M21]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, third column.
 */
operator fun Matrix4.component11(): Float = this.`val`[Matrix4.M22]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return third row, forth column.
 */
operator fun Matrix4.component12(): Float = this.`val`[Matrix4.M23]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return forth row, first column.
 */
operator fun Matrix4.component13(): Float = this.`val`[Matrix4.M30]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return forth row, second column.
 */
operator fun Matrix4.component14(): Float = this.`val`[Matrix4.M31]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return forth row, third column.
 */
operator fun Matrix4.component15(): Float = this.`val`[Matrix4.M32]

/**
 * Operator function that allows to deconstruct this matrix.
 * @return forth row, forth column.
 */
operator fun Matrix4.component16(): Float = this.`val`[Matrix4.M33]
