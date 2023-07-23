package ktx.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.kotcrab.vis.ui.VisUI
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.mockito.kotlin.mock

/**
 * Utility value for numeric tests.
 */
const val TOLERANCE = 0.0001f

/**
 * Tests that require mocked libGDX environment should inherit from this class.
 */
abstract class ApplicationTest {
  companion object {
    @JvmStatic
    @BeforeClass
    fun `initiate libGDX context`() {
      LwjglNativesLoader.load()

      Gdx.files = LwjglFiles()
      Gdx.graphics = mock()
      Gdx.gl20 = mock()
      Gdx.app = mock()
      Gdx.gl = mock()

      // Includes a default skin filled with styles of all Scene2D widgets:
      VisUI.load()
    }

    @JvmStatic
    @AfterClass
    fun `destroy libGDX context`() {
      Gdx.graphics = null
      Gdx.files = null
      Gdx.gl20 = null
      Gdx.app = null
      Gdx.gl = null

      VisUI.dispose()
    }
  }

  @Before
  fun setDefaultSkin() {
    Scene2DSkin.defaultSkin = VisUI.getSkin()
  }
}
