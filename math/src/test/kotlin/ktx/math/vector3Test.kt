package ktx.math

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Vector3]-related utilities.
 */
class Vector3Test {
  val floatTolerance = 0.00001f

  @Test
  fun `should create vector with default values`() {
    val zero = vec3()

    assertEquals(0f, zero.x, floatTolerance)
    assertEquals(0f, zero.y, floatTolerance)
    assertEquals(0f, zero.z, floatTolerance)
  }

  @Test
  fun `should create vector`() {
    val vector = vec3(x = 10f, y = -10f, z = 5f)

    assertEquals(10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
  }

  @Test
  fun `vector s,t,b should equal to vector x,y,z`() {
    val vector = vec3(x = 10f, y = -10f, z = 5f)

    assertEquals(vector.x, vector.s, floatTolerance)
    assertEquals(vector.y, vector.t, floatTolerance)
    assertEquals(vector.z, vector.b, floatTolerance)
  }

  @Test
  fun `vector r,g,b should equal to vector x,y,z`() {
    val vector = vec3(x = 10f, y = -10f, z = 5f)

    assertEquals(vector.x, vector.r, floatTolerance)
    assertEquals(vector.y, vector.g, floatTolerance)
    assertEquals(vector.z, vector.b, floatTolerance)
  }

  @Test
  fun `vector stb should equal to vector xyz`() {
    val vector = vec3(x = 10f, y = -10f, z = 5f)

    val st = vector.st
    assertEquals(vector.x, st.x, floatTolerance)
    assertEquals(vector.y, st.y, floatTolerance)

    val sb = vector.sb
    assertEquals(vector.x, sb.x, floatTolerance)
    assertEquals(vector.z, sb.y, floatTolerance)

    val tb = vector.tb
    assertEquals(vector.y, tb.x, floatTolerance)
    assertEquals(vector.z, tb.y, floatTolerance)

    val stb = vector.stb
    assertEquals(vector.x, stb.x, floatTolerance)
    assertEquals(vector.y, stb.y, floatTolerance)
    assertEquals(vector.z, stb.z, floatTolerance)
  }

  @Test
  fun `vector rgb should equal to vector xyz`() {
    val vector = vec3(x = 10f, y = -10f, z = 5f)

    val rg = vector.rg
    assertEquals(vector.x, rg.x, floatTolerance)
    assertEquals(vector.y, rg.y, floatTolerance)

    val rb = vector.rb
    assertEquals(vector.x, rb.x, floatTolerance)
    assertEquals(vector.z, rb.y, floatTolerance)

    val gb = vector.gb
    assertEquals(vector.y, gb.x, floatTolerance)
    assertEquals(vector.z, gb.y, floatTolerance)

    val rgb = vector.rgb
    assertEquals(vector.x, rgb.x, floatTolerance)
    assertEquals(vector.y, rgb.y, floatTolerance)
    assertEquals(vector.z, rgb.z, floatTolerance)
  }

  @Test
  fun `should invert values with unary - operator`() {
    val vector = Vector3(10f, 10f, -10f)

    -vector

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
  }

  @Test
  fun `should add vectors with += operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector += Vector3(20f, -20f, -10f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(0f, vector.z, floatTolerance)
  }

  @Test
  fun `should add Vector2 with += operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector += Vector2(20f, -20f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
  }

  @Test
  fun `should subtract vectors with -= operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector -= Vector3(20f, -20f, -10f)

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
  }

  @Test
  fun `should subtract Vector2 with -= operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector -= Vector2(20f, -20f)

    assertEquals(-10f, vector.x, floatTolerance)
    assertEquals(30f, vector.y, floatTolerance)
    assertEquals(10f, vector.z, floatTolerance)
  }

  @Test
  fun `should multiply vectors with *= operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector *= Vector3(3f, -1f, 0.5f)

    assertEquals(30f, vector.x, floatTolerance)
    assertEquals(-10f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
  }

  @Test
  fun `should divide vectors with divAssign operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector /= Vector3(2f, -5f, 0.5f)

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(-2f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
  }

  @Test
  fun `should multiply vectors by int scalars with *= operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector *= 2

    assertEquals(20f, vector.x, floatTolerance)
    assertEquals(20f, vector.y, floatTolerance)
    assertEquals(20f, vector.z, floatTolerance)
  }

  @Test
  fun `should divide vectors by int scalars with divAssign operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector /= 2

    assertEquals(5f, vector.x, floatTolerance)
    assertEquals(5f, vector.y, floatTolerance)
    assertEquals(5f, vector.z, floatTolerance)
  }

  @Test
  fun `should multiply vectors by float scalars with *= operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector *= 2.5f

    assertEquals(25f, vector.x, floatTolerance)
    assertEquals(25f, vector.y, floatTolerance)
    assertEquals(25f, vector.z, floatTolerance)
  }

  @Test
  fun `should divide vectors by float scalars with divAssign operator`() {
    val vector = Vector3(10f, 10f, 10f)

    vector /= 2.5f

    assertEquals(4f, vector.x, floatTolerance)
    assertEquals(4f, vector.y, floatTolerance)
    assertEquals(4f, vector.z, floatTolerance)
  }

  @Test
  fun `should add vectors with + operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector + Vector3(20f, -20f, -10f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(0f, result.z, floatTolerance)
  }

  @Test
  fun `should add Vector2 with + operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector + Vector2(20f, -20f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(10f, result.z, floatTolerance)
  }

  @Test
  fun `should subtract vectors with - operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector - Vector3(20f, -20f, -10f)

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(30f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
  }

  @Test
  fun `should subtract Vector2 with - operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector - Vector2(20f, -20f)

    assertEquals(-10f, result.x, floatTolerance)
    assertEquals(30f, result.y, floatTolerance)
    assertEquals(10f, result.z, floatTolerance)
  }

  @Test
  fun `should multiply vectors with * operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector * Vector3(3f, -1f, 0.5f)

    assertEquals(30f, result.x, floatTolerance)
    assertEquals(-10f, result.y, floatTolerance)
    assertEquals(5f, result.z, floatTolerance)
  }

  @Test
  fun `should divide vectors with div operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector / Vector3(2f, -5f, 0.5f)

    assertEquals(5f, result.x, floatTolerance)
    assertEquals(-2f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
  }

  @Test
  fun `should multiply vectors by int scalars with * operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector * 2

    assertEquals(20f, result.x, floatTolerance)
    assertEquals(20f, result.y, floatTolerance)
    assertEquals(20f, result.z, floatTolerance)
  }

  @Test
  fun `should divide vectors by int scalars with div operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector / 2

    assertEquals(5f, result.x, floatTolerance)
    assertEquals(5f, result.y, floatTolerance)
    assertEquals(5f, result.z, floatTolerance)
  }

  @Test
  fun `should multiply vectors by float scalars with * operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector * 2.5f

    assertEquals(25f, result.x, floatTolerance)
    assertEquals(25f, result.y, floatTolerance)
    assertEquals(25f, result.z, floatTolerance)
  }

  @Test
  fun `should divide vectors by float scalars with div operator`() {
    val vector = Vector3(10f, 10f, 10f)

    val result = vector / 2.5f

    assertEquals(4f, result.x, floatTolerance)
    assertEquals(4f, result.y, floatTolerance)
    assertEquals(4f, result.z, floatTolerance)
  }

  @Test
  fun `should increment vector values with ++ operator`() {
    var vector = Vector3(10f, 10f, 10f)

    vector++

    assertEquals(11f, vector.x, floatTolerance)
    assertEquals(11f, vector.y, floatTolerance)
    assertEquals(11f, vector.z, floatTolerance)
  }

  @Test
  fun `should decrement vector values with -- operator`() {
    var vector = Vector3(10f, 10f, 10f)

    vector--

    assertEquals(9f, vector.x, floatTolerance)
    assertEquals(9f, vector.y, floatTolerance)
    assertEquals(9f, vector.z, floatTolerance)
  }

  @Test
  fun `should destruct vector into three floats`() {
    val (x, y, z) = Vector3(10f, 20f, 30f)

    assertEquals(10f, x, floatTolerance)
    assertEquals(20f, y, floatTolerance)
    assertEquals(30f, z, floatTolerance)
  }

  @Test
  fun `should compare vectors by length`() {
    val vec1 = Vector3(10f, 10f, 10f)
    val vec2 = Vector3(10f, -20f, 10f) // This vector has the greatest overall length.
    val vec3 = Vector3(10f, 10f, 10f)

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
    val vec1 = Vector3(2f, 1f, 3f)
    val vec2 = Vector3(3f, -4f, -3f)

    val result = vec1 dot vec2

    assertEquals(-7f, result, floatTolerance)
  }

  @Test
  fun `should be the cross product of two vectors`() {
    val vec1 = Vector3(2f, 1f, 3f)
    val vec2 = Vector3(3f, -4f, -3f)

    val result = vec1 x vec2

    assertEquals(9f, result.x, floatTolerance)
    assertEquals(15f, result.y, floatTolerance)
    assertEquals(-11f, result.z, floatTolerance)
  }
}
