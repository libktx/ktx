package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

/**
 * @param F type of [WidgetFactory] under test
 * @param FR return type of [WidgetFactory] under test
 * @author Kotcrab
 */
@Ignore("Base class for others tests should not be tested")
abstract class WidgetFactoryTest<F : WidgetFactory<FR>, FR> : NeedsLibgdx() {
  @Test
  fun shouldCreateLabel() = testFactoryMethod({ it.label("label") })

  @Test
  fun shouldCreateLinkLabel() = testFactoryMethod({ it.linkLabel("label") })

  @Test
  fun shouldCreateImageFromDrawable() = testFactoryMethod({ it.image(VisUI.getSkin().getDrawable("white")) })

  @Test
  fun shouldCreateImageFromName() = testFactoryMethod({ it.image("white") })

  @Test
  fun shouldCreateList() = testFactoryMethod({ it.list<String>() })

  @Test
  fun shouldCreateProgressBar() = testFactoryMethod({ it.progressBar() })

  @Test
  fun shouldCreateProgressBarFromStyle() = testFactoryMethod({ it.progressBar(styleName = "default-vertical") })

  @Test
  fun shouldCreateSelectBox() = testFactoryMethod({ it.selectBox<String>() })

  @Test
  fun shouldCreateSlider() = testFactoryMethod({ it.slider() })

  @Test
  fun shouldCreateSliderFromStyle() = testFactoryMethod({ it.slider(styleName = "default-vertical") })

  @Test
  fun shouldCreateTextArea() = testFactoryMethod({ it.textArea() })

  @Test
  fun shouldCreateHighlightTextArea() = testFactoryMethod({ it.highlightTextArea() })

  @Test
  fun shouldCreateScrollableTextArea() = testFactoryMethod({ it.scrollableTextArea() })

  @Test
  fun shouldCreateTextField() = testFactoryMethod({ it.textField() })

  @Test
  fun shouldCreateValidatableTextField() = testFactoryMethod({ it.validatableTextField("textfield") })

  @Test
  fun shouldCreateTouchpad() = testFactoryMethod({ it.touchpad(1f) })

  @Test
  fun shouldCreateBusyBar() = testFactoryMethod({ it.busyBar() })

  @Test
  fun shouldCreateSeparator() = testFactoryMethod({ it.separator() })

  @Test
  fun shouldCreateButton() = testFactoryMethod({ it.button() })

  @Test
  fun shouldCreateTextButton() = testFactoryMethod({ it.textButton("textbutton") })

  @Test
  fun shouldCreateImageButtonFromStyleName() = testFactoryMethod({ it.imageButton("default") })

  @Test
  fun shouldCreateImageButtonFromDrawable() = testFactoryMethod({ it.imageButton(VisUI.getSkin().getDrawable("white")) })

  @Test
  fun shouldCreateImageTextButton() = testFactoryMethod({ it.imageTextButton("imagetextbutton") })

  @Test
  fun shouldCreateCheckBox() = testFactoryMethod({ it.checkBox("checkbox") })

  @Test
  fun shouldCreateRadioButton() = testFactoryMethod({ it.radioButton("radiobutton") })

  @Test
  fun shouldCreateTree() = testFactoryMethod({ it.tree() })

  @Test
  fun shouldCreateSpinner() = testFactoryMethod({ it.spinner("spinner", IntSpinnerModel(0, 0, 100)) })

  @Test @Ignore("Unable to compile shader in test environment")
  fun shouldCreateBasicColorPicker() = testFactoryMethod({ it.basicColorPicker() })

  @Test @Ignore("Unable to compile shader in test environment")
  fun shouldCreateExtendedColorPicker() = testFactoryMethod({ it.extendedColorPicker() })

  @Test
  fun shouldCreateTable() = testFactoryMethod({ it.table { } })

  @Test
  fun shouldCreateHorizontalGroup() = testFactoryMethod({ it.horizontalGroup { } })

  @Test
  fun shouldCreateHorizontalFlowGroup() = testFactoryMethod({ it.horizontalFlowGroup { } })

  @Test
  fun shouldCreateVerticalGroup() = testFactoryMethod({ it.verticalGroup { } })

  @Test
  fun shouldCreateVerticalFlowGroup() = testFactoryMethod({ it.verticalFlowGroup { } })

  @Test
  fun shouldCreateGridGroup() = testFactoryMethod({ it.gridGroup { } })

  @Test
  fun shouldCreateFloatingGroup() = testFactoryMethod({ it.floatingGroup { } })

  @Test
  fun shouldCreateFloatingGroupFromPrefSize() = testFactoryMethod({ it.floatingGroup(1f, 1f) {} })

  @Test
  fun shouldCreateStack() = testFactoryMethod({ it.stack { } })

  @Test
  fun shouldCreateScrollPane() = testFactoryMethod({ it.scrollPane(table { }) })

  @Test
  fun shouldCreateSplitPane() = testFactoryMethod({ it.splitPane(table { }, table { }) })

  @Test
  fun shouldCreateSplitPaneFromStyle() = testFactoryMethod({ it.splitPane(table { }, table { }, styleName = "default-vertical") })

  @Test
  fun shouldCreateMultiSplitPane() = testFactoryMethod({ it.multiSplitPane() })

  @Test
  fun shouldCreateMultiSplitPaneFromStyle() = testFactoryMethod({ it.multiSplitPane(styleName = "default-vertical") })

  @Test
  fun shouldCreateContainer() = testFactoryMethod({ it.container(table { }) })

  @Test
  fun shouldCreateCollapsible() = testFactoryMethod({ it.collapsible(table { }) })

  @Test
  fun shouldCreateActor() = testFactoryMethod({ it.actor(Actor(), {}) })

  abstract fun testFactoryMethod(factoryMethodUnderTest: (F) -> FR)
}

class TableWidgetFactoryTest() : WidgetFactoryTest<TableWidgetFactory, Cell<*>>() {
  override fun testFactoryMethod(factoryMethodUnderTest: (TableWidgetFactory) -> Cell<*>) {
    var initInvoked = false
    table {
      initInvoked = true
      val childrenBeforeWidgetAdded = children.size
      val widgetCell = factoryMethodUnderTest(this)
      assertNotNull(widgetCell)
      assertEquals(childrenBeforeWidgetAdded + 1, children.size)
      assertTrue(children.last() == widgetCell.actor)
    }
    assertTrue(initInvoked)
  }
}

class WidgetGroupWidgetFactoryTest() : WidgetFactoryTest<WidgetGroupWidgetFactory, Any>() {
  override fun testFactoryMethod(factoryMethodUnderTest: (WidgetGroupWidgetFactory) -> Any) {
    var initInvoked = false
    horizontalGroup {
      initInvoked = true
      val childrenBeforeWidgetAdded = children.size
      val widget = factoryMethodUnderTest(this)
      assertNotNull(widget)
      assertEquals(childrenBeforeWidgetAdded + 1, children.size)
      assertTrue(children.last() == widget)
    }
    assertTrue(initInvoked)
  }
}


