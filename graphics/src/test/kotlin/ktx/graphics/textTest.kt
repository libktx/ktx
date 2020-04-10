package ktx.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Array as GdxArray
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Loads the .fnt settings file of the default LibGDX Arial font, but
 * omits loading the textures. For testing purposes.
 */
class FakeFont : BitmapFont(
  BitmapFontData(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), true),
  GdxArray.with(mock()),
  true)
{
  override fun load(data: BitmapFontData?) {
    // Do nothing.
  }
}

class TextUtilitiesTest {
  @Before
  fun `setup files`() {
    Gdx.files = LwjglFiles()
  }

  @Test
  fun `should center text on a rectangle`() {
    val font = FakeFont()
    val width = 100f
    val height = 200f

    val position = font.center("text", width, height)

    assertEquals(38.5f, position.x, 0.1f)
    assertEquals(105.5f, position.y, 0.1f)
  }
  @Test
  fun `should center text on a rectangle at given position`() {
    val font = FakeFont()

    val position = font.center("text", x = 100f, y = 200f, width = 100f, height = 200f)

    assertEquals(138.5f, position.x, 0.1f)
    assertEquals(305.5f, position.y, 0.1f)
  }

  @After
  fun `dispose of files`() {
    Gdx.files = null
  }
}
