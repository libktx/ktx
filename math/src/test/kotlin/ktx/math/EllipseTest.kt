package ktx.math

import com.badlogic.gdx.math.Ellipse
import org.junit.Assert.assertEquals
import org.junit.Test

class EllipseTest {
  @Test
  fun `should destructure Ellipse`() {
    val ellipse = Ellipse(1f, 2f, 3f, 4f)

    val (x, y, w, h) = ellipse

    assertEquals(1f, x)
    assertEquals(2f, y)
    assertEquals(3f, w)
    assertEquals(4f, h)
  }
}
