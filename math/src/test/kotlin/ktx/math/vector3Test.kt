package ktx.math

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Vector3]-related utilities.
 * @author MJ
 */
class Vector3Test {
  val floatTolerance = 0.00001f

  @Test
  fun shouldCreateVectors() {
    val zero = vec3()
    assertNotNull(zero)
    assertEquals(0f, zero.x, floatTolerance)
    assertEquals(0f, zero.y, floatTolerance)
    assertEquals(0f, zero.z, floatTolerance)

    val vector = vec3(x = 10f, y = -10f, z = 5f)
    assertNotNull(vector)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
  }

  @Test
  fun shouldInvertValuesWithUnaryMinusOperator() {
    val vector = Vector3(10f, 10f, -10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(-10f, vector.z, floatTolerance)
    -vector
    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
  }

  @Test
  fun shouldAddVectorsWithPlusOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector + Vector3(20f, -20f, -10f)
    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(0f, vector.z, floatTolerance)
  }

  @Test
  fun shouldAddVector2WithPlusOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector + Vector2(20f, -20f)
    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
  }

  @Test
  fun shouldSubtractVectorsWithMinusOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector - Vector3(20f, -20f, -10f)
    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
  }

  @Test
  fun shouldSubtractVector2WithMinusOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector - Vector2(20f, -20f)
    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
  }

  @Test
  fun shouldMultiplyVectorsWithTimesOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector * Vector3(3f, -1f, 0.5f)
    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
  }

  @Test
  fun shouldDivideVectorsWithDivOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    vector / Vector3(2f, -5f, 0.5f)
    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(-2f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
  }

  @Test
  fun shouldMultiplyVectorsByScalarsWithTimesOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector * 2
    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    vector * 3f
    assertEquals(60f, vector.x, floatTolerance)
    assertEquals(60f, vector.y, floatTolerance)
    assertEquals(60f, vector.z, floatTolerance)
  }

  @Test
  fun shouldDivideVectorsByScalarWithDivOperator() {
    val vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector / 2
    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
    vector / 2.5f
    assertEquals(2f, vector.x, floatTolerance)
    assertEquals(2f, vector.y, floatTolerance)
    assertEquals(2f, vector.z, floatTolerance)
  }

  @Test
  fun shouldIncrementVector() {
    var vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector++
    assertEquals(11f, vector.x, floatTolerance)
    assertEquals(11f, vector.y, floatTolerance)
    assertEquals(11f, vector.z, floatTolerance)
  }

  @Test
  fun shouldDecrementVector() {
    var vector = Vector3(10f, 10f, 10f)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    vector--
    assertEquals(9f, vector.x, floatTolerance)
    assertEquals(9f, vector.y, floatTolerance)
    assertEquals(9f, vector.z, floatTolerance)
  }

  @Test
  fun shouldDestructVectorIntoThreeFloats() {
    val (x, y, z) = Vector3(10f, 20f, 30f)
    assertEquals(10f, x, floatTolerance)
    assertEquals(20f, y, floatTolerance)
    assertEquals(30f, z, floatTolerance)
  }

  @Test
  fun shouldCompareVectors() {
    val vec1 = Vector3(10f, 10f, 10f)
    val vec2 = Vector3(10f, -20f, 10f) // This vector has the greatest overall length.
    val vec3 = Vector3(10f, 10f, 10f)

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
