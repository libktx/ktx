package ktx.scene2d.vis

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.kotcrab.vis.ui.layout.FloatingGroup
import com.kotcrab.vis.ui.layout.FlowGroup
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.util.adapter.ListAdapter
import com.kotcrab.vis.ui.widget.BusyBar
import com.kotcrab.vis.ui.widget.ButtonBar
import com.kotcrab.vis.ui.widget.CollapsibleWidget
import com.kotcrab.vis.ui.widget.HighlightTextArea
import com.kotcrab.vis.ui.widget.HorizontalCollapsibleWidget
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.MultiSplitPane
import com.kotcrab.vis.ui.widget.ScrollableTextArea
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisList
import com.kotcrab.vis.ui.widget.VisProgressBar
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisSplitPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextArea
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisTree
import com.kotcrab.vis.ui.widget.VisValidatableTextField
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.color.BasicColorPicker
import com.kotcrab.vis.ui.widget.color.ExtendedColorPicker
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.toast.Toast
import com.kotcrab.vis.ui.widget.toast.ToastTable
import ktx.scene2d.KTable
import ktx.scene2d.KTree
import ktx.scene2d.KWidget
import ktx.scene2d.RootWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.defaultHorizontalStyle
import ktx.scene2d.defaultStyle
import ktx.scene2d.defaultVerticalStyle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Constructs a top-level [VisWindow] widget.
 * @param title will be displayed as window's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked on the [VisWindow] widget. Inlined.
 * @return a new [VisWindow] instance.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun RootWidget.visWindow(
  title: String,
  style: String = defaultStyle,
  init: KVisWindow.() -> Unit = {},
): KVisWindow {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return storeActor(KVisWindow(title, style)).apply(init)
}

/**
 * Constructs a top-level [VisDialog] widget.
 * @param title will be displayed as window's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked on the [VisDialog] widget. Inlined.
 * @return a new [VisDialog] instance.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun RootWidget.visDialog(
  title: String,
  style: String = defaultStyle,
  init: KVisDialog.() -> Unit = {},
): KVisDialog {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return storeActor(KVisDialog(title, style)).apply(init)
}

/**
 * Constructs a top-level [ToastTable] widget. Utility for constructing [Toast] instances.
 * @param defaultSpacing if true, default VisUI spacing will be applied to the table.
 * @param init will be invoked on the [ToastTable] widget. Inlined.
 * @return a new [ToastTable] instance.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun RootWidget.toastTable(
  defaultSpacing: Boolean = false,
  init: KToastTable.() -> Unit = {},
): KToastTable {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return storeActor(KToastTable(defaultSpacing)).apply(init)
}

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisLabel] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visLabel(
  text: CharSequence,
  style: String = defaultStyle,
  init: (@Scene2dDsl VisLabel).(S) -> Unit = {},
): VisLabel {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisLabel(text, style), init)
}

/**
 * @param text will be displayed on the label.
 * @param url URL that will be opened by the label. If not given, will default to [text].
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [LinkLabel] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.linkLabel(
  text: CharSequence,
  url: CharSequence = text,
  style: String = defaultStyle,
  init: (@Scene2dDsl LinkLabel).(S) -> Unit = {},
): LinkLabel {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(LinkLabel(text, url, style), init)
}

/**
 * @param drawable will be rendered by this image.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImage] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImage(
  drawable: Drawable,
  scaling: Scaling = Scaling.stretch,
  align: Int = Align.center,
  init: (@Scene2dDsl VisImage).(S) -> Unit = {},
): VisImage {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisImage(drawable, scaling, align), init)
}

/**
 * @param drawableName name of a drawable stored in the VisUI skin.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImage] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImage(
  drawableName: String,
  init: (@Scene2dDsl VisImage).(S) -> Unit = {},
): VisImage {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisImage(drawableName), init)
}

/**
 * @param texture will be rendered by this image.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImage] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImage(
  texture: Texture,
  init: (@Scene2dDsl VisImage).(S) -> Unit = {},
): VisImage {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisImage(texture), init)
}

/**
 * @param ninePatch will be rendered by this image.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImage] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImage(
  ninePatch: NinePatch,
  init: (@Scene2dDsl VisImage).(S) -> Unit = {},
): VisImage {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisImage(ninePatch), init)
}

/**
 * @param textureRegion will be rendered by this image.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImage] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImage(
  textureRegion: TextureRegion,
  init: (@Scene2dDsl VisImage).(S) -> Unit = {},
): VisImage {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisImage(textureRegion), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Note that in contrary to other widgets, this [init] function
 * has no parameters. If you need to access [Cell] or [Node] that this list is in, use [KTable.cell], [KTable.inCell],
 * [KTree.node] or [KTree.inNode]. Allows to fill list items.
 * @return a [VisList] widget instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <I> KWidget<*>.visList(
  style: String = defaultStyle,
  init: KVisList<I>.() -> Unit = {},
): KVisList<I> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val list = KVisList<I>(style)
  storeActor(list)
  list.init()
  list.refreshItems()
  return list
}

/**
 * @param items optional libGDX array of list widget items. Defaults to null.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @return a [VisList] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
fun <I> KWidget<*>.visListOf(
  items: GdxArray<I>? = null,
  style: String = defaultStyle,
): KVisList<I> {
  val list = KVisList<I>(style)
  storeActor(list)
  if (items != null && items.size > 0) {
    list.setItems(items)
  }
  return list
}

/**
 * @param min minimum value displayed by the bar. Defaults to 0.
 * @param min maximum value displayed by the bar. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 * [defaultHorizontalStyle] if the widget is horizontal.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisProgressBar] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visProgressBar(
  min: Float = 0f,
  max: Float = 100f,
  step: Float = 1f,
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  init: (@Scene2dDsl VisProgressBar).(S) -> Unit = {},
): VisProgressBar {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisProgressBar(min, max, step, vertical, style), init)
}

/**
 * @param items optional libGDX array of the [VisSelectBox] items. Defaults to null.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @return a [VisSelectBox] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
fun <I> KWidget<*>.visSelectBoxOf(
  items: GdxArray<I>? = null,
  style: String = defaultStyle,
): KVisSelectBox<I> {
  val selectBox = KVisSelectBox<I>(style)
  storeActor(selectBox)
  if (items != null && items.size > 0) {
    selectBox.items = items
  }
  return selectBox
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Note that in contrary to other widgets, this [init] function
 * has no parameters. If you need to access [Cell] or [Node] that this list is in, use [KTable.cell], [KTable.inCell],
 * [KTree.node] or [KTree.inNode]. Allows to fill select box items.
 * @return a [VisSelectBox] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <I> KWidget<*>.visSelectBox(
  style: String = defaultStyle,
  init: KVisSelectBox<I>.() -> Unit = {},
): KVisSelectBox<I> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val selectBox = KVisSelectBox<I>(style)
  storeActor(selectBox)
  selectBox.init()
  selectBox.refreshItems()
  return selectBox
}

/**
 * @param min minimum value displayed by the slider. Defaults to 0.
 * @param min maximum value displayed by the slider. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 * [defaultHorizontalStyle] if the widget is horizontal.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisSlider] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visSlider(
  min: Float = 0f,
  max: Float = 100f,
  step: Float = 1f,
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  init: (@Scene2dDsl VisSlider).(S) -> Unit = {},
): VisSlider {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisSlider(min, max, step, vertical, style), init)
}

/**
 * @param text initial text displayed by the area. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisTextArea] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visTextArea(
  text: String = "",
  style: String = defaultStyle,
  init: (@Scene2dDsl VisTextArea).(S) -> Unit = {},
): VisTextArea {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisTextArea(text, style), init)
}

/**
 * @param text initial text displayed by the area. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [HighlightTextArea] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.highlightTextArea(
  text: String = "",
  style: String = defaultStyle,
  init: (@Scene2dDsl HighlightTextArea).(S) -> Unit = {},
): HighlightTextArea {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(HighlightTextArea(text, style), init)
}

/**
 * @param text initial text displayed by the area. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ScrollableTextArea] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.scrollableTextArea(
  text: String = "",
  style: String = defaultStyle,
  init: (@Scene2dDsl ScrollableTextArea).(S) -> Unit = {},
): ScrollableTextArea {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(ScrollableTextArea(text, style), init)
}

/**
 * @param text initial text displayed by the field. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisTextField] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visTextField(
  text: String = "",
  style: String = defaultStyle,
  init: (@Scene2dDsl VisTextField).(S) -> Unit = {},
): VisTextField {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisTextField(text, style), init)
}

/**
 * @param text initial text displayed by the field. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisValidatableTextField] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visValidatableTextField(
  text: String = "",
  style: String = defaultStyle,
  init: (@Scene2dDsl VisValidatableTextField).(S) -> Unit = {},
): VisValidatableTextField {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(VisValidatableTextField(text, style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [BusyBar] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.busyBar(
  style: String = defaultStyle,
  init: (@Scene2dDsl BusyBar).(S) -> Unit = {},
): BusyBar {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(BusyBar(style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Separator] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.separator(
  style: String = defaultStyle,
  init: (@Scene2dDsl Separator).(S) -> Unit = {},
): Separator {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Separator(style), init)
}

/**
 * @param text will be displayed as [VisTextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisTextButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visTextButton(
  text: String,
  style: String = defaultStyle,
  init: KVisTextButton.(S) -> Unit = {},
): KVisTextButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisTextButton(text, style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImageButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImageButton(
  style: String = defaultStyle,
  init: KVisImageButton.(S) -> Unit = {},
): KVisImageButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisImageButton(style), init)
}

/**
 * @param text will be displayed as [VisImageTextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisImageTextButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visImageTextButton(
  text: String,
  style: String = defaultStyle,
  init: KVisImageTextButton.(S) -> Unit = {},
): KVisImageTextButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisImageTextButton(text, style), init)
}

/**
 * @param text will be displayed as [VisCheckBox] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisCheckBox] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visCheckBox(
  text: String,
  style: String = defaultStyle,
  init: KVisCheckBox.(S) -> Unit = {},
): KVisCheckBox {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisCheckBox(text, style), init)
}

/**
 * @param text will be displayed as [VisRadioButton] text.
 * @param style name of the widget style. Defaults to "radio".
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisRadioButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visRadioButton(
  text: String,
  style: String = "radio",
  init: KVisRadioButton.(S) -> Unit = {},
): KVisRadioButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisRadioButton(text, style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisTree] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visTree(
  style: String = defaultStyle,
  init: KVisTree.(S) -> Unit = {},
): KVisTree {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisTree(style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [BasicColorPicker] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.basicColorPicker(
  style: String = defaultStyle,
  init: KBasicColorPicker.(S) -> Unit = {},
): KBasicColorPicker {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KBasicColorPicker(style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ExtendedColorPicker] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.extendedColorPicker(
  style: String = defaultStyle,
  init: KExtendedColorPicker.(S) -> Unit = {},
): KExtendedColorPicker {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KExtendedColorPicker(style), init)
}

/**
 * @param name label of the [Spinner].
 * @param model defines how the [Spinner] values are chosen.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Spinner] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.spinner(
  name: String,
  model: SpinnerModel,
  style: String = defaultStyle,
  init: KSpinner.(S) -> Unit = {},
): KSpinner {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KSpinner(style, name, model), init)
}

/**
 * @param defaultSpacing if true, default VisUI spacing will be applied to the table.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisTable] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visTable(
  defaultSpacing: Boolean = false,
  init: KVisTable.(S) -> Unit = {},
): KVisTable {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisTable(defaultSpacing), init)
}

/**
 * Deprecated. Use [flowGroup] instead.
 * @param spacing item spacing of this group.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [KHorizontalFlowGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
@Suppress("DEPRECATION")
@Deprecated("Use KFlowGroup instead.", replaceWith = ReplaceWith("flowGroup"))
inline fun <S> KWidget<S>.horizontalFlowGroup(
  spacing: Float = 0f,
  init: KHorizontalFlowGroup.(S) -> Unit = {},
): KHorizontalFlowGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KHorizontalFlowGroup(spacing), init)
}

/**
 * Deprecated. Use [flowGroup] instead.
 * @param spacing item spacing of this group.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [KVerticalFlowGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
@Suppress("DEPRECATION")
@Deprecated("Use KFlowGroup instead.", replaceWith = ReplaceWith("flowGroup"))
inline fun <S> KWidget<S>.verticalFlowGroup(
  spacing: Float = 0f,
  init: KVerticalFlowGroup.(S) -> Unit = {},
): KVerticalFlowGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVerticalFlowGroup(spacing), init)
}

/**
 * @param vertical if true, child actors will be stacked vertically. If false, they will be placed horizontally.
 * @param spacing item spacing of this group.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [FlowGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.flowGroup(
  vertical: Boolean = false,
  spacing: Float = 0f,
  init: KFlowGroup.(S) -> Unit = {},
): KFlowGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KFlowGroup(vertical, spacing), init)
}

/**
 * @param itemSize size of stored items.
 * @param spacing item spacing of this group.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [GridGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.gridGroup(
  itemSize: Float = 256f,
  spacing: Float = 8f,
  init: KGridGroup.(S) -> Unit = {},
): KGridGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KGridGroup(itemSize, spacing), init)
}

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [FloatingGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.floatingGroup(init: KFloatingGroup.(S) -> Unit = {}): KFloatingGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KFloatingGroup(), init)
}

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [FloatingGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.dragPane(init: KDragPane.(S) -> Unit = {}): KDragPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KDragPane(), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisScrollPane] instance added to this group. Note that this actor may have only a single child.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visScrollPane(
  style: String = defaultStyle,
  init: KVisScrollPane.(S) -> Unit = {},
): KVisScrollPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisScrollPane(style), init)
}

/**
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 * [defaultHorizontalStyle] if the widget is horizontal.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VisSplitPane] instance added to this group. Note that this actor can store only two children.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.visSplitPane(
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  init: KVisSplitPane.(S) -> Unit = {},
): KVisSplitPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVisSplitPane(vertical, style), init)
}

/**
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 * [defaultHorizontalStyle] if the widget is horizontal.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [MultiSplitPane] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.multiSplitPane(
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  init: KMultiSplitPane.(S) -> Unit = {},
): KMultiSplitPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KMultiSplitPane(vertical, style), init)
}

/**
 * @param defaultSpacing if true, default VisUI spacing will be applied to this widget's table.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [CollapsibleWidget] instance with a [VisTable] added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.collapsible(
  defaultSpacing: Boolean = false,
  init: KCollapsible.(S) -> Unit = {},
): KCollapsible {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KCollapsible(KVisTable(defaultSpacing)), init)
}

/**
 * @param defaultSpacing if true, default VisUI spacing will be applied to this widget's table.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [HorizontalCollapsibleWidget] instance with a [VisTable] added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.horizontalCollapsible(
  defaultSpacing: Boolean = false,
  init: KHorizontalCollapsible.(S) -> Unit = {},
): KHorizontalCollapsible {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KHorizontalCollapsible(KVisTable(defaultSpacing)), init)
}

/**
 * @param order buttons order. See [ButtonBar] static variables.
 * @param tableInit will be invoked _after_ [init] on the table storing button bar widgets. Consumes actor container
 * (usually a [Cell] or [Node]) that contains the widget. Might consume the actor itself if this group does not keep
 * actors in dedicated containers. Inlined.
 * @param init will be invoked with the [ButtonBar] as "this". Inlined.
 * @return a [HorizontalCollapsibleWidget] instance with a [VisTable] added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.buttonBar(
  order: String? = null,
  tableInit: VisTable.(S) -> Unit = {},
  init: (@Scene2dDsl ButtonBar).() -> Unit = {},
): ButtonBar {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val bar = if (order == null) ButtonBar() else ButtonBar(order)
  bar.init()
  actor(bar.createTable(), tableInit)
  return bar
}

/**
 * @param itemAdapter defines [ListView] items.
 * @param style name of the [ListView] style to apply.
 * @param init will be invoked with the [ListView] instance as "this". Inlined.
 * @return a new instance of [ListView]. [ListView.mainTable] will be added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <I> KWidget<*>.listView(
  itemAdapter: ListAdapter<I>,
  style: String = defaultStyle,
  init: ListView<I>.() -> Unit = {},
): ListView<I> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val view = ListView(itemAdapter, style)
  storeActor(view.mainTable)
  return view.also(init)
}

/**
 * @param style name of the [TabbedPane.TabbedPaneStyle] style to apply.
 * @param init will be invoked with the [TabbedPane] instance as "this". Consumes container
 * (such as a [Cell] or [Node]) with the [TabbedPane.mainTable], or the table itself if the parent
 * does not store actors in containers.
 * @return a new instance of [TabbedPane]. [TabbedPane.mainTable] will be added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.tabbedPane(
  style: String = defaultStyle,
  init: KTabbedPane.(S) -> Unit = {},
): KTabbedPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val pane = KTabbedPane(style)
  val table = pane.table
  actor(table, { pane.init(it) })
  return pane
}
