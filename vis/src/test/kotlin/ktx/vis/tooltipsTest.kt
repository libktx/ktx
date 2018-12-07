package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** @author Kotcrab */
class TooltipsTest : NeedsLibGDX() {
  @Test
  fun shouldCreateTooltip() {
    val actor = Actor()
    val content = VisTable()
    val tooltip = actor.addTooltip(content)
    assertEquals(tooltip.content, content)
  }

  @Test
  fun shouldInvokeTooltipInitializer() {
    val actor = Actor()
    var initInvoked = false
    actor.addTooltip(VisTable()) {
      initInvoked = true
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateTextTooltip() {
    val actor = Actor()
    val testText = "text tooltip"
    val tooltip = actor.addTextTooltip(testText)
    assertEquals((tooltip.content as VisLabel).text.toString(), testText)
  }

  @Test
  fun shouldCreateAlignedTextTooltip() {
    val actor = Actor()
    val testAlign = Align.right
    val tooltip = actor.addTextTooltip("", testAlign)
    assertEquals((tooltip.content as VisLabel).labelAlign, testAlign)
  }

  @Test
  fun shouldInvokeTextTooltipInitializer() {
    val actor = Actor()
    var initInvoked = false
    actor.addTextTooltip("tooltip") {
      initInvoked = true
    }
    assertTrue(initInvoked)
  }
}
