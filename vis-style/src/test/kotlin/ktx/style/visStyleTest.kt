package ktx.style

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.Sizes
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter.SimpleListAdapterStyle
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.FormValidatorStyle
import com.kotcrab.vis.ui.widget.BusyBar.BusyBarStyle
import com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelStyle
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
import org.mockito.Mockito

class VisStyleTest {
  @Test
  fun shouldAddSizes() {
    val skin = skin {
      sizes {
        borderSize = 1f
      }
    }
    val style = skin.get<Sizes>(defaultStyle)
    assertEquals(1f, style.borderSize)
  }

  @Test
  fun shouldExtendSizes() {
    val skin = skin {
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
  fun shouldAddBusyBarStyle() {
    val skin = skin {
      busyBar {
        height = 1
      }
    }
    val style = skin.get<BusyBarStyle>(defaultStyle)
    assertEquals(1, style.height)
  }

  @Test
  fun shouldExtendBusyBarStyle() {
    val skin = skin {
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
  fun shouldAddColorPickerStyle() {
    val skin = skin {
      colorPicker {
        titleFontColor = Color.RED
      }
    }
    val style = skin.get<ColorPickerStyle>(defaultStyle)
    assertEquals(Color.RED, style.titleFontColor)
  }

  @Test
  fun shouldExtendColorPickerStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      colorPicker("base") {
        pickerStyle = colorPickerWidget {}
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
  fun shouldAddColorPickerWidgetStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      colorPickerWidget {
        barSelector = drawable
      }
    }
    val style = skin.get<ColorPickerWidgetStyle>(defaultStyle)
    assertEquals(drawable, style.barSelector)
  }

  @Test
  fun shouldExtendColorPickerWidgetStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddFormValidatorStyle() {
    val skin = skin {
      formValidator {
        colorTransitionDuration = 1f
      }
    }
    val style = skin.get<FormValidatorStyle>(defaultStyle)
    assertEquals(1f, style.colorTransitionDuration)
  }

  @Test
  fun shouldExtendFormValidatorStyle() {
    val skin = skin {
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
  fun shouldAddLinkLabelStyle() {
    val skin = skin {
      linkLabel {
        fontColor = Color.RED
      }
    }
    val style = skin.get<LinkLabelStyle>(defaultStyle)
    assertEquals(Color.RED, style.fontColor)
  }

  @Test
  fun shouldExtendLinkLabelStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddMenuStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      menu {
        border = drawable
      }
    }
    val style = skin.get<MenuStyle>(defaultStyle)
    assertEquals(drawable, style.border)
  }

  @Test
  fun shouldExtendMenuStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddMenuBarStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      menuBar {
        background = drawable
      }
    }
    val style = skin.get<MenuBarStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendMenuBarStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      menuBar("base") {
        background = drawable
      }
      menuBar("new", extend = "base") {}
    }
    val style = skin.get<MenuBarStyle>("new")
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldAddMenuItemStyle() {
    val skin = skin {
      menuItem {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<MenuItemStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendMenuItemStyle() {
    val skin = skin {
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
  fun shouldAddMultiSplitPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      multiSplitPane {
        handle = drawable
      }
    }
    val style = skin.get<MultiSplitPaneStyle>(defaultStyle)
    assertEquals(drawable, style.handle)
  }

  @Test
  fun shouldExtendMultiSplitPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddPopupMenuStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      popupMenu {
        background = drawable
      }
    }
    val style = skin.get<PopupMenuStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendPopupMenuStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddSeparatorStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      separator {
        background = drawable
      }
    }
    val style = skin.get<SeparatorStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendSeparatorStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddSimpleListAdapterStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      simpleListAdapter {
        background = drawable
      }
    }
    val style = skin.get<SimpleListAdapterStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendSimpleListAdapterStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddSpinnerStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      spinner {
        down = drawable
      }
    }
    val style = skin.get<SpinnerStyle>(defaultStyle)
    assertEquals(drawable, style.down)
  }

  @Test
  fun shouldExtendSpinnerStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddTabbedPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      tabbedPane {
        background = drawable
      }
    }
    val style = skin.get<TabbedPaneStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendTabbedPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddToastStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      toast {
        background = drawable
      }
    }
    val style = skin.get<ToastStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendToastStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val buttonStyle = Mockito.mock(VisImageButtonStyle::class.java)
    val skin = skin {
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
  fun shouldAddVisCheckBoxStyle() {
    val skin = skin {
      visCheckBox {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<VisCheckBoxStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendVisCheckBoxStyle() {
    val skin = skin {
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
  fun shouldAddVisImageButtonStyle() {
    val skin = skin {
      visImageButton {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<VisImageButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendVisImageButtonStyle() {
    val skin = skin {
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
  fun shouldAddVisImageTextButtonStyle() {
    val skin = skin {
      visImageTextButton {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<VisImageTextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendVisImageTextButtonStyle() {
    val skin = skin {
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
  fun shouldAddVisSplitPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      visSplitPane {
        handle = drawable
      }
    }
    val style = skin.get<VisSplitPaneStyle>(defaultStyle)
    assertEquals(drawable, style.handle)
  }

  @Test
  fun shouldExtendVisSplitPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
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
  fun shouldAddVisTextButtonStyle() {
    val skin = skin {
      visTextButton {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<VisTextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendVisTextButtonStyle() {
    val skin = skin {
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
  fun shouldAddVisTextFieldStyle() {
    val skin = skin {
      visTextField {
        fontColor = Color.CYAN
      }
    }
    val style = skin.get<VisTextFieldStyle>(defaultStyle)
    assertEquals(Color.CYAN, style.fontColor)
  }

  @Test
  fun shouldExtendVisTextFieldStyle() {
    val skin = skin {
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
  fun shouldAddVisTooltipStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      visTooltip {
        background = drawable
      }
    }
    val style = skin.get<TooltipStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendVisTooltipStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      visTooltip("base") {
        background = drawable
      }
      visTooltip("new", extend = "base") {
      }
    }
    val style = skin.get<TooltipStyle>("new")
    assertEquals(drawable, style.background)
  }
}