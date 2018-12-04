package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.VisLabel

/** @author Kotcrab */

/** Creates and adds text [Tooltip] to [Actor] */
@Deprecated("Vis tooltips are broken in VisUI 1.4.1 used against LibGDX 1.9.9.") // TODO Update VisUI.
fun Actor.addTooltip(content: Actor, styleName: String = DEFAULT_STYLE, init: (@VisDsl Tooltip).() -> Unit = {}): Tooltip {
  val tooltip = Tooltip(styleName)
  tooltip.content = content
  tooltip.target = this
  tooltip.init()
  return tooltip
}

/** Creates and adds [Tooltip] to [Actor] */
@Deprecated("Vis tooltips are broken in VisUI 1.4.1 used against LibGDX 1.9.9.") // TODO Update VisUI.
fun Actor.addTextTooltip(text: String, textAlign: Int = Align.center, styleName: String = DEFAULT_STYLE, init: (@VisDsl Tooltip).() -> Unit = {}): Tooltip {
  val label = VisLabel(text)
  label.setAlignment(textAlign)
  val tooltip = Tooltip(styleName)
  tooltip.content = label
  tooltip.target = this
  tooltip.init()
  return tooltip
}
