package ktx.box2d

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.*
import org.junit.Test
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests Box2D bodies utilities and [BodyDefinition] - KTX extension of Box2D BodyDef with [FixtureDefinition] factory
 * methods.
 */
class BodiesTest : Box2DTest() {
  // BodyDefinition DSL tests:

  @Test
  fun `should construct FixtureDef with a custom shape`() {
    val bodyDefinition = BodyDefinition()
    val shape = mock<Shape>()

    val fixtureDef = bodyDefinition.fixture(shape) {
      density = 0.5f
      assertSame(shape, it)
    }

    assertSame(shape, fixtureDef.shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
  }

  @Test
  fun `should construct FixtureDef with a CircleShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.circle(radius = 1f, position = Vector2(2f, 3f)) {
      density = 0.5f
      assertTrue(it is CircleShape)
    }

    assertTrue(fixtureDef.shape is CircleShape)
    val shape = fixtureDef.shape as CircleShape
    assertEquals(1f, shape.radius)
    assertEquals(2f, shape.position.x)
    assertEquals(3f, shape.position.y)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape set to a box`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.box(
        width = 2f,
        height = 2f,
        position = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    assertPolygonEquals(arrayOf(Vector2(2f, 0f), Vector2(2f, 2f), Vector2(0f, 2f), Vector2(0f, 0f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.polygon(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape with Vector2 Points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.polygon(vertices = *arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a ChainShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.chain(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a ChainShape with Vector2 Points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.chain(vertices = *arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a looped ChainShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.loop(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertTrue(shape.isLooped)
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with a looped ChainShape with Vector2 Points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.loop(vertices = *arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertTrue(shape.isLooped)
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with an EdgeShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f)) {
      density = 0.5f
      assertTrue(it is EdgeShape)
    }

    assertTrue(fixtureDef.shape is EdgeShape)
    val shape = fixtureDef.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should construct FixtureDef with an EdgeShape with float points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.edge(fromX = 1f, fromY = 2f, toX = 3f, toY = 4f) {
      density = 0.5f
      assertTrue(it is EdgeShape)
    }

    assertTrue(fixtureDef.shape is EdgeShape)
    val shape = fixtureDef.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 2f), Vector2(3f, 4f), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    shape.dispose()
  }

  @Test
  fun `should replace creation callback`() {
    val bodyDefinition = BodyDefinition()
    val callback = { _: Body -> }

    bodyDefinition.onCreate(callback)

    assertSame(callback, bodyDefinition.creationCallback)
  }

  // Body extension tests:

  @Test
  fun `should construct Fixture with a custom shape`() {
    val body = createBody()
    val shape = CircleShape()

    val fixture = body.fixture(shape) {
      density = 0.5f
      assertSame(shape, it)
    }

    assertTrue(fixture.shape is CircleShape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a CircleShape`() {
    val body = createBody()

    val fixture = body.circle(radius = 1f, position = Vector2(2f, 3f)) {
      density = 0.5f
      assertTrue(it is CircleShape)
    }

    assertTrue(fixture.shape is CircleShape)
    val shape = fixture.shape as CircleShape
    assertEquals(1f, shape.radius)
    assertEquals(2f, shape.position.x)
    assertEquals(3f, shape.position.y)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a PolygonShape set to a box`() {
    val body = createBody()

    val fixture = body.box(
        width = 2f,
        height = 2f,
        position = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    assertPolygonEquals(arrayOf(Vector2(2f, 0f), Vector2(2f, 2f), Vector2(0f, 2f), Vector2(0f, 0f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a PolygonShape`() {
    val body = createBody()

    val fixture = body.polygon(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a PolygonShape with Vector2 Points`() {
    val body = createBody()

    val fixture = body.polygon(vertices = *arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a ChainShape`() {
    val body = createBody()

    val fixture = body.chain(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a ChainShape with Vector2 Points`() {
    val body = createBody()

    val fixture = body.chain(vertices = *arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a looped ChainShape`() {
    val body = createBody()

    val fixture = body.loop(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with a looped ChainShape with Vector2 Points`() {
    val body = createBody()

    val fixture = body.loop(vertices = *arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))) {
      density = 0.5f
      assertTrue(it is ChainShape)
    }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with an EdgeShape`() {
    val body = createBody()

    val fixture = body.edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f)) {
      density = 0.5f
      assertTrue(it is EdgeShape)
    }

    assertTrue(fixture.shape is EdgeShape)
    val shape = fixture.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should construct Fixture with an EdgeShape with float points`() {
    val body = createBody()

    val fixture = body.edge(fromX = 1f, fromY = 2f, toX = 3f, toY = 4f) {
      density = 0.5f
      assertTrue(it is EdgeShape)
    }

    assertTrue(fixture.shape is EdgeShape)
    val shape = fixture.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 2f), Vector2(3f, 4f), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    body.world.dispose()
  }

  @Test
  fun `should invoke fixture creation callback`() {
    val body = createBody()
    var callbackParameter: Fixture? = null

    val fixture = body.circle {
      onCreate {
        callbackParameter = it
      }
    }

    assertSame(fixture, callbackParameter)
    body.world.dispose()
  }

  private fun createBody(): Body = World(Vector2.Zero, true).createBody(BodyDef())
}
