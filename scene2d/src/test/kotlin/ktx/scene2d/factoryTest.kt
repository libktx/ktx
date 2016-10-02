package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List as ListWidget
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.utils.Array as GdxArray
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests factory methods without init blocks.
 * @author MJ
 */
class NoInitBlockActorFactoriesTest : NeedsLibGDX() {
  private fun <T : Actor> test(validate: (T) -> Unit = {}, widget: KWidget<*>.() -> T?) {
    val parent = table {}
    val child = parent.widget()
    assertNotNull(child)
    assertTrue(child in parent.children)
    validate(child!!)
  }

  @Test
  fun shouldCreateButton() = test { button() }

  @Test
  fun shouldCreateButtonGroup() = test { buttonGroup(minCheckedCount = 1, maxCheckedCount = 2) }

  @Test
  fun shouldCreateCheckBox() = test(widget = { checkBox("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      })

  @Test
  fun shouldCreateContainer() = test { container() }

  @Test
  fun shouldCreateHorizontalGroup() = test { horizontalGroup() }

  @Test
  fun shouldCreateImage() = test(widget = { image(drawable = "button") },
      validate = {
        assertEquals(VisUI.getSkin().getDrawable("button"), it.drawable)
      })

  @Test
  fun shouldCreateImageButton() = test { imageButton() }

  @Test
  fun shouldCreateImageTextButton() = test(widget = { imageTextButton("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      })

  @Test
  fun shouldCreateLabel() = test(widget = { label("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      })

  @Test
  fun shouldCreateList() = test { listWidgetOf<String>() }

  @Test
  fun shouldCreateListWithItems() = test(widget = { listWidgetOf(GdxArray.with("one", "two", "three")) },
      validate = {
        assertEquals(GdxArray.with("one", "two", "three"), it.items)
      })

  @Test
  fun shouldCreateProgressBar() = test(widget = { progressBar(min = 1f, max = 2f, step = 0.5f) },
      validate = {
        assertEquals(1f, it.minValue, TOLERANCE)
        assertEquals(2f, it.maxValue, TOLERANCE)
        assertEquals(0.5f, it.stepSize, TOLERANCE)
      })

  @Test
  fun shouldCreateScrollPane() = test { scrollPane() }

  @Test
  fun shouldCreateSelectBox() = test { selectBoxOf<String>() }

  @Test
  fun shouldCreateSelectBoxWithItems() = test(widget = { selectBoxOf(GdxArray.with("one", "two", "three")) },
      validate = {
        assertEquals(GdxArray.with("one", "two", "three"), it.items)
      })

  @Test
  fun shouldCreateSlider() = test(widget = { slider(min = 1f, max = 2f, step = 0.5f) },
      validate = {
        assertEquals(1f, it.minValue, TOLERANCE)
        assertEquals(2f, it.maxValue, TOLERANCE)
        assertEquals(0.5f, it.stepSize, TOLERANCE)
      })

  @Test
  fun shouldCreateSplitPane() = test { splitPane() }

  @Test
  fun shouldCreateStack() = test { stack() }

  @Test
  fun shouldCreateTable() = test { table() }

  @Test
  fun shouldCreateTextArea() = test(widget = { textArea("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      })

  @Test
  fun shouldCreateTextButton() = test(widget = { textButton("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      })

  @Test
  fun shouldCreateTextField() = test(widget = { textField("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      })

  @Test
  fun shouldCreateTouchpad() = test { touchpad(deadzone = 10f) }

  @Test
  fun shouldCreateTree() = test { tree() }

  @Test
  fun shouldCreateVerticalGroup() = test { verticalGroup() }
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

  private fun <T : Actor> test(validate: (T) -> Unit = {}, widget: KWidget<Cell<*>>.() -> T?) {
    val parent = table {}
    val child = parent.widget()
    assertNotNull(child)
    assertTrue(child in parent.children)
    assertEquals("For the purpose of this test, the actor must include 'color = Color.BLUE' in its init block.",
        Color.BLUE, child!!.color)
    validate(child)
  }


  @Test
  fun shouldCreateButton() = test {
    button {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateButtonGroup() = test {
    buttonGroup(minCheckedCount = 1, maxCheckedCount = 2) {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateCheckBox() = test(widget = {
    checkBox("Test.") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals("Test.", it.text.toString())
  })

  @Test
  fun shouldCreateContainer() = test {
    container {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateHorizontalGroup() = test {
    horizontalGroup {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateImage() = test(widget = {
    image(drawable = "button") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals(VisUI.getSkin().getDrawable("button"), it.drawable)
  })

  @Test
  fun shouldCreateImageButton() = test {
    imageButton {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateImageTextButton() = test(widget = {
    imageTextButton("Test.") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals("Test.", it.text.toString())
  })

  @Test
  fun shouldCreateLabel() = test(widget = {
    label("Test.") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals("Test.", it.text.toString())
  })

  @Test
  fun shouldCreateList() = test(widget = {
    listWidget<String, Cell<*>> {
      color = Color.BLUE
      assertTrue(it is Cell<*>)
      // Adding list items:
      -"one"
      -"two"
      -"three"
    }
  }, validate = {
    assertEquals(GdxArray.with("one", "two", "three"), it.items)
  })

  @Test
  fun shouldCreateProgressBar() = test(widget = {
    progressBar(min = 1f, max = 2f, step = 0.5f) {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals(1f, it.minValue, TOLERANCE)
    assertEquals(2f, it.maxValue, TOLERANCE)
    assertEquals(0.5f, it.stepSize, TOLERANCE)
  })

  @Test
  fun shouldCreateScrollPane() = test {
    scrollPane {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateSelectBox() = test(widget = {
    selectBox<String, Cell<*>> {
      color = Color.BLUE
      assertTrue(it is Cell<*>)
      // Adding select box items:
      -"one"
      -"two"
      -"three"
    }
  }, validate = {
    assertEquals(GdxArray.with("one", "two", "three"), it.items)
  })

  @Test
  fun shouldCreateSlider() = test(widget = {
    slider(min = 1f, max = 2f, step = 0.5f) {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals(1f, it.minValue, TOLERANCE)
    assertEquals(2f, it.maxValue, TOLERANCE)
    assertEquals(0.5f, it.stepSize, TOLERANCE)
  })

  @Test
  fun shouldCreateSplitPane() = test {
    splitPane {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateStack() = test {
    stack {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateTable() = test(widget = {
    table {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals(Scene2DSkin.defaultSkin, it.skin)
  })

  @Test
  fun shouldCreateTextArea() = test(widget = {
    textArea("Test.") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals("Test.", it.text)
  })

  @Test
  fun shouldCreateTextButton() = test(widget = {
    textButton("Test.") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals("Test.", it.text.toString())
  })

  @Test
  fun shouldCreateTextField() = test(widget = {
    textField("Test.") {
      color = Color.BLUE
    }
  }, validate = {
    assertEquals("Test.", it.text)
  })

  @Test
  fun shouldCreateTouchpad() = test {
    touchpad(deadzone = 10f) {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateTree() = test {
    tree {
      color = Color.BLUE
    }
  }

  @Test
  fun shouldCreateVerticalGroup() = test {
    verticalGroup {
      color = Color.BLUE
    }
  }
}
