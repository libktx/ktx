package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests extension methods that allow to add [Tooltip] instances to all actors.
 */
class TooltipFactoriesTest : NeedsLibGDX() {
  @Test
  fun `should add TextTooltip`() {
    val actor = Actor()

    val tooltip = actor.addTextTooltip("Test.")

    assertNotNull(tooltip)
    assertTrue(tooltip in actor.listeners)
    assertEquals("Test.", tooltip.actor.text.toString())
  }

  @Test
  fun `should add TextTooltip with building block`() {
    val actor = Actor()

    val tooltip = actor.addTextTooltip("Test.") {
      // Changing Label color:
      color = Color.BLUE
    }

    assertNotNull(tooltip)
    assertTrue(tooltip in actor.listeners)
    assertEquals("Test.", tooltip.actor.text.toString())
    assertEquals(Color.BLUE, tooltip.actor.color)
  }

  @Test
  fun `should add Tooltip with building block`() {
    val actor = Actor()

    val tooltip = actor.addTooltip {
      // Changing Table color:
      color = Color.BLUE
      // Adding child to Table content:
      label("Test.")
    }

    assertNotNull(tooltip)
    assertTrue(tooltip in actor.listeners)
    assertEquals("Test.", (tooltip.actor.children.first() as Label).text.toString())
    assertEquals(Color.BLUE, tooltip.actor.color)
  }
}
