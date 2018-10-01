@file:Suppress("NOTHING_TO_INLINE")

package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Represent an immutable vector
 */
interface ImmutableVector<T : ImmutableVector<T>> {

    /**
     * This method is faster than [Vector.len] because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact lengths, as the return value is the square of the actual length.
     *
     * @return The squared euclidean length
     */
    val len2: Float

    /** @return Whether the length of this vector is smaller than the given margin */
    fun isZero(margin: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean

    /** @return a vector of the same direction and a squared length of [len2] */
    fun withLength2(len2: Float): T

    /** @return Result of subtracting the given vector from this vector */
    operator fun minus(v: T): T

    /** @return Result of adding the given vector from this vector */
    operator fun plus(v: T): T

    /** @return This vector scaled by the given [scalar] */
    operator fun times(scalar: Float): T

    /** @return This vector scaled by the given [vector] */
    operator fun times(vector: T): T

    /** @return The dot product of this vector by the given [vector] */
    fun dot(vector: T): Float

    /**
     * This method is faster than [dst] because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact distance, as the return value is the square of the actual distance.
     * @return The squared distance between this and the other vector
     */
    fun dst2(vector: T): Float

    fun lerp(target: T, alpha: Float): T

    fun withRandomDirection(): T

    fun isOnLine(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean

    fun epsilonEquals(other: T, epsilon: Float): Boolean
}

/**
 * @return The euclidean length
 */
inline val ImmutableVector<*>.len: Float get() = sqrt(len2)

/**
 * @return the unit vector of same direction or this vector if it is zero.
 */
val <T : ImmutableVector<T>> T.nor: T
    get() {
        val l2 = len2

        if (l2 == 1f || l2 == 0f) return this

        return withLength2(1f)
    }

/** @return Whether this vector is a unit length vector within the given margin. (no margin by default) */
inline fun <T : ImmutableVector<T>> T.isUnit(margin: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean = abs(1f - len2) < margin

inline fun <T : ImmutableVector<T>> T.dst(v: T): Float = sqrt(dst2(v))
inline fun <T : ImmutableVector<T>> T.limit(limit: Float): T = limit2(limit * limit)

/** @return this vector if the [ImmutableVector.len2] is <= [limit2] and A vector with the same direction and length [limit2] otherwise */
inline fun <T : ImmutableVector<T>> T.limit2(limit2: Float): T =
        if (len2 <= limit2) this else withLength2(limit2)

inline fun <T : ImmutableVector<T>> T.clamp(min: Float, max: Float): T = clamp2(min * min, max * max)

/** @return Clamps this vector's length to given [min] and [max] values*/
inline fun <T : ImmutableVector<T>> T.clamp2(min2: Float, max2: Float): T {
    val l2 = len2
    return when {
        l2 < min2 -> withLength2(min2)
        l2 > max2 -> withLength2(max2)
        else -> this
    }
}

inline fun <T : ImmutableVector<T>> T.withLength(len: Float): T = withLength2(len * len)

/** @return The opposite vector of same length */
inline operator fun <T : ImmutableVector<T>> T.unaryMinus(): T = times(-1f)

inline fun <T : ImmutableVector<T>> T.interpolate(target: T, alpha: Float, interpolation: Interpolation): T =
        lerp(target, interpolation.apply(alpha))

inline fun <T : ImmutableVector<T>> T.isCollinear(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean =
        isOnLine(other, epsilon) && hasSameDirection(other)

inline fun <T : ImmutableVector<T>> T.isCollinearOpposite(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean =
        isOnLine(other, epsilon) && hasOppositeDirection(other)

inline fun <T : ImmutableVector<T>> T.isPerpendicular(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean =
        MathUtils.isZero(dot(other), epsilon)

inline fun <T : ImmutableVector<T>> T.hasSameDirection(other: T): Boolean =
        dot(other) > 0f

inline fun <T : ImmutableVector<T>> T.hasOppositeDirection(other: T): Boolean =
        dot(other) < 0f
