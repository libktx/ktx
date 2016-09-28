package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [KGroup] interface: base for all simple WidgetGroup-based parental actors.
 * @author MJ
 */
class KGroupTest {
  @Test
  fun shouldAddWidgetToGroupAndReturnIt() {
    val group = TestGroup()
    val actor = Actor()
    val result = group.appendActor(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result)
  }

  @Test
  fun shouldAddWidgetToGroupAndReturnTheActorInsteadOfStorageObject() {
    val group = TestGroup()
    val actor = Actor()
    val result: Actor = group.storeActor(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result)
  }

  class TestGroup : Group(), KGroup
}

/**
 * Tests [KTable] interface: base for all Table-based parental actors.
 * @author MJ
 */
class KTableTest {
  @Test
  fun shouldAddWidgetToGroupAndReturnIt() {
    val group = TestTable()
    val actor = Actor()
    val result = group.appendActor(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result)
  }

  @Test
  fun shouldAddWidgetToGroupAndReturnItsCell() {
    val group = TestTable()
    val actor = Actor()
    val result: Cell<*> = group.storeActor(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result.actor)
  }

  class TestTable : Table(), KTable
}

/**
 * Tests [KTree] interface: base for all parental actors operating on tree nodes.
 * @author MJ
 */
class KTreeTest : NeedsLibGDX() {
  @Test
  fun shouldAddWidgetToGroupAndReturnIt() {
    val group = TestTree()
    val actor = Actor()
    val result = group.appendActor(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result)
  }

  @Test
  fun shouldAddWidgetToGroupAndReturnItsCell() {
    val group = TestTree()
    val actor = Actor()
    val result: Node = group.storeActor(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result.actor)
  }

  class TestTree : Tree(VisUI.getSkin()), KTree {
    override fun add(actor: Actor): KNode {
      val node = KNode(actor)
      add(node)
      return node
    }
  }
}

/**
 * Tests KTX custom actor: [KButtonTable].
 * @author MJ
 */
class KButtonTableTest : NeedsLibGDX() {
  @Test
  fun shouldAddButtonsToButtonGroup() {
    val buttonTable = KButtonTable(1, 2, Skin())
    val actor = Actor()
    buttonTable.add(actor)
    val button = Button()
    buttonTable.add(button)
    assertFalse(actor in buttonTable.buttonGroup.buttons)
    assertTrue(button in buttonTable.buttonGroup.buttons)
    assertTrue(actor in buttonTable.children)
    assertTrue(button in buttonTable.children)
  }
}

/**
 * Testing KTX-adapted widget: [KContainer].
 * @author MJ
 */
class KContainerTest {
  @Test
  fun shouldStoreChild() {
    val container = KContainer<Actor>()
    val actor = Actor()
    container.addActor(actor)
    assertEquals(actor, container.actor)
    assertTrue(actor in container.children)
  }

  @Test(expected = IllegalStateException::class)
  fun shouldFailToStoreMultipleChildren() {
    val container = KContainer<Actor>()
    container.addActor(Actor())
    container.addActor(Actor()) // Throws.
  }
}

/**
 * Testing "mock" actor - [KTree], based on tree's [Node] and implementing [KTree] interface for extra interface
 * building utility.
 * @author MJ
 */
class KNodeTest {
  @Test
  fun shouldCreateNestedNodes() {
    val node = KNode(Actor())
    val actor = Actor()
    val nested = node.add(actor)
    assertSame(actor, nested.actor)
    assertSame(node, nested.parent)
    assertSame(nested, node.children.first())
  }
}

/**
 * Testing KTX-adapted widget: [KScrollPane].
 * @author MJ
 */
class KScrollPaneTest : NeedsLibGDX() {
  @Test
  fun shouldStoreChild() {
    val scrollPane = KScrollPane(VisUI.getSkin(), defaultStyle)
    val actor = Actor()
    scrollPane.addActor(actor)
    assertEquals(actor, scrollPane.widget)
    assertTrue(actor in scrollPane.children)
  }

  @Test(expected = IllegalStateException::class)
  fun shouldFailToStoreMultipleChildren() {
    val scrollPane = KScrollPane(VisUI.getSkin(), defaultStyle)
    scrollPane.addActor(Actor())
    scrollPane.addActor(Actor()) // Throws.
  }
}

/**
 * Testing KTX-adapted widget: [KSplitPane].
 * @author MJ
 */
class KSplitPaneTest {
  @Test
  fun shouldStoreTwoChildren() {
    val splitPane = KSplitPane(false, VisUI.getSkin(), defaultHorizontalStyle)
    val first = Actor()
    splitPane.addActor(first)
    val second = Actor()
    splitPane.addActor(second)
    assertTrue(first in splitPane.children)
    assertTrue(second in splitPane.children)
    // No way to access first and second widget managed internally by SplitPane (except for reflection...).
  }

  @Test(expected = IllegalStateException::class)
  fun shouldFailToStoreMoreThanTwoChildren() {
    val splitPane = KSplitPane(false, VisUI.getSkin(), defaultHorizontalStyle)
    splitPane.addActor(Actor())
    splitPane.addActor(Actor())
    splitPane.addActor(Actor()) // Throws.
  }
}

/**
 * Testing KTX-adapted widget: [KTreeWidget].
 * @author MJ
 */
class KTreeWidgetTest : NeedsLibGDX() {
  @Test
  fun shouldSpawnNodes() {
    val tree = KTreeWidget(VisUI.getSkin(), defaultStyle)
    val actor = Actor()
    val node = tree.add(actor)
    assertSame(actor, node.actor)
    assertSame(tree, node.tree)
    assertSame(tree, node.actor.parent)
    assertSame(node, tree.nodes.first())
  }
}

// Note: other Scene2D widgets are not tested, as they do not implement any custom logic and simply inherit from KGroup
// or KTable, both of which are already tested. It is assumed that their addActor/add methods are properly implemented.
