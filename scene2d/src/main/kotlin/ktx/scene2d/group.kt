package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/* Inlined factory methods of regular widget groups' children. */

// TODO Factory methods: Stack, HorizontalGroup, VerticalGroup, SplitPane, ScrollPane, Container.

/**
 * Utility method for adding existing [Actor] instances to widget groups with an inlined type-safe builder block.
 * Mostly for internal use.
 * @param actor will be added to the group.
 * @param init will be invoked right after the actor is added to the group.
 * @return actor instance passed as the parameter.
 */
inline fun <T : Actor> KGroup.add(actor: T, init: T.() -> Unit): T {
  add(actor).init()
  return actor
}

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Inlined.
 * @return a [Label] instance added to this group.
 */
inline fun KGroup.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                        init: Label.() -> Unit) = add(Label(text, skin, style), init)

// TODO Inlined factory methods of widget group children.
