package ktx.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class CameraUtilitiesTest {
  @Before
  fun `mock graphics`() {
    LwjglNativesLoader.load()
    Gdx.graphics =
      mock {
        on { width } doReturn 800
        on { height } doReturn 600
      }
  }

  @Test
  fun `should center camera to screen center`() {
    val camera = OrthographicCamera()

    camera.center()
    camera.update()

    assertEquals(Vector3(400f, 300f, 0f), camera.position)
  }

  @Test
  fun `should center camera to rectangle center with offset`() {
    val camera = OrthographicCamera()

    camera.center(x = 100f, y = 50f, width = 1000f, height = 500f)
    camera.update()

    assertEquals(Vector3(600f, 300f, 0f), camera.position)
  }

  @Test
  fun `should immediately move camera to target`() {
    val target = Vector2(100f, 200f)
    val camera = OrthographicCamera()

    camera.moveTo(target)
    camera.update()

    assertEquals(Vector3(100f, 200f, 0f), camera.position)
  }

  @Test
  fun `should immediately move camera to target with offset`() {
    val target = Vector2(100f, 200f)
    val camera = OrthographicCamera()

    camera.moveTo(target, x = 50f, y = -25f)
    camera.update()

    assertEquals(Vector3(150f, 175f, 0f), camera.position)
  }

  @Test
  fun `should smoothly move the camera`() {
    val target = Vector2(200f, 400f)
    val camera = OrthographicCamera()
    camera.position.set(100f, 100f, 0f)
    camera.update()

    camera.lerpTo(target, lerp = 0.5f)
    camera.update()

    assertEquals(Vector3(150f, 250f, 0f), camera.position)
  }

  @Test
  fun `should smoothly move the camera with non-interpolated offsets`() {
    val target = Vector2(200f, 400f)
    val camera = OrthographicCamera()
    camera.position.set(100f, 100f, 0f)
    camera.update()

    camera.lerpTo(target, lerp = 0.5f, x = -25f, y = 100f)
    camera.update()

    assertEquals(Vector3(125f, 350f, 0f), camera.position)
  }

  @Test
  fun `should update camera automatically`() {
    val camera = spy(OrthographicCamera())
    var operationCalls = 0

    camera.update {
      operationCalls++
      verify(camera, never()).update()
    }

    assertEquals(1, operationCalls)
    verify(camera, times(1)).update()
  }

  @Test
  fun `should perform camera update operation exactly once`() {
    val camera = OrthographicCamera()
    val variable: Int

    camera.update {
      variable = 42
    }

    assertEquals(42, variable)
  }
}

/**
 * Tests [LetterboxingViewport] class, which merges ScreenViewport and FitViewport behavior.
 */
class LetterboxingViewportTest {
  private val tolerance = 0.00001f

  @Before
  fun `mock graphics`() {
    LwjglNativesLoader.load()
    Gdx.graphics =
      mock {
        on(it.ppiX) doReturn 100f
        on(it.ppiY) doReturn 100f
        on(it.width) doReturn 800
        on(it.height) doReturn 600
      }
    Gdx.gl20 = mock()
    Gdx.gl = Gdx.gl20
  }

  @Test
  fun `should calculate scale`() {
    val tested = LetterboxingViewport(targetPpiX = 50f, targetPpiY = 100f)

    tested.updateScale()

    assertEquals(0.5f, tested.scaleX, tolerance)
    assertEquals(1f, tested.scaleY, tolerance)
  }

  @Test
  fun `should update world size`() {
    val tested = LetterboxingViewport(targetPpiX = 50f, targetPpiY = 50f, aspectRatio = 4f / 3f)

    tested.update(800, 600, true)

    assertEquals(400f, tested.worldWidth, tolerance)
    assertEquals(300f, tested.worldHeight, tolerance)
  }

  @Test
  fun `should update world size with values provided from graphics`() {
    val tested = LetterboxingViewport(targetPpiX = 200f, targetPpiY = 200f, aspectRatio = 8f / 3f)

    tested.updateWorldSize() // Graphics.width == 800, Graphics.height = 600

    assertEquals(1600f, tested.worldWidth, tolerance)
    // Height is letterboxed - 1200/2:
    assertEquals(600f, tested.worldHeight, tolerance)
  }

  @Test
  fun `should update scale after target PPI is modified manually`() {
    val tested = LetterboxingViewport(targetPpiX = 50f, targetPpiY = 50f)
    assertEquals(0.5f, tested.scaleX, tolerance)
    assertEquals(0.5f, tested.scaleY, tolerance)

    tested.targetPpiX = 150f
    tested.targetPpiY = 150f
    tested.updateScale()

    assertEquals(1.5f, tested.scaleX, tolerance)
    assertEquals(1.5f, tested.scaleY, tolerance)
  }
}
