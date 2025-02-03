package ktx.math

import com.badlogic.gdx.math.Polyline
import org.junit.Assert.assertEquals
import org.junit.Test

class PolylineTest {
  @Test
  fun `should destructure Polyline`() {
    val polyline = Polyline().apply { setPosition(1f, 2f) }

    val (x, y) = polyline

    assertEquals(1f, x)
    assertEquals(2f, y)
  }
}
