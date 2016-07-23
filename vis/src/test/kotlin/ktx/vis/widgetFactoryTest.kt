package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

/**
 * @param F type of [WidgetFactory] under test
 * @param FR return type of [WidgetFactory] under test
 * @author Kotcrab
 */
@Ignore("Base class for others tests should not be tested")
abstract class WidgetFactoryTest<F : WidgetFactory<FR>, FR> {
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
  fun shouldCreateActor() = testFactoryMethod({ it.actor(Actor(), {}) })

  abstract fun testFactoryMethod(factoryMethodUnderTestProvider: (F) -> FR)
}

abstract class TableWidgetFactoryTest(val tableBasedGroupUnderTestProvider: (TableWidgetFactory.() -> Unit) -> WidgetGroup)
: WidgetFactoryTest<TableWidgetFactory, Cell<*>>() {
  override fun testFactoryMethod(factoryMethodUnderTestProvider: (TableWidgetFactory) -> Cell<*>) {
    var initInvoked = false
    tableBasedGroupUnderTestProvider {
      initInvoked = true
      val table = this as Table
      val childrenBeforeWidgetAdded = table.children.size
      val widgetCell = factoryMethodUnderTestProvider(this)
      assertNotNull(widgetCell)
      assertEquals(childrenBeforeWidgetAdded + 1, table.children.size)
      assertTrue(table.children.last() == widgetCell.actor)
    }
    assertTrue(initInvoked)
  }
}

abstract class WidgetGroupWidgetFactoryTest(val groupUnderTestProvider: (WidgetGroupWidgetFactory.() -> Unit) -> WidgetGroup)
: WidgetFactoryTest<WidgetGroupWidgetFactory, Any>() {
  override fun testFactoryMethod(factoryMethodUnderTestProvider: (WidgetGroupWidgetFactory) -> Any) {
    var initInvoked = false
    groupUnderTestProvider {
      initInvoked = true
      val widgetGroup = this as WidgetGroup
      val childrenBeforeWidgetAdded = widgetGroup.children.size
      val widget = factoryMethodUnderTestProvider(this)
      assertNotNull(widget)
      assertEquals(childrenBeforeWidgetAdded + 1, widgetGroup.children.size)
      assertTrue(widgetGroup.children.last() == widget)
    }
    assertTrue(initInvoked)
  }
}


