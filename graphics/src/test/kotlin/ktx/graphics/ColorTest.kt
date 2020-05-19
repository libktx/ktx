package ktx.graphics

import com.badlogic.gdx.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class ColorTest {
  @Test
  fun `should construct Color`() {
    val color = color(red = 0.25f, green = 0.5f, blue = 0.75f, alpha = 0.6f)

    assertEquals(0.25f, color.r)
    assertEquals(0.5f, color.g)
    assertEquals(0.75f, color.b)
    assertEquals(0.6f, color.a)
  }

  @Test
  fun `should construct Color with optional alpha`() {
    val color = color(red = 0.25f, green = 0.5f, blue = 0.75f)

    assertEquals(0.25f, color.r)
    assertEquals(0.5f, color.g)
    assertEquals(0.75f, color.b)
    assertEquals(1f, color.a)
  }

  @Test
  fun `should copy Color values`() {
    val color = Color(0.4f, 0.5f, 0.6f, 0.7f)

    val copy = color.copy()

    assertNotSame(color, copy)
    assertEquals(0.4f, copy.r)
    assertEquals(0.5f, copy.g)
    assertEquals(0.6f, copy.b)
    assertEquals(0.7f, copy.a)
    assertEquals(color, copy)
  }

  @Test
  fun `should override chosen Color values when copying`() {
    val color = Color(0.4f, 0.5f, 0.6f, 0.7f)

    val copy = color.copy(red = 0.25f, green = 0.35f, blue = 0.45f, alpha = 0.55f)

    assertNotSame(color, copy)
    assertEquals(0.25f, copy.r)
    assertEquals(0.35f, copy.g)
    assertEquals(0.45f, copy.b)
    assertEquals(0.55f, copy.a)
    assertNotEquals(color, copy)
  }

  @Test
  fun `should destruct Color into red, green, blue and alpha components`() {
    val color = Color(0.1f, 0.2f, 0.3f, 0.4f)

    val (red, green, blue, alpha) = color

    assertEquals(color.r, red)
    assertEquals(color.g, green)
    assertEquals(color.b, blue)
    assertEquals(color.a, alpha)
  }
}
