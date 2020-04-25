@file:Suppress("DEPRECATION")

package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*

/* Inlined factory methods of basic WidgetGroup-based root actors. */

/**
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Stack] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.stack", imports = ["ktx.scene2d.scene2d"]))
inline fun stack(
    init: KStack.() -> Unit = {}) = actor(KStack(), init)

/**
 * @param init will be invoked on the widget. Inlined.
 * @return a new [HorizontalGroup] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.horizontalGroup", imports = ["ktx.scene2d.scene2d"]))
inline fun horizontalGroup(
    init: KHorizontalGroup.() -> Unit = {}) = actor(KHorizontalGroup(), init)

/**
 * @param init will be invoked on the widget. Inlined.
 * @return a new [VerticalGroup] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.verticalGroup", imports = ["ktx.scene2d.scene2d"]))
inline fun verticalGroup(
    init: KVerticalGroup.() -> Unit = {}) = actor(KVerticalGroup(), init)

/**
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Container] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.container", imports = ["ktx.scene2d.scene2d"]))
inline fun container(
    init: KContainer<Actor>.() -> Unit = {}) = actor(KContainer(), init)

/**
 * @param vertical true to make the widget vertical, false to make it horizontal. Defaults to false (horizontal).
 * @param style name of the widget style. Defaults to [defaultVerticalStyle] or [defaultHorizontalStyle] depending on the
 *    vertical property.
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [SplitPane] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.splitPane", imports = ["ktx.scene2d.scene2d"]))
inline fun splitPane(
    vertical: Boolean = false,
    style: String = if (vertical) defaultVerticalStyle else defaultHorizontalStyle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: KSplitPane.() -> Unit = {}) = actor(KSplitPane(vertical, skin, style), init)

/**
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [ScrollPane] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.scrollPane", imports = ["ktx.scene2d.scene2d"]))
inline fun scrollPane(
    style: String = defaultStyle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: KScrollPane.() -> Unit = {}) = actor(KScrollPane(skin, style), init)
