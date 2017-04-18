package ktx.vis

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Test

/** @author Kotcrab */
class MenusTest : NeedsLibGDX() {
  @Test
  fun shouldCreatePopupMenu() {
    var initInvoked = false
    popupMenu {
      initInvoked = true
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateMenuBar() {
    var initInvoked = false
    menuBar {
      initInvoked = true
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateMenu() {
    var initInvoked = false
    menuBar {
      val title = "Test Menu"
      val menu = menu(title) {
        initInvoked = true
      }
      assertEquals(menu.title, title)
      assertEquals(this.table.children.size, 1)
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateMenuItem() {
    var initInvoked = false
    popupMenu {
      val text = "Test Item"
      menuItem(text) {
        initInvoked = true
        assertEquals(this.text.toString(), text)
      }
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateMenuItemWithDrawable() {
    var initInvoked = false
    popupMenu {
      val text = "Test Item"
      val drawable = VisUI.getSkin().getDrawable("white")
      menuItem(text, drawable) {
        initInvoked = true
        assertEquals(this.text.toString(), text)
        assertEquals(this.image.drawable, drawable)
      }
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateMenuItemWithImage() {
    var initInvoked = false
    popupMenu {
      val text = "Test Item"
      val image = Image(VisUI.getSkin().getDrawable("white"))
      menuItem(text, image) {
        initInvoked = true
        assertEquals(this.text.toString(), text)
        assertEquals(this.image, image)
      }
    }
    assertTrue(initInvoked)
  }

  @Test
  fun shouldCreateSubMenu() {
    var initInvoked = false
    popupMenu {
      menuItem("") {
        subMenu {
          initInvoked = true
        }
        assertNotNull(this.subMenu)
      }
    }
    assertTrue(initInvoked)
  }
}
