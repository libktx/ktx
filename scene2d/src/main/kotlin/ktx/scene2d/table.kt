package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*

/* Inlined factory methods of table widgets and their children. */

/**
 * @param skin will be used to apply styles to some of the table children. Defaults to [Scene2DSkin.defaultSkin]
 * @param init will be invoked on the table. Inlined.
 * @return a new [Table] instance.
 */
inline fun table(skin: Skin = Scene2DSkin.defaultSkin, init: KTableWidget.() -> Unit) = actor(KTableWidget(skin), init)

/**
 * @param title will be displayed as window's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Window] instance.
 */
inline fun window(title: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                  init: KWindow.() -> Unit) = actor(KWindow(title, skin, style), init)

/**
 * @param title will be displayed as dialog's title.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked on the widget. Inlined.
 * @return a new [Dialog] instance.
 */
inline fun dialog(title: String, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                  init: KDialog.() -> Unit) = actor(KDialog(title, skin, style), init)

/**
 * @param minCheckedCount minimum amount of buttons checked at once.
 * @param maxCheckedCount maximum amount of buttons checked at once.
 * @param skin will be used to apply styles to some of the table children. Defaults to [Scene2DSkin.defaultSkin]
 * @param init will be invoked on the table. Inlined.
 * @return a new [KButtonTable] instance, which manages a [ButtonGroup] internally. All [Button] instances added
 *    directly to this widget will registered in the [ButtonGroup].
 */
inline fun buttonGroup(minCheckedCount: Int, maxCheckedCount: Int, skin: Skin = Scene2DSkin.defaultSkin,
                       init: KButtonTable.() -> Unit) = actor(KButtonTable(minCheckedCount, maxCheckedCount, skin), init)

/**
 * Utility method for adding existing [Actor] instances to the table with an inlined type-safe builder block. Mostly
 * for internal use.
 * @param actor will be added to the [Table].
 * @param init will be invoked right after the actor is added to the [Table]. Consumes actor's [Cell].
 * @return actor instance passed as the parameter.
 */
inline fun <T : Actor> KTable.add(actor: T, init: T.(Cell<T>) -> Unit): T {
  actor.init(add(actor))
  return actor
}

/**
 * @param text will be displayed on the label.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes [Cell] that contains the widget. Inlined.
 * @return a [Label] instance.
 */
inline fun KTable.label(text: CharSequence, style: String = defaultStyle, skin: Skin = Scene2DSkin.defaultSkin,
                        init: Label.(Cell<Label>) -> Unit) = add(Label(text, skin, style), init)

// TODO Inlined factory methods of table children.
