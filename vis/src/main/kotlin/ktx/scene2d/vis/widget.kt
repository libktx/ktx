@file:Suppress("DEPRECATION") // Deprecated imports.

package ktx.scene2d.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.DragPane
import com.kotcrab.vis.ui.layout.FloatingGroup
import com.kotcrab.vis.ui.layout.FlowGroup
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.CollapsibleWidget
import com.kotcrab.vis.ui.widget.HorizontalCollapsibleWidget
import com.kotcrab.vis.ui.widget.MultiSplitPane
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisList
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisSplitPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTree
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.color.BasicColorPicker
import com.kotcrab.vis.ui.widget.color.ExtendedColorPicker
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.kotcrab.vis.ui.widget.toast.ToastTable
import ktx.scene2d.KGroup
import ktx.scene2d.KNode
import ktx.scene2d.KTable
import ktx.scene2d.KTree
import ktx.scene2d.Scene2dDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Extends [VisTable] with type-safe widget builders. */
@Scene2dDsl
class KVisTable(
  useVisDefaults: Boolean,
) : VisTable(useVisDefaults),
  KTable

/** Extends [ToastTable] with type-safe widget builders. */
@Scene2dDsl
class KToastTable(
  useVisDefaults: Boolean,
) : ToastTable(useVisDefaults),
  KTable

/** Extends [VisWindow] with type-safe widget builders. */
@Scene2dDsl
class KVisWindow(
  title: String,
  styleName: String,
) : VisWindow(title, styleName),
  KTable

/** Extends [VisDialog] with type-safe widget builders. */
@Scene2dDsl
class KVisDialog(
  title: String,
  styleName: String,
) : VisDialog(title, styleName),
  KTable

/** Extends [VisCheckBox] with type-safe widget builders. */
@Scene2dDsl
class KVisCheckBox(
  text: String,
  styleName: String,
) : VisCheckBox(text, styleName),
  KTable

/** Extends [VisRadioButton] with type-safe widget builders. */
@Scene2dDsl
class KVisRadioButton(
  text: String,
  styleName: String,
) : VisRadioButton(text, VisUI.getSkin()[styleName, VisCheckBoxStyle::class.java]),
  KTable

/** Extends [VisTextButton] with type-safe widget builders. */
@Scene2dDsl
class KVisTextButton(
  text: String,
  styleName: String,
) : VisTextButton(text, styleName),
  KTable

/** Extends [VisImageButton] with type-safe widget builders. */
@Scene2dDsl
class KVisImageButton(
  styleName: String,
) : VisImageButton(styleName),
  KTable

/** Extends [VisImageTextButton] with type-safe widget builders. */
@Scene2dDsl
class KVisImageTextButton(
  text: String,
  styleName: String,
) : VisImageTextButton(text, styleName),
  KTable

/** Extends [VisTree] with type-safe widget builders. */
@Scene2dDsl
class KVisTree(
  styleName: String,
) : VisTree<KNode<*>, Any?>(styleName),
  KTree {
  override fun <T : Actor> add(actor: T): KNode<T> {
    val node = KNode(actor)
    add(node)
    return node
  }
}

/** Extends [BasicColorPicker] with type-safe widget builders. */
@Scene2dDsl
class KBasicColorPicker(
  styleName: String,
) : BasicColorPicker(styleName, null),
  KTable

/** Extends [ExtendedColorPicker] with type-safe widget builders. */
@Scene2dDsl
class KExtendedColorPicker(
  styleName: String,
) : ExtendedColorPicker(styleName, null),
  KTable

/** Extends [Spinner] with type-safe widget builders. */
@Scene2dDsl
class KSpinner(
  styleName: String,
  name: String,
  model: SpinnerModel,
) : Spinner(styleName, name, model),
  KTable

/** Extends [HorizontalFlowGroup] with type-safe widget builders. */
@Scene2dDsl
@Deprecated("Use KFlowGroup instead.", replaceWith = ReplaceWith("KFlowGroup"))
class KHorizontalFlowGroup(
  spacing: Float,
) : HorizontalFlowGroup(spacing),
  KGroup

/** Extends [VerticalFlowGroup] with type-safe widget builders. */
@Scene2dDsl
@Deprecated("Use KFlowGroup instead.", replaceWith = ReplaceWith("KFlowGroup"))
class KVerticalFlowGroup(
  spacing: Float,
) : VerticalFlowGroup(spacing),
  KGroup

/** Extends [FlowGroup] with type-safe widget builders. */
@Scene2dDsl
class KFlowGroup(
  vertical: Boolean,
  spacing: Float,
) : FlowGroup(vertical, spacing),
  KGroup

/** Extends [GridGroup] with type-safe widget builders. */
@Scene2dDsl
class KGridGroup(
  itemSize: Float,
  spacing: Float,
) : GridGroup(itemSize, spacing),
  KGroup

/** Extends [FloatingGroup] with type-safe widget builders. */
@Scene2dDsl
class KFloatingGroup :
  FloatingGroup(),
  KGroup

/** Extends [DragPane] with type-safe widget builders. */
@Scene2dDsl
class KDragPane :
  DragPane(KVisTable(false)),
  KGroup {
  /** Allows to access [KVisTable] storing [KDragPane] children. */
  val table: KVisTable
    get() = group as KVisTable
}

/** Extends [TabbedPane] with type-safe widget builders. */
@Scene2dDsl
class KTabbedPane(
  styleName: String,
) : TabbedPane(styleName)

/**
 * Begins creation of new [Tab] using type-safe builder. Newly created tab will be added to tabbed pane automatically
 * so there is no need to call [TabbedPane.add] manually.
 * @param title title of the [Tab].
 * @param savable see [Tab.savable].
 * @param closeableByUser see [Tab.closeableByUser].
 * @param init allows to customize the [Tab].
 * @return a new [Tab] instance added to this pane.
 */
@OptIn(ExperimentalContracts::class)
inline fun KTabbedPane.tab(
  title: String,
  savable: Boolean = false,
  closeableByUser: Boolean = true,
  init: KTab.() -> Unit = {},
): Tab {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val tab = KTab(title, savable, closeableByUser)
  add(tab)
  tab.init()
  return tab
}

/**
 * On tab switch, tab content will be added to provided [Table] instance.
 * Previous [table] content will be cleared.
 */
fun TabbedPane.addTabContentsTo(table: Table) {
  addPaneContentListener {
    table.clear()
    table.add(it).grow()
  }
}

/**
 * On tab switch, tab content will be added to provided widget [Group] instance.
 * Previous [group] content will be cleared.
 */
fun TabbedPane.addTabContentsTo(group: Group) {
  addPaneContentListener {
    group.clear()
    group.addActor(it)
  }
}

/**
 * On tab switch, tab content will be added to provided [Container] instance.
 * Previous [container] actor will be replaced.
 */
fun TabbedPane.addTabContentsTo(container: Container<*>) {
  addPaneContentListener {
    @Suppress("UNCHECKED_CAST")
    (container as Container<Table>).actor = it
  }
}

private inline fun TabbedPane.addPaneContentListener(crossinline contentChanged: (Table) -> Unit) {
  addListener(
    object : TabbedPaneAdapter() {
      override fun switchedTab(tab: Tab) {
        contentChanged(tab.contentTable)
      }
    },
  )
}

/** See [Tab]. Note that [KTab] is only intended to by used with type-safe builder from within [KTabbedPane.tab]. */
@Scene2dDsl
class KTab(
  private val title: String,
  savable: Boolean,
  closeableByUser: Boolean,
) : Tab(savable, closeableByUser),
  KTable {
  private val content = VisTable()

  override fun <T : Actor> add(actor: T): Cell<T> = content.add(actor)

  override fun getContentTable(): Table = content

  override fun getTabTitle(): String = title
}

/** Extends [VisList] widget with items building method. */
@Scene2dDsl
class KVisList<T>(
  style: String,
) : VisList<T>(style) {
  /**
   * Allows to add items to the list with builder-like syntax.
   */
  @Scene2dDsl
  operator fun T.unaryMinus() {
    items.add(this)
  }

  /**
   * Utility method. If the internal array with items stored by this widget was modified, use this method to safely
   * refresh the items that this widget draws.
   */
  fun refreshItems() {
    // Internal items array has to be copied, as it is cleared by the setter method.
    setItems(Array(items))
  }
}

/** Extends [VisSelectBox] with items building method. */
@Scene2dDsl
class KVisSelectBox<T>(
  style: String,
) : VisSelectBox<T>(style) {
  /**
   * Allows to add items to the select box with builder-like syntax.
   */
  @Scene2dDsl
  operator fun T.unaryMinus() {
    items.add(this)
  }

  /**
   * Utility method. If the internal array with items stored by this widget was modified, use this method to safely
   * refresh the items that this widget draws.
   */
  fun refreshItems() {
    // Internal items array has to be copied, as it is cleared by the setter method.
    items = Array(items)
  }
}

/** Extends [VisSplitPane] API with type-safe widget builders. Note that this widget may store only two children.
 * It is advised to use the inlined extension factory methods added by [KGroup] rather than set its widgets directly
 * with [setFirstWidget] or [setSecondWidget]. */
@Scene2dDsl
class KVisSplitPane(
  vertical: Boolean,
  style: String,
) : VisSplitPane(null, null, vertical, style),
  KGroup {
  override fun addActor(actor: Actor?) {
    when (this.children.size) {
      0 -> setFirstWidget(actor)
      1 -> setSecondWidget(actor)
      else -> throw IllegalStateException("VisSplitPane may store only two children.")
    }
  }
}

/** Extends [VisScrollPane] API with type-safe widget builders. Note that this widget may store only a single child. */
@Scene2dDsl
class KVisScrollPane(
  style: String,
) : VisScrollPane(null, style),
  KGroup {
  override fun addActor(actor: Actor?) {
    this.actor == null || throw IllegalStateException("ScrollPane may store only a single child.")
    this.actor = actor
  }
}

/** Extends [MultiSplitPane] with type-safe widget builders. */
@Scene2dDsl
class KMultiSplitPane(
  vertical: Boolean,
  style: String,
) : MultiSplitPane(vertical, style),
  KGroup {
  override fun addActor(actor: Actor?) {
    if (children.size == 0) {
      setWidgets(actor)
    } else {
      setWidgets(children + actor)
    }
  }
}

/** Extends [CollapsibleWidget] with type-safe widget builders. Adds children to collapsible's [table]. */
@Scene2dDsl
class KCollapsible(
  val table: KVisTable,
) : CollapsibleWidget(table),
  KTable {
  override fun <T : Actor> add(actor: T): Cell<T> = table.add(actor)

  @Deprecated(
    message = "Table is set on construction and should not be changed.",
    replaceWith = ReplaceWith("Nothing"),
  )
  override fun setTable(table: Table?): Unit = throw GdxRuntimeException("Use default table instead.")
}

/** Extends [HorizontalCollapsibleWidget] with type-safe widget builders. Adds children to collapsible's [table]. */
@Scene2dDsl
class KHorizontalCollapsible(
  val table: KVisTable,
) : HorizontalCollapsibleWidget(table),
  KTable {
  override fun <T : Actor> add(actor: T): Cell<T> = table.add(actor)

  @Deprecated(
    message = "Table is set on construction and should not be changed.",
    replaceWith = ReplaceWith("Nothing"),
  )
  override fun setTable(table: Table?): Unit = throw GdxRuntimeException("Use default table instead.")
}
