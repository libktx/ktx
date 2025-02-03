package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.junit.Assert.assertEquals
import org.junit.BeforeClass

/**
 * Initiates Box2D native library. Provides comparison methods for common Box2D data objects that cannot be easily
 * compared with standard `equals`.
 */
abstract class Box2DTest {
  protected val floatTolerance = 0.0001f

  protected fun assertChainEquals(
    vertices: Array<Vector2>,
    shape: ChainShape,
  ) {
    assertEquals(
      "${vertices.size} vertices expected, ${shape.vertexCount} found instead.",
      vertices.size,
      shape.vertexCount,
    )
    val vertex = Vector2()
    vertices.forEachIndexed { index, expected ->
      shape.getVertex(index, vertex)
      val errorMessage = "Vertex at $index should equal $expected, $vertex found instead."
      assertEquals(errorMessage, expected.x, vertex.x, floatTolerance)
      assertEquals(errorMessage, expected.y, vertex.y, floatTolerance)
    }
  }

  protected fun assertPolygonEquals(
    vertices: Array<Vector2>,
    shape: PolygonShape,
  ) {
    assertEquals(
      "${vertices.size} vertices expected, ${shape.vertexCount} found instead.",
      vertices.size,
      shape.vertexCount,
    )
    val vertex = Vector2()
    vertices.forEachIndexed { index, expected ->
      shape.getVertex(index, vertex)
      val errorMessage = "Vertex at $index should equal $expected, $vertex found instead."
      assertEquals(errorMessage, expected.x, vertex.x, floatTolerance)
      assertEquals(errorMessage, expected.y, vertex.y, floatTolerance)
    }
  }

  protected fun assertEdgeEquals(
    from: Vector2,
    to: Vector2,
    edgeShape: EdgeShape,
  ) {
    val vertex = Vector2()
    edgeShape.getVertex1(vertex)
    assertEquals(from, vertex)
    edgeShape.getVertex2(vertex)
    assertEquals(to, vertex)
  }

  companion object {
    @JvmStatic
    @BeforeClass
    fun `initiate Box2D`() {
      Box2D.init()
    }
  }
}
