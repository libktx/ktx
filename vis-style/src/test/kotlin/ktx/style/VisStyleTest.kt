package ktx.style

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.Sizes
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter.SimpleListAdapterStyle
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.FormValidatorStyle
import com.kotcrab.vis.ui.widget.BusyBar.BusyBarStyle
import com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelStyle
import com.kotcrab.vis.ui.widget.ListViewStyle
import com.kotcrab.vis.ui.widget.Menu.MenuStyle
import com.kotcrab.vis.ui.widget.MenuBar.MenuBarStyle
import com.kotcrab.vis.ui.widget.MenuItem.MenuItemStyle
import com.kotcrab.vis.ui.widget.MultiSplitPane.MultiSplitPaneStyle
import com.kotcrab.vis.ui.widget.PopupMenu.PopupMenuStyle
import com.kotcrab.vis.ui.widget.Separator.SeparatorStyle
import com.kotcrab.vis.ui.widget.Tooltip.TooltipStyle
import com.kotcrab.vis.ui.widget.VisCheckBox.VisCheckBoxStyle
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle
import com.kotcrab.vis.ui.widget.VisImageTextButton.VisImageTextButtonStyle
import com.kotcrab.vis.ui.widget.VisSplitPane.VisSplitPaneStyle
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle
import com.kotcrab.vis.ui.widget.VisTextField.VisTextFieldStyle
import com.kotcrab.vis.ui.widget.color.ColorPickerStyle
import com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle
import com.kotcrab.vis.ui.widget.spinner.Spinner.SpinnerStyle
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle
import com.kotcrab.vis.ui.widget.toast.Toast.ToastStyle
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

/**
 * Tests building utilities of VisUI widget styles.
 */
class VisStyleTest {
  @Test
  fun `should add Sizes`() {
    val skin =
      skin {
        sizes {
          borderSize = 1f
        }
      }

    val style = skin.get<Sizes>(defaultStyle)
    assertEquals(1f, style.borderSize)
  }

  @Test
  fun `should extend Sizes`() {
    val skin =
      skin {
        sizes("base") {
          borderSize = 1f
        }
        sizes("new", extend = "base") {
          buttonBarSpacing = 1f
        }
      }

    val style = skin.get<Sizes>("new")
    assertEquals(1f, style.borderSize)
    assertEquals(1f, style.buttonBarSpacing)
  }

  @Test
  fun `should add BusyBarStyle`() {
    val skin =
      skin {
        busyBar {
          height = 1
        }
      }

    val style = skin.get<BusyBarStyle>(defaultStyle)
    assertEquals(1, style.height)
  }

  @Test
  fun `should extend BusyBarStyle`() {
    val skin =
      skin {
        busyBar("base") {
          height = 1
        }
        busyBar("new", extend = "base") {
          segmentWidth = 1
        }
      }

    val style = skin.get<BusyBarStyle>("new")
    assertEquals(1, style.height)
    assertEquals(1, style.segmentWidth)
  }

  @Test
  fun `should add ColorPickerStyle`() {
    val skin =
      skin {
        colorPicker {
          titleFontColor = Color.RED
        }
      }

    val style = skin.get<ColorPickerStyle>(defaultStyle)
    assertEquals(Color.RED, style.titleFontColor)
  }

  @Test
  fun `should extend ColorPickerStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        colorPicker("base") {
          pickerStyle = it.colorPickerWidget()
          titleFontColor = Color.RED
        }
        colorPicker("new", extend = "base") {
          stageBackground = drawable
        }
      }

    val style = skin.get<ColorPickerStyle>("new")
    assertEquals(Color.RED, style.titleFontColor)
    assertEquals(drawable, style.stageBackground)
  }

  @Test
  fun `should add ColorPickerWidgetStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        colorPickerWidget {
          barSelector = drawable
        }
      }

    val style = skin.get<ColorPickerWidgetStyle>(defaultStyle)
    assertEquals(drawable, style.barSelector)
  }

  @Test
  fun `should extend ColorPickerWidgetStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        colorPickerWidget("base") {
          barSelector = drawable
        }
        colorPickerWidget("new", extend = "base") {
          cross = drawable
        }
      }

    val style = skin.get<ColorPickerWidgetStyle>("new")
    assertEquals(drawable, style.barSelector)
    assertEquals(drawable, style.cross)
  }

  @Test
  fun `should add FormValidatorStyle`() {
    val skin =
      skin {
        formValidator {
          colorTransitionDuration = 1f
        }
      }

    val style = skin.get<FormValidatorStyle>(defaultStyle)
    assertEquals(1f, style.colorTransitionDuration)
  }

  @Test
  fun `should extend FormValidatorStyle`() {
    val skin =
      skin {
        formValidator("base") {
          colorTransitionDuration = 1f
        }
        formValidator("new", extend = "base") {
          errorLabelColor = Color.RED
        }
      }

    val style = skin.get<FormValidatorStyle>("new")
    assertEquals(1f, style.colorTransitionDuration)
    assertEquals(Color.RED, style.errorLabelColor)
  }

  @Test
  fun `should add LinkLabelStyle`() {
    val skin =
      skin {
        linkLabel {
          fontColor = Color.RED
        }
      }

    val style = skin.get<LinkLabelStyle>(defaultStyle)
    assertEquals(Color.RED, style.fontColor)
  }

  @Test
  fun `should extend LinkLabelStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        linkLabel("base") {
          fontColor = Color.RED
        }
        linkLabel("new", extend = "base") {
          background = drawable
        }
      }

    val style = skin.get<LinkLabelStyle>("new")
    assertEquals(Color.RED, style.fontColor)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should add ListViewStyle`() {
    val scrollPane = mock<ScrollPaneStyle>()
    val skin =
      skin {
        listView {
          scrollPaneStyle = scrollPane
        }
      }

    val style = skin.get<ListViewStyle>(defaultStyle)
    assertEquals(scrollPane, style.scrollPaneStyle)
  }

  @Test
  fun `should extend ListViewStyle`() {
    val scrollPane = ScrollPaneStyle()
    val drawable = mock<Drawable>()
    scrollPane.background = drawable
    val skin =
      skin {
        listView("base") {
          scrollPaneStyle = scrollPane
        }
        listView("new", extend = "base") {
          scrollPaneStyle.corner = drawable
        }
      }

    val style = skin.get<ListViewStyle>("new")
    // ScrollPaneStyle is copied, so nested properties are checked:
    assertEquals(drawable, style.scrollPaneStyle.background)
    assertEquals(drawable, style.scrollPaneStyle.corner)
  }

  @Test
  fun `should add MenuStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        menu {
          border = drawable
        }
      }

    val style = skin.get<MenuStyle>(defaultStyle)
    assertEquals(drawable, style.border)
  }

  @Test
  fun `should extend MenuStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        menu("base") {
          border = drawable
        }
        menu("new", extend = "base") {
          background = drawable
        }
      }

    val style = skin.get<MenuStyle>("new")
    assertEquals(drawable, style.border)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should add MenuBarStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        menuBar {
          background = drawable
        }
      }

    val style = skin.get<MenuBarStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend MenuBarStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        menuBar("base") {
          background = drawable
        }
        menuBar("new", extend = "base")
      }

    val style = skin.get<MenuBarStyle>("new")
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should add MenuItemStyle`() {
    val skin =
      skin {
        menuItem {
          pressedOffsetX = 1f
        }
      }

    val style = skin.get<MenuItemStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend MenuItemStyle`() {
    val skin =
      skin {
        menuItem("base") {
          pressedOffsetX = 1f
        }
        menuItem("new", extend = "base") {
          pressedOffsetY = 1f
        }
      }

    val style = skin.get<MenuItemStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun `should add MultiSplitPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        multiSplitPane {
          handle = drawable
        }
      }

    val style = skin.get<MultiSplitPaneStyle>(defaultStyle)
    assertEquals(drawable, style.handle)
  }

  @Test
  fun `should extend MultiSplitPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        multiSplitPane("base") {
          handle = drawable
        }
        multiSplitPane("new", extend = "base") {
          handleOver = drawable
        }
      }

    val style = skin.get<MultiSplitPaneStyle>("new")
    assertEquals(drawable, style.handle)
    assertEquals(drawable, style.handleOver)
  }

  @Test
  fun `should add PopupMenuStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        popupMenu {
          background = drawable
        }
      }

    val style = skin.get<PopupMenuStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend PopupMenuStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        popupMenu("base") {
          background = drawable
        }
        popupMenu("new", extend = "base") {
          border = drawable
        }
      }

    val style = skin.get<PopupMenuStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(drawable, style.border)
  }

  @Test
  fun `should add SeparatorStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        separator {
          background = drawable
        }
      }

    val style = skin.get<SeparatorStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend SeparatorStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        separator("base") {
          background = drawable
        }
        separator("new", extend = "base") {
          thickness = 1
        }
      }

    val style = skin.get<SeparatorStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(1, style.thickness)
  }

  @Test
  fun `should add SimpleListAdapterStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        simpleListAdapter {
          background = drawable
        }
      }

    val style = skin.get<SimpleListAdapterStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend SimpleListAdapterStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        simpleListAdapter("base") {
          background = drawable
        }
        simpleListAdapter("new", extend = "base") {
          selection = drawable
        }
      }

    val style = skin.get<SimpleListAdapterStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(drawable, style.selection)
  }

  @Test
  fun `should add SpinnerStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        spinner {
          down = drawable
        }
      }

    val style = skin.get<SpinnerStyle>(defaultStyle)
    assertEquals(drawable, style.down)
  }

  @Test
  fun `should extend SpinnerStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        spinner("base") {
          down = drawable
        }
        spinner("new", extend = "base") {
          up = drawable
        }
      }

    val style = skin.get<SpinnerStyle>("new")
    assertEquals(drawable, style.down)
    assertEquals(drawable, style.up)
  }

  @Test
  fun `should add TabbedPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        tabbedPane {
          background = drawable
        }
      }

    val style = skin.get<TabbedPaneStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend TabbedPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        tabbedPane("base") {
          background = drawable
        }
        tabbedPane("new", extend = "base") {
          vertical = true
        }
      }

    val style = skin.get<TabbedPaneStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(true, style.vertical)
  }

  @Test
  fun `should add ToastStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        toast {
          background = drawable
        }
      }

    val style = skin.get<ToastStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend ToastStyle`() {
    val drawable = mock<Drawable>()
    val buttonStyle = mock<VisImageButtonStyle>()

    val skin =
      skin {
        toast("base") {
          background = drawable
        }
        toast("new", extend = "base") {
          closeButtonStyle = buttonStyle
        }
      }

    val style = skin.get<ToastStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(buttonStyle, style.closeButtonStyle)
  }

  @Test
  fun `should add VisCheckBoxStyle`() {
    val skin =
      skin {
        visCheckBox {
          pressedOffsetX = 1f
        }
      }

    val style = skin.get<VisCheckBoxStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend VisCheckBoxStyle`() {
    val skin =
      skin {
        visCheckBox("base") {
          pressedOffsetX = 1f
        }
        visCheckBox("new", extend = "base") {
          pressedOffsetY = 1f
        }
      }

    val style = skin.get<VisCheckBoxStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun `should add VisImageButtonStyle`() {
    val skin =
      skin {
        visImageButton {
          pressedOffsetX = 1f
        }
      }

    val style = skin.get<VisImageButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend VisImageButtonStyle`() {
    val skin =
      skin {
        visImageButton("base") {
          pressedOffsetX = 1f
        }
        visImageButton("new", extend = "base") {
          pressedOffsetY = 1f
        }
      }

    val style = skin.get<VisImageButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun `should add VisImageTextButtonStyle`() {
    val skin =
      skin {
        visImageTextButton {
          pressedOffsetX = 1f
        }
      }

    val style = skin.get<VisImageTextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend VisImageTextButtonStyle`() {
    val skin =
      skin {
        visImageTextButton("base") {
          pressedOffsetX = 1f
        }
        visImageTextButton("new", extend = "base") {
          pressedOffsetY = 1f
        }
      }

    val style = skin.get<VisImageTextButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun `should add VisSplitPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        visSplitPane {
          handle = drawable
        }
      }

    val style = skin.get<VisSplitPaneStyle>(defaultStyle)
    assertEquals(drawable, style.handle)
  }

  @Test
  fun `should extend VisSplitPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        visSplitPane("base") {
          handle = drawable
        }
        visSplitPane("new", extend = "base") {
          handleOver = drawable
        }
      }

    val style = skin.get<VisSplitPaneStyle>("new")
    assertEquals(drawable, style.handle)
    assertEquals(drawable, style.handleOver)
  }

  @Test
  fun `should add VisTextButtonStyle`() {
    val skin =
      skin {
        visTextButton {
          pressedOffsetX = 1f
        }
      }

    val style = skin.get<VisTextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend VisTextButtonStyle`() {
    val skin =
      skin {
        visTextButton("base") {
          pressedOffsetX = 1f
        }
        visTextButton("new", extend = "base") {
          pressedOffsetY = 1f
        }
      }

    val style = skin.get<VisTextButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun `should add VisTextFieldStyle`() {
    val skin =
      skin {
        visTextField {
          fontColor = Color.CYAN
        }
      }

    val style = skin.get<VisTextFieldStyle>(defaultStyle)
    assertEquals(Color.CYAN, style.fontColor)
  }

  @Test
  fun `should extend VisTextFieldStyle`() {
    val skin =
      skin {
        visTextField("base") {
          fontColor = Color.CYAN
        }
        visTextField("new", extend = "base") {
          disabledFontColor = Color.BLACK
        }
      }

    val style = skin.get<VisTextFieldStyle>("new")
    assertEquals(Color.CYAN, style.fontColor)
    assertEquals(Color.BLACK, style.disabledFontColor)
  }

  @Test
  fun `should add VisTooltipStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        visTooltip {
          background = drawable
        }
      }

    val style = skin.get<TooltipStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend VisTooltipStyle`() {
    val drawable = mock<Drawable>()
    val skin =
      skin {
        visTooltip("base") {
          background = drawable
        }
        visTooltip("new", extend = "base")
      }

    val style = skin.get<TooltipStyle>("new")
    assertEquals(drawable, style.background)
  }
}
