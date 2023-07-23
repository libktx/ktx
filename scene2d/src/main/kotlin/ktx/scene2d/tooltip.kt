package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Adds a new [TextTooltip] to this actor.
 * @param text will be displayed on the tooltip.
 * @param style name of the style of the tooltip. Defaults to [defaultStyle].
 * @param skin [Skin] instance which contains the style. Defaults to [Scene2DSkin.defaultSkin].
 * @param tooltipManager manages tooltips and their settings. Defaults to the global manager obtained with
 * [TooltipManager.getInstance].
 * @param init inlined building block, which allows to manage [Label] actor of the [TextTooltip]. Takes the
 * [TextTooltip] as its parameter, so it can be modified with the *it* reference as well. See usage examples.
 * @return a new [TextTooltip] instance added to the actor.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun Actor.textTooltip(
  text: String,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  tooltipManager: TooltipManager = TooltipManager.getInstance(),
  init: (@Scene2dDsl Label).(TextTooltip) -> Unit = {},
): TextTooltip {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val tooltip = TextTooltip(text, tooltipManager, skin, style)
  tooltip.actor.init(tooltip)
  this.addListener(tooltip)
  return tooltip
}

/**
 * Adds a new [Tooltip] to this actor, storing a flexible [Table] widget.
 * @param background optional name of a drawable which will be extracted from the [Skin] and set as the main table's
 * background. Defaults to null.
 * @param skin [Skin] instance which contains the background. Will be passed to the [Table].
 * @param tooltipManager manages tooltips and their settings. Defaults to the global manager obtained with
 * [TooltipManager.getInstance].
 * @param init inlined building block, which allows to manage main [Table] content and fill it with children. Takes the
 * [Tooltip] as its parameter, so it can be modified with the *it* reference. See usage examples.
 * @return a new [Tooltip] instance added to this actor.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun Actor.tooltip(
  background: String? = null,
  skin: Skin = Scene2DSkin.defaultSkin,
  tooltipManager: TooltipManager = TooltipManager.getInstance(),
  init: KTableWidget.(Tooltip<KTableWidget>) -> Unit = {},
): Tooltip<KTableWidget> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val table = KTableWidget(skin)
  if (background != null) {
    table.setBackground(background)
  }
  val tooltip = Tooltip(table, tooltipManager)
  table.init(tooltip)
  this.addListener(tooltip)
  return tooltip
}
