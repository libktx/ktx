package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/* Factory methods of all groups' children. */

/**
 * Internal utility method.
 * @param actor will be initiated.
 * @param init will be invoked on the actor. Inlined.
 * @return passed [Actor].
 */
inline fun <T : Actor> actor(actor: T, init: T.() -> Unit): T {
  actor.init()
  return actor
}

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @return a [Label] instance added to this group.
 */
fun KWidget.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin) =
    appendActor(Label(text, skin, style))

// TODO Factory methods of all actors.
