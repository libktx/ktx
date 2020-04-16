package ktx.scene2d.vis

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ktx.scene2d.NeedsLibGDX
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests menu extensions.
 */
class MenusTest : NeedsLibGDX() {
  @Test
  fun `should create PopupMenu`() {
    var initInvoked = false

    val menu = scene2d.popupMenu {
      initInvoked = true
    }

    assertNotNull(menu)
    assertTrue(initInvoked)
  }

  @Test
  fun `should create MenuBar`() {
    var initInvoked = false

    val menu = scene2d.menuBar {
      initInvoked = true
    }

    assertNotNull(menu)
    assertTrue(initInvoked)
  }

  @Test
  fun `should add MenuBar table to parent`() {
    lateinit var menuBar: MenuBar

    val table = scene2d.table {
      menuBar = menuBar()
    }

    assertTrue(menuBar.table in table.children)
  }

  @Test
  fun `should create Menu`() {
    var initInvoked = false
    lateinit var menu: Menu

    val menuBar = scene2d.menuBar {
      menu = menu("Test menu") {
        initInvoked = true
      }
    }

    assertEquals("Test menu", menu.title)
    assertEquals(menuBar.table.children.size, 1)
    assertTrue(initInvoked)
  }

  @Test
  fun `should create MenuItem`() {
    var initInvoked = false
    lateinit var menuItem: MenuItem

    scene2d.popupMenu {
      menuItem = menuItem("Test item") {
        initInvoked = true
      }
    }

    assertEquals("Test item", menuItem.text.toString())
    assertTrue(initInvoked)
  }

  @Test
  fun `should create MenuItem with Drawable`() {
    var initInvoked = false
    val drawable = VisUI.getSkin().getDrawable("white")
    lateinit var menuItem: MenuItem

    scene2d.popupMenu {
      menuItem = menuItem("Test item", drawable) {
        initInvoked = true
      }
    }

    assertEquals("Test item", menuItem.text.toString())
    assertSame(drawable, menuItem.image.drawable)
    assertTrue(initInvoked)
  }

  @Test
  fun `should create MenuItem with Drawable name`() {
    var initInvoked = false
    val drawableName = "white"
    lateinit var menuItem: MenuItem

    scene2d.popupMenu {
      menuItem = menuItem("Test item", drawableName = drawableName) {
        initInvoked = true
      }
    }

    assertEquals("Test item", menuItem.text.toString())
    assertSame(VisUI.getSkin().getDrawable(drawableName), menuItem.image.drawable)
    assertTrue(initInvoked)
  }

  @Test
  fun `should create MenuItem with Image`() {
    var initInvoked = false
    val image = Image(VisUI.getSkin().getDrawable("white"))
    lateinit var menuItem: MenuItem

    scene2d.popupMenu {
      menuItem = menuItem("Test item", image) {
        initInvoked = true
      }
    }

    assertEquals("Test item", menuItem.text.toString())
    assertSame(image, menuItem.image)
    assertTrue(initInvoked)
  }

  @Test
  fun `should create submenu`() {
    var initInvoked = false
    lateinit var menuItem: MenuItem
    lateinit var subMenu: PopupMenu

    scene2d.popupMenu {
      menuItem = menuItem("") {
        subMenu = subMenu {
          initInvoked = true
        }
      }
    }

    assertNotNull(subMenu)
    assertSame(subMenu, menuItem.subMenu)
    assertTrue(initInvoked)
  }
}
