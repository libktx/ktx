package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.*
import com.kotcrab.vis.ui.util.adapter.ListAdapter
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.color.BasicColorPicker
import com.kotcrab.vis.ui.widget.color.ExtendedColorPicker
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.kotcrab.vis.ui.widget.toast.ToastTable

/** @author Kotcrab */

/** @see [VisTable] */
@VisDsl
class KVisTable(setVisDefaults: Boolean) : VisTable(setVisDefaults), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/**
 * [VisTable] that a appends all buttons to internal [ButtonGroup] before adding them to table. Others actors are
 * added to table normally.
 */
@VisDsl
class KButtonTable(setVisDefaults: Boolean) : VisTable(setVisDefaults), TableWidgetFactory {
  val buttonGroup: ButtonGroup<Button> = ButtonGroup()

  override fun <T : Actor> addActorToTable(actor: T): Cell<T> {
    if (actor is Button) {
      buttonGroup.add(actor)
    }
    return add(actor)
  }
}

/** @see [ToastTable] */
@VisDsl
class KToastTable(setVisDefaults: Boolean) : ToastTable(setVisDefaults), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisWindow] */
@VisDsl
class KVisWindow(title: String, styleName: String) : VisWindow(title, styleName), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [Button] */
@VisDsl
class KButton(styleName: String) : Button(VisUI.getSkin(), styleName), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisCheckBox] */
@VisDsl
class KVisCheckBox(text: String, styleName: String) : VisCheckBox(text, styleName), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisRadioButton] */
@VisDsl
class KVisRadioButton(text: String) : VisRadioButton(text), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisTextButton] */
@VisDsl
class KVisTextButton(text: String, styleName: String) : VisTextButton(text, styleName), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisImageButton] */
@VisDsl
class KVisImageButton : VisImageButton, TableWidgetFactory {
  constructor(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable) : super(imageUp, imageDown, imageChecked)
  constructor(styleName: String) : super(styleName)

  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisImageTextButton] */
@VisDsl
class KVisImageTextButton(text: String, styleName: String) : VisImageTextButton(text, styleName), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [VisTree] */
@VisDsl
class KVisTree(styleName: String) : VisTree(styleName), TreeWidgetFactory {
  override fun <T : Actor> addActorToTree(actor: T): KNode {
    val node = KNode(actor)
    add(node)
    return node
  }
}

/** Extends [Tree] [Node] API with type-safe widget builders. */
@VisDsl
class KNode(actor: Actor) : Node<Node<*, *, *>, Any?, Actor>(actor), TreeWidgetFactory {
  override fun <T : Actor> addActorToTree(actor: T): KNode {
    val node = KNode(actor)
    add(node)
    return node
  }

  /**
   * Allows to inline a function block on a [KNode]. Syntax sugar for nested [Tree] nodes creation.
   * @param init will be invoked on this node.
   * @return this node.
   */
  inline operator fun invoke(init: KNode.() -> Unit): KNode {
    this.init()
    return this
  }
}

/** @see [BasicColorPicker] */
@VisDsl
class KBasicColorPicker(styleName: String) : BasicColorPicker(styleName, null), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [ExtendedColorPicker] */
@VisDsl
class KExtendedColorPicker(styleName: String) : ExtendedColorPicker(styleName, null), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [Spinner] */
@VisDsl
class KSpinner(styleName: String, name: String, model: SpinnerModel) : Spinner(styleName, name, model), TableWidgetFactory {
  override fun <T : Actor> addActorToTable(actor: T): Cell<T> = add(actor)
}

/** @see [ButtonBar] */
@VisDsl
class KButtonBar : ButtonBar, VoidWidgetFactory {
  constructor() : super()
  constructor(order: String) : super(order)
}

/** @see [ListView] */
@VisDsl
class KListView<ItemT>(adapter: ListAdapter<ItemT>, styleName: String)
  : ListView<ItemT>(adapter, styleName), VoidWidgetFactory

/** @see [Stack] */
@VisDsl
class KStack : Stack(), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [HorizontalGroup] */
@VisDsl
class KHorizontalGroup : HorizontalGroup(), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [HorizontalFlowGroup] */
@VisDsl
class KHorizontalFlowGroup(spacing: Float) : HorizontalFlowGroup(spacing), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [VerticalGroup] */
@VisDsl
class KVerticalGroup : VerticalGroup(), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [VerticalFlowGroup] */
@VisDsl
class KVerticalFlowGroup(spacing: Float) : VerticalFlowGroup(spacing), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [GridGroup] */
@VisDsl
class KGridGroup(itemSize: Float, spacing: Float) : GridGroup(itemSize, spacing), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [FloatingGroup] */
@VisDsl
class KFloatingGroup : FloatingGroup, WidgetFactory<Actor> {
  constructor() : super()
  constructor(prefWidth: Float, prefHeight: Float) : super(prefWidth, prefHeight)

  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    addActor(actor)
    return actor
  }
}

/** @see [DragPane] */
@VisDsl
class KDragPane(group: WidgetGroup) : DragPane(group), WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    // addActor is not deprecated in DragPane but it's deprecated in Container, in Java warning wouldn't be shown here
    @Suppress("DEPRECATION")
    addActor(actor)
    return actor
  }
}

/** @see [TabbedPane] */
@VisDsl
class KTabbedPane(styleName: String) : TabbedPane(styleName) {
  /**
   * Begins creation of new tab using type-safe builder, newly created tab will be added to tabbed pane automatically
   * so there is no need to call [add] manually.
   */
  fun tab(title: String, savable: Boolean = false, closeableByUser: Boolean = true, init: KTab.() -> Unit): Tab {
    val tab = KTab(title, savable, closeableByUser)
    super.add(tab)
    tab.init()
    return tab
  }
}

/** On tab switch, tab content will be added to provided table instance. Previous content will be cleared. */
fun TabbedPane.addTabContentsTo(table: Table) {
  addPaneContentListener {
    table.clear()
    table.add(it).grow()
  }
}

/** On tab switch, tab content will be added to provided widget group instance. Previous content will be cleared. */
fun TabbedPane.addTabContentsTo(widgetGroup: WidgetGroup) {
  addPaneContentListener {
    widgetGroup.clear()
    widgetGroup.addActor(it)
  }
}

/** On tab switch, tab content will be added to provided container instance. Previous content will be replaced. */
fun TabbedPane.addTabContentsTo(container: Container<*>) {
  addPaneContentListener { container.actor = it }
}

private fun TabbedPane.addPaneContentListener(contentChanged: (Table) -> Unit) {
  addListener(object : TabbedPaneAdapter() {
    override fun switchedTab(tab: Tab) {
      contentChanged(tab.contentTable)
    }
  })
}

/** See [Tab]. Note that KTab is only intended to by used with type-safe builder from within [KTabbedPane.tab]. */
@VisDsl
class KTab(private val title: String, savable: Boolean, closeableByUser: Boolean) : Tab(savable, closeableByUser), TableWidgetFactory {
  val content = Table()

  override fun <T : Actor> addActorToTable(actor: T): Cell<T> {
    return content.add(actor)
  }

  override fun getContentTable(): Table {
    return content
  }

  override fun getTabTitle(): String {
    return title
  }
}
