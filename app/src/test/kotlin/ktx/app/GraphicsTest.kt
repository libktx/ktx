package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

/**
 * Tests general utilities related to LibGDX graphics API.
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
  fun `should clear with optional alpha`() {
    Gdx.gl = mock()

    clearScreen(0.25f, 0.5f, 0.75f)

    verify(Gdx.gl).glClearColor(0.25f, 0.5f, 0.75f, 1f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
  }
}
