package ktx.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI
import org.junit.Assert.*
import org.junit.Test

class KGroupTest {
  @Test
  fun shouldAddWidgetToGroupAndReturnIt() {
    val group = TestGroup()
    val actor = Actor()
    val result = group.add(actor)
    assertTrue(actor in group.children)
    assertSame(actor, result)
  }

  class TestGroup : Group(), KGroup
}

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

fun t() {
  table {
    color = Color.RED
  }
}

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

class KTreeTest : NeedsLibGDX() {
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
