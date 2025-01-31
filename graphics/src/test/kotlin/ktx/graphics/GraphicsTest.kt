package ktx.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import java.io.File

/**
 * Tests general utilities related to libGDX graphics API.
 */
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
  fun `should bind ShaderProgram`() {
    val shaderProgram = mock<ShaderProgram>()

    shaderProgram.use {
      verify(shaderProgram).bind()
      assertSame(shaderProgram, it)
    }
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
    Gdx.graphics =
      mock {
        on { backBufferHeight } doReturn 4
        on { backBufferWidth } doReturn 4
      }
    val fileHandle = spy(FileHandle(File.createTempFile("screenshot", ".png")))

    takeScreenshot(fileHandle)

    verify(fileHandle).write(false)
  }
}
