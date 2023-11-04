package ktx.math

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests [Vector4]-related utilities.
 */
class Vector4Test {
  private val floatTolerance = 0.00001f

  @Test
  fun `should create vector with default values`() {
    val zero = vec4()

    assertEquals(0f, zero.x, floatTolerance)
    assertEquals(0f, zero.y, floatTolerance)
    assertEquals(0f, zero.z, floatTolerance)
    assertEquals(0f, zero.w, floatTolerance)
  }

  @Test
  fun `should create vector`() {
    val vector = vec4(x = 10f, y = -10f, z = 5f, w = -5f)

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
    assertEquals(-5f, vector.w, floatTolerance)
  }

  @Test
  fun `should create Vector4 with Vector2 instance`() {
    val vector = vec4(Vector2(10f, 5f))

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(0f, vector.z, floatTolerance)
    assertEquals(0f, vector.w, floatTolerance)
  }

  @Test
  fun `should create Vector4 with Vector2 instance and ZW components`() {
    val vector = vec4(Vector2(10f, 5f), z = -15f, w = -5f)

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(-15f, vector.z, floatTolerance)
    assertEquals(-5f, vector.w, floatTolerance)
  }

  @Test
  fun `should create Vector4 with two Vector2 instances`() {
    val vector = vec4(xy = Vector2(10f, 5f), zw = Vector2(20f, 30f))

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    assertEquals(30f, vector.w, floatTolerance)
  }

  @Test
  fun `should create Vector4 with Vector3 instance`() {
    val vector = vec4(Vector3(10f, 5f, -5f))

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(-5f, vector.z, floatTolerance)
    assertEquals(0f, vector.w, floatTolerance)
  }

  @Test
  fun `should create Vector4 with Vector3 instance and W component`() {
    val vector = vec4(Vector3(10f, 5f, -15f), w = -5f)

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(-15f, vector.z, floatTolerance)
    assertEquals(-5f, vector.w, floatTolerance)
  }

  @Test
  fun `should invert values with unary - operator`() {
    val originalVector = Vector4(10f, 15f, -10f, -5f)

    val vector = -originalVector

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-15f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    assertEquals(5f, vector.w, floatTolerance)
    assertEquals(10f, originalVector.x, floatTolerance)
    assertEquals(15f, originalVector.y, floatTolerance)
    assertEquals(-10f, originalVector.z, floatTolerance)
    assertEquals(-5f, originalVector.w, floatTolerance)
  }

  @Test
  fun `should add vectors with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector += Vector4(20f, -20f, -10f, -5f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(0f, vector.z, floatTolerance)
    assertEquals(5f, vector.w, floatTolerance)
  }

  @Test
  fun `should add Vector2 with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector += Vector2(20f, -20f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    assertEquals(10f, vector.w, floatTolerance)
  }

  @Test
  fun `should add Vector3 with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector += Vector3(20f, -20f, 5f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(15f, vector.z, floatTolerance)
    assertEquals(10f, vector.w, floatTolerance)
  }

  @Test
  fun `should add floats to vectors with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector += 10f

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    assertEquals(20f, vector.w, floatTolerance)
  }

  @Test
  fun `should add ints to vectors with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector += 10

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    assertEquals(20f, vector.w, floatTolerance)
  }

  @Test
  fun `should subtract vectors with -= operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector -= Vector4(20f, -20f, -10f, 5f)

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    assertEquals(5f, vector.w, floatTolerance)
  }

  @Test
  fun `should subtract Vector2 with -= operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector -= Vector2(20f, -20f)

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
    assertEquals(10f, vector.w, floatTolerance)
  }

  @Test
  fun `should subtract Vector3 with -= operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector -= Vector3(20f, -20f, 5f)

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
    assertEquals(10f, vector.w, floatTolerance)
  }

  @Test
  fun `should subtract floats from vectors with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector -= 20f

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(-10f, vector.z, floatTolerance)
    assertEquals(-10f, vector.w, floatTolerance)
  }

  @Test
  fun `should subtract ints from vectors with += operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector -= 20f

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(-10f, vector.z, floatTolerance)
    assertEquals(-10f, vector.w, floatTolerance)
  }

  @Test
  fun `should multiply vectors with timesAssign operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector *= Vector4(3f, -1f, 0.5f, 2f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
    assertEquals(20f, vector.w, floatTolerance)
  }

  @Test
  fun `should multiply vectors by float scalars with timesAssign operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector *= 2.5f

    assertEquals(25f, vector.x, floatTolerance)
    assertEquals(25f, vector.y, floatTolerance)
    assertEquals(25f, vector.z, floatTolerance)
    assertEquals(25f, vector.w, floatTolerance)
  }

  @Test
  fun `should multiply vectors by int scalars with timesAssign operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector *= 2

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    assertEquals(20f, vector.w, floatTolerance)
  }

  @Test
  fun `should divide vectors with divAssign operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector /= Vector4(2f, -5f, 0.5f, 0.25f)

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(-2f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
    assertEquals(40f, vector.w, floatTolerance)
  }

  @Test
  fun `should divide vectors by float scalars with divAssign operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector /= 2.5f

    assertEquals(4f, vector.x, floatTolerance)
    assertEquals(4f, vector.y, floatTolerance)
    assertEquals(4f, vector.z, floatTolerance)
    assertEquals(4f, vector.w, floatTolerance)
  }

  @Test
  fun `should divide vectors by int scalars with divAssign operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    vector /= 2

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
    assertEquals(5f, vector.w, floatTolerance)
  }

  @Test
  fun `should add vectors with + operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector + Vector4(20f, -20f, -10f, 5f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(0f, result.z, floatTolerance)
    assertEquals(15f, result.w, floatTolerance)
  }

  @Test
  fun `should add Vector2 with + operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector + Vector2(20f, -20f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(10f, result.z, floatTolerance)
    assertEquals(10f, result.w, floatTolerance)
  }

  @Test
  fun `should add Vector3 with + operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector + Vector3(20f, -20f, 5f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(15f, result.z, floatTolerance)
    assertEquals(10f, result.w, floatTolerance)
  }

  @Test
  fun `should add vectors and floats with + operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector + 10f

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
    assertEquals(20f, result.w, floatTolerance)
  }

  @Test
  fun `should add vectors and ints with + operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector + 10

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
    assertEquals(20f, result.w, floatTolerance)
  }

  @Test
  fun `should subtract vectors with - operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector - Vector4(20f, -20f, -10f, 5f)

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(30f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
    assertEquals(5f, result.w, floatTolerance)
  }

  @Test
  fun `should subtract Vector2 with - operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector - Vector2(20f, -20f)

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(30f, result.y, floatTolerance)
    assertEquals(10f, result.z, floatTolerance)
    assertEquals(10f, result.w, floatTolerance)
  }

  @Test
  fun `should subtract Vector3 with - operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector - Vector3(20f, -20f, 5f)

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(30f, result.y, floatTolerance)
    assertEquals(5f, result.z, floatTolerance)
    assertEquals(10f, result.w, floatTolerance)
  }

  @Test
  fun `should subtract vectors and floats with - operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector - 20f

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(-10f, result.z, floatTolerance)
    assertEquals(-10f, result.w, floatTolerance)
  }

  @Test
  fun `should subtract vectors and ints with - operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector - 20

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(-10f, result.z, floatTolerance)
    assertEquals(-10f, result.w, floatTolerance)
  }

  @Test
  fun `should multiply vectors with times operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector * Vector4(3f, -1f, 0.5f, 2f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(5f, result.z, floatTolerance)
    assertEquals(20f, result.w, floatTolerance)
  }

  @Test
  fun `should divide vectors with div operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector / Vector4(2f, -5f, 0.5f, 2.5f)

    assertEquals(5f, result.x, floatTolerance)
    assertEquals(-2f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
    assertEquals(4f, result.w, floatTolerance)
  }

  @Test
  fun `should multiply vectors by int scalars with times operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector * 2

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
    assertEquals(20f, result.w, floatTolerance)
  }

  @Test
  fun `should divide vectors by int scalars with div operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector / 2

    assertEquals(5f, result.x, floatTolerance)
    assertEquals(5f, result.y, floatTolerance)
    assertEquals(5f, result.z, floatTolerance)
    assertEquals(5f, result.w, floatTolerance)
  }

  @Test
  fun `should multiply vectors by float scalars with times operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector * 2.5f

    assertEquals(25f, result.x, floatTolerance)
    assertEquals(25f, result.y, floatTolerance)
    assertEquals(25f, result.z, floatTolerance)
    assertEquals(25f, result.w, floatTolerance)
  }

  @Test
  fun `should divide vectors by float scalars with div operator`() {
    val vector = Vector4(10f, 10f, 10f, 10f)

    val result = vector / 2.5f

    assertEquals(4f, result.x, floatTolerance)
    assertEquals(4f, result.y, floatTolerance)
    assertEquals(4f, result.z, floatTolerance)
    assertEquals(4f, result.w, floatTolerance)
  }

  @Test
  fun `should increment vector values with ++ operator`() {
    val originalVector = Vector4(10f, 10f, 10f, 10f)
    var vector = originalVector

    vector++

    assertEquals(11f, vector.x, floatTolerance)
    assertEquals(11f, vector.y, floatTolerance)
    assertEquals(11f, vector.z, floatTolerance)
    assertEquals(11f, vector.w, floatTolerance)
    assertEquals(10f, originalVector.x, floatTolerance)
    assertEquals(10f, originalVector.y, floatTolerance)
    assertEquals(10f, originalVector.z, floatTolerance)
    assertEquals(10f, originalVector.w, floatTolerance)
  }

  @Test
  fun `should decrement vector values with -- operator`() {
    val originalVector = Vector4(10f, 10f, 10f, 10f)
    var vector = originalVector

    vector--

    assertEquals(9f, vector.x, floatTolerance)
    assertEquals(9f, vector.y, floatTolerance)
    assertEquals(9f, vector.z, floatTolerance)
    assertEquals(9f, vector.w, floatTolerance)
    assertEquals(10f, originalVector.x, floatTolerance)
    assertEquals(10f, originalVector.y, floatTolerance)
    assertEquals(10f, originalVector.z, floatTolerance)
    assertEquals(10f, originalVector.w, floatTolerance)
  }

  @Test
  fun `should destruct vector into three floats`() {
    val (x, y, z, w) = Vector4(10f, 20f, 30f, 40f)

    assertEquals(10f, x, floatTolerance)
    assertEquals(20f, y, floatTolerance)
    assertEquals(30f, z, floatTolerance)
    assertEquals(40f, w, floatTolerance)
  }

  @Test
  fun `should compare vectors by length`() {
    val vec1 = Vector4(10f, 10f, 10f, 10f)
    val vec2 = Vector4(10f, -20f, 10f, -10f) // This vector has the greatest overall length.
    val vec3 = Vector4(10f, 10f, 10f, -10f)

    assertTrue(vec1 < vec2)
    assertTrue(vec1 <= vec2)
    assertFalse(vec1 > vec2)
    assertFalse(vec1 >= vec2)

    assertTrue(vec1 >= vec3)
    assertTrue(vec1 <= vec3)
    assertFalse(vec1 < vec3)
    assertFalse(vec1 > vec3)
    assertTrue(vec1 == vec3.apply { w = -w })
  }

  @Test
  fun `should calculate the dot product of two vectors`() {
    val vec1 = Vector4(2f, 1f, 3f, 4f)
    val vec2 = Vector4(3f, -4f, -3f, 2f)

    val result = vec1 dot vec2

    assertEquals(1f, result, floatTolerance)
  }
}
