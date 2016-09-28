package ktx.scene2d

import com.kotcrab.vis.ui.VisUI
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests inlined factory methods of Tree-based root actors.
 * @author MJ
 */
class TreeFactoryTest : NeedsLibGDX() {
  @Before
  fun setDefaultSkin() {
    Scene2DSkin.defaultSkin = VisUI.getSkin()
  }

  @Test
  fun shouldCreateTables() {
    val widget = tree {
      height = 100f
    }
    Assert.assertNotNull(widget)
    Assert.assertEquals(100f, widget.height, TOLERANCE)
  }
}