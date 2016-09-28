package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node

/* Implementations of actors and widget interfaces required to set up type-safe GUI builders. */

/**
 * Common interface applied to so-called "parental" widgets.
 * @author MJ
 */
interface KWidget<out Storage> {
  /**
   * Internal utility method for adding actors to the group. Assumes the actor is stored in a container.
   * @param actor will be added to this group.
   * @return storage object, wrapping around the actor or the actor itself if there is no storage object.
   * @see Node
   * @see Cell
   */
  fun storeActor(actor: Actor): Storage

  /**
   * Internal utility method for adding actors to the group.
   * @param actor will be added to this group.
   * @return actor passed as the parameter.
   */
  fun <T : Actor> appendActor(actor: T): T
}

/**
 * Common interface applied to widgets that extend the original [Table] and keep their children in [Cell] instances.
 * @author MJ
 */
interface KTable : KWidget<Cell<*>> {
  /**
   * @param actor will be added to this widget.
   * @return [Cell] instance wrapping around the actor.
   * @see [Table.add]
   */
  fun <T : Actor> add(actor: T): Cell<T>

  override fun storeActor(actor: Actor) = add(actor)
  override fun <T : Actor> appendActor(actor: T): T {
    add(actor)
    return actor
  }
}

/**
 * Common interface applied to widgets that extend [WidgetGroup] or [Group] and keep their children in an internal
 * collection.
 * @author MJ
 */
interface KGroup : KWidget<Actor> {
  /**
   * @param actor will be added to this group.
   * @see [Group.addActor]
   * @see [WidgetGroup.addActor]
   */
  fun addActor(actor: Actor?)

  /**
   * Utility method that allows to add actors to the group with fluent API.
   * @param actor will be added to this group.
   * @return passed actor instance.
   * @see [Group.addActor]
   * @see [WidgetGroup.addActor]
   */
  fun <T : Actor> add(actor: T): T {
    addActor(actor)
    return actor;
  }

  override fun storeActor(actor: Actor) = add(actor)
  override fun <T : Actor> appendActor(actor: T): T {
    addActor(actor)
    return actor;
  }
}

/**
 * Common interface applied to widgets that keep their children in [Tree] [Node] instances.
 * @author MJ
 */
interface KTree : KWidget<KNode> {
  /**
   * @param actor will be placed in a [Node] inside this widget.
   * @return [Node] instance containing the actor.
   */
  fun add(actor: Actor): KNode

  override fun storeActor(actor: Actor) = add(actor)
  override fun <T : Actor> appendActor(actor: T): T {
    add(actor)
    return actor
  }
}

/** Extends [Button] API with type-safe widget builders.
 * @author MJ */
class KButton(skin: Skin, style: String) : Button(skin, style), KTable

/**
 * Extends [Table] API with type-safe widget builders. All [Button] instances added to this table will be automatically
 * included in an internal [ButtonGroup].
 * @author MJ
 * @see ButtonGroup
 * @param minCheckCount minimum amount of checked buttons.
 * @param maxCheckCount maximum amount of checked buttons.
 */
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

/**  Extends [CheckBox] API with type-safe widget builders.
 * @author MJ */
class KCheckBox(text: String, skin: Skin, style: String) : CheckBox(text, skin, style), KTable

/** Extends [Container] API with type-safe widget builders. Note that this widget may store only a single child.
 * @author MJ */
class KContainer<T : Actor>() : Container<T>(), KGroup {
  @Suppress("UNCHECKED_CAST")
  override fun addActor(actor: Actor?) {
    if (this.actor != null) throw IllegalStateException("Container may store only a single child.")
    this.actor = actor as T
  }
}

/**  Extends [Dialog] API with type-safe widget builders.
 * @author MJ */
class KDialog(title: String, skin: Skin, style: String) : Dialog(title, skin, style), KTable

/** Extends [HorizontalGroup] API with type-safe widget builders.
 * @author MJ */
class KHorizontalGroup : HorizontalGroup(), KGroup

/**  Extends [ImageButton] API with type-safe widget builders.
 * @author MJ */
class KImageButton(skin: Skin, style: String) : ImageButton(skin, style), KTable

/**  Extends [ImageTextButton] API with type-safe widget builders.
 * @author MJ */
class KImageTextButton(text: String, skin: Skin, style: String) : ImageTextButton(text, skin, style), KTable

/**  Extends [Tree] [Node] API with type-safe widget builders.
 * @author MJ */
class KNode(actor: Actor) : Node(actor), KTree {
  override fun add(actor: Actor): KNode {
    val node = KNode(actor)
    add(node)
    return node
  }
}

/** Extends [ScrollPane] API with type-safe widget builders. Note that this widget may store only a single child.
 * @author MJ */
class KScrollPane(skin: Skin, style: String) : ScrollPane(null, skin, style), KGroup {
  override fun addActor(actor: Actor?) {
    if (this.widget != null) throw IllegalStateException("ScrollPane may store only a single child.")
    this.widget = actor
  }
}

/** Extends [ScrollPane] API with type-safe widget builders. Note that this widget may store only a single child.
 * It is advised to use the inlined extension factory methods added by [KGroup] rather than set its widgets directly
 * with [setFirstWidget] or [setSecondWidget].
 * @author MJ */
class KSplitPane(vertical: Boolean, skin: Skin, style: String) : SplitPane(null, null, vertical, skin, style), KGroup {
  override fun addActor(actor: Actor?) {
    when (this.children.size) {
      0 -> setFirstWidget(actor)
      1 -> setSecondWidget(actor)
      else -> throw IllegalStateException("SplitPane may store only two children.")
    }
  }
}

/**  Extends [Stack] API with type-safe widget builders.
 * @author MJ */
class KStack : Stack(), KGroup

/**  Extends [Table] API with type-safe widget builders.
 * @author MJ */
class KTableWidget(skin: Skin) : Table(skin), KTable

/**  Extends [TextButton] API with type-safe widget builders.
 * @author MJ */
class KTextButton(text: String, skin: Skin, style: String) : TextButton(text, skin, style), KTable

/**  Extends [Tree] API with type-safe widget builders.
 * @author MJ */
class KTreeWidget(skin: Skin, style: String) : Tree(skin, style), KTree {
  override fun add(actor: Actor): KNode {
    val node = KNode(actor)
    add(node)
    return node
  }
}

/** Extends [VerticalGroup] API with type-safe widget builders.
 * @author MJ */
class KVerticalGroup : VerticalGroup(), KGroup

/**  Extends [Window] API with type-safe widget builders.
 * @author MJ */
class KWindow(title: String, skin: Skin, style: String) : Window(title, skin, style), KTable
