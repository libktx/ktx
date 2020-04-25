package ktx.scene2d.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.mock.mock
import ktx.scene2d.*
import org.junit.Assert.*
import org.junit.Test

/* Tests of customized VisUI parent widgets. */

/**
 * Tests KTX-adapted widget: [KVisTree].
 */
class KVisTreeTest : NeedsLibGDX() {
  @Test
  fun `should add widget to group and return its node`() {
    val tree = scene2d.visTree()
    val actor = Actor()

    val result: KNode<Actor> = tree.storeActor(actor)

    assertTrue(actor in tree.children)
    assertSame(actor, result.actor)
  }

  @Test
  fun `should provide access to children nodes`() {
    val tree = scene2d.visTree()
    tree.apply {
      val label = label("Test")

      val node: KNode<Label> = label.inNode

      assertNotNull(node)
      assertSame(label, node.actor)
    }
  }

  @Test
  fun `should allow to configure children nodes`() {
    val tree = scene2d.visTree()
    val icon = mock<Drawable>()
    tree.apply {
      val node: KNode<Label> = label("Test") {}.node(
        icon = icon,
        selectable = false,
        expanded = true,
        userObject = "Test"
      ).inNode

      assertSame(icon, node.icon)
      assertFalse(node.isSelectable)
      assertTrue(node.isExpanded)
      assertEquals("Test", node.value)
    }
  }

  @Test
  fun `should spawn nodes`() {
    val tree = KVisTree(defaultStyle)
    val actor = Actor()

    val node = tree.add(actor)

    assertSame(actor, node.actor)
    assertSame(tree, node.tree)
    assertSame(tree, node.actor.parent)
    assertSame(node, tree.nodes.first())
  }
}

/**
 * Tests KTX-adapted widget: [KDragPane].
 */
class KDragPaneTest : NeedsLibGDX() {
  @Test
  fun `should add actors to its table`() {
    val label: Label
    val image: Image

    val pane = scene2d.dragPane {
      label = label("Test")
      image = visImage("white")
    }

    assertTrue(label in pane.table.children)
    assertTrue(image in pane.table.children)
  }
}

/**
 * Tests KTX-adapted widget: [KTabbedPane].
 */
class KTabbedPaneTest : NeedsLibGDX() {
  @Test
  fun `should add tabs`() {
    val tab: Tab
    val label: Label

    val pane = scene2d.tabbedPane {
      tab = tab(title = "Test", savable = true, closeableByUser = true) {
        label = label("Test")
      }
    }

    assertTrue(tab in pane.tabs)
    assertTrue(label in tab.contentTable.children)
    assertSame(pane, tab.pane)
  }

  @Test
  fun `should pass content table when added to group`() {
    var invoked: Boolean

    scene2d.tabbedPane { contentTable ->
      assertSame(this.table, contentTable)
      invoked = true
    }

    assertTrue(invoked)
  }

  @Test
  fun `should pass content table cell when added to table`() {
    var invoked: Boolean

    scene2d.table {
      tabbedPane { cell ->
        assertSame(this.table, cell.actor)
        invoked = true
      }
    }

    assertTrue(invoked)
  }

  @Test
  fun `should pass content table node when added to tree`() {
    var invoked: Boolean

    scene2d.tree {
      tabbedPane { node ->
        assertSame(this.table, node.actor)
        invoked = true
      }
    }

    assertTrue(invoked)
  }

  @Test
  fun `should allow to add content to Table`() {
    val table = Table()
    val tab: Tab
    val pane = scene2d.tabbedPane {
      addTabContentsTo(table)
      tab("Inactive")
      tab = tab("Active")
    }

    pane.switchTab(tab)

    assertTrue(tab.contentTable in table.children)
  }

  @Test
  fun `should allow to add content to Group`() {
    val group = Group()
    val tab: Tab
    val pane = scene2d.tabbedPane {
      addTabContentsTo(group)
      tab("Inactive")
      tab = tab("Active")
    }

    pane.switchTab(tab)

    assertTrue(tab.contentTable in group.children)
  }

  @Test
  fun `should allow to add content to Container`() {
    val container = Container<Actor>()
    val tab: Tab
    val pane = scene2d.tabbedPane {
      addTabContentsTo(container)
      tab("Inactive")
      tab = tab("Active")
    }

    pane.switchTab(tab)

    assertTrue(tab.contentTable in container.children)
  }
}

/**
 * Tests KTX-adapted widget: [KVisList].
 */
class KVisListTest : NeedsLibGDX() {
  @Test
  fun `should add items`() {
    val list = KVisList<String>(defaultStyle)

    list.apply {
      -"one"
      -"two"
      -"three"
    }

    assertEquals(Array.with("one", "two", "three"), list.items)
  }

  @Test
  fun `should not clear items on refresh`() {
    // Normally list.setItems(list.items) clears the items instead, as the internal "setter" implementation clear the
    // internal array and copies the one passed as the argument, even if both are the same object.
    val list = KVisList<String>(defaultStyle)

    list.items.apply {
      add("one")
      add("two")
      add("three")
    }

    assertEquals(3, list.items.size)
    list.refreshItems() // This implementation fails the test: list.setItems(list.items)
    assertEquals(3, list.items.size)
    assertEquals(Array.with("one", "two", "three"), list.items)
  }
}

/**
 * Tests KTX-adapted widget: [KVisScrollPane].
 */
class KVisScrollPaneTest : NeedsLibGDX() {
  @Test
  fun `should store child`() {
    val scrollPane = KVisScrollPane(defaultStyle)
    val actor = Actor()

    scrollPane.addActor(actor)

    assertEquals(actor, scrollPane.actor)
    assertTrue(actor in scrollPane.children)
  }

  @Test
  fun `should fail to store multiple children`() {
    val scrollPane = KVisScrollPane(defaultStyle)

    scrollPane.addActor(Actor())

    shouldThrow<IllegalStateException> {
      scrollPane.addActor(Actor())
    }
  }
}

/**
 * Tests KTX-adapted widget: [KVisSelectBox].
 */
class KSelectBoxTest : NeedsLibGDX() {
  @Test
  fun `should add items`() {
    val selectBox = KVisSelectBox<String>(defaultStyle)

    selectBox.apply {
      -"one"
      -"two"
      -"three"
    }

    assertEquals(Array.with("one", "two", "three"), selectBox.items)
  }

  @Test
  fun `should not clear items on refresh`() {
    // Normally actor.items = actor.items clears the items instead, as the internal "setter" implementation clear the
    // internal array and copies the one passed as the argument, even if both are the same object.
    val selectBox = KVisSelectBox<String>(defaultStyle)

    selectBox.items.apply {
      add("one")
      add("two")
      add("three")
    }

    assertEquals(3, selectBox.items.size)
    selectBox.refreshItems() // This implementation fails the test: selectBox.items = selectBox.items
    assertEquals(3, selectBox.items.size)
    assertEquals(Array.with("one", "two", "three"), selectBox.items)
  }
}

/**
 * Tests KTX-adapted widget: [KVisSplitPane].
 */
class KVisSplitPaneTest : NeedsLibGDX() {
  @Test
  fun `should store two children`() {
    val splitPane = KVisSplitPane(false, defaultHorizontalStyle)
    val first = Actor()
    val second = Actor()

    splitPane.addActor(first)
    splitPane.addActor(second)

    assertTrue(first in splitPane.children)
    assertTrue(second in splitPane.children)
    // No way to access first and second widget managed internally by SplitPane (except for reflection...).
  }

  @Test
  fun `should fail to store more than two children`() {
    val splitPane = KVisSplitPane(true, defaultVerticalStyle)

    splitPane.addActor(Actor())
    splitPane.addActor(Actor())

    shouldThrow<IllegalStateException> {
      splitPane.addActor(Actor())
    }
  }
}


/**
 * Tests KTX-adapted widget: [KMultiSplitPane].
 */
class KMultiSplitPaneTest : NeedsLibGDX() {
  @Test
  fun `should store multiple children`() {
    val splitPane = KMultiSplitPane(false, defaultHorizontalStyle)
    val first = Actor()
    val second = Actor()
    val third = Actor()

    splitPane.addActor(first)
    splitPane.addActor(second)
    splitPane.addActor(third)

    assertTrue(first in splitPane.children)
    assertTrue(second in splitPane.children)
    assertTrue(third in splitPane.children)
  }
}

/**
 * Tests KTX-adapted widget: [KCollapsible].
 */
class KCollapsibleTest : NeedsLibGDX() {
  @Test
  fun `should add actors to its table`() {
    val label: Label
    val image: Image

    val collapsible = scene2d.collapsible {
      label = label("Test").cell(expand = true)
      image = visImage("white")
    }

    assertTrue(label in collapsible.table.children)
    assertTrue(image in collapsible.table.children)
  }
}

/**
 * Tests KTX-adapted widget: [KHorizontalCollapsible].
 */
class KHorizontalCollapsibleTest : NeedsLibGDX() {
  @Test
  fun `should add actors to its table`() {
    val label: Label
    val image: Image

    val collapsible = scene2d.horizontalCollapsible {
      label = label("Test").cell(expand = true)
      image = visImage("white")
    }

    assertTrue(label in collapsible.table.children)
    assertTrue(image in collapsible.table.children)
  }
}

// Note: other extended VisUI widgets are not tested, as they do not implement any custom logic and simply inherit
// from KGroup or KTable, both of which are already tested. It is assumed that their addActor/add methods are properly
// implemented - we are basically relying on LibGDX and VisUI to behave correctly.
