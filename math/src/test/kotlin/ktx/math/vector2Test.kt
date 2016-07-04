package ktx.math

import com.badlogic.gdx.math.Vector2
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Vector2]-related utilities.
 * @author MJ
 */
class Vector2Test {
  val floatTolerance = 0.00001f

  @Test
  fun shouldCreateVectors() {
    val zero = vec2()
    assertNotNull(zero)
    assertEquals(0f, zero.x, floatTolerance)
    assertEquals(0f, zero.y, floatTolerance)

    val vector = vec2(x = 10f, y = -10f)
    assertNotNull(vector)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun shouldInvertValuesWithUnaryMinusOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    -vector
    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun shouldAddVectorsWithPlusOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector + Vector2(20f, -20f)
    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun shouldSubtractVectorsWithMinusOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector - Vector2(20f, -20f)
    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
  }

  @Test
  fun shouldMultiplyVectorsWithTimesOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector * Vector2(3f, -1f)
    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun shouldDivideVectorsWithDivOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector / Vector2(2f, -5f)
    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(-2f, vector.y, floatTolerance)
  }

  @Test
  fun shouldMultiplyVectorsByScalarsWithTimesOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector * 2
    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
    vector * 3f
    assertEquals(60f, vector.x, floatTolerance)
    assertEquals(60f, vector.y, floatTolerance)
  }

  @Test
  fun shouldDivideVectorsByScalarWithDivOperator() {
    val vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector / 2
    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    vector / 2.5f
    assertEquals(2f, vector.x, floatTolerance)
    assertEquals(2f, vector.y, floatTolerance)
  }

  @Test
  fun shouldIncrementVector() {
    var vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector++
    assertEquals(11f, vector.x, floatTolerance)
    assertEquals(11f, vector.y, floatTolerance)
  }

  @Test
  fun shouldDecrementVector() {
    var vector = Vector2(10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector--
    assertEquals(9f, vector.x, floatTolerance)
    assertEquals(9f, vector.y, floatTolerance)
  }

  @Test
  fun shouldDestructVectorIntoTwoFloats() {
    val (x, y) = Vector2(10f, 10f)
    assertEquals(10f, x, floatTolerance)
    assertEquals(10f, y, floatTolerance)
  }

  @Test
  fun shouldCompareVectors() {
    val vec1 = Vector2(10f, 10f)
    val vec2 = Vector2(10f, -20f) // This vector has the greatest overall length.
    val vec3 = Vector2(10f, 10f)

    assertTrue(vec1 < vec2)
    assertTrue(vec1 <= vec2)
    assertFalse(vec1 > vec2)
    assertFalse(vec1 >= vec2)

    assertTrue(vec1 == vec3) // Actually, this one uses equals, not compareTo.
    assertTrue(vec1 >= vec3)
    assertTrue(vec1 <= vec3)
    assertFalse(vec1 < vec3)
    assertFalse(vec1 > vec3)
  }
}
