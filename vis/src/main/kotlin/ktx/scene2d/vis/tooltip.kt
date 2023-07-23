package ktx.scene2d.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.defaultStyle
import ktx.scene2d.scene2d
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates and adds [Tooltip] to this [Actor].
 * @param content content of the displayed tooltip. Can be defined with [scene2d].
 * @param style name of the tooltip style. Defaults to [defaultStyle].
 * @param init will be invoked on the [Tooltip], allowing to customize it.
 * @return a new instance of [Tooltip] added to this [Actor].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun Actor.visTooltip(
  content: Actor,
  style: String = defaultStyle,
  init: (@Scene2dDsl Tooltip).() -> Unit = {},
): Tooltip {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val tooltip = Tooltip(style)
  tooltip.content = content
  tooltip.target = this
  tooltip.init()
  return tooltip
}

/**
 * Creates and adds [Tooltip] with a [VisLabel] to this [Actor].
 * @param text content of the [VisLabel].
 * @param textAlign allows to customize text alignment of the [VisLabel].
 * @param style name of the tooltip style. Defaults to [defaultStyle].
 * @param init will be invoked on the [Tooltip], allowing to customize it.
 * @return a new instance of [Tooltip] added to this [Actor].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun Actor.visTextTooltip(
  text: CharSequence,
  textAlign: Int = Align.center,
  style: String = defaultStyle,
  init: (@Scene2dDsl Tooltip).() -> Unit = {},
): Tooltip {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val label = VisLabel(text)
  label.setAlignment(textAlign)
  val tooltip = Tooltip(style)
  tooltip.content = label
  tooltip.target = this
  tooltip.init()
  return tooltip
}
