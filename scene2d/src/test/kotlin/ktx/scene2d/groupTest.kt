package ktx.scene2d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests inlined factory methods of simple WidgetGroup-based root actors.
 */
class GroupFactoriesTest : NeedsLibGDX() {
  @Test
  fun `should create Stacks`() {
    val widget = stack {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create HorizontalGroups`() {
    val widget = horizontalGroup {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create VerticalGroups`() {
    val widget = verticalGroup {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create Containers`() {
    val widget = container {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create SplitPanes`() {
    val widget = splitPane {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create ScrollPanes`() {
    val widget = scrollPane {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }
}
