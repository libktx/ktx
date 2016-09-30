@file:Suppress("NOTHING_TO_INLINE")

package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node

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
 * @return a [Button] instance added to this group.
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

// TODO List ProgressBar KScrollPane SelectBox Slider KSplitPane KStack KTable TextArea TextField Touchpad KTree KVerticalGroup
// Note: List and SelectBox items need to be explicitly set after widget creation, otherwise they will not be visible.
