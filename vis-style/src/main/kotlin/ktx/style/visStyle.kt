package ktx.style

import com.badlogic.gdx.scenes.scene2d.ui.Skin
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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @param name name of the sizes preference as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ sizes container. Its values will be copied and used as base for
 * this container.
 * @param init will be applied to the [Sizes] instance. Inlined.
 * @return a new instance of [Sizes] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.sizes(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl Sizes).() -> Unit = {},
): Sizes {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) Sizes() else Sizes(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [BusyBarStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.busyBar(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl BusyBarStyle).() -> Unit = {},
): BusyBarStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) BusyBarStyle() else BusyBarStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ColorPickerStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.colorPicker(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ColorPickerStyle).() -> Unit = {},
): ColorPickerStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ColorPickerStyle() else ColorPickerStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ColorPickerWidgetStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.colorPickerWidget(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ColorPickerWidgetStyle).() -> Unit = {},
): ColorPickerWidgetStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ColorPickerWidgetStyle() else ColorPickerWidgetStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [FormValidatorStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.formValidator(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl FormValidatorStyle).() -> Unit = {},
): FormValidatorStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) FormValidatorStyle() else FormValidatorStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [LinkLabelStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.linkLabel(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl LinkLabelStyle).() -> Unit = {},
): LinkLabelStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) LinkLabelStyle() else LinkLabelStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ListViewStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.listView(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ListViewStyle).() -> Unit = {},
): ListViewStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ListViewStyle() else ListViewStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [MenuStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.menu(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl MenuStyle).() -> Unit = {},
): MenuStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) MenuStyle() else MenuStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [MenuBarStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.menuBar(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl MenuBarStyle).() -> Unit = {},
): MenuBarStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) MenuBarStyle() else MenuBarStyle(get<MenuBarStyle>(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [MenuItemStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.menuItem(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl MenuItemStyle).() -> Unit = {},
): MenuItemStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) MenuItemStyle() else MenuItemStyle(get<MenuItemStyle>(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [MultiSplitPaneStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.multiSplitPane(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl MultiSplitPaneStyle).() -> Unit = {},
): MultiSplitPaneStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) MultiSplitPaneStyle() else MultiSplitPaneStyle(get<MultiSplitPaneStyle>(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [PopupMenuStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.popupMenu(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl PopupMenuStyle).() -> Unit = {},
): PopupMenuStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) PopupMenuStyle() else PopupMenuStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [SeparatorStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.separator(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl SeparatorStyle).() -> Unit = {},
): SeparatorStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) SeparatorStyle() else SeparatorStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [SimpleListAdapterStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.simpleListAdapter(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl SimpleListAdapterStyle).() -> Unit = {},
): SimpleListAdapterStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) SimpleListAdapterStyle() else SimpleListAdapterStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [SpinnerStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.spinner(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl SpinnerStyle).() -> Unit = {},
): SpinnerStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) SpinnerStyle() else SpinnerStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [TabbedPaneStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.tabbedPane(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl TabbedPaneStyle).() -> Unit = {},
): TabbedPaneStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TabbedPaneStyle() else TabbedPaneStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ToastStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.toast(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ToastStyle).() -> Unit = {},
): ToastStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ToastStyle() else ToastStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [VisCheckBoxStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visCheckBox(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl VisCheckBoxStyle).() -> Unit = {},
): VisCheckBoxStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) VisCheckBoxStyle() else VisCheckBoxStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [VisImageButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visImageButton(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl VisImageButtonStyle).() -> Unit = {},
): VisImageButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) VisImageButtonStyle() else VisImageButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [VisImageTextButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visImageTextButton(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl VisImageTextButtonStyle).() -> Unit = {},
): VisImageTextButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) VisImageTextButtonStyle() else VisImageTextButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [VisSplitPaneStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visSplitPane(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl VisSplitPaneStyle).() -> Unit = {},
): VisSplitPaneStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) VisSplitPaneStyle() else VisSplitPaneStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [VisTextButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visTextButton(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl VisTextButtonStyle).() -> Unit = {},
): VisTextButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) VisTextButtonStyle() else VisTextButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [VisTextFieldStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visTextField(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl VisTextFieldStyle).() -> Unit = {},
): VisTextFieldStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) VisTextFieldStyle() else VisTextFieldStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [TooltipStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.visTooltip(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl TooltipStyle).() -> Unit = {},
): TooltipStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TooltipStyle() else TooltipStyle(get<TooltipStyle>(extend)), init)
}
