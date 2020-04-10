package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

/**
 * Tests [ClosedRange]-related utilities.
 */
class RangesTest {

  @Test
  fun `should produce expected int range`() {
    val center = -300
    val tolerance = 10000
    val range = center amid tolerance
    assertEquals(range.first, center - tolerance)
    assertEquals(range.last, center + tolerance)
  }

  @Test
  fun `should multiply int range`() {
    val original = -20..500
    val multiplier = 37
    val scaled = original * multiplier
    val invertedScaled = multiplier * original
    assertEquals(original.first * multiplier, scaled.first)
    assertEquals(original.last * multiplier, scaled.last)
    assertEquals(original.first * multiplier, invertedScaled.first)
    assertEquals(original.last * multiplier, invertedScaled.last)
  }

  @Test
  fun `should divide int range`() {
    val original = -20..500
    val divisor = 37
    val divided = original / divisor
    assertEquals(original.first / divisor, divided.first)
    assertEquals(original.last / divisor, divided.last)
  }

  @Test
  fun `should add to int range`() {
    val original = -20..500
    val addend = 37
    val shifted = original + addend
    assertEquals(original.first + addend, shifted.first)
    assertEquals(original.last + addend, shifted.last)
  }

  @Test
  fun `should subtract from int range`() {
    val original = -20..500
    val subtrahend = 37
    val shifted = original - subtrahend
    assertEquals(original.first - subtrahend, shifted.first)
    assertEquals(original.last - subtrahend, shifted.last)
  }

  @Test
  fun `should produce uniform int distribution`() {
    val count = 100000
    val allowableError = 0.03f

    val range = -4060..24500
    val values = Array(count) {
      range.random(MathUtils.random)
    }

    val numInnerRanges = 5
    val expectedCountEach = count / numInnerRanges.toFloat()
    (range step ((range.last - range.first) / numInnerRanges))
      .zipWithNext()
      .map { it.first until it.second }
      .forEach { innerRange ->
        val innerCount = values.count { it in innerRange }
        assertTrue(abs(innerCount.toFloat() - expectedCountEach) / expectedCountEach <= allowableError)
      }
    assertTrue(values.all { it in range })
  }

  @Test
  fun `should produce expected float range`() {
    val center = -325f
    val tolerance = 6000f
    val range = center amid tolerance
    assertEquals(range.start, center - tolerance)
    assertEquals(range.endInclusive, center + tolerance)
  }

  @Test
  fun `should multiply float range`() {
    val original = -20f..500f
    val multiplier = 37f
    val scaled = original * multiplier
    val invertedScaled = multiplier * original
    assertEquals(original.start * multiplier, scaled.start)
    assertEquals(original.endInclusive * multiplier, scaled.endInclusive)
    assertEquals(original.start * multiplier, invertedScaled.start)
    assertEquals(original.endInclusive * multiplier, invertedScaled.endInclusive)
  }

  @Test
  fun `should divide float range`() {
    val original = -20f..500f
    val divisor = 37f
    val divided = original / divisor
    assertEquals(original.start / divisor, divided.start)
    assertEquals(original.endInclusive / divisor, divided.endInclusive)
  }

  @Test
  fun `should add to float range`() {
    val original = -20f..500f
    val addend = 37f
    val shifted = original + addend
    assertEquals(original.start + addend, shifted.start)
    assertEquals(original.endInclusive + addend, shifted.endInclusive)
  }

  @Test
  fun `should subtract from float range`() {
    val original = -20f..500f
    val subtrahend = 37f
    val shifted = original - subtrahend
    assertEquals(original.start - subtrahend, shifted.start)
    assertEquals(original.endInclusive - subtrahend, shifted.endInclusive)
  }

  @Test
  fun `should produce uniform float distribution`() {
    val count = 100000
    val allowableError = 0.03f

    val range = -4060f..24500f
    val values = Array(count) {
      range.random()
    }

    val numInnerRanges = 5
    val expectedCountEach = count / numInnerRanges.toFloat()
    List(numInnerRanges + 1) { it * (range.endInclusive - range.start) / numInnerRanges + range.start }
      .zipWithNext()
      .map { it.first..it.second }
      .forEach { innerRange ->
        val innerCount = values.count { it in innerRange }
        assertTrue(abs(innerCount.toFloat() - expectedCountEach) / expectedCountEach <= allowableError)
      }
    assertTrue(values.all { it in range })
  }

  @Test
  fun `should produce gaussian distribution`() {
    val count = 100000
    val allowableError = 0.03f

    val center = 350f
    val tolerance = 6300f
    val range = center amid tolerance
    val fourSigma = center amid (tolerance * 2f / 3f)
    val twoSigma = center amid (tolerance / 3f)
    var withinRange = 0
    var withinFourSigma = 0
    var withinTwoSigma = 0
    repeat(count) {
      val value = range.randomGaussian(false)
      if (value in range) withinRange++
      if (value in fourSigma) withinFourSigma++
      if (value in twoSigma) withinTwoSigma++
    }

    val resultsToExpected = listOf(
      withinRange.toFloat() / count to 0.9973f,
      withinFourSigma.toFloat() / count to 0.9545f,
      withinTwoSigma.toFloat() / count to 0.6827f)
    for ((result, expected) in resultsToExpected) {
      assertTrue(abs(result - expected) / expected <= allowableError)
    }
  }

  @Test
  fun `should produce symmetric triangular distribution`() {
    val count = 100000
    val allowableError = 0.03f

    val range = -240f..9800f
    val span = range.endInclusive - range.start
    val values = Array(count) {
      range.randomTriangular()
    }

    val center = 0.5 * (range.endInclusive + range.start)
    val beforeModeSpan = center - range.start
    val afterModeSpan = range.endInclusive - center
    for (fraction in listOf(0.2f, 0.4f, 0.6f, 0.8f)) {
      val innerRange = (beforeModeSpan * fraction + range.start)..(range.endInclusive - afterModeSpan * fraction)
      val leftTriangleBase = innerRange.start - range.start
      val leftTriangleArea = 0.5f * leftTriangleBase * (2 * leftTriangleBase / (span * beforeModeSpan))
      val probability = 1f - leftTriangleArea * 2
      val result = values.count { it in innerRange }.toFloat() / count
      assertTrue(abs(result - probability) / probability <= allowableError)
    }
    assertTrue(values.all { it in range })
  }

  @Test
  fun `should produce triangular distribution`() {
    val count = 100000
    val allowableError = 0.03f

    val range = -240f..9800f
    val span = range.endInclusive - range.start
    val fractionalMode = 0.73f
    val values = Array(count) {
      range.randomTriangular(fractionalMode)
    }

    val expectedMode = fractionalMode * (range.endInclusive - range.start) + range.start
    val beforeModeSpan = expectedMode - range.start
    val afterModeSpan = range.endInclusive - expectedMode
    for (fraction in listOf(0.2f, 0.4f, 0.6f, 0.8f)) {
      // For simplicity, all checked ranges span the mode
      val innerRange = (beforeModeSpan * fraction + range.start)..(range.endInclusive - afterModeSpan * fraction)
      val leftTriangleBase = innerRange.start - range.start
      val leftTriangleArea = 0.5f * leftTriangleBase * (2 * leftTriangleBase / (span * beforeModeSpan))
      val rightTriangleBase = range.endInclusive - innerRange.endInclusive
      val rightTriangleArea = 0.5f * rightTriangleBase * (2 * rightTriangleBase / (span * afterModeSpan))
      val probability = 1f - leftTriangleArea - rightTriangleArea
      val result = values.count { it in innerRange }.toFloat() / count
      assertTrue(abs(result - probability) / probability <= allowableError)
    }
    assertTrue(values.all { it in range })
  }

  @Test
  fun `should interpolate linearly`() {
    val start = 150f
    val end = 300f
    val progress = 0.75f

    val range = start..end

    assertEquals(range.lerp(progress), 262.5f, 0.000001f)
  }

  @Test
  fun `should interpolate`() {
    val start = 0f
    val end = 1f
    val progress = 0.5f

    val range = start..end

    assertEquals(range.interpolate(progress, Interpolation.sineOut), 0.7071f, 0.001f)
  }
}
