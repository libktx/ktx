package ktx.math

import com.badlogic.gdx.math.Matrix3
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests [Matrix3]-related utilities.
 */
class Matrix3Test {
  private val floatTolerance = 0.00001f

  @Test
  fun `should create matrix`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
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
    )
  }

  @Test
  fun `should negate matrix values`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
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
    )
  }

  @Test
  fun `should invert matrix`() {
    val matrix =
      mat3(
        1f,
        2f,
        1f,
        0f,
        1f,
        1f,
        0f,
        0f,
        1f,
      )

    val result = !matrix

    assertMatrixEquals(
      result,
      +1f,
      -2f,
      +1f,
      +0f,
      +1f,
      -1f,
      +0f,
      +0f,
      +1f,
    )
    assertMatrixEquals(
      matrix,
      1f,
      2f,
      1f,
      0f,
      1f,
      1f,
      0f,
      0f,
      1f,
    )
  }

  @Test
  fun `should addAssign matrices`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    matrix +=
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
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
    )
  }

  @Test
  fun `should subtractAssign matrices`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    matrix -=
      mat3(
        9f,
        8f,
        7f,
        6f,
        5f,
        4f,
        3f,
        2f,
        1f,
      )

    assertMatrixEquals(
      matrix,
      -8f,
      -6f,
      -4f,
      -2f,
      +0f,
      +2f,
      +4f,
      +6f,
      +8f,
    )
  }

  @Test
  fun `should multiplyAssign matrices`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    matrix *=
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    assertMatrixEquals(
      matrix,
      1f * 1f + 2f * 4f + 3f * 7f,
      1f * 2f + 2f * 5f + 3f * 8f,
      1f * 3f + 2f * 6f + 3f * 9f,
      4f * 1f + 5f * 4f + 6f * 7f,
      4f * 2f + 5f * 5f + 6f * 8f,
      4f * 3f + 5f * 6f + 6f * 9f,
      7f * 1f + 8f * 4f + 9f * 7f,
      7f * 2f + 8f * 5f + 9f * 8f,
      7f * 3f + 8f * 6f + 9f * 9f,
    )
  }

  @Test
  fun `should multiplyAssign matrices with scalar`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    matrix *= 3f

    assertMatrixEquals(
      matrix,
      3f,
      +2f,
      3f,
      4f,
      15f,
      6f,
      7f,
      +8f,
      9f,
    )
  }

  @Test
  fun `should multiply matricesAssign with Vector2 scale`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    matrix *= vec2(3f, 2f / 5f)

    assertMatrixEquals(
      matrix,
      3f,
      2f,
      3f,
      4f,
      2f,
      6f,
      7f,
      8f,
      9f,
    )
  }

  @Test
  fun `should multiplyAssign matrices with Vector3 scale`() {
    val matrix =
      mat3(
        3f,
        2f,
        3f,
        4f,
        2f,
        6f,
        7f,
        8f,
        9f,
      )

    matrix *= vec3(3f, 2f, 0f) // Last value is ignored.

    assertMatrixEquals(
      matrix,
      9f,
      2f,
      3f,
      4f,
      4f,
      6f,
      7f,
      8f,
      9f,
    )
  }

  @Test
  fun `should multiplyAssign Vector2 with matrices`() {
    val vector = vec2(1f, 2f)

    vector *=
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    assertEquals(1f * 1f + 2f * 2f + 1f * 3f, vector.x, floatTolerance)
    assertEquals(1f * 4f + 2f * 5f + 1f * 6f, vector.y, floatTolerance)
  }

  @Test
  fun `should add matrices`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    val result =
      matrix +
        mat3(
          1f,
          2f,
          3f,
          4f,
          5f,
          6f,
          7f,
          8f,
          9f,
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
    )
  }

  @Test
  fun `should subtract matrices`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    val result =
      matrix -
        mat3(
          9f,
          8f,
          7f,
          6f,
          5f,
          4f,
          3f,
          2f,
          1f,
        )

    assertMatrixEquals(
      result,
      -8f,
      -6f,
      -4f,
      -2f,
      +0f,
      +2f,
      +4f,
      +6f,
      +8f,
    )
  }

  @Test
  fun `should multiply matrices`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    val result =
      matrix *
        mat3(
          1f,
          2f,
          3f,
          4f,
          5f,
          6f,
          7f,
          8f,
          9f,
        )

    assertMatrixEquals(
      result,
      1f * 1f + 2f * 4f + 3f * 7f,
      1f * 2f + 2f * 5f + 3f * 8f,
      1f * 3f + 2f * 6f + 3f * 9f,
      4f * 1f + 5f * 4f + 6f * 7f,
      4f * 2f + 5f * 5f + 6f * 8f,
      4f * 3f + 5f * 6f + 6f * 9f,
      7f * 1f + 8f * 4f + 9f * 7f,
      7f * 2f + 8f * 5f + 9f * 8f,
      7f * 3f + 8f * 6f + 9f * 9f,
    )
  }

  @Test
  fun `should multiply matrices with scalar`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    val result = matrix * 3f

    assertMatrixEquals(
      result,
      3f,
      +2f,
      3f,
      4f,
      15f,
      6f,
      7f,
      +8f,
      9f,
    )
  }

  @Test
  fun `should multiply matrices with Vector2`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    val result = matrix * vec2(3f, 2f)

    assertEquals(3f * 1f + 2f * 2f + 1f * 3f, result.x, floatTolerance)
    assertEquals(3f * 4f + 2f * 5f + 1f * 6f, result.y, floatTolerance)
  }

  @Test
  fun `should destruct matrices into nine floats`() {
    val matrix =
      mat3(
        1f,
        2f,
        3f,
        4f,
        5f,
        6f,
        7f,
        8f,
        9f,
      )

    val (
      x0y0, x0y1, x0y2,
      x1y0, x1y1, x1y2,
      x2y0, x2y1, x2y2,
    ) = matrix

    assertEquals(1f, x0y0, floatTolerance)
    assertEquals(2f, x0y1, floatTolerance)
    assertEquals(3f, x0y2, floatTolerance)

    assertEquals(4f, x1y0, floatTolerance)
    assertEquals(5f, x1y1, floatTolerance)
    assertEquals(6f, x1y2, floatTolerance)

    assertEquals(7f, x2y0, floatTolerance)
    assertEquals(8f, x2y1, floatTolerance)
    assertEquals(9f, x2y2, floatTolerance)
  }

  private fun assertMatrixEquals(
    matrix: Matrix3,
    m00: Float,
    m01: Float,
    m02: Float,
    m10: Float,
    m11: Float,
    m12: Float,
    m20: Float,
    m21: Float,
    m22: Float,
    tolerance: Float = floatTolerance,
  ) {
    val values = matrix.`val`
    assertEquals(m00, values[Matrix3.M00], tolerance)
    assertEquals(m01, values[Matrix3.M01], tolerance)
    assertEquals(m02, values[Matrix3.M02], tolerance)
    assertEquals(m10, values[Matrix3.M10], tolerance)
    assertEquals(m11, values[Matrix3.M11], tolerance)
    assertEquals(m12, values[Matrix3.M12], tolerance)
    assertEquals(m20, values[Matrix3.M20], tolerance)
    assertEquals(m21, values[Matrix3.M21], tolerance)
    assertEquals(m22, values[Matrix3.M22], tolerance)
  }
}
