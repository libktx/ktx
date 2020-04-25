@file:Suppress("DEPRECATION")

package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.*

/* Inlined factory methods of table-based root widgets. */

/**
 * @param skin will be used to apply styles to some of the table children. Defaults to [Scene2DSkin.defaultSkin]
 * @param init will be invoked on the table. Inlined.
 * @return a new [Table] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.tree", imports = ["ktx.scene2d.scene2d"]))
inline fun table(
    skin: Skin = Scene2DSkin.defaultSkin,
    init: KTableWidget.() -> Unit = {}) = actor(KTableWidget(skin), init)

/**
 * @param title will be displayed as window's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Window] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.window", imports = ["ktx.scene2d.scene2d"]))
inline fun window(
    title: String,
    style: String = defaultStyle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: KWindow.() -> Unit = {}) = actor(KWindow(title, skin, style), init)

/**
 * @param title will be displayed as dialog's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Dialog] instance.
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.dialog", imports = ["ktx.scene2d.scene2d"]))
inline fun dialog(
    title: String,
    style: String = defaultStyle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: KDialog.() -> Unit = {}) = actor(KDialog(title, skin, style), init)

/**
 * @param minCheckedCount minimum amount of buttons checked at once.
 * @param maxCheckedCount maximum amount of buttons checked at once.
 * @param skin will be used to apply styles to some of the table children. Defaults to [Scene2DSkin.defaultSkin]
 * @param init will be invoked on the table. Inlined.
 * @return a new [KButtonTable] instance, which manages a [ButtonGroup] internally. All [Button] instances added
 *    directly to this widget will registered in the [ButtonGroup].
 */
@Scene2dDsl
@Deprecated(
  message = "Root widgets should now be created with `scene2d` DSL.",
  replaceWith = ReplaceWith("scene2d.tree", imports = ["ktx.scene2d.scene2d"]))
inline fun buttonGroup(
    minCheckedCount: Int,
    maxCheckedCount: Int,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: KButtonTable.() -> Unit = {}) = actor(KButtonTable(minCheckedCount, maxCheckedCount, skin), init)
