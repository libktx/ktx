package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList
import com.badlogic.gdx.utils.Array as GdxArray

/* Implementations of actors and widget interfaces required to set up type-safe GUI builders. */

/**
 * Common interface applied to so-called "parental" widgets.
 */
@Scene2dDsl
interface KWidget<out Storage> {
  /**
   * Internal utility method for adding actors to the group. Assumes the actor might be stored in a container.
   * @param actor will be added to this group.
   * @return storage object, wrapping around the [actor] or the [actor] itself if no storage objects are used.
   * @see Node
   * @see Cell
   */
  fun <T : Actor> storeActor(actor: T): Storage
}

/**
 * A specialized interface for widgets that can create top-level root actors.
 * @see scene2d
 */
@Scene2dDsl
interface RootWidget : KWidget<Actor> {
  override fun <T : Actor> storeActor(actor: T) : T
}

/**
 * Root of the Scene2D DSL. Use this object to create new Scene2D actors and widgets without parents.
 */
@Scene2dDsl
@Suppress("ClassName")
object scene2d : RootWidget {
  override fun <T : Actor> storeActor(actor: T): T {
    // Actor is not modified or added to a group.
    return actor
  }
}

/**
 * Allows to define an actor within a DSL lambda block.
 * @param dsl will be immediately invoked. Must return an actor.
 * @return [Actor] returned by [dsl].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <T : Actor> scene2d(dsl: RootWidget.() -> T): T {
  contract { callsInPlace(dsl, InvocationKind.EXACTLY_ONCE) }
  return scene2d.dsl()
}

/**
 * Common interface applied to widgets that extend the original [Table] and keep their children in [Cell] instances.
 */
@Scene2dDsl
interface KTable : KWidget<Cell<*>> {
  /**
   * Matches [Table.add] API.
   * @param actor will be added to this widget.
   * @return [Cell] instance wrapping around the actor.
   * @see [Table.add]
   */
  fun <T : Actor> add(actor: T): Cell<T>

  override fun <T : Actor> storeActor(actor: T): Cell<T> {
    val cell = add(actor)
    actor.userObject = cell
    return cell
  }

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
  fun <T : Actor> T.cell(
    grow: Boolean = false,
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
    row: Boolean = false
  ): T {
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
   * Allows to access [Cell] in this actor is stored in the [Table]. Relies on the [Actor.userObject] mechanism.
   * @throws IllegalStateException if actor was improperly added to the [Table] or does not have its [Cell] instance
   *    assigned as user object.
   * @see cell
   */
  @Suppress("UNCHECKED_CAST")
  val <T : Actor> T.inCell: Cell<T>
    get() = userObject as? Cell<T> ?: throw IllegalStateException("This actor has no declared Cell. " +
      "Was it properly added to the table? Was its user object cleared?")
}

/**
 * Common interface applied to widgets that extend [WidgetGroup] or [Group] and keep their children in an internal
 * collection with no specialized containers such as [Cell] or [Node].
 */
@Scene2dDsl
interface KGroup : KWidget<Actor> {
  /**
   * Matches [Group.addActor] API.
   * @param actor will be added to this group.
   * @see [Group.addActor]
   * @see [WidgetGroup.addActor]
   */
  fun addActor(actor: Actor?)

  override fun <T : Actor> storeActor(actor: T): T = actor.also { addActor(it) }
}

/** Common interface applied to widgets that keep their children in [Tree] [Node] instances. */
@Scene2dDsl
interface KTree : KWidget<KNode<*>> {
  /**
   * @param actor will be placed in a [Node] inside this widget.
   * @return [Node] instance containing the actor.
   */
  fun <T : Actor> add(actor: T): KNode<T>

  override fun <T : Actor> storeActor(actor: T): KNode<T> {
    val node = add(actor)
    actor.userObject = node
    return node
  }

  /**
   * Allows to customize properties of the [Node] storing this actor .
   * @param icon will be drawn next to the actor.
   * @param expanded true to expand the node, false to hide its children.
   * @param selectable if false, this node cannot be selected.
   * @param userObject custom object assigned to the node.
   * @return this actor.
   * @see inNode
   */
  fun <T : Actor> T.node(
    icon: Drawable? = null,
    expanded: Boolean? = null,
    selectable: Boolean? = null,
    userObject: Any? = null): T {
    val node = inNode
    icon?.let { node.icon = icon }
    expanded?.let { node.isExpanded = expanded }
    selectable?.let { node.isSelectable = selectable }
    node.value = userObject
    return this
  }

  /**
   * Allows to access [Node] in this actor is stored in the [Tree]. Relies on the [Actor.userObject] mechanism.
   * @throws IllegalStateException if actor was improperly added to the [Tree] or does not have its [Node] instance
   *    assigned as user object.
   * @see node
   */
  @Suppress("UNCHECKED_CAST")
  val <T : Actor> T.inNode: KNode<T>
    get() = userObject as? KNode<T> ?: throw IllegalStateException("This actor has no declared Node. " +
      "Was it properly added to the tree? Was its user object cleared?")
}

/** Extends [Button] API with type-safe widget builders. */
@Scene2dDsl
class KButton(skin: Skin, style: String) : Button(skin, style), KTable

/**
 * Extends [Table] API with type-safe widget builders. All [Button] instances added to this table will be automatically
 * included in an internal [ButtonGroup].
 * @see ButtonGroup
 * @param minCheckCount minimum amount of checked buttons.
 * @param maxCheckCount maximum amount of checked buttons.
 */
@Scene2dDsl
class KButtonTable(minCheckCount: Int, maxCheckCount: Int, skin: Skin) : Table(skin), KTable {
  val buttonGroup = ButtonGroup<Button>()

  init {
    buttonGroup.setMinCheckCount(minCheckCount)
    buttonGroup.setMaxCheckCount(maxCheckCount)
  }

  override fun <T : Actor> add(actor: T): Cell<T> {
    if (actor is Button) buttonGroup.add(actor)
    return super.add(actor)
  }
}

/** Extends [CheckBox] API with type-safe widget builders. */
@Scene2dDsl
class KCheckBox(text: String, skin: Skin, style: String) : CheckBox(text, skin, style), KTable

/** Extends [Container] API with type-safe widget builders. Note that this widget may store only a single child. */
@Scene2dDsl
class KContainer<T : Actor> : Container<T>(), KGroup {
  @Suppress("UNCHECKED_CAST")
  override fun addActor(actor: Actor?) {
    this.actor == null || throw IllegalStateException("Container may store only a single child.")
    this.actor = actor as T
  }
}

/** Extends [Dialog] API with type-safe widget builders. */
@Scene2dDsl
class KDialog(title: String, skin: Skin, style: String) : Dialog(title, skin, style), KTable

/** Extends [HorizontalGroup] API with type-safe widget builders. */
@Scene2dDsl
class KHorizontalGroup : HorizontalGroup(), KGroup

/** Extends [ImageButton] API with type-safe widget builders. */
@Scene2dDsl
class KImageButton(skin: Skin, style: String) : ImageButton(skin, style), KTable

/** Extends [ImageTextButton] API with type-safe widget builders. */
@Scene2dDsl
class KImageTextButton(text: String, skin: Skin, style: String) : ImageTextButton(text, skin, style), KTable

/** Extends LibGDX List widget with items building method. */
@Scene2dDsl
class KListWidget<T>(skin: Skin, style: String) : GdxList<T>(skin, style) {
  /**
   * Allows to add items to the list with builder-like syntax.
   */
  operator fun T.unaryMinus() {
    items.add(this)
  }

  /**
   * Utility method. If the internal array with items stored by this widget was modified, use this method to safely
   * refresh the items that this widget draws.
   */
  fun refreshItems() {
    // Internal items array has to be copied, as it is cleared by the setter method...
    setItems(GdxArray(items))
  }
}

/** Extends [Tree] [Node] API with type-safe widget builders. */
@Scene2dDsl
class KNode<T : Actor>(actor: T) : Node<KNode<*>, Any?, T>(actor), KTree {
  override fun <T : Actor> add(actor: T): KNode<T> {
    val node = KNode(actor)
    add(node)
    return node
  }

  // TODO As of Kotlin 1.3, contracts are prohibited in operators. Add contracts to `invoke` methods.

  /**
   * Allows to inline a function block on a [KNode]. Syntax sugar for nested [Tree] nodes creation.
   * @param init will be invoked on this node.
   * @return this node.
   */
  @Scene2dDsl
  inline operator fun invoke(init: KNode<T>.() -> Unit): KNode<T> {
    this.init()
    return this
  }
}

/** Extends [ScrollPane] API with type-safe widget builders. Note that this widget may store only a single child. */
@Scene2dDsl
class KScrollPane(skin: Skin, style: String) : ScrollPane(null, skin, style), KGroup {
  override fun addActor(actor: Actor?) {
    this.actor == null || throw IllegalStateException("ScrollPane may store only a single child.")
    this.actor = actor
  }
}

/** Extends [SelectBox] with items building method. */
@Scene2dDsl
class KSelectBox<T>(skin: Skin, style: String) : SelectBox<T>(skin, style) {
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
    // Internal items array has to be copied, as it is cleared by the setter method...
    items = GdxArray(items)
  }
}

/** Extends [SplitPane] API with type-safe widget builders. Note that this widget may store only two children.
 * It is advised to use the inlined extension factory methods added by [KGroup] rather than set its widgets directly
 * with [setFirstWidget] or [setSecondWidget]. */
@Scene2dDsl
class KSplitPane(
  vertical: Boolean, skin: Skin, style: String
) : SplitPane(null, null, vertical, skin, style), KGroup {
  override fun addActor(actor: Actor?) {
    when (this.children.size) {
      0 -> setFirstWidget(actor)
      1 -> setSecondWidget(actor)
      else -> throw IllegalStateException("SplitPane may store only two children.")
    }
  }
}

/** Extends [Stack] API with type-safe widget builders. */
@Scene2dDsl
class KStack : Stack(), KGroup

/** Extends [Table] API with type-safe widget builders. */
@Scene2dDsl
class KTableWidget(skin: Skin) : Table(skin), KTable

/** Extends [TextButton] API with type-safe widget builders. */
@Scene2dDsl
class KTextButton(text: String, skin: Skin, style: String) : TextButton(text, skin, style), KTable

/** Extends [Tree] API with type-safe widget builders. */
@Scene2dDsl
class KTreeWidget(skin: Skin, style: String) : Tree<Node<*, *, *>, Any?>(skin, style), KTree {
  override fun <A : Actor> add(actor: A): KNode<A> {
    val node = KNode(actor)
    add(node)
    return node
  }
}

/** Extends [VerticalGroup] API with type-safe widget builders. */
@Scene2dDsl
class KVerticalGroup : VerticalGroup(), KGroup

/** Extends [Window] API with type-safe widget builders. */
@Scene2dDsl
class KWindow(title: String, skin: Skin, style: String) : Window(title, skin, style), KTable
