package ktx.math

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.GdxNativesLoader
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests [Matrix4]-related utilities.
 */
class Matrix4Test {
  private val floatTolerance = 0.00001f

  @Test
  fun `should create matrix`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    assertMatrixEquals(
      matrix,
      +1f,
      +2f,
      +3f,
      +4f,
      +5f,
      +6f,
      +7f,
      +8f,
      +9f,
      10f,
      11f,
      12f,
      13f,
      14f,
      15f,
      16f,
    )
  }

  @Test
  fun `should negate matrix values`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val result = -matrix

    assertMatrixEquals(
      result,
      -1f,
      -2f,
      -3f,
      -4f,
      -5f,
      -6f,
      -7f,
      -8f,
      -9f,
      -10f,
      -11f,
      -12f,
      -13f,
      -14f,
      -15f,
      -16f,
    )
    assertMatrixEquals(
      matrix,
      1f,
      2f,
      3f,
      4f,
      5f,
      6f,
      7f,
      8f,
      9f,
      10f,
      11f,
      12f,
      13f,
      14f,
      15f,
      16f,
    )
  }

  @Test
  fun `should invert matrix`() {
    val matrix =
      mat4(
        1f,
        0f,
        1f,
        0f,
        0f,
        1f,
        0f,
        1f,
        0f,
        0f,
        1f,
        0f,
        0f,
        0f,
        0f,
        1f,
      )

    val result = !matrix

    assertMatrixEquals(
      result,
      1f,
      0f,
      -1f,
      0f,
      0f,
      1f,
      0f,
      -1f,
      0f,
      0f,
      1f,
      0f,
      0f,
      0f,
      0f,
      1f,
    )
    assertMatrixEquals(
      matrix,
      1f,
      0f,
      1f,
      0f,
      0f,
      1f,
      0f,
      1f,
      0f,
      0f,
      1f,
      0f,
      0f,
      0f,
      0f,
      1f,
    )
  }

  @Test
  fun `should addAssign matrices`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    matrix +=
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    assertMatrixEquals(
      matrix,
      +2f,
      +4f,
      +6f,
      +8f,
      10f,
      12f,
      14f,
      16f,
      18f,
      20f,
      22f,
      24f,
      26f,
      28f,
      30f,
      32f,
    )
  }

  @Test
  fun `should subtractAssign matrices`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    matrix -=
      mat4(
        16f,
        15f,
        14f,
        13f,
        12f,
        11f,
        10f,
        +9f,
        +8f,
        +7f,
        +6f,
        +5f,
        +4f,
        +3f,
        +2f,
        +1f,
      )

    assertMatrixEquals(
      matrix,
      -15f,
      -13f,
      -11f,
      -9f,
      -7f,
      -5.0f,
      -3f,
      -1f,
      1.0f,
      3.0f,
      5.0f,
      7f,
      9.0f,
      11.0f,
      13f,
      15f,
    )
  }

  @Test
  fun `should multiplyAssign matrices`() {
    GdxNativesLoader.load() // Matrix.mul4 is written with native code.

    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    matrix *=
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    assertMatrixEquals(
      matrix,
      1f * 1f + 2f * 5f + 3f * 9f + 4f * 13f,
      1f * 2f + 2f * 6f + 3f * 10f + 4f * 14f,
      1f * 3f + 2f * 7f + 3f * 11f + 4f * 15f,
      1f * 4f + 2f * 8f + 3f * 12f + 4f * 16f,
      5f * 1f + 6f * 5f + 7f * 9f + 8f * 13f,
      5f * 2f + 6f * 6f + 7f * 10f + 8f * 14f,
      5f * 3f + 6f * 7f + 7f * 11f + 8f * 15f,
      5f * 4f + 6f * 8f + 7f * 12f + 8f * 16f,
      9f * 1f + 10f * 5f + 11f * 9f + 12f * 13f,
      9f * 2f + 10f * 6f + 11f * 10f + 12f * 14f,
      9f * 3f + 10f * 7f + 11f * 11f + 12f * 15f,
      9f * 4f + 10f * 8f + 11f * 12f + 12f * 16f,
      13f * 1f + 14f * 5f + 15f * 9f + 16f * 13f,
      13f * 2f + 14f * 6f + 15f * 10f + 16f * 14f,
      13f * 3f + 14f * 7f + 15f * 11f + 16f * 15f,
      13f * 4f + 14f * 8f + 15f * 12f + 16f * 16f,
    )
  }

  @Test
  fun `should multiplyAssign matrices with scalar`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    matrix *= 3f

    assertMatrixEquals(
      matrix,
      +3f,
      +2f,
      +3f,
      +4f,
      +5f,
      18f,
      +7f,
      +8f,
      +9f,
      10f,
      33f,
      12f,
      13f,
      14f,
      15f,
      16f,
    )
  }

  @Test
  fun `should multiplyAssign matrices with Vector3 scalar`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    matrix *= vec3(3f, 4f, 5f)

    assertMatrixEquals(
      matrix,
      +3f,
      +2f,
      +3f,
      +4f,
      +5f,
      24f,
      +7f,
      +8f,
      +9f,
      10f,
      55f,
      12f,
      13f,
      14f,
      15f,
      16f,
    )
  }

  @Test
  fun `should multiplyAssign Vector3 with matrices`() {
    val vector = vec3(1f, 2f, 3f)

    vector *=
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    assertEquals(1f * 1f + 2f * 2f + 3f * 3f + 1f * 4f, vector.x, floatTolerance)
    assertEquals(1f * 5f + 2f * 6f + 3f * 7f + 1f * 8f, vector.y, floatTolerance)
    assertEquals(1f * 9f + 2f * 10f + 3f * 11f + 1f * 12f, vector.z, floatTolerance)
  }

  @Test
  fun `should add matrices`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val result =
      matrix +
        mat4(
          +1f,
          +2f,
          +3f,
          +4f,
          +5f,
          +6f,
          +7f,
          +8f,
          +9f,
          10f,
          11f,
          12f,
          13f,
          14f,
          15f,
          16f,
        )

    assertMatrixEquals(
      result,
      +2f,
      +4f,
      +6f,
      +8f,
      10f,
      12f,
      14f,
      16f,
      18f,
      20f,
      22f,
      24f,
      26f,
      28f,
      30f,
      32f,
    )
  }

  @Test
  fun `should subtract matrices`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val result =
      matrix -
        mat4(
          16f,
          15f,
          14f,
          13f,
          12f,
          11f,
          10f,
          +9f,
          +8f,
          +7f,
          +6f,
          +5f,
          +4f,
          +3f,
          +2f,
          +1f,
        )

    assertMatrixEquals(
      result,
      -15f,
      -13f,
      -11f,
      -9f,
      -7f,
      -5.0f,
      -3f,
      -1f,
      1.0f,
      3.0f,
      5.0f,
      7f,
      9.0f,
      11.0f,
      13f,
      15f,
    )
  }

  @Test
  fun `should multiply matrices`() {
    GdxNativesLoader.load() // Matrix.mul4 is written with native code.

    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val result =
      matrix *
        mat4(
          +1f,
          +2f,
          +3f,
          +4f,
          +5f,
          +6f,
          +7f,
          +8f,
          +9f,
          10f,
          11f,
          12f,
          13f,
          14f,
          15f,
          16f,
        )

    assertMatrixEquals(
      result,
      1f * 1f + 2f * 5f + 3f * 9f + 4f * 13f,
      1f * 2f + 2f * 6f + 3f * 10f + 4f * 14f,
      1f * 3f + 2f * 7f + 3f * 11f + 4f * 15f,
      1f * 4f + 2f * 8f + 3f * 12f + 4f * 16f,
      5f * 1f + 6f * 5f + 7f * 9f + 8f * 13f,
      5f * 2f + 6f * 6f + 7f * 10f + 8f * 14f,
      5f * 3f + 6f * 7f + 7f * 11f + 8f * 15f,
      5f * 4f + 6f * 8f + 7f * 12f + 8f * 16f,
      9f * 1f + 10f * 5f + 11f * 9f + 12f * 13f,
      9f * 2f + 10f * 6f + 11f * 10f + 12f * 14f,
      9f * 3f + 10f * 7f + 11f * 11f + 12f * 15f,
      9f * 4f + 10f * 8f + 11f * 12f + 12f * 16f,
      13f * 1f + 14f * 5f + 15f * 9f + 16f * 13f,
      13f * 2f + 14f * 6f + 15f * 10f + 16f * 14f,
      13f * 3f + 14f * 7f + 15f * 11f + 16f * 15f,
      13f * 4f + 14f * 8f + 15f * 12f + 16f * 16f,
    )
  }

  @Test
  fun `should multiply matrices with scalar`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val result = matrix * 3f

    assertMatrixEquals(
      result,
      +3f,
      +2f,
      +3f,
      +4f,
      +5f,
      18f,
      +7f,
      +8f,
      +9f,
      10f,
      33f,
      12f,
      13f,
      14f,
      15f,
      16f,
    )
  }

  @Test
  fun `should multiply matrices with Vector3`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val result = matrix * vec3(3f, 4f, 5f)

    assertEquals(3f * 1f + 4f * 2f + 5f * 3f + 1f * 4f, result.x, floatTolerance)
    assertEquals(3f * 5f + 4f * 6f + 5f * 7f + 1f * 8f, result.y, floatTolerance)
    assertEquals(3f * 9f + 4f * 10f + 5f * 11f + 1f * 12f, result.z, floatTolerance)
  }

  @Test
  fun `should destruct matrices into sixteen floats`() {
    val matrix =
      mat4(
        +1f,
        +2f,
        +3f,
        +4f,
        +5f,
        +6f,
        +7f,
        +8f,
        +9f,
        10f,
        11f,
        12f,
        13f,
        14f,
        15f,
        16f,
      )

    val (
      x0y0, x0y1, x0y2, x0y3,
      x1y0, x1y1, x1y2, x1y3,
      x2y0, x2y1, x2y2, x2y3,
      x3y0, x3y1, x3y2, x3y3,
    ) = matrix

    assertEquals(1f, x0y0, floatTolerance)
    assertEquals(2f, x0y1, floatTolerance)
    assertEquals(3f, x0y2, floatTolerance)
    assertEquals(4f, x0y3, floatTolerance)

    assertEquals(5f, x1y0, floatTolerance)
    assertEquals(6f, x1y1, floatTolerance)
    assertEquals(7f, x1y2, floatTolerance)
    assertEquals(8f, x1y3, floatTolerance)

    assertEquals(9f, x2y0, floatTolerance)
    assertEquals(10f, x2y1, floatTolerance)
    assertEquals(11f, x2y2, floatTolerance)
    assertEquals(12f, x2y3, floatTolerance)

    assertEquals(13f, x3y0, floatTolerance)
    assertEquals(14f, x3y1, floatTolerance)
    assertEquals(15f, x3y2, floatTolerance)
    assertEquals(16f, x3y3, floatTolerance)
  }

  private fun assertMatrixEquals(
    matrix: Matrix4,
    m00: Float,
    m01: Float,
    m02: Float,
    m03: Float,
    m10: Float,
    m11: Float,
    m12: Float,
    m13: Float,
    m20: Float,
    m21: Float,
    m22: Float,
    m23: Float,
    m30: Float,
    m31: Float,
    m32: Float,
    m33: Float,
    tolerance: Float = floatTolerance,
  ) {
    val values = matrix.`val`
    assertEquals(m00, values[Matrix4.M00], tolerance)
    assertEquals(m01, values[Matrix4.M01], tolerance)
    assertEquals(m02, values[Matrix4.M02], tolerance)
    assertEquals(m03, values[Matrix4.M03], tolerance)
    assertEquals(m10, values[Matrix4.M10], tolerance)
    assertEquals(m11, values[Matrix4.M11], tolerance)
    assertEquals(m12, values[Matrix4.M12], tolerance)
    assertEquals(m13, values[Matrix4.M13], tolerance)
    assertEquals(m20, values[Matrix4.M20], tolerance)
    assertEquals(m21, values[Matrix4.M21], tolerance)
    assertEquals(m22, values[Matrix4.M22], tolerance)
    assertEquals(m23, values[Matrix4.M23], tolerance)
    assertEquals(m30, values[Matrix4.M30], tolerance)
    assertEquals(m31, values[Matrix4.M31], tolerance)
    assertEquals(m32, values[Matrix4.M32], tolerance)
    assertEquals(m33, values[Matrix4.M33], tolerance)
  }
}
