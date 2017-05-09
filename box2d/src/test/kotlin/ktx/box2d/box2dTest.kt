package ktx.box2d

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [BodyDefinition] - KTX extension of Box2D BodyDef with FixtureDef factory methods.
 */
class BodyDefinitionTest : Box2DTest() {
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

    val fixtureDef = bodyDefinition.circle(radius = 1f, x = 2f, y = 3f) {
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
  }

  @Test
  fun `should construct FixtureDef with a PolygonShape set to a box`() {
    val bodyDefinition = BodyDefinition()

    val fixtureDef = bodyDefinition.box(
        width = 2f,
        height = 2f,
        center = Vector2(1f, 1f),
        angle = 90f * MathUtils.degreesToRadians) {
      density = 0.5f
      assertTrue(it is PolygonShape)
    }

    assertTrue(fixtureDef.shape is PolygonShape)
    val shape = fixtureDef.shape as PolygonShape
    assertPolygonEquals(arrayOf(Vector2(2f, 0f), Vector2(2f, 2f), Vector2(0f, 2f), Vector2(0f, 0f)), shape)
    assertEquals(0.5f, fixtureDef.density)
    assertTrue(fixtureDef in bodyDefinition.fixtureDefinitions)
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
  }
}

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

/**
 * Tests utility extensions related to body fixtures.
 */
class FixturesTest : Box2DTest() {
  @Test
  fun `should set filter properties of FixtureDef`() {
    val fixtureDefinition = FixtureDef()

    fixtureDefinition.filter(category = 1, mask = 2, group = 3)

    fixtureDefinition.filter.apply {
      assertEquals(1.toShort(), categoryBits)
      assertEquals(2.toShort(), maskBits)
      assertEquals(3.toShort(), groupIndex)
    }
  }

  @Test
  fun `should copy filter properties into filter of FixtureDef`() {
    val fixtureDefinition = FixtureDef()
    val filter = Filter().apply {
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
}

/**
 * Initiates Box2D native library. Provides comparison methods for some Box2D data objects that are difficult to verify.
 */
abstract class Box2DTest {
  protected fun assertChainEquals(vertices: Array<Vector2>, shape: ChainShape) {
    val tolerance = 0.0001f
    assertEquals("${vertices.size} vertices expected, ${shape.vertexCount} found instead.",
        vertices.size, shape.vertexCount)
    val vertex = Vector2()
    vertices.forEachIndexed { index, expected ->
      shape.getVertex(index, vertex)
      val errorMessage = "Vertex at $index should equal $expected, $vertex found instead."
      assertEquals(errorMessage, expected.x, vertex.x, tolerance)
      assertEquals(errorMessage, expected.y, vertex.y, tolerance)
    }
  }

  protected fun assertPolygonEquals(vertices: Array<Vector2>, shape: PolygonShape) {
    val tolerance = 0.0001f
    assertEquals("${vertices.size} vertices expected, ${shape.vertexCount} found instead.",
        vertices.size, shape.vertexCount)
    val vertex = Vector2()
    vertices.forEachIndexed { index, expected ->
      shape.getVertex(index, vertex)
      val errorMessage = "Vertex at $index should equal $expected, $vertex found instead."
      assertEquals(errorMessage, expected.x, vertex.x, tolerance)
      assertEquals(errorMessage, expected.y, vertex.y, tolerance)
    }
  }

  protected fun assertEdgeEquals(from: Vector2, to: Vector2, edgeShape: EdgeShape) {
    val vertex = Vector2()
    edgeShape.getVertex1(vertex)
    assertEquals(from, vertex)
    edgeShape.getVertex2(vertex)
    assertEquals(to, vertex)
  }

  private companion object {
    @JvmStatic
    @BeforeClass
    fun `initiate Box2D`() {
      Box2D.init()
    }
  }
}
