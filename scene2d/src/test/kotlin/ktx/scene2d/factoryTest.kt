package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests factory methods without init blocks.
 * @author MJ
 */
class InitBlockActorFactoriesTest : NeedsLibGDX() {
  @Test
  fun shouldCreateButton() {
    var widget: Button? = null
    val parent = table {
      widget = button()
    }
    assertNotNull(widget)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateButtonGroup() {
    var widget: KButtonTable? = null
    val parent = table {
      widget = buttonGroup(minCheckedCount = 1, maxCheckedCount = 2)
    }
    assertNotNull(widget)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateCheckBox() {
    var widget: CheckBox? = null
    val parent = table {
      widget = checkBox("Test.")
    }
    assertNotNull(widget)
    assertEquals("Test.", widget!!.text.toString())
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateContainer() {
    var widget: Container<Actor>? = null
    val parent = table {
      widget = container()
    }
    assertNotNull(widget)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateHorizontalGroup() {
    var widget: HorizontalGroup? = null
    val parent = table {
      widget = horizontalGroup()
    }
    assertNotNull(widget)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateImage() {
    var widget: Image? = null
    val parent = table {
      widget = image(drawable = "button")
    }
    assertNotNull(widget)
    assertEquals(VisUI.getSkin().getDrawable("button"), widget!!.drawable)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateImageButton() {
    var widget: ImageButton? = null
    val parent = table {
      widget = imageButton()
    }
    assertNotNull(widget)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateImageTextButton() {
    var widget: ImageTextButton? = null
    val parent = table {
      widget = imageTextButton("Test.")
    }
    assertNotNull(widget)
    assertEquals("Test.", widget!!.text.toString())
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateLabel() {
    var widget: Label? = null
    val parent = table {
      widget = label("Test.")
    }
    assertNotNull(widget)
    assertEquals("Test.", widget!!.text.toString())
    assertTrue(widget in parent.children)
  }
}

/**
 * Tests inlined factory methods with init blocks.
 */
class InlinedActorFactoriesTest : NeedsLibGDX() {
  /**
   * Tests and showcases how generic [KWidget] API - and its additional inlined methods - give access to specific actor
   * storage objects like [Cell] or [Node] through init block parameter.
   */
  @Test
  fun shouldGiveAccessToWidgetSpecificStorageObjects() {
    stack {
      // In regular groups, children blocks point to the new actor as both 'this' and 'it'.
      label("Actor") {
        // No specialized storage objects - 'it' should point to the actor itself:
        assertTrue(it is Label)
        assertEquals("Actor", (it as Label).text.toString())
      }
    }
    table {
      // In table-based groups, children blocks point to the new actor as 'this' and its cell as 'it'.
      label("Cell") {
        // Actors stored in cells:
        assertTrue(it is Cell<*>)
        assertTrue(it.actor is Label)
      }
    }
    tree {
      // In trees, children blocks point to the new actor as 'this' and to its node as 'it'.
      label("Node") {
        // Actors stored in tree nodes:
        assertTrue(it is Node)
        assertTrue(it.actor is Label)
        it {
          label("NestedNode") {
            assertTrue(it is Node)
            assertTrue(it.actor is Label)
            assertEquals("NestedNode", (it.actor as Label).text.toString())
          }
        }
      }
    }
  }

  @Test
  fun shouldCreateButton() {
    var widget: Button? = null
    val parent = table {
      widget = button {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateButtonGroup() {
    var widget: KButtonTable? = null
    val parent = table {
      widget = buttonGroup(minCheckedCount = 1, maxCheckedCount = 2) {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateCheckBox() {
    var widget: CheckBox? = null
    val parent = table {
      widget = checkBox("Test.") {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals("Test.", widget!!.text.toString())
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateContainer() {
    var widget: Container<Actor>? = null
    val parent = table {
      widget = container {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateHorizontalGroup() {
    var widget: HorizontalGroup? = null
    val parent = table {
      widget = horizontalGroup {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateImage() {
    var widget: Image? = null
    val parent = table {
      widget = image(drawable = "button") {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals(VisUI.getSkin().getDrawable("button"), widget!!.drawable)
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateImageButton() {
    var widget: ImageButton? = null
    val parent = table {
      widget = imageButton {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateImageTextButton() {
    var widget: ImageTextButton? = null
    val parent = table {
      widget = imageTextButton("Test.") {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals("Test.", widget!!.text.toString())
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }

  @Test
  fun shouldCreateLabel() {
    var widget: Label? = null
    val parent = table {
      widget = label("Test.") {
        color = Color.CYAN
      }
    }
    assertNotNull(widget)
    assertEquals("Test.", widget!!.text.toString())
    assertEquals(Color.CYAN, widget!!.color)
    assertTrue(widget in parent.children)
  }
}
