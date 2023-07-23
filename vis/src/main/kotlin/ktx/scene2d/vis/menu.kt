package ktx.scene2d.vis

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.defaultStyle
import ktx.scene2d.scene2d
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Begins creation of a new [PopupMenu].
 * @param style name of the [PopupMenu.PopupMenuStyle].
 * @param init allows to customize the [PopupMenu].
 * @return a new instance of [PopupMenu].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
@Suppress("UnusedReceiverParameter")
inline fun scene2d.popupMenu(
  style: String = defaultStyle,
  init: (@Scene2dDsl PopupMenu).() -> Unit = {},
): PopupMenu {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val popupMenu = PopupMenu(style)
  popupMenu.init()
  return popupMenu
}

/**
 * Begins creation of a new [MenuBar].
 * @param style name of the [MenuBar.MenuBarStyle].
 * @param init allows to customize the [MenuBar]. Receives the container (such as a table cell or tree node)
 * that the menu bar table was stored in, or the table itself if the parent does not use containers.
 * @return a new instance of [MenuBar].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.menuBar(
  style: String = defaultStyle,
  init: (@Scene2dDsl MenuBar).(S) -> Unit = {},
): MenuBar {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val menuBar = MenuBar(style)
  menuBar.init(storeActor(menuBar.table))
  return menuBar
}

/**
 * Adds new [Menu] to this [MenuBar].
 * @param title title of the [Menu].
 * @param style name of the [Menu.MenuStyle].
 * @param init allows to customize the [Menu].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun MenuBar.menu(
  title: String,
  style: String = defaultStyle,
  init: (@Scene2dDsl PopupMenu).() -> Unit = {},
): Menu {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val menu = Menu(title, style)
  menu.init()
  this.addMenu(menu)
  return menu
}

/**
 * Creates and assigns new submenu to this [MenuItem].
 * @param style name of the [PopupMenu.PopupMenuStyle].
 * @param init allows to customize the [PopupMenu].
 * @return a new instance of [PopupMenu] set as a submenu of this [MenuItem].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun MenuItem.subMenu(
  style: String = defaultStyle,
  init: (@Scene2dDsl PopupMenu).() -> Unit = {},
): PopupMenu {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val subMenu = PopupMenu(style)
  setSubMenu(subMenu)
  subMenu.init()
  return subMenu
}

/**
 * Adds new [MenuItem] to this [PopupMenu].
 * @param text will be displayed by the [MenuItem].
 * @param style name of a [MenuItem.MenuItemStyle].
 * @param init allows to customize the [MenuItem].
 * @return a new instance of [MenuItem] added to this [PopupMenu].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun PopupMenu.menuItem(
  text: String,
  style: String = defaultStyle,
  init: (@Scene2dDsl MenuItem).() -> Unit = {},
): MenuItem {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val item = MenuItem(text, style)
  addItem(item)
  item.init()
  return item
}

/**
 * Adds new [MenuItem] to this [PopupMenu].
 * @param text will be displayed by the [MenuItem].
 * @param image will be displayed by the [MenuItem].
 * @param style name of a [MenuItem.MenuItemStyle].
 * @param init allows to customize the [MenuItem].
 * @return a new instance of [MenuItem] added to this [PopupMenu].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun PopupMenu.menuItem(
  text: String,
  image: Image,
  style: String = defaultStyle,
  init: (@Scene2dDsl MenuItem).() -> Unit = {},
): MenuItem {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val item = MenuItem(text, image, style)
  addItem(item)
  item.init()
  return item
}

/**
 * Adds new [MenuItem] to this [PopupMenu].
 * @param text will be displayed by the [MenuItem].
 * @param drawable will be displayed by the [MenuItem].
 * @param style name of a [MenuItem.MenuItemStyle].
 * @param init allows to customize the [MenuItem].
 * @return a new instance of [MenuItem] added to this [PopupMenu].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun PopupMenu.menuItem(
  text: String,
  drawable: Drawable,
  style: String = defaultStyle,
  init: (@Scene2dDsl MenuItem).() -> Unit = {},
): MenuItem {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val item = MenuItem(text, drawable, style)
  addItem(item)
  item.init()
  return item
}

/**
 * Adds new [MenuItem] to this [PopupMenu].
 * @param text will be displayed by the [MenuItem].
 * @param drawableName will be used to find a [Drawable] to be displayed by the [MenuItem].
 * @param style name of a [MenuItem.MenuItemStyle].
 * @param init allows to customize the [MenuItem].
 * @return a new instance of [MenuItem] added to this [PopupMenu].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
fun PopupMenu.menuItem(
  text: String,
  drawableName: String,
  style: String = defaultStyle,
  init: (@Scene2dDsl MenuItem).() -> Unit = {},
): MenuItem {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return menuItem(text, VisUI.getSkin().getDrawable(drawableName), style, init)
}
