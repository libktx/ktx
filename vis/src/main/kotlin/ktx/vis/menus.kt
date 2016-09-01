package ktx.vis

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu

/** @author Kotcrab */

/** Begins creation of new [PopupMenu]. */
inline fun popupMenu(styleName: String = DEFAULT_STYLE, init: PopupMenu.() -> Unit): PopupMenu {
  val popupMenu = PopupMenu(styleName)
  popupMenu.init()
  return popupMenu
}

/** Begins creation of new [MenuBar]. */
inline fun menuBar(styleName: String = DEFAULT_STYLE, init: MenuBar.() -> Unit): MenuBar {
  val menuBar = MenuBar(styleName)
  menuBar.init()
  return menuBar
}

/** Adds new [Menu] to this [MenuBar]. */
fun MenuBar.menu(menuTitle: String, styleName: String = DEFAULT_STYLE, init: PopupMenu.() -> Unit = {}): Menu {
  val menu = Menu(menuTitle, styleName)
  menu.init()
  this.addMenu(menu)
  return menu
}

/** Creates and assigns new sub menu to this [MenuItem]. */
fun MenuItem.subMenu(styleName: String = DEFAULT_STYLE, init: PopupMenu.() -> Unit = {}): PopupMenu {
  val subMenu = PopupMenu(styleName)
  setSubMenu(subMenu)
  subMenu.init()
  return subMenu
}

/** Adds new [MenuItem] to this [PopupMenu]. */
fun PopupMenu.menuItem(text: String, styleName: String = DEFAULT_STYLE, init: MenuItem.() -> Unit = {}): MenuItem
    = actor(MenuItem(text, styleName), init)

/** Adds new [MenuItem] to this [PopupMenu]. */
fun PopupMenu.menuItem(text: String, image: Image, styleName: String = DEFAULT_STYLE, init: MenuItem.() -> Unit = {}): MenuItem
    = actor(MenuItem(text, image, styleName), init)

/** Adds new [MenuItem] to this [PopupMenu]. */
fun PopupMenu.menuItem(text: String, drawable: Drawable, styleName: String = DEFAULT_STYLE, init: MenuItem.() -> Unit = {}): MenuItem
    = actor(MenuItem(text, drawable, styleName), init)

private fun <T : MenuItem> PopupMenu.actor(actor: T, init: T.() -> Unit): T {
  addItem(actor)
  actor.init()
  return actor
}
