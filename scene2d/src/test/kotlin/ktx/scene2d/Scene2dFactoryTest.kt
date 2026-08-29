package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class Scene2dFactoryTest : ApplicationTest() {
  private val sidePane = scene2dFactory {
    table {
      label("Inventory")
    }
  }

  @Test
  fun `factory value can be invoked in root scope and creates fresh actors`() {
    val first = scene2d { sidePane() }
    val second = scene2d { sidePane() }

    assertNotSame(first, second)
    assertEquals("Inventory", (first.children.first() as Label).text.toString())
    assertEquals("Inventory", (second.children.first() as Label).text.toString())
  }

  @Test
  fun `factory trailing init configures actor and placement configures table cell`() {
    val root = scene2d.table {
      sidePane(
        placement = {
          growY()
        },
      ) {
        label("Extra content")
      }
    }

    val pane = root.children.first() as KTableWidget
    assertEquals(2, pane.children.size)
    assertEquals("Extra content", (pane.children[1] as Label).text.toString())
    assertEquals(1, root.getCell(pane).expandY)
  }

  @Test
  fun `same factory mounts in table and group scopes`() {
    val table = scene2d.table { sidePane() }
    val group = scene2d.stack { sidePane() }

    assertSame(table, (table.children.first() as KTableWidget).parent)
    assertSame(group, group.children.first().parent)
  }

  private class HealthBar(val value: Float) : Actor()

  private val healthBarFactory = scene2dFactory { actor(HealthBar(1f)) }

  private inline fun <S> KWidget<S>.healthBar(
    value: Float,
    placement: S.() -> Unit = {},
    init: (@Scene2dDsl HealthBar).() -> Unit = {},
  ): HealthBar = mount(HealthBar(value), placement, init)

  @Test
  fun `mount accepts isolated custom actor init receiver`() {
    val init: (@Scene2dDsl HealthBar).() -> Unit = {
      name = "isolated mount"
      // KWidget functions such as label() require an explicit outer receiver in this block.
    }

    val root = scene2d.table {
      mount(HealthBar(1f), init = init)
    }

    assertEquals("isolated mount", root.children.first().name)
  }

  @Test
  fun `factory accepts isolated custom actor init receiver`() {
    val init: (@Scene2dDsl HealthBar).() -> Unit = {
      name = "isolated factory"
      // KWidget functions such as label() require an explicit outer receiver in this block.
    }

    val root = scene2d.table {
      healthBarFactory(init = init)
    }

    assertEquals("isolated factory", root.children.first().name)
  }

  @Test
  fun `custom actor factory uses mount`() {
    val root = scene2d.stack {
      healthBar(0.75f) {
        name = "health"
      }
    }

    assertEquals("health", root.children.first().name)
    assertEquals(0.75f, (root.children.first() as HealthBar).value, TOLERANCE)
  }
}
