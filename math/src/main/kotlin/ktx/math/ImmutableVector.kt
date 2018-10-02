@file:Suppress("NOTHING_TO_INLINE")

package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Represent an immutable vector.
 */
interface ImmutableVector<T : ImmutableVector<T>> : Comparable<T> {

    /**
     * Returns the squared euclidean length
     *
     * This method is faster than [Vector.len] because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact lengths, as the return value is the square of the actual length.
     */
    val len2: Float

    /** Returns whether the length of this vector is smaller than the given [margin] */
    fun isZero(margin: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean

    /** Returns a vector of the same direction and a squared length of [length2] */
    fun withLength2(length2: Float): T

    /** Returns the result of subtracting the [other] vector from this vector */
    operator fun minus(other: T): T

    /** Returns the result of adding the [other] vector to this vector */
    operator fun plus(other: T): T

    /** Returns the same vector with all members incremented by 1 */
    operator fun inc(): T

    /** Returns the same vector with all members decremented by 1 */
    operator fun dec(): T

    /** Returns this vector scaled by the given [scalar] */
    operator fun times(scalar: Float): T

    /** Returns his vector scaled by the given [vector] */
    operator fun times(vector: T): T

    /** Returns he dot product of this vector by the given [vector] */
    infix fun dot(vector: T): Float

    /**
     * Returns the squared distance between this and the other vector
     *
     * This method is faster than [dst] because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact distance, as the return value is the square of the actual distance.
     */
    infix fun dst2(vector: T): Float

    /** Linearly interpolates between this vector and the target vector by alpha which is in the range [0,1] */
    fun lerp(target: T, alpha: Float): T

    /** Returns a vector of same length and a random direction */
    fun withRandomDirection(): T

    /** Returns true if this vector is in line with the other vector (either in the same or the opposite direction) */
    fun isOnLine(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean

    /** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing */
    fun epsilonEquals(other: T, epsilon: Float): Boolean

    override fun compareTo(other: T): Int = len2.compareTo(other.len2)
}

/**
 * Returns the euclidean length
 */
inline val ImmutableVector<*>.len: Float get() = sqrt(len2)

/**
 * Returns the unit vector of same direction or this vector if it is zero.
 */
val <T : ImmutableVector<T>> T.nor: T
    get() {
        val l2 = len2

        if (l2 == 1f || l2 == 0f) return this

        return withLength2(1f)
    }

/** Returns whether this vector is a unit length vector within the given [margin]. (no margin by default) */
inline fun <T : ImmutableVector<T>> T.isUnit(margin: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean = abs(1f - len2) < margin

/** Returns the distance between this and the [other] vector */
inline infix fun <T : ImmutableVector<T>> T.dst(other: T): Float = sqrt(dst2(other))

/** Returns this vector scaled by (1 / [scalar]) */
inline operator fun <T : ImmutableVector<T>> T.div(scalar: Float): T = times(1 / scalar)

/** Returns this vector if the [ImmutableVector.len] is <= [limit] or a vector with the same direction and length [limit] otherwise */
inline fun <T : ImmutableVector<T>> T.limit(limit: Float): T = limit2(limit * limit)

/** Returns this vector if the [ImmutableVector.len2] is <= [limit2] or a vector with the same direction and length [limit2] otherwise */
inline fun <T : ImmutableVector<T>> T.limit2(limit2: Float): T =
        if (len2 <= limit2) this else withLength2(limit2)

/** Clamps this vector's length to given [min] and [max] values*/
inline fun <T : ImmutableVector<T>> T.clamp(min: Float, max: Float): T = clamp2(min * min, max * max)

/** Clamps this vector's squared length to given [min2] and [max2] values*/
inline fun <T : ImmutableVector<T>> T.clamp2(min2: Float, max2: Float): T {
    val l2 = len2
    return when {
        l2 < min2 -> withLength2(min2)
        l2 > max2 -> withLength2(max2)
        else -> this
    }
}

/** Returns a vector of same direction and the given [length] */
inline fun <T : ImmutableVector<T>> T.withLength(length: Float): T = withLength2(length * length)

/** Returns the opposite vector of same length */
inline operator fun <T : ImmutableVector<T>> T.unaryMinus(): T = times(-1f)

/** Interpolates between this vector and the given [target] vector by [alpha] (within range [0,1]) using the given [interpolation] method. */
inline fun <T : ImmutableVector<T>> T.interpolate(target: T, alpha: Float, interpolation: Interpolation): T =
        lerp(target, interpolation.apply(alpha))

/** Returns true if this vector is collinear with the [other] vector */
inline fun <T : ImmutableVector<T>> T.isCollinear(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean =
        isOnLine(other, epsilon) && hasSameDirection(other)

/** Returns true if this vector is opposite collinear with the [other] vector */
inline fun <T : ImmutableVector<T>> T.isCollinearOpposite(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean =
        isOnLine(other, epsilon) && hasOppositeDirection(other)

/** Returns true if this vector is opposite perpendicular with the [other] vector */
inline fun <T : ImmutableVector<T>> T.isPerpendicular(other: T, epsilon: Float = MathUtils.FLOAT_ROUNDING_ERROR): Boolean =
        MathUtils.isZero(dot(other), epsilon)

/** Returns whether this vector has similar direction compared to the other vector. */
inline fun <T : ImmutableVector<T>> T.hasSameDirection(other: T): Boolean =
        dot(other) > 0f

/** Returns whether this vector has opposite direction compared to the other vector. */
inline fun <T : ImmutableVector<T>> T.hasOppositeDirection(other: T): Boolean =
        dot(other) < 0f
