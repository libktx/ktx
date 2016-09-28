package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.kotcrab.vis.ui.VisUI
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class NonInlinedActorFactoriesTest : NeedsLibGDX() {

}

/**
 * Tests inlined methods
 */
class InlinedActorFactoriesTest : NeedsLibGDX() {
  @Before
  fun setDefaultSkin() {
    Scene2DSkin.defaultSkin = VisUI.getSkin()
  }

  /**
   * Tests and showcases how generic [KWidget] API - and its additional inlined methods - give access to specific actor
   * storage objects like [Cell] or [Node] through init block parameter.
   */
  @Test
  fun shouldGiveAccessToWidgetSpecificStorageObjects() {
    stack {
      // In regular groups, children blocks point to the new actor as both 'this' and 'it'.
      label("Actor") {
        // No specialized storage objects - 'it' should point to the actor itself:
        assertTrue(it is Label)
        assertEquals("Actor", (it as Label).text.toString())
      }
    }
    table {
      // In table-based groups, children blocks point to the new actor as 'this' and its cell as 'it'.
      label("Cell") {
        // Actors stored in cells:
        assertTrue(it is Cell<*>)
        assertTrue(it.actor is Label)
      }
    }
    tree {
      // In trees, children blocks point to the new actor as 'this' and to its node as 'it'.
      label("Node") {
        // Actors stored in tree nodes:
        assertTrue(it is Node)
        assertTrue(it.actor is Label)
        it {
          label("NestedNode") {
            assertTrue(it is Node)
            assertTrue(it.actor is Label)
            assertEquals("NestedNode", (it.actor as Label).text.toString())
          }
        }
      }
    }
  }
}
