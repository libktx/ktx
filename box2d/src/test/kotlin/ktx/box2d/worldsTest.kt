package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.*
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
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

    val body = world.body()

    assertNotNull(body)
    assertSame(world, body.world)
    assertEquals(StaticBody, body.type)
    world.dispose()
  }

  @Test
  fun `should construct a Body of chosen BodyType`() {
    val world = createWorld()

    val body = world.body(type = KinematicBody)

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
  fun `should construct Body exactly once`() {
    val world = createWorld()
    val fixture: FixtureDefinition
    val variable: Int

    world.body {
      fixture = circle()
      variable = 42
    }

    assertNotNull(fixture)
    assertEquals(42, variable)
  }

  @Test
  fun `should dispose of Shape instances during Body construction`() {
    val world = createWorld()
    val reusable = spy(CircleShape())
    val disposable = spy(CircleShape())

    world.body {
      fixture(reusable, disposeOfShape = false)
      fixture(disposable, disposeOfShape = true)
    }

    verify(reusable, never()).dispose()
    verify(disposable).dispose()
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

  @Test
  fun `should ray-cast between two vectors`() {
    val world = createWorld()
    val expectedEdge = world.body().edge(from = Vector2.Zero, to = Vector2(0f, 2f))
    var called = false

    world.rayCast(Vector2(-1f, 1f), Vector2(1f, 1f)) { fixture, point, normal, fraction ->
      called = true
      assertSame(expectedEdge, fixture)
      assertEquals(Vector2(0f, 1f), point)
      assertEquals(Vector2(-1f, 0f), normal)
      assertEquals(0.5f, fraction)
      RayCast.TERMINATE
    }

    assertTrue(called)
    world.dispose()
  }

  @Test
  fun `should ray-cast between two coordinates`() {
    val world = createWorld()
    val expectedEdge = world.body().edge(from = Vector2.Zero, to = Vector2(0f, 2f))
    var called = false

    world.rayCast(startX = -1f, startY = 1f, endX = 1f, endY = 1f) { fixture, point, normal, fraction ->
      called = true
      assertSame(expectedEdge, fixture)
      assertEquals(Vector2(0f, 1f), point)
      assertEquals(Vector2(-1f, 0f), normal)
      assertEquals(0.5f, fraction)
      RayCast.TERMINATE
    }

    assertTrue(called)
    world.dispose()
  }

  @Test
  fun `should provide constants for ray-cast behavior`() {
    val tolerance = 0f

    assertEquals(1f, RayCast.CONTINUE, tolerance)
    assertEquals(0f, RayCast.TERMINATE, tolerance)
    assertEquals(-1f, RayCast.IGNORE, tolerance)
  }

  @Test
  fun `should query for overlapping fixtures with AABB and stop`() {
    val world = createWorld()
    val matchingEdge1 = world.body().edge(from = Vector2.Zero, to = Vector2(0f, 2f))
    val matchingEdge2 = world.body().edge(from = Vector2(1f, 0f), to = Vector2(1f, 2f))
    val matchedFixtures = mutableSetOf<Fixture>()

    world.query(lowerX = -1f, lowerY = 1f, upperX = 1f, upperY = 1f) { fixture ->
      matchedFixtures += fixture
      Query.STOP
    }

    assertEquals(1, matchedFixtures.size)
    assertTrue(matchedFixtures.contains(matchingEdge1) || matchedFixtures.contains(matchingEdge2))
    world.dispose()
  }

  @Test
  fun `should query for overlapping fixtures with AABB and continue`() {
    val world = createWorld()
    val matchingEdge1 = world.body().edge(from = Vector2.Zero, to = Vector2(0f, 2f))
    val matchingEdge2 = world.body().edge(from = Vector2(1f, 0f), to = Vector2(1f, 2f))
    val matchedFixtures = mutableSetOf<Fixture>()

    world.query(lowerX = -1f, lowerY = 1f, upperX = 1f, upperY = 1f) { fixture ->
      matchedFixtures += fixture
      Query.CONTINUE
    }

    assertEquals(setOf(matchingEdge1, matchingEdge2), matchedFixtures)
    world.dispose()
  }

  @Test
  fun `should query world and not callback if there are no fixtures overlapping the AABB`() {
    val world = createWorld()
    world.body().edge(from = Vector2.Zero, to = Vector2(0f, 2f))
    world.body().edge(from = Vector2(1f, 0f), to = Vector2(1f, 2f))
    val matchedFixtures = mutableSetOf<Fixture>()

    world.query(lowerX = -2f, lowerY = 1f, upperX = -1f, upperY = 1f) { fixture ->
      matchedFixtures += fixture
      Query.CONTINUE
    }

    assertTrue(matchedFixtures.isEmpty())
    world.dispose()
  }
}
