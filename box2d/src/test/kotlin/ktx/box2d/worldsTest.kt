package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.World
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [World] utilities.
 */
class WorldsTest : Box2DTest() {
  @Test
  fun `should roughly match Earth's gravity`() {
    assertEquals(0f, earthGravity.x)
    assertEquals(-9.8f, earthGravity.y)
  }

  @Test
  fun `should create World with default gravity`() {
    val world = createWorld()

    assertEquals(Vector2(0f, 0f), world.gravity)
  }

  @Test
  fun `should create World with custom gravity`() {
    val world = createWorld(gravity = Vector2(1f, 1f), allowSleep = false)

    assertEquals(Vector2(1f, 1f), world.gravity)
  }

  @Test
  fun `should construct a Body`() {
    val world = createWorld()

    val body = world.body { }

    assertNotNull(body)
    assertSame(world, body.world)
  }

  @Test
  fun `should construct a customized Body with a Fixture`() {
    val world = createWorld()

    val body = world.body {
      type = DynamicBody
      circle(radius = 2f) {
        density = 0.5f
      }
    }

    assertSame(world, body.world)
    assertEquals(DynamicBody, body.type)
    assertEquals(1, body.fixtureList.size)
    val fixture = body.fixtureList[0]
    assertEquals(0.5f, fixture.density)
    assertTrue(fixture.shape is CircleShape)
    assertEquals(2f, fixture.shape.radius)
  }

  @Test
  fun `should construct a customized Body with multiple Fixture instances`() {
    val world = createWorld()

    val body = world.body {
      type = DynamicBody
      circle(radius = 2f) {
        density = 0.5f
      }
      edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f)) {
        density = 0.75f
      }
    }

    assertSame(world, body.world)
    assertEquals(DynamicBody, body.type)
    assertEquals(2, body.fixtureList.size)
    val circle = body.fixtureList[0]
    assertEquals(0.5f, circle.density)
    assertTrue(circle.shape is CircleShape)
    assertEquals(2f, circle.shape.radius)
    val edge = body.fixtureList[1]
    assertEquals(0.75f, edge.density)
    assertTrue(edge.shape is EdgeShape)
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), edge.shape as EdgeShape)
  }

  @Test
  fun `should dispose of Body fixture shapes by default`() {
    val shape = spy(CircleShape())
    val world = createWorld()

    world.body {
      fixture(shape) {}
    }

    verify(shape).dispose()
  }

  @Test
  fun `should not dispose of Body fixture shapes if explicitly forbidden`() {
    val shape = spy(CircleShape())
    val world = createWorld()

    world.body {
      disposeOfShapes = false // This allows to reuse Shape instances to create multiple bodies.
      fixture(shape) {}
    }

    verify(shape, never()).dispose()
  }
}
