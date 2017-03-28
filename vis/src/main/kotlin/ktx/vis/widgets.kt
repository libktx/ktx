package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
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
class KVisTable(setVisDefaults: Boolean) : VisTable(setVisDefaults), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/**
 * [VisTable] that a appends all buttons to internal [ButtonGroup] before adding them to table. Others actors are
 * added to table normally.
 */
class KButtonTable(setVisDefaults: Boolean) : VisTable(setVisDefaults), TableWidgetFactory {
  val buttonGroup: ButtonGroup<Button> = ButtonGroup()

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> {
    if (actor is Button) {
      buttonGroup.add(actor)
    }
    return add(actor)
  }
}

/** @see [ToastTable] */
class KToastTable(setVisDefaults: Boolean) : ToastTable(setVisDefaults), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisWindow] */
class KVisWindow(title: String, styleName: String) : VisWindow(title, styleName), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [Button] */
class KButton(styleName: String) : Button(VisUI.getSkin(), styleName), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisCheckBox] */
class KVisCheckBox(text: String, styleName: String) : VisCheckBox(text, styleName), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisRadioButton] */
class KVisRadioButton(text: String) : VisRadioButton(text), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisTextButton] */
class KVisTextButton(text: String, styleName: String) : VisTextButton(text, styleName), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisImageButton] */
class KVisImageButton : VisImageButton, TableWidgetFactory {
  constructor(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable) : super(imageUp, imageDown, imageChecked)
  constructor(styleName: String) : super(styleName)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisImageTextButton] */
class KVisImageTextButton(text: String, styleName: String) : VisImageTextButton(text, styleName), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [VisTree] */
class KVisTree(styleName: String) : VisTree(styleName), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [BasicColorPicker] */
class KBasicColorPicker(styleName: String) : BasicColorPicker(styleName, null), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [ExtendedColorPicker] */
class KExtendedColorPicker(styleName: String) : ExtendedColorPicker(styleName, null), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [Spinner] */
class KSpinner(styleName: String, name: String, model: SpinnerModel) : Spinner(styleName, name, model), TableWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

/** @see [ButtonBar] */
class KButtonBar : ButtonBar, VoidWidgetFactory {
  constructor() : super()
  constructor(order: String) : super(order)
}

/** @see [ListView] */
class KListView<ItemT>(adapter: ListAdapter<ItemT>, styleName: String)
  : ListView<ItemT>(adapter, styleName), VoidWidgetFactory

/** @see [Stack] */
class KStack : Stack(), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [HorizontalGroup] */
class KHorizontalGroup : HorizontalGroup(), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [HorizontalFlowGroup] */
class KHorizontalFlowGroup(spacing: Float) : HorizontalFlowGroup(spacing), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [VerticalGroup] */
class KVerticalGroup : VerticalGroup(), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [VerticalFlowGroup] */
class KVerticalFlowGroup(spacing: Float) : VerticalFlowGroup(spacing), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [GridGroup] */
class KGridGroup(itemSize: Float, spacing: Float) : GridGroup(itemSize, spacing), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [FloatingGroup] */
class KFloatingGroup : FloatingGroup, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(prefWidth: Float, prefHeight: Float) : super(prefWidth, prefHeight)

  override fun addActorToWidgetGroup(actor: Actor): Actor {
    addActor(actor)
    return actor
  }
}

/** @see [DragPane] */
class KDragPane(group: WidgetGroup) : DragPane(group), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Actor {
    // addActor is not deprecated in DragPane but it's deprecated in Container, in Java warning wouldn't be shown here
    @Suppress("DEPRECATION")
    addActor(actor)
    return actor
  }
}

/** @see [TabbedPane] */
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

/** On tab switch, tab content will be added to provided widget group instance. Previous content will be cleared. */
fun <T : WidgetGroup> TabbedPane.addTabContentsTo(groupCell: Cell<T>) {
  val actor = groupCell.actor
  when (actor) { // Kotlin's smart casts are in action here, different overloads will be called
    is Table -> addTabContentsTo(actor)
    is Container<*> -> addTabContentsTo(actor)
    else -> addTabContentsTo(actor)
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
class KTab(private val title: String, savable: Boolean, closeableByUser: Boolean) : Tab(savable, closeableByUser), TableWidgetFactory {
  val content = Table()

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> {
    return content.add(actor)
  }

  override fun getContentTable(): Table {
    return content
  }

  override fun getTabTitle(): String {
    return title
  }
}
