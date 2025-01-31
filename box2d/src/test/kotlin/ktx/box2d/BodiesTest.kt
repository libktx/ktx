package ktx.box2d

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.World
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

// TODO Kotlin Contracts in 1.3 do not support vararg methods such as polygon, chain or loop.
// Once that compiler bug is fixed, their tests with init blocks should be extended to test the contracts.
// https://youtrack.jetbrains.com/issue/KT-30497

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

    val fixtureDef = bodyDefinition.fixture(shape)

    assertSame(shape, fixtureDef.shape)
    assertFalse(fixtureDef.disposeOfShape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    verify(shape, never()).dispose()
  }

  @Test
  fun `should construct FixtureDef with a custom shape with init block`() {
    val bodyDefinition = BodyDefinition()
    val shape = mock<Shape>()
    val variable: Int

    val fixtureDef =
      bodyDefinition.fixture(shape) {
        density = 0.5f
        assertSame(shape, it)
        variable = 42
      }

    assertSame(shape, fixtureDef.shape)
    assertFalse(fixtureDef.disposeOfShape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with a custom shape and mark it for disposing`() {
    val bodyDefinition = BodyDefinition()
    val shape = mock<Shape>()

    val fixtureDef = bodyDefinition.fixture(shape, disposeOfShape = true)

    assertSame(shape, fixtureDef.shape)
    assertTrue(fixtureDef.disposeOfShape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
  }

  @Test
  fun `should construct FixtureDef with a CircleShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.circle(radius = 1f, position = Vector2(2f, 3f))

    assertTrue(fixtureDef.shape is CircleShape)
    val shape = fixtureDef.shape as CircleShape
    assertEquals(1f, shape.radius)
    assertEquals(2f, shape.position.x)
    assertEquals(3f, shape.position.y)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a CircleShape with init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.circle(radius = 1f, position = Vector2(2f, 3f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is CircleShape)
    val shape = fixtureDef.shape as CircleShape
    assertEquals(1f, shape.radius)
    assertEquals(2f, shape.position.x)
    assertEquals(3f, shape.position.y)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape set to a box`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef =
      bodyDefinition.box(
        width = 2f,
        height = 2f,
        position = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians,
      )

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    assertPolygonEquals(
      arrayOf(
        Vector2(2f, 0f),
        Vector2(2f, 2f),
        Vector2(0f, 2f),
        Vector2(0f, 0f),
      ),
      shape,
    )
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape set to a box with init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.box(
        width = 2f,
        height = 2f,
        position = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians,
      ) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    assertPolygonEquals(
      arrayOf(
        Vector2(2f, 0f),
        Vector2(2f, 2f),
        Vector2(0f, 2f),
        Vector2(0f, 0f),
      ),
      shape,
    )
    assertEquals(0.5f, fixtureDef.density)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.polygon(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f))

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape with init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.polygon(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape with Vector2 points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.polygon(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape with Vector2 points and init block`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef =
      bodyDefinition.polygon(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)) {
        density = 0.5f
      }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a ChainShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.chain(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f))

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a ChainShape with init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.chain(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with a ChainShape with Vector2 points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.chain(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a ChainShape with Vector2 points and init block`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef =
      bodyDefinition.chain(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)) {
        density = 0.5f
      }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a looped ChainShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.loop(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f))

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertTrue(shape.isLooped)
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a looped ChainShape with init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.loop(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertTrue(shape.isLooped)
    // Loop adds another vertex closing the chain:
    assertChainEquals(
      arrayOf(
        Vector2(1f, 1f),
        Vector2(2f, 2f),
        Vector2(1f, 2f),
        Vector2(1f, 1f),
      ),
      shape,
    )
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with a looped ChainShape with Vector2 points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.loop(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertTrue(shape.isLooped)
    // Loop adds another vertex closing the chain:
    assertChainEquals(
      arrayOf(
        Vector2(1f, 1f),
        Vector2(2f, 2f),
        Vector2(1f, 2f),
        Vector2(1f, 1f),
      ),
      shape,
    )
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with a looped ChainShape with Vector2 points and init block`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef =
      bodyDefinition.loop(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)) {
        density = 0.5f
      }

    assertTrue(fixtureDef.shape is ChainShape)
    val shape = fixtureDef.shape as ChainShape
    assertTrue(shape.isLooped)
    // Loop adds another vertex closing the chain:
    assertChainEquals(
      arrayOf(
        Vector2(1f, 1f),
        Vector2(2f, 2f),
        Vector2(1f, 2f),
        Vector2(1f, 1f),
      ),
      shape,
    )
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with an EdgeShape`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f))

    assertTrue(fixtureDef.shape is EdgeShape)
    val shape = fixtureDef.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with an EdgeShape with init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is EdgeShape)
    val shape = fixtureDef.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct FixtureDef with an EdgeShape with float points`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.edge(fromX = 1f, fromY = 2f, toX = 3f, toY = 4f)

    assertTrue(fixtureDef.shape is EdgeShape)
    val shape = fixtureDef.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 2f), Vector2(3f, 4f), shape)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
  }

  @Test
  fun `should construct FixtureDef with an EdgeShape with float points and init block`() {
    val bodyDefinition = BodyDefinition()
    val variable: Int

    val fixtureDef =
      bodyDefinition.edge(fromX = 1f, fromY = 2f, toX = 3f, toY = 4f) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixtureDef.shape is EdgeShape)
    val shape = fixtureDef.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 2f), Vector2(3f, 4f), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
    assertTrue(fixtureDef.disposeOfShape)
    assertEquals(42, variable)
  }

  @Test
  fun `should replace creation callback`() {
    val bodyDefinition = BodyDefinition()
    val callback: (Body) -> Unit = {}

    bodyDefinition.onCreate(callback)

    assertSame(callback, bodyDefinition.creationCallback)
  }

  // Body extension tests:

  @Test
  fun `should construct Fixture with a custom shape`() {
    val body = createBody()
    val shape = spy(CircleShape())

    val fixture = body.fixture(shape)

    assertTrue(fixture.shape is CircleShape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    verify(shape, never()).dispose()
  }

  @Test
  fun `should construct Fixture with a custom shape with init block`() {
    val body = createBody()
    val shape = spy(CircleShape())
    val variable: Int

    val fixture =
      body.fixture(shape) {
        density = 0.5f
        assertSame(shape, it)
        variable = 42
      }

    assertTrue(fixture.shape is CircleShape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    verify(shape, never()).dispose()
    assertEquals(42, variable)
  }

  @Test
  fun `should construct Fixture with a custom shape and dispose of it`() {
    val body = createBody()
    val shape = spy(CircleShape())

    val fixture = body.fixture(shape, disposeOfShape = true)

    assertTrue(fixture.shape is CircleShape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    verify(shape).dispose()
  }

  @Test
  fun `should construct Fixture with a CircleShape`() {
    val body = createBody()

    val fixture = body.circle(radius = 1f, position = Vector2(2f, 3f))

    assertTrue(fixture.shape is CircleShape)
    val shape = fixture.shape as CircleShape
    assertEquals(1f, shape.radius)
    assertEquals(2f, shape.position.x)
    assertEquals(3f, shape.position.y)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a CircleShape with init block`() {
    val body = createBody()
    val variable: Int

    val fixture =
      body.circle(radius = 1f, position = Vector2(2f, 3f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixture.shape is CircleShape)
    val shape = fixture.shape as CircleShape
    assertEquals(1f, shape.radius)
    assertEquals(2f, shape.position.x)
    assertEquals(3f, shape.position.y)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct Fixture with a PolygonShape set to a box`() {
    val body = createBody()

    val fixture =
      body.box(
        width = 2f,
        height = 2f,
        position = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians,
      )

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    assertPolygonEquals(
      arrayOf(
        Vector2(2f, 0f),
        Vector2(2f, 2f),
        Vector2(0f, 2f),
        Vector2(0f, 0f),
      ),
      shape,
    )
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a PolygonShape set to a box with init block`() {
    val body = createBody()
    val variable: Int

    val fixture =
      body.box(
        width = 2f,
        height = 2f,
        position = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians,
      ) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    assertPolygonEquals(
      arrayOf(
        Vector2(2f, 0f),
        Vector2(2f, 2f),
        Vector2(0f, 2f),
        Vector2(0f, 0f),
      ),
      shape,
    )
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct Fixture with a PolygonShape`() {
    val body = createBody()

    val fixture = body.polygon(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f))

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a PolygonShape with init block`() {
    val body = createBody()
    val variable: Int

    val fixture =
      body.polygon(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct Fixture with a PolygonShape with Vector2 Points`() {
    val body = createBody()

    val fixture = body.polygon(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))

    assertTrue(fixture.shape is PolygonShape)
    val shape = fixture.shape as PolygonShape
    // Box2D seems to change vertices order:
    assertPolygonEquals(arrayOf(Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a PolygonShape with Vector2 points and init block`() {
    val body = createBody()

    val fixture =
      body.polygon(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)) {
        density = 0.5f
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
  fun `should construct Fixture with a ChainShape`() {
    val body = createBody()

    val fixture = body.chain(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f))

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a ChainShape with init block`() {
    val body = createBody()
    val variable: Int

    val fixture =
      body.chain(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct Fixture with a ChainShape with Vector2 Points`() {
    val body = createBody()

    val fixture = body.chain(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a ChainShape with Vector2 points and init block`() {
    val body = createBody()

    val fixture =
      body.chain(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)) {
        density = 0.5f
      }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    assertFalse(shape.isLooped)
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a looped ChainShape`() {
    val body = createBody()

    val fixture = body.loop(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f))

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a looped ChainShape with init block`() {
    val body = createBody()

    val fixture =
      body.loop(vertices = floatArrayOf(1f, 1f, 2f, 2f, 1f, 2f)) {
        density = 0.5f
      }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a looped ChainShape with Vector2 points`() {
    val body = createBody()

    val fixture = body.loop(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f))

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with a looped ChainShape with Vector2 points and init block`() {
    val body = createBody()

    val fixture =
      body.loop(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f)) {
        density = 0.5f
      }

    assertTrue(fixture.shape is ChainShape)
    val shape = fixture.shape as ChainShape
    // Loop adds another vertex closing the chain:
    assertChainEquals(arrayOf(Vector2(1f, 1f), Vector2(2f, 2f), Vector2(1f, 2f), Vector2(1f, 1f)), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with an EdgeShape`() {
    val body = createBody()

    val fixture = body.edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f))

    assertTrue(fixture.shape is EdgeShape)
    val shape = fixture.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with an EdgeShape with init block`() {
    val body = createBody()
    val variable: Int

    val fixture =
      body.edge(from = Vector2(1f, 1f), to = Vector2(2f, 2f)) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixture.shape is EdgeShape)
    val shape = fixture.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 1f), Vector2(2f, 2f), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    assertEquals(42, variable)
  }

  @Test
  fun `should construct Fixture with an EdgeShape with float points`() {
    val body = createBody()

    val fixture = body.edge(fromX = 1f, fromY = 2f, toX = 3f, toY = 4f)

    assertTrue(fixture.shape is EdgeShape)
    val shape = fixture.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 2f), Vector2(3f, 4f), shape)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
  }

  @Test
  fun `should construct Fixture with an EdgeShape with float points and init block`() {
    val body = createBody()
    val variable: Int

    val fixture =
      body.edge(fromX = 1f, fromY = 2f, toX = 3f, toY = 4f) {
        density = 0.5f
        variable = 42
      }

    assertTrue(fixture.shape is EdgeShape)
    val shape = fixture.shape as EdgeShape
    assertEdgeEquals(Vector2(1f, 2f), Vector2(3f, 4f), shape)
    assertEquals(0.5f, fixture.density)
    assertSame(body, fixture.body)
    assertTrue(fixture in body.fixtureList)
    assertEquals(42, variable)
  }

  @Test
  fun `should invoke fixture creation callback`() {
    val body = createBody()
    var callbackParameter: Fixture? = null

    val fixture =
      body.circle {
        onCreate {
          callbackParameter = it
        }
      }

    assertSame(fixture, callbackParameter)
  }

  private fun createBody(): Body = World(Vector2.Zero, true).createBody(BodyDef())
}
