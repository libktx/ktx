package ktx.scene2d

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Constructs a top-level [Window] widget.
 * @param title will be displayed as window's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the [Window] widget. Inlined.
 * @return a new [Window] instance.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun RootWidget.window(
  title: String,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KWindow.() -> Unit = {},
): KWindow {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return storeActor(KWindow(title, skin, style)).apply(init)
}

/**
 * Constructs a top-level [Dialog] widget.
 * @param title will be displayed as dialog's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the [Dialog] widget. Inlined.
 * @return a new [Dialog] instance.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun RootWidget.dialog(
  title: String,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KDialog.() -> Unit = {},
): KDialog {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return storeActor(KDialog(title, skin, style)).apply(init)
}

/**
 * Utility function for adding existing actors to the group with a type-safe builder init block.
 * Mostly for internal use.
 * @param actor will be added to the group.
 * @param init will be invoked on the actor, consuming actor storage object (usually a [Cell], [Node] or the actor
 * itself, if the group does not keep the actors in a dedicated storage object).
 * @return the passed actor.
 * @param S storage type.
 * @param A actor type.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S, A : Actor> KWidget<S>.actor(
  actor: A,
  init: (@Scene2dDsl A).(S) -> Unit = {},
): A {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  actor.init(storeActor(actor))
  return actor
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Button] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.button(
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KButton.(S) -> Unit = {},
): KButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KButton(skin, style), init)
}

/**
 * @param minCheckedCount minimum amount of buttons checked at once.
 * @param maxCheckedCount maximum amount of buttons checked at once.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [KButtonTable] instance (which manages an internal [ButtonGroup]) added to this group. All direct
 * [Button]-extending children of this widget will be added to the internal [ButtonGroup].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.buttonGroup(
  minCheckedCount: Int,
  maxCheckedCount: Int,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KButtonTable.(S) -> Unit = {},
): KButtonTable {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KButtonTable(minCheckedCount, maxCheckedCount, skin), init)
}

/**
 * @param text will be displayed as [CheckBox] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [CheckBox] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.checkBox(
  text: String,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KCheckBox.(S) -> Unit = {},
): KCheckBox {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KCheckBox(text, skin, style), init)
}

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Container] instance added to this group. Note that this actor might store only a single child.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.container(init: KContainer<Actor>.(S) -> Unit = {}): KContainer<Actor> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KContainer(), init)
}

/**
 * Use this [container] method variant for customly built actors that you want to keep in a [Container].
 * @param actor will be added to the container.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined. Since [Container] can store only a single child and it is passed as [actor], this [init] block must not
 * create any new actors added to this group.
 * @return a [Container] instance added to this group. Note that this actor might store only a single child.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S, A : Actor> KWidget<S>.container(
  actor: A,
  init: KContainer<A>.(S) -> Unit = {},
): KContainer<A> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KContainer(actor), init)
}

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [HorizontalGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.horizontalGroup(init: KHorizontalGroup.(S) -> Unit = {}): KHorizontalGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KHorizontalGroup(), init)
}

/**
 * @param drawableName name of a drawable stored in the chosen skin.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Image] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
  drawableName: String,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl Image).(S) -> Unit = {},
): Image {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Image(skin.getDrawable(drawableName)), init)
}

/**
 * @param ninePatch will be drawn by the [Image].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Image] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
  ninePatch: NinePatch,
  init: (@Scene2dDsl Image).(S) -> Unit = {},
): Image {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Image(ninePatch), init)
}

/**
 * @param textureRegion will be drawn by the [Image].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Image] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
  textureRegion: TextureRegion,
  init: (@Scene2dDsl Image).(S) -> Unit = {},
): Image {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Image(textureRegion), init)
}

/**
 * @param texture will be drawn by the [Image].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Image] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
  texture: Texture,
  init: (@Scene2dDsl Image).(S) -> Unit = {},
): Image {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Image(texture), init)
}

/**
 * @param drawable will be drawn by the [Image]. Per default it is null.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Image] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
  drawable: Drawable? = null,
  init: (@Scene2dDsl Image).(S) -> Unit = {},
): Image {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Image(drawable), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ImageButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.imageButton(
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KImageButton.(S) -> Unit = {},
): KImageButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KImageButton(skin, style), init)
}

/**
 * @param text will be displayed as [ImageTextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ImageTextButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.imageTextButton(
  text: String,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KImageTextButton.(S) -> Unit = {},
): KImageTextButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KImageTextButton(text, skin, style), init)
}

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Label] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.label(
  text: CharSequence,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl Label).(S) -> Unit = {},
): Label {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Label(text, skin, style), init)
}

/**
 * @param items optional libGDX array of list widget items. Defaults to null.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a List widget instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
fun <I> KWidget<*>.listWidgetOf(
  items: GdxArray<I>? = null,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
): KListWidget<I> {
  val list = KListWidget<I>(skin, style)
  storeActor(list)
  if (items != null && items.size > 0) {
    list.setItems(items)
  }
  return list
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Note that in contrary to other widgets, this [init] function
 * has no parameters. If you need to access [Cell] or [Node] that this list is in, use [KTable.cell], [KTable.inCell],
 * [KTree.node] or [KTree.inNode]. Allows to fill list items.
 * @return a List widget instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <I> KWidget<*>.listWidget(
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KListWidget<I>.() -> Unit = {},
): KListWidget<I> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val list = KListWidget<I>(skin, style)
  storeActor(list)
  list.init()
  list.refreshItems()
  return list
}

/**
 * @param particleEffect a loaded [ParticleEffect] that will be rendered by this actor.
 * @param resetOnStart if true, this actor will call [ParticleEffect.reset] on [ParticleEffectActor.start] call.
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ParticleEffectActor] instance added to this group. It does not have to be disposed.
 * @see ParticleEffectActor
 * @see ParticleEffect
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.particleEffect(
  particleEffect: ParticleEffect,
  resetOnStart: Boolean = true,
  init: (@Scene2dDsl ParticleEffectActor).(S) -> Unit = {},
): ParticleEffectActor {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(ParticleEffectActor(particleEffect, resetOnStart), init)
}

/**
 * @param min minimum value displayed by the bar. Defaults to 0.
 * @param min maximum value displayed by the bar. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 * [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ProgressBar] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.progressBar(
  min: Float = 0f,
  max: Float = 1f,
  step: Float = 0.01f,
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl ProgressBar).(S) -> Unit = {},
): ProgressBar {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(ProgressBar(min, max, step, vertical, skin, style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [ScrollPane] instance added to this group. Note that this actor may have only a single child.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.scrollPane(
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KScrollPane.(S) -> Unit = {},
): KScrollPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KScrollPane(skin, style), init)
}

/**
 * @param items optional libGDX array of the [SelectBox] items. Defaults to null.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [SelectBox] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
fun <I> KWidget<*>.selectBoxOf(
  items: GdxArray<I>? = null,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
): KSelectBox<I> {
  val selectBox = KSelectBox<I>(skin, style)
  storeActor(selectBox)
  if (items != null && items.size > 0) {
    selectBox.items = items
  }
  return selectBox
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Note that in contrary to other widgets, this [init] function
 * has no parameters. If you need to access [Cell] or [Node] that this list is in, use [KTable.cell], [KTable.inCell],
 * [KTree.node] or [KTree.inNode]. Allows to fill select box items.
 * @return a [SelectBox] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <I> KWidget<*>.selectBox(
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KSelectBox<I>.() -> Unit = {},
): KSelectBox<I> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val selectBox = KSelectBox<I>(skin, style)
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
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Slider] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.slider(
  min: Float = 0f,
  max: Float = 1f,
  step: Float = 0.01f,
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl Slider).(S) -> Unit = {},
): Slider {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Slider(min, max, step, vertical, skin, style), init)
}

/**
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 * [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [SplitPane] instance added to this group. Note that this actor can store only two children.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.splitPane(
  vertical: Boolean = false,
  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KSplitPane.(S) -> Unit = {},
): KSplitPane {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KSplitPane(vertical, skin, style), init)
}

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Stack] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.stack(init: KStack.(S) -> Unit = {}): KStack {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KStack(), init)
}

/**
 * @param skin [Skin] instance that will be applied to some table children. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Table] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.table(
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KTableWidget.(S) -> Unit = {},
): KTableWidget {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KTableWidget(skin), init)
}

/**
 * @param text initial text displayed by the area. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [TextArea] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.textArea(
  text: String = "",
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl TextArea).(S) -> Unit = {},
): TextArea {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(TextArea(text, skin, style), init)
}

/**
 * @param text initial text displayed by the field. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [TextField] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.textField(
  text: String = "",
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl TextField).(S) -> Unit = {},
): TextField {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(TextField(text, skin, style), init)
}

/**
 * @param text will be displayed as [TextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [TextButton] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.textButton(
  text: String,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KTextButton.(S) -> Unit = {},
): KTextButton {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KTextButton(text, skin, style), init)
}

/**
 * @param deadzone the distance from the center of the touchpad required for the knob to be moved.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Touchpad] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.touchpad(
  deadzone: Float,
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: (@Scene2dDsl Touchpad).(S) -> Unit = {},
): Touchpad {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(Touchpad(deadzone, skin, style), init)
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [Tree] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.tree(
  style: String = defaultStyle,
  skin: Skin = Scene2DSkin.defaultSkin,
  init: KTreeWidget.(S) -> Unit = {},
): KTreeWidget {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KTreeWidget(skin, style), init)
}

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [VerticalGroup] instance added to this group.
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.verticalGroup(init: KVerticalGroup.(S) -> Unit = {}): KVerticalGroup {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return actor(KVerticalGroup(), init)
}
