package ktx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Scaling.stretch
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.nio.IntBuffer

class StagesTest {
  @Before
  fun `mock libGDX statics`() {
    LwjglNativesLoader.load()
    Gdx.app = mock()
    Gdx.gl =
      mock {
        // Mocking shader compilation, so SpriteBatch can be initiated:
        on(it.glCreateShader(any())) doReturn 1
        on(it.glCreateProgram()) doReturn 1
        on(it.glGetProgramiv(any(), any(), any())) doAnswer { invocation ->
          if (invocation.arguments[1] == GL20.GL_LINK_STATUS) {
            invocation.getArgument<IntBuffer>(2).put(0, 1)
          }
        }
        on(it.glGetShaderiv(any(), any(), any())) doAnswer { invocation ->
          if (invocation.arguments[1] == GL20.GL_COMPILE_STATUS) {
            invocation.getArgument<IntBuffer>(2).put(0, 1)
            Unit
          }
        }
        on(it.glGenBuffer()) doReturn 1
      }
    Gdx.gl20 = Gdx.gl
    Gdx.graphics =
      mock {
        on(it.width) doReturn 800
        on(it.height) doReturn 600
      }
  }

  @Test
  fun `should initiate Stage`() {
    // When:
    val stage = stage()

    // Then:
    assert(stage.batch is SpriteBatch)
    assertIsDefaultViewport(stage.viewport)
  }

  @Test
  fun `should initiate Stage with custom Viewport`() {
    // Given:
    val viewport = mock<Viewport>()

    // When:
    val stage = stage(viewport = viewport)

    // Then:
    assert(stage.batch is SpriteBatch)
    assertSame(viewport, stage.viewport)
  }

  @Test
  fun `should initiate Stage with custom Batch`() {
    // Given:
    val batch = mock<Batch>()

    // When:
    val stage = stage(batch)

    // Then:
    assertSame(batch, stage.batch)
    assertIsDefaultViewport(stage.viewport)
  }

  private fun assertIsDefaultViewport(viewport: Viewport) {
    assert(viewport is ScalingViewport)
    assertSame(stretch, (viewport as ScalingViewport).scaling)
    assertEquals(Gdx.graphics.width.toFloat(), viewport.worldWidth)
    assertEquals(Gdx.graphics.height.toFloat(), viewport.worldHeight)
    assert(viewport.camera is OrthographicCamera)
  }

  @Test
  fun `should initiate Stage with custom Batch and Viewport`() {
    // Given:
    val batch = mock<Batch>()
    val viewport = mock<Viewport>()

    // When:
    val stage = stage(batch, viewport)

    // Then:
    assertSame(batch, stage.batch)
    assertSame(viewport, stage.viewport)
  }

  @After
  fun `remove libGDX statics`() {
    Gdx.gl = null
    Gdx.app = null
    Gdx.gl20 = null
    Gdx.graphics = null
  }
}
