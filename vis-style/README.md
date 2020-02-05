[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-vis-style.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-vis-style)

# KTX: VisUI style builders

Type-safe builders of **VisUI** widget styles.

This is an extension of [`ktx-style`](../style) module. See its documentation to get started with type-safe stylesheet
builders for `Scene2D` widgets.

Additionally to features provided by the `ktx-style` library, `ktx-vis-style` comes with factory methods for most
[VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) widget styles. On top of original Scene2D widget styles
handled by basic `ktx-style` utilities, supported extension methods include:

`Skin` method | Style class
:---: | ---
`sizes` | `com.kotcrab.vis.ui.Sizes`
`busyBar` | `com.kotcrab.vis.ui.widget.BusyBar.BusyBarStyle`
`colorPicker` | `com.kotcrab.vis.ui.widget.color.ColorPickerStyle`
`colorPickerWidget` | `com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle`
`formValidator` | `com.kotcrab.vis.ui.util.form.SimpleFormValidator.FormValidatorStyle`
`linkLabel` | `com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelStyle`
`listView` | `com.kotcrab.vis.ui.widget.ListViewStyle`
`menu` | `com.kotcrab.vis.ui.widget.Menu.MenuStyle`
`menuBar` | `com.kotcrab.vis.ui.widget.MenuBar.MenuBarStyle`
`menuItem` | `com.kotcrab.vis.ui.widget.MenuItem.MenuItemStyle`
`multiSplitPane` | `com.kotcrab.vis.ui.widget.MultiSplitPane.MultiSplitPaneStyle`
`popupMenu` | `com.kotcrab.vis.ui.widget.PopupMenu.PopupMenuStyle`
`separator` | `com.kotcrab.vis.ui.widget.Separator.SeparatorStyle`
`simpleListAdapter` | `com.kotcrab.vis.ui.util.adapter.SimpleListAdapter.SimpleListAdapterStyle`
`spinner` | `com.kotcrab.vis.ui.widget.spinner.Spinner.SpinnerStyle`
`tabbedPane` | `com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle`
`toast` | `com.kotcrab.vis.ui.widget.toast.Toast.ToastStyle`
`visCheckBox` | `com.kotcrab.vis.ui.widget.VisCheckBox.VisCheckBoxStyle`
`visImageButton` | `com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle`
`visImageTextButton` | `com.kotcrab.vis.ui.widget.VisImageTextButton.VisImageTextButtonStyle`
`visSplitPane` | `com.kotcrab.vis.ui.widget.VisSplitPane.VisSplitPaneStyle`
`visTextButton` | `com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle`
`visTextField` | `com.kotcrab.vis.ui.widget.VisTextField.VisTextFieldStyle`
`visTooltip` | `com.kotcrab.vis.ui.widget.Tooltip.TooltipStyle`

_Note_: `FileChooserStyle` is not included, as it is basically a desktop-only widget that does not work
(or compile - see GWT) on most platforms. Adding a similar extension method for `FileChooserStyle` would be pretty
straightforward: see [`visStyle.kt`](src/main/kotlin/ktx/style/visStyle.kt) for code samples.

### Guide

Additionally to providing a significant amount of new widgets, VisUI also provides a default skin
style following a modern flat design. Until you decide to redesign the UI completely, extending
the defaults is the common thing to do.

Extending the default VisUI `Skin`:

```kotlin
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.VisUI.SkinScale.X1
import ktx.style.defaultStyle
import ktx.style.visCheckBox

fun loadSkin(): Skin {
  VisUI.load(X1)
  val skin: Skin = VisUI.getSkin()
  return skin.apply { 
    visCheckBox(extend = defaultStyle) { 
      // Define your extended style here.
    }
  }
}
```

To see all styles provided by default, refer to the
[JSON `Skin` definition](https://github.com/kotcrab/vis-ui/blob/master/ui/src/main/resources/com/kotcrab/vis/ui/skin/x1/uiskin.json)
in VisUI library. Additional skin styles can also be found in projects such as
[vis-ui-contrib](https://github.com/kotcrab/vis-ui-contrib) or
[gdx-skins](https://github.com/czyzby/gdx-skins).

Other than providing additional extension methods, `ktx-vis-style` works very similarly to the
original [`ktx-style`](../style) module. Please refer to its guide for further details on how
to use this library.

#### Additional documentation

- [VisUI wiki.](https://github.com/kotcrab/vis-editor/wiki/VisUI)
- [`Skin` article.](https://github.com/libgdx/libgdx/wiki/Skin)
