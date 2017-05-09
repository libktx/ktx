package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.mock.mock
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests general utilities related to LibGDX graphics API.
 */
class GraphicsTest {
  @Test
  fun `should construct Color`() {
    val color = color(red = 0.25f, green = 0.5f, blue = 0.75f, alpha = 0.6f)

    assertEquals(0.25f, color.r)
    assertEquals(0.5f, color.g)
    assertEquals(0.75f, color.b)
    assertEquals(0.6f, color.a)
  }

  @Test
  fun `should construct Color with optional alpha`() {
    val color = color(red = 0.25f, green = 0.5f, blue = 0.75f)

    assertEquals(0.25f, color.r)
    assertEquals(0.5f, color.g)
    assertEquals(0.75f, color.b)
    assertEquals(1f, color.a)
  }

  @Test
  fun `should copy Color values`() {
    val color = Color(0.4f, 0.5f, 0.6f, 0.7f)

    val copy = color.copy()

    assertNotSame(color, copy)
    assertEquals(0.4f, copy.r)
    assertEquals(0.5f, copy.g)
    assertEquals(0.6f, copy.b)
    assertEquals(0.7f, copy.a)
    assertEquals(color, copy)
  }

  @Test
  fun `should override chosen Color values when copying`() {
    val color = Color(0.4f, 0.5f, 0.6f, 0.7f)

    val copy = color.copy(red = 0.25f, green = 0.35f, blue = 0.45f, alpha = 0.55f)

    assertNotSame(color, copy)
    assertEquals(0.25f, copy.r)
    assertEquals(0.35f, copy.g)
    assertEquals(0.45f, copy.b)
    assertEquals(0.55f, copy.a)
    assertNotEquals(color, copy)
  }

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
