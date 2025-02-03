package ktx.tiled

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.beginInternal
import com.badlogic.gdx.maps.tiled.renderers.endInternal
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import io.kotlintest.mock.mock
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class TiledMapRendererTest {
  @Test
  fun `should call beginRender and endRender without setView`() {
    val renderer = mock<BatchTiledMapRenderer>()

    renderer.use {
      verify(renderer).beginInternal()
      assertSame(renderer, it)
      verify(renderer, never()).endInternal()
    }
    verify(renderer).endInternal()
    verify(renderer, never()).setView(any())
  }

  @Test
  fun `should call beginRender and endRender with camera setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val camera = OrthographicCamera()

    renderer.use(camera) {
      verify(renderer).beginInternal()
      verify(renderer).setView(camera)
      assertSame(renderer, it)
      verify(renderer, never()).endInternal()
    }
    verify(renderer).endInternal()
  }

  @Test
  fun `should call beginRender and endRender with map boundary setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val mat4 = Matrix4()
    val mapBoundary = Rectangle(1f, 2f, 3f, 4f)

    renderer.use(mat4, mapBoundary) {
      verify(renderer).beginInternal()
      verify(renderer).setView(mat4, mapBoundary.x, mapBoundary.y, mapBoundary.width, mapBoundary.height)
      assertSame(renderer, it)
      verify(renderer, never()).endInternal()
    }
    verify(renderer).endInternal()
  }

  @Test
  fun `should call beginRender and endRender with map boundary rectangle setView`() {
    val renderer = mock<BatchTiledMapRenderer>()
    val mat4 = Matrix4()
    val mapBoundary = Rectangle(1f, 2f, 3f, 4f)

    renderer.use(mat4, mapBoundary) {
      verify(renderer).beginInternal()
      verify(renderer).setView(mat4, mapBoundary.x, mapBoundary.y, mapBoundary.width, mapBoundary.height)
      assertSame(renderer, it)
      verify(renderer, never()).endInternal()
    }
    verify(renderer).endInternal()
  }
}
