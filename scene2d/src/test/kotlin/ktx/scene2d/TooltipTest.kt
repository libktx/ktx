package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests extension methods that allow to add [Tooltip] instances to all actors.
 */
class TooltipTest : ApplicationTest() {
  @Test
  fun `should add TextTooltip`() {
    val actor = Actor()

    val tooltip = actor.textTooltip("Test.")

    assertNotNull(tooltip)
    assertTrue(tooltip in actor.listeners)
    assertEquals("Test.", tooltip.actor.text.toString())
  }

  @Test
  fun `should add TextTooltip with init block`() {
    val actor = Actor()
    val variable: Int

    val tooltip =
      actor.textTooltip("Test.") {
        // Changing Label color:
        color = Color.BLUE
        variable = 42
      }

    assertNotNull(tooltip)
    assertTrue(tooltip in actor.listeners)
    assertEquals("Test.", tooltip.actor.text.toString())
    assertEquals(Color.BLUE, tooltip.actor.color)
    assertEquals(42, variable)
  }

  @Test
  fun `should add Tooltip with init block`() {
    val actor = Actor()
    val variable: Int

    val tooltip =
      actor.tooltip {
        // Changing Table color:
        color = Color.BLUE
        // Adding child to Table content:
        label("Test.")
        variable = 42
      }

    assertNotNull(tooltip)
    assertTrue(tooltip in actor.listeners)
    assertEquals("Test.", (tooltip.actor.children.first() as Label).text.toString())
    assertEquals(Color.BLUE, tooltip.actor.color)
    assertEquals(42, variable)
  }
}
