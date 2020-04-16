@file:Suppress("DEPRECATION")

package ktx.scene2d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests inlined factory methods of Tree-based root actors.
 */
class TreeFactoryTest : NeedsLibGDX() {
  @Test
  fun `should create Tree`() {
    val widget = tree()

    assertNotNull(widget)
  }

  @Test
  fun `should create Tree with init block`() {
    val widget = tree {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }
}
