package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.Disableable
import com.kotcrab.vis.ui.layout.FloatingGroup
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.util.ToastManager
import com.kotcrab.vis.ui.util.form.FormValidator
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.toast.ToastTable

/** @author Kotcrab */

/** Begins creation of UI using type-safe builder, uses [VisTable] as base widget container. */
inline fun table(setVisDefaults: Boolean = false, init: KVisTable.() -> Unit): VisTable = actor(KVisTable(setVisDefaults), init)

/** Begins creation of UI using type-safe builder, uses [KButtonTable] as base widget container. */
inline fun buttonTable(setVisDefaults: Boolean = false, init: KButtonTable.() -> Unit): VisTable = actor(KButtonTable(setVisDefaults), init)

/**
 * Begins creation of UI using type-safe builder, uses [ToastTable] as base widget container.
 * @see [ToastManager]
 */
inline fun toastTable(setVisDefaults: Boolean = false, init: KToastTable.() -> Unit): ToastTable = actor(KToastTable(setVisDefaults), init)

/** Begins creation of UI using type-safe builder, uses [VisWindow] as base widget container. */
inline fun window(title: String, styleName: String = DEFAULT_STYLE, init: KVisWindow.() -> Unit): VisWindow = actor(KVisWindow(title, styleName), init)

/** Begins creation of UI using type-safe builder, uses [HorizontalGroup] as base widget container. */
inline fun horizontalGroup(init: KHorizontalGroup.() -> Unit): HorizontalGroup = actor(KHorizontalGroup(), init)

/** Begins creation of UI using type-safe builder, uses [HorizontalFlowGroup] as base widget container. */
inline fun horizontalFlowGroup(spacing: Float = 0f, init: KHorizontalFlowGroup.() -> Unit): HorizontalFlowGroup
    = actor(KHorizontalFlowGroup(spacing), init)

/** Begins creation of UI using type-safe builder, uses [VerticalGroup] as base widget container. */
inline fun verticalGroup(init: KVerticalGroup.() -> Unit): VerticalGroup = actor(KVerticalGroup(), init)

/** Begins creation of UI using type-safe builder, uses [VerticalFlowGroup] as base widget container. */
inline fun verticalFlowGroup(spacing: Float = 0f, init: KVerticalFlowGroup.() -> Unit): VerticalFlowGroup
    = actor(KVerticalFlowGroup(spacing), init)

/** Begins creation of UI using type-safe builder, uses [GridGroup] as base widget container. */
inline fun gridGroup(itemSize: Float = 256f, spacing: Float = 8f, init: KGridGroup.() -> Unit): GridGroup
    = actor(KGridGroup(itemSize, spacing), init)

/** Begins creation of UI using type-safe builder, uses [FloatingGroup] as base widget container. */
inline fun floatingGroup(init: KFloatingGroup.() -> Unit): FloatingGroup = actor(KFloatingGroup(), init)

/** Begins creation of UI using type-safe builder, uses [FloatingGroup] as base widget container. */
inline fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): FloatingGroup
    = actor(KFloatingGroup(prefWidth, prefHeight), init)

/** Begins creation of UI using type-safe builder, uses [Stack] as base widget container. */
inline fun stack(init: KStack.() -> Unit): Stack = actor(KStack(), init)

inline fun <T : Actor> actor(actor: T, init: T.() -> Unit): T {
  actor.init()
  return actor
}

/**  Creates and returns new [FormValidator] for use with type-safe builders. */
inline fun validator(targetToDisable: Disableable? = null, messageLabel: Label? = null, styleName: String = DEFAULT_STYLE,
                     init: FormValidator.() -> Unit): FormValidator {
  val validator = FormValidator(targetToDisable, messageLabel, styleName)
  validator.init()
  return validator
}
