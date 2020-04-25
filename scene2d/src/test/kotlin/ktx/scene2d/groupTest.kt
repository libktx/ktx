@file:Suppress("DEPRECATION")

package ktx.scene2d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests inlined factory methods of simple WidgetGroup-based root actors.
 */
class GroupFactoriesTest : NeedsLibGDX() {
  @Test
  fun `should create Stack`() {
    val widget = stack()

    assertNotNull(widget)
  }

  @Test
  fun `should create Stack with init block`() {
    val widget = stack {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create HorizontalGroup`() {
    val widget = horizontalGroup()

    assertNotNull(widget)
  }

  @Test
  fun `should create HorizontalGroup with init block`() {
    val widget = horizontalGroup {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create VerticalGroup`() {
    val widget = verticalGroup()

    assertNotNull(widget)
  }

  @Test
  fun `should create VerticalGroup with init block`() {
    val widget = verticalGroup {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create Container`() {
    val widget = container()

    assertNotNull(widget)
  }

  @Test
  fun `should create Container with init block`() {
    val widget = container {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create SplitPane`() {
    val widget = splitPane()

    assertNotNull(widget)
  }

  @Test
  fun `should create SplitPane with init block`() {
    val widget = splitPane {
      height = 100f
    }
    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }

  @Test
  fun `should create ScrollPane`() {
    val widget = scrollPane()

    assertNotNull(widget)
  }

  @Test
  fun `should create ScrollPane with init block`() {
    val widget = scrollPane {
      height = 100f
    }

    assertNotNull(widget)
    assertEquals(100f, widget.height, TOLERANCE)
  }
}
