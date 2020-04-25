package ktx.style

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap
import kotlin.annotation.AnnotationTarget.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Should annotate builder methods of Scene2D [Skin]. */
@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class SkinDsl

/**
 * Name of default resources in [Skin]. Often used as default Scene2D actor style names.
 */
const val defaultStyle = "default"

/**
 * @param init will be applied to the [Skin] instance. Inlined.
 * @return a new instance of [Skin].
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun skin(init: (@SkinDsl Skin).(Skin) -> Unit = {}): Skin {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val skin = Skin()
  skin.init(skin)
  return skin
}

/**
 * @param atlas will be disposed along with the [Skin].
 * @param init will be applied to the [Skin] instance. Inlined.
 * @return a new instance of [Skin].
 */
@OptIn(ExperimentalContracts::class)
inline fun skin(atlas: TextureAtlas, init: (@SkinDsl Skin).(Skin) -> Unit = {}): Skin {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val skin = Skin(atlas)
  skin.init(skin)
  return skin
}

/**
 * Utility function that makes it easier to access [Skin] assets.
 * @param name name of the requested resource. Defaults to [defaultStyle].
 * @return resource of the specified type with the selected name.
 * @throws GdxRuntimeException if unable to find the resource.
 */
inline operator fun <reified Resource : Any> Skin.get(name: String = defaultStyle): Resource =
  this[name, Resource::class.java]

/**
 * Utility function that makes it easier to access [Skin] assets.
 * @param name name of the requested resource.
 * @return resource of the specified type with the selected name.
 * @throws GdxRuntimeException if unable to find the resource.
 */
inline operator fun <reified Resource : Any, E : Enum<E>> Skin.get(name: E): Resource = this[name.toString()]

/**
 * Utility function that makes it easier to access [Skin] assets or return null if they don't exist.
 * @param name name of the requested resource. Defaults to [defaultStyle].
 * @return resource of the specified type with the selected name, or `null` if it doesn't exist.
 */
inline fun <reified Resource : Any> Skin.optional(name: String = defaultStyle): Resource? =
  this.optional(name, Resource::class.java)

/**
 * Utility function that makes it easier to add [Skin] assets.
 * @param name name of the passed resource.
 * @param resource will be added to the skin and mapped to the selected name.
 */
inline operator fun <reified Resource : Any> Skin.set(name: String, resource: Resource) =
  this.add(name, resource, Resource::class.java)

/**
 * Utility function that makes it easier to add [Skin] assets.
 * @param name name of the passed resource.
 * @param resource will be added to the skin and mapped to the selected name.
 */
inline operator fun <reified Resource : Any, E : Enum<E>> Skin.set(name: E, resource: Resource) =
  this.set(name.toString(), resource)

/**
 * Utility function that makes it easier to add [Skin] assets.
 * @param name name of the passed resource. Defaults to [defaultStyle].
 * @param resource will be added to the skin and mapped to the selected name.
 */
inline fun <reified Resource : Any> Skin.add(resource: Resource, name: String = defaultStyle) =
  this.add(name, resource, Resource::class.java)

/**
 * Utility function that makes it easier to add [Skin] assets under the [defaultStyle] name.
 * @param resource will be added to the skin and mapped to the selected name.
 */
inline operator fun <reified Resource : Any> Skin.plusAssign(resource: Resource) =
  this.add(resource)

/**
 * Utility function that makes it easier to remove [Skin] assets.
 * @param name name of the passed resource. Defaults to [defaultStyle].
 * @throws NullPointerException if unable to find the resource.
 */
inline fun <reified Resource : Any> Skin.remove(name: String = defaultStyle) =
  this.remove(name, Resource::class.java)

/**
 * Utility function that makes it easier to check if [Skin] contains assets.
 * @param name name of the resource to look for. Defaults to [defaultStyle].
 */
inline fun <reified Resource : Any> Skin.has(name: String = defaultStyle): Boolean =
  this.has(name, Resource::class.java)

/**
 * Utility function that makes it easier to access all [Skin] assets of a certain type.
 * @return map of the resources for the [Resource] type, or `null` if no resources of that type is in the skin.
 */
inline fun <reified Resource : Any> Skin.getAll(): ObjectMap<String, Resource>? =
  this.getAll(Resource::class.java)

/**
 * Utility function for adding existing styles to the skin. Mostly for internal use.
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param style non-null existing style instance.
 * @param init will be applied to the style instance. Inlined.
 * @return passed style instance (for chaining).
 */
@OptIn(ExperimentalContracts::class)
inline fun <Style> Skin.addStyle(name: String, style: Style, init: Style.() -> Unit = {}): Style {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  style.init()
  this.add(name, style)
  return style
}

/**
 * Adds a new [Color] instance to the skin.
 * @param name name of the color.
 * @param red red color component in range of [0, 1].
 * @param green green color component in range of [0, 1].
 * @param blue blue color component in range of [0, 1].
 * @param alpha alpha (transparency) color component in range of [0, 1]. Defaults to 1f.
 * @return a new instance of [Color].
 */
fun Skin.color(
  name: String,
  red: Float,
  green: Float,
  blue: Float,
  alpha: Float = 1f
): Color {
  val color = Color(red, green, blue, alpha)
  this.add(name, color)
  return color
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.button(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ButtonStyle).() -> Unit = {}
): ButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ButtonStyle() else ButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [CheckBoxStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.checkBox(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl CheckBoxStyle).() -> Unit = {}
): CheckBoxStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) CheckBoxStyle() else CheckBoxStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ImageButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.imageButton(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ImageButtonStyle).() -> Unit = {}
): ImageButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ImageButtonStyle() else ImageButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ImageTextButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.imageTextButton(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ImageTextButtonStyle).() -> Unit = {}
): ImageTextButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ImageTextButtonStyle() else ImageTextButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [LabelStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.label(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl LabelStyle).() -> Unit = {}
): LabelStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) LabelStyle() else LabelStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ListStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.list(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ListStyle).() -> Unit = {}
): ListStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ListStyle() else ListStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ProgressBarStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.progressBar(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ProgressBarStyle).() -> Unit = {}
): ProgressBarStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ProgressBarStyle() else ProgressBarStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ScrollPaneStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.scrollPane(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl ScrollPaneStyle).() -> Unit = {}
): ScrollPaneStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) ScrollPaneStyle() else ScrollPaneStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [SelectBoxStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.selectBox(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl SelectBoxStyle).() -> Unit = {}
): SelectBoxStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) SelectBoxStyle() else SelectBoxStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [SliderStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.slider(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl SliderStyle).() -> Unit = {}
): SliderStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) SliderStyle() else SliderStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [SplitPaneStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.splitPane(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl SplitPaneStyle).() -> Unit = {}
): SplitPaneStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) SplitPaneStyle() else SplitPaneStyle(get<SplitPaneStyle>(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ButtonStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.textButton(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl TextButtonStyle).() -> Unit = {}
): TextButtonStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TextButtonStyle() else TextButtonStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [TextFieldStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.textField(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl TextFieldStyle).() -> Unit = {}
): TextFieldStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TextFieldStyle() else TextFieldStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [TextFieldStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.textTooltip(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl TextTooltipStyle).() -> Unit = {}
): TextTooltipStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TextTooltipStyle() else TextTooltipStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [TouchpadStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.touchpad(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl TouchpadStyle).() -> Unit = {}
): TouchpadStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TouchpadStyle() else TouchpadStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [TreeStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.tree(
  name: String = defaultStyle,
  extend: String? = null,
  init: TreeStyle.() -> Unit = {}
): TreeStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) TreeStyle() else TreeStyle(get(extend)), init)
}

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 * this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [WindowStyle] added to the [Skin] with the selected name.
 */
@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.window(
  name: String = defaultStyle,
  extend: String? = null,
  init: (@SkinDsl WindowStyle).() -> Unit = {}
): WindowStyle {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return addStyle(name, if (extend == null) WindowStyle() else WindowStyle(get(extend)), init)
}
