package ktx.math

import com.badlogic.gdx.math.Polygon
import org.junit.Assert.assertEquals
import org.junit.Test

class PolygonTest {
  @Test
  fun `should destructure Polygon`() {
    val polygon = Polygon().apply { setPosition(1f, 2f) }

    val (x, y) = polygon

    assertEquals(1f, x)
    assertEquals(2f, y)
  }
}
