@file:Suppress("NOTHING_TO_INLINE")

package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector
import java.util.Random
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Represent an immutable vector.
 *
 * This is the root interface of immutable alternatives to the default mutable vectors provided by libGDX.
 */
interface ImmutableVector<T : ImmutableVector<T>> : Comparable<T> {
  /**
   * Returns the squared euclidean length
   *
   * This method is faster than [Vector.len] because it avoids calculating a square root. It is useful for comparisons,
   * but not for getting exact lengths, as the return value is the square of the actual length.
   */
  val len2: Float

  /** Returns the euclidean length */
  val len: Float get() = sqrt(len2)

  /**
   * Returns the unit vector of same direction or this vector if it is zero.
   */
  val nor: T

  /** Returns whether the length of this vector is smaller than the given [margin] */
  fun isZero(margin: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean

  /** Returns whether this vector is a unit length vector within the given [margin]. (no margin by default) */
  fun isUnit(margin: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean = abs(1f - len2) < margin

  /** Returns the opposite vector of same length */
  operator fun unaryMinus(): T

  /** Returns the result of subtracting the [other] vector from this vector */
  operator fun minus(other: T): T

  /** Returns the result of adding the [other] vector to this vector */
  operator fun plus(other: T): T

  /** Returns a new vector instance with all members incremented by 1 */
  operator fun inc(): T

  /** Returns a new vector instance with all members decremented by 1 */
  operator fun dec(): T

  /** Returns a new vector instance scaled by the given [scalar] */
  operator fun times(scalar: Float): T

  /** Returns a new vector instance scaled by the given [vector] */
  operator fun times(vector: T): T

  /** Returns a vector of the same direction and a squared length of [length2] */
  fun withLength2(length2: Float): T

  /** Returns the dot product of this vector by the given [vector] */
  infix fun dot(vector: T): Float

  /**
   * Returns the squared distance between this and the other [vector]
   *
   * This method is faster than [dst] because it avoids calculating a square root. It is useful for comparisons,
   * but not for getting exact distance, as the return value is the square of the actual distance.
   */
  infix fun dst2(vector: T): Float

  /** Linearly interpolates between this vector and the [target] vector by [alpha] */
  fun withLerp(
    target: T,
    alpha: Float,
  ): T

  /**
   * Returns a vector of same length and a random direction.
   *
   * @param rng Random number generator to use ([MathUtils.random] by default)
   */
  fun withRandomDirection(rng: Random = MathUtils.random): T

  /** Returns a vector of same direction and the given [length] */
  fun withLength(length: Float): T = withLength2(length * length)

  /** Returns this vector if the [ImmutableVector.len] is <= [limit] or a vector with the same direction and
   * length [limit] otherwise */
  fun withLimit(limit: Float): T = withLimit2(limit * limit)

  /** Returns this vector if the [ImmutableVector.len2] is <= [limit2] or a vector with the same direction and
   * length [limit2] otherwise */
  fun withLimit2(limit2: Float): T

  /** Returns a vector of same direction and the length clamped between [min] and [max] */
  fun withClamp(
    min: Float,
    max: Float,
  ): T = withClamp2(min * min, max * max)

  /** Returns a vector of same direction and the squared length clamped between [min2] and [max2] */
  fun withClamp2(
    min2: Float,
    max2: Float,
  ): T

  /** Returns the result of interpolation between this vector and the given [target] vector by [alpha]
   * (within range [0,1]) using the given [interpolation] method. */
  fun withInterpolation(
    target: T,
    alpha: Float,
    interpolation: Interpolation,
  ): T = withLerp(target, interpolation.apply(alpha))

  /**
   * Returns true if this vector is on-line with the [other] vector (either in the same or the opposite direction)
   *
   * @param epsilon Acceptable margin.
   */
  fun isOnLine(
    other: T,
    epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR,
  ): Boolean

  /**
   * Compares this vector with the [other] vector, using the supplied [epsilon] for fuzzy equality testing
   *
   * @param epsilon Acceptable difference for members. A small value makes equality it stricter, while a big value
   * makes equality fuzzier.
   */
  fun epsilonEquals(
    other: T,
    epsilon: Float,
  ): Boolean

  override fun compareTo(other: T): Int = len2.compareTo(other.len2)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withLerp(target, alpha)"), DeprecationLevel.ERROR)
  fun lerp(
    target: T,
    alpha: Float,
  ): T = withLerp(target, alpha)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withLength2(len2)"), DeprecationLevel.ERROR)
  fun setLength2(len2: Float): T = withLength2(len2)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("this * scalar"), DeprecationLevel.ERROR)
  fun scl(scalar: Float): T = this * scalar

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("this * v"), DeprecationLevel.ERROR)
  fun scl(v: T): T = this * v

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("this + v"), DeprecationLevel.ERROR)
  fun add(v: T): T = this + v

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withRandomDirection()"), DeprecationLevel.ERROR)
  fun setToRandomDirection(): T = withRandomDirection()

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("this + (v * scalar)"), DeprecationLevel.ERROR)
  fun mulAdd(
    v: T,
    scalar: Float,
  ): T = this + (v * scalar)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("this + (v * mulVec)"), DeprecationLevel.ERROR)
  fun mulAdd(
    v: T,
    mulVec: T,
  ): T = this + (v * mulVec)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withLimit(limit)"), DeprecationLevel.ERROR)
  fun limit(limit: Float): T = withLimit(limit)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withClamp(min, max)"), DeprecationLevel.ERROR)
  fun clamp(
    min: Float,
    max: Float,
  ): T = withClamp(min, max)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("v"), DeprecationLevel.ERROR)
  fun set(v: T): T = v

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withInterpolation(target, alpha, interpolator)"), DeprecationLevel.ERROR)
  fun interpolate(
    target: T,
    alpha: Float,
    interpolator: Interpolation,
  ): T = withInterpolation(target, alpha, interpolator)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withLength(len)"), DeprecationLevel.ERROR)
  fun setLength(len: Float): T = withLength(len)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("withLimit2(limit2)"), DeprecationLevel.ERROR)
  fun limit2(limit2: Float): T = withLimit2(limit2)

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("this - v"), DeprecationLevel.ERROR)
  fun sub(v: T): T = this - v

  @Deprecated(MUTABLE_METHOD_DEPRECATION_MESSAGE, ReplaceWith("nor"), DeprecationLevel.ERROR)
  fun nor(): T = nor
}

/** Returns the distance between this and the [other] vector */
inline infix fun <T : ImmutableVector<T>> T.dst(other: T): Float = sqrt(dst2(other))

/** Returns this vector scaled by (1 / [scalar]) */
inline operator fun <T : ImmutableVector<T>> T.div(scalar: Float): T = times(1 / scalar)

/**
 * Returns true if this vector is collinear with the [other] vector
 *
 * @param epsilon Acceptable margin.
 */
fun <T : ImmutableVector<T>> T.isCollinear(
  other: T,
  epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR,
): Boolean = isOnLine(other, epsilon) && hasSameDirection(other)

/**
 * Returns true if this vector is opposite collinear with the [other] vector
 *
 * @param epsilon Acceptable margin.
 */
fun <T : ImmutableVector<T>> T.isCollinearOpposite(
  other: T,
  epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR,
): Boolean = isOnLine(other, epsilon) && hasOppositeDirection(other)

/**
 * Returns true if this vector is opposite perpendicular with the [other] vector
 *
 * @param epsilon Acceptable margin.
 */
fun <T : ImmutableVector<T>> T.isPerpendicular(
  other: T,
  epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR,
): Boolean = MathUtils.isZero(dot(other), epsilon)

/** Returns whether this vector has similar direction compared to the [other] vector. */
fun <T : ImmutableVector<T>> T.hasSameDirection(other: T): Boolean = dot(other) > 0f

/** Returns whether this vector has opposite direction compared to the [other] vector. */
fun <T : ImmutableVector<T>> T.hasOppositeDirection(other: T): Boolean = dot(other) < 0f

internal const val MUTABLE_METHOD_DEPRECATION_MESSAGE =
  "Unlike its equivalent in libGDX, this function does not change the internal state of the vector" +
    "and returns a new instance instead. This might break existing code designed with mutable vectors in mind."
