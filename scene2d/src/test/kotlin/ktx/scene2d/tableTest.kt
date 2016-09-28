package ktx.scene2d

import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests inlined factory methods of Table-based root actors.
 * @author MJ
 */
class TableFactoriesTest : NeedsLibGDX() {
  @Before
  fun setDefaultSkin() {
    Scene2DSkin.defaultSkin = VisUI.getSkin()
  }

  @Test
  fun shouldCreateTables() {
    val widget = table {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(widget.skin, Scene2DSkin.defaultSkin)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateWindows() {
    val widget = window(title = "test") {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateDialogs() {
    val widget = dialog(title = "test") {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals("test", widget.titleLabel.text.toString())
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateButtonGroups() {
    val widget = buttonGroup(minCheckedCount = 1, maxCheckedCount = 1) {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(Scene2DSkin.defaultSkin, widget.skin)
    assertEquals(100f, widget.height, TOLERANCE)
    // Checked counts are not accessible through public API.
  }
}