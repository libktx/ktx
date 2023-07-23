package ktx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class WidgetsTest {
  @Before
  fun `initiate libGDX`() {
    // Mocking BitmapFont is tedious, if not next to impossible, because of direct fields usage in related classes
    // constructors. Label will not successfully construct an instance without a BitmapFont.
    LwjglNativesLoader.load()

    Gdx.graphics = mock()
    Gdx.app = mock()
    Gdx.gl20 = mock()
    Gdx.files = LwjglFiles()
    Gdx.gl = Gdx.gl20
  }

  @Test
  fun `should read text property of a Label`() {
    val label = Label("text", LabelStyle(BitmapFont(), Color.BLACK))

    assertEquals("text", label.txt)
  }

  @Test
  fun `should write text property of a Label`() {
    val label = Label("text", LabelStyle(BitmapFont(), Color.BLACK))

    label.txt = "replaced"

    val text = label.text.toString()
    assertEquals("replaced", text)
  }

  @Test
  fun `should read text property of a TextButton`() {
    val button = TextButton("text", TextButtonStyle().apply { font = BitmapFont() })

    assertEquals("text", button.txt)
  }

  @Test
  fun `should write text property of a TextButton`() {
    val button = TextButton("text", TextButtonStyle().apply { font = BitmapFont() })

    button.txt = "replaced"

    val text = button.text.toString()
    assertEquals("replaced", text)
  }
}
