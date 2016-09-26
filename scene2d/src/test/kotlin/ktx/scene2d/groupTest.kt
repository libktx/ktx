package ktx.scene2d

import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GroupFactoriesTest : NeedsLibGDX() {
  @Before
  fun setDefaultSkin() {
    Scene2DSkin.defaultSkin = VisUI.getSkin()
  }

  @Test
  fun shouldCreateStacks() {
    val widget = stack {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateHorizontalGroups() {
    val widget = horizontalGroup {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateVerticalGroups() {
    val widget = verticalGroup {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateContainers() {
    val widget = container {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateSplitPanes() {
    val widget = splitPane {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun shouldCreateScrollPanes() {
    val widget = scrollPane {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }
}