@file:Suppress("DEPRECATION")

package ktx.scene2d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests inlined factory methods of Table-based root actors.
 */
class TableFactoriesTest : NeedsLibGDX() {
  @Test
  fun `should create Table`() {
    val widget = table()

    assertNotNull(widget)
    assertEquals(widget.skin, Scene2DSkin.defaultSkin)
  }

  @Test
  fun `should create Table with init block`() {
    val widget = table {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(widget.skin, Scene2DSkin.defaultSkin)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create Window`() {
    val widget = window(title = "test")

    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
  }

  @Test
  fun `should create Window with init block`() {
    val widget = window(title = "test") {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create Dialog`() {
    val widget = dialog(title = "test")

    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
  }

  @Test
  fun `should create Dialog with init block`() {
    val widget = dialog(title = "test") {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create ButtonGroup`() {
    val widget = buttonGroup(minCheckedCount = 1, maxCheckedCount = 1)

    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    // Checked counts are not accessible through public API.
  }

  @Test
  fun `should create ButtonGroup with init block`() {
    val widget = buttonGroup(minCheckedCount = 1, maxCheckedCount = 1) {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals(100f, widget.height, TOLERANCE)
    // Checked counts are not accessible through public API.
  }
}
