package ktx.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

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
    verify(batch, never()).projectionMatrix = any()
  }

  @Test
  fun `should set projection matrix`() {
    val batch = mock<Batch>()
    val matrix = Matrix4((0..15).map { it.toFloat() }.toFloatArray())

    batch.use(matrix) {
      verify(batch).projectionMatrix = matrix
      verify(batch).begin()
      assertSame(batch, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
  }

  @Test
  fun `should use Batch exactly once`() {
    val batch = mock<Batch>()
    val variable: Int

    batch.use {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `should set projection matrix if a camera is passed`() {
    val batch = mock<Batch>()
    val camera = OrthographicCamera()

    batch.use(camera) {
      verify(batch).projectionMatrix = camera.combined
      verify(batch).begin()
      assertSame(batch, it)
      verify(batch, never()).end()
    }
    verify(batch).end()
  }

  @Test
  fun `should use Batch with camera exactly once`() {
    val batch = mock<Batch>()
    val variable: Int

    batch.use(OrthographicCamera()) {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `should begin with provided projection matrix`() {
    val batch = mock<Batch>()
    val matrix = Matrix4(FloatArray(16) { it.toFloat() })

    batch.begin(projectionMatrix = matrix)

    verify(batch).projectionMatrix = matrix
    verify(batch).begin()
  }

  @Test
  fun `should use Batch with projection matrix exactly once`() {
    val batch = mock<Batch>()
    val variable: Int

    batch.use(Matrix4()) {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `should begin with provided camera combined matrix`() {
    val batch = mock<Batch>()
    val camera = OrthographicCamera()

    batch.begin(camera = camera)

    verify(batch).projectionMatrix = camera.combined
    verify(batch).begin()
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
  fun `should use ShaderProgram exactly once`() {
    val shaderProgram = mock<ShaderProgram>()
    val variable: Int

    shaderProgram.use {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `should begin and end FrameBuffer`() {
    val frameBuffer = mock<FrameBuffer>()

    frameBuffer.use {
      verify(frameBuffer).begin()
      assertSame(frameBuffer, it)
      verify(frameBuffer, never()).end()
    }
    verify(frameBuffer).end()
  }

  @Test
  fun `should use FrameBuffer exactly once`() {
    val frameBuffer = mock<FrameBuffer>()
    val variable: Int

    frameBuffer.use {
      variable = 42
    }

    assertEquals(42, variable)
  }

  @Test
  fun `should take screenshot`() {
    LwjglNativesLoader.load()
    Gdx.gl = mock()
    Gdx.graphics = mock {
      on { backBufferHeight } doReturn 4
      on { backBufferWidth } doReturn 4
    }
    val fileHandle = spy(FileHandle(File.createTempFile("screenshot", ".png")))

    takeScreenshot(fileHandle)

    verify(fileHandle).write(false)
  }
}
