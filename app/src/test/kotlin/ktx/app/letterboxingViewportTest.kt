package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.GL20
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Tests [LetterboxingViewport] class, which merges ScreenViewport and FitViewport behavior.
 */
class LetterboxingViewportTest {
  val tolerance = 0.00001f
  @Before
  fun mockGraphics() {
    LwjglNativesLoader.load()
    Gdx.graphics = Mockito.mock(Graphics::class.java)
    Mockito.`when`(Gdx.graphics.ppiX).thenReturn(100f)
    Mockito.`when`(Gdx.graphics.ppiY).thenReturn(100f)
    Mockito.`when`(Gdx.graphics.width).thenReturn(800)
    Mockito.`when`(Gdx.graphics.height).thenReturn(600)
    Gdx.gl = Mockito.mock(GL20::class.java)
    Gdx.gl20 = Gdx.gl
  }

  @Test
  fun shouldCalculateScale() {
    val tested = LetterboxingViewport(targetPpiX = 50f, targetPpiY = 100f)
    tested.updateScale()
    assertEquals(0.5f, tested.scaleX, tolerance)
    assertEquals(1f, tested.scaleY, tolerance)
  }

  @Test
  fun shouldUpdateWorldSize() {
    val tested = LetterboxingViewport(targetPpiX = 50f, targetPpiY = 50f, aspectRatio = 4f / 3f)
    tested.update(800, 600, true)
    assertEquals(400f, tested.worldWidth, tolerance)
    assertEquals(300f, tested.worldHeight, tolerance)
  }

  @Test
  fun shouldUpdateWorldSizeWithValuesProvidedFromGraphics() {
    val tested = LetterboxingViewport(targetPpiX = 200f, targetPpiY = 200f, aspectRatio = 8f / 3f)
    tested.updateWorldSize() // Graphics.width == 800, Graphics.height = 600
    assertEquals(1600f, tested.worldWidth, tolerance)
    // Height is letterboxed - 1200/2:
    assertEquals(600f, tested.worldHeight, tolerance)
  }

  @Test
  fun shouldUpdateScaleAfterTargetPpiIsModifiedManually() {
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