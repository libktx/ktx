package ktx.math

import com.badlogic.gdx.math.Rectangle
import org.junit.Assert.assertEquals
import org.junit.Test

class RectangleTest {
  @Test
  fun `should destructure Rectangle`() {
    val rect = Rectangle(1f, 2f, 3f, 4f)

    val (x, y, w, h) = rect

    assertEquals(1f, x)
    assertEquals(2f, y)
    assertEquals(3f, w)
    assertEquals(4f, h)
  }
}
