package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests extension methods that allow to add [Tooltip] instances to all actors.
 * @author MJ
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
      // TextTooltip should be available as lambda parameter:
      assertTrue(it is TextTooltip)
      // Lambda should be invoked directly on the TextTooltip child:
      assertTrue(this is Label)
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
      // Tooltip should be available as lambda parameter:
      assertTrue(it is Tooltip)
      // Lambda should be invoked directly on the Tooltip child:
      assertTrue(this is Table)
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
