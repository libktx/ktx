package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests [LetterboxingViewport] class, which merges ScreenViewport and FitViewport behavior.
 */
class LetterboxingViewportTest {
  private val tolerance = 0.00001f

  @Before
  fun `mock graphics`() {
    LwjglNativesLoader.load()
    Gdx.graphics = mock {
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
