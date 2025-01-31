package ktx.math

import com.badlogic.gdx.math.Circle
import org.junit.Assert.assertEquals
import org.junit.Test

class CircleTest {
  @Test
  fun `should destructure Circle`() {
    val circle = Circle(1f, 2f, 3f)

    val (x, y, radius) = circle

    assertEquals(1f, x)
    assertEquals(2f, y)
    assertEquals(3f, radius)
  }
}
