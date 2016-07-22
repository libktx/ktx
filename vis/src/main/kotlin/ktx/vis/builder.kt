package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.kotcrab.vis.ui.layout.FloatingGroup
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.VisTable

/** @author Kotcrab */

/** Begins creation of UI using type-safe builder, uses [VisTable] as base widget container. */
inline fun table(init: KVisTable.() -> Unit): VisTable = actor(KVisTable(), init)

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
