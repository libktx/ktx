package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Tree

/* Inlined factory methods of tree root widget. */

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Tree] instance.
 */
inline fun tree(style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                init: KTreeWidget.() -> Unit) = actor(KTreeWidget(skin, style), init)

/**
 * Allows to inline a function block on a [KNode]. Syntax sugar for nested [Tree] nodes creation.
 * @param init will be invoked on this node.
 * @return this node.
 */
inline operator fun KNode.invoke(init: KNode.() -> Unit): KNode {
  this.init()
  return this
}
