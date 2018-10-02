@file:Suppress("NOTHING_TO_INLINE")

package ktx.math

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import kotlin.math.*

/**
 * Represent an **immutable** vector
 *
 * @property x the x-component of this vector
 * @property y the y-component of this vector
 */
data class ImmutableVector2(val x: Float, val y: Float) : ImmutableVector<ImmutableVector2> {

    override val len2: Float = Vector2.len2(x, y)

    override fun isZero(margin: Float): Boolean = (x == 0f && y == 0f) || len2 < margin

    override operator fun minus(v: ImmutableVector2): ImmutableVector2 = minus(v.x, v.y)

    /** @return Result of subtracting the given vector from this vector */
    fun minus(deltaX: Float = 0f, deltaY: Float = 0f): ImmutableVector2 = ImmutableVector2(x - deltaX, y - deltaY)

    override operator fun plus(v: ImmutableVector2): ImmutableVector2 = plus(v.x, v.y)

    /** @return Result of adding the given vector from this vector */
    fun plus(deltaX: Float = 0f, deltaY: Float = 0f): ImmutableVector2 = ImmutableVector2(x + deltaX, y + deltaY)

    override operator fun times(scalar: Float): ImmutableVector2 = times(scalar, scalar)
    override operator fun times(vector: ImmutableVector2): ImmutableVector2 = times(vector.x, vector.y)

    /** @return This vector scaled by the given [xf] and [yf] factors */
    fun times(xf: Float, yf: Float): ImmutableVector2 = ImmutableVector2(x * xf, y * yf)

    override fun dot(vector: ImmutableVector2): Float = dot(vector.x, vector.y)

    /** @return The dot product of this vector by the given vector */
    fun dot(ox: Float, oy: Float): Float = Vector2.dot(x, y, ox, oy)

    override fun dst2(vector: ImmutableVector2): Float = dst2(vector.x, vector.y)

    /**
     * This method is faster than [dst] because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact distance, as the return value is the square of the actual distance.
     * @return The squared distance between this and the other vector
     */
    fun dst2(ox: Float, oy: Float): Float = Vector2.dst2(x, y, ox, oy)

    /** @return the distance between this and the other vector */
    fun dst(ox: Float, oy: Float): Float = Vector2.dst(x, y, ox, oy)

    override fun withLength2(len2: Float): ImmutableVector2 {
        val oldLen2 = this.len2

        if (oldLen2 == 0f || oldLen2 == len2) return this

        return times(sqrt(len2 / oldLen2))
    }

    operator fun times(matrix: Matrix3): ImmutableVector2 = ImmutableVector2(
            x = x * matrix.`val`[0] + y * matrix.`val`[3] + matrix.`val`[6],
            y = x * matrix.`val`[1] + y * matrix.`val`[4] + matrix.`val`[7]
    )

    fun crs(vector: ImmutableVector2): Float = crs(vector.x, vector.y)
    fun crs(x: Float, y: Float): Float = this.x * y - this.y * x

    fun withAngleRad(radians: Float): ImmutableVector2 = ImmutableVector2(len, 0f).rotateRad(radians)

    fun angleRad(reference: ImmutableVector2 = ImmutableVector2.X): Float = angleRad(reference.x, reference.y)
    fun angleRad(x: Float, y: Float): Float {
        val result = atan2(this.y, this.x) - atan2(y, x)
        return when {
            result > MathUtils.PI -> result - MathUtils.PI2
            result < -MathUtils.PI -> result + MathUtils.PI2
            else -> result
        }
    }

    fun rotateRad(radians: Float): ImmutableVector2 {
        val cos = cos(radians.toDouble()).toFloat()
        val sin = sin(radians.toDouble()).toFloat()

        return ImmutableVector2(
                x = this.x * cos - this.y * sin,
                y = this.x * sin + this.y * cos
        )
    }

    fun rotate(degrees: Float): ImmutableVector2 = rotateRad(degrees * MathUtils.degreesToRadians)
    fun withAngle(degrees: Float): ImmutableVector2 = withAngleRad(degrees * MathUtils.degreesToRadians)

    fun rotate90(dir: Int): ImmutableVector2 =
            if (dir >= 0) ImmutableVector2(x = -y, y = x) else ImmutableVector2(x = y, y = -x)

    override fun lerp(target: ImmutableVector2, alpha: Float): ImmutableVector2 {
        val invAlpha = 1.0f - alpha
        return ImmutableVector2(
                x = x * invAlpha + target.x * alpha,
                y = y * invAlpha + target.y * alpha
        )
    }

    override fun withRandomDirection(): ImmutableVector2 = withAngleRad(MathUtils.random(0f, MathUtils.PI2))

    override fun epsilonEquals(other: ImmutableVector2, epsilon: Float): Boolean =
            epsilonEquals(other.x, other.y, epsilon)

    fun epsilonEquals(x: Float, y: Float, epsilon: Float = Float.MIN_VALUE): Boolean =
            abs(x - this.x) <= epsilon && abs(y - this.y) <= epsilon

    override fun isOnLine(other: ImmutableVector2, epsilon: Float): Boolean =
            MathUtils.isZero(x * other.y - y * other.x, epsilon)

    override fun toString(): String = "($x,$y)"

    companion object {

        /** Vector zero */
        val ZERO = ImmutableVector2(0f, 0f)

        /** unit vector of positive x axis */
        val X = ImmutableVector2(1f, 0f)

        /** unit vector of positive y axis */
        val Y = ImmutableVector2(0f, 1f)

        /**
         * @return [ImmutableVector2] represented by the specified string according to the format of [ImmutableVector2::toString]
         */
        fun fromString(string: String): ImmutableVector2 =
                Vector2().fromString(string).toImmutableVector2()
    }
}

/** @return an instance of [ImmutableVector2] with the same x and y values */
inline fun ImmutableVector2.toVector2(): Vector2 = Vector2(x, y)

/** @return an instance of [Vector2] with the same x and y values */
inline fun Vector2.toImmutableVector2(): ImmutableVector2 = ImmutableVector2(x, y)

@Deprecated(
        message = "This function doesn't behave like its equivalent in LibGDX and return an angle between -180 and 180 (some LibGDX functions return between -180 and 180 and some other between 0 and 360)",
        replaceWith = ReplaceWith("angleDeg(reference)")
)
inline fun ImmutableVector2.angle(reference: ImmutableVector2 = ImmutableVector2.X): Float = angleDeg(reference)

@Deprecated(
        message = "This function doesn't behave like its equivalent in LibGDX and return an angle between -180 and 180 (some LibGDX functions return between -180 and 180 and some other between 0 and 360)",
        replaceWith = ReplaceWith("angleDeg(x, y)")
)
inline fun ImmutableVector2.angle(x: Float, y: Float): Float = angleDeg(x, y)

/** Returns the angle in degrees of this vector (point) relative to the [reference]. Angles are towards the positive y-axis (typically counter-clockwise.) between -180 and +180 */
inline fun ImmutableVector2.angleDeg(reference: ImmutableVector2 = ImmutableVector2.X): Float = angleDeg(reference.x, reference.y)

/** Returns the angle in degrees of this vector (point) relative to the reference vector described by [x] and [y]. Angles are towards the positive y-axis (typically counter-clockwise.) between -180 and +180 */
inline fun ImmutableVector2.angleDeg(x: Float, y: Float): Float = angleRad(x, y) * MathUtils.radiansToDegrees
