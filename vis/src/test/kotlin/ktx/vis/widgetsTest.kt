package ktx.vis

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import org.junit.Assert.*
import org.junit.Test

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
