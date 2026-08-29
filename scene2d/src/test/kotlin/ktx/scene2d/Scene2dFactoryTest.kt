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

  private inline fun <S> KWidget<S>.healthBar(
    value: Float,
    placement: S.() -> Unit = {},
    init: HealthBar.() -> Unit = {},
  ): HealthBar = mount(HealthBar(value), placement, init)

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
