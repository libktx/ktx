package ktx.tiled

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import io.kotlintest.mock.mock
import io.kotlintest.mock.`when`
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class BatchTiledMapRendererTest {

  @Test
  fun `should call beginRender and endRender without setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val batch = mock<Batch>()
    `when`(renderer.batch).thenReturn(batch)

    renderer.use {
      verify(batch).begin()
      assertSame(renderer, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
    verify(renderer, never()).setView(any())
  }

  @Test
  fun `should call beginRender and endRender with camera setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val batch = mock<Batch>()
    `when`(renderer.batch).thenReturn(batch)
    val camera = OrthographicCamera()

    renderer.use(camera) {
      verify(batch).begin()
      verify(renderer).setView(camera)
      assertSame(renderer, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
  }

  @Test
  fun `should call beginRender and endRender with map boundary setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val batch = mock<Batch>()
    `when`(renderer.batch).thenReturn(batch)
    val mat4 = Matrix4()
    val mapBoundary = Rectangle(1f, 2f, 3f, 4f)

    renderer.use(mat4, mapBoundary.x, mapBoundary.y, mapBoundary.width, mapBoundary.height) {
      verify(batch).begin()
      verify(renderer).setView(mat4, mapBoundary.x, mapBoundary.y, mapBoundary.width, mapBoundary.height)
      assertSame(renderer, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
  }

  @Test
  fun `should call beginRender and endRender with map boundary rectangle setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val batch = mock<Batch>()
    `when`(renderer.batch).thenReturn(batch)
    val mat4 = Matrix4()
    val mapBoundary = Rectangle(1f, 2f, 3f, 4f)

    renderer.use(mat4, mapBoundary) {
      verify(batch).begin()
      verify(renderer).setView(mat4, mapBoundary.x, mapBoundary.y, mapBoundary.width, mapBoundary.height)
      assertSame(renderer, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
  }

}
