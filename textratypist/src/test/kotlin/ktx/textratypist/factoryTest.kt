package ktx.textratypist

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.FWSkin
import com.github.tommyettinger.textra.TextraLabel
import com.github.tommyettinger.textra.TypingLabel
import ktx.scene2d.ApplicationTest
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.stack
import ktx.scene2d.table
import ktx.scene2d.tree
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BaseFactoryTest : ApplicationTest() {
  @Before
  fun `setup textratypist compatible skin`() {
    // TextraTypist requires a skin with textra label styles defined. This requires loading a skin with either
    // FWSkin or FreeTypist.
    Scene2DSkin.defaultSkin = FWSkin(Gdx.files.internal("test-skin.json"))
  }

  @After
  fun `cleanup textratypist skins`() {
    Scene2DSkin.defaultSkin.dispose()
  }

  private fun <T : Actor> test(
    validate: (T) -> Unit = {},
    widget: KWidget<*>.() -> T?,
  ) {
    // Using a parental widget that allows to use invoke actors' factory methods:
    val parent = scene2d.table()
    // Invoking widget-specific factory method:
    val child = parent.widget()
    // Ensuring the child is not null and owned by the parent:
    assertNotNull(child)
    assertTrue(child in parent.children)
    validate(child!!) // Additional validation specific to widget.
  }

  @Test
  fun `should be able to create TextraLabel without init block`() =
    test(
      widget = { textraLabel("TextraTest.") },
      validate = {
        assertEquals("TextraTest.", it.storedText.toString())
      },
    )

  @Test
  fun `should be able to create TypingLabel without init block`() =
    test(
      widget = { typingLabel("TypingTest.") },
      validate = {
        assertEquals("TypingTest.", it.storedText.toString())
      },
    )

  @Test
  fun `should be able to create TextraLabel with init block`() =
    test(
      widget = { textraLabel("TextraTest.") { alignment = Align.bottom } },
      validate = {
        assertEquals("TextraTest.", it.storedText.toString())
        assertEquals(Align.bottom, it.alignment)
      },
    )

  @Test
  fun `should be able to create TypingLabel with init block`() =
    test(
      widget = { typingLabel("TypingTest.") { alignment = Align.bottom } },
      validate = {
        assertEquals("TypingTest.", it.storedText.toString())
        assertEquals(Align.bottom, it.alignment)
      },
    )
}

class InlinedFactoryTest : ApplicationTest() {
  @Before
  fun `setup textratypist compatible skin`() {
    Scene2DSkin.defaultSkin = FWSkin(Gdx.files.internal("test-skin.json"))
  }

  @After
  fun `cleanup textratypist skins`() {
    Scene2DSkin.defaultSkin.dispose()
  }

  /**
   * Tests that textraLabel and typingLabel work within the generic [KWidget] API
   */
  @Test
  fun `textraLabel within a stack`() {
    scene2d.stack {
      // In regular groups, children blocks point to the new actor as both 'this' and 'it'.
      textraLabel("Actor") {
        // No specialized storage objects - 'it' should point to the actor itself:
        assertTrue(it is TextraLabel)
        assertEquals("Actor", (it as TextraLabel).storedText.toString())
      }
    }
  }

  @Test
  fun `textraLabel within a table`() {
    scene2d.table {
      // In table-based groups, children blocks point to the new actor as 'this' and its cell as 'it'.
      textraLabel("Cell") {
        // Actors stored in cells:
        assertTrue(it.actor is TextraLabel)
      }
    }
  }

  @Test
  fun `textraLabel within a tree`() {
    scene2d.tree {
      // In trees, children blocks point to the new actor as 'this' and node is the lambda parameter.
      textraLabel("Node") { node ->
        // Actors stored in tree nodes:
        assertTrue(node.actor is TextraLabel)
        node {
          textraLabel("NestedNode") {
            assertTrue(it.actor is TextraLabel)
            assertEquals("NestedNode", (it.actor as TextraLabel).storedText.toString())
          }
        }
      }
    }
  }

  @Test
  fun `typingLabel within a stack`() {
    scene2d.stack {
      // In regular groups, children blocks point to the new actor as both 'this' and 'it'.
      typingLabel("Actor") {
        // No specialized storage objects - 'it' should point to the actor itself:
        assertTrue(it is TypingLabel)
        assertEquals("Actor", (it as TypingLabel).storedText.toString())
      }
    }
  }

  @Test
  fun `typingLabel within a table`() {
    scene2d.table {
      // In table-based groups, children blocks point to the new actor as 'this' and its cell as 'it'.
      typingLabel("Cell") {
        // Actors stored in cells:
        assertTrue(it.actor is TypingLabel)
      }
    }
  }

  @Test
  fun `typingLabel within a tree`() {
    scene2d.tree {
      // In trees, children blocks point to the new actor as 'this' and node is the lambda parameter.
      typingLabel("Node") { node ->
        // Actors stored in tree nodes:
        assertTrue(node.actor is TypingLabel)
        node {
          typingLabel("NestedNode") {
            assertTrue(it.actor is TypingLabel)
            assertEquals("NestedNode", (it.actor as TypingLabel).storedText.toString())
          }
        }
      }
    }
  }
}
