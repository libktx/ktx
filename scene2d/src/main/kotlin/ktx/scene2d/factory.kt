@file:Suppress("NOTHING_TO_INLINE")

package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.utils.Array as GdxArray

/* Factory methods of groups' children. */

// Note that there are two factory methods for each actor: regular and inlined. This is because Kotlin currently (or at
// least during development time) does not support default parameters on inlined lambdas. Syntax like this:
//
// init: A.(S) -> Unit = {}
//
// ...simply would not compile. Mandatory empty braces near each actor would make the API very inconvenient, hence the
// need for duplication.

/**
 * Allows to create an actor and immediately invoke its type-safe building init block. Internal utility method.
 * @param actor will be initiated.
 * @param init will be invoked on the actor. Inlined.
 * @return passed [Actor].
 */
inline fun <T : Actor> actor(actor: T, init: T.() -> Unit): T {
  actor.init()
  return actor
}

/**
 * Utility function for adding existing actors to the group with a type-safe builder init block. Mostly for internal use.
 * @param actor will be added to the group.
 * @param init will be invoked on the actor, consuming actor storage object (usually a [Cell], [Node] or the actor itself,
 *    if the group does not keep the actors in a dedicated storage object).
 * @return the passed actor.
 * @param S storage type.
 * @param A actor type.
 */
inline fun <S, A : Actor> KWidget<S>.actor(actor: A, init: A.(S) -> Unit): A {
  actor.init(storeActor(actor))
  return actor
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Button] instance added to this group.
 */
inline fun KWidget<*>.button(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KButton(skin, style))

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Button] instance added to this group.
 */
inline fun <S> KWidget<S>.button(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                 init: KButton.(S) -> Unit) = actor(KButton(skin, style), init)

/**
 * @param minCheckedCount minimum amount of buttons checked at once.
 * @param maxCheckedCount maximum amount of buttons checked at once.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [KButtonTable] instance (which manages an internal [ButtonGroup]) added to this group. All direct
 *    [Button]-extending children of this widget will be added to the internal [ButtonGroup].
 */
inline fun KWidget<*>.buttonGroup(minCheckedCount: Int, maxCheckedCount: Int, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KButtonTable(minCheckedCount, maxCheckedCount, skin))

/**
 * @param minCheckedCount minimum amount of buttons checked at once.
 * @param maxCheckedCount maximum amount of buttons checked at once.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [KButtonTable] instance (which manages an internal [ButtonGroup]) added to this group. All direct
 *    [Button]-extending children of this widget will be added to the internal [ButtonGroup].
 */
inline fun <S> KWidget<S>.buttonGroup(minCheckedCount: Int, maxCheckedCount: Int, skin: Skin = Scene2DSkin.defaultSkin,
                                      init: KButtonTable.(S) -> Unit) =
    actor(KButtonTable(minCheckedCount, maxCheckedCount, skin), init)

/**
 * @param text will be displayed as [CheckBox] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [CheckBox] instance added to this group.
 */
inline fun KWidget<*>.checkBox(text: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KCheckBox(text, skin, style))

/**
 * @param text will be displayed as [CheckBox] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [CheckBox] instance added to this group.
 */
inline fun <S> KWidget<S>.checkBox(text: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                   init: KCheckBox.(S) -> Unit) = actor(KCheckBox(text, skin, style), init)

/**
 * @return a [Container] instance added to this group. Note that this actor might store only a single child.
 */
inline fun KWidget<*>.container() = appendActor(KContainer<Actor>())

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Container] instance added to this group. Note that this actor might store only a single child.
 */
inline fun <S> KWidget<S>.container(init: KContainer<Actor>.(S) -> Unit) = actor(KContainer<Actor>(), init)

/**
 * @return a [HorizontalGroup] instance added to this group.
 */
inline fun KWidget<*>.horizontalGroup() = appendActor(KHorizontalGroup())

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [HorizontalGroup] instance added to this group.
 */
inline fun <S> KWidget<S>.horizontalGroup(init: KHorizontalGroup.(S) -> Unit) = actor(KHorizontalGroup(), init)

/**
 * @param drawable name of a drawable stored in the chosen skin.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Image] instance added to this group.
 */
inline fun KWidget<*>.image(drawable: String, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(Image(skin.getDrawable(drawable)))

/**
 * @param drawable name of a drawable stored in the chosen skin.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Image] instance added to this group.
 */
inline fun <S> KWidget<S>.image(drawable: String, skin: Skin = Scene2DSkin.defaultSkin, init: Image.(S) -> Unit) =
    actor(Image(skin.getDrawable(drawable)), init)

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [ImageButton] instance added to this group.
 */
inline fun KWidget<*>.imageButton(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KImageButton(skin, style))

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [ImageButton] instance added to this group.
 */
inline fun <S> KWidget<S>.imageButton(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                      init: KImageButton.(S) -> Unit) = actor(KImageButton(skin, style), init)

/**
 * @param text will be displayed as [ImageTextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [ImageTextButton] instance added to this group.
 */
inline fun KWidget<*>.imageTextButton(text: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KImageTextButton(text, skin, style))

/**
 * @param text will be displayed as [ImageTextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [ImageTextButton] instance added to this group.
 */
inline fun <S> KWidget<S>.imageTextButton(text: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                          init: KImageTextButton.(S) -> Unit) =
    actor(KImageTextButton(text, skin, style), init)

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Label] instance added to this group.
 */
inline fun KWidget<*>.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(Label(text, skin, style))

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Label] instance added to this group.
 */
inline fun <S> KWidget<S>.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                init: Label.(S) -> Unit) = actor(Label(text, skin, style), init)

/**
 * @param items optional LibGDX array of list widget items. Defaults to null.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a List widget instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
inline fun <I> KWidget<*>.listWidgetOf(items: GdxArray<I>? = null, style: String = defaultStyle,
                                       skin: Skin = Scene2DSkin.defaultSkin): KListWidget<I> {
  val list = appendActor(KListWidget<I>(skin, style))
  if (items != null && items.size > 0) {
    list.setItems(items)
  }
  return list
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined. Allows to fill list's items.
 * @return a List widget instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 * @param S type of actor containers used by the parent. Usually [Cell], [Node] or [Actor].
 */
inline fun <I, S> KWidget<S>.listWidget(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                        init: KListWidget<I>.(S) -> Unit): KListWidget<I> {
  val list = KListWidget<I>(skin, style)
  list.init(storeActor(list))
  list.refreshItems()
  return list
}

/**
 * @param min minimum value displayed by the bar. Defaults to 0.
 * @param min maximum value displayed by the bar. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 *    [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [ProgressBar] instance added to this group.
 */
inline fun KWidget<*>.progressBar(min: Float = 0f, max: Float = 1f, step: Float = 0.01f, vertical: Boolean = false,
                                  style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
                                  skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(ProgressBar(min, max, step, vertical, skin, style))

/**
 * @param min minimum value displayed by the bar. Defaults to 0.
 * @param min maximum value displayed by the bar. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 *    [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [ProgressBar] instance added to this group.
 */
inline fun <S> KWidget<S>.progressBar(min: Float = 0f, max: Float = 1f, step: Float = 0.01f, vertical: Boolean = false,
                                      style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
                                      skin: Skin = Scene2DSkin.defaultSkin, init: ProgressBar.(S) -> Unit) =
    actor(ProgressBar(min, max, step, vertical, skin, style), init)

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [ScrollPane] instance added to this group. Note that this actor may have only a single child.
 */
inline fun KWidget<*>.scrollPane(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KScrollPane(skin, style))

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [ScrollPane] instance added to this group. Note that this actor may have only a single child.
 */
inline fun <S> KWidget<S>.scrollPane(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                     init: KScrollPane.(S) -> Unit) = actor(KScrollPane(skin, style), init)

/**
 * @param items optional LibGDX array [SelectBox] items. Defaults to null.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [SelectBox] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 */
inline fun <I> KWidget<*>.selectBoxOf(items: GdxArray<I>? = null, style: String = defaultStyle,
                                      skin: Skin = Scene2DSkin.defaultSkin): KSelectBox<I> {
  val selectBox = appendActor(KSelectBox<I>(skin, style))
  if (items != null && items.size > 0) {
    selectBox.items = items
  }
  return selectBox
}

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined. Allows to fill list's items.
 * @return a [SelectBox] instance added to this group.
 * @param I type of items stored by this widget. Usually items are converted to string and displayed.
 * @param S type of actor containers used by the parent. Usually [Cell], [Node] or [Actor].
 */
inline fun <I, S> KWidget<S>.selectBox(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                       init: KSelectBox<I>.(S) -> Unit): KSelectBox<I> {
  val selectBox = KSelectBox<I>(skin, style)
  selectBox.init(storeActor(selectBox))
  selectBox.refreshItems()
  return selectBox
}

/**
 * @param min minimum value displayed by the slider. Defaults to 0.
 * @param min maximum value displayed by the slider. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 *    [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Slider] instance added to this group.
 */
inline fun KWidget<*>.slider(min: Float = 0f, max: Float = 1f, step: Float = 0.01f, vertical: Boolean = false,
                             style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
                             skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(Slider(min, max, step, vertical, skin, style))

/**
 * @param min minimum value displayed by the slider. Defaults to 0.
 * @param min maximum value displayed by the slider. Defaults to 1.
 * @param step the size of a single step between two values. Defaults to 0.01.
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 *    [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Slider] instance added to this group.
 */
inline fun <S> KWidget<S>.slider(min: Float = 0f, max: Float = 1f, step: Float = 0.01f, vertical: Boolean = false,
                                 style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
                                 skin: Skin = Scene2DSkin.defaultSkin, init: Slider.(S) -> Unit) =
    actor(Slider(min, max, step, vertical, skin, style), init)

/**
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 *    [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [SplitPane] instance added to this group. Note that this actor can store only two children.
 */
inline fun KWidget<*>.splitPane(vertical: Boolean = false,
                                style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
                                skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KSplitPane(vertical, skin, style))

/**
 * @param vertical true if the widget is vertical, false if horizontal.
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] if the widget is vertical or
 *    [defaultHorizontalStyle] if the widget is horizontal.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [SplitPane] instance added to this group. Note that this actor can store only two children.
 */
inline fun <S> KWidget<S>.splitPane(vertical: Boolean = false,
                                    style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
                                    skin: Skin = Scene2DSkin.defaultSkin, init: KSplitPane.(S) -> Unit) =
    actor(KSplitPane(vertical, skin, style), init)

/**
 * @return a [Stack] instance added to this group.
 */
inline fun KWidget<*>.stack() = appendActor(KStack())

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Stack] instance added to this group.
 */
inline fun <S> KWidget<S>.stack(init: KStack.(S) -> Unit) = actor(KStack(), init)

/**
 * @param skin [Skin] instance that will be applied to some table children. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Table] instance added to this group.
 */
inline fun KWidget<*>.table(skin: Skin = Scene2DSkin.defaultSkin) = appendActor(KTableWidget(skin))

/**
 * @param skin [Skin] instance that will be applied to some table children. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Table] instance added to this group.
 */
inline fun <S> KWidget<S>.table(skin: Skin = Scene2DSkin.defaultSkin, init: KTableWidget.(S) -> Unit) =
    actor(KTableWidget(skin), init)

/**
 * @param text initial text displayed by the area. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [TextArea] instance added to this group.
 */
inline fun KWidget<*>.textArea(text: String = "", style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(TextArea(text, skin, style))

/**
 * @param text initial text displayed by the area. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [TextArea] instance added to this group.
 */
inline fun <S> KWidget<S>.textArea(text: String = "", style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                   init: TextArea.(S) -> Unit) = actor(TextArea(text, skin, style), init)


/**
 * @param text initial text displayed by the field. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [TextField] instance added to this group.
 */
inline fun KWidget<*>.textField(text: String = "", style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(TextField(text, skin, style))

/**
 * @param text initial text displayed by the field. Defaults to empty string.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [TextField] instance added to this group.
 */
inline fun <S> KWidget<S>.textField(text: String = "", style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                    init: TextField.(S) -> Unit) = actor(TextField(text, skin, style), init)

/**
 * @param text will be displayed as [TextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [TextButton] instance added to this group.
 */
inline fun KWidget<*>.textButton(text: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KTextButton(text, skin, style))

/**
 * @param text will be displayed as [TextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [TextButton] instance added to this group.
 */
inline fun <S> KWidget<S>.textButton(text: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                     init: KTextButton.(S) -> Unit) = actor(KTextButton(text, skin, style), init)

/**
 * @param deadzone the distance from the center of the touchpad required for the knob to be moved.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Touchpad] instance added to this group.
 */
inline fun KWidget<*>.touchpad(deadzone: Float, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(Touchpad(deadzone, skin, style))

/**
 * @param deadzone the distance from the center of the touchpad required for the knob to be moved.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Touchpad] instance added to this group.
 */
inline fun <S> KWidget<S>.touchpad(deadzone: Float, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                   init: Touchpad.(S) -> Unit) = actor(Touchpad(deadzone, skin, style), init)

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Tree] instance added to this group.
 */
inline fun KWidget<*>.tree(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(KTreeWidget(skin, style))

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [Tree] instance added to this group.
 */
inline fun <S> KWidget<S>.tree(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                               init: KTreeWidget.(S) -> Unit) = actor(KTreeWidget(skin, style), init)

/**
 * @return a [VerticalGroup] instance added to this group.
 */
inline fun KWidget<*>.verticalGroup() = appendActor(KVerticalGroup())

/**
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 *    contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 *    Inlined.
 * @return a [HorizontalGroup] instance added to this group.
 */
inline fun <S> KWidget<S>.verticalGroup(init: KVerticalGroup.(S) -> Unit) = actor(KVerticalGroup(), init)
