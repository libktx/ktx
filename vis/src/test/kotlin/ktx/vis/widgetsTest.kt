package ktx.vis

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito

/** @author Kotcrab */


class VisTableWidgetFactoryTest() : TableBasedWidgetTest({ table(false, it) })

class ButtonTableWidgetFactoryTest() : TableBasedWidgetTest({ buttonTable(false, it) })

class ToastTableFactoryTest() : TableBasedWidgetTest({ toastTable(false, it) })

class WindowWidgetFactoryTest() : TableBasedWidgetTest({ window("title", DEFAULT_STYLE, it) })

class ButtonWidgetFactoryTest : TableBasedWidgetTest({ actor(KButton(DEFAULT_STYLE), it) })

class VisCheckBoxWidgetFactoryTest : TableBasedWidgetTest({ actor(KVisCheckBox("", DEFAULT_STYLE), it) })

class VisRadioButtonWidgetFactoryTest : TableBasedWidgetTest({ actor(KVisRadioButton(""), it) })

class VisTextButtonWidgetFactoryTest : TableBasedWidgetTest({ actor(KVisTextButton("", DEFAULT_STYLE), it) })

class VisImageButtonWidgetFactoryTest : TableBasedWidgetTest({ actor(KVisImageButton(DEFAULT_STYLE), it) })

class VisImageTextButtonWidgetFactoryTest : TableBasedWidgetTest({ actor(KVisImageTextButton("", DEFAULT_STYLE), it) })

class VisTreeWidgetFactoryTest : WidgetGroupBasedWidgetTest({ actor(KVisTree(DEFAULT_STYLE), it) })

class StackWidgetFactoryTest() : WidgetGroupBasedWidgetTest(::stack)

class SpinnerWidgetFactoryTest : TableBasedWidgetTest({ actor(KSpinner(DEFAULT_STYLE, "", IntSpinnerModel(0, 0, 100)), it) })

class HorizontalGroupWidgetFactoryTest() : WidgetGroupBasedWidgetTest(::horizontalGroup)

class HorizontalFlowGroupWidgetFactoryTest() : WidgetGroupBasedWidgetTest({ horizontalFlowGroup(0f, it) })

class VerticalGroupWidgetFactoryTest() : WidgetGroupBasedWidgetTest(::verticalGroup)

class VerticalFlowGroupWidgetFactoryTest() : WidgetGroupBasedWidgetTest({ verticalFlowGroup(0f, it) })

class GridGroupWidgetFactoryTest() : WidgetGroupBasedWidgetTest({ gridGroup(1f, 1f, it) })

class FloatingGroupWidgetFactoryTest() : WidgetGroupBasedWidgetTest(::floatingGroup)

class FloatingGroupWidgetFactoryTestWithPrefSize() : WidgetGroupBasedWidgetTest({ floatingGroup(1f, 1f, it) })

class DragPaneWidgetFactoryTest() : WidgetGroupBasedWidgetTest({ actor(KDragPane(HorizontalGroup()), it) })

abstract class TableBasedWidgetTest(val widgetProvider: (TableWidgetFactory.() -> Unit) -> Table) : NeedsLibgdx() {
  @Test
  fun shouldAddActorToGroup() {
    var initInvoked = false
    widgetProvider {
      initInvoked = true
      val table = this as Table
      val childrenBeforeWidgetAdded = table.children.size
      val widgetCell = label("")
      assertNotNull(widgetCell)
      assertEquals(childrenBeforeWidgetAdded + 1, table.children.size)
      assertTrue(table.children.last() == widgetCell.actor)
    }
    assertTrue(initInvoked)
  }
}

abstract class WidgetGroupBasedWidgetTest(val widgetProvider: (WidgetGroupWidgetFactory.() -> Unit) -> WidgetGroup) : NeedsLibgdx() {
  @Test
  fun shouldAddActorToGroup() {
    var initInvoked = false
    widgetProvider {
      initInvoked = true
      val widgetGroup = this as WidgetGroup
      val childrenBeforeWidgetAdded = widgetGroup.children.size
      val widget = label("")
      assertNotNull(widget)
      assertEquals(childrenBeforeWidgetAdded + 1, widgetGroup.children.size)
      assertTrue(widgetGroup.children.last() == widget)
    }
    assertTrue(initInvoked)
  }
}

class KButtonTableTest : NeedsLibgdx() {
  @Test
  fun shouldAddActorToButtonGroup() {
    var actorAdded = false
    buttonTable {
      val buttonCell = button { }
      actorAdded = buttonGroup.buttons.contains(buttonCell.actor)
    }
    assertTrue(actorAdded)
  }
}

class ValidatorTest : NeedsLibgdx() {
  @Test
  fun shouldCreateValidator() {
    var initInvoked = false
    validator {
      initInvoked = true
    }
    assertTrue(initInvoked)
  }
}

class KTabbedPaneTest : NeedsLibgdx() {
  @Test
  fun shouldCreateTab() {
    val tabTitle = "Test Tab"
    var tab: Tab? = null
    var tabbedPane: TabbedPane? = null
    table {
      tabbedPane = tabbedPane {
        tab(tabTitle) {
          tab = this
        }
      }.widget
    }
    assertNotNull(tab)
    assertEquals(tab!!.tabTitle, tabTitle)
    assertEquals(tabbedPane!!.tabs.size, 1)
  }

  @Test
  fun shouldAllowToAddStandardTabManually() {
    table {
      tabbedPane {
        add(Mockito.mock(Tab::class.java))
      }
    }
  }

  @Test
  fun shouldAllowToInsertStandardTabManually() {
    table {
      tabbedPane {
        insert(0, Mockito.mock(Tab::class.java))
      }
    }
  }

  @Test
  fun shouldCreateTabContent() {
    var tab: Tab? = null
    table {
      tabbedPane {
        tab("") {
          tab = this
          label("")
        }
      }
    }
    assertNotNull(tab)
    assertEquals(tab!!.contentTable.children.size, 1)
  }

  @Test
  fun shouldAddTabContentToTable() {
    verticalGroup {
      val content = table()
      tabbedPane {
        addTabContentsTo(content)
        tab("") {
          label("")
        }
      }
      assertEquals(content.children.size, 1)
    }
  }

  @Test
  fun shouldAddTabContentToTableCell() {
    table {
      val content = table()
      tabbedPane {
        addTabContentsTo(content)
        tab("") {
          label("")
        }
      }
      assertEquals(content.actor.children.size, 1)
    }
  }

  @Test
  fun shouldAddTabContentToContainerCell() {
    table {
      val content = container<Table>()
      tabbedPane {
        addTabContentsTo(content)
        tab("") {

        }
      }
      assertNotNull(content.actor.actor)
    }
  }

  @Test
  fun shouldAddTabContentToContainer() {
    verticalGroup {
      val content = container<Table>()
      tabbedPane {
        addTabContentsTo(content)
        tab("") {

        }
      }
      assertNotNull(content.actor)
    }
  }

  @Test
  fun shouldAddTabContentToWidgetGroup() {
    verticalGroup {
      val content = verticalGroup()
      tabbedPane {
        addTabContentsTo(content)
        tab("") {

        }
      }
      assertEquals(content.children.size, 1)
    }
  }

  @Test
  fun shouldAddTabContentToWidgetGroupCell() {
    table {
      val content = verticalGroup()
      tabbedPane {
        addTabContentsTo(content)
        tab("") {

        }
      }
      assertEquals(content.actor.children.size, 1)
    }
  }
}
