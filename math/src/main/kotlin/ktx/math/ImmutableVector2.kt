@file:Suppress("NOTHING_TO_INLINE")

package ktx.math

import com.badlogic.gdx.math.Affine2
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import java.util.Random
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represent an immutable vector 2D
 *
 * You may use [ImmutableVector2.toMutable] to create a (mutable) [Vector2] from a an [ImmutableVector2].
 * And an [ImmutableVector2] can be created from a [Vector2] with [Vector2.toImmutable].
 *
 * @property x the x-component of this vector
 * @property y the y-component of this vector
 */
data class ImmutableVector2(
  val x: Float,
  val y: Float,
) : ImmutableVector<ImmutableVector2> {
  override val len2: Float = Vector2.len2(x, y)

  override val nor: ImmutableVector2 get() = withLength2(1f)

  override fun isZero(margin: Float): Boolean = (x == 0f && y == 0f) || len2 < margin

  override fun unaryMinus(): ImmutableVector2 = ImmutableVector2(-x, -y)

  override operator fun minus(other: ImmutableVector2): ImmutableVector2 = minus(other.x, other.y)

  /** Returns the result of subtracting the ([deltaX], [deltaY]) vector from this vector */
  fun minus(
    deltaX: Float = 0f,
    deltaY: Float = 0f,
  ): ImmutableVector2 = ImmutableVector2(x - deltaX, y - deltaY)

  override operator fun plus(other: ImmutableVector2): ImmutableVector2 = plus(other.x, other.y)

  /** Returns the result of adding the given ([deltaX], [deltaY]) vector to this vector */
  fun plus(
    deltaX: Float = 0f,
    deltaY: Float = 0f,
  ): ImmutableVector2 = ImmutableVector2(x + deltaX, y + deltaY)

  override fun inc(): ImmutableVector2 = ImmutableVector2(x + 1, y + 1)

  override fun dec(): ImmutableVector2 = ImmutableVector2(x - 1, y - 1)

  override operator fun times(scalar: Float): ImmutableVector2 = times(scalar, scalar)

  override operator fun times(vector: ImmutableVector2): ImmutableVector2 = times(vector.x, vector.y)

  /** Returns a new vector instance scaled by the given [factorX] and [factorY] factors */
  fun times(
    factorX: Float,
    factorY: Float,
  ): ImmutableVector2 = ImmutableVector2(x * factorX, y * factorY)

  override fun withLimit2(limit2: Float): ImmutableVector2 = if (len2 <= limit2) this else withLength2(limit2)

  override fun withClamp2(
    min2: Float,
    max2: Float,
  ): ImmutableVector2 {
    val l2 = len2

    return when {
      l2 < min2 -> withLength2(min2)
      l2 > max2 -> withLength2(max2)
      else -> this
    }
  }

  override fun withLength2(length2: Float): ImmutableVector2 {
    val oldLen2 = len2

    return if (oldLen2 == 0f || oldLen2 == length2) this else times(sqrt(length2 / oldLen2))
  }

  override fun dot(vector: ImmutableVector2): Float = dot(vector.x, vector.y)

  /** Returns the dot product of this vector by the given ([otherX], [otherY]) vector */
  fun dot(
    otherX: Float,
    otherY: Float,
  ): Float = Vector2.dot(x, y, otherX, otherY)

  override fun dst2(vector: ImmutableVector2): Float = dst2(vector.x, vector.y)

  /**
   * Returns the squared distance between this and the ([otherX], [otherY]) vector
   *
   * This method is faster than [dst] because it avoids calculating a square root. It is useful for comparisons,
   * but not for getting exact distance, as the return value is the square of the actual distance.
   */
  fun dst2(
    otherX: Float,
    otherY: Float,
  ): Float = Vector2.dst2(x, y, otherX, otherY)

  /** @return the distance between this and the ([otherX], [otherY]) vector */
  fun dst(
    otherX: Float,
    otherY: Float,
  ): Float = Vector2.dst(x, y, otherX, otherY)

  /** Apply the given affine [transformation] and return the resulting vector */
  operator fun times(transformation: Affine2): ImmutableVector2 =
    ImmutableVector2(
      x = x * transformation.m00 + y * transformation.m01 + transformation.m02,
      y = x * transformation.m10 + y * transformation.m11 + transformation.m12,
    )

  /** Calculates the 2D cross product between this and the ([otherX], [otherY]) vector */
  fun crs(
    otherX: Float,
    otherY: Float,
  ): Float = x * otherY - y * otherX

  /** Returns the angle in radians of this vector relative to the [reference]. Angles are towards the positive y-axis. (typically counter-clockwise) */
  fun angleRad(reference: ImmutableVector2 = X): Float = angleRad(reference.x, reference.y)

  /** Returns the angle in radians of this vector relative to the ([referenceX], [referenceY]) reference. Angles are towards the positive y-axis. (typically counter-clockwise) */
  fun angleRad(
    referenceX: Float,
    referenceY: Float,
  ): Float {
    if ((x == 0f && y == 0f) || (referenceX == 0f && referenceY == 0f)) return Float.NaN

    val result = atan2(y, x) - atan2(referenceY, referenceX)
    return when {
      result > MathUtils.PI -> result - MathUtils.PI2
      result < -MathUtils.PI -> result + MathUtils.PI2
      else -> result
    }
  }

  /** Returns a vector of same length with the given [angle] in radians */
  fun withAngleRad(angle: Float): ImmutableVector2 = ImmutableVector2(len, 0f).withRotationRad(angle)

  /**
   * Returns a vector of same length rotated by 90 degrees in the given [direction]
   *
   * @param direction positive value means toward positive y-axis (typically counter-clockwise). Negative value means toward negative y-axis (typically clockwise).
   */
  fun withRotation90(direction: Int): ImmutableVector2 = if (direction >= 0) ImmutableVector2(-y, x) else ImmutableVector2(y, -x)

  /** Returns a vector of same length rotated by the given [angle] in radians */
  fun withRotationRad(angle: Float): ImmutableVector2 {
    val cos = cos(angle)
    val sin = sin(angle)

    return ImmutableVector2(
      x = this.x * cos - this.y * sin,
      y = this.x * sin + this.y * cos,
    )
  }

  override fun withLerp(
    target: ImmutableVector2,
    alpha: Float,
  ): ImmutableVector2 {
    val invAlpha = 1.0f - alpha

    return ImmutableVector2(
      x = x * invAlpha + target.x * alpha,
      y = y * invAlpha + target.y * alpha,
    )
  }

  override fun withRandomDirection(rng: Random): ImmutableVector2 = withAngleRad(rng.nextFloat() * MathUtils.PI2)

  override fun epsilonEquals(
    other: ImmutableVector2,
    epsilon: Float,
  ): Boolean = epsilonEquals(other.x, other.y, epsilon)

  /**
   * Compares this vector with the ([otherX], [otherY]) vector, using the supplied [epsilon] for fuzzy equality testing
   *
   * @param epsilon Acceptable difference for members. A small value makes equality it stricter,
   * while a big value makes equality fuzzier.
   */
  fun epsilonEquals(
    otherX: Float,
    otherY: Float,
    epsilon: Float,
  ): Boolean = abs(otherX - this.x) <= epsilon && abs(otherY - this.y) <= epsilon

  override fun isOnLine(
    other: ImmutableVector2,
    epsilon: Float,
  ): Boolean = MathUtils.isZero(x * other.y - y * other.x, epsilon) && !isZero(0f) && !other.isZero(0f)

  override fun toString(): String = "($x,$y)"

  @Deprecated(
    message =
      "This function doesn't behave like its equivalent in libGDX and return an angle between -180 and 180" +
        "(some libGDX functions return between -180 and 180 and some other between 0 and 360)",
    replaceWith = ReplaceWith("angleDeg(reference)"),
  )
  inline fun angle(reference: ImmutableVector2 = X): Float = angleDeg(reference)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withRotationDeg(angle)"), DeprecationLevel.ERROR)
  inline fun rotate(angle: Float): ImmutableVector2 = withRotationDeg(angle)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("ImmutableVector2.ZERO"), DeprecationLevel.ERROR)
  fun setZero(): ImmutableVector2 = ZERO

  @Deprecated(
    MUTABLE_METHOD_DEPRECATION_MESSAGE,
    ReplaceWith("this * Affine2().set(matrix)", "com.badlogic.gdx.math.Affine2"),
    DeprecationLevel.ERROR,
  )
  fun mul(matrix: Matrix3): ImmutableVector2 = this * Affine2().set(matrix)

  @Deprecated("Immutable instances don't need to be copied", ReplaceWith("this"))
  fun cpy(): ImmutableVector2 = this

  companion object {
    /** Vector zero */
    val ZERO = ImmutableVector2(0f, 0f)

    /** Unit vector of positive x axis */
    val X = ImmutableVector2(1f, 0f)

    /** Unit vector of positive y axis */
    val Y = ImmutableVector2(0f, 1f)

    /**
     * Returns the [ImmutableVector2] represented by the specified [string] according to
     * the format of [ImmutableVector2::toString]
     */
    fun fromString(string: String): ImmutableVector2 = Vector2().fromString(string).toImmutable()
  }
}

/** @return an instance of [ImmutableVector2] with the same x and y values */
inline fun ImmutableVector2.toMutable(): Vector2 = Vector2(x, y)

/** @return an instance of [Vector2] with the same x and y values */
inline fun Vector2.toImmutable(): ImmutableVector2 = ImmutableVector2(x, y)

/** Returns the angle in degrees of this vector relative to the [reference].
 * Angles are towards the positive y-axis (typically counter-clockwise) between -180 and +180 */
inline fun ImmutableVector2.angleDeg(reference: ImmutableVector2 = ImmutableVector2.X): Float = angleDeg(reference.x, reference.y)

/**
 * Returns the angle in degrees of this vector relative to the reference vector described by
 * [referenceX] and [referenceY].
 *
 * Angles are towards the positive y-axis (typically counter-clockwise.) between -180 and +180
 */
inline fun ImmutableVector2.angleDeg(
  referenceX: Float,
  referenceY: Float,
): Float = angleRad(referenceX, referenceY) * MathUtils.radiansToDegrees

/** Returns a vector of same length rotated by the given [angle] in degree */
inline fun ImmutableVector2.withRotationDeg(angle: Float): ImmutableVector2 = withRotationRad(angle * MathUtils.degreesToRadians)

/** Returns a vector of same length with the given [angle] in degree */
inline fun ImmutableVector2.withAngleDeg(angle: Float): ImmutableVector2 = withAngleRad(angle * MathUtils.degreesToRadians)

/** Calculates the 2D cross product between this and the [other] vector */
inline infix fun ImmutableVector2.x(other: ImmutableVector2): Float = crs(other.x, other.y)

/** Calculates the 2D cross product between this and the [other] vector */
inline infix fun ImmutableVector2.crs(other: ImmutableVector2): Float = crs(other.x, other.y)
