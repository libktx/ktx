package ktx.math

import com.badlogic.gdx.math.Vector2
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Vector2]-related utilities.
 */
class Vector2Test {
  private val floatTolerance = 0.00001f

  @Test
  fun `should create vector with default values`() {
    val vector = vec2()

    assertEquals(0f, vector.x, floatTolerance)
    assertEquals(0f, vector.y, floatTolerance)
  }

  @Test
  fun `should create vector`() {
    val vector = vec2(x = 10f, y = -10f)

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun `should invert values with unary - operator`() {
    val vector = Vector2(10f, 10f)

    val result = -vector

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(10f, vector.y, floatTolerance)
  }

  @Test
  fun `should add vectors with += operator`() {
    val vector = Vector2(10f, 10f)

    vector += Vector2(20f, -20f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun `should add floats with += operator`() {
    val vector = Vector2(10f, 10f)

    vector += 10f

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
  }

  @Test
  fun `should add ints with += operator`() {
    val vector = Vector2(10f, 10f)

    vector += 10

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
  }

  @Test
  fun `should subtract vectors with -= operator`() {
    val vector = Vector2(10f, 10f)

    vector -= Vector2(20f, -20f)

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
  }

  @Test
  fun `should subtract floats with -= operator`() {
    val vector = Vector2(10f, 10f)

    vector -= 20f

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun `should subtract ints with -= operator`() {
    val vector = Vector2(10f, 10f)

    vector -= 5

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
  }

  @Test
  fun `should multiply vectors with *= operator`() {
    val vector = Vector2(10f, 10f)

    vector *= Vector2(3f, -1f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
  }

  @Test
  fun `should divide vectors with divAssign operator`() {
    val vector = Vector2(10f, 10f)

    vector /= Vector2(2f, -5f)

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(-2f, vector.y, floatTolerance)
  }

  @Test
  fun `should multiply vectors by int scalars with *= operator`() {
    val vector = Vector2(10f, 10f)

    vector *= 2

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
  }

  @Test
  fun `should divide vectors by int scalars with divAssign operator`() {
    val vector = Vector2(10f, 10f)

    vector /= 2

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
  }

  @Test
  fun `should multiply vectors by float scalars with *= operator`() {
    val vector = Vector2(10f, 10f)

    vector *= 2.5f

    assertEquals(25f, vector.x, floatTolerance)
    assertEquals(25f, vector.y, floatTolerance)
  }

  @Test
  fun `should divide vectors by float scalars with divAssign operator`() {
    val vector = Vector2(10f, 10f)

    vector /= 2.5f

    assertEquals(4f, vector.x, floatTolerance)
    assertEquals(4f, vector.y, floatTolerance)
  }

  @Test
  fun `should add vectors with + operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector + Vector2(20f, -20f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
  }

  @Test
  fun `should add vector and float with + operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector + 10f

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
  }

  @Test
  fun `should add vector and int with + operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector + 10

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
  }

  @Test
  fun `should subtract vectors with - operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector - Vector2(20f, -20f)

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(30f, result.y, floatTolerance)
  }

  @Test
  fun `should subtract vector and float with - operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector - 20f

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
  }

  @Test
  fun `should subtract vector and int with - operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector - 20

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
  }

  @Test
  fun `should multiply vectors with * operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector * Vector2(3f, -1f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
  }

  @Test
  fun `should multiply vectors by float scalars with * operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector * 2.5f

    assertEquals(25f, result.x, floatTolerance)
    assertEquals(25f, result.y, floatTolerance)
  }

  @Test
  fun `should multiply vectors by int scalars with * operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector * 2

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
  }

  @Test
  fun `should divide vectors with div operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector / Vector2(2f, -5f)

    assertEquals(5f, result.x, floatTolerance)
    assertEquals(-2f, result.y, floatTolerance)
  }

  @Test
  fun `should divide vectors by int scalars with div operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector / 2

    assertEquals(5f, result.x, floatTolerance)
    assertEquals(5f, result.y, floatTolerance)
  }

  @Test
  fun `should divide vectors by float scalars with div operator`() {
    val vector = Vector2(10f, 10f)

    val result = vector / 2.5f

    assertEquals(4f, result.x, floatTolerance)
    assertEquals(4f, result.y, floatTolerance)
  }

  @Test
  fun `should increment vector values with ++ operator`() {
    val originalVector = Vector2(10f, 10f)
    var vector = originalVector

    vector++

    assertEquals(11f, vector.x, floatTolerance)
    assertEquals(11f, vector.y, floatTolerance)
    assertEquals(10f, originalVector.x, floatTolerance)
    assertEquals(10f, originalVector.y, floatTolerance)
  }

  @Test
  fun `should decrement vector values with -- operator`() {
    val originalVector = Vector2(10f, 10f)
    var vector = originalVector

    vector--

    assertEquals(9f, vector.x, floatTolerance)
    assertEquals(9f, vector.y, floatTolerance)
    assertEquals(10f, originalVector.x, floatTolerance)
    assertEquals(10f, originalVector.y, floatTolerance)
  }

  @Test
  fun `should destruct vector into two floats`() {
    val (x, y) = Vector2(10f, 10f)

    assertEquals(10f, x, floatTolerance)
    assertEquals(10f, y, floatTolerance)
  }

  @Test
  fun `should compare vectors by length`() {
    val vec1 = Vector2(10f, 10f)
    val vec2 = Vector2(10f, -20f) // This vector has the greatest overall length.
    val vec3 = Vector2(10f, 10f)

    assertTrue(vec1 < vec2)
    assertTrue(vec1 <= vec2)
    assertFalse(vec1 > vec2)
    assertFalse(vec1 >= vec2)

    assertTrue(vec1 == vec3)
    assertTrue(vec1 >= vec3)
    assertTrue(vec1 <= vec3)
    assertFalse(vec1 < vec3)
    assertFalse(vec1 > vec3)
  }

  @Test
  fun `should be the dot product of two vectors`() {
    val vec1 = Vector2(2f, 1f)
    val vec2 = Vector2(3f, -4f)

    val result = vec1 dot vec2

    assertEquals(2f, result, floatTolerance)
  }

  @Test
  fun `should be the cross product of two vectors`() {
    val vec1 = Vector2(2f, 1f)
    val vec2 = Vector2(3f, -4f)

    val result = vec1 x vec2

    assertEquals(-11f, result, floatTolerance)
  }
}
