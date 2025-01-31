package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils

/**
 * Creates a range defined with the given inclusive [tolerance] above and below this center value.
 */
infix fun Int.amid(tolerance: Int) = (this - tolerance)..(this + tolerance)

/**
 * Returns a random element from this range using the specified source of randomness.
 *
 * This overload allows passing a [java.util.Random] instance so, for instance, [MathUtils.random] may be used. Results
 * are undefined for an empty range, and there is no error checking.
 */
fun IntRange.random(random: java.util.Random) = random.nextInt(1 + last - first) + first

/**
 * Creates a range by scaling this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [multiplier].
 */
operator fun IntRange.times(multiplier: Int) = (first * multiplier)..(last * multiplier)

/**
 * Creates a range by scaling the [range]'s [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * this multiplier.
 */
operator fun Int.times(range: IntRange) = (this * range.first)..(this * range.last)

/**
 * Creates a range by scaling this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [divisor].
 */
operator fun IntRange.div(divisor: Int) = (first / divisor)..(last / divisor)

/**
 * Creates a range by shifting this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [addend].
 */
operator fun IntRange.plus(addend: Int) = (first + addend)..(last + addend)

/**
 * Creates a range by shifting this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [subtrahend].
 */
operator fun IntRange.minus(subtrahend: Int) = (start - subtrahend)..(endInclusive - subtrahend)

/**
 * Creates a range defined with the given [tolerance] above and below this center value.
 */
infix fun Float.amid(tolerance: Float) = (this - tolerance)..(this + tolerance)

/**
 * Creates a range by scaling this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [multiplier].
 */
operator fun ClosedRange<Float>.times(multiplier: Float) = (start * multiplier)..(endInclusive * multiplier)

/**
 * Creates a range by scaling the [range]'s [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * this multiplier.
 */
operator fun Float.times(range: ClosedRange<Float>) = (this * range.start)..(this * range.endInclusive)

/**
 * Creates a range by scaling this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [denominator].
 */
operator fun ClosedRange<Float>.div(denominator: Float) = (start / denominator)..(endInclusive / denominator)

/**
 * Creates a range by shifting this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [addend].
 */
operator fun ClosedRange<Float>.plus(addend: Float) = (start + addend)..(endInclusive + addend)

/**
 * Creates a range by shifting this range's [start][ClosedRange.start] and [endInclusive][ClosedRange.endInclusive] by
 * the [subtrahend].
 */
operator fun ClosedRange<Float>.minus(subtrahend: Float) = (start - subtrahend)..(endInclusive - subtrahend)

/**
 * Returns a pseudo-random, uniformly distributed [Float] value from [MathUtils.random]'s sequence, bounded by
 * this range.
 *
 * Results are undefined for an empty range, and there is no error checking. Note that
 * [endInclusive][ClosedRange.endInclusive] is treated as exclusive as it is not practical to keep it inclusive.
 */
fun ClosedRange<Float>.random() = MathUtils.random.nextFloat() * (endInclusive - start) + start

/**
 * Returns a pseudo-random, standard Gaussian distributed [Float] value from [MathUtils.random]'s sequence. The
 * distribution is centered to this range's center and is scaled so this range is six standard deviations wide.
 *
 * Results are undefined for an empty range, and there is no error checking.
 *
 * @param clamped If true (the default), values outside the range are clamped to the range.
 */
fun ClosedRange<Float>.randomGaussian(clamped: Boolean = true) =
  ((MathUtils.random.nextGaussian() / 6.0 + 0.5).toFloat() * (endInclusive - start) + start).let {
    if (clamped) {
      it.coerceIn(this)
    } else {
      it
    }
  }

/**
 * Returns a triangularly distributed random number in this range, with the *mode* centered in this range, giving a
 * symmetric distribution.
 *
 * This function uses [MathUtils.randomTriangular]. Note that [endInclusive][ClosedRange.endInclusive] is treated as
 * exclusive as it is not practical to keep it inclusive. Results are undefined for an empty range, and there is no
 * error checking.
 */
fun ClosedRange<Float>.randomTriangular() = MathUtils.randomTriangular(start, endInclusive)

/**
 * Returns a triangularly distributed random number in this range, where values around the *mode* are more likely.
 * [normalizedMode] must be a value in the range 0.0..1.0 and represents the fractional position of the mode across the
 * range.
 *
 * This function uses `MathUtils.randomTriangular(min, max, mode)`. Note that [endInclusive][ClosedRange.endInclusive]
 * is treated as exclusive as it is not practical to keep it inclusive. Results are undefined for an empty range, and
 * there is no error checking.
 */
fun ClosedRange<Float>.randomTriangular(normalizedMode: Float): Float =
  MathUtils.randomTriangular(
    start,
    endInclusive,
    normalizedMode * (endInclusive - start) + start,
  )

/**
 * Linearly interpolate between the start and end of this range.
 *
 * @param progress The position to interpolate, where 0 corresponds with [ClosedRange.start] and 1 corresponds with
 * [ClosedRange.endInclusive].
 * @return The interpolated value.
 */
fun ClosedRange<Float>.lerp(progress: Float): Float = progress * (endInclusive - start) + start

/**
 * Interpolate between the start and end of this range.
 *
 * @param progress The position to interpolate, where 0 corresponds with [ClosedRange.start] and 1 corresponds with
 * [ClosedRange.endInclusive].
 * @param interpolation The function to interpolate with.
 * @return The interpolated value.
 */
fun ClosedRange<Float>.interpolate(
  progress: Float,
  interpolation: Interpolation,
): Float = interpolation.apply(progress) * (endInclusive - start) + start
