package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.*
import com.kotcrab.vis.ui.util.adapter.ListAdapter
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.color.BasicColorPicker
import com.kotcrab.vis.ui.widget.color.ExtendedColorPicker
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import java.lang.IllegalStateException

/** @author Kotcrab */

const val DEFAULT_STYLE = "default"

/**
 * Provides methods allowing to build scene2d.ui using type-safe builders.
 */
interface WidgetFactory<S> {
  // Non-parental widgets

  /** @see [VisLabel] */
  fun label(text: String, styleName: String = DEFAULT_STYLE, init: (@VisDsl VisLabel).(S) -> Unit = {}): VisLabel
      = actor(VisLabel(text, styleName), init)

  /** @see [LinkLabel] */
  fun linkLabel(text: String, url: String = text, styleName: String = DEFAULT_STYLE, init: (@VisDsl LinkLabel).(S) -> Unit = {}): LinkLabel
      = actor(LinkLabel(text, url, styleName), init)

  /** @see [VisImage] */
  fun image(drawable: Drawable, scaling: Scaling = Scaling.stretch, align: Int = Align.center, init: (@VisDsl VisImage).(S) -> Unit = {}): VisImage
      = actor(VisImage(drawable, scaling, align), init)

  /** @see [VisImage] */
  fun image(drawableName: String, init: (@VisDsl VisImage).(S) -> Unit = {}): VisImage = actor(VisImage(drawableName), init)

  /** @see [VisList] */
  fun <T> list(styleName: String = DEFAULT_STYLE, init: (@VisDsl VisList<T>).(S) -> Unit = {}): VisList<T> = actor(VisList(styleName), init)

  /** @see [VisProgressBar] */
  fun progressBar(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false, init: (@VisDsl VisProgressBar).(S) -> Unit = {}): VisProgressBar
      = actor(VisProgressBar(min, max, step, vertical), init)

  /** @see [VisProgressBar] */
  fun progressBar(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false,
                  styleName: String, init: (@VisDsl VisProgressBar).(S) -> Unit = {}): VisProgressBar
      = actor(VisProgressBar(min, max, step, vertical, styleName), init)

  /** @see [VisSelectBox] */
  fun <T> selectBox(styleName: String = DEFAULT_STYLE, init: (@VisDsl VisSelectBox<T>).(S) -> Unit = {}): VisSelectBox<T>
      = actor(VisSelectBox(styleName), init)

  /** @see [VisSlider] */
  fun slider(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false, init: (@VisDsl VisSlider).(S) -> Unit = {}): VisSlider
      = actor(VisSlider(min, max, step, vertical), init)

  /** @see [VisSlider] */
  fun slider(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false, styleName: String, init: (@VisDsl VisSlider).(S) -> Unit = {}): VisSlider
      = actor(VisSlider(min, max, step, vertical, styleName), init)

  /** @see [VisTextArea] */
  fun textArea(text: String = "", styleName: String = DEFAULT_STYLE, init: (@VisDsl VisTextArea).(S) -> Unit = {}): VisTextArea
      = actor(VisTextArea(text, styleName), init)

  /** @see [HighlightTextArea] */
  fun highlightTextArea(text: String = "", styleName: String = DEFAULT_STYLE, init: (@VisDsl HighlightTextArea).(S) -> Unit = {}): HighlightTextArea =
      actor(HighlightTextArea(text, styleName), init)

  /** @see [ScrollableTextArea] */
  fun scrollableTextArea(text: String = "", styleName: String = DEFAULT_STYLE, init: (@VisDsl ScrollableTextArea).(S) -> Unit = {}): ScrollableTextArea =
      actor(ScrollableTextArea(text, styleName), init)

  /** @see [VisTextField] */
  fun textField(text: String = "", styleName: String = DEFAULT_STYLE, init: (@VisDsl VisTextField).(S) -> Unit = {}): VisTextField
      = actor(VisTextField(text, styleName), init)

  /** @see [VisValidatableTextField] */
  fun validatableTextField(text: String = "", styleName: String = DEFAULT_STYLE, init: (@VisDsl VisValidatableTextField).(S) -> Unit = {}): VisValidatableTextField
      = actor(VisValidatableTextField(text, styleName), init)

  /** @see [Touchpad] */
  fun touchpad(deadzoneRadius: Float, styleName: String = DEFAULT_STYLE, init: (@VisDsl Touchpad).(S) -> Unit = {}): Touchpad
      = actor(Touchpad(deadzoneRadius, VisUI.getSkin(), styleName), init)

  /** @see [BusyBar] */
  fun busyBar(styleName: String = DEFAULT_STYLE, init: (@VisDsl BusyBar).(S) -> Unit = {}): BusyBar
      = actor(BusyBar(styleName), init)

  /** @see [Separator] */
  fun separator(styleName: String = DEFAULT_STYLE, init: (@VisDsl Separator).(S) -> Unit = {}): Separator = actor(Separator(styleName), init)

  // Parental widgets

  /** @see [Button] */
  fun button(styleName: String = DEFAULT_STYLE, init: KButton.(S) -> Unit = {}): Button
      = actor(KButton(styleName), init)

  /** @see [VisTextButton] */
  fun textButton(text: String, styleName: String = DEFAULT_STYLE, init: KVisTextButton.(S) -> Unit = {}): VisTextButton
      = actor(KVisTextButton(text, styleName), init)

  /** @see [VisImageButton] */
  fun imageButton(styleName: String = DEFAULT_STYLE, init: KVisImageButton.(S) -> Unit = {}): VisImageButton
      = actor(KVisImageButton(styleName), init)

  /** @see [VisImageButton] */
  fun imageButton(imageUp: Drawable, imageDown: Drawable = imageUp, imageChecked: Drawable = imageUp,
                  init: KVisImageButton.(S) -> Unit = {}): VisImageButton
      = actor(KVisImageButton(imageUp, imageDown, imageChecked), init)

  /** @see [VisImageTextButton] */
  fun imageTextButton(text: String, styleName: String = DEFAULT_STYLE, init: KVisImageTextButton.(S) -> Unit = {}): VisImageTextButton
      = actor(KVisImageTextButton(text, styleName), init)

  /** @see [VisCheckBox] */
  fun checkBox(text: String, styleName: String = DEFAULT_STYLE, init: KVisCheckBox.(S) -> Unit = {}): VisCheckBox
      = actor(KVisCheckBox(text, styleName), init)

  /** @see [VisRadioButton] */
  fun radioButton(text: String, init: KVisRadioButton.(S) -> Unit = {}): VisRadioButton
      = actor(KVisRadioButton(text), init)

  /** @see [VisTree] */
  fun tree(styleName: String = DEFAULT_STYLE, init: KVisTree.(S) -> Unit = {}): VisTree = actor(KVisTree(styleName), init)

  /** @see [BasicColorPicker] */
  fun basicColorPicker(styleName: String = DEFAULT_STYLE, init: KBasicColorPicker.(S) -> Unit = {}): BasicColorPicker
      = actor(KBasicColorPicker(styleName), init)

  /** @see [ExtendedColorPicker] */
  fun extendedColorPicker(styleName: String = DEFAULT_STYLE, init: KExtendedColorPicker.(S) -> Unit = {}): ExtendedColorPicker
      = actor(KExtendedColorPicker(styleName), init)

  /** @see [Spinner] */
  fun spinner(name: String, model: SpinnerModel, styleName: String = DEFAULT_STYLE, init: KSpinner.(S) -> Unit = {}): Spinner
      = actor(KSpinner(styleName, name, model), init)

  // Parental widgets groups

  /** @see [VisTable] */
  fun table(defaultSpacing: Boolean = false, init: KVisTable.(S) -> Unit = {}): VisTable = actor(KVisTable(defaultSpacing), init)

  /** @see [KButtonTable] */
  fun buttonTable(defaultSpacing: Boolean = false, init: KButtonTable.(S) -> Unit = {}): VisTable = actor(KButtonTable(defaultSpacing), init)

  /** @see [HorizontalGroup] */
  fun horizontalGroup(init: KHorizontalGroup.(S) -> Unit = {}): HorizontalGroup = actor(KHorizontalGroup(), init)

  /** @see [HorizontalFlowGroup] */
  fun horizontalFlowGroup(spacing: Float = 0f, init: KHorizontalFlowGroup.(S) -> Unit = {}): HorizontalFlowGroup
      = actor(KHorizontalFlowGroup(spacing), init)

  /** @see [VerticalGroup] */
  fun verticalGroup(init: KVerticalGroup.(S) -> Unit = {}): VerticalGroup = actor(KVerticalGroup(), init)

  /** @see [VerticalFlowGroup] */
  fun verticalFlowGroup(spacing: Float = 0f, init: KVerticalFlowGroup.(S) -> Unit = {}): VerticalFlowGroup
      = actor(KVerticalFlowGroup(spacing), init)

  /** @see [GridGroup] */
  fun gridGroup(itemSize: Float = 256f, spacing: Float = 8f, init: KGridGroup.(S) -> Unit = {}): GridGroup
      = actor(KGridGroup(itemSize, spacing), init)

  /** @see [FloatingGroup] */
  fun floatingGroup(init: KFloatingGroup.(S) -> Unit = {}): FloatingGroup = actor(KFloatingGroup(), init)

  /** @see [FloatingGroup] */
  fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.(S) -> Unit = {}): FloatingGroup
      = actor(KFloatingGroup(prefWidth, prefHeight), init)

  /** @see [DragPane] */
  fun dragPane(widgetGroup: WidgetGroup, init: KDragPane.(S) -> Unit = {}): DragPane
      = actor(KDragPane(widgetGroup), init)

  /** @see [Stack] */
  fun stack(init: KStack.(S) -> Unit = {}): Stack = actor(KStack(), init)

  // Others

  /** @see [VisScrollPane] */
  fun scrollPane(widget: Actor, styleName: String = DEFAULT_STYLE, init: (@VisDsl VisScrollPane).(S) -> Unit = {}): VisScrollPane
      = actor(VisScrollPane(widget, styleName), init)

  /** @see [VisSplitPane] */
  fun splitPane(firstWidget: Actor, secondWidget: Actor, vertical: Boolean = false, init: (@VisDsl VisSplitPane).(S) -> Unit = {}): VisSplitPane
      = actor(VisSplitPane(firstWidget, secondWidget, vertical), init)

  /** @see [VisSplitPane] */
  fun splitPane(firstWidget: Actor, secondWidget: Actor, vertical: Boolean = false, styleName: String, init: (@VisDsl VisSplitPane).(S) -> Unit = {}): VisSplitPane
      = actor(VisSplitPane(firstWidget, secondWidget, vertical, styleName), init)

  /** @see [MultiSplitPane] */
  fun multiSplitPane(vertical: Boolean = false, init: (@VisDsl MultiSplitPane).(S) -> Unit = {}): MultiSplitPane
      = actor(MultiSplitPane(vertical), init)

  /** @see [MultiSplitPane] */
  fun multiSplitPane(vertical: Boolean = false, styleName: String, init: (@VisDsl MultiSplitPane).(S) -> Unit = {}): MultiSplitPane
      = actor(MultiSplitPane(vertical, styleName), init)

  /** @see [Container] */
  fun <T : Actor> container(actor: T? = null, init: (@VisDsl Container<T>).(S) -> Unit = {}): Container<T> = actor(Container(actor), init)

  /** @see [CollapsibleWidget] */
  fun collapsible(table: Table, init: (@VisDsl CollapsibleWidget).(S) -> Unit = {}): CollapsibleWidget = actor(CollapsibleWidget(table), init)

  /** @see [HorizontalCollapsibleWidget] */
  fun horizontalCollapsible(table: Table, init: (@VisDsl HorizontalCollapsibleWidget).(S) -> Unit = {}): HorizontalCollapsibleWidget =
      actor(HorizontalCollapsibleWidget(table), init)

  /** @see [ButtonBar] */
  fun buttonBar(order: String? = null, tableInit: VisTable.(S) -> Unit = {}, init: KButtonBar.() -> Unit): ButtonBar {
    val bar = if (order == null) KButtonBar() else KButtonBar(order)
    bar.init()
    actor(bar.createTable(), tableInit)
    return bar
  }

  /** @see [ListView] */
  fun <ItemT> listView(itemAdapter: ListAdapter<ItemT>, styleName: String = DEFAULT_STYLE, init: KListView<ItemT>.(S) -> Unit = {}): ListView<ItemT> {
    val view = KListView(itemAdapter, styleName)
    var storage: S? = null
    actor(view.mainTable, { storage = it })
    view.init(storage!!)
    return view
  }

  /** @see [TabbedPane] */
  fun tabbedPane(styleName: String = DEFAULT_STYLE, init: KTabbedPane.(S) -> Unit = {}): TabbedPane {
    val pane = KTabbedPane(styleName)
    var storage: S? = null
    actor(pane.table, { storage = it })
    pane.init(storage!!)
    return pane
  }

  /** @see [Actor] */
  fun <T : Actor> actor(actor: T, init: (@VisDsl T).(S) -> Unit): T {
    val result = addActorToWidgetGroup(actor)
    actor.init(result)
    return actor
  }

  fun <T : Actor> addActorToWidgetGroup(actor: T): S
}

/**
 * Implemented by classes providing type-safe builders. Requires that [addActorToTable]
 * returns [Cell] instance with actor being added to [Table].
 * @author Kotcrab, MJ
 */
interface TableWidgetFactory : WidgetFactory<Cell<*>> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): Cell<*> {
    val cell = addActorToTable(actor)
    actor.userObject = cell
    return cell
  }

  fun <T : Actor> addActorToTable(actor: T): Cell<T>

  /**
   * Allows to customize properties of the [Cell] storing this actor.
   * @param grow if true, expands and fills the cell vertically and horizontally. Defaults to false.
   * @param growX if true, expands and fills the cell horizontally. Defaults to false. Overrides [grow].
   * @param growY if true, expands and fills the cell vertically. Defaults to false. Overrides [grow].
   * @param expand if true, expands the cell vertically and horizontally. Overrides [growX], [growY].
   * @param expandX if true, expands the cell horizontally. Overrides [expand].
   * @param expandY if true, expands the cell vertically. Overrides [expand].
   * @param fill if true, the actor will fill the whole cell area vertically and horizontally. Overrides [growX], [growY].
   * @param fillX if true, the actor will fill the whole cell area horizontally. Overrides [fill].
   * @param fillY if true, the actor will fill the whole cell area vertically. Overrides [fill].
   * @param uniform see [Cell.uniform].
   * @param uniformX see [Cell.uniformX]. Overrides [uniform].
   * @param uniformY see [Cell.uniformY]. Overrides [uniform].
   * @param align actor alignment in the cell. See [com.badlogic.gdx.utils.Align].
   * @param colspan amount of columns taken by the cell.
   * @param width sets maximum, preferred and minimum width of the cell in viewport units.
   * @param minWidth sets minimum width of the cell. Overrides [width].
   * @param preferredWidth sets preferred width of the cell. Overrides [width].
   * @param maxWidth sets maximum width of the cell. Overrides [width].
   * @param height sets maximum, preferred and minimum height of the cell in viewport units.
   * @param minHeight sets minimum height of the cell. Overrides [height].
   * @param preferredHeight sets preferred height of the cell. Overrides [height].
   * @param maxHeight sets maximum height of the cell. Overrides [height].
   * @param pad top, left, right and bottom padding value in viewport units. Contrary to [space], paddings are summed.
   * @param padTop top padding value. Overrides [pad].
   * @param padLeft left padding value. Overrides [pad].
   * @param padRight right padding value. Overrides [pad].
   * @param padBottom bottom padding value. Overrides [pad].
   * @param space top, left, right and bottom spacing value in viewport units. Contrary to [pad], spacings are not summed.
   * @param spaceTop top spacing value. Overrides [space].
   * @param spaceLeft left spacing value. Overrides [space].
   * @param spaceRight right spacing value. Overrides [space].
   * @param spaceBottom bottom spacing value. Overrides [space].
   * @param row if true, another row of actors will be started after this cell. Defaults to false.
   * @return this actor.
   * @see inCell
   */
  fun <T : Actor> T.cell(grow: Boolean = false,
                         growX: Boolean = false,
                         growY: Boolean = false,
                         expand: Boolean? = null,
                         expandX: Boolean? = null,
                         expandY: Boolean? = null,
                         fill: Boolean? = null,
                         fillX: Boolean? = null,
                         fillY: Boolean? = null,
                         uniform: Boolean? = null,
                         uniformX: Boolean? = null,
                         uniformY: Boolean? = null,
                         align: Int? = null,
                         colspan: Int? = null,
                         width: Float? = null,
                         minWidth: Float? = null,
                         preferredWidth: Float? = null,
                         maxWidth: Float? = null,
                         height: Float? = null,
                         minHeight: Float? = null,
                         preferredHeight: Float? = null,
                         maxHeight: Float? = null,
                         pad: Float? = null,
                         padTop: Float? = null,
                         padLeft: Float? = null,
                         padRight: Float? = null,
                         padBottom: Float? = null,
                         space: Float? = null,
                         spaceTop: Float? = null,
                         spaceLeft: Float? = null,
                         spaceRight: Float? = null,
                         spaceBottom: Float? = null,
                         row: Boolean = false): T {
    val cell = this.inCell
    if (grow) cell.grow()
    if (growX) cell.growX()
    if (growY) cell.growY()
    expand?.let { cell.expand(it, it) }
    expandX?.let { cell.expand(it, expandY ?: expand ?: false) }
    expandY?.let { cell.expand(expandX ?: expand ?: false, it) }
    fill?.let { cell.fill(it, it) }
    fillX?.let { cell.fill(it, fillY ?: fill ?: false) }
    fillY?.let { cell.fill(fillX ?: fill ?: false, it) }
    uniform?.let { cell.uniform(it, it) }
    uniformX?.let { cell.uniform(it, uniformY ?: uniform ?: false) }
    uniformY?.let { cell.uniform(uniformX ?: uniform ?: false, it) }
    align?.let { cell.align(it) }
    colspan?.let { cell.colspan(colspan) }
    width?.let { cell.width(it) }
    minWidth?.let { cell.minWidth(it) }
    preferredWidth?.let { cell.prefWidth(it) }
    maxWidth?.let { cell.maxWidth(it) }
    height?.let { cell.height(it) }
    minHeight?.let { cell.minHeight(it) }
    preferredHeight?.let { cell.prefHeight(it) }
    maxHeight?.let { cell.maxHeight(it) }
    pad?.let { cell.pad(it) }
    padTop?.let { cell.padTop(it) }
    padLeft?.let { cell.padLeft(it) }
    padRight?.let { cell.padRight(it) }
    padBottom?.let { cell.padBottom(it) }
    space?.let { cell.space(it) }
    spaceTop?.let { cell.spaceTop(it) }
    spaceLeft?.let { cell.spaceLeft(it) }
    spaceRight?.let { cell.spaceRight(it) }
    spaceBottom?.let { cell.spaceBottom(it) }
    if (row) cell.row()
    return this
  }

  /**
   * Allows to access [Cell] if this actor is stored in the [Table]. Relies on the [Actor.userObject] mechanism.
   * @throws IllegalStateException if actor was improperly added to the [Table] or does not have its [Cell] instance
   *    assigned as user object.
   * @see cell
   */
  @Suppress("UNCHECKED_CAST")
  val <T : Actor> T.inCell: Cell<T> get() = userObject as? Cell<T> ?:
      throw IllegalStateException("This actor has no declared Cell. " +
          "Was it properly added to the table? Was its user object cleared?")
}

/**
 * Implemented by classes providing type-safe builders. Requires that [addActorToTree]
 * returns [KNode] instance with actor being added to [Tree].
 * @author Kotcrab, MJ
 */
interface TreeWidgetFactory : WidgetFactory<KNode> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): KNode {
    val node = addActorToTree(actor)
    actor.userObject = node
    return node
  }

  fun <T : Actor> addActorToTree(actor: T): KNode

  /**
   * Allows to customize properties of the [Node] storing this actor .
   * @param icon will be drawn next to the actor.
   * @param expanded true to expand the node, false to hide its children.
   * @param selectable if false, this node cannot be selected.
   * @param userObject custom object assigned to the node.
   * @return this actor.
   * @see inNode
   */
  fun <T : Actor> T.node(icon: Drawable? = null,
                         expanded: Boolean? = null,
                         selectable: Boolean? = null,
                         userObject: Any? = null): T {
    val node = inNode
    icon?.let { node.icon = icon }
    expanded?.let { node.isExpanded = expanded }
    selectable?.let { node.isSelectable = selectable }
    node.`object` = userObject
    return this
  }

  /**
   * Allows to access [Node] in this actor is stored in the [Tree]. Relies on the [Actor.userObject] mechanism.
   * @throws IllegalStateException if actor was improperly added to the [Tree] or does not have its [Node] instance
   *    assigned as user object.
   * @see node
   */
  val <T : Actor> T.inNode: KNode get() = userObject as? KNode ?:
      throw IllegalStateException("This actor has no declared Node. " +
          "Was it properly added to the tree? Was its user object cleared?")
}

/**
 * Implemented by classes that wants to utilize type-safe builders however they require actors to be added manually using
 * class custom methods. Good example is [ButtonBar].
 */
interface VoidWidgetFactory : WidgetFactory<Actor> {
  override fun <T : Actor> addActorToWidgetGroup(actor: T): T {
    return actor
  }
}
