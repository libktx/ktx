package ktx.scene2d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests inlined factory methods of simple WidgetGroup-based root actors.
 * @author MJ
 */
class GroupFactoriesTest : NeedsLibGDX() {
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