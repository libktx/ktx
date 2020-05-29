package ktx.scene2d.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import ktx.scene2d.NeedsLibGDX
import ktx.scene2d.TOLERANCE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TooltipsTest : NeedsLibGDX() {
  @Test
  fun `should create Tooltip`() {
    val actor = Actor()
    val content = VisTable()

    val tooltip = actor.visTooltip(content)

    assertEquals(tooltip.content, content)
  }

  @Test
  fun `should create Tooltip with init block`() {
    val actor = Actor()
    var initInvoked: Boolean

    val tooltip = actor.visTooltip(VisTable()) {
      initInvoked = true
      fadeTime = 0.5f
    }

    assertEquals(0.5f, tooltip.fadeTime, TOLERANCE)
    assertTrue(initInvoked)
  }

  @Test
  fun `should create Tooltip with Label`() {
    val actor = Actor()
    val text = "text tooltip"

    val tooltip = actor.visTextTooltip(text, Align.right)

    assertEquals(text, (tooltip.content as VisLabel).text.toString())
    assertEquals(Align.right, (tooltip.content as VisLabel).labelAlign)
  }

  @Test
  fun `should create text Tooltip with init block`() {
    val actor = Actor()
    val text = "text tooltip"
    var initInvoked: Boolean

    val tooltip = actor.visTextTooltip(text) {
      initInvoked = true
      fadeTime = 0.5f
    }

    assertEquals(0.5f, tooltip.fadeTime, TOLERANCE)
    assertEquals(text, (tooltip.content as VisLabel).text.toString())
    assertTrue(initInvoked)
  }
}
