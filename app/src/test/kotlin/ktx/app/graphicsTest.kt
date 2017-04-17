package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import io.kotlintest.mock.mock
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class GraphicsTest {
  @Test
  fun `should begin and end Batch`() {
    val batch = mock<Batch>()

    batch.use {
      verify(batch).begin()
      assertSame(batch, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
  }

  @Test
  fun `should begin and end ShaderProgram`() {
    val shaderProgram = mock<ShaderProgram>()

    shaderProgram.use {
      verify(shaderProgram).begin()
      assertSame(shaderProgram, it)
      verify(shaderProgram, never()).end()
    }
    verify(shaderProgram).end()
  }

  @Test
  fun `should clear screen`() {
    Gdx.gl = mock<GL20>()

    clearScreen(0.25f, 0.5f, 0.75f, 0.6f)

    verify(Gdx.gl).glClearColor(0.25f, 0.5f, 0.75f, 0.6f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT)
  }

  @Test
  fun `should clear with optional alpha`() {
    Gdx.gl = mock<GL20>()

    clearScreen(0.25f, 0.5f, 0.75f)

    verify(Gdx.gl).glClearColor(0.25f, 0.5f, 0.75f, 1f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT)
  }
}
