package ktx.scene2d.vis

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter
import com.kotcrab.vis.ui.widget.ButtonBar
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.toast.ToastTable
import ktx.scene2d.ApplicationTest
import ktx.scene2d.KWidget
import ktx.scene2d.TOLERANCE
import ktx.scene2d.actors
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.mock
import com.badlogic.gdx.utils.Array as GdxArray

class TopLevelActorFactoriesTest : ApplicationTest() {
  @Test
  fun `should create VisWindow`() {
    val window = scene2d.visWindow(title = "test")

    assertNotNull(window)
    assertEquals("test", window.titleLabel.text.toString())
  }

  @Test
  fun `should create VisWindow with init block`() {
    val window =
      scene2d.visWindow(title = "test") {
        height = 100f
      }

    assertNotNull(window)
    assertEquals("test", window.titleLabel.text.toString())
    assertEquals(100f, window.height, TOLERANCE)
  }

  @Test
  fun `should create VisWindow with nested children`() {
    lateinit var label: VisLabel

    val window =
      scene2d.visWindow(title = "test") {
        label = visLabel("Test")
      }

    assertTrue(label in window.children)
  }

  @Test
  fun `should create VisDialog`() {
    val dialog = scene2d.visDialog(title = "test")

    assertNotNull(dialog)
    assertEquals("test", dialog.titleLabel.text.toString())
  }

  @Test
  fun `should create VisDialog with init block`() {
    val dialog =
      scene2d.visDialog(title = "test") {
        height = 100f
      }

    assertNotNull(dialog)
    assertEquals("test", dialog.titleLabel.text.toString())
    assertEquals(100f, dialog.height, TOLERANCE)
  }

  @Test
  fun `should create VisDialog with nested children`() {
    lateinit var label: VisLabel

    val dialog =
      scene2d.visDialog(title = "test") {
        label = visLabel("Test")
      }

    assertTrue(label in dialog.children)
  }

  @Test
  fun `should create ToastTable`() {
    val table = scene2d.toastTable()

    assertNotNull(table)
    assertSame(VisUI.getSkin(), table.skin)
  }

  @Test
  fun `should create ToastTable with init block`() {
    val table =
      scene2d.toastTable {
        height = 100f
      }

    assertNotNull(table)
    assertEquals(100f, table.height, TOLERANCE)
  }

  @Test
  fun `should create ToastTable with nested children`() {
    lateinit var label: VisLabel

    val table =
      scene2d.toastTable {
        label = visLabel("Test")
      }

    assertTrue(label in table.children)
  }

  @Test
  fun `should add top-level actors to Stage`() {
    val stage = Stage(mock(), mock())
    lateinit var window: VisWindow
    lateinit var dialog: VisDialog
    lateinit var table: ToastTable
    lateinit var label: VisLabel

    stage.actors {
      window = visWindow("Test")
      dialog = visDialog("Test")
      table =
        toastTable {
          label = visLabel("Test")
        }
    }

    assertTrue(window in stage.actors)
    assertTrue(window in stage.root.children)
    assertTrue(dialog in stage.actors)
    assertTrue(dialog in stage.root.children)
    assertTrue(table in stage.actors)
    assertTrue(table in stage.root.children)
    assertFalse(label in stage.actors)
    assertFalse(label in stage.root.children)
    assertTrue(label in table.children)
  }
}

/**
 * Tests factory methods without init blocks.
 */
class NoInitBlockActorFactoriesTest : ApplicationTest() {
  private fun <T : Actor> test(
    validate: (T) -> Unit = {},
    widget: KWidget<*>.() -> T?,
  ) {
    // Using a parental widget that allows to use invoke actors' factory methods:
    val parent = scene2d.visTable()

    // Invoking widget-specific factory method:
    val child = parent.widget()

    // Ensuring the child is not null and owned by the parent:
    assertNotNull(child)
    assertTrue(child in parent.children)
    validate(child!!) // Additional validation specific to widget.
  }

  @Test
  fun `should create VisLabel`() =
    test(
      widget = { visLabel("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create LinkLabel`() =
    test(
      widget = { linkLabel("Text.", "URL") },
      validate = {
        assertEquals("Text.", it.text.toString())
        assertEquals("URL", it.url)
      },
    )

  @Test
  fun `should create VisImage with drawable`() =
    test(
      widget = { visImage(VisUI.getSkin().getDrawable("button")) },
      validate = {
        assertSame(VisUI.getSkin().getDrawable("button"), it.drawable)
      },
    )

  @Test
  fun `should create VisImage with drawable name`() =
    test(
      widget = { visImage("button") },
      validate = {
        assertSame(VisUI.getSkin().getDrawable("button"), it.drawable)
      },
    )

  @Test
  fun `should create VisImage with nine patch`() = test { visImage(VisUI.getSkin().getPatch("button")) }

  @Test
  fun `should create VisImage with texture`() = test { visImage(VisUI.getSkin().getRegion("button").texture) }

  @Test
  fun `should create VisImage with texture region`() = test { visImage(VisUI.getSkin().getRegion("button")) }

  @Test
  fun `should create VisList`() = test { visListOf<String>() }

  @Test
  fun `should create VisList with items`() =
    test(
      widget = { visListOf(GdxArray.with("one", "two", "three")) },
      validate = {
        assertEquals(GdxArray.with("one", "two", "three"), it.items)
      },
    )

  @Test
  fun `should create VisProgressBar`() =
    test(
      widget = { visProgressBar(min = 1f, max = 2f, step = 0.5f) },
      validate = {
        assertEquals(1f, it.minValue, TOLERANCE)
        assertEquals(2f, it.maxValue, TOLERANCE)
        assertEquals(0.5f, it.stepSize, TOLERANCE)
      },
    )

  @Test
  fun `should create VisSelectBox`() = test { visSelectBoxOf<String>() }

  @Test
  fun `should create VisSelectBox with items`() =
    test(
      widget = { visSelectBoxOf(GdxArray.with("one", "two", "three")) },
      validate = {
        assertEquals(GdxArray.with("one", "two", "three"), it.items)
      },
    )

  @Test
  fun `should create VisSlider`() =
    test(
      widget = { visSlider(min = 1f, max = 2f, step = 0.5f) },
      validate = {
        assertEquals(1f, it.minValue, TOLERANCE)
        assertEquals(2f, it.maxValue, TOLERANCE)
        assertEquals(0.5f, it.stepSize, TOLERANCE)
      },
    )

  @Test
  fun `should create VisTextArea`() =
    test(
      widget = { visTextArea("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create HighlightTextArea`() =
    test(
      widget = { highlightTextArea("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create ScrollableTextArea`() =
    test(
      widget = { scrollableTextArea("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create VisTextField`() =
    test(
      widget = { visTextField("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create ValidatableTextField`() =
    test(
      widget = { visValidatableTextField("Test.") },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create BusyBar`() = test { busyBar() }

  @Test
  fun `should create Separator`() = test { separator() }

  @Test
  fun `should create VisTextButton`() =
    test(
      widget = { visTextButton("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisImageButton`() = test { visImageButton() }

  @Test
  fun `should create VisImageTextButton`() =
    test(
      widget = { visImageTextButton("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisCheckBox`() =
    test(
      widget = { visCheckBox("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisRadioButton`() =
    test(
      widget = { visRadioButton("Test.") },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisTree`() = test { visTree() }

  @Test
  @Ignore("Unable to compile shader in test environment.")
  fun `should create BasicColorPicker`() = test { basicColorPicker() }

  @Test
  @Ignore("Unable to compile shader in test environment.")
  fun `should create ExtendedColorPicker`() = test { extendedColorPicker() }

  @Test
  fun `should create Spinner`() =
    test(
      widget = { spinner("Name", IntSpinnerModel(0, 0, 10)) },
      validate = {
        assertEquals("Name", (it.children[0] as Label).text.toString())
      },
    )

  @Test
  fun `should create VisTable`() = test { visTable() }

  @Test
  @Suppress("DEPRECATION")
  fun `should create HorizontalFlowGroup`() =
    test(
      widget = { horizontalFlowGroup(spacing = 10f) },
      validate = {
        assertEquals(10f, it.spacing, TOLERANCE)
      },
    )

  @Test
  @Suppress("DEPRECATION")
  fun `should create VerticalFlowGroup`() =
    test(
      widget = { verticalFlowGroup(spacing = 10f) },
      validate = {
        assertEquals(10f, it.spacing, TOLERANCE)
      },
    )

  @Test
  fun `should create FlowGroup`() =
    test(
      widget = { flowGroup(vertical = true, spacing = 10f) },
      validate = {
        assertTrue(it.isVertical)
        assertEquals(10f, it.spacing, TOLERANCE)
      },
    )

  @Test
  fun `should create GridGroup`() =
    test(
      widget = { gridGroup(itemSize = 100f, spacing = 10f) },
      validate = {
        assertEquals(10f, it.spacing, TOLERANCE)
        assertEquals(100f, it.itemWidth, TOLERANCE)
        assertEquals(100f, it.itemHeight, TOLERANCE)
      },
    )

  @Test
  fun `should create FloatingGroup`() = test { floatingGroup() }

  @Test
  fun `should create DragPane`() = test { dragPane() }

  @Test
  fun `should create VisScrollPane`() = test { visScrollPane() }

  @Test
  fun `should create VisSplitPane`() = test { visSplitPane() }

  @Test
  fun `should create MultiSplitPane`() = test { multiSplitPane() }

  @Test
  fun `should create CollapsibleWidget`() = test { collapsible() }

  @Test
  fun `should create HorizontalCollapsibleWidget`() = test { horizontalCollapsible() }

  @Test
  fun `should create ButtonBar`() {
    val parent = scene2d.visTable()

    val buttonBar = parent.buttonBar()

    assertNotNull(buttonBar)
    assertEquals(1, parent.children.size)
    assertTrue(parent.children[0] is Table)
  }

  @Test
  fun `should create ButtonBar with order`() {
    val parent = scene2d.visTable()

    val buttonBar = parent.buttonBar(ButtonBar.LINUX_ORDER)

    assertNotNull(buttonBar)
    assertEquals(1, parent.children.size)
    assertTrue(parent.children[0] is Table)
  }

  @Test
  fun `should create ListView`() {
    val parent = scene2d.visTable()

    val listView = parent.listView(SimpleListAdapter(GdxArray.with("1", "2")))

    assertNotNull(listView)
    assertEquals(listOf("1", "2"), listView.adapter.iterable().toList())
    assertTrue(listView.mainTable in parent.children)
  }

  @Test
  fun `should create TabbedPane`() {
    val parent = scene2d.visTable()

    val pane = parent.tabbedPane()

    assertNotNull(pane)
    assertTrue(pane.table in parent.children)
  }
}

/**
 * Tests inlined factory methods with init blocks.
 */
class InlinedInitBlockActorFactoriesTest : ApplicationTest() {
  private fun <T : Actor> test(
    validate: (T) -> Unit = {},
    widget: KWidget<Cell<*>>.() -> T?,
  ) {
    // Creating builder context that allows to use actors factory methods:
    val parent = scene2d.table()

    // Invoking widget-specific factory method:
    val child = parent.widget()

    // Ensuring the child is not null and owned by the parent:
    assertNotNull(child)
    assertTrue(child in parent.children)
    assertEquals(
      "For the purpose of this test, the actor must include 'color = Color.BLUE' in its init block.",
      Color.BLUE,
      child!!.color,
    )
    validate(child) // Performing widget-specific validation.
  }

  @Test
  fun `should create VisLabel`() =
    test(
      widget = { visLabel("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create LinkLabel`() =
    test(
      widget = { linkLabel("Text.", "URL") { color = Color.BLUE } },
      validate = {
        assertEquals("Text.", it.text.toString())
        assertEquals("URL", it.url)
      },
    )

  @Test
  fun `should create VisImage with drawable`() =
    test(
      widget = { visImage(VisUI.getSkin().getDrawable("button")) { color = Color.BLUE } },
      validate = {
        assertSame(VisUI.getSkin().getDrawable("button"), it.drawable)
      },
    )

  @Test
  fun `should create VisImage with drawable name`() =
    test(
      widget = { visImage("button") { color = Color.BLUE } },
      validate = {
        assertSame(VisUI.getSkin().getDrawable("button"), it.drawable)
      },
    )

  @Test
  fun `should create VisImage with nine patch`() =
    test {
      visImage(VisUI.getSkin().getPatch("button")) { color = Color.BLUE }
    }

  @Test
  fun `should create VisImage with texture`() =
    test {
      visImage(VisUI.getSkin().getRegion("button").texture) { color = Color.BLUE }
    }

  @Test
  fun `should create VisImage with texture region`() =
    test {
      visImage(VisUI.getSkin().getRegion("button")) { color = Color.BLUE }
    }

  @Test
  fun `should create VisList`() =
    test(
      widget = {
        visList<String> {
          color = Color.BLUE
          // Adding list items:
          -"one"
          -"two"
          -"three"
        }
      },
      validate = {
        assertEquals(GdxArray.with("one", "two", "three"), it.items)
      },
    )

  @Test
  fun `should create VisProgressBar`() =
    test(
      widget = { visProgressBar(min = 1f, max = 2f, step = 0.5f) { color = Color.BLUE } },
      validate = {
        assertEquals(1f, it.minValue, TOLERANCE)
        assertEquals(2f, it.maxValue, TOLERANCE)
        assertEquals(0.5f, it.stepSize, TOLERANCE)
      },
    )

  @Test
  fun `should create VisSelectBox`() =
    test(
      widget = {
        visSelectBox<String> {
          color = Color.BLUE
          // Adding select box items:
          -"one"
          -"two"
          -"three"
        }
      },
      validate = {
        assertEquals(GdxArray.with("one", "two", "three"), it.items)
      },
    )

  @Test
  fun `should create VisSlider`() =
    test(
      widget = { visSlider(min = 1f, max = 2f, step = 0.5f) { color = Color.BLUE } },
      validate = {
        assertEquals(1f, it.minValue, TOLERANCE)
        assertEquals(2f, it.maxValue, TOLERANCE)
        assertEquals(0.5f, it.stepSize, TOLERANCE)
      },
    )

  @Test
  fun `should create VisTextArea`() =
    test(
      widget = { visTextArea("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create HighlightTextArea`() =
    test(
      widget = { highlightTextArea("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create ScrollableTextArea`() =
    test(
      widget = { scrollableTextArea("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create VisTextField`() =
    test(
      widget = { visTextField("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create ValidatableTextField`() =
    test(
      widget = { visValidatableTextField("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text)
      },
    )

  @Test
  fun `should create BusyBar`() = test { busyBar { color = Color.BLUE } }

  @Test
  fun `should create Separator`() = test { separator { color = Color.BLUE } }

  @Test
  fun `should create VisTextButton`() =
    test(
      widget = { visTextButton("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisImageButton`() = test { visImageButton { color = Color.BLUE } }

  @Test
  fun `should create VisImageTextButton`() =
    test(
      widget = { visImageTextButton("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisCheckBox`() =
    test(
      widget = { visCheckBox("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisRadioButton`() =
    test(
      widget = { visRadioButton("Test.") { color = Color.BLUE } },
      validate = {
        assertEquals("Test.", it.text.toString())
      },
    )

  @Test
  fun `should create VisTree`() = test { visTree { color = Color.BLUE } }

  @Test
  @Ignore("Unable to compile shader in test environment.")
  fun `should create BasicColorPicker`() = test { basicColorPicker { color = Color.BLUE } }

  @Test
  @Ignore("Unable to compile shader in test environment.")
  fun `should create ExtendedColorPicker`() = test { extendedColorPicker { color = Color.BLUE } }

  @Test
  fun `should create Spinner`() =
    test(
      widget = {
        spinner("Name", IntSpinnerModel(0, 0, 10)) {
          color = Color.BLUE
        }
      },
      validate = {
        assertEquals("Name", (it.children[0] as Label).text.toString())
      },
    )

  @Test
  fun `should create VisTable`() = test { visTable { color = Color.BLUE } }

  @Test
  @Suppress("DEPRECATION")
  fun `should create HorizontalFlowGroup`() =
    test(
      widget = { horizontalFlowGroup(spacing = 10f) { color = Color.BLUE } },
      validate = {
        assertEquals(10f, it.spacing, TOLERANCE)
      },
    )

  @Test
  @Suppress("DEPRECATION")
  fun `should create VerticalFlowGroup`() =
    test(
      widget = { verticalFlowGroup(spacing = 10f) { color = Color.BLUE } },
      validate = {
        assertEquals(10f, it.spacing, TOLERANCE)
      },
    )

  @Test
  fun `should create FlowGroup`() =
    test(
      widget = { flowGroup(vertical = false, spacing = 10f) { color = Color.BLUE } },
      validate = {
        assertFalse(it.isVertical)
        assertEquals(10f, it.spacing, TOLERANCE)
      },
    )

  @Test
  fun `should create GridGroup`() =
    test(
      widget = { gridGroup(itemSize = 100f, spacing = 10f) { color = Color.BLUE } },
      validate = {
        assertEquals(10f, it.spacing, TOLERANCE)
        assertEquals(100f, it.itemWidth, TOLERANCE)
        assertEquals(100f, it.itemHeight, TOLERANCE)
      },
    )

  @Test
  fun `should create FloatingGroup`() = test { floatingGroup { color = Color.BLUE } }

  @Test
  fun `should create DragPane`() = test { dragPane { color = Color.BLUE } }

  @Test
  fun `should create VisScrollPane`() = test { visScrollPane { color = Color.BLUE } }

  @Test
  fun `should create VisSplitPane`() = test { visSplitPane { color = Color.BLUE } }

  @Test
  fun `should create MultiSplitPane`() = test { multiSplitPane { color = Color.BLUE } }

  @Test
  fun `should create CollapsibleWidget`() = test { collapsible { color = Color.BLUE } }

  @Test
  fun `should create HorizontalCollapsibleWidget`() = test { horizontalCollapsible { color = Color.BLUE } }

  @Test
  fun `should create ButtonBar`() {
    val parent = scene2d.visTable()

    val buttonBar =
      parent.buttonBar {
        isIgnoreSpacing = true
      }

    assertNotNull(buttonBar)
    assertTrue(buttonBar.isIgnoreSpacing)
    assertEquals(1, parent.children.size)
    assertTrue(parent.children[0] is Table)
  }

  @Test
  fun `should create ButtonBar with order`() {
    val parent = scene2d.visTable()

    val buttonBar =
      parent.buttonBar(ButtonBar.LINUX_ORDER) {
        isIgnoreSpacing = true
      }

    assertNotNull(buttonBar)
    assertTrue(buttonBar.isIgnoreSpacing)
    assertEquals(1, parent.children.size)
    assertTrue(parent.children[0] is Table)
  }

  @Test
  fun `should create ListView`() {
    val parent = scene2d.visTable()

    val listView =
      parent.listView(SimpleListAdapter(GdxArray.with("1", "2"))) {
        mainTable.color = Color.BLUE
      }

    assertNotNull(listView)
    assertEquals(listView.mainTable.color, Color.BLUE)
    assertEquals(listOf("1", "2"), listView.adapter.iterable().toList())
    assertTrue(listView.mainTable in parent.children)
  }

  @Test
  fun `should create TabbedPane`() {
    val parent = scene2d.visTable()

    val pane =
      parent.tabbedPane {
        table.color = Color.BLUE
      }

    assertNotNull(pane)
    assertEquals(pane.table.color, Color.BLUE)
    assertTrue(pane.table in parent.children)
  }
}
