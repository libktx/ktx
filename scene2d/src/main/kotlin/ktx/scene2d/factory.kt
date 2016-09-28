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
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Label] instance added to this group.
 */
fun KWidget<*>.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(Label(text, skin, style))

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes [Cell] that contains the widget. Inlined.
 * @return a [Label] instance.
 */
inline fun <S> KWidget<S>.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                                init: Label.(S) -> Unit) = actor(Label(text, skin, style), init)

// TODO Factory methods of all actors.
