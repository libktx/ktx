package kts.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import ktx.actors.txt
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import com.badlogic.gdx.utils.Array as GdxArray

class WidgetsTest {
  @Before
  fun `initiate LibGDX`() {
    // Mocking BitmapFont is tedious, if not next to impossible, because of direct fields usage in related classes
    // constructors. Label will not successfully construct an instance without a BitmapFont.
    LwjglNativesLoader.load()

    Gdx.graphics = Mockito.mock(Graphics::class.java)
    Gdx.app = Mockito.mock(Application::class.java)
    Gdx.gl = Mockito.mock(GL20::class.java)
    Gdx.files = LwjglFiles()
    Gdx.gl20 = Gdx.gl
  }

  @Test
  fun `should read text property of a Label`() {
    val given = Label("text", LabelStyle(BitmapFont(), Color.BLACK))

    val expect = given.txt

    assertEquals("text", expect)
  }

  @Test
  fun `should write text property of a Label`() {
    val given = Label("text", LabelStyle(BitmapFont(), Color.BLACK))

    given.txt = "replaced"

    val expect = given.text.toString()
    assertEquals("replaced", expect)
  }

  @Test
  fun `should read text property of a TextButton`() {
    val given = TextButton("text", TextButtonStyle().apply { font = BitmapFont() })

    val expect = given.txt

    assertEquals("text", expect)
  }

  @Test
  fun `should write text property of a TextButton`() {
    val given = TextButton("text", TextButtonStyle().apply { font = BitmapFont() })

    given.txt = "replaced"

    val expect = given.text.toString()
    assertEquals("replaced", expect)
  }
}
