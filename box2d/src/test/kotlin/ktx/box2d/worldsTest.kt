package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [World] utilities and extension methods.
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
    world.dispose()
  }

  @Test
  fun `should create World with custom gravity`() {
    val world = createWorld(gravity = Vector2(1f, 1f), allowSleep = false)

    assertEquals(Vector2(1f, 1f), world.gravity)
    world.dispose()
  }

  @Test
  fun `should construct a Body`() {
    val world = createWorld()

    val body = world.body { }

    assertNotNull(body)
    assertSame(world, body.world)
    assertEquals(StaticBody, body.type)
    world.dispose()
  }

  @Test
  fun `should construct a Body of chosen BodyType`() {
    val world = createWorld()

    val body = world.body(type = KinematicBody) { }

    assertNotNull(body)
    assertSame(world, body.world)
    assertEquals(KinematicBody, body.type)
    world.dispose()
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
    world.dispose()
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
    world.dispose()
  }

  @Test
  fun `should set custom user data of bodies`() {
    val world = createWorld()
    val data = Any()

    val body = world.body {
      userData = data
    }

    assertSame(data, body.userData)
    world.dispose()
  }

  @Test
  fun `should set custom user data of fixtures`() {
    val world = createWorld()
    val circleData = Any()
    val boxData = Any()

    val body = world.body {
      circle {
        userData = circleData
      }
      box {
        userData = boxData
      }
    }

    val circle = body.fixtureList[0]
    assertSame(circleData, circle.userData)
    val box = body.fixtureList[1]
    assertSame(boxData, box.userData)
    world.dispose()
  }

  @Test
  fun `should invoke body creation callback`() {
    val world = createWorld()
    var callbackParameter: Body? = null

    val body = world.body {
      onCreate {
        callbackParameter = it
      }
    }

    assertSame(body, callbackParameter)
    world.dispose()
  }

  @Test
  fun `should invoke fixture creation callback`() {
    val world = createWorld()
    var callbackParameter: Fixture? = null

    val body = world.body {
      circle {
        onCreate {
          callbackParameter = it
        }
      }
    }

    assertSame(body.fixtureList[0], callbackParameter)
    world.dispose()
  }
}
