package ktx.scene2d

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.GL20
import com.kotcrab.vis.ui.VisUI
import org.mockito.Mockito

/**
 * Tests that require mocked libGDX environment must inherit from this class.
 * @author Kotcrab
 */
open class NeedsLibGDX {
  private companion object {
    init {
      LwjglNativesLoader.load()

      Gdx.app = Mockito.mock(Application::class.java)
      Gdx.graphics = Mockito.mock(Graphics::class.java)
      Gdx.files = LwjglFiles()
      Gdx.gl = Mockito.mock(GL20::class.java)
      Gdx.gl20 = Gdx.gl

      // Includes a default skin filled with styles of all Scene2D widgets:
      VisUI.load()
    }
  }
}

const val TOLERANCE = 0.0001f