package ktx.scene2d

import org.junit.Assert
import org.junit.Test

/**
 * Tests inlined factory methods of Tree-based root actors.
 * @author MJ
 */
class TreeFactoryTest : NeedsLibGDX() {
  @Test
  fun `should create Trees`() {
    val widget = tree {
      height = 100f
    }
    Assert.assertNotNull(widget)
    Assert.assertEquals(100f, widget.height, TOLERANCE)
  }
}
