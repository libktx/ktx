package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Test
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [RootWidget] factory methods for actors that are usually added directly to a stage.
 * Leverages [scene2d], as it implements [RootWidget] and does not modify the created actors.
 */
class RootActorFactoriesTest : NeedsLibGDX() {
  @Test
  fun `should create Window`() {
    val window = scene2d.window(title = "test")

    assertNotNull(window)
    assertEquals(Scene2DSkin.defaultSkin, window.skin)
    assertEquals("test", window.titleLabel.text.toString())
  }

  @Test
  fun `should create Window with init block`() {
    val window = scene2d.window(title = "test") {
      height = 100f
    }

    assertNotNull(window)
    assertEquals(Scene2DSkin.defaultSkin, window.skin)
    assertEquals("test", window.titleLabel.text.toString())
    assertEquals(100f, window.height, TOLERANCE)
  }

  @Test
  fun `should create Window with nested children`() {
    val label: Label

    val window = scene2d.window(title = "test") {
      label = label("Test")
    }

    assertTrue(label in window.children)
  }

  @Test
  fun `should create Dialog`() {
    val dialog = scene2d.dialog(title = "test")

    assertNotNull(dialog)
    assertEquals(Scene2DSkin.defaultSkin, dialog.skin)
    assertEquals("test", dialog.titleLabel.text.toString())
  }

  @Test
  fun `should create Dialog with init block`() {
    val dialog = scene2d.dialog(title = "test") {
      height = 100f
    }

    assertNotNull(dialog)
    assertEquals(Scene2DSkin.defaultSkin, dialog.skin)
    assertEquals("test", dialog.titleLabel.text.toString())
    assertEquals(100f, dialog.height, TOLERANCE)
  }

  @Test
  fun `should create Dialog with nested children`() {
    val label: Label

    val dialog = scene2d.dialog(title = "test") {
      label = label("Test")
    }

    assertTrue(label in dialog.children)
  }
}

/**
 * Tests factory methods without init blocks.
 */
class NoInitBlockActorFactoriesTest : NeedsLibGDX() {
  private fun <T : Actor> test(validate: (T) -> Unit = {}, widget: KWidget<*>.() -> T?) {
    // Using a parental widget that allows to use invoke actors' factory methods:
    val parent = scene2d.table()

    // Invoking widget-specific factory method:
    val child = parent.widget()

    // Ensuring the child is not null and owned by the parent:
    assertNotNull(child)
    assertTrue(child in parent.children)
    validate(child!!) // Additional validation specific to widget.
  }

  @Test
  fun `should create Button`() = test { button() }

  @Test
  fun `should create ButtonGroup`() = test { buttonGroup(minCheckedCount = 1, maxCheckedCount = 2) }

  @Test
  fun `should create CheckBox`() = test(
    widget = { checkBox("Test.") },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create Container`() = test { container() }

  @Test
  fun `should create HorizontalGroup`() = test { horizontalGroup() }

  @Test
  fun `should create Image`() = test(
    widget = { image(drawable = "button") },
    validate = {
      assertEquals(VisUI.getSkin().getDrawable("button"), it.drawable)
    })

  @Test
  fun `should create ImageButton`() = test { imageButton() }

  @Test
  fun `should create ImageTextButton`() = test(
    widget = { imageTextButton("Test.") },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create Label`() = test(
    widget = { label("Test.") },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create List`() = test { listWidgetOf<String>() }

  @Test
  fun `should create List with items`() = test(
    widget = { listWidgetOf(GdxArray.with("one", "two", "three")) },
    validate = {
      assertEquals(GdxArray.with("one", "two", "three"), it.items)
    })

  @Test
  fun `should create ProgressBar`() = test(
    widget = { progressBar(min = 1f, max = 2f, step = 0.5f) },
    validate = {
      assertEquals(1f, it.minValue, TOLERANCE)
      assertEquals(2f, it.maxValue, TOLERANCE)
      assertEquals(0.5f, it.stepSize, TOLERANCE)
    })

  @Test
  fun `should create ScrollPane`() = test { scrollPane() }

  @Test
  fun `should create SelectBox`() = test { selectBoxOf<String>() }

  @Test
  fun `should create SelectBox with items`() = test(
    widget = { selectBoxOf(GdxArray.with("one", "two", "three")) },
    validate = {
      assertEquals(GdxArray.with("one", "two", "three"), it.items)
    })

  @Test
  fun `should create Slider`() = test(
    widget = { slider(min = 1f, max = 2f, step = 0.5f) },
    validate = {
      assertEquals(1f, it.minValue, TOLERANCE)
      assertEquals(2f, it.maxValue, TOLERANCE)
      assertEquals(0.5f, it.stepSize, TOLERANCE)
    })

  @Test
  fun `should create SplitPane`() = test { splitPane() }

  @Test
  fun `should create Stack`() = test { stack() }

  @Test
  fun `should create Table`() = test { table() }

  @Test
  fun `should create TextArea`() = test(
    widget = { textArea("Test.") },
    validate = {
      assertEquals("Test.", it.text)
    })

  @Test
  fun `should create TextButton`() = test(
    widget = { textButton("Test.") },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create TextField`() = test(
    widget = { textField("Test.") },
    validate = {
      assertEquals("Test.", it.text)
    })

  @Test
  fun `should create Touchpad`() = test { touchpad(deadzone = 10f) }

  @Test
  fun `should create Tree`() = test { tree() }

  @Test
  fun `should create VerticalGroup`() = test { verticalGroup() }
}

/**
 * Tests inlined factory methods with init blocks.
 */
class InlinedInitBlockActorFactoriesTest : NeedsLibGDX() {
  /**
   * Tests and showcases how generic [KWidget] API - and its additional inlined methods - give access to specific actor
   * storage objects like [Cell] or [Node] through init block parameter.
   */
  @Test
  fun `should give access to widget specific storage objects`() {
    scene2d.stack {
      // In regular groups, children blocks point to the new actor as both 'this' and 'it'.
      label("Actor") {
        // No specialized storage objects - 'it' should point to the actor itself:
        assertTrue(it is Label)
        assertEquals("Actor", (it as Label).text.toString())
      }
    }
    scene2d.table {
      // In table-based groups, children blocks point to the new actor as 'this' and its cell as 'it'.
      label("Cell") {
        // Actors stored in cells:
        assertTrue(it.actor is Label)
      }
    }
    scene2d.tree {
      // In trees, children blocks point to the new actor as 'this' and node is the lambda parameter.
      label("Node") { node ->
        // Actors stored in tree nodes:
        assertTrue(node.actor is Label)
        node {
          label("NestedNode") {
            assertTrue(it.actor is Label)
            assertEquals("NestedNode", (it.actor as Label).text.toString())
          }
        }
      }
    }
  }

  private fun <T : Actor> test(validate: (T) -> Unit = {}, widget: KWidget<Cell<*>>.() -> T?) {
    // Creating builder context that allows to use actors factory methods:
    val parent = scene2d.table()

    // Invoking widget-specific factory method:
    val child = parent.widget()

    // Ensuring the child is not null and owned by the parent:
    assertNotNull(child)
    assertTrue(child in parent.children)
    assertEquals("For the purpose of this test, the actor must include 'color = Color.BLUE' in its init block.",
      Color.BLUE, child!!.color)
    validate(child) // Performing widget-specific validation.
  }

  @Test
  fun `should create Button`() = test {
    button {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create ButtonGroup`() = test {
    buttonGroup(minCheckedCount = 1, maxCheckedCount = 2) {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create CheckBox`() = test(
    widget = {
      checkBox("Test.") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create Container`() = test {
    container {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create HorizontalGroup`() = test {
    horizontalGroup {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create Image`() = test(
    widget = {
      image(drawable = "button") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals(VisUI.getSkin().getDrawable("button"), it.drawable)
    })

  @Test
  fun `should create ImageButton`() = test {
    imageButton {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create ImageTextButton`() = test(
    widget = {
      imageTextButton("Test.") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create Label`() = test(
    widget = {
      label("Test.") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create List`() = test(
    widget = {
      listWidget<String> {
        color = Color.BLUE
        // Adding list items:
        -"one"
        -"two"
        -"three"
      }
    },
    validate = {
      assertEquals(GdxArray.with("one", "two", "three"), it.items)
    })

  @Test
  fun `should create ProgressBar`() = test(
    widget = {
      progressBar(min = 1f, max = 2f, step = 0.5f) {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals(1f, it.minValue, TOLERANCE)
      assertEquals(2f, it.maxValue, TOLERANCE)
      assertEquals(0.5f, it.stepSize, TOLERANCE)
    })

  @Test
  fun `should create ScrollPane`() = test {
    scrollPane {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create SelectBox`() = test(
    widget = {
      selectBox<String> {
        color = Color.BLUE
        // Adding select box items:
        -"one"
        -"two"
        -"three"
      }
    },
    validate = {
      assertEquals(GdxArray.with("one", "two", "three"), it.items)
    })

  @Test
  fun `should create Slider`() = test(
    widget = {
      slider(min = 1f, max = 2f, step = 0.5f) {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals(1f, it.minValue, TOLERANCE)
      assertEquals(2f, it.maxValue, TOLERANCE)
      assertEquals(0.5f, it.stepSize, TOLERANCE)
    })

  @Test
  fun `should create SplitPane`() = test {
    splitPane {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create Stack`() = test {
    stack {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create Table`() = test(
    widget = {
      table {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals(Scene2DSkin.defaultSkin, it.skin)
    })

  @Test
  fun `should create TextArea`() = test(
    widget = {
      textArea("Test.") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals("Test.", it.text)
    })

  @Test
  fun `should create TextButton`() = test(
    widget = {
      textButton("Test.") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals("Test.", it.text.toString())
    })

  @Test
  fun `should create TextField`() = test(
    widget = {
      textField("Test.") {
        color = Color.BLUE
      }
    },
    validate = {
      assertEquals("Test.", it.text)
    })

  @Test
  fun `should create Touchpad`() = test {
    touchpad(deadzone = 10f) {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create Tree`() = test {
    tree {
      color = Color.BLUE
    }
  }

  @Test
  fun `should create VerticalGroup`() = test {
    verticalGroup {
      color = Color.BLUE
    }
  }
}
