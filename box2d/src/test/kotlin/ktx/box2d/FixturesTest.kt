package ktx.box2d

import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Tests utility extensions related to body fixtures.
 */
class FixturesTest : Box2DTest() {
  @Test
  fun `should set filter properties of FixtureDef`() {
    val fixtureDefinition = FixtureDef()

    fixtureDefinition.filter {
      categoryBits = 1
      maskBits = 2
      groupIndex = 3
    }

    fixtureDefinition.filter.apply {
      assertEquals(1.toShort(), categoryBits)
      assertEquals(2.toShort(), maskBits)
      assertEquals(3.toShort(), groupIndex)
    }
  }

  @Test
  fun `should configure filter exactly once`() {
    val fixtureDefinition = FixtureDef()
    val variable: Int

    fixtureDefinition.filter {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `should copy filter properties into filter of FixtureDef`() {
    val fixtureDefinition = FixtureDef()
    val filter =
      Filter().apply {
        categoryBits = 1
        maskBits = 2
        groupIndex = 3
      }

    fixtureDefinition.filter(filter)

    fixtureDefinition.filter.apply {
      assertEquals(1.toShort(), categoryBits)
      assertEquals(2.toShort(), maskBits)
      assertEquals(3.toShort(), groupIndex)
    }
  }

  @Test
  fun `should replace creation callback`() {
    val fixtureDefinition = FixtureDefinition()
    val callback: (Fixture) -> Unit = {}

    fixtureDefinition.onCreate(callback)

    assertSame(callback, fixtureDefinition.creationCallback)
  }
}
