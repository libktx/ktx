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
data class KVector2(val x: Float, val y: Float) : KVector<KVector2> {

    override val len2: Float = Vector2.len2(x, y)

    override fun isZero(margin: Float): Boolean = (x == 0f && y == 0f) || len2 < margin

    override operator fun minus(v: KVector2): KVector2 = minus(v.x, v.y)

    /** @return Result of subtracting the given vector from this vector */
    fun minus(deltaX: Float = 0f, deltaY: Float = 0f): KVector2 = KVector2(x - deltaX, y - deltaY)

    override operator fun plus(v: KVector2): KVector2 = plus(v.x, v.y)

    /** @return Result of adding the given vector from this vector */
    fun plus(deltaX: Float = 0f, deltaY: Float = 0f): KVector2 = KVector2(x + deltaX, y + deltaY)

    override operator fun times(scalar: Float): KVector2 = times(scalar, scalar)
    override operator fun times(vector: KVector2): KVector2 = times(vector.x, vector.y)

    /** @return This vector scaled by the given [xf] and [yf] factors */
    fun times(xf: Float, yf: Float): KVector2 = KVector2(x * xf, y * yf)

    override fun dot(vector: KVector2): Float = dot(vector.x, vector.y)

    /** @return The dot product of this vector by the given vector */
    fun dot(ox: Float, oy: Float): Float = Vector2.dot(x, y, ox, oy)

    override fun dst2(vector: KVector2): Float = dst2(vector.x, vector.y)

    /**
     * This method is faster than [dst] because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact distance, as the return value is the square of the actual distance.
     * @return The squared distance between this and the other vector
     */
    fun dst2(ox: Float, oy: Float): Float = Vector2.dst2(x, y, ox, oy)

    /** @return the distance between this and the other vector */
    fun dst(ox: Float, oy: Float): Float = Vector2.dst(x, y, ox, oy)

    override fun withLength2(len2: Float): KVector2 {
        val oldLen2 = this.len2

        if (oldLen2 == 0f || oldLen2 == len2) return this

        return times(sqrt(len2 / oldLen2))
    }

    operator fun times(matrix: Matrix3): KVector2 = KVector2(
            x = x * matrix.`val`[0] + y * matrix.`val`[3] + matrix.`val`[6],
            y = x * matrix.`val`[1] + y * matrix.`val`[4] + matrix.`val`[7]
    )

    fun crs(vector: KVector2): Float = crs(vector.x, vector.y)
    fun crs(x: Float, y: Float): Float = this.x * y - this.y * x

    fun withAngleRad(radians: Float): KVector2 = KVector2(len, 0f).rotateRad(radians)

    fun angleRad(reference: KVector2 = KVector2.X): Float = angleRad(reference.x, reference.y)
    fun angleRad(x: Float, y: Float): Float {
        val result = atan2(this.y, this.x) - atan2(y, x)
        return when {
            result > MathUtils.PI -> result - MathUtils.PI2
            result < -MathUtils.PI -> result + MathUtils.PI2
            else -> result
        }
    }

    fun rotateRad(radians: Float): KVector2 {
        val cos = cos(radians.toDouble()).toFloat()
        val sin = sin(radians.toDouble()).toFloat()

        return KVector2(
                x = this.x * cos - this.y * sin,
                y = this.x * sin + this.y * cos
        )
    }

    fun angle(reference: KVector2 = KVector2.X): Float = angle(reference.x, reference.y)
    fun angle(x: Float, y: Float): Float = angleRad(x, y) * MathUtils.radiansToDegrees

    fun rotate(degrees: Float): KVector2 = rotateRad(degrees * MathUtils.degreesToRadians)
    fun withAngle(degrees: Float): KVector2 = withAngleRad(degrees * MathUtils.degreesToRadians)

    fun rotate90(dir: Int): KVector2 =
            if (dir >= 0) KVector2(x = -y, y = x) else KVector2(x = y, y = -x)

    override fun lerp(target: KVector2, alpha: Float): KVector2 {
        val invAlpha = 1.0f - alpha
        return KVector2(
                x = x * invAlpha + target.x * alpha,
                y = y * invAlpha + target.y * alpha
        )
    }

    override fun withRandomDirection(): KVector2 = withAngleRad(MathUtils.random(0f, MathUtils.PI2))

    override fun epsilonEquals(other: KVector2, epsilon: Float): Boolean =
            epsilonEquals(other.x, other.y, epsilon)

    fun epsilonEquals(x: Float, y: Float, epsilon: Float = Float.MIN_VALUE): Boolean =
            abs(x - this.x) <= epsilon && abs(y - this.y) <= epsilon

    override fun isOnLine(other: KVector2, epsilon: Float): Boolean =
            MathUtils.isZero(x * other.y - y * other.x, epsilon)

    override fun toString(): String = "($x,$y)"

    companion object {

        /** Vector zero */
        val ZERO = KVector2(0f, 0f)

        /** unit vector of positive x axis */
        val X = KVector2(1f, 0f)

        /** unit vector of positive y axis */
        val Y = KVector2(0f, 1f)

        /**
         * @return [KVector2] represented by the specified string according to the format of [KVector2::toString]
         */
        fun fromString(string: String): KVector2 =
                Vector2().fromString(string).toKVector2()
    }
}

/** @return an instance of [KVector2] with the same x and y values */
fun KVector2.toVector2(): Vector2 = Vector2(x, y)

/** @return an instance of [Vector2] with the same x and y values */
fun Vector2.toKVector2(): KVector2 = KVector2(x, y)
