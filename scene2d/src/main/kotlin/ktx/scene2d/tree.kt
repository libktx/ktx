package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*

/* Inlined factory methods of tree widget and trees' children. */

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Tree] instance.
 */
inline fun tree(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                init: KTreeWidget.() -> Unit) = actor(KTreeWidget(skin, style), init)

/**
 * Utility method for adding existing [Actor] instances to trees with an inlined type-safe builder block.
 * Mostly for internal use.
 * @param actor will be added to the [Tree].
 * @param init will be invoked right after the actor is added to the [Tree]. Consumes actor's [Node].
 * @return actor instance passed as the parameter.
 */
inline fun <T : Actor> KTree.add(actor: T, init: T.(KNode) -> Unit): T {
  actor.init(add(actor))
  return actor
}

/**
 * Allows to inline a function block on a [KNode]. Syntax sugar for nested [Tree] nodes creation.
 * @param init will be invoked on this node.
 * @return this node.
 */
inline operator fun KNode.invoke(init: KNode.() -> Unit): KNode {
  this.init()
  return this
}

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes [Node] that contains the widget. Inlined.
 * @return a [Label] instance added to this group.
 */
inline fun KTree.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                       init: Label.(KNode) -> Unit) = add(Label(text, skin, style), init)

// TODO Inlined factory methods of tree children.