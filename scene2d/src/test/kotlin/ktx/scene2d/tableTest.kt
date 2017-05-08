package ktx.scene2d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests inlined factory methods of Table-based root actors.
 */
class TableFactoriesTest : NeedsLibGDX() {
  @Test
  fun `should create Tables`() {
    val widget = table {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(widget.skin, Scene2DSkin.defaultSkin)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create Windows`() {
    val widget = window(title = "test") {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create Dialogs`() {
    val widget = dialog(title = "test") {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create ButtonGroups`() {
    val widget = buttonGroup(minCheckedCount = 1, maxCheckedCount = 1) {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals(100f, widget.height, TOLERANCE)
    // Checked counts are not accessible through public API.
  }
}
