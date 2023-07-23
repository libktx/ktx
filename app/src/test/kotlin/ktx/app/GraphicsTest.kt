package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Tests general utilities related to libGDX graphics API.
 */
class GraphicsTest {
  @Test
  fun `should clear screen`() {
    Gdx.gl = mock()

    clearScreen(0.25f, 0.5f, 0.75f, 0.6f)

    verify(Gdx.gl).glClearColor(0.25f, 0.5f, 0.75f, 0.6f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
  }

  @Test
  fun `should clear screen with optional alpha`() {
    Gdx.gl = mock()

    clearScreen(0.25f, 0.5f, 0.75f)

    verify(Gdx.gl).glClearColor(0.25f, 0.5f, 0.75f, 1f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
  }

  @Test
  fun `should clear screen without the depth buffer`() {
    Gdx.gl = mock()

    clearScreen(0.25f, 0.5f, 0.75f, alpha = 0.5f, clearDepth = false)

    verify(Gdx.gl).glClearColor(0.25f, 0.5f, 0.75f, 0.5f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT)
  }
}
